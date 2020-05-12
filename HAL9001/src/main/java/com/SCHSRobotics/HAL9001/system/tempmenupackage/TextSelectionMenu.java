package com.SCHSRobotics.HAL9001.system.tempmenupackage;

import com.SCHSRobotics.HAL9001.util.misc.Button;

import java.util.concurrent.atomic.AtomicInteger;

import static com.SCHSRobotics.HAL9001.system.tempmenupackage.StringUtils.splitEqually;

public class TextSelectionMenu extends HALMenu {
    private static final int MINI_CYCLE_MAX_SIZE = 4;
    private TextInput.CharSet charSet;
    private StringBuilder entryBuilder;
    private AtomicInteger charPositon;
    private HALMenu nextMenu;
    private BlinkableTextElement entryDisplayText;
    public static final String ENTERED_TEXT_ID = "text";
    public TextSelectionMenu(Payload payload, TextInput.CharSet charSet, HALMenu nextMenu) {
        super(payload);
        this.charSet = charSet;
        this.nextMenu = nextMenu;
        selectionZone = new SelectionZone(new boolean[][]{
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
        setCursorPos(0,1);

        entryBuilder = new StringBuilder().append(' ');
        charPositon = new AtomicInteger();
        entryDisplayText = new BlinkableTextElement(entryBuilder.toString()).blinkCharAt(0, cursorChar);
    }

    public TextSelectionMenu(TextInput.CharSet charSet, HALMenu nextMenu) {
        this(new Payload(), charSet, nextMenu);
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

        addItem(new EntireViewButton()
                .onClick(new Button<>(1, Button.BooleanInputs.dpad_right), () -> {
                    if(charPositon.get() == entryDisplayText.getUnmodifiedText().length() - 1) {
                        entryBuilder.append(' ');
                        entryDisplayText.append(' ');
                        entryDisplayText.removeAllBlinkingChars();
                        entryDisplayText.blinkCharAt(charPositon.incrementAndGet(), cursorChar);
                    }
                    else {
                        entryDisplayText.removeAllBlinkingChars();
                        entryDisplayText.blinkCharAt(charPositon.incrementAndGet(), cursorChar);
                    }
                })
                .onClick(new Button<>(1, Button.BooleanInputs.dpad_left), () -> {
                    if(charPositon.get() != 0) {
                        entryDisplayText.removeAllBlinkingChars();
                        entryDisplayText.blinkCharAt(charPositon.decrementAndGet(), cursorChar);
                    }
                })
                .addBackgroundTask(() -> {
                    entryDisplayText.setBlinkEnabled(true);
                }));

        String[] cycles = splitEqually(charSet.getString(), MINI_CYCLE_MAX_SIZE);
        for (int i = 0; i < cycles.length; i++) {
            if(i % MAX_LINES_PER_SCREEN == 0) {
                addItem(entryDisplayText);
            }
            else if((i+1) % MAX_LINES_PER_SCREEN == 0) {
                addItem(new ViewButton("#|Done")
                    .onClick(new Button<>(1, Button.BooleanInputs.a), () -> {
                        //todo parse text to remove bad spaces, spaces = evil
                        payload.addItem(ENTERED_TEXT_ID, entryDisplayText.getUnmodifiedText());
                        gui.inflate(nextMenu, payload);
                    }));
            }
            else {
                addItem(new ViewButton("#|"+cycles[i])
                    .whileClicked(new Button<>(1, Button.BooleanInputs.a), (String text) -> {
                        String currentCycle = ' ' + text.substring(text.indexOf('|') + 1);
                        char currentChar = entryBuilder.charAt(charPositon.get());
                        int currentCharIdx = currentCycle.indexOf(currentChar);
                        if(currentCharIdx != -1) {
                            int nextCharIdx = (currentCharIdx + 1) % currentCycle.length();
                            char nextChar = currentCycle.charAt(nextCharIdx);
                            entryDisplayText.setBlinkEnabled(false);
                            entryDisplayText.setChar(charPositon.get(), nextChar);
                        }
                        return text;
                    }));
            }
        }
    }
}
