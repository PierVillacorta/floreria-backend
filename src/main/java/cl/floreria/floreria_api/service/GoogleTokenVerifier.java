package cl.floreria.floreria_api.service;

import java.util.Locale;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;

import cl.floreria.floreria_api.exception.UnauthorizedException;

@Service
public class GoogleTokenVerifier {

    private static final String GOOGLE_JWKS_URI = "https://www.googleapis.com/oauth2/v3/certs";
    private static final Set<String> GOOGLE_ISSUERS =
            Set.of("accounts.google.com", "https://accounts.google.com");

    private final NimbusJwtDecoder decoder;

    public GoogleTokenVerifier(@Value("${app.google.client-id}") String clientId) {
        this.decoder = NimbusJwtDecoder.withJwkSetUri(GOOGLE_JWKS_URI).build();

        OAuth2TokenValidator<Jwt> issuer = jwt ->
                GOOGLE_ISSUERS.contains(jwt.getClaimAsString("iss"))
                        ? OAuth2TokenValidatorResult.success()
                        : OAuth2TokenValidatorResult.failure(
                                new OAuth2Error("invalid_token", "Emisor inválido", null));

        OAuth2TokenValidator<Jwt> audience = jwt ->
                jwt.getAudience() != null && jwt.getAudience().contains(clientId)
                        ? OAuth2TokenValidatorResult.success()
                        : OAuth2TokenValidatorResult.failure(
                                new OAuth2Error("invalid_token", "Audiencia inválida", null));
        decoder.setJwtValidator(
                new DelegatingOAuth2TokenValidator<>(JwtValidators.createDefault(), issuer, audience));
    }

    public GoogleIdentity verify(String credential) {
        Jwt jwt;
        try {
            jwt = decoder.decode(credential);
        } catch (JwtException e) {
            throw new UnauthorizedException("No se pudo verificar la cuenta de Google");
        }

        String email = jwt.getClaimAsString("email");
        if (email == null || !Boolean.TRUE.equals(jwt.getClaimAsBoolean("email_verified"))) {
            throw new UnauthorizedException("Tu cuenta de Google no tiene un correo verificado");
        }
        email = email.toLowerCase(Locale.ROOT);

        
        boolean authoritative = email.endsWith("@gmail.com") || jwt.getClaimAsString("hd") != null;
        if (!authoritative) {
            throw new UnauthorizedException("Ese correo no puede verificarse a través de Google");
        }

        return new GoogleIdentity(jwt.getSubject(), email, jwt.getClaimAsString("name"));
    }

    public record GoogleIdentity(String sub, String email, String name) {
    }
}