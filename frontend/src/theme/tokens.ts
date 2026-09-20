/**
 * Design tokens — Trilha Mais
 * Identidade visual extraída do protótipo (Figma) do app.
 */

export const colors = {
  // Marca
  primary: '#2F4CD9',
  primaryDark: '#1E2F8F',
  primaryLight: '#5C74E6',
  accent: '#F2790C',
  accentDark: '#D9660A',

  // Status
  success: '#1CA967',
  successBg: '#E3F7EC',
  progress: '#2F4CD9',
  progressBg: '#D9DFFB',
  pending: '#9AA3B2',
  pendingBg: '#EEF0F4',

  // Neutros
  background: '#FFFFFF',
  surface: '#F5F6F9',
  border: '#E4E7EC',
  textPrimary: '#151A2D',
  textSecondary: '#6B7280',
  textOnPrimary: '#FFFFFF',
  placeholder: '#9CA3AF',
} as const;

export const spacing = {
  xs: 4,
  sm: 8,
  md: 16,
  lg: 24,
  xl: 32,
} as const;

export const radius = {
  sm: 8,
  md: 14,
  lg: 20,
  pill: 999,
} as const;

export const typography = {
  fontFamily:
    "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif",
  h1: { fontSize: 22, fontWeight: 700 },
  h2: { fontSize: 18, fontWeight: 700 },
  body: { fontSize: 14, fontWeight: 400 },
  bodyBold: { fontSize: 14, fontWeight: 600 },
  caption: { fontSize: 12, fontWeight: 500 },
} as const;
