package com.ozdece.gheasy.ui.components;

import com.ozdece.gheasy.ui.ColorConverter;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import static io.vavr.API.Try;

public class JGithubLabel extends JLabel {

    public JGithubLabel(String text, String backgroundColorHex) {
        super(text);
        final Color backgroundColor = Try(() -> ColorConverter.convertFromHex(backgroundColorHex))
                .toOption()
                .getOrElse(Color.GRAY);

        final Border paddingBorder = new EmptyBorder(2, 2, 2, 2);

        setBorder(paddingBorder);
        setBackground(backgroundColor);
        setOpaque(true);
    }
}
