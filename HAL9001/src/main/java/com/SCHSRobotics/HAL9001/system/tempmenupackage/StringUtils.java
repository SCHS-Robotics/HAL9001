package com.SCHSRobotics.HAL9001.system.tempmenupackage;

import com.SCHSRobotics.HAL9001.util.exceptions.DumpsterFireException;
import com.SCHSRobotics.HAL9001.util.exceptions.ExceptionChecker;

import static java.lang.Math.min;

public class StringUtils {
    private StringUtils() {}

    public static String removeFirstChar(String str) {
        return str.substring(1);
    }

    public static String removeLastChar(String str) {
        return str.substring(0, str.length() - 1);
    }

    public static String reverseString(String str) {
        StringBuilder strBuilder = new StringBuilder(str);
        return strBuilder.reverse().toString();
    }

    public static String setChar(String str, int charIdx, char c) {
        ExceptionChecker.assertTrue(charIdx < str.length(), new IndexOutOfBoundsException("Char index must point to a location within the string."));
        StringBuilder strBuilder = new StringBuilder(str);
        strBuilder.setCharAt(charIdx, c);
        return strBuilder.toString();
    }

    public static String bilateralStrip(String str, char charToStrip) {
        int startIdx = 0;
        int endIdx = 0;

        //removes leading chars
        for(int i = 0; i < str.length(); i++) {
            if(str.charAt(i) != charToStrip) {
                startIdx = i;
                break;
            }
        }

        //removes trailing chars
        for(int i = str.length()-1; i >= 0; i--) {
            if(str.charAt(i) != charToStrip) {
                endIdx = i;
                break;
            }
        }

        String strippedString = str.substring(startIdx,endIdx+1);
        strippedString = strippedString.equals(String.valueOf(charToStrip)) ? "" : strippedString;
        return strippedString;
    }

    public static String repeatCharacter(char c, int timesToRepeat) {
        if(timesToRepeat == 0) {
            return "";
        }
        ExceptionChecker.assertTrue(timesToRepeat > 0, new DumpsterFireException("Cannot repeat a char "+timesToRepeat+" times."));
        return new String(new char[timesToRepeat]).replace('\0', c);
    }

    public static String[] splitEqually(String text, int maxChunkSize) {
        ExceptionChecker.assertTrue(maxChunkSize > 0, new DumpsterFireException("Max chunk size for equally splitting a string must be greater than 0."));
        String[] ret = new String[(text.length() + maxChunkSize - 1) / maxChunkSize];
        int i = 0;
        for (int start = 0; start < text.length(); start += maxChunkSize) {
            ret[i] = text.substring(start, min(text.length(), start + maxChunkSize));
            i++;
        }
        return ret;
    }
}
