package com.tpo.prisma.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tpo.prisma.dto.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class StreamingChatService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final ObjectMapper objectMapper;
    
    // Configuración TTL: 5 minutos después de finalizar el streaming
    private static final long CHAT_TTL_AFTER_END_MINUTES = 5;
    private static final int MAX_MESSAGES = 500;

    public StreamingChatService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Envía un mensaje de chat para un Streaming (Redis)
     */
    public ChatMessage sendMessage(String chatId, String streamId, String userId, String username, String text) {
        ChatMessage message = new ChatMessage(userId, username, text);

        try {
            String messageJson = objectMapper.writeValueAsString(message);
            String redisKey = "chat:" + chatId + ":messages";
            
            // Agregar mensaje a la lista en Redis
            redisTemplate.opsForList().rightPush(redisKey, messageJson);
            
            // Mantener solo los últimos 500 mensajes
            redisTemplate.opsForList().trim(redisKey, -500, -1);
            
            return message;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al serializar mensaje de chat", e);
        }
    }

    /**
     * Obtiene mensajes recientes de un chat de Streaming
     */
    public List<ChatMessage> getRecentMessages(String chatId, int limit) {
        String redisKey = "chat:" + chatId + ":messages";
        
        // Obtener los últimos N mensajes
        List<String> messagesJson = redisTemplate.opsForList().range(redisKey, -limit, -1);
        
        if (messagesJson == null || messagesJson.isEmpty()) {
            return new ArrayList<>();
        }

        List<ChatMessage> messages = new ArrayList<>();
        for (String json : messagesJson) {
            try {
                ChatMessage message = objectMapper.readValue(json, ChatMessage.class);
                messages.add(message);
            } catch (JsonProcessingException e) {
                // Log error pero continuar con otros mensajes
                System.err.println("Error al deserializar mensaje: " + e.getMessage());
            }
        }
        
        return messages;
    }

    /**
     * Limpia el chat de un Streaming
     */
    public void clearChat(String chatId) {
        String redisKey = "chat:" + chatId + ":messages";
        redisTemplate.delete(redisKey);
    }
    
    /**
     * Establece TTL de 5 minutos para el chat cuando el streaming finaliza
     * @param chatId ID del chat
     */
    public void setExpirationOnStreamEnd(String chatId) {
        String redisKey = "chat:" + chatId + ":messages";
        redisTemplate.expire(redisKey, CHAT_TTL_AFTER_END_MINUTES, TimeUnit.MINUTES);
    }
    
    /**
     * Obtiene el tiempo restante de vida del chat (en segundos)
     * @param chatId ID del chat
     * @return Segundos restantes, -1 si no tiene TTL, -2 si no existe
     */
    public Long getChatTTL(String chatId) {
        String redisKey = "chat:" + chatId + ":messages";
        return redisTemplate.getExpire(redisKey, TimeUnit.SECONDS);
    }
}
