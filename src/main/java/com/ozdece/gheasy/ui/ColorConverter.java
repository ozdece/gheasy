package com.ozdece.gheasy.ui;

import java.awt.Color;

public class ColorConverter {
    public static Color convertFromHex(String colorHex) throws IllegalArgumentException {
        if (colorHex.length() < 6 || colorHex.length() > 7) {
            throw new IllegalArgumentException("Color Hex String length must be either 6 or 7 characters");
        }
        final String colorStr = formatAndValidateColorString(colorHex);


        final String redStr = colorStr.substring(0, 2);
        final String greenStr = colorStr.substring(2, 4);
        final String blueStr = colorStr.substring(4, 6);

        final int red = Integer.parseInt(redStr, 16);
        final int green = Integer.parseInt(greenStr, 16);
        final int blue = Integer.parseInt(blueStr, 16);

        return new Color(red, green, blue);
    }

    private static String formatAndValidateColorString(String colorHex) throws IllegalArgumentException {
        final String colorStr = (colorHex.startsWith("#") ? colorHex.substring(1) : colorHex)
                .toUpperCase();

        for (int i = 0; i< colorStr.length(); i++) {
           final char ch = colorStr.charAt(i);

           final boolean notHexLetter = ch < 'A' || ch > 'F';
           final boolean notDigit = ch < '0' || ch > '9';

           if (notHexLetter && notDigit) {
              throw new IllegalArgumentException("%c is not a hexadecimal character at index %d".formatted(ch, i));
           }
        }
        return colorStr;
    }
}
