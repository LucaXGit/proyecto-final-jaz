package com.tienda.security;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import com.google.gson.Gson;

public final class JwtUtil {

    private static final String ALGORITMO = "HmacSHA256";
    private static final String SECRET_KEY = obtenerSecret();
    private static final Gson gson = new Gson();

    private JwtUtil() {
    }

    private static String obtenerSecret() {
        String secret = System.getenv("JWT_SECRET");
        if (secret == null || secret.isBlank()) {
            return "MetroDropSuperSecretKeyForJWTAuth2026SecureString";
        }
        return secret;
    }

    public static String generarToken(long id, String correo, String rol) {
        try {
            Map<String, String> header = new HashMap<>();
            header.put("alg", "HS256");
            header.put("typ", "JWT");
            String headerJson = gson.toJson(header);
            String headerBase64 = base64UrlEncode(headerJson);

            long ahora = System.currentTimeMillis() / 1000L;
            long expiracion = ahora + (2 * 60 * 60); // 2 horas

            Map<String, Object> payload = new HashMap<>();
            payload.put("id", id);
            payload.put("correo", correo);
            payload.put("rol", rol);
            payload.put("exp", expiracion);
            payload.put("iat", ahora);
            String payloadJson = gson.toJson(payload);
            String payloadBase64 = base64UrlEncode(payloadJson);

            String datosParaFirmar = headerBase64 + "." + payloadBase64;
            String firma = calcularHmac(datosParaFirmar);

            return datosParaFirmar + "." + firma;
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el token JWT", e);
        }
    }

    public static Map<String, Object> validarYDecodificar(String token) {
        if (token == null) {
            return null;
        }
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String[] partes = token.split("\\.");
        if (partes.length != 3) {
            return null;
        }

        String headerBase64 = partes[0];
        String payloadBase64 = partes[1];
        String firmaBase64 = partes[2];

        try {
            String datosParaFirmar = headerBase64 + "." + payloadBase64;
            String firmaEsperada = calcularHmac(datosParaFirmar);

            if (!safeEquals(firmaBase64, firmaEsperada)) {
                return null;
            }

            String payloadJson = new String(base64UrlDecode(payloadBase64), StandardCharsets.UTF_8);
            @SuppressWarnings("unchecked")
            Map<String, Object> payload = gson.fromJson(payloadJson, Map.class);

            if (payload == null || !payload.containsKey("exp")) {
                return null;
            }

            double expDouble = (double) payload.get("exp");
            long expiracion = (long) expDouble;
            long ahora = System.currentTimeMillis() / 1000L;

            if (ahora > expiracion) {
                return null; // Token expirado
            }

            return payload;
        } catch (Exception e) {
            return null;
        }
    }

    private static String base64UrlEncode(String valor) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(valor.getBytes(StandardCharsets.UTF_8));
    }

    private static byte[] base64UrlDecode(String valor) {
        return Base64.getUrlDecoder().decode(valor);
    }

    private static String calcularHmac(String datos) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(ALGORITMO);
        SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), ALGORITMO);
        mac.init(keySpec);
        byte[] hmacBytes = mac.doFinal(datos.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hmacBytes);
    }

    private static boolean safeEquals(String a, String b) {
        byte[] aBytes = a.getBytes(StandardCharsets.UTF_8);
        byte[] bBytes = b.getBytes(StandardCharsets.UTF_8);
        if (aBytes.length != bBytes.length) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < aBytes.length; i++) {
            result |= aBytes[i] ^ bBytes[i];
        }
        return result == 0;
    }
}
