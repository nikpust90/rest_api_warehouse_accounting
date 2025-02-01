package rest_api_warehouse_accounting.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;

@Component
public class JWTUtil {

    @Value("${jwt.secret}")
    private String secret;

    private static final String ISSUER = "Maxima School";
    private static final String SUBJECT = "JWT with user details";

    /**
     * Генерация JWT токена с username и role
     *
     * @param username имя пользователя
     * @param role роль пользователя
     * @return сгенерированный JWT токен
     */
    public String generateToken(String username, String role) {
        Date expirationDate = Date.from(ZonedDateTime.now().plusMinutes(60).toInstant()); // Время жизни токена

        return JWT.create()
                .withSubject(SUBJECT)
                .withClaim("username", username)
                .withClaim("role", role) // Роль добавляется в токен
                .withIssuedAt(new Date())
                .withIssuer(ISSUER)
                .withExpiresAt(expirationDate) // Устанавливаем время истечения
                .sign(Algorithm.HMAC256(secret)); // Используем секрет для подписи
    }

    /**
     * Извлекаем имя пользователя из токена
     *
     * @param token JWT токен
     * @return имя пользователя
     * @throws JWTVerificationException если токен неверен
     */
    public String extractUsername(String token) throws JWTVerificationException {
        return getDecodedJWT(token).getClaim("username").asString();
    }

    /**
     * Извлекаем роль из токена
     *
     * @param token JWT токен
     * @return роль пользователя
     * @throws JWTVerificationException если токен неверен
     */
    public String extractRole(String token) throws JWTVerificationException {
        return getDecodedJWT(token).getClaim("role").asString();
    }

    /**
     * Получаем расшифрованный JWT
     *
     * @param token JWT токен
     * @return DecodedJWT расшифрованный JWT
     * @throws JWTVerificationException если токен неверен
     */
    private DecodedJWT getDecodedJWT(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                .withIssuer(ISSUER)
                .withSubject(SUBJECT)
                .build();
        return verifier.verify(token); // Проверка и расшифровка токена
    }

    /**
     * Проверка валидности токена
     *
     * @param token JWT токен
     * @return имя пользователя из токена
     * @throws JWTVerificationException если токен неверен
     */
    public String validateToken(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                .withIssuer(ISSUER)
                .withSubject(SUBJECT)
                .build();

        DecodedJWT decodedJWT = verifier.verify(token); // Проверка токена
        return decodedJWT.getClaim("username").asString(); // Извлечение имени пользователя
    }

}