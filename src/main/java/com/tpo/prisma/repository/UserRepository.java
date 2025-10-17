package com.tpo.prisma.repository;

import com.tpo.prisma.model.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<Usuario, String> {
    
    // Buscar usuario por email (para login)
    Optional<Usuario> findByMail(String mail);
    
    // Buscar usuario por nombre de usuario (para validar unicidad en registro)
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);
    
    // Verificar si existe un email (para validar unicidad en registro)
    boolean existsByMail(String mail);
    
    // Verificar si existe un nombre de usuario (para validar unicidad en registro)
    boolean existsByNombreUsuario(String nombreUsuario);
}
