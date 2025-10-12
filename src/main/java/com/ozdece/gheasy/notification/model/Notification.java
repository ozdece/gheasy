package com.ozdece.gheasy.notification.model;

import com.google.common.collect.ImmutableSet;

import java.util.Optional;

public record Notification(
        String title,
        Optional<String> description,
        NotificationUrgency urgency,
        ImmutableSet<NotificationButton> buttons
) {}
