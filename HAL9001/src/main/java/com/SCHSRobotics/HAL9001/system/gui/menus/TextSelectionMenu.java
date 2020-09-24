package com.SCHSRobotics.HAL9001.system.gui.menus;

import com.SCHSRobotics.HAL9001.system.gui.HALMenu;
import com.SCHSRobotics.HAL9001.system.gui.Payload;
import com.SCHSRobotics.HAL9001.system.gui.SelectionZone;
import com.SCHSRobotics.HAL9001.system.gui.UniqueID;
import com.SCHSRobotics.HAL9001.system.gui.event.DataPacket;
import com.SCHSRobotics.HAL9001.system.gui.viewelement.TextElement;
import com.SCHSRobotics.HAL9001.system.gui.viewelement.eventlistener.EntireViewButton;
import com.SCHSRobotics.HAL9001.system.gui.viewelement.eventlistener.ViewButton;
import com.SCHSRobotics.HAL9001.util.constant.Charset;
import com.SCHSRobotics.HAL9001.util.control.Button;
import com.SCHSRobotics.HAL9001.util.exceptions.DumpsterFireException;
import com.SCHSRobotics.HAL9001.util.exceptions.ExceptionChecker;
import com.SCHSRobotics.HAL9001.util.misc.StringUtils;

import static java.lang.Math.min;

public class TextSelectionMenu extends HALMenu {

    //Constants
    public static final UniqueID ENTERED_TEXT_ID = new UniqueID("output text"),
                                 CHAR_SET_ID = new UniqueID("charset"),
                                 NEXT_MENU_ID = new UniqueID("next menu"),
                                 BACK_BUTTON_ID = new UniqueID("back button"),
                                 FORWARD_BUTTON_ID = new UniqueID("forward button"),
                                 LEFT_BUTTON_ID = new UniqueID("left button"),
                                 RIGHT_BUTTON_ID = new UniqueID("right button");
    private static final String SELECTION_PREFIX = "#|";
    private static final char UNDERLINE_CHAR = '\u0332', SPACE_CHAR = '_';
    private static final int MAX_CHAR_CHUNK_SIZE = 3, MAX_CHUNKS_PER_LINE = 3, MAX_ROW_LENGTH_CHARS = (MAX_CHAR_CHUNK_SIZE*MAX_CHUNKS_PER_LINE)+(MAX_CHUNKS_PER_LINE - 1)+(MAX_CHUNKS_PER_LINE*SELECTION_PREFIX.length());

    private static final boolean[] ROW_SELECTION_ZONE = new boolean[MAX_ROW_LENGTH_CHARS], ARROW_SELECTION_ZONE = new boolean[MAX_ROW_LENGTH_CHARS];
    static {
        ARROW_SELECTION_ZONE[0] = true;
        ARROW_SELECTION_ZONE[MAX_ROW_LENGTH_CHARS-1] = true;

        for (int i = 0; i < MAX_ROW_LENGTH_CHARS; i+= MAX_CHAR_CHUNK_SIZE + SELECTION_PREFIX.length() + 1) {
            ROW_SELECTION_ZONE[i] = true;
        }
    }

    private TextElement inputTextElement;
    private Class<HALMenu> nextMenu;
    private String validChars;

    public TextSelectionMenu(Payload payload) {
        super(payload);

        inputTextElement = new TextElement(""+SPACE_CHAR);
        selectionZone = new SelectionZone(new boolean[][] {{false}});
    }

    public TextSelectionMenu() {
        this(new Payload());
    }

    /*
            MENU FORMAT:
            ###################################

            ____________________ (_ = spaces) 0
            #|abc #|def #|ghi                 1
            #|jkl #|mno #|pqr                 2
            #|stu #|vwx #|yz0                 3
            #|123 #|456 #|789                 4
            #|!?@ #|$%^ #|&*                  5
            #|<--       -->|#                 6
            #|Done                            7
    */

