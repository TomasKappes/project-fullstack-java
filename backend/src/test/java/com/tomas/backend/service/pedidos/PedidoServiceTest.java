package com.tomas.backend.service.pedidos;

import com.tomas.backend.DTOs.pedidos.PedidosCreateDTO;
import com.tomas.backend.DTOs.pedidos.PedidosResponseDTO;
import com.tomas.backend.DTOs.pedidos_detalles.PedidoDetallesCreateDTO;
import com.tomas.backend.entity.Pedido;
import com.tomas.backend.entity.PedidoDetalle;
import com.tomas.backend.entity.Producto;
import com.tomas.backend.entity.Usuario;
import com.tomas.backend.enums.EstadoPedido;
import com.tomas.backend.excetions.custom.BadRequestException;
import com.tomas.backend.excetions.custom.ConflictException;
import com.tomas.backend.excetions.custom.ResourceNotFoundException;
import com.tomas.backend.mappers.PedidoDetalleMapper;
import com.tomas.backend.mappers.PedidoMapper;
import com.tomas.backend.repository.PedidoRepository;
import com.tomas.backend.repository.ProductoRepository;
import com.tomas.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private PedidoMapper pedidoMapper;

    @Mock
    private PedidoDetalleMapper pedidoDetalleMapper;

    @Mock
    private CompatibilidadService compatibilidadService;

    @InjectMocks
    private PedidoService pedidoService;

    // =====================================================================
    // crear(...)
    // =====================================================================

    @Test
    void deberiaCrearPedidoCorrectamente() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setNombre("Tomás");

        Producto producto1 = crearProducto(1L, "CPU AMD", new BigDecimal("100.00"), 10);
        Producto producto2 = crearProducto(2L, "Motherboard AMD", new BigDecimal("50.00"), 10);
        Producto producto3 = crearProducto(3L, "RAM 16GB", new BigDecimal("25.00"), 10);

        PedidoDetallesCreateDTO detalleDTO1 = crearDetalleDTO(1L, 1);
        PedidoDetallesCreateDTO detalleDTO2 = crearDetalleDTO(2L, 1);
        PedidoDetallesCreateDTO detalleDTO3 = crearDetalleDTO(3L, 1);

        PedidosCreateDTO createDTO = new PedidosCreateDTO();
        createDTO.setUsuarioId(1L);
        createDTO.setPedidosDetalle(Arrays.asList(detalleDTO1, detalleDTO2, detalleDTO3));

        PedidoDetalle detalle1 = new PedidoDetalle();
        detalle1.setCantidad(1);
        PedidoDetalle detalle2 = new PedidoDetalle();
        detalle2.setCantidad(1);
        PedidoDetalle detalle3 = new PedidoDetalle();
        detalle3.setCantidad(1);

        PedidosResponseDTO responseDTO = new PedidosResponseDTO();
        responseDTO.setIdPedido(10L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(pedidoDetalleMapper.toEntity(detalleDTO1)).thenReturn(detalle1);
        when(pedidoDetalleMapper.toEntity(detalleDTO2)).thenReturn(detalle2);
        when(pedidoDetalleMapper.toEntity(detalleDTO3)).thenReturn(detalle3);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto1));
        when(productoRepository.findById(2L)).thenReturn(Optional.of(producto2));
        when(productoRepository.findById(3L)).thenReturn(Optional.of(producto3));
        when(pedidoMapper.toResponseDTO(any(Pedido.class))).thenReturn(responseDTO);

        // Act
        PedidosResponseDTO resultado = pedidoService.crear(createDTO);

        // Assert
        assertEquals(10L, resultado.getIdPedido());

        // El pedido se guarda con estado PRESUPUESTADO, total calculado y fecha seteada.
        ArgumentCaptor<Pedido> captor = ArgumentCaptor.forClass(Pedido.class);
        verify(pedidoRepository).save(captor.capture());

        Pedido pedidoGuardado = captor.getValue();
        assertEquals(EstadoPedido.PRESUPUESTADO, pedidoGuardado.getEstado());
        assertEquals(0, pedidoGuardado.getTotal().compareTo(new BigDecimal("175.00")));
        assertNotNull(pedidoGuardado.getFecha());
        assertEquals(3, pedidoGuardado.getPedidoDetalles().size());
    }

    @Test
    void deberiaCalcularTotalCorrectamente() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);

        Producto producto1 = crearProducto(1L, "Producto 1", new BigDecimal("100.00"), 10);
        Producto producto2 = crearProducto(2L, "Producto 2", new BigDecimal("50.00"), 10);
        Producto producto3 = crearProducto(3L, "Producto 3", new BigDecimal("25.00"), 10);

        PedidoDetallesCreateDTO dto1 = crearDetalleDTO(1L, 2);
        PedidoDetallesCreateDTO dto2 = crearDetalleDTO(2L, 3);
        PedidoDetallesCreateDTO dto3 = crearDetalleDTO(3L, 1);

        PedidosCreateDTO createDTO = new PedidosCreateDTO();
        createDTO.setUsuarioId(1L);
        createDTO.setPedidosDetalle(Arrays.asList(dto1, dto2, dto3));

        PedidoDetalle detalle1 = new PedidoDetalle();
        detalle1.setCantidad(2);
        PedidoDetalle detalle2 = new PedidoDetalle();
        detalle2.setCantidad(3);
        PedidoDetalle detalle3 = new PedidoDetalle();
        detalle3.setCantidad(1);

        PedidosResponseDTO responseDTO = new PedidosResponseDTO();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(pedidoDetalleMapper.toEntity(dto1)).thenReturn(detalle1);
        when(pedidoDetalleMapper.toEntity(dto2)).thenReturn(detalle2);
        when(pedidoDetalleMapper.toEntity(dto3)).thenReturn(detalle3);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto1));
        when(productoRepository.findById(2L)).thenReturn(Optional.of(producto2));
        when(productoRepository.findById(3L)).thenReturn(Optional.of(producto3));
        when(pedidoMapper.toResponseDTO(any(Pedido.class))).thenReturn(responseDTO);

        // Act
        pedidoService.crear(createDTO);

        // Assert
        // 100x2 + 50x3 + 25x1 = 200 + 150 + 25 = 375
        ArgumentCaptor<Pedido> captor = ArgumentCaptor.forClass(Pedido.class);
        verify(pedidoRepository).save(captor.capture());

        assertEquals(0, captor.getValue().getTotal().compareTo(new BigDecimal("375.00")));
    }

    @Test
    void deberiaLanzarResourceNotFoundCuandoUsuarioNoExiste() {
        // Arrange
        PedidosCreateDTO createDTO = crearPedidosCreateDTO(99L, 3);

        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException excepcion = assertThrows(
                ResourceNotFoundException.class,
                () -> pedidoService.crear(createDTO)
        );

        // Ojo: typo real de produccion ("Valido" con mayuscula).
        assertEquals("Usuario no Valido", excepcion.getMessage());
    }

    @Test
    void deberiaLanzarBadRequestCuandoHayMenosDe3Componentes() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);

        PedidosCreateDTO createDTO = new PedidosCreateDTO();
        createDTO.setUsuarioId(1L);
        createDTO.setPedidosDetalle(Arrays.asList(
                crearDetalleDTO(1L, 1),
                crearDetalleDTO(2L, 1)
        ));

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // Act & Assert
        BadRequestException excepcion = assertThrows(
                BadRequestException.class,
                () -> pedidoService.crear(createDTO)
        );

        // Ojo: typo real de produccion ("almenos").
        assertEquals("El pedido debe tener almenos 3 componentes.", excepcion.getMessage());

        // La validacion de compatibilidad ni siquiera se intenta.
        verify(compatibilidadService, never()).validarDetalle(anyList());
    }

    @Test
    void deberiaLanzarConflictCuandoStockEsInsuficiente() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);

        Producto producto1 = crearProducto(1L, "CPU AMD", new BigDecimal("100.00"), 10);
        Producto producto2 = crearProducto(2L, "Motherboard AMD", new BigDecimal("150.00"), 10);
        // El tercer producto tiene stock 2 pero se piden 5.
        Producto productoMalo = crearProducto(3L, "RAM 16GB", new BigDecimal("25.00"), 2);

        PedidoDetallesCreateDTO dto1 = crearDetalleDTO(1L, 1);
        PedidoDetallesCreateDTO dto2 = crearDetalleDTO(2L, 1);
        PedidoDetallesCreateDTO dto3 = crearDetalleDTO(3L, 5);

        PedidosCreateDTO createDTO = new PedidosCreateDTO();
        createDTO.setUsuarioId(1L);
        createDTO.setPedidosDetalle(Arrays.asList(dto1, dto2, dto3));

        PedidoDetalle detalle1 = new PedidoDetalle();
        detalle1.setCantidad(1);
        PedidoDetalle detalle2 = new PedidoDetalle();
        detalle2.setCantidad(1);
        PedidoDetalle detalle3 = new PedidoDetalle();
        detalle3.setCantidad(5);

        // Los 3 detalles se procesan en orden: 1 y 2 bien, el 3° lanza.
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(pedidoDetalleMapper.toEntity(dto1)).thenReturn(detalle1);
        when(pedidoDetalleMapper.toEntity(dto2)).thenReturn(detalle2);
        when(pedidoDetalleMapper.toEntity(dto3)).thenReturn(detalle3);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto1));
        when(productoRepository.findById(2L)).thenReturn(Optional.of(producto2));
        when(productoRepository.findById(3L)).thenReturn(Optional.of(productoMalo));

        // Act & Assert
        ConflictException excepcion = assertThrows(
                ConflictException.class,
                () -> pedidoService.crear(createDTO)
        );

        // En crear() el mensaje SI lleva espacio antes de "sin".
        String mensajeEsperado = "Producto " + productoMalo.getNombre() + " sin stock disponible";
        assertEquals(mensajeEsperado, excepcion.getMessage());
    }

    @Test
    void deberiaLanzarResourceNotFoundCuandoProductoNoExiste() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);

        PedidoDetallesCreateDTO dto1 = crearDetalleDTO(1L, 1);
        PedidoDetallesCreateDTO dto2 = crearDetalleDTO(2L, 1);
        PedidoDetallesCreateDTO dto3 = crearDetalleDTO(3L, 1);

        PedidosCreateDTO createDTO = new PedidosCreateDTO();
        createDTO.setUsuarioId(1L);
        createDTO.setPedidosDetalle(Arrays.asList(dto1, dto2, dto3));

        PedidoDetalle detalle1 = new PedidoDetalle();
        detalle1.setCantidad(1);

        // Solo se consume el primer detalle: el loop lanza antes de llegar a 2 y 3.
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(pedidoDetalleMapper.toEntity(dto1)).thenReturn(detalle1);
        when(productoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException excepcion = assertThrows(
                ResourceNotFoundException.class,
                () -> pedidoService.crear(createDTO)
        );

        assertEquals("Producto no encontrado", excepcion.getMessage());
    }

    @Test
    void deberiaPropagarExcepcionDeCompatibilidad() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);

        PedidosCreateDTO createDTO = crearPedidosCreateDTO(1L, 3);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        doThrow(new BadRequestException("incompatibles"))
                .when(compatibilidadService).validarDetalle(anyList());

        // Act & Assert
        BadRequestException excepcion = assertThrows(
                BadRequestException.class,
                () -> pedidoService.crear(createDTO)
        );

        assertEquals("incompatibles", excepcion.getMessage());
        verify(compatibilidadService).validarDetalle(anyList());
    }

    // =====================================================================
    // obtenerPedido(...)
    // =====================================================================

    @Test
    void deberiaObtenerPedidoCuandoExiste() {
        // Arrange
        Pedido pedido = crearPedido(EstadoPedido.PRESUPUESTADO);

        PedidosResponseDTO responseDTO = new PedidosResponseDTO();
        responseDTO.setIdPedido(1L);
        responseDTO.setEstadoPedido(EstadoPedido.PRESUPUESTADO);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoMapper.toResponseDTO(pedido)).thenReturn(responseDTO);

        // Act
        PedidosResponseDTO resultado = pedidoService.obtenerPedido(1L);

        // Assert
        assertEquals(1L, resultado.getIdPedido());
        assertEquals(EstadoPedido.PRESUPUESTADO, resultado.getEstadoPedido());
    }

    @Test
    void deberiaLanzarResourceNotFoundCuandoPedidoNoExiste() {
        // Arrange
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException excepcion = assertThrows(
                ResourceNotFoundException.class,
                () -> pedidoService.obtenerPedido(99L)
        );

        assertEquals("Pedido no encontrado", excepcion.getMessage());
    }

    // =====================================================================
    // confirmarPedido(...)
    // =====================================================================

    @Test
    void deberiaConfirmarPedidoYDescontarStock() {
        // Arrange
        Producto producto1 = crearProducto(1L, "CPU AMD", new BigDecimal("200.00"), 10);
        Producto producto2 = crearProducto(2L, "Motherboard AMD", new BigDecimal("150.00"), 5);

        PedidoDetalle detalle1 = crearPedidoDetalle(producto1, 3);
        PedidoDetalle detalle2 = crearPedidoDetalle(producto2, 5);

        Pedido pedido = crearPedido(EstadoPedido.PRESUPUESTADO);
        pedido.setPedidoDetalles(Arrays.asList(detalle1, detalle2));

        PedidosResponseDTO responseDTO = new PedidosResponseDTO();
        responseDTO.setIdPedido(1L);
        responseDTO.setEstadoPedido(EstadoPedido.CONFIRMADO);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoMapper.toResponseDTO(pedido)).thenReturn(responseDTO);

        // Act
        PedidosResponseDTO resultado = pedidoService.confirmarPedido(1L);

        // Assert
        assertEquals(EstadoPedido.CONFIRMADO, pedido.getEstado());
        // El descuento muta los mismos objetos Producto que armamos.
        assertEquals(7, producto1.getStock());
        assertEquals(0, producto2.getStock());
        assertSame(responseDTO, resultado);
    }

    @Test
    void deberiaLanzarConflictSiPedidoYaConfirmado() {
        // Arrange
        Pedido pedido = crearPedido(EstadoPedido.CONFIRMADO);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        // Act & Assert
        ConflictException excepcion = assertThrows(
                ConflictException.class,
                () -> pedidoService.confirmarPedido(1L)
        );

        assertEquals("Este pedido ya fue confirmado", excepcion.getMessage());
    }

    @Test
    void deberiaLanzarResourceNotFoundSiPedidoNoExiste() {
        // Arrange
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> pedidoService.confirmarPedido(99L)
        );
    }

    @Test
    void deberiaLanzarConflictSiStockInsuficienteAlConfirmar() {
        // Arrange
        Producto producto = crearProducto(1L, "RAM 16GB", new BigDecimal("25.00"), 2);
        PedidoDetalle detalle = crearPedidoDetalle(producto, 5);

        Pedido pedido = crearPedido(EstadoPedido.PRESUPUESTADO);
        pedido.setPedidoDetalles(Arrays.asList(detalle));

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        // Act & Assert
        ConflictException excepcion = assertThrows(
                ConflictException.class,
                () -> pedidoService.confirmarPedido(1L)
        );

        // Mensaje unificado (2026-08-08): igual que en crear(), con espacio antes de "sin".
        String mensajeEsperado = "Producto " + producto.getNombre() + " sin stock disponible";
        assertEquals(mensajeEsperado, excepcion.getMessage());

        // La excepcion se lanza dentro del loop, antes de setear CONFIRMADO.
        assertEquals(EstadoPedido.PRESUPUESTADO, pedido.getEstado());
    }

    // =====================================================================
    // reCrearPedido(...)
    // =====================================================================

    @Test
    void deberiaReCrearPedidoCuandoEstaPresupuestado() {
        // Arrange
        Pedido pedidoOriginal = crearPedido(EstadoPedido.PRESUPUESTADO);

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);

        Producto producto1 = crearProducto(1L, "CPU AMD", new BigDecimal("100.00"), 10);
        Producto producto2 = crearProducto(2L, "Motherboard AMD", new BigDecimal("50.00"), 10);
        Producto producto3 = crearProducto(3L, "RAM 16GB", new BigDecimal("25.00"), 10);

        PedidoDetallesCreateDTO dto1 = crearDetalleDTO(1L, 1);
        PedidoDetallesCreateDTO dto2 = crearDetalleDTO(2L, 1);
        PedidoDetallesCreateDTO dto3 = crearDetalleDTO(3L, 1);

        PedidosCreateDTO createDTO = new PedidosCreateDTO();
        createDTO.setUsuarioId(1L);
        createDTO.setPedidosDetalle(Arrays.asList(dto1, dto2, dto3));

        PedidoDetalle detalle1 = new PedidoDetalle();
        detalle1.setCantidad(1);
        PedidoDetalle detalle2 = new PedidoDetalle();
        detalle2.setCantidad(1);
        PedidoDetalle detalle3 = new PedidoDetalle();
        detalle3.setCantidad(1);

        PedidosResponseDTO responseDTO = new PedidosResponseDTO();
        responseDTO.setIdPedido(20L);

        // reCrearPedido delega en el crear() real de la misma instancia:
        // hay que stubbear TODOS los colaboradores de crear().
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoOriginal));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(pedidoDetalleMapper.toEntity(dto1)).thenReturn(detalle1);
        when(pedidoDetalleMapper.toEntity(dto2)).thenReturn(detalle2);
        when(pedidoDetalleMapper.toEntity(dto3)).thenReturn(detalle3);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto1));
        when(productoRepository.findById(2L)).thenReturn(Optional.of(producto2));
        when(productoRepository.findById(3L)).thenReturn(Optional.of(producto3));
        when(pedidoMapper.toResponseDTO(any(Pedido.class))).thenReturn(responseDTO);

        // Act
        PedidosResponseDTO resultado = pedidoService.reCrearPedido(1L, createDTO);

        // Assert
        // El pedido original se cancela en memoria (no se guarda explicitamente).
        assertEquals(EstadoPedido.CANCELADO, pedidoOriginal.getEstado());
        assertEquals(20L, resultado.getIdPedido());

        // save() se llama UNA vez: desde el crear() interno, no para el CANCELADO.
        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    void deberiaLanzarConflictAlReCrearPedidoConfirmado() {
        // Arrange
        Pedido pedido = crearPedido(EstadoPedido.CONFIRMADO);
        PedidosCreateDTO createDTO = crearPedidosCreateDTO(1L, 3);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        // Act & Assert
        ConflictException excepcion = assertThrows(
                ConflictException.class,
                () -> pedidoService.reCrearPedido(1L, createDTO)
        );

        // Ojo: typo real de produccion ("modifcarse").
        assertEquals("Este pedido no puede modifcarse debido a que ya fue confirmado",
                excepcion.getMessage());

        // Ni siquiera se intenta validar el usuario del nuevo pedido.
        verify(usuarioRepository, never()).findById(any(Long.class));
    }

    @Test
    void deberiaLanzarResourceNotFoundAlReCrearPedidoInexistente() {
        // Arrange
        PedidosCreateDTO createDTO = crearPedidosCreateDTO(1L, 3);

        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> pedidoService.reCrearPedido(99L, createDTO)
        );
    }

    @Test
    void deberiaPropagarErrorDeValidacionDelNuevoPedido() {
        // Arrange
        Pedido pedidoOriginal = crearPedido(EstadoPedido.PRESUPUESTADO);
        PedidosCreateDTO createDTO = crearPedidosCreateDTO(99L, 3);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoOriginal));
        // El crear() interno falla porque el usuario 99 no existe.
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException excepcion = assertThrows(
                ResourceNotFoundException.class,
                () -> pedidoService.reCrearPedido(1L, createDTO)
        );

        assertEquals("Usuario no Valido", excepcion.getMessage());

        // El original ya fue cancelado EN MEMORIA ANTES de que el nuevo falle.
        // Opción A (atomicidad): si el nuevo pedido falla, @Transactional revierte
        // toda la transacción → en BD el original queda PRESUPUESTADO.
        // El estado en memoria (CANCELADO) ≠ estado en BD (PRESUPUESTADO).
        // El rollback real se valida en tests de integración (FASE 3), no en unit.
        assertEquals(EstadoPedido.CANCELADO, pedidoOriginal.getEstado());
    }

    // =====================================================================
    // Helpers
    // =====================================================================

    private PedidoDetalle crearPedidoDetalle(Producto producto, int cantidad) {
        PedidoDetalle detalle = new PedidoDetalle();
        detalle.setProducto(producto);
        detalle.setCantidad(cantidad);
        return detalle;
    }

    private Producto crearProducto(Long id, String nombre, BigDecimal precio, int stock) {
        Producto producto = new Producto();
        producto.setIdProducto(id);
        producto.setNombre(nombre);
        producto.setPrecio(precio);
        producto.setStock(stock);
        return producto;
    }

    private PedidoDetallesCreateDTO crearDetalleDTO(Long productoId, int cantidad) {
        PedidoDetallesCreateDTO dto = new PedidoDetallesCreateDTO();
        dto.setProductoId(productoId);
        dto.setCantidad(cantidad);
        return dto;
    }

    private PedidosCreateDTO crearPedidosCreateDTO(Long usuarioId, int cantidadDetalles) {
        PedidosCreateDTO createDTO = new PedidosCreateDTO();
        createDTO.setUsuarioId(usuarioId);

        List<PedidoDetallesCreateDTO> detalles = new ArrayList<>();
        for (int i = 1; i <= cantidadDetalles; i++) {
            detalles.add(crearDetalleDTO((long) i, 1));
        }
        createDTO.setPedidosDetalle(detalles);
        return createDTO;
    }

    private Pedido crearPedido(EstadoPedido estado) {
        Pedido pedido = new Pedido();
        pedido.setIdPedido(1L);
        pedido.setEstado(estado);
        return pedido;
    }
}
