package com.tpo.prisma.repository;

import com.tpo.prisma.model.ChatMongoDB;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatMongoRepository extends MongoRepository<ChatMongoDB, String> {
    List<ChatMongoDB> findByChatIdOrderByTimestampDesc(String chatId);
    List<ChatMongoDB> findByContentIdOrderByTimestampDesc(String contentId);
    void deleteByChatId(String chatId);
}
