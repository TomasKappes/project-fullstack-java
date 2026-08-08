package com.tomas.backend.security;

import com.tomas.backend.entity.Usuario;
import com.tomas.backend.enums.Roles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private static final String SECRET_KEY = "myverysecuresecretkeyforjwttokens123";
    private static final long JWT_EXPIRATION = 86400000L;

    @InjectMocks
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        // Los valores @Value no se inyectan en tests unitarios sin contexto Spring.
        // Se asignan por reflexión replicando application.properties.
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET_KEY);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", JWT_EXPIRATION);
    }

    private Usuario crearUsuario(String email, String nombre) {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setPassword("hash");
        usuario.setRol(Roles.USER);
        return usuario;
    }

    @Test
    void deberiaGenerarTokenNoNuloParaUnUsuario() {
        // Arrange
        UserDetails usuario = crearUsuario("tomas@mail.com", "Tomás");
        UserDetails otroUsuario = crearUsuario("otro@mail.com", "Otro");

        // Act
        String token = jwtService.generateToken(usuario);
        String otroToken = jwtService.generateToken(otroUsuario);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertNotEquals(token, otroToken, "Tokens de usuarios distintos no deben ser iguales");
    }

    @Test
    void deberiaExtraerElUsernameCorrectoDelToken() {
        // Arrange
        UserDetails usuario = crearUsuario("tomas@mail.com", "Tomás");

        // Act
        String token = jwtService.generateToken(usuario);
        String username = jwtService.extractUsername(token);

        // Assert
        assertEquals(usuario.getUsername(), username);
    }

    @Test
    void deberiaConsiderarValidoUnTokenGeneradoParaElMismoUsuario() {
        // Arrange
        UserDetails usuario = crearUsuario("tomas@mail.com", "Tomás");

        // Act
        String token = jwtService.generateToken(usuario);

        // Assert
        assertTrue(jwtService.isTokenValid(token, usuario));
    }

    @Test
    void deberiaRechazarUnTokenConUsernameDiferente() {
        // Arrange
        UserDetails usuario = crearUsuario("tomas@mail.com", "Tomás");
        UserDetails otroUsuario = crearUsuario("otro@mail.com", "Otro");

        // Act
        String token = jwtService.generateToken(usuario);

        // Assert
        assertFalse(jwtService.isTokenValid(token, otroUsuario));
    }

    @Test
    void deberiaRechazarUnTokenExpirado() {
        // Arrange
        UserDetails usuario = crearUsuario("tomas@mail.com", "Tomás");
        // Fuerza una expiracion en el pasado: ahora - 24h
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", -JWT_EXPIRATION);

        // Act
        String tokenExpirado = jwtService.generateToken(usuario);

        // Assert
        // FIX (produccion): antes, jjwt 0.11.5 lanzaba ExpiredJwtException al parsear
        // (valida "exp" durante el parseo) y eso derivaba en un HTTP 500 en el filtro.
        // Contrato nuevo: un token expirado NO es valido (isTokenValid = false) y
        // extractUsername devuelve null, de modo que el filtro trata al usuario como
        // no autenticado (guard `username != null`) y la cadena continua sin romperse.
        assertNull(jwtService.extractUsername(tokenExpirado));
        assertFalse(jwtService.isTokenValid(tokenExpirado, usuario));
    }

    @Test
    void deberiaDevolverFalseCuandoElTokenEstaMalformado() {
        // Arrange
        UserDetails usuario = crearUsuario("tomas@mail.com", "Tomás");

        // Act & Assert
        // Un string que no es un JWT (sin estructura base64.cuerpo.firma) debe
        // rechazarse sin lanzar excepcion: isTokenValid devuelve false y el filtro
        // continúa la cadena sin autenticar al usuario.
        assertFalse(jwtService.isTokenValid("token-invalido-abc", usuario));
    }
}
