package br.com.restaurantmanagement.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Requisição para atualização de dados do usuário (exceto senha)")
public record UpdateUserRequest(

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

        @Valid
        @NotNull(message = "Endereço é obrigatório")
        @Schema(description = "Endereço do usuário")
        AddressRequest address
) {
}
