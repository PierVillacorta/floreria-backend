package cl.floreria.floreria_api.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

import cl.floreria.floreria_api.entity.User;

public record UserResponse(
        @JsonProperty("user_id") Long userId,
        @JsonProperty("full_name") String fullName,
        String email,
        String role,
        @JsonProperty("account_status") String accountStatus,
        String address,
        String region,
        String commune) {

    public static UserResponse from(User u) {
        return new UserResponse(
                u.getId(),
                u.getFullName(),
                u.getEmail(),
                u.getUserType().getName(),
                u.getAccountStatus(),
                u.getAddress(),
                u.getRegion(),
                u.getCommune());
    }
}