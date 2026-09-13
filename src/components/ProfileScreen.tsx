import React from 'react';
import { colors, spacing, radius, typography } from '../theme/tokens';
import type { UserProfile } from '../types';

interface MenuItem {
  id: string;
  icon: string;
  label: string;
  danger?: boolean;
}

const MENU_ITEMS: MenuItem[] = [
  { id: 'settings', icon: '⚙️', label: 'Configurações' },
  { id: 'notifications', icon: '🔔', label: 'Notificações' },
  { id: 'theme', icon: '🎨', label: 'Tema' },
  { id: 'help', icon: '❓', label: 'Ajuda e suporte' },
  { id: 'logout', icon: '🚪', label: 'Sair', danger: true },
];

interface ProfileScreenProps {
  profile: UserProfile;
  onSelectMenuItem: (itemId: string) => void;
  activeTab: 'inicio' | 'agenda' | 'materias' | 'progresso' | 'perfil';
  onSelectTab: (tab: ProfileScreenProps['activeTab']) => void;
}

const TABS: { id: ProfileScreenProps['activeTab']; icon: string; label: string }[] = [
  { id: 'inicio', icon: '🏠', label: 'Início' },
  { id: 'agenda', icon: '📅', label: 'Agenda' },
  { id: 'materias', icon: '📖', label: 'Matérias' },
  { id: 'progresso', icon: '📊', label: 'Progresso' },
  { id: 'perfil', icon: '👤', label: 'Perfil' },
];

export const ProfileScreen: React.FC<ProfileScreenProps> = ({
  profile,
  onSelectMenuItem,
  activeTab,
  onSelectTab,
}) => {
  return (
    <div style={styles.screen}>
      <div style={styles.header}>
        <div style={styles.avatar}>🎓</div>
        <h1 style={styles.name}>{profile.name}</h1>
        <p style={styles.email}>{profile.email}</p>
      </div>

      <div style={styles.statsRow}>
        <StatBox value={profile.studyStreak} label="Dia de estudo" emoji="🔥" />
        <StatBox value={profile.evaluationsCount} label="Avaliações" />
        <StatBox value={profile.subjectsCount} label="Matérias" />
      </div>

      <div style={styles.menuList}>
        {MENU_ITEMS.map((item) => (
          <button key={item.id} style={styles.menuItem} onClick={() => onSelectMenuItem(item.id)}>
            <span style={styles.menuIcon}>{item.icon}</span>
            <span style={{ ...styles.menuLabel, ...(item.danger ? styles.menuLabelDanger : {}) }}>
              {item.label}
            </span>
            <span style={styles.chevron}>›</span>
          </button>
        ))}
      </div>

      <nav style={styles.tabBar}>
        {TABS.map((tab) => {
          const isActive = tab.id === activeTab;
          return (
            <button
              key={tab.id}
              style={styles.tabButton}
              onClick={() => onSelectTab(tab.id)}
              aria-current={isActive}
            >
              <span style={{ fontSize: 18, opacity: isActive ? 1 : 0.5 }}>{tab.icon}</span>
              <span style={{ ...styles.tabLabel, color: isActive ? colors.primary : colors.textSecondary }}>
                {tab.label}
              </span>
            </button>
          );
        })}
      </nav>
    </div>
  );
};

const StatBox: React.FC<{ value: number; label: string; emoji?: string }> = ({ value, label, emoji }) => (
  <div style={styles.statBox}>
    <span style={styles.statValue}>
      {value} {emoji}
    </span>
    <span style={styles.statLabel}>{label}</span>
  </div>
);

const styles: Record<string, React.CSSProperties> = {
  screen: {
    fontFamily: typography.fontFamily,
    background: colors.background,
    minHeight: '100%',
    display: 'flex',
    flexDirection: 'column',
  },
  header: {
    background: `linear-gradient(160deg, ${colors.primary} 0%, ${colors.primaryDark} 100%)`,
    color: colors.textOnPrimary,
    textAlign: 'center',
    padding: `${spacing.xl}px ${spacing.lg}px ${spacing.lg}px`,
  },
  avatar: {
    width: 64,
    height: 64,
    borderRadius: radius.pill,
    background: 'rgba(255,255,255,0.18)',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    fontSize: 28,
    margin: '0 auto',
    marginBottom: spacing.sm,
  },
  name: { ...typography.h1, margin: 0 },
  email: { ...typography.body, margin: `${spacing.xs}px 0 0`, opacity: 0.85 },
  statsRow: {
    display: 'flex',
    justifyContent: 'space-around',
    padding: `${spacing.md}px ${spacing.lg}px`,
    borderBottom: `1px solid ${colors.border}`,
  },
  statBox: { textAlign: 'center' },
  statValue: { display: 'block', ...typography.h2, color: colors.textPrimary },
  statLabel: { ...typography.caption, color: colors.textSecondary },
  menuList: { padding: spacing.lg, display: 'flex', flexDirection: 'column', gap: spacing.sm, flex: 1 },
  menuItem: {
    display: 'flex',
    alignItems: 'center',
    gap: spacing.md,
    width: '100%',
    background: colors.background,
    border: `1px solid ${colors.border}`,
    borderRadius: radius.md,
    padding: spacing.md,
    cursor: 'pointer',
    textAlign: 'left',
  },
  menuIcon: { fontSize: 18 },
  menuLabel: { ...typography.bodyBold, flex: 1, color: colors.textPrimary },
  menuLabelDanger: { color: colors.primary },
  chevron: { color: colors.textSecondary, fontSize: 18 },
  tabBar: {
    display: 'flex',
    justifyContent: 'space-around',
    borderTop: `1px solid ${colors.border}`,
    padding: `${spacing.sm}px 0`,
  },
  tabButton: {
    background: 'none',
    border: 'none',
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    gap: 2,
    cursor: 'pointer',
  },
  tabLabel: { fontSize: 11, fontWeight: 600 },
};

export default ProfileScreen;
