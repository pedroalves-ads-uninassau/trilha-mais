import React from 'react';
import { colors, spacing, radius, typography } from '../theme/tokens';
import type { Subject, SubjectStatus, Topic } from '../types';

interface SubjectScreenProps {
  subject: Subject;
  onSelectTopic: (topic: Topic) => void;
  onAddTopic: () => void;
}

const STATUS_LABEL: Record<SubjectStatus, string> = {
  concluido: 'Concluído',
  andamento: 'Em andamento',
  pendente: 'Pendente',
};

const STATUS_COLORS: Record<SubjectStatus, { bg: string; fg: string; border: string }> = {
  concluido: { bg: colors.successBg, fg: colors.success, border: colors.success },
  andamento: { bg: colors.progressBg, fg: colors.progress, border: colors.progress },
  pendente: { bg: colors.pendingBg, fg: colors.pending, border: colors.border },
};

const StatusIcon: React.FC<{ status: SubjectStatus }> = ({ status }) => {
  const c = STATUS_COLORS[status];
  const symbol = status === 'concluido' ? '✓' : status === 'andamento' ? '●' : '○';
  return (
    <span
      style={{
        width: 32,
        height: 32,
        borderRadius: radius.sm,
        border: `1.5px solid ${c.border}`,
        color: c.fg,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        fontSize: 14,
        flexShrink: 0,
      }}
    >
      {symbol}
    </span>
  );
};

export const SubjectScreen: React.FC<SubjectScreenProps> = ({ subject, onSelectTopic, onAddTopic }) => {
  return (
    <div style={styles.screen}>
      <div style={styles.header}>
        <div style={styles.subjectRow}>
          <span style={styles.subjectIcon}>{subject.icon}</span>
          <div>
            <h1 style={styles.subjectName}>{subject.name}</h1>
            <p style={styles.subjectMeta}>{subject.topicsCount} assuntos</p>
          </div>
        </div>

        <div style={styles.statsRow}>
          <StatBox value={subject.concluded} label="Concluídos" />
          <StatBox value={subject.inProgress} label="Andamento" />
          <StatBox value={subject.pending} label="Pendentes" />
        </div>

        <div style={styles.progressLabelRow}>
          <span style={styles.progressLabel}>Progresso</span>
          <span style={styles.progressLabel}>{subject.progressPercent}%</span>
        </div>
        <div style={styles.progressTrack}>
          <div style={{ ...styles.progressFill, width: `${subject.progressPercent}%` }} />
        </div>
      </div>

      <div style={styles.body}>
        <div style={styles.bodyHeader}>
          <h2 style={styles.bodyTitle}>Assuntos para estudar</h2>
          <button style={styles.addButton} onClick={onAddTopic}>
            + Novo
          </button>
        </div>

        <ul style={styles.topicList}>
          {subject.topics.map((topic) => (
            <li key={topic.id}>
              <button style={styles.topicCard} onClick={() => onSelectTopic(topic)}>
                <StatusIcon status={topic.status} />
                <div style={styles.topicTextGroup}>
                  <span style={styles.topicTitle}>{topic.title}</span>
                  <span
                    style={{
                      ...styles.topicBadge,
                      background: STATUS_COLORS[topic.status].bg,
                      color: STATUS_COLORS[topic.status].fg,
                    }}
                  >
                    {STATUS_LABEL[topic.status]}
                  </span>
                </div>
                <span style={styles.chevron}>›</span>
              </button>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
};

const StatBox: React.FC<{ value: number; label: string }> = ({ value, label }) => (
  <div style={styles.statBox}>
    <span style={styles.statValue}>{value}</span>
    <span style={styles.statLabel}>{label}</span>
  </div>
);

const styles: Record<string, React.CSSProperties> = {
  screen: { fontFamily: typography.fontFamily, background: colors.background, minHeight: '100%' },
  header: {
    background: `linear-gradient(160deg, ${colors.primary} 0%, ${colors.primaryDark} 100%)`,
    color: colors.textOnPrimary,
    padding: `${spacing.lg}px ${spacing.lg}px ${spacing.md}px`,
  },
  subjectRow: { display: 'flex', alignItems: 'center', gap: spacing.md, marginBottom: spacing.md },
  subjectIcon: {
    width: 44,
    height: 44,
    borderRadius: radius.md,
    background: 'rgba(255,255,255,0.18)',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    fontSize: 22,
  },
  subjectName: { ...typography.h1, margin: 0 },
  subjectMeta: { ...typography.body, margin: 0, opacity: 0.85 },
  statsRow: { display: 'flex', gap: spacing.sm, marginBottom: spacing.md },
  statBox: {
    flex: 1,
    background: 'rgba(255,255,255,0.14)',
    borderRadius: radius.md,
    padding: `${spacing.sm}px 0`,
    textAlign: 'center',
  },
  statValue: { display: 'block', fontSize: 20, fontWeight: 700 },
  statLabel: { ...typography.caption, opacity: 0.85 },
  progressLabelRow: { display: 'flex', justifyContent: 'space-between', marginBottom: spacing.xs },
  progressLabel: { ...typography.caption, opacity: 0.9 },
  progressTrack: { height: 6, background: 'rgba(255,255,255,0.25)', borderRadius: radius.pill },
  progressFill: { height: '100%', background: colors.textOnPrimary, borderRadius: radius.pill },
  body: { padding: spacing.lg },
  bodyHeader: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: spacing.md },
  bodyTitle: { ...typography.h2, margin: 0, color: colors.textPrimary },
  addButton: {
    background: colors.progressBg,
    color: colors.primary,
    border: 'none',
    borderRadius: radius.pill,
    padding: '6px 14px',
    fontSize: 13,
    fontWeight: 700,
    cursor: 'pointer',
  },
  topicList: { listStyle: 'none', margin: 0, padding: 0, display: 'flex', flexDirection: 'column', gap: spacing.sm },
  topicCard: {
    width: '100%',
    display: 'flex',
    alignItems: 'center',
    gap: spacing.md,
    background: colors.background,
    border: `1px solid ${colors.border}`,
    borderRadius: radius.md,
    padding: spacing.md,
    cursor: 'pointer',
    textAlign: 'left',
  },
  topicTextGroup: { display: 'flex', flexDirection: 'column', gap: 4, flex: 1 },
  topicTitle: { ...typography.bodyBold, color: colors.textPrimary },
  topicBadge: {
    alignSelf: 'flex-start',
    fontSize: 11,
    fontWeight: 700,
    padding: '2px 8px',
    borderRadius: radius.pill,
  },
  chevron: { color: colors.textSecondary, fontSize: 18 },
};

export default SubjectScreen;
