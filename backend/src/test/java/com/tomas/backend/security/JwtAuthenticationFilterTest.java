package com.tomas.backend.security;

import com.tomas.backend.entity.Usuario;
import com.tomas.backend.enums.Roles;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        // Evita que una autenticación previa contamine el contexto de otros tests
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private Usuario crearUsuario() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setNombre("Tomás");
        usuario.setEmail("tomas@mail.com");
        usuario.setPassword("hash");
        usuario.setRol(Roles.USER);
        return usuario;
    }

    @Test
    void deberiaAutenticarAlUsuarioCuandoElTokenEsValido() throws Exception {
        // Arrange
        UserDetails usuario = crearUsuario();
        when(request.getHeader("Authorization")).thenReturn("Bearer tokenValido");
        when(jwtService.extractUsername("tokenValido")).thenReturn("tomas@mail.com");
        when(userDetailsService.loadUserByUsername("tomas@mail.com")).thenReturn(usuario);
        when(jwtService.isTokenValid("tokenValido", usuario)).thenReturn(true);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertSame(usuario, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void deberiaContinuarLaCadenaCuandoNoHayHeaderAuthorization() throws Exception {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtService, never()).extractUsername(anyString());
    }

    @Test
    void deberiaContinuarLaCadenaCuandoElHeaderNoEsBearer() throws Exception {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Token 123");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtService, never()).extractUsername(anyString());
    }

    @Test
    void deberiaPropagarUsernameNotFoundCuandoElUsuarioNoExiste() throws Exception {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer tokenValido");
        when(jwtService.extractUsername("tokenValido")).thenReturn("noexiste@mail.com");
        when(userDetailsService.loadUserByUsername("noexiste@mail.com"))
                .thenThrow(new UsernameNotFoundException("Usuario no encontrado"));

        // Act & Assert
        assertThrows(
                UsernameNotFoundException.class,
                () -> jwtAuthenticationFilter.doFilterInternal(request, response, filterChain)
        );

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void deberiaNoAutenticarCuandoElTokenNoEsValido() throws Exception {
        // Arrange
        UserDetails usuario = crearUsuario();
        when(request.getHeader("Authorization")).thenReturn("Bearer tokenInvalido");
        when(jwtService.extractUsername("tokenInvalido")).thenReturn("tomas@mail.com");
        when(userDetailsService.loadUserByUsername("tomas@mail.com")).thenReturn(usuario);
        when(jwtService.isTokenValid("tokenInvalido", usuario)).thenReturn(false);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
}
