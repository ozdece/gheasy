package com.ozdece.gheasy.ui.components;

import com.google.common.collect.ImmutableSet;

import javax.swing.*;
import java.awt.*;

public class JLabelsPanel extends JPanel {

    public JLabelsPanel(ImmutableSet<JGithubLabel> labels) {
        super();

        setLayout(new FlowLayout(FlowLayout.LEADING));

        labels.forEach(this::add);
    }

}
