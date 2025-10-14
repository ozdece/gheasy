package com.ozdece.gheasy.ui.components;

import com.google.common.collect.ImmutableList;
import com.ozdece.gheasy.github.pullrequest.model.CheckRun;
import com.ozdece.gheasy.github.pullrequest.model.StatusCheckRollup;
import com.ozdece.gheasy.github.pullrequest.model.StatusContext;

import javax.swing.*;
import java.awt.*;
import java.util.stream.Collectors;

public class JStatusCheckProgressBar extends JProgressBar {

    public JStatusCheckProgressBar(ImmutableList<StatusCheckRollup> statusCheckRollupList) {
        super(0, 100);

        final long statusCheckCount = statusCheckRollupList.size();
        final long completedCheckCount = statusCheckRollupList.stream()
                .filter(StatusCheckRollup::isSuccessful)
                .count();

        setOpaque(true);
        setBorderPainted(false);
        setStringPainted(true);

        final double percentage = ((double) completedCheckCount / statusCheckCount) * 100;

        setValue((int)percentage);
        setString("%d/%d tasks succeeded".formatted(completedCheckCount, statusCheckCount));

        setToolTipText(getTooltipText(statusCheckRollupList));
        setForeground(new Color(46, 204, 113)); // A nice green
    }

    private String getTooltipText(ImmutableList<StatusCheckRollup> statusCheckRollupList) {
        return statusCheckRollupList.stream()
                .map(this::getStatusCheckRollupStatus)
                .collect(Collectors.joining("\n"));
    }

    private String getStatusCheckRollupStatus(StatusCheckRollup statusCheckRollup) {
        return switch (statusCheckRollup) {
            case CheckRun checkRun -> "Check Run: %s = %s".formatted(checkRun.name(), checkRun.checkRunState());
            case StatusContext statusContext -> "Status Context: %s = %s".formatted(statusContext.context(), statusContext.state().name());
        };
    }


}
