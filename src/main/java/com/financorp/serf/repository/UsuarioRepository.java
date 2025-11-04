package com.financorp.serf.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.financorp.serf.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    /**
     * Busca un usuario por su nombre de usuario
     * @param username Nombre de usuario
     * @return Optional con el usuario si existe
     */
    Optional<Usuario> findByUsername(String username);
    
    /**
     * Verifica si existe un usuario con el username dado
     * @param username Nombre de usuario
     * @return true si existe, false en caso contrario
     */
    boolean existsByUsername(String username);
    
    /**
     * Busca un usuario por su email
     * @param email Email del usuario
     * @return Optional con el usuario si existe
     */
    Optional<Usuario> findByEmail(String email);
    
    /**
     * Verifica si existe un usuario con el email dado
     * @param email Email del usuario
     * @return true si existe, false en caso contrario
     */
    boolean existsByEmail(String email);
}