package br.com.restaurantmanagement.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Requisição para troca de senha")
public record UpdatePasswordRequest(

        @NotBlank(message = "Senha atual é obrigatória")
        @Schema(description = "Senha atual do usuário", example = "senha123")
        String currentPassword,

        @NotBlank(message = "Nova senha é obrigatória")
        @Size(min = 6, message = "Nova senha deve ter no mínimo 6 caracteres")
        @Schema(description = "Nova senha (mínimo 6 caracteres)", example = "novaSenha456")
        String newPassword,

        @NotBlank(message = "Confirmação de senha é obrigatória")
        @Schema(description = "Confirmação da nova senha", example = "novaSenha456")
        String confirmNewPassword
) {
}
