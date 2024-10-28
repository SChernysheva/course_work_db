package org.example.sport_section.Utils.JWT;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {
    private final byte[] SECRET_KEY = "your_256_bit_long_secret_key_here".getBytes(); // Замените 'your_secret_key' на ваш ключ

    public String generateToken(String username) {
        System.out.println("username in generate token: " + username);
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        try {
            // Создаем JWTClaimsSet
            JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder()
                    .subject(subject)
                    .issueTime(new Date())
                    .expirationTime(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)); // 10 часов

            // Добавляем дополнительную информацию из claims
            for (Map.Entry<String, Object> entry : claims.entrySet()) {
                claimsBuilder.claim(entry.getKey(), entry.getValue());
            }

            // Создаем и подписываем JWT
            JWTClaimsSet claimsSet = claimsBuilder.build();
            JWSSigner signer = new MACSigner(SECRET_KEY);

            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
            signedJWT.sign(signer);

            return signedJWT.serialize();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании токена", e);
        }
    }

    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    private JWTClaimsSet extractAllClaims(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(SECRET_KEY);
            if (signedJWT.verify(verifier)) {
                return signedJWT.getJWTClaimsSet();
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при извлечении токена", e);
        }
        return null;
    }

    private Boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpirationTime().before(new Date());
    }
}
