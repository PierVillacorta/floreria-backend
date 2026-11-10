package cl.floreria.floreria_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import cl.floreria.floreria_api.entity.Category;

public record CategoryResponse(
        @JsonProperty("category_id") Long categoryId,
        @JsonProperty("category_name") String categoryName) {

    public static CategoryResponse from(Category c) {
        return new CategoryResponse(c.getId(), c.getName());
    }
}