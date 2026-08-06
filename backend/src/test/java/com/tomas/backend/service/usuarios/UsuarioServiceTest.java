package com.tomas.backend.service.usuarios;

import com.tomas.backend.DTOs.usuarios.UsuarioCreateDTO;
import com.tomas.backend.DTOs.usuarios.UsuarioRequestDTO;
import com.tomas.backend.DTOs.usuarios.UsuarioResponseDTO;
import com.tomas.backend.DTOs.usuarios.UsuarioUpdateDTO;
import com.tomas.backend.entity.Usuario;
import com.tomas.backend.excetions.custom.ConflictException;
import com.tomas.backend.excetions.custom.InvalidCredentialsException;
import com.tomas.backend.excetions.custom.ResourceNotFoundException;
import com.tomas.backend.mappers.UsuarioMapper;
import com.tomas.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void deberiaObtenerUsuarioPorId() {

        Long id = 1L;

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(id);
        usuario.setNombre("Tomás");

        UsuarioResponseDTO responseDTO = new UsuarioResponseDTO();
        responseDTO.setId(id);
        responseDTO.setNombre("Tomás");

        when(usuarioRepository.findById(id))
                .thenReturn(Optional.of(usuario));

        when(usuarioMapper.toResponseDTO(usuario))
                .thenReturn(responseDTO);

        UsuarioResponseDTO resultado = usuarioService.obtenerUsuario(id);

        assertEquals(id, resultado.getId());
        assertEquals("Tomás", resultado.getNombre());

        verify(usuarioRepository).findById(id);

        verify(usuarioMapper).toResponseDTO(usuario);
    }

    @Test
    void deberiaLanzarExcepcionCuandoUsuarioNoExiste() {

        Long id = 10L;

        when(usuarioRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> usuarioService.obtenerUsuario(id)
        );

        verify(usuarioRepository).findById(id);
    }

    @Test
    void deberiaCrearUsuarioEncodeandoPassword() {
        // Arrange
        UsuarioCreateDTO createDTO = new UsuarioCreateDTO();
        createDTO.setEmail("tomas@mail.com");
        createDTO.setNombre("Tomás");
        createDTO.setPassword("password123");

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setEmail("tomas@mail.com");
        usuario.setNombre("Tomás");
        usuario.setPassword("password123");

        Usuario usuarioGuardado = new Usuario();
        usuarioGuardado.setIdUsuario(1L);
        usuarioGuardado.setEmail("tomas@mail.com");
        usuarioGuardado.setNombre("Tomás");
        usuarioGuardado.setPassword("passwordEncodeada");

        UsuarioResponseDTO responseDTO = new UsuarioResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setEmail("tomas@mail.com");
        responseDTO.setNombre("Tomás");

        when(usuarioMapper.toEntity(createDTO)).thenReturn(usuario);
        when(passwordEncoder.encode("password123")).thenReturn("passwordEncodeada");
        when(usuarioRepository.save(usuario)).thenReturn(usuarioGuardado);
        when(usuarioMapper.toResponseDTO(usuarioGuardado)).thenReturn(responseDTO);

        // Act
        UsuarioResponseDTO resultado = usuarioService.crearUsuario(createDTO);

        // Assert
        assertEquals("tomas@mail.com", resultado.getEmail());
        assertEquals("Tomás", resultado.getNombre());

        // El encoder recibio la password en texto plano...
        verify(passwordEncoder).encode("password123");

        // ...y el usuario persistido llevo la password ya encodeada.
        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        assertEquals("passwordEncodeada", captor.getValue().getPassword());
    }

    @Test
    void deberiaListarUsuariosCuandoExisten() {
        // Arrange
        Usuario usuario1 = new Usuario();
        usuario1.setIdUsuario(1L);
        usuario1.setNombre("Tomás");
        usuario1.setEmail("tomas@mail.com");

        Usuario usuario2 = new Usuario();
        usuario2.setIdUsuario(2L);
        usuario2.setNombre("Ana");
        usuario2.setEmail("ana@mail.com");

        UsuarioResponseDTO dto1 = new UsuarioResponseDTO();
        dto1.setId(1L);
        dto1.setNombre("Tomás");
        dto1.setEmail("tomas@mail.com");

        UsuarioResponseDTO dto2 = new UsuarioResponseDTO();
        dto2.setId(2L);
        dto2.setNombre("Ana");
        dto2.setEmail("ana@mail.com");

        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuario1, usuario2));
        when(usuarioMapper.toResponseDTO(usuario1)).thenReturn(dto1);
        when(usuarioMapper.toResponseDTO(usuario2)).thenReturn(dto2);

        // Act
        List<UsuarioResponseDTO> resultado = usuarioService.listaUsuarios();

        // Assert
        assertEquals(2, resultado.size());
        assertEquals("Tomás", resultado.get(0).getNombre());
        assertEquals("Ana", resultado.get(1).getNombre());
    }

    @Test
    void deberiaRetornarListaVaciaCuandoNoHayUsuarios() {
        // Arrange
        when(usuarioRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<UsuarioResponseDTO> resultado = usuarioService.listaUsuarios();

        // Assert
        assertTrue(resultado.isEmpty());
    }

    @Test
    void deberiaObtenerUsuarioPorEmailCuandoExiste() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setEmail("tomas@mail.com");
        usuario.setNombre("Tomás");

        UsuarioResponseDTO responseDTO = new UsuarioResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setEmail("tomas@mail.com");
        responseDTO.setNombre("Tomás");

        when(usuarioRepository.findByEmail("tomas@mail.com")).thenReturn(Optional.of(usuario));
        when(usuarioMapper.toResponseDTO(usuario)).thenReturn(responseDTO);

        // Act
        UsuarioResponseDTO resultado = usuarioService.obtenerUsuarioPorEmail("tomas@mail.com");

        // Assert
        assertEquals("tomas@mail.com", resultado.getEmail());
        assertEquals("Tomás", resultado.getNombre());

        verify(usuarioRepository).findByEmail("tomas@mail.com");
    }

    @Test
    void deberiaLanzarExcepcionCuandoEmailNoExiste() {
        // Arrange
        when(usuarioRepository.findByEmail("noexiste@mail.com")).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException excepcion = assertThrows(
                ResourceNotFoundException.class,
                () -> usuarioService.obtenerUsuarioPorEmail("noexiste@mail.com")
        );

        assertEquals("Usuario con este email no existe", excepcion.getMessage());
    }

    @Test
    void deberiaEliminarUsuarioCuandoExiste() {
        // Arrange
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        // Act
        usuarioService.eliminarUsuario(1L);

        // Assert
        verify(usuarioRepository).deleteById(1L);
    }

    @Test
    void deberiaLanzarExcepcionAlEliminarUsuarioInexistente() {
        // Arrange
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> usuarioService.eliminarUsuario(99L)
        );

        verify(usuarioRepository, never()).deleteById(99L);
    }

    @Test
    void deberiaActualizarUsuarioCorrectamente() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setEmail("viejo@mail.com");
        usuario.setNombre("Viejo");
        usuario.setPassword("passwordVieja");

        UsuarioUpdateDTO updateDTO = new UsuarioUpdateDTO();
        updateDTO.setEmail("nuevo@mail.com");
        updateDTO.setNombre("Nuevo");
        updateDTO.setPassword("passwordNueva");

        UsuarioResponseDTO responseDTO = new UsuarioResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setEmail("nuevo@mail.com");
        responseDTO.setNombre("Nuevo");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("passwordNueva")).thenReturn("passwordNuevaEncodeada");
        when(usuarioRepository.save(usuario)).thenReturn(usuario);
        when(usuarioMapper.toResponseDTO(usuario)).thenReturn(responseDTO);

        // Act
        UsuarioResponseDTO resultado = usuarioService.actualizarUsuario(1L, updateDTO);

        // Assert
        assertEquals("nuevo@mail.com", resultado.getEmail());
        assertEquals("Nuevo", resultado.getNombre());

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        assertEquals("nuevo@mail.com", captor.getValue().getEmail());
        assertEquals("Nuevo", captor.getValue().getNombre());
        assertEquals("passwordNuevaEncodeada", captor.getValue().getPassword());
    }

    @Test
    void deberiaLanzarExcepcionAlActualizarUsuarioInexistente() {
        // Arrange
        UsuarioUpdateDTO updateDTO = new UsuarioUpdateDTO();
        updateDTO.setEmail("nuevo@mail.com");
        updateDTO.setNombre("Nuevo");
        updateDTO.setPassword("passwordNueva");

        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> usuarioService.actualizarUsuario(99L, updateDTO)
        );
    }

    @Test
    void deberiaHacerLoginConCredencialesValidas() {
        // Arrange
        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO();
        requestDTO.setEmail("tomas@mail.com");
        requestDTO.setPassword("password123");

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setEmail("tomas@mail.com");
        usuario.setNombre("Tomás");
        usuario.setPassword("hash123");

        UsuarioResponseDTO responseDTO = new UsuarioResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setEmail("tomas@mail.com");
        responseDTO.setNombre("Tomás");

        when(usuarioRepository.findByEmail("tomas@mail.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("password123", "hash123")).thenReturn(true);
        when(usuarioMapper.toResponseDTO(usuario)).thenReturn(responseDTO);

        // Act
        UsuarioResponseDTO resultado = usuarioService.loginUsuario(requestDTO);

        // Assert
        assertEquals("tomas@mail.com", resultado.getEmail());

        verify(passwordEncoder).matches("password123", "hash123");
    }

    @Test
    void deberiaLanzarExcepcionAlLoginConEmailNoRegistrado() {
        // Arrange
        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO();
        requestDTO.setEmail("noexiste@mail.com");
        requestDTO.setPassword("password123");

        when(usuarioRepository.findByEmail("noexiste@mail.com")).thenReturn(Optional.empty());

        // Act & Assert
        InvalidCredentialsException excepcion = assertThrows(
                InvalidCredentialsException.class,
                () -> usuarioService.loginUsuario(requestDTO)
        );

        assertEquals("Credenciales invalidas", excepcion.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionAlLoginConPasswordIncorrecta() {
        // Arrange
        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO();
        requestDTO.setEmail("tomas@mail.com");
        requestDTO.setPassword("passwordIncorrecta");

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setEmail("tomas@mail.com");
        usuario.setNombre("Tomás");
        usuario.setPassword("hash123");

        when(usuarioRepository.findByEmail("tomas@mail.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("passwordIncorrecta", "hash123")).thenReturn(false);

        // Act & Assert
        InvalidCredentialsException excepcion = assertThrows(
                InvalidCredentialsException.class,
                () -> usuarioService.loginUsuario(requestDTO)
        );

        assertEquals("Credenciales inválidas", excepcion.getMessage());
    }

    @Test
    void deberiaRegistrarUsuarioCuandoEmailNoExiste() {
        // Arrange
        UsuarioCreateDTO createDTO = new UsuarioCreateDTO();
        createDTO.setEmail("tomas@mail.com");
        createDTO.setNombre("Tomás");
        createDTO.setPassword("password123");

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setEmail("tomas@mail.com");
        usuario.setNombre("Tomás");
        usuario.setPassword("password123");

        UsuarioResponseDTO responseDTO = new UsuarioResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setEmail("tomas@mail.com");
        responseDTO.setNombre("Tomás");

        when(usuarioRepository.findByEmail("tomas@mail.com")).thenReturn(Optional.empty());
        when(usuarioMapper.toEntity(createDTO)).thenReturn(usuario);
        when(passwordEncoder.encode("password123")).thenReturn("passwordEncodeada");
        when(usuarioRepository.save(usuario)).thenReturn(usuario);
        when(usuarioMapper.toResponseDTO(usuario)).thenReturn(responseDTO);

        // Act
        UsuarioResponseDTO resultado = usuarioService.registrarUsuario(createDTO);

        // Assert
        assertEquals("tomas@mail.com", resultado.getEmail());

        verify(passwordEncoder).encode("password123");
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deberiaLanzarConflictExceptionAlRegistrarEmailExistente() {
        // Arrange
        UsuarioCreateDTO createDTO = new UsuarioCreateDTO();
        createDTO.setEmail("tomas@mail.com");
        createDTO.setNombre("Tomás");
        createDTO.setPassword("password123");

        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setIdUsuario(1L);
        usuarioExistente.setEmail("tomas@mail.com");

        when(usuarioRepository.findByEmail("tomas@mail.com")).thenReturn(Optional.of(usuarioExistente));

        // Act & Assert
        ConflictException excepcion = assertThrows(
                ConflictException.class,
                () -> usuarioService.registrarUsuario(createDTO)
        );

        assertEquals("Este Usuario ya existe", excepcion.getMessage());

        verify(usuarioRepository, never()).save(any());
    }

}
