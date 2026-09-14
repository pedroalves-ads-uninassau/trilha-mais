import React, { useState } from 'react';
import { colors, spacing, radius, typography } from '../theme/tokens';
import type { SignUpFormData } from '../types';

interface SignUpScreenProps {
  onSubmit: (data: SignUpFormData) => void;
  onBack?: () => void;
}

const initialState: SignUpFormData = {
  fullName: '',
  email: '',
  password: '',
  confirmPassword: '',
};

export const SignUpScreen: React.FC<SignUpScreenProps> = ({ onSubmit, onBack }) => {
  const [form, setForm] = useState<SignUpFormData>(initialState);
  const [errors, setErrors] = useState<Partial<Record<keyof SignUpFormData, string>>>({});

  const handleChange =
    (field: keyof SignUpFormData) =>
    (e: React.ChangeEvent<HTMLInputElement>) => {
      setForm((prev) => ({ ...prev, [field]: e.target.value }));
    };

  const validate = (): boolean => {
    const nextErrors: typeof errors = {};
    if (!form.fullName.trim()) nextErrors.fullName = 'Informe seu nome completo';
    if (!/^\S+@\S+\.\S+$/.test(form.email)) nextErrors.email = 'E-mail inválido';
    if (form.password.length < 6) nextErrors.password = 'Mínimo de 6 caracteres';
    if (form.confirmPassword !== form.password) nextErrors.confirmPassword = 'As senhas não coincidem';
    setErrors(nextErrors);
    return Object.keys(nextErrors).length === 0;
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (validate()) onSubmit(form);
  };

  return (
    <div style={styles.screen}>
      <div style={styles.header}>
        {onBack && (
          <button style={styles.backButton} onClick={onBack} aria-label="Voltar">
            ←
          </button>
        )}
        <h1 style={styles.title}>Criar conta</h1>
        <p style={styles.subtitle}>Comece sua jornada no Trilha+</p>
      </div>

      <form style={styles.form} onSubmit={handleSubmit} noValidate>
        <Field
          label="Nome completo"
          placeholder="Luiz Henrique"
          value={form.fullName}
          onChange={handleChange('fullName')}
          error={errors.fullName}
        />
        <Field
          label="E-mail"
          placeholder="seu@email.com"
          type="email"
          value={form.email}
          onChange={handleChange('email')}
          error={errors.email}
        />
        <Field
          label="Senha"
          placeholder="••••••••"
          type="password"
          value={form.password}
          onChange={handleChange('password')}
          error={errors.password}
        />
        <Field
          label="Confirmar senha"
          placeholder="••••••••"
          type="password"
          value={form.confirmPassword}
          onChange={handleChange('confirmPassword')}
          error={errors.confirmPassword}
        />

        <button type="submit" style={styles.submitButton}>
          Criar conta
        </button>

        <p style={styles.terms}>
          Ao criar uma conta, você concorda com nossos{' '}
          <span style={styles.link}>Termos de Uso</span>
        </p>
      </form>
    </div>
  );
};

interface FieldProps {
  label: string;
  placeholder: string;
  value: string;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  type?: string;
  error?: string;
}

const Field: React.FC<FieldProps> = ({ label, placeholder, value, onChange, type = 'text', error }) => (
  <div style={styles.fieldGroup}>
    <label style={styles.label}>{label}</label>
    <input
      style={{ ...styles.input, ...(error ? styles.inputError : {}) }}
      type={type}
      placeholder={placeholder}
      value={value}
      onChange={onChange}
    />
    {error && <span style={styles.errorText}>{error}</span>}
  </div>
);

const styles: Record<string, React.CSSProperties> = {
  screen: {
    fontFamily: typography.fontFamily,
    background: colors.background,
    minHeight: '100%',
  },
  header: {
    background: `linear-gradient(180deg, ${colors.primary} 0%, ${colors.primaryDark} 100%)`,
    padding: `${spacing.xl}px ${spacing.lg}px ${spacing.lg * 1.5}px`,
    color: colors.textOnPrimary,
  },
  backButton: {
    background: 'rgba(255,255,255,0.15)',
    border: 'none',
    borderRadius: radius.pill,
    color: colors.textOnPrimary,
    width: 32,
    height: 32,
    marginBottom: spacing.sm,
    cursor: 'pointer',
  },
  title: { ...typography.h1, margin: 0 },
  subtitle: { ...typography.body, margin: `${spacing.xs}px 0 0`, opacity: 0.9 },
  form: { padding: spacing.lg },
  fieldGroup: { marginBottom: spacing.md },
  label: { ...typography.bodyBold, display: 'block', marginBottom: spacing.xs, color: colors.textPrimary },
  input: {
    width: '100%',
    boxSizing: 'border-box',
    padding: '12px 14px',
    borderRadius: radius.sm,
    border: `1px solid ${colors.border}`,
    background: colors.surface,
    fontSize: 14,
    color: colors.textPrimary,
  },
  inputError: { borderColor: '#E0403F' },
  errorText: { color: '#E0403F', fontSize: 12, marginTop: spacing.xs, display: 'block' },
  submitButton: {
    width: '100%',
    background: colors.accent,
    color: colors.textOnPrimary,
    border: 'none',
    borderRadius: radius.pill,
    padding: '14px 0',
    fontSize: 15,
    fontWeight: 700,
    cursor: 'pointer',
    marginTop: spacing.sm,
  },
  terms: { ...typography.caption, color: colors.textSecondary, textAlign: 'center', marginTop: spacing.md },
  link: { color: colors.primary, fontWeight: 600 },
};

export default SignUpScreen;
