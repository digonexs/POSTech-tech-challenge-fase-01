package br.com.restaurantmanagement.dto.response;

import br.com.restaurantmanagement.domain.model.enums.UserType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Dados do usuário (sem senha)")
public record UserResponse(

        @Schema(description = "ID único do usuário")
        UUID id,

        @Schema(description = "Nome completo", example = "João da Silva")
        String name,

        @Schema(description = "E-mail", example = "joao@email.com")
        String email,

        @Schema(description = "Login", example = "joao.silva")
        String login,

        @Schema(description = "Tipo de usuário", example = "CLIENT")
        UserType userType,

        @Schema(description = "Data da última alteração")
        LocalDateTime lastModifiedDate,

        @Schema(description = "Endereço")
        AddressResponse address
) {
}
