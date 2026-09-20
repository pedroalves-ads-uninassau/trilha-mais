package com.trilhamais.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para criacao e atualizacao de uma Materia.
 */
public class SubjectRequest {

    @NotBlank(message = "O nome da matéria é obrigatório.")
    @Size(max = 255, message = "O nome da matéria deve ter no máximo 255 caracteres.")
    private String name;

    @Size(max = 1000, message = "A descrição deve ter no máximo 1000 caracteres.")
    private String description;

    public SubjectRequest() {
    }

    public SubjectRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
