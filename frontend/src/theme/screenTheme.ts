// Adaptador entre o seu design system (tokens.ts) e as 3 telas novas.
// Se algum nome de cor for diferente no seu tokens.ts, ajuste só AQUI.
import { colors as tokenColors } from './tokens';

const t = tokenColors as Record<string, string>;

export const c = {
  primary: t.primary ?? '#4F46E5',
  background: t.background ?? '#F7F7FB',
  surface: t.surface ?? '#FFFFFF',
  text: t.text ?? '#1B1B2F',
  textMuted: t.textSecondary ?? t.muted ?? '#6B6B80',
  border: t.border ?? '#E4E4EE',
  success: t.success ?? '#1F9D63',
  error: t.error ?? '#D64545',
  onPrimary: '#FFFFFF',
};

export const s = { xs: 4, sm: 8, md: 12, lg: 16, xl: 24 };
export const r = { sm: 8, md: 12, lg: 16, pill: 999 };
