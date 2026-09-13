import React, { useState } from 'react';
import { colors, spacing, radius, typography } from '../theme/tokens';
import type { QuizQuestion } from '../types';

interface QuizScreenProps {
  subjectTopic: string;
  questions: QuizQuestion[];
  currentIndex: number;
  onAnswer: (questionId: string, optionId: string) => void;
  onFinish: (answers: Record<string, string>) => void;
}

export const QuizScreen: React.FC<QuizScreenProps> = ({
  subjectTopic,
  questions,
  currentIndex,
  onAnswer,
  onFinish,
}) => {
  const [answers, setAnswers] = useState<Record<string, string>>({});
  const question = questions[currentIndex];
  const isLast = currentIndex === questions.length - 1;
  const progressPercent = Math.round(((currentIndex + 1) / questions.length) * 100);

  const handleSelect = (optionId: string) => {
    setAnswers((prev) => ({ ...prev, [question.id]: optionId }));
    onAnswer(question.id, optionId);
  };

  const handleFinishClick = () => {
    if (isLast) onFinish(answers);
  };

  const selectedOptionId = answers[question.id];

  return (
    <div style={styles.screen}>
      <div style={styles.header}>
        <div style={styles.headerTop}>
          <span style={styles.eyebrow}>{subjectTopic}</span>
          <span style={styles.progressBadge}>{progressPercent}%</span>
        </div>
        <p style={styles.questionCounter}>
          Questão {currentIndex + 1} de {questions.length}
        </p>
        <div style={styles.progressTrack}>
          <div style={{ ...styles.progressFill, width: `${progressPercent}%` }} />
        </div>
      </div>

      <div style={styles.body}>
        <div style={styles.promptCard}>
          <p style={styles.promptText}>{question.prompt}</p>
        </div>

        <div style={styles.optionsList}>
          {question.options.map((option) => {
            const isSelected = selectedOptionId === option.id;
            return (
              <button
                key={option.id}
                style={{
                  ...styles.optionCard,
                  ...(isSelected ? styles.optionCardSelected : {}),
                }}
                onClick={() => handleSelect(option.id)}
              >
                <span
                  style={{
                    ...styles.optionLetter,
                    ...(isSelected ? styles.optionLetterSelected : {}),
                  }}
                >
                  {option.id}
                </span>
                <span style={styles.optionText}>{option.text}</span>
              </button>
            );
          })}
        </div>
      </div>

      <div style={styles.footer}>
        <button
          style={{ ...styles.actionButton, ...(!selectedOptionId ? styles.actionButtonDisabled : {}) }}
          disabled={!selectedOptionId}
          onClick={handleFinishClick}
        >
          {isLast ? 'Finalizar avaliação ✓' : 'Próxima questão'}
        </button>
      </div>
    </div>
  );
};

const styles: Record<string, React.CSSProperties> = {
  screen: {
    fontFamily: typography.fontFamily,
    background: colors.background,
    minHeight: '100%',
    display: 'flex',
    flexDirection: 'column',
  },
  header: { padding: `${spacing.lg}px ${spacing.lg}px ${spacing.md}px` },
  headerTop: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: spacing.xs },
  eyebrow: { ...typography.caption, color: colors.textSecondary, letterSpacing: 0.4 },
  progressBadge: {
    ...typography.caption,
    background: colors.progressBg,
    color: colors.primary,
    padding: '2px 10px',
    borderRadius: radius.pill,
    fontWeight: 700,
  },
  questionCounter: { ...typography.h2, margin: `0 0 ${spacing.sm}px`, color: colors.textPrimary },
  progressTrack: { height: 5, background: colors.border, borderRadius: radius.pill },
  progressFill: { height: '100%', background: colors.accent, borderRadius: radius.pill },
  body: { padding: `0 ${spacing.lg}px`, flex: 1 },
  promptCard: {
    background: colors.surface,
    border: `1px solid ${colors.border}`,
    borderRadius: radius.md,
    padding: spacing.lg,
    marginBottom: spacing.md,
  },
  promptText: { ...typography.bodyBold, fontSize: 16, margin: 0, color: colors.textPrimary },
  optionsList: { display: 'flex', flexDirection: 'column', gap: spacing.sm },
  optionCard: {
    display: 'flex',
    alignItems: 'center',
    gap: spacing.md,
    width: '100%',
    background: colors.background,
    border: `1.5px solid ${colors.border}`,
    borderRadius: radius.md,
    padding: spacing.md,
    cursor: 'pointer',
    textAlign: 'left',
  },
  optionCardSelected: { borderColor: colors.primary, background: colors.progressBg },
  optionLetter: {
    width: 28,
    height: 28,
    borderRadius: radius.pill,
    background: colors.surface,
    color: colors.textSecondary,
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    fontSize: 13,
    fontWeight: 700,
    flexShrink: 0,
  },
  optionLetterSelected: { background: colors.primary, color: colors.textOnPrimary },
  optionText: { ...typography.body, color: colors.textPrimary },
  footer: { padding: spacing.lg },
  actionButton: {
    width: '100%',
    background: colors.primary,
    color: colors.textOnPrimary,
    border: 'none',
    borderRadius: radius.pill,
    padding: '14px 0',
    fontSize: 15,
    fontWeight: 700,
    cursor: 'pointer',
  },
  actionButtonDisabled: { background: colors.pending, cursor: 'not-allowed' },
};

export default QuizScreen;
