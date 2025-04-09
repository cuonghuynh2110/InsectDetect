package org.tensorflow.lite.examples.detection.Models;

public class ChatBotMessage {
    public static String SENT_BY_ME = "me";
    public static String SENT_BY_BOT="bot";

    String message;
    String sentBy;

    public ChatBotMessage() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSentBy() {
        return sentBy;
    }

    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }

    public ChatBotMessage(String message, String sentBy) {
        this.message = message;
        this.sentBy = sentBy;
    }
}