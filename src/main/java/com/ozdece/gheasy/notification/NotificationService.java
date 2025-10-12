package com.ozdece.gheasy.notification;

import com.ozdece.gheasy.notification.model.Notification;

import java.io.IOException;

public interface NotificationService {
    void sendNotification(Notification notification) throws IOException, InterruptedException;
}
