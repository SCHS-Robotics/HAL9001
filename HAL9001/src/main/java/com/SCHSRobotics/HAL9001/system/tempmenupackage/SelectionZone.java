package com.SCHSRobotics.HAL9001.system.tempmenupackage;

import static java.lang.Math.max;

public class SelectionZone {

    private boolean[][] zoneMatrix;
    private boolean isZero;
    private int width, height;

    public SelectionZone(int width, int height) {
        isZero = width <= 0 && height <= 0;
        if(!isZero) {
            zoneMatrix = new boolean[height][width];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    zoneMatrix[i][j] = true;
                }
            }
            this.width = width;
            this.height = height;
        }
        else {
            zoneMatrix = new boolean[1][1];
            this.width = 0;
            this.height = 0;
        }
    }

    public SelectionZone(boolean[][] selectionZoneMatrix) {
        if(selectionZoneMatrix.length > 0 && allRowsGreaterThanZeroLength(selectionZoneMatrix)) {
            isZero = false;
            zoneMatrix = boxify(selectionZoneMatrix);
            width = zoneMatrix[0].length;
            height = zoneMatrix.length;
        }
        else {
            isZero = true;
            width = 0;
            height = 0;
        }
    }

    public SelectionZone(int[][] selectionZoneMatrix) {
        this(intMatrixToBoolMatrix(selectionZoneMatrix));
    }

    public boolean isValidLocation(int x, int y) {
        if(x >= width || y >= height) {
            return false;
        }
        return zoneMatrix[y][x];
    }

    public void setValue(int x, int y, boolean value) {
        if(x < width && y < height) {
            zoneMatrix[y][x] = value;
        }
    }

    public void addRow(boolean[] row) {
        if(isZero) {
            zoneMatrix = boxify(new boolean[][] {row});
            isZero = false;
        }
        else {
            boolean[][] newZoneMatrixNonBox = new boolean[zoneMatrix[0].length][zoneMatrix.length + 1];
            System.arraycopy(zoneMatrix, 0, newZoneMatrixNonBox, 0, zoneMatrix.length);
            newZoneMatrixNonBox[zoneMatrix.length] = row;
            zoneMatrix = boxify(newZoneMatrixNonBox);
        }
        width = zoneMatrix[0].length;
        height = zoneMatrix.length;
    }

    public void addRow(int[] row) {
        addRow(intArrayToBoolArray(row));
    }

    public boolean isZero() {
        return isZero;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    private static boolean[] intArrayToBoolArray(int[] intArray) {
        boolean[] boolArray = new boolean[intArray.length];
        for (int i = 0; i < intArray.length; i++) {
            boolArray[i] = intArray[i] > 0; //If val is > 0 (preferably 1) it's true. if val <= 0 (preferably 0), it's false
        }
        return boolArray;
    }

    private static boolean[][] intMatrixToBoolMatrix(int[][] intMatrix) {
        boolean[][] boolMatrix = new boolean[intMatrix[0].length][intMatrix.length];
        for (int i = 0; i < intMatrix.length; i++) {
            boolMatrix[i] = intArrayToBoolArray(intMatrix[i]);
        }
        return boolMatrix;
    }

    private static boolean[][] boxify(boolean[][] nonBoxMatrix) {
        int maxRowLength = 0;
        for (boolean[] row : nonBoxMatrix) {
            maxRowLength = max(maxRowLength, row.length);
        }
        boolean[][] boxMatrix = new boolean[nonBoxMatrix.length][maxRowLength];
        for (int i = 0; i < nonBoxMatrix.length; i++) {
            System.arraycopy(nonBoxMatrix[i], 0, boxMatrix[i], 0, nonBoxMatrix[i].length);
        }
        return boxMatrix;
    }

    private static boolean allRowsGreaterThanZeroLength(boolean[][] matrix) {
        boolean greaterThanZeroLength = true;
        for(boolean[] row : matrix) {
            greaterThanZeroLength &= row.length > 0;
        }
        return greaterThanZeroLength;
    }
}
