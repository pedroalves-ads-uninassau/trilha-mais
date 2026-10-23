package com.trilhamais.backend.dto;

/**
 * DTO das configuracoes do Perfil (Notificacoes e Tema). Tema: light ou dark.
 */
public class UserSettingsDto {

    private Boolean notificationsEnabled;

    private String theme;

    public UserSettingsDto() {
    }

    public UserSettingsDto(Boolean notificationsEnabled, String theme) {
        this.notificationsEnabled = notificationsEnabled;
        this.theme = theme;
    }

    public Boolean getNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(Boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
}
