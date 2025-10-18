package com.tpo.prisma.repository.repositoryPostgre;

import com.tpo.prisma.model.modelPostgre.UsuarioRef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRefRepository extends JpaRepository<UsuarioRef, String> {
}   