package com.tomas.backend.service.productos;

import com.tomas.backend.DTOs.productos.ProductoCreateDTO;
import com.tomas.backend.DTOs.productos.ProductoResponseDTO;
import com.tomas.backend.DTOs.productos.ProductoUpdateDTO;
import com.tomas.backend.entity.Categoria;
import com.tomas.backend.entity.Producto;
import com.tomas.backend.excetions.custom.BadRequestException;
import com.tomas.backend.excetions.custom.ConflictException;
import com.tomas.backend.excetions.custom.ResourceNotFoundException;
import com.tomas.backend.mappers.ProductoMapper;
import com.tomas.backend.repository.CategoriaRepository;
import com.tomas.backend.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private ProductoMapper productoMapper;

    @InjectMocks
    private ProductoService productoService;

    // ---------------------------------------------------------------
    // crearProducto
    // ---------------------------------------------------------------

    @Test
    void deberiaCrearProductoCuandoCategoriaExiste() {
        // Arrange
        Categoria categoria = crearCategoria(1L);

        ProductoCreateDTO createDTO = new ProductoCreateDTO();
        createDTO.setNombre("Teclado Mecanico");
        createDTO.setCategoriaId(1L);
        createDTO.setPrecio(new BigDecimal("150.00"));
        createDTO.setDescripcion("Teclado RGB");
        createDTO.setStock(10);

        Producto producto = crearProducto(null, "Teclado Mecanico", new BigDecimal("150.00"), 10, true);
        Producto productoGuardado = crearProducto(1L, "Teclado Mecanico", new BigDecimal("150.00"), 10, true);
        ProductoResponseDTO responseDTO = crearResponseDTO(1L, "Teclado Mecanico", new BigDecimal("150.00"));

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoMapper.toEntity(createDTO, categoria)).thenReturn(producto);
        when(productoRepository.save(producto)).thenReturn(productoGuardado);
        when(productoMapper.toResponseDTO(productoGuardado)).thenReturn(responseDTO);

        // Act
        ProductoResponseDTO resultado = productoService.crearProducto(createDTO);

        // Assert
        assertEquals(1L, resultado.getProductoId());
        assertEquals("Teclado Mecanico", resultado.getNombre());

        verify(productoRepository).save(producto);
    }

    @Test
    void deberiaLanzarResourceNotFoundAlCrearProductoConCategoriaInexistente() {
        // Arrange
        ProductoCreateDTO createDTO = new ProductoCreateDTO();
        createDTO.setCategoriaId(99L);

        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException excepcion = assertThrows(
                ResourceNotFoundException.class,
                () -> productoService.crearProducto(createDTO)
        );

        assertEquals("Categoria no encontrada", excepcion.getMessage());
    }

    // ---------------------------------------------------------------
    // obtenerProducto
    // ---------------------------------------------------------------

    @Test
    void deberiaObtenerProductoCuandoExisteYEstaActivo() {
        // Arrange
        Producto producto = crearProducto(1L, "RTX 4060", new BigDecimal("350.00"), 5, true);
        ProductoResponseDTO responseDTO = crearResponseDTO(1L, "RTX 4060", new BigDecimal("350.00"));

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoMapper.toResponseDTO(producto)).thenReturn(responseDTO);

        // Act
        ProductoResponseDTO resultado = productoService.obtenerProducto(1L);

        // Assert
        assertEquals(1L, resultado.getProductoId());
        assertEquals("RTX 4060", resultado.getNombre());

        verify(productoRepository).findById(1L);
        verify(productoMapper).toResponseDTO(producto);
    }

    @Test
    void deberiaLanzarConflictCuandoProductoEstaDesactivado() {
        // Arrange
        Producto producto = crearProducto(1L, "RTX 4060", new BigDecimal("350.00"), 5, false);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // Act & Assert
        ConflictException excepcion = assertThrows(
                ConflictException.class,
                () -> productoService.obtenerProducto(1L)
        );

        assertEquals("El producto se encuentra desactivado", excepcion.getMessage());
    }

    @Test
    void deberiaLanzarResourceNotFoundCuandoProductoNoExiste() {
        // Arrange
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException excepcion = assertThrows(
                ResourceNotFoundException.class,
                () -> productoService.obtenerProducto(99L)
        );

        assertEquals("Producto no encontrado", excepcion.getMessage());
    }

    // ---------------------------------------------------------------
    // listarProductos
    // ---------------------------------------------------------------

    @Test
    void deberiaListarProductosCuandoExisten() {
        // Arrange
        Producto producto1 = crearProducto(1L, "RTX 4060", new BigDecimal("350.00"), 5, true);
        Producto producto2 = crearProducto(2L, "Ryzen 5", new BigDecimal("250.00"), 8, true);

        ProductoResponseDTO dto1 = crearResponseDTO(1L, "RTX 4060", new BigDecimal("350.00"));
        ProductoResponseDTO dto2 = crearResponseDTO(2L, "Ryzen 5", new BigDecimal("250.00"));

        when(productoRepository.findAll()).thenReturn(Arrays.asList(producto1, producto2));
        when(productoMapper.toResponseDTO(producto1)).thenReturn(dto1);
        when(productoMapper.toResponseDTO(producto2)).thenReturn(dto2);

        // Act
        List<ProductoResponseDTO> resultado = productoService.listarProductos();

        // Assert
        assertEquals(2, resultado.size());
        assertEquals("RTX 4060", resultado.get(0).getNombre());
        assertEquals("Ryzen 5", resultado.get(1).getNombre());
    }

    @Test
    void deberiaRetornarListaVaciaCuandoNoHayProductos() {
        // Arrange
        when(productoRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<ProductoResponseDTO> resultado = productoService.listarProductos();

        // Assert
        assertTrue(resultado.isEmpty());
    }

    // ---------------------------------------------------------------
    // actualizarProducto
    // ---------------------------------------------------------------

    @Test
    void deberiaActualizarProductoSinCambiarCategoria() {
        // Arrange
        Producto producto = crearProducto(1L, "RTX 4060", new BigDecimal("350.00"), 5, true);
        ProductoResponseDTO responseDTO = crearResponseDTO(1L, "RTX 4060 Actualizada", new BigDecimal("400.00"));

        ProductoUpdateDTO updateDTO = new ProductoUpdateDTO();
        updateDTO.setNombre("RTX 4060 Actualizada");
        updateDTO.setPrecio(new BigDecimal("400.00"));
        updateDTO.setCategoriaId(null);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(producto)).thenReturn(producto);
        when(productoMapper.toResponseDTO(producto)).thenReturn(responseDTO);

        // Act
        ProductoResponseDTO resultado = productoService.actualizarProducto(updateDTO, 1L);

        // Assert
        assertEquals("RTX 4060 Actualizada", resultado.getNombre());

        verify(productoMapper).toUpdateEntity(updateDTO, producto, null);
        verify(productoRepository).save(producto);
    }

    @Test
    void deberiaActualizarProductoConCategoriaNuevaValida() {
        // Arrange
        Producto producto = crearProducto(1L, "RTX 4060", new BigDecimal("350.00"), 5, true);
        Categoria categoria = crearCategoria(5L);
        ProductoResponseDTO responseDTO = crearResponseDTO(1L, "RTX 4060", new BigDecimal("350.00"));

        ProductoUpdateDTO updateDTO = new ProductoUpdateDTO();
        updateDTO.setCategoriaId(5L);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(categoriaRepository.findById(5L)).thenReturn(Optional.of(categoria));
        when(productoRepository.save(producto)).thenReturn(producto);
        when(productoMapper.toResponseDTO(producto)).thenReturn(responseDTO);

        // Act
        ProductoResponseDTO resultado = productoService.actualizarProducto(updateDTO, 1L);

        // Assert
        assertEquals("RTX 4060", resultado.getNombre());

        verify(productoMapper).toUpdateEntity(updateDTO, producto, categoria);
        verify(productoRepository).save(producto);
    }

    @Test
    void deberiaLanzarResourceNotFoundAlActualizarProductoInexistente() {
        // Arrange
        ProductoUpdateDTO updateDTO = new ProductoUpdateDTO();

        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException excepcion = assertThrows(
                ResourceNotFoundException.class,
                () -> productoService.actualizarProducto(updateDTO, 99L)
        );

        assertEquals("No existe el producto con el id: 99", excepcion.getMessage());
    }

    @Test
    void deberiaLanzarResourceNotFoundAlActualizarConCategoriaInexistente() {
        // Arrange
        Producto producto = crearProducto(1L, "RTX 4060", new BigDecimal("350.00"), 5, true);

        ProductoUpdateDTO updateDTO = new ProductoUpdateDTO();
        updateDTO.setCategoriaId(99L);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException excepcion = assertThrows(
                ResourceNotFoundException.class,
                () -> productoService.actualizarProducto(updateDTO, 1L)
        );

        assertEquals("La categoria que se le dio a este producto no existe", excepcion.getMessage());
    }

    // ---------------------------------------------------------------
    // productosPorCategoria
    // ---------------------------------------------------------------

    @Test
    void deberiaListarProductosPorCategoriaCuandoExisten() {
        // Arrange
        Categoria categoria = crearCategoria(1L);

        Producto producto1 = crearProducto(1L, "RTX 4060", new BigDecimal("350.00"), 5, true);
        Producto producto2 = crearProducto(2L, "RTX 4070", new BigDecimal("600.00"), 3, true);

        ProductoResponseDTO dto1 = crearResponseDTO(1L, "RTX 4060", new BigDecimal("350.00"));
        ProductoResponseDTO dto2 = crearResponseDTO(2L, "RTX 4070", new BigDecimal("600.00"));

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoRepository.findByCategoria(categoria)).thenReturn(Arrays.asList(producto1, producto2));
        when(productoMapper.toResponseDTO(producto1)).thenReturn(dto1);
        when(productoMapper.toResponseDTO(producto2)).thenReturn(dto2);

        // Act
        List<ProductoResponseDTO> resultado = productoService.productosPorCategoria(1L);

        // Assert
        assertEquals(2, resultado.size());
        assertEquals("RTX 4060", resultado.get(0).getNombre());
        assertEquals("RTX 4070", resultado.get(1).getNombre());
    }

    @Test
    void deberiaRetornarListaVaciaPorCategoriaSinProductos() {
        // Arrange
        Categoria categoria = crearCategoria(1L);

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoRepository.findByCategoria(categoria)).thenReturn(Collections.emptyList());

        // Act
        List<ProductoResponseDTO> resultado = productoService.productosPorCategoria(1L);

        // Assert
        assertTrue(resultado.isEmpty());
    }

    @Test
    void deberiaLanzarResourceNotFoundPorCategoriaInexistente() {
        // Arrange
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException excepcion = assertThrows(
                ResourceNotFoundException.class,
                () -> productoService.productosPorCategoria(99L)
        );

        assertEquals("Categoria no encontrada", excepcion.getMessage());
    }

    // ---------------------------------------------------------------
    // desactivarProducto
    // ---------------------------------------------------------------

    @Test
    void deberiaDesactivarProductoCuandoExiste() {
        // Arrange
        Producto producto = crearProducto(1L, "RTX 4060", new BigDecimal("350.00"), 5, true);
        ProductoResponseDTO responseDTO = crearResponseDTO(1L, "RTX 4060", new BigDecimal("350.00"));

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(producto)).thenReturn(producto);
        when(productoMapper.toResponseDTO(producto)).thenReturn(responseDTO);

        // Act
        ProductoResponseDTO resultado = productoService.desactivarProducto(1L);

        // Assert
        assertEquals("RTX 4060", resultado.getNombre());

        ArgumentCaptor<Producto> captor = ArgumentCaptor.forClass(Producto.class);
        verify(productoRepository).save(captor.capture());
        assertFalse(captor.getValue().isActivo());
    }

    @Test
    void deberiaLanzarResourceNotFoundAlDesactivarProductoInexistente() {
        // Arrange
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> productoService.desactivarProducto(99L)
        );
    }

    // ---------------------------------------------------------------
    // activarProducto
    // ---------------------------------------------------------------

    @Test
    void deberiaActivarProductoCuandoExiste() {
        // Arrange
        Producto producto = crearProducto(1L, "RTX 4060", new BigDecimal("350.00"), 5, false);
        ProductoResponseDTO responseDTO = crearResponseDTO(1L, "RTX 4060", new BigDecimal("350.00"));

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(producto)).thenReturn(producto);
        when(productoMapper.toResponseDTO(producto)).thenReturn(responseDTO);

        // Act
        ProductoResponseDTO resultado = productoService.activarProducto(1L);

        // Assert
        assertEquals("RTX 4060", resultado.getNombre());

        ArgumentCaptor<Producto> captor = ArgumentCaptor.forClass(Producto.class);
        verify(productoRepository).save(captor.capture());
        assertTrue(captor.getValue().isActivo());
    }

    @Test
    void deberiaLanzarResourceNotFoundAlActivarProductoInexistente() {
        // Arrange
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> productoService.activarProducto(99L)
        );
    }

    // ---------------------------------------------------------------
    // aumentarStock
    // ---------------------------------------------------------------

    @Test
    void deberiaAumentarStockCuandoProductoActivo() {
        // Arrange
        Producto producto = crearProducto(1L, "RTX 4060", new BigDecimal("350.00"), 5, true);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // Act
        productoService.aumentarStock(1L, 3);

        // Assert
        ArgumentCaptor<Producto> captor = ArgumentCaptor.forClass(Producto.class);
        verify(productoRepository).save(captor.capture());
        assertEquals(8, captor.getValue().getStock());
    }

    @Test
    void deberiaLanzarConflictAlAumentarStockDeProductoDesactivado() {
        // Arrange
        Producto producto = crearProducto(1L, "RTX 4060", new BigDecimal("350.00"), 5, false);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // Act & Assert
        ConflictException excepcion = assertThrows(
                ConflictException.class,
                () -> productoService.aumentarStock(1L, 3)
        );

        assertEquals("El producto se encuentra desactivado", excepcion.getMessage());
    }

    @Test
    void deberiaLanzarResourceNotFoundAlAumentarStockDeProductoInexistente() {
        // Arrange
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> productoService.aumentarStock(99L, 3)
        );
    }

    @Test
    void deberiaLanzarBadRequestAlAumentarStockConValorCeroONegativo() {
        // Arrange
        Producto producto = crearProducto(1L, "RTX 4060", new BigDecimal("350.00"), 5, true);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // Act & Assert
        BadRequestException excepcion = assertThrows(
                BadRequestException.class,
                () -> productoService.aumentarStock(1L, 0)
        );

        assertEquals("Error al aumentar stock, el valor de aumento debe ser mayor que 0", excepcion.getMessage());
    }

    // ---------------------------------------------------------------
    // disminuirStock
    // ---------------------------------------------------------------

    @Test
    void deberiaDisminuirStockCuandoProductoActivoYStockSuficiente() {
        // Arrange
        Producto producto = crearProducto(1L, "RTX 4060", new BigDecimal("350.00"), 10, true);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // Act
        productoService.disminuirStock(1L, 4);

        // Assert
        ArgumentCaptor<Producto> captor = ArgumentCaptor.forClass(Producto.class);
        verify(productoRepository).save(captor.capture());
        assertEquals(6, captor.getValue().getStock());
    }

    @Test
    void deberiaLanzarConflictAlDisminuirStockDeProductoDesactivado() {
        // Arrange
        Producto producto = crearProducto(1L, "RTX 4060", new BigDecimal("350.00"), 10, false);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // Act & Assert
        ConflictException excepcion = assertThrows(
                ConflictException.class,
                () -> productoService.disminuirStock(1L, 4)
        );

        assertEquals("El producto se encuentra desactivado", excepcion.getMessage());
    }

    @Test
    void deberiaLanzarResourceNotFoundAlDisminuirStockDeProductoInexistente() {
        // Arrange
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> productoService.disminuirStock(99L, 4)
        );
    }

    @Test
    void deberiaLanzarBadRequestAlDisminuirStockConValorCeroONegativo() {
        // Arrange
        Producto producto = crearProducto(1L, "RTX 4060", new BigDecimal("350.00"), 10, true);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // Act & Assert
        BadRequestException excepcion = assertThrows(
                BadRequestException.class,
                () -> productoService.disminuirStock(1L, 0)
        );

        assertEquals("Error al resta stock, el valor de resta debe ser mayor que 0", excepcion.getMessage());
    }

    @Test
    void deberiaLanzarConflictAlDisminuirStockMayorAlStockActual() {
        // Arrange
        Producto producto = crearProducto(1L, "RTX 4060", new BigDecimal("350.00"), 5, true);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // Act & Assert
        ConflictException excepcion = assertThrows(
                ConflictException.class,
                () -> productoService.disminuirStock(1L, 10)
        );

        assertEquals("No se puede realizar la accion, Stock insuficiente", excepcion.getMessage());
    }

    // ---------------------------------------------------------------
    // estaActivo
    // ---------------------------------------------------------------

    @Test
    void deberiaRetornarTrueCuandoProductoActivo() {
        // Arrange
        Producto producto = crearProducto(1L, "RTX 4060", new BigDecimal("350.00"), 5, true);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // Act
        boolean resultado = productoService.estaActivo(1L);

        // Assert
        assertTrue(resultado);
    }

    @Test
    void deberiaRetornarFalseCuandoProductoInactivo() {
        // Arrange
        Producto producto = crearProducto(1L, "RTX 4060", new BigDecimal("350.00"), 5, false);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // Act
        boolean resultado = productoService.estaActivo(1L);

        // Assert
        assertFalse(resultado);
    }

    @Test
    void deberiaLanzarResourceNotFoundAlConsultarActivoDeProductoInexistente() {
        // Arrange
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> productoService.estaActivo(99L)
        );
    }

    // ---------------------------------------------------------------
    // actualizarPrecio
    // ---------------------------------------------------------------

    @Test
    void deberiaActualizarPrecioCuandoProductoExiste() {
        // Arrange
        Producto producto = crearProducto(1L, "RTX 4060", new BigDecimal("350.00"), 5, true);
        BigDecimal nuevoPrecio = new BigDecimal("420.00");
        ProductoResponseDTO responseDTO = crearResponseDTO(1L, "RTX 4060", new BigDecimal("420.00"));

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(producto)).thenReturn(producto);
        when(productoMapper.toResponseDTO(producto)).thenReturn(responseDTO);

        // Act
        ProductoResponseDTO resultado = productoService.actualizarPrecio(1L, nuevoPrecio);

        // Assert
        assertEquals("RTX 4060", resultado.getNombre());

        ArgumentCaptor<Producto> captor = ArgumentCaptor.forClass(Producto.class);
        verify(productoRepository).save(captor.capture());
        assertEquals(nuevoPrecio, captor.getValue().getPrecio());
    }

    @Test
    void deberiaLanzarResourceNotFoundAlActualizarPrecioDeProductoInexistente() {
        // Arrange
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException excepcion = assertThrows(
                ResourceNotFoundException.class,
                () -> productoService.actualizarPrecio(99L, new BigDecimal("420.00"))
        );

        assertEquals("Producto no encontrado", excepcion.getMessage());
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private Producto crearProducto(Long id, String nombre, BigDecimal precio, int stock, boolean activo) {
        Producto producto = new Producto();
        producto.setIdProducto(id);
        producto.setNombre(nombre);
        producto.setPrecio(precio);
        producto.setStock(stock);
        producto.setActivo(activo);
        return producto;
    }

    private Categoria crearCategoria(Long id) {
        Categoria categoria = new Categoria();
        categoria.setId(id);
        return categoria;
    }

    private ProductoResponseDTO crearResponseDTO(Long id, String nombre, BigDecimal precio) {
        ProductoResponseDTO dto = new ProductoResponseDTO();
        dto.setProductoId(id);
        dto.setNombre(nombre);
        dto.setPrecio(precio);
        return dto;
    }
}
