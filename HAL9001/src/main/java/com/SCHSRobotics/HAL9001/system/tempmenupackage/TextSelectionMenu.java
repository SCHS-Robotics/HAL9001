package com.SCHSRobotics.HAL9001.system.tempmenupackage;

public class TextSelectionMenu extends HALMenu {
    private TextInput input;
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
        ####################
        #|abcd #|efgh #|ijkl
        #|mnop #|qrst #|uvwx
        #|yz01 #|2345 #|6789
        #|!?@$ #|%^&* #|#
        # | Done
         */
        int[][] zoneMatrix = new int[][] {
                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                {1,0,0,0,0,0,0,1,0,0,0,0,0,0,1,0,0,0,0,0},
                {1,0,0,0,0,0,0,1,0,0,0,0,0,0,1,0,0,0,0,0},
                {1,0,0,0,0,0,0,1,0,0,0,0,0,0,1,0,0,0,0,0},
                {1,0,0,0,0,0,0,1,0,0,0,0,0,0,1,0,0,0,0,0},
                {1,0,0,0,0,0,0,1,0,0,0,0,0,0,1,0,0,0,0,0},
                {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}
        };
        selectionZone = new SelectionZone(zoneMatrix);
    }
}
