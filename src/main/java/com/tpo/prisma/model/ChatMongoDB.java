package com.tpo.prisma.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "chat_messages")
public class ChatMongoDB {
    
    @Id
    private String id;
    private String chatId;
    private String contentId;
    private String userId;
    private String username;
    private String text;
    private LocalDateTime timestamp;

    public ChatMongoDB() {
        this.timestamp = LocalDateTime.now();
    }

    public ChatMongoDB(String chatId, String contentId, String userId, String username, String text) {
        this.chatId = chatId;
        this.contentId = contentId;
        this.userId = userId;
        this.username = username;
        this.text = text;
        this.timestamp = LocalDateTime.now();
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
