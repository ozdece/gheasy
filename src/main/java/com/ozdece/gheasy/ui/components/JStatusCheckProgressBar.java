package com.ozdece.gheasy.ui.components;

import javax.swing.*;
import java.awt.*;

public class JStatusCheckProgressBar extends JProgressBar {

    public JStatusCheckProgressBar(long statusCheckCount, long completedCheckCount) {
        super(0, 100);

        setOpaque(true);
        setBorderPainted(false);
        setStringPainted(true);

        final double percentage = ((double) completedCheckCount / statusCheckCount) * 100;

        setValue((int)percentage);
        setString("%d/%d tasks succeeded".formatted(completedCheckCount, statusCheckCount));

        setForeground(new Color(46, 204, 113)); // A nice green
    }

}
