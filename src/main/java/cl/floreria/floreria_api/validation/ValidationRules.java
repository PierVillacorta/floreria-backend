package cl.floreria.floreria_api.validation;

public final class ValidationRules {

    private ValidationRules() {
    }

    public static final String ALLOWED_EMAIL =
            "^[^@\\s]+@(gmail\\.com|duoc\\.cl|profesor\\.duoc\\.cl|floreria\\.com)$";

    public static final String ALLOWED_EMAIL_MESSAGE =
            "Solo se permiten correos @gmail.com, @duoc.cl, @profesor.duoc.cl o @floreria.com";
}