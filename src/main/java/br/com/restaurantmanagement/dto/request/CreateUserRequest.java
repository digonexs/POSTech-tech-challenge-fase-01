package br.com.restaurantmanagement.dto.request;

import br.com.restaurantmanagement.domain.model.enums.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Requisição para criação de usuário")
public record CreateUserRequest(

        @NotBlank(message = "Nome é obrigatório")
        @Schema(description = "Nome completo do usuário", example = "João da Silva")
        String name,

        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail inválido")
        @Schema(description = "E-mail único do usuário", example = "joao@email.com")
        String email,

        @NotBlank(message = "Login é obrigatório")
        @Schema(description = "Login de acesso", example = "joao.silva")
        String login,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
        @Schema(description = "Senha de acesso (mínimo 6 caracteres)", example = "senha123")
        String password,

        @NotNull(message = "Tipo de usuário é obrigatório")
        @Schema(description = "Tipo do usuário", example = "CLIENT",
                allowableValues = {"RESTAURANT_OWNER", "CLIENT"})
        UserType userType,

        @Valid
        @NotNull(message = "Endereço é obrigatório")
        @Schema(description = "Endereço do usuário")
        AddressRequest address
) {
}
