package br.com.restaurantmanagement.dto.response;

import br.com.restaurantmanagement.domain.model.enums.UserType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Resposta de autenticação")
public record LoginResponse(

        @Schema(description = "ID do usuário autenticado")
        UUID userId,

        @Schema(description = "Login do usuário", example = "joao.silva")
        String login,

        @Schema(description = "Tipo de usuário", example = "CLIENT")
        UserType userType,

        @Schema(description = "Token JWT para autenticação nas demais rotas")
        String token,

        @Schema(description = "Tipo do token", example = "Bearer")
        String tokenType,

        @Schema(description = "Mensagem informativa", example = "Autenticação realizada com sucesso")
        String message
) {
}
