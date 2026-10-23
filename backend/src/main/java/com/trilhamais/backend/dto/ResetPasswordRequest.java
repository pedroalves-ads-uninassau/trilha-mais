package com.trilhamais.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO da troca de senha usando o codigo de recuperacao recebido por e-mail.
 */
public class ResetPasswordRequest {

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Formato de e-mail inválido.")
    private String email;

    @NotBlank(message = "O código é obrigatório.")
    private String code;

    @NotBlank(message = "A nova senha é obrigatória.")
    @Size(min = 6, message = "A senha deve conter no mínimo 6 caracteres.")
    private String newPassword;

    public ResetPasswordRequest() {
    }

    public ResetPasswordRequest(String email, String code, String newPassword) {
        this.email = email;
        this.code = code;
        this.newPassword = newPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
