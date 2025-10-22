package com.tpo.prisma.dto;

public class CreateChatMessageRequest {
    private String text;

    public CreateChatMessageRequest() {
    }

    public CreateChatMessageRequest(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
