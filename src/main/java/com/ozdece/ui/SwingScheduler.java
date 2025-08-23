package com.ozdece.ui;

import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import javax.swing.*;

public class SwingScheduler {

    public static Scheduler edt() {
        return Schedulers.fromExecutor(SwingUtilities::invokeLater);
    }

}
