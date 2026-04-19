package br.com.restaurantmanagement.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Dados do endereço")
public record AddressRequest(

        @NotBlank(message = "Rua é obrigatória")
        @Schema(description = "Nome da rua", example = "Rua das Flores")
        String street,

        @NotBlank(message = "Número é obrigatório")
        @Schema(description = "Número", example = "123")
        String number,

        @NotBlank(message = "Cidade é obrigatória")
        @Schema(description = "Cidade", example = "São Paulo")
        String city,

        @NotBlank(message = "CEP é obrigatório")
        @Schema(description = "CEP", example = "01310-100")
        String zipCode
) {
}
