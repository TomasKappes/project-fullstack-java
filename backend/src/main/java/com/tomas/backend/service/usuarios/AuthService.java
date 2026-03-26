package com.tomas.backend.service.usuarios;

import com.tomas.backend.DTOs.auth.AuthResponse;
import com.tomas.backend.DTOs.auth.LoginRequest;
import com.tomas.backend.DTOs.auth.RegisterRequest;
import com.tomas.backend.entity.Usuario;
import com.tomas.backend.enums.Roles;
import com.tomas.backend.repository.UsuarioRepository;
import com.tomas.backend.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository, JwtService jwtService, AuthenticationManager authenticationManager,PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }



public AuthResponse login(LoginRequest request) {

    authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
            )
    );

    Usuario user = usuarioRepository.findByEmail(request.getEmail()).orElseThrow();

    String token = jwtService.generateToken(user);

    return new AuthResponse(token);
}

public String register(RegisterRequest request) {
    Usuario usuario = new Usuario();
    usuario.setRol(Roles.USER);
    usuario.setNombre(request.getUsername());
    usuario.setEmail(request.getEmail());
    usuario.setPassword(passwordEncoder.encode(request.getPassword()));
    usuarioRepository.save(usuario);
    return "User registered successfully";
}}
