package io.github.jdbc.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record StudentRequest(
        @NotBlank @Size(max = 100) String name,
        @Min(1) @Max(120) Integer age,
        @NotBlank @Email @Size(max = 255) String email,
        @NotNull @Positive Integer rollNo
) {
}
