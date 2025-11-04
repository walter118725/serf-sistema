package com.financorp.serf.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.financorp.serf.model.Usuario;
import com.financorp.serf.repository.UsuarioRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UserDetailsService {
    
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
        
        // Actualizar último acceso
        usuario.setUltimoAcceso(LocalDateTime.now());
        usuarioRepository.save(usuario);
        
        return usuario;
    }
    
    /**
     * Crear usuario por defecto si no existe
     */
    @PostConstruct
    public void crearUsuarioAdminPorDefecto() {
        if (!usuarioRepository.existsByUsername("admin")) {
            Usuario admin = Usuario.builder()
                .username("admin")
                .password(passwordEncoder.encode("123456"))
                .nombre("Administrador del Sistema")
                .email("admin@financorp.com")
                .rol(Usuario.Rol.ADMIN)
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .build();
            
            usuarioRepository.save(admin);
            System.out.println("✅ Usuario administrador creado: admin / 123456");
        }
    }
    
    /**
     * Obtener usuario por username
     */
    public Optional<Usuario> obtenerPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }
    
    /**
     * Crear nuevo usuario
     */
    public Usuario crearUsuario(String username, String password, String nombre, String email, Usuario.Rol rol) {
        if (usuarioRepository.existsByUsername(username)) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }
        
        if (usuarioRepository.existsByEmail(email)) {
            throw new RuntimeException("El email ya está registrado");
        }
        
        Usuario usuario = Usuario.builder()
            .username(username)
            .password(passwordEncoder.encode(password))
            .nombre(nombre)
            .email(email)
            .rol(rol)
            .activo(true)
            .fechaCreacion(LocalDateTime.now())
            .build();
        
        return usuarioRepository.save(usuario);
    }
    
    /**
     * Listar todos los usuarios
     */
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }
    
    /**
     * Activar/desactivar usuario
     */
    public Usuario cambiarEstado(Long id, boolean activo) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        usuario.setActivo(activo);
        return usuarioRepository.save(usuario);
    }
    
    /**
     * Cambiar contraseña
     */
    public void cambiarPassword(String username, String nuevaPassword) {
        Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);
    }
}