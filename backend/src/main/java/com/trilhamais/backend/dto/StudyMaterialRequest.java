package com.trilhamais.backend.dto;

import com.trilhamais.backend.model.MaterialType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para criacao ou edicao de um material de estudo.
 */
public class StudyMaterialRequest {

    @NotBlank(message = "O título do material é obrigatório.")
    @Size(max = 255, message = "O título deve ter no máximo 255 caracteres.")
    private String title;

    @NotBlank(message = "O conteúdo do material é obrigatório.")
    private String content;

    private MaterialType type = MaterialType.NOTES;

    public StudyMaterialRequest() {
    }

    public StudyMaterialRequest(String title, String content, MaterialType type) {
        this.title = title;
        this.content = content;
        this.type = type != null ? type : MaterialType.NOTES;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MaterialType getType() {
        return type;
    }

    public void setType(MaterialType type) {
        this.type = type;
    }
}
