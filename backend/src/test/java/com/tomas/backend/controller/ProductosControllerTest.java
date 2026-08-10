package com.tomas.backend.controller;

import com.tomas.backend.DTOs.productos.ProductoCreateDTO;
import com.tomas.backend.DTOs.productos.ProductoResponseDTO;
import com.tomas.backend.DTOs.productos.ProductoUpdateDTO;
import com.tomas.backend.config.CorsConfig;
import com.tomas.backend.config.SecurityConfig;
import com.tomas.backend.security.CustomUserDetailsService;
import com.tomas.backend.security.JwtAuthenticationFilter;
import com.tomas.backend.service.productos.ProductoService;
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

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test de integración (FASE 3) del controlador de productos.
 *
 * Todos los endpoints de /productos/** están protegidos (authenticated), por eso
 * se usa @WithMockUser a nivel de clase.
 */
@WebMvcTest(ProductosController.class)
@Import({SecurityConfig.class, CorsConfig.class})
@WithMockUser(username = "admin@mail.com", roles = {"ADMIN"})
class ProductosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

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

    private ProductoResponseDTO productoResponse(Long id) {
        ProductoResponseDTO dto = new ProductoResponseDTO();
        dto.setProductoId(id);
        dto.setNombre("Procesador AMD Ryzen 5");
        dto.setDescripcion("CPU de 6 núcleos");
        dto.setPrecio(new BigDecimal("250000.00"));
        return dto;
    }

    @Test
    void deberiaRetornar200YListaAlListarProductos() throws Exception {
        // Arrange
        when(productoService.listarProductos()).thenReturn(List.of(productoResponse(1L)));

        // Act & Assert
        mockMvc.perform(get("/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productoId").value(1));
    }

    @Test
    void deberiaRetornar200AlObtenerProductoActivo() throws Exception {
        // Arrange
        when(productoService.obtenerProducto(1L)).thenReturn(productoResponse(1L));

        // Act & Assert
        mockMvc.perform(get("/productos/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productoId").value(1));
    }

    @Test
    void deberiaRetornar200AlCrearProductoValido() throws Exception {
        // Arrange
        when(productoService.crearProducto(any(ProductoCreateDTO.class))).thenReturn(productoResponse(1L));

        // Act & Assert
        mockMvc.perform(post("/productos/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Procesador AMD Ryzen 5\",\"categoriaId\":1,\"precio\":250000.00,\"descripcion\":\"CPU de 6 núcleos\",\"stock\":10}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productoId").value(1));
    }

    @Test
    void deberiaRetornar400CuandoElProductoCreateEsInvalido() throws Exception {
        // Act & Assert
        // Sin mockear nada: @Valid falla (faltan nombre, precio y stock) y
        // GlobalExceptionsHandler responde 400 antes de que el service se ejecute.
        mockMvc.perform(post("/productos/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"categoriaId\":1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar200AlActualizarProducto() throws Exception {
        // Arrange
        when(productoService.actualizarProducto(any(ProductoUpdateDTO.class), eq(1L)))
                .thenReturn(productoResponse(1L));

        // Act & Assert
        mockMvc.perform(post("/productos/actualizar/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Procesador AMD Ryzen 7\",\"categoriaId\":1,\"precio\":300000.00,\"stock\":5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productoId").value(1));
    }
}