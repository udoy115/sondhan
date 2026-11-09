package com.sondhan.app.models;

public class ChatMessage {
    public static final int TYPE_USER = 1;
    public static final int TYPE_AI = 2;

    private int type;
    private String content;

    public ChatMessage(int type, String content) {
        this.type = type;
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public String getContent() {
        return content;
    }
}

