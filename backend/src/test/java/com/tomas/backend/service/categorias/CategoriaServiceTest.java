package com.tomas.backend.service.categorias;

import com.tomas.backend.DTOs.categoria.CategoriaCreateDTO;
import com.tomas.backend.DTOs.categoria.CategoriaRequestDTO;
import com.tomas.backend.DTOs.categoria.CategoriaResponseDTO;
import com.tomas.backend.entity.Categoria;
import com.tomas.backend.excetions.custom.BadRequestException;
import com.tomas.backend.excetions.custom.ConflictException;
import com.tomas.backend.excetions.custom.ResourceNotFoundException;
import com.tomas.backend.mappers.CategoriaMapper;
import com.tomas.backend.repository.CategoriaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private CategoriaMapper categoriaMapper;

    @InjectMocks
    private CategoriaService categoriaService;

    @Test
    void deberiaListarCategoriasCuandoExisten() {
        // Arrange
        Categoria procesadores = crearCategoria(1L, "Procesadores", true);
        Categoria graficas = crearCategoria(2L, "Placas de Video", true);

        CategoriaResponseDTO dtoProcesadores = crearResponseDTO(1L, "Procesadores");
        CategoriaResponseDTO dtoGraficas = crearResponseDTO(2L, "Placas de Video");

        when(categoriaRepository.findAll()).thenReturn(Arrays.asList(procesadores, graficas));
        when(categoriaMapper.toResponseDTO(procesadores)).thenReturn(dtoProcesadores);
        when(categoriaMapper.toResponseDTO(graficas)).thenReturn(dtoGraficas);

        // Act
        List<CategoriaResponseDTO> resultado = categoriaService.listarCategorias();

        // Assert
        assertEquals(2, resultado.size());
        assertEquals("Procesadores", resultado.get(0).getNombre());
        assertEquals("Placas de Video", resultado.get(1).getNombre());
    }

    @Test
    void deberiaRetornarListaVaciaCuandoNoHayCategorias() {
        // Arrange
        when(categoriaRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<CategoriaResponseDTO> resultado = categoriaService.listarCategorias();

        // Assert
        assertTrue(resultado.isEmpty());
    }

    @Test
    void deberiaObtenerCategoriaCuandoExisteYEstaActiva() {
        // Arrange
        Categoria categoria = crearCategoria(1L, "Procesadores", true);
        CategoriaResponseDTO responseDTO = crearResponseDTO(1L, "Procesadores");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaMapper.toResponseDTO(categoria)).thenReturn(responseDTO);

        // Act
        CategoriaResponseDTO resultado = categoriaService.obtenerCategoria(1L);

        // Assert
        assertEquals(1L, resultado.getCategoriaId());
        assertEquals("Procesadores", resultado.getNombre());

        verify(categoriaRepository).findById(1L);
        verify(categoriaMapper).toResponseDTO(categoria);
    }

    @Test
    void deberiaLanzarConflictExceptionCuandoCategoriaEstaDesactivada() {
        // Arrange
        Categoria categoria = crearCategoria(1L, "Procesadores", false);

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        // Act & Assert
        ConflictException excepcion = assertThrows(
                ConflictException.class,
                () -> categoriaService.obtenerCategoria(1L)
        );

        assertEquals("La categoria se encuentra desactivada", excepcion.getMessage());
    }

    @Test
    void deberiaLanzarResourceNotFoundExceptionCuandoCategoriaNoExiste() {
        // Arrange
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException excepcion = assertThrows(
                ResourceNotFoundException.class,
                () -> categoriaService.obtenerCategoria(99L)
        );

        assertEquals("No existe una categoria con el id: 99", excepcion.getMessage());
    }

    @Test
    void deberiaCrearCategoriaCorrectamente() {
        // Arrange
        CategoriaCreateDTO createDTO = new CategoriaCreateDTO();
        createDTO.setNombre("Procesadores");

        Categoria categoria = crearCategoria(null, "Procesadores", true);
        Categoria categoriaGuardada = crearCategoria(1L, "Procesadores", true);
        CategoriaResponseDTO responseDTO = crearResponseDTO(1L, "Procesadores");

        when(categoriaMapper.toEntity(createDTO)).thenReturn(categoria);
        when(categoriaRepository.save(categoria)).thenReturn(categoriaGuardada);
        when(categoriaMapper.toResponseDTO(categoriaGuardada)).thenReturn(responseDTO);

        // Act
        CategoriaResponseDTO resultado = categoriaService.crearCategoria(createDTO);

        // Assert
        assertEquals("Procesadores", resultado.getNombre());

        verify(categoriaRepository).save(categoria);
    }

    @Test
    void deberiaLanzarBadRequestCuandoNombreEsNuloAlCrear() {
        // Arrange
        CategoriaCreateDTO createDTO = new CategoriaCreateDTO();
        createDTO.setNombre(null);

        // Act & Assert
        BadRequestException excepcion = assertThrows(
                BadRequestException.class,
                () -> categoriaService.crearCategoria(createDTO)
        );

        assertEquals("La categoria debe tener un nombre", excepcion.getMessage());
    }

    @Test
    void deberiaActualizarCategoriaCorrectamente() {
        // Arrange
        Categoria categoria = crearCategoria(1L, "Procesadores", true);

        CategoriaRequestDTO requestDTO = new CategoriaRequestDTO();
        requestDTO.setCategoriaId(1L);
        requestDTO.setNombre("Procesadores Actualizado");

        CategoriaResponseDTO responseDTO = crearResponseDTO(1L, "Procesadores Actualizado");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.save(categoria)).thenReturn(categoria);
        when(categoriaMapper.toResponseDTO(categoria)).thenReturn(responseDTO);

        // Act
        CategoriaResponseDTO resultado = categoriaService.actualizarCategoria(requestDTO, 1L);

        // Assert
        assertEquals("Procesadores Actualizado", resultado.getNombre());

        verify(categoriaMapper).toUpdateEntity(requestDTO, categoria);
        verify(categoriaRepository).save(categoria);
    }

    @Test
    void deberiaLanzarBadRequestCuandoNombreEsNuloAlActualizar() {
        // Arrange
        Categoria categoria = crearCategoria(1L, "Procesadores", true);

        CategoriaRequestDTO requestDTO = new CategoriaRequestDTO();
        requestDTO.setCategoriaId(1L);
        requestDTO.setNombre(null);

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        // Act & Assert
        BadRequestException excepcion = assertThrows(
                BadRequestException.class,
                () -> categoriaService.actualizarCategoria(requestDTO, 1L)
        );

        assertEquals("La categoria debe tener un nombre", excepcion.getMessage());
    }

    @Test
    void deberiaDesactivarCategoriaCuandoExiste() {
        // Arrange
        Categoria categoria = crearCategoria(1L, "Procesadores", true);
        CategoriaResponseDTO responseDTO = crearResponseDTO(1L, "Procesadores");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.save(categoria)).thenReturn(categoria);
        when(categoriaMapper.toResponseDTO(categoria)).thenReturn(responseDTO);

        // Act
        CategoriaResponseDTO resultado = categoriaService.desactivarCategoria(1L);

        // Assert
        assertEquals("Procesadores", resultado.getNombre());

        ArgumentCaptor<Categoria> captor = ArgumentCaptor.forClass(Categoria.class);
        verify(categoriaRepository).save(captor.capture());
        assertFalse(captor.getValue().isActivo());
    }

    @Test
    void deberiaActivarCategoriaCuandoExiste() {
        // Arrange
        Categoria categoria = crearCategoria(1L, "Procesadores", false);
        CategoriaResponseDTO responseDTO = crearResponseDTO(1L, "Procesadores");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.save(categoria)).thenReturn(categoria);
        when(categoriaMapper.toResponseDTO(categoria)).thenReturn(responseDTO);

        // Act
        CategoriaResponseDTO resultado = categoriaService.activarCategoria(1L);

        // Assert
        assertEquals("Procesadores", resultado.getNombre());

        ArgumentCaptor<Categoria> captor = ArgumentCaptor.forClass(Categoria.class);
        verify(categoriaRepository).save(captor.capture());
        assertTrue(captor.getValue().isActivo());
    }

    @Test
    void deberiaLanzarResourceNotFoundAlDesactivarCategoriaInexistente() {
        // Arrange
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> categoriaService.desactivarCategoria(99L)
        );
    }

    @Test
    void deberiaLanzarResourceNotFoundAlActivarCategoriaInexistente() {
        // Arrange
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> categoriaService.activarCategoria(99L)
        );
    }

    private Categoria crearCategoria(Long id, String nombre, Boolean activo) {
        Categoria categoria = new Categoria();
        categoria.setId(id);
        categoria.setNombre(nombre);
        categoria.setActivo(activo);
        return categoria;
    }

    private CategoriaResponseDTO crearResponseDTO(Long id, String nombre) {
        CategoriaResponseDTO dto = new CategoriaResponseDTO();
        dto.setCategoriaId(id);
        dto.setNombre(nombre);
        return dto;
    }
}
