package com.ozdece.gheasy.notification.logic;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.ozdece.gheasy.notification.NotificationService;
import com.ozdece.gheasy.notification.model.Notification;
import com.ozdece.gheasy.notification.model.NotificationButton;
import com.ozdece.gheasy.process.ProcessService;

import java.io.IOException;
import java.util.Optional;

public class NotifySendNotificationService implements NotificationService {

    private final ProcessService processService;

    public NotifySendNotificationService(ProcessService processService) {
        this.processService = processService;
    }

    @Override
    public void sendNotification(Notification notification) throws IOException, InterruptedException {
        final ImmutableList<String> notifySendCommand = getNotifySendCommand(notification);
        final ProcessBuilder processBuilder = new ProcessBuilder(notifySendCommand);

        final String clickedButtonId = processService.getProcessOutput(processBuilder).trim();

        final Optional<NotificationButton> maybeClickedNotificationButton = notification.buttons().stream()
                .filter(button -> button.getId().equals(clickedButtonId))
                .findFirst();

        maybeClickedNotificationButton
                .ifPresent(NotificationButton::fireButtonClick);
    }

    private ImmutableList<String> getNotifySendCommand(Notification notification) {
       final String title = notification.title();
       final Optional<String> maybeDescription = notification.description();
       final ImmutableSet<NotificationButton> buttons = notification.buttons();

       final ImmutableList.Builder<String> builder = ImmutableList
               .<String>builder()
               .add("notify-send")
               .add(title);

       maybeDescription.ifPresent(builder::add);

       final ImmutableSet<String> actionsCommand = buttons.stream()
               .map(button -> "--action=%s=%s".formatted(button.getId(), button.getText()))
               .collect(ImmutableSet.toImmutableSet());

       builder.addAll(actionsCommand);
       builder.addAll(ImmutableSet.of("-u", notification.urgency().name()));

       return builder.build();
    }
}
