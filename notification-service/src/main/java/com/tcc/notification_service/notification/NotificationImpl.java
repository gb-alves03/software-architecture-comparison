package com.tcc.notification_service.notification;

public class NotificationImpl implements Notification {

    @Override
    public void send(Long accountId, String message) {
        try {
            String email = createEmailMessage(accountId, message);
            System.out.println(email);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String createEmailMessage(Long accountId, String message) {
        StringBuilder sb = new StringBuilder("");

        sb.append("[accountId]= ");
        sb.append(accountId);
        sb.append("\n");
        sb.append("[message]= ");
        sb.append(message);

        return sb.toString().trim();
    }
}
