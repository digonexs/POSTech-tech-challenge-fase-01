package br.com.restaurantmanagement.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados do endereço")
public record AddressResponse(

        @Schema(description = "Rua", example = "Rua das Flores")
        String street,

        @Schema(description = "Número", example = "123")
        String number,

        @Schema(description = "Cidade", example = "São Paulo")
        String city,

        @Schema(description = "CEP", example = "01310-100")
        String zipCode
) {
}
