package com.SCHSRobotics.HAL9001.system.tempmenupackage;

import android.util.Log;

import com.SCHSRobotics.HAL9001.util.misc.Button;

import java.util.concurrent.atomic.AtomicInteger;

import static com.SCHSRobotics.HAL9001.system.tempmenupackage.StringUtils.splitEqually;

public class TextSelectionMenu extends HALMenu {
    public static final String ENTERED_TEXT_ID = "text", CHAR_SET_ID = "charSet", NEXT_MENU_ID = "nextMenu";
    private static final int MINI_CYCLE_MAX_SIZE = 4;
    private AtomicInteger charPositon;
    private BlinkableTextElement entryDisplayText;

    public TextSelectionMenu(Payload payload) {
        super(payload);
    }

    public TextSelectionMenu() {
        this(new Payload());
    }

    @Override
    protected void init(Payload payload) {
        /*
            pg 1
            ____________________ (_ = spaces) 0
            #|abcd                            1
            #|efgh                            2
            #|ijkl                            3
            #|mnop                            4
            #|qrst                            5
            #|uvwx                            6
            #|Done                            7

            pg 2
            #___________________ (_ = spaces) 0
            #|yz01                            1
            #|2345                            2
            #|6789                            3
            #|!?@$                            4
            #|%^&*                            5
            #|Done                            6
         */

        TextInput.CharSet charSet = payload.idPresent(CHAR_SET_ID) ? payload.get(CHAR_SET_ID) : TextInput.CharSet.ALPHANUMERIC;
        HALMenu nextMenu = payload.idPresent(NEXT_MENU_ID) ? payload.get(NEXT_MENU_ID) : null;

        selectionZone = new SelectionZone(new boolean[][] {
                {false},
                {true},
                {true},
                {true},
                {true},
                {true},
                {true},
                {true},
                {false},
                {true},
                {true},
                {true},
                {true},
                {true},
                {true},
                {true},
                {false},
                {true},
                {true},
                {true},
                {true},
                {true},
                {true},
                {true},
        });

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
                    Log.wtf("done","ran");
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
}
