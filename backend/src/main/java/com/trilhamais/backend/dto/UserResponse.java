package com.trilhamais.backend.dto;

/**
 * DTO que representa o perfil do usuario com métricas de estudo para a tela de Perfil.
 * Nunca expoe senhas ou hashes para o cliente/aplicativo.
 */
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private int streakDays;
    private int evaluationsCount;
    private int subjectsCount;
    private String avatarEmoji;

    public UserResponse() {
    }

    public UserResponse(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.streakDays = 0;
        this.evaluationsCount = 0;
        this.subjectsCount = 0;
        this.avatarEmoji = "🎓";
    }

    public UserResponse(Long id, String name, String email, int streakDays, int evaluationsCount, int subjectsCount, String avatarEmoji) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.streakDays = streakDays;
        this.evaluationsCount = evaluationsCount;
        this.subjectsCount = subjectsCount;
        this.avatarEmoji = avatarEmoji != null ? avatarEmoji : "🎓";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getStreakDays() {
        return streakDays;
    }

    public void setStreakDays(int streakDays) {
        this.streakDays = streakDays;
    }

    public int getEvaluationsCount() {
        return evaluationsCount;
    }

    public void setEvaluationsCount(int evaluationsCount) {
        this.evaluationsCount = evaluationsCount;
    }

    public int getSubjectsCount() {
        return subjectsCount;
    }

    public void setSubjectsCount(int subjectsCount) {
        this.subjectsCount = subjectsCount;
    }

    public String getAvatarEmoji() {
        return avatarEmoji;
    }

    public void setAvatarEmoji(String avatarEmoji) {
        this.avatarEmoji = avatarEmoji;
    }
}
