package com.tomas.backend.security;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;


import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;


    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;


    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }


    // EXTRAER USERNAME


    public String extractUsername(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (JwtException | IllegalArgumentException e) {
            // FIX (produccion): en jjwt 0.11.5, parseClaimsJws() valida la expiracion
            // DURANTE el parseo y lanza ExpiredJwtException (hija de JwtException) antes
            // de devolver los claims. Si no se captura aqui, JwtAuthenticationFilter
            // propagaria la excepcion fuera de la cadena de filtros y el servidor
            // responderia 500 en lugar de tratar al usuario como no autenticado.
            // Cualquier token expirado, malformado, con firma invalida o base64 corrupto
            // se interpreta como "sin usuario": devolver null permite que el filtro caiga
            // en su guard `username != null` y continue la cadena (401/403 en endpoints
            // protegidos).
            return null;
        }
    }


    // GENERAR TOKEN


    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }


    // VALIDAR TOKEN


    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);

        // FIX (produccion): extractUsername devuelve null para tokens expirados o
        // malformados (ver comentario en extractUsername). Si no se valida antes,
        // `username.equals(...)` lanzaria NullPointerException.
        if (username == null) {
            return false;
        }

        return username.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }


    // EXTRAER CLAIM


    public <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver
    ) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }


    // TOKEN EXPIRADO
    // Nota: con jjwt 0.11.5 estos metodos quedan en gran parte inalcanzables para
    // tokens expirados (parseClaimsJws ya valida "exp" durante el parseo y lanza
    // ExpiredJwtException). Se conservan como defensa en profundidad por si el parser
    // se configurara en el futuro sin validacion de expiracion.

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    // EXTRAER TODOS LOS CLAIMS


    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ================================
    // CONSTRUIR TOKEN
    // ================================

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

}