package br.com.gerenciadoremprestimos.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import br.com.gerenciadoremprestimos.model.Usuario;
import br.com.gerenciadoremprestimos.security.JwtProperties;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtProperties jwtProperties;

    public String gerarToken(Usuario usuario) {
        return JWT.create()
                .withIssuer("GerenciadorEmprestimo")
                .withSubject(usuario.getUsername())
                .withClaim("id", usuario.getId())
                .withExpiresAt(LocalDateTime.now()
                        .plusMinutes(30)
                        .toInstant(ZoneOffset.of("-03:00"))
                ).sign(Algorithm.HMAC256(jwtProperties.getSecret()));
    }


    public String getSubject(String token) {
        return JWT.require(Algorithm.HMAC256(jwtProperties.getSecret()))
                .withIssuer("GerenciadorEmprestimo")
                .build().verify(token).getSubject();

    }
}