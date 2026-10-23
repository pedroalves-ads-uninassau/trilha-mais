package com.trilhamais.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de edicao do perfil do estudante (nome e avatar).
 */
public class UpdateProfileRequest {

    @NotBlank(message = "O nome é obrigatório.")
    private String name;

    @Size(max = 20, message = "O avatar deve ter no máximo 20 caracteres.")
    private String avatarEmoji;

    public UpdateProfileRequest() {
    }

    public UpdateProfileRequest(String name, String avatarEmoji) {
        this.name = name;
        this.avatarEmoji = avatarEmoji;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarEmoji() {
        return avatarEmoji;
    }

    public void setAvatarEmoji(String avatarEmoji) {
        this.avatarEmoji = avatarEmoji;
    }
}
