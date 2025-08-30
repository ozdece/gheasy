package com.ozdece.gheasy.ui;

import javax.swing.*;
import java.awt.*;

public class Fonts {

    public static final Font DEFAULT_FONT = new JLabel().getFont();
    public static final Font BOLD_FONT = DEFAULT_FONT.deriveFont(Font.BOLD);
    public static final Font ITALIC_FONT = DEFAULT_FONT.deriveFont(Font.ITALIC);

    public static Font withSize(int size) {
        final float sizeFloat = (float) size;

        return DEFAULT_FONT.deriveFont(sizeFloat);
    }

    public static Font boldFontWithSize(int size) {
        final float sizeFloat = (float) size;

        return BOLD_FONT.deriveFont(sizeFloat);
    }

}