    @Override
    protected void init(Payload payload) {
        if(payload.idPresent(CHAR_SET_ID)) {
            Object obj = payload.get(CHAR_SET_ID);
            if(obj instanceof Charset) {
                validChars = ((Charset) obj).getChars();
            }
            else if(obj instanceof String) {
                validChars = (String) obj;
            }
        }
        else {
            validChars = Charset.ALPHANUMERIC.getChars();
        }
        nextMenu = payload.idPresent(NEXT_MENU_ID) ? payload.get(NEXT_MENU_ID) : null;

        if(payload.idPresent(LEFT_BUTTON_ID)) {
            addItem(new EntireViewButton()
                .onClick(payload.get(LEFT_BUTTON_ID), (DataPacket packet) -> {
                    String originalText = inputTextElement.getText();

                    int currentlySelectedCharIdx = getCurrentSelectedCharIdx(originalText);
                    char selectedChar = originalText.charAt(currentlySelectedCharIdx);

                    if(selectedChar == SPACE_CHAR) {
                        originalText = StringUtils.setChar(originalText, currentlySelectedCharIdx, ' ');
                    }

                    String nonUnderlined = originalText.replaceAll(""+UNDERLINE_CHAR, "");

                    if(currentlySelectedCharIdx > 0) {
                        if (currentlySelectedCharIdx == nonUnderlined.length() - 1 && selectedChar == SPACE_CHAR) {
                            nonUnderlined = StringUtils.removeLastChar(nonUnderlined);
                        }
                        inputTextElement.setText(selectChar(nonUnderlined, currentlySelectedCharIdx - 1));
                    }
                }));
        }
        if(payload.idPresent(RIGHT_BUTTON_ID)) {
            addItem(new EntireViewButton()
                    .onClick(payload.get(RIGHT_BUTTON_ID), (DataPacket packet) -> {
                        String originalText = inputTextElement.getText();

                        int currentlySelectedCharIdx = getCurrentSelectedCharIdx(originalText);
                        char selectedChar = originalText.charAt(currentlySelectedCharIdx);

                        if(selectedChar == SPACE_CHAR) {
                            originalText = StringUtils.setChar(originalText, currentlySelectedCharIdx, ' ');
                        }

                        String nonUnderlined = originalText.replaceAll(""+UNDERLINE_CHAR, "");

                        if(currentlySelectedCharIdx + 1 == nonUnderlined.length()) {
                            nonUnderlined += ' ';
                        }
                        inputTextElement.setText(selectChar(nonUnderlined, currentlySelectedCharIdx + 1));
                    }));
        }
        if(payload.idPresent(BACK_BUTTON_ID)) {
            addItem(new EntireViewButton()
                .onClick(payload.get(BACK_BUTTON_ID), (DataPacket packet) -> gui.back(payload)));
        }
        if(payload.idPresent(FORWARD_BUTTON_ID)) {
            addItem(new EntireViewButton()
                .onClick(payload.get(FORWARD_BUTTON_ID), (DataPacket packet) -> {
                    String parsedText = inputTextElement.getText().replaceAll("["+UNDERLINE_CHAR+SPACE_CHAR+"]", "");
                    payload.add(ENTERED_TEXT_ID, StringUtils.bilateralStrip(parsedText, ' '));
                    gui.forward(payload);
                }));
        }

        addItem(inputTextElement);

        int chunkIdxInRow = 0;
        StringBuilder rowText = new StringBuilder();
        String[] characterChunks = StringUtils.splitEqually(validChars, MAX_CHAR_CHUNK_SIZE);
        for(String chunk : characterChunks) {
            chunkIdxInRow++;

            rowText.append(SELECTION_PREFIX);
            rowText.append(chunk);
            rowText.append(' ');

            if(chunkIdxInRow % MAX_CHUNKS_PER_LINE == 0) {
                addItem(
                        new ViewButton(rowText.toString())
                            .onClick(new Button<>(1, Button.BooleanInputs.a), (DataPacket packet) -> {
                                ViewButton thisButton = packet.getListener();
                                String thisText = thisButton.getText();

                                String originalText = inputTextElement.getText();

                                int chunkStart = getCursorX() + SELECTION_PREFIX.length();
                                int chunkEnd = chunkStart+min(thisText.length()-chunkStart, MAX_CHAR_CHUNK_SIZE);
                                String characterOptions = thisText.substring(chunkStart, chunkEnd).trim() + SPACE_CHAR;

                                int currentlySelectedCharIdx = getCurrentSelectedCharIdx(originalText);
                                int currentOptionIdx = characterOptions.indexOf(originalText.charAt(currentlySelectedCharIdx));

                                //Note: if idx is -1 (it didn't find the char), this will make it the first char in the options.;
                                int nextOptionIdx = (currentOptionIdx + 1) % characterOptions.length();
                                char nextOption = characterOptions.charAt(nextOptionIdx);

                                inputTextElement.setText(selectChar(StringUtils.setChar(originalText, currentlySelectedCharIdx, nextOption), currentlySelectedCharIdx));
                            })
                );
                selectionZone.addRow(ROW_SELECTION_ZONE);
                rowText.delete(0, rowText.length());
                chunkIdxInRow = 0;
            }
        }
        if(chunkIdxInRow != 0) {
            addItem(new TextElement(rowText.toString()));
            selectionZone.addRow(ROW_SELECTION_ZONE);
        }

        selectionZone.addRow(ARROW_SELECTION_ZONE);
        selectionZone.addRow(new boolean[] {true});

        addItem(new ViewButton(SELECTION_PREFIX+"<--       -->"+StringUtils.reverseString(SELECTION_PREFIX))
            .onClick(new Button<>(1, Button.BooleanInputs.a), (DataPacket packet) -> {
                String originalText = inputTextElement.getText();

                int currentlySelectedCharIdx = getCurrentSelectedCharIdx(originalText);
                char selectedChar = originalText.charAt(currentlySelectedCharIdx);

                if(selectedChar == SPACE_CHAR) {
                    originalText = StringUtils.setChar(originalText, currentlySelectedCharIdx, ' ');
                }

                String nonUnderlined = originalText.replaceAll(""+UNDERLINE_CHAR, "");

                //Cursor X refers to user's cursor position, NOT the selected character.
                if(getCursorX() == 0 && currentlySelectedCharIdx > 0) {
                    if(currentlySelectedCharIdx == nonUnderlined.length() - 1 && selectedChar == SPACE_CHAR) {
                        nonUnderlined = StringUtils.removeLastChar(nonUnderlined);
                    }
                    inputTextElement.setText(selectChar(nonUnderlined, currentlySelectedCharIdx - 1));
                }
                else if (getCursorX() == MAX_ROW_LENGTH_CHARS - 1){
                    if(currentlySelectedCharIdx + 1 == nonUnderlined.length()) {
                        nonUnderlined += ' ';
                    }
                    inputTextElement.setText(selectChar(nonUnderlined, currentlySelectedCharIdx + 1));
                }
            }));
        addItem(new ViewButton(SELECTION_PREFIX+"Done")
            .onClick(new Button<>(1, Button.BooleanInputs.a), (DataPacket packet) -> {
                String parsedText = inputTextElement.getText().replaceAll("["+UNDERLINE_CHAR+SPACE_CHAR+"]", "");
                payload.add(ENTERED_TEXT_ID, StringUtils.bilateralStrip(parsedText, ' '));
                if(nextMenu == null) {
                    gui.back(payload);
                }
                else {
                    gui.inflate(nextMenu, payload);
                }
            }));

        setCursorPos(0,1);
    }

    private static String selectChar(String input, int charIdx) {
        char selectedChar = input.charAt(charIdx);

        if(selectedChar == SPACE_CHAR) {
            return input.replaceAll(""+UNDERLINE_CHAR, "");
        }
        else if(selectedChar == ' ') {
            return StringUtils.setChar(input, charIdx, '_').replaceAll(""+UNDERLINE_CHAR, "");
        }
        else {
            String nonUnderlineText = input.replaceAll(""+UNDERLINE_CHAR, "");

            String firstHalf = nonUnderlineText.substring(0, charIdx+1);
            String secondHalf = nonUnderlineText.substring(charIdx+1);
            return firstHalf + UNDERLINE_CHAR + secondHalf;
        }
    }

    private int getCurrentSelectedCharIdx(String inputText) {
        int selectedIdx = inputText.indexOf(UNDERLINE_CHAR)-1;
        if(selectedIdx == -2) {
            selectedIdx = inputText.indexOf('_');
        }
        ExceptionChecker.assertFalse(selectedIdx == -1, new DumpsterFireException("No indicator characters found in input string, we lost your place in the input string somehow :("));
        return selectedIdx;
    }
}
