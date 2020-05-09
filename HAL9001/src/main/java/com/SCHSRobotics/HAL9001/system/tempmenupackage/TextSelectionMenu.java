package com.SCHSRobotics.HAL9001.system.tempmenupackage;

import com.SCHSRobotics.HAL9001.util.misc.Button;

import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.min;

public class TextSelectionMenu extends ListViewMenu {
    private static final int MINI_CYCLE_MAX_SIZE = 4;
    private TextInput input;
    private StringBuilder enteredText;
    private AtomicInteger charPositon;
    public TextSelectionMenu(Payload payload, TextInput input) {
        super(payload);
        this.input = input;
    }

    public TextSelectionMenu(TextInput input) {
        this(new Payload(), input);
    }

    @Override
    protected void init(Payload payload) {
        /*
            pg 1
            #___________________ (_ = spaces) 0
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
        selectionZone = new SelectionZone(new boolean[][]{
                {false}
        });
        setCursorPos(0,1);

        enteredText = new StringBuilder();
        charPositon = new AtomicInteger();

        String[] cycles = splitEqually(input.getCharSet().getString());
        for (int i = 0; i < cycles.length; i++) {
            if(i % MAX_LINES_PER_SCREEN == 0) {
                addItem(new ListViewButton("#")
                    .onClick(new Button<>(1, Button.BooleanInputs.dpad_right), () -> {
                        char[] charArrayInput = enteredText.toString().toCharArray();
                        if(charPositon.get() == charArrayInput.length - 1) {
                            String returnText = enteredText.toString() + '#';
                            enteredText.append(' ');
                            return returnText;
                        }
                        else {
                            charArrayInput[charPositon.get()] = enteredText.charAt(charPositon.get());
                        }
                        return "";
                    })
                    .onClick(new Button<>(1, Button.BooleanInputs.dpad_left), (String textInput) -> {
                        charPositon.decrementAndGet();
                        return "";
                    }));
            }
            else if((i+1) % MAX_LINES_PER_SCREEN == 0) {
                addItem(new ListViewButton("#|Done")
                    .onClick(new Button<>(1, Button.BooleanInputs.a), () -> {
                        //todo add payload
                        //gui.back();
                    }));
            }
            else {
                addItem(new ListViewButton("#|"+cycles[i])
                    .onClick(new Button<>(1, Button.BooleanInputs.a), (String textInput) -> {

                        return "";
                    }));
            }
        }
    }

    private static String[] splitEqually(String text) {
        String[] ret = new String[(text.length() + MINI_CYCLE_MAX_SIZE - 1) / MINI_CYCLE_MAX_SIZE];
        int i = 0;
        for (int start = 0; start < text.length(); start += MINI_CYCLE_MAX_SIZE) {
            ret[i] = text.substring(start, min(text.length(), start + MINI_CYCLE_MAX_SIZE));
            i++;
        }
        return ret;
    }
}
