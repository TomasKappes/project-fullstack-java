package com.tomas.backend.controller;

import com.tomas.backend.DTOs.auth.AuthResponse;
import com.tomas.backend.DTOs.auth.LoginRequest;
import com.tomas.backend.DTOs.auth.RegisterRequest;
import com.tomas.backend.DTOs.auth.RegisterResponse;
import com.tomas.backend.config.CorsConfig;
import com.tomas.backend.config.SecurityConfig;
import com.tomas.backend.excetions.custom.ConflictException;
import com.tomas.backend.security.CustomUserDetailsService;
import com.tomas.backend.security.JwtAuthenticationFilter;
import com.tomas.backend.service.usuarios.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test de integración (FASE 3) del controlador de autenticación.
 *
 * Estrategia: @WebMvcTest levanta solo la capa web (controller + @RestControllerAdvice)
 * y @Import(SecurityConfig.class) carga la configuración REAL de seguridad, donde
 * /auth/login y /auth/register son permitAll. No se necesita BD ni @WithMockUser
 * porque estos endpoints son públicos.
 */
@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, CorsConfig.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    // Beans que SecurityConfig necesita como dependencias del constructor
    // y que @WebMvcTest no provee:
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() throws Exception {
        // El filtro JWT es un mock: si no delegamos en la cadena, la petición
        // nunca llega al controller (el mock no ejecuta chain.doFilter()).
        // Lo configuramos como "pass-through" para que sea transparente.
        doAnswer(invocation -> {
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(jwtAuthenticationFilter)
                .doFilter(any(ServletRequest.class), any(ServletResponse.class), any(FilterChain.class));
    }

    @Test
    void deberiaRetornar200YTokenAlHacerLogin() throws Exception {
        // Arrange
        AuthResponse authResponse = new AuthResponse("token123", 1L);
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@mail.com\",\"password\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token123"))
                .andExpect(jsonPath("$.usuarioId").value(1));
    }

    @Test
    void deberiaRetornar200AlRegistrarUsuario() throws Exception {
        // Arrange
        RegisterResponse registerResponse = new RegisterResponse("Usuario registrado con exito");
        when(authService.register(any(RegisterRequest.class))).thenReturn(registerResponse);

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"Tomás\",\"email\":\"test@mail.com\",\"password\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Usuario registrado con exito"));
    }

    @Test
    void deberiaRetornar409CuandoElEmailYaEstaRegistrado() throws Exception {
        // Arrange
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new ConflictException("El email ya está registrado"));

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"Tomás\",\"email\":\"test@mail.com\",\"password\":\"123456\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("El email ya está registrado"));
    }
}