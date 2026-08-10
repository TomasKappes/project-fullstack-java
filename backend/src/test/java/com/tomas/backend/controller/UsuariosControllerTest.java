package com.tomas.backend.controller;

import com.tomas.backend.DTOs.usuarios.UsuarioResponseDTO;
import com.tomas.backend.DTOs.usuarios.UsuarioUpdateDTO;
import com.tomas.backend.config.CorsConfig;
import com.tomas.backend.config.SecurityConfig;
import com.tomas.backend.security.CustomUserDetailsService;
import com.tomas.backend.security.JwtAuthenticationFilter;
import com.tomas.backend.service.usuarios.UsuarioService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test de integración (FASE 3) del controlador de usuarios.
 *
 * Todos los endpoints de /users/** están protegidos (authenticated), por eso
 * se usa @WithMockUser a nivel de clase.
 */
@WebMvcTest(UsuariosController.class)
@Import({SecurityConfig.class, CorsConfig.class})
@WithMockUser(username = "admin@mail.com", roles = {"ADMIN"})
class UsuariosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

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

    private UsuarioResponseDTO usuarioResponse(Long id) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(id);
        dto.setEmail("test@mail.com");
        dto.setNombre("Tomás");
        return dto;
    }

    @Test
    void deberiaRetornar200AlObtenerUsuarioPorId() throws Exception {
        // Arrange
        when(usuarioService.obtenerUsuario(1L)).thenReturn(usuarioResponse(1L));

        // Act & Assert
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deberiaRetornar200YListaAlListarUsuarios() throws Exception {
        // Arrange
        when(usuarioService.listaUsuarios()).thenReturn(List.of(usuarioResponse(1L)));

        // Act & Assert
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void deberiaRetornar200AlActualizarUsuario() throws Exception {
        // Arrange
        when(usuarioService.actualizarUsuario(eq(1L), any(UsuarioUpdateDTO.class)))
                .thenReturn(usuarioResponse(1L));

        // Act & Assert
        mockMvc.perform(put("/users/Update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@mail.com\",\"password\":\"12345678\",\"nombre\":\"Tomás\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deberiaRetornar200AlEliminarUsuario() throws Exception {
        // Act & Assert
        // eliminarUsuario es void: no hace falta stub, el mock devuelve sin hacer nada.
        mockMvc.perform(delete("/users/Delete/1"))
                .andExpect(status().isOk());
    }
}