package com.trilhamais.backend.model;

/**
 * Tipos de atividade de estudo que contam para a sequencia de dias (streak) do estudante.
 */
public enum ActivityType {
    QUIZ,      // Simulado finalizado
    TOPIC,     // Assunto marcado como em andamento ou concluido
    SCHEDULE,  // Compromisso da agenda concluido
    CHAT       // Conversa com o tutor de IA
}
