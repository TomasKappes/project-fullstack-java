package com.tomas.backend.controller;

import com.tomas.backend.DTOs.categoria.CategoriaCreateDTO;
import com.tomas.backend.DTOs.categoria.CategoriaResponseDTO;
import com.tomas.backend.config.CorsConfig;
import com.tomas.backend.config.SecurityConfig;
import com.tomas.backend.security.CustomUserDetailsService;
import com.tomas.backend.security.JwtAuthenticationFilter;
import com.tomas.backend.service.categorias.CategoriaService;
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
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test de integración (FASE 3) del controlador de categorías.
 *
 * Todos los endpoints de /categorias/** están protegidos (authenticated), por eso
 * se usa @WithMockUser a nivel de clase.
 */
@WebMvcTest(CategoriasController.class)
@Import({SecurityConfig.class, CorsConfig.class})
@WithMockUser(username = "admin@mail.com", roles = {"ADMIN"})
class CategoriasControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoriaService categoriaService;

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

    private CategoriaResponseDTO categoriaResponse(Long id) {
        CategoriaResponseDTO dto = new CategoriaResponseDTO();
        dto.setCategoriaId(id);
        dto.setNombre("Procesadores");
        return dto;
    }

    @Test
    void deberiaRetornar200YListaAlListarCategorias() throws Exception {
        // Arrange
        when(categoriaService.listarCategorias()).thenReturn(List.of(categoriaResponse(1L)));

        // Act & Assert
        mockMvc.perform(get("/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoriaId").value(1));
    }

    @Test
    void deberiaRetornar200AlObtenerCategoriaActiva() throws Exception {
        // Arrange
        when(categoriaService.obtenerCategoria(1L)).thenReturn(categoriaResponse(1L));

        // Act & Assert
        mockMvc.perform(get("/categorias/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoriaId").value(1));
    }

    @Test
    void deberiaRetornar200AlCrearCategoria() throws Exception {
        // Arrange
        when(categoriaService.crearCategoria(any(CategoriaCreateDTO.class)))
                .thenReturn(categoriaResponse(1L));

        // Act & Assert
        mockMvc.perform(post("/categorias/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Procesadores\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoriaId").value(1));
    }
}