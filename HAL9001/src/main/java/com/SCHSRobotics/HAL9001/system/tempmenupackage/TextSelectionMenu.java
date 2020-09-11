package com.SCHSRobotics.HAL9001.system.tempmenupackage;

import android.util.Log;

import com.SCHSRobotics.HAL9001.util.exceptions.DumpsterFireException;
import com.SCHSRobotics.HAL9001.util.exceptions.ExceptionChecker;
import com.SCHSRobotics.HAL9001.util.misc.Button;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Math.min;

public class TextSelectionMenu extends HALMenu {
    //Constants
    public static final String ENTERED_TEXT_ID = "text", CHAR_SET_ID = "charSet", NEXT_MENU_ID = "nextMenu";
    private static final int[] ROW_SELECTION_ZONE = new int[] {1,0,0,0,0,0,1,0,0,0,0,0,1};
    private static final String SELECTION_PREFIX = "#|";
    private static final char UNDERLINE_CHAR = '\u0332', SPACE_CHAR = '_';
    private static final int MAX_CHAR_CHUNK_SIZE = 3, MAX_CHUNKS_PER_LINE = 3, MAX_ROW_CHAR_LENGTH = (MAX_CHAR_CHUNK_SIZE*MAX_CHUNKS_PER_LINE)+(MAX_CHUNKS_PER_LINE - 1)+(MAX_CHUNKS_PER_LINE*SELECTION_PREFIX.length());


    private TextElement inputTextElement;

    public TextSelectionMenu(Payload payload) {
        super(payload);
        inputTextElement = new TextElement(""+SPACE_CHAR);
        selectionZone = new SelectionZone(new boolean[][]{{false}});
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
        TextInput.CharSet charSet = payload.idPresent(CHAR_SET_ID) ? payload.get(CHAR_SET_ID) : TextInput.CharSet.ALPHANUMERIC;
        HALMenu nextMenu = payload.idPresent(NEXT_MENU_ID) ? payload.get(NEXT_MENU_ID) : null;

        addItem(inputTextElement);

        int chunkIdxInRow = 0;
        StringBuilder rowText = new StringBuilder();
        String[] characterChunks = StringUtils.splitEqually(charSet.getString(), MAX_CHAR_CHUNK_SIZE);
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

                                if(nextOption == SPACE_CHAR && currentlySelectedCharIdx + 1 < originalText.length()) {
                                    String nonUnderlineText = originalText.replaceAll(""+UNDERLINE_CHAR, "");
                                    inputTextElement.setText(StringUtils.setChar(nonUnderlineText, currentlySelectedCharIdx, SPACE_CHAR));
                                }
                                else if(nextOption != SPACE_CHAR && currentlySelectedCharIdx + 1 < originalText.length()) {
                                    char adjacentChar = originalText.charAt(currentlySelectedCharIdx + 1);
                                    if(adjacentChar != UNDERLINE_CHAR) {
                                        String firstHalf = originalText.substring(0, currentlySelectedCharIdx + 1);
                                        String secondHalf = originalText.substring(currentlySelectedCharIdx + 1);
                                        inputTextElement.setText(firstHalf + UNDERLINE_CHAR + secondHalf);
                                    }
                                    else {
                                        inputTextElement.setText(StringUtils.setChar(originalText, currentlySelectedCharIdx, nextOption));
                                    }
                                }
                                else if(nextOption != SPACE_CHAR) {
                                    inputTextElement.setText(originalText.substring(0, originalText.length() - 1) + nextOption + UNDERLINE_CHAR);
                                }
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

        selectionZone.addRow(new int[] {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1});
        selectionZone.addRow(new int[] {1});

        addItem(new ViewButton(SELECTION_PREFIX+"<--       -->"+StringUtils.reverseString(SELECTION_PREFIX))
            .onClick(new Button<>(1, Button.BooleanInputs.a), (DataPacket packet) -> {
                String originalText = inputTextElement.getText();

                int currentlySelectedCharIdx = getCurrentSelectedCharIdx(originalText);
                char selectedChar = originalText.charAt(currentlySelectedCharIdx);
                String newSelectedInputText = "";

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
                else if (getCursorX() == MAX_ROW_CHAR_LENGTH - 1){
                    if(currentlySelectedCharIdx + 1 == nonUnderlined.length()) {
                        nonUnderlined += ' ';
                    }
                    inputTextElement.setText(selectChar(nonUnderlined, currentlySelectedCharIdx + 1));
                }
            }));
        addItem(new TextElement(SELECTION_PREFIX+"Done"));

        setCursorPos(0,1);

        /*
        charPositon = new AtomicInteger();
        if(payload.idPresent(ENTERED_TEXT_ID)) {
            String startText = payload.get(ENTERED_TEXT_ID);
            entryDisplayText = new BlinkableTextElement(startText+" ").blinkCharAt(startText.length(), cursorChar);
        }
        else {
            entryDisplayText = new BlinkableTextElement(" ").blinkCharAt(0, cursorChar);
        }

        ViewButton doneButton = new ViewButton("#|Done")
                .onClick(new Button<>(1, Button.BooleanInputs.a), (DataPacket packet) -> {
                    //todo parse text to remove bad spaces, spaces = evil
                    payload.add(ENTERED_TEXT_ID, entryDisplayText.getUnmodifiedText());
                    if(nextMenu == null) {
                        gui.back(payload);
                    }
                    else {
                        gui.inflate(nextMenu, payload);
                    }
                });

        addItem(new EntireViewButton()
                .onClick(new Button<>(1, Button.BooleanInputs.dpad_right), (DataPacket packet) -> {
                    if(charPositon.get() == entryDisplayText.getUnmodifiedText().length() - 1) {
                        entryDisplayText.append(' ');
                    }
                    entryDisplayText.removeAllBlinkingChars();
                    entryDisplayText.blinkCharAt(charPositon.incrementAndGet(), cursorChar);
                })
                .onClick(new Button<>(1, Button.BooleanInputs.dpad_left), (DataPacket packet) -> {
                    if(charPositon.get() != 0) {
                        entryDisplayText.removeAllBlinkingChars();
                        entryDisplayText.blinkCharAt(charPositon.decrementAndGet(), cursorChar);
                    }
                })
                .addBackgroundTask((DataPacket packet) -> entryDisplayText.setBlinkEnabled(true)));

        String[] cycles = splitEqually(charSet.getString(), MINI_CYCLE_MAX_SIZE);
        int cycleIdx = 0;
        for (int i = 0; i < (cycles.length/(MAX_LINES_PER_SCREEN-2))*MAX_LINES_PER_SCREEN + (cycles.length % (MAX_LINES_PER_SCREEN - 2) == 0 ? 0 : cycles.length % (MAX_LINES_PER_SCREEN - 2) + 1); i++) {
            if(i % MAX_LINES_PER_SCREEN == 0) {
                addItem(entryDisplayText);
            }
            else if((i+1) % MAX_LINES_PER_SCREEN == 0) {
                addItem(doneButton);
            }
            else {
                addItem(new ViewButton("#|"+cycles[cycleIdx])
                    .whileClicked(new Button<>(1, Button.BooleanInputs.a), (DataPacket packet) -> {
                        String currentText = packet.getListener().getText();
                        String currentCycle = ' ' + currentText.substring(currentText.indexOf('|') + 1);

                        char currentChar = entryDisplayText.getUnmodifiedText().charAt(charPositon.get());
                        int currentCharIdx = currentCycle.indexOf(currentChar);

                        if(currentCharIdx != -1) {
                            int nextCharIdx = (currentCharIdx + 1) % currentCycle.length();
                            char nextChar = currentCycle.charAt(nextCharIdx);
                            entryDisplayText.setBlinkEnabled(false);
                            entryDisplayText.setChar(charPositon.get(), nextChar);
                        }
                        else {
                            char nextChar = currentCycle.charAt(0);
                            entryDisplayText.setBlinkEnabled(false);
                            entryDisplayText.setChar(charPositon.get(), nextChar);
                        }
                    }));
                cycleIdx++;
            }
        }

        if(cycles.length % (MAX_LINES_PER_SCREEN - 2) != 0) {
            addItem(doneButton);
        }

        setCursorPos(0,1);
    }
    */
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

            String firstHalf = nonUnderlineText.substring(0, charIdx);
            String secondHalf = nonUnderlineText.substring(charIdx);
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
