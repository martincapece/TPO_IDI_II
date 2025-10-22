package com.tpo.prisma.repository;

import com.tpo.prisma.model.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<Usuario, String> {
    
    Optional<Usuario> findByMail(String mail);
    
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);
    
    boolean existsByMail(String mail);
    
    boolean existsByNombreUsuario(String nombreUsuario);
}
