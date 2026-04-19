package br.com.restaurantmanagement.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Requisição de autenticação")
public record LoginRequest(

        @NotBlank(message = "Login é obrigatório")
        @Schema(description = "Login do usuário", example = "joao.silva")
        String login,

        @NotBlank(message = "Senha é obrigatória")
        @Schema(description = "Senha do usuário", example = "senha123")
        String password
) {
}
