package com.tomas.backend.controller;

import com.tomas.backend.DTOs.pedidos.PedidosCreateDTO;
import com.tomas.backend.DTOs.pedidos.PedidosResponseDTO;
import com.tomas.backend.config.CorsConfig;
import com.tomas.backend.config.SecurityConfig;
import com.tomas.backend.enums.EstadoPedido;
import com.tomas.backend.security.CustomUserDetailsService;
import com.tomas.backend.security.JwtAuthenticationFilter;
import com.tomas.backend.service.pedidos.PedidoService;
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
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test de integración (FASE 3) del controlador de pedidos.
 *
 * Todos los endpoints de /pedidos/** están protegidos (authenticated), por eso
 * se usa @WithMockUser a nivel de clase: inyecta una autenticación simulada en
 * el SecurityContext sin pasar por el filtro JWT real.
 */
@WebMvcTest(PedidosController.class)
@Import({SecurityConfig.class, CorsConfig.class})
@WithMockUser(username = "admin@mail.com", roles = {"ADMIN"})
class PedidosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PedidoService pedidoService;

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

    private PedidosResponseDTO pedidoResponse(Long id, EstadoPedido estado) {
        PedidosResponseDTO dto = new PedidosResponseDTO();
        dto.setIdPedido(id);
        dto.setEstadoPedido(estado);
        dto.setNombreUsuario("Tomás");
        dto.setFechaPedido(LocalDateTime.now());
        dto.setValorTotal(new BigDecimal("1500.00"));
        return dto;
    }

    @Test
    void deberiaRetornar200AlCrearPedidoValido() throws Exception {
        // Arrange
        PedidosResponseDTO dto = pedidoResponse(1L, EstadoPedido.PRESUPUESTADO);
        when(pedidoService.crear(any(PedidosCreateDTO.class))).thenReturn(dto);

        // Act & Assert
        mockMvc.perform(post("/pedidos/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usuarioId\":1,\"pedidosDetalle\":[{\"productoId\":1,\"cantidad\":1},{\"productoId\":2,\"cantidad\":1},{\"productoId\":3,\"cantidad\":1}]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPedido").value(1));
    }

    @Test
    void deberiaRetornar400CuandoElRequestDeCrearEsInvalido() throws Exception {
        // Act & Assert
        // Sin mockear nada: la validación @Valid falla (falta usuarioId) y
        // GlobalExceptionsHandler responde 400 antes de que el service se ejecute.
        mockMvc.perform(post("/pedidos/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pedidosDetalle\":[]}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar200AlObtenerPedidoPorId() throws Exception {
        // Arrange
        PedidosResponseDTO dto = pedidoResponse(1L, EstadoPedido.PRESUPUESTADO);
        when(pedidoService.obtenerPedido(1L)).thenReturn(dto);

        // Act & Assert
        mockMvc.perform(get("/pedidos/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPedido").value(1));
    }

    @Test
    void deberiaRetornar200AlConfirmarPedido() throws Exception {
        // Arrange
        PedidosResponseDTO dto = pedidoResponse(1L, EstadoPedido.CONFIRMADO);
        when(pedidoService.confirmarPedido(1L)).thenReturn(dto);

        // Act & Assert
        mockMvc.perform(put("/pedidos/confirmar/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoPedido").value("CONFIRMADO"));
    }

    @Test
    void deberiaRetornar200AlReCrearPedido() throws Exception {
        // Arrange
        PedidosResponseDTO dto = pedidoResponse(2L, EstadoPedido.PRESUPUESTADO);
        when(pedidoService.reCrearPedido(eq(1L), any(PedidosCreateDTO.class))).thenReturn(dto);

        // Act & Assert
        mockMvc.perform(put("/pedidos/agregar-producto/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usuarioId\":1,\"pedidosDetalle\":[{\"productoId\":1,\"cantidad\":1},{\"productoId\":2,\"cantidad\":1},{\"productoId\":3,\"cantidad\":1}]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPedido").value(2));
    }
}