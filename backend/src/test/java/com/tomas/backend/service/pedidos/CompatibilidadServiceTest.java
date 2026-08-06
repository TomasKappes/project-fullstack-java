package com.tomas.backend.service.pedidos;

import com.tomas.backend.DTOs.pedidos_detalles.PedidoDetallesCreateDTO;
import com.tomas.backend.entity.Categoria;
import com.tomas.backend.entity.Producto;
import com.tomas.backend.enums.TipoCategoria;
import com.tomas.backend.excetions.custom.BadRequestException;
import com.tomas.backend.excetions.custom.ConflictException;
import com.tomas.backend.excetions.custom.ResourceNotFoundException;
import com.tomas.backend.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompatibilidadServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private CompatibilidadService compatibilidadService;

    // ================================
    // Escenarios vía validarDetalle
    // ================================

    @Test
    void deberiaAceptarCpuAmdConMotherboardAmd() {
        // Arrange
        Producto cpu = crearProducto(1L, TipoCategoria.CPU_AMD);
        Producto motherBoard = crearProducto(2L, TipoCategoria.MOTHERBOARD_AMD);
        Producto ram = crearProducto(3L, TipoCategoria.RAM);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(cpu));
        when(productoRepository.findById(2L)).thenReturn(Optional.of(motherBoard));
        when(productoRepository.findById(3L)).thenReturn(Optional.of(ram));

        List<PedidoDetallesCreateDTO> detalles = List.of(
                crearDetalle(1L),
                crearDetalle(2L),
                crearDetalle(3L)
        );

        // Act & Assert
        assertDoesNotThrow(() -> compatibilidadService.validarDetalle(detalles));
    }

    @Test
    void deberiaAceptarCpuIntelConMotherboardIntel() {
        // Arrange
        Producto cpu = crearProducto(1L, TipoCategoria.CPU_INTEL);
        Producto motherBoard = crearProducto(2L, TipoCategoria.MOTHERBOARD_INTEL);
        Producto gpu = crearProducto(3L, TipoCategoria.GPU);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(cpu));
        when(productoRepository.findById(2L)).thenReturn(Optional.of(motherBoard));
        when(productoRepository.findById(3L)).thenReturn(Optional.of(gpu));

        List<PedidoDetallesCreateDTO> detalles = List.of(
                crearDetalle(1L),
                crearDetalle(2L),
                crearDetalle(3L)
        );

        // Act & Assert
        assertDoesNotThrow(() -> compatibilidadService.validarDetalle(detalles));
    }

    @Test
    void deberiaLanzarConflictPorCpuAmdConMotherboardIntel() {
        // Arrange
        Producto cpu = crearProducto(1L, TipoCategoria.CPU_AMD);
        Producto motherBoard = crearProducto(2L, TipoCategoria.MOTHERBOARD_INTEL);
        Producto ram = crearProducto(3L, TipoCategoria.RAM);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(cpu));
        when(productoRepository.findById(2L)).thenReturn(Optional.of(motherBoard));
        when(productoRepository.findById(3L)).thenReturn(Optional.of(ram));

        List<PedidoDetallesCreateDTO> detalles = List.of(
                crearDetalle(1L),
                crearDetalle(2L),
                crearDetalle(3L)
        );

        // Act & Assert
        ConflictException excepcion = assertThrows(
                ConflictException.class,
                () -> compatibilidadService.validarDetalle(detalles)
        );

        assertEquals(
                "Error, Componentes incompatibles , cpu amd no es compatible con motherboard intel",
                excepcion.getMessage()
        );
    }

    @Test
    void deberiaLanzarConflictPorCpuIntelConMotherboardAmd() {
        // Arrange
        Producto cpu = crearProducto(1L, TipoCategoria.CPU_INTEL);
        Producto motherBoard = crearProducto(2L, TipoCategoria.MOTHERBOARD_AMD);
        Producto ram = crearProducto(3L, TipoCategoria.RAM);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(cpu));
        when(productoRepository.findById(2L)).thenReturn(Optional.of(motherBoard));
        when(productoRepository.findById(3L)).thenReturn(Optional.of(ram));

        List<PedidoDetallesCreateDTO> detalles = List.of(
                crearDetalle(1L),
                crearDetalle(2L),
                crearDetalle(3L)
        );

        // Act & Assert
        ConflictException excepcion = assertThrows(
                ConflictException.class,
                () -> compatibilidadService.validarDetalle(detalles)
        );

        assertEquals(
                "Error, Componentes incompatibles, cpu intel no es compatible con motherboard amd",
                excepcion.getMessage()
        );
    }

    @Test
    void deberiaLanzarBadRequestCuandoFaltaCpu() {
        // Arrange
        Producto motherBoard = crearProducto(2L, TipoCategoria.MOTHERBOARD_AMD);
        Producto ram = crearProducto(3L, TipoCategoria.RAM);
        Producto gpu = crearProducto(4L, TipoCategoria.GPU);

        when(productoRepository.findById(2L)).thenReturn(Optional.of(motherBoard));
        when(productoRepository.findById(3L)).thenReturn(Optional.of(ram));
        when(productoRepository.findById(4L)).thenReturn(Optional.of(gpu));

        List<PedidoDetallesCreateDTO> detalles = List.of(
                crearDetalle(2L),
                crearDetalle(3L),
                crearDetalle(4L)
        );

        // Act & Assert
        BadRequestException excepcion = assertThrows(
                BadRequestException.class,
                () -> compatibilidadService.validarDetalle(detalles)
        );

        assertEquals(
                "El pedido debe incluir si o si una motherBoard y un cpu",
                excepcion.getMessage()
        );
    }

    @Test
    void deberiaLanzarBadRequestCuandoFaltaMotherboard() {
        // Arrange
        Producto cpu = crearProducto(1L, TipoCategoria.CPU_AMD);
        Producto ram = crearProducto(3L, TipoCategoria.RAM);
        Producto gpu = crearProducto(4L, TipoCategoria.GPU);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(cpu));
        when(productoRepository.findById(3L)).thenReturn(Optional.of(ram));
        when(productoRepository.findById(4L)).thenReturn(Optional.of(gpu));

        List<PedidoDetallesCreateDTO> detalles = List.of(
                crearDetalle(1L),
                crearDetalle(3L),
                crearDetalle(4L)
        );

        // Act & Assert
        assertThrows(
                BadRequestException.class,
                () -> compatibilidadService.validarDetalle(detalles)
        );
    }

    @Test
    void deberiaLanzarConflictConDosCpus() {
        // Arrange
        Producto cpuAmd = crearProducto(1L, TipoCategoria.CPU_AMD);
        Producto cpuIntel = crearProducto(2L, TipoCategoria.CPU_INTEL);

        // NOTA: el tercer detalle nunca llega a procesarse, la excepción se lanza
        // al encontrar la segunda CPU, por lo que su findById NO debe stubbearse
        // (strict-stubbing lo marcaría como stub no usado).
        when(productoRepository.findById(1L)).thenReturn(Optional.of(cpuAmd));
        when(productoRepository.findById(2L)).thenReturn(Optional.of(cpuIntel));

        List<PedidoDetallesCreateDTO> detalles = List.of(
                crearDetalle(1L),
                crearDetalle(2L),
                crearDetalle(3L)
        );

        // Act & Assert
        ConflictException excepcion = assertThrows(
                ConflictException.class,
                () -> compatibilidadService.validarDetalle(detalles)
        );

        assertEquals("Solo puede haber un cpu por pedido", excepcion.getMessage());
    }

    @Test
    void deberiaLanzarConflictConDosMotherboards() {
        // Arrange
        Producto motherBoardAmd = crearProducto(1L, TipoCategoria.MOTHERBOARD_AMD);
        Producto motherBoardIntel = crearProducto(2L, TipoCategoria.MOTHERBOARD_INTEL);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(motherBoardAmd));
        when(productoRepository.findById(2L)).thenReturn(Optional.of(motherBoardIntel));

        List<PedidoDetallesCreateDTO> detalles = List.of(
                crearDetalle(1L),
                crearDetalle(2L),
                crearDetalle(3L)
        );

        // Act & Assert
        ConflictException excepcion = assertThrows(
                ConflictException.class,
                () -> compatibilidadService.validarDetalle(detalles)
        );

        assertEquals("Solo puede haber una motherBoard por pedido", excepcion.getMessage());
    }

    @Test
    void deberiaLanzarResourceNotFoundCuandoProductoNoExiste() {
        // Arrange
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        List<PedidoDetallesCreateDTO> detalles = List.of(crearDetalle(99L));

        // Act & Assert
        ResourceNotFoundException excepcion = assertThrows(
                ResourceNotFoundException.class,
                () -> compatibilidadService.validarDetalle(detalles)
        );

        assertEquals("Producto no encontrado", excepcion.getMessage());
    }

    // ================================
    // Escenarios directos de validarComponentes
    // ================================

    @Test
    void deberiaLanzarBadRequestSiCpuEsNull() {
        // Arrange
        Producto motherBoard = crearProducto(2L, TipoCategoria.MOTHERBOARD_AMD);

        // Act & Assert
        BadRequestException excepcion = assertThrows(
                BadRequestException.class,
                () -> compatibilidadService.validarComponentes(null, motherBoard)
        );

        assertEquals(
                "El pedido debe incluir si o si una motherBoard y un cpu",
                excepcion.getMessage()
        );
    }

    @Test
    void deberiaLanzarBadRequestSiMotherboardEsNull() {
        // Arrange
        Producto cpu = crearProducto(1L, TipoCategoria.CPU_AMD);

        // Act & Assert
        assertThrows(
                BadRequestException.class,
                () -> compatibilidadService.validarComponentes(cpu, null)
        );
    }

    @Test
    void deberiaAceptarComponentesCompatibleAmdAmd() {
        // Arrange
        Producto cpu = crearProducto(1L, TipoCategoria.CPU_AMD);
        Producto motherBoard = crearProducto(2L, TipoCategoria.MOTHERBOARD_AMD);

        // Act & Assert
        assertDoesNotThrow(() -> compatibilidadService.validarComponentes(cpu, motherBoard));
    }

    // ================================
    // Helpers
    // ================================

    private Producto crearProducto(Long id, TipoCategoria tipo) {
        Categoria categoria = new Categoria();
        categoria.setId(id);
        categoria.setNombreCategoria(tipo);

        Producto producto = new Producto("Producto " + id, categoria, new BigDecimal("100.00"), 10);
        producto.setIdProducto(id);
        return producto;
    }

    private PedidoDetallesCreateDTO crearDetalle(Long productoId) {
        PedidoDetallesCreateDTO detalle = new PedidoDetallesCreateDTO();
        detalle.setProductoId(productoId);
        detalle.setCantidad(1);
        return detalle;
    }
}
