import { colors } from './tokens';

// Cor de destaque por matéria. Português, História e Biologia usam tokens
// já existentes (primary, accent, success). Matemática usa um roxo que
// ainda não está em tokens.ts — se quiserem oficializar, basta adicionar
// algo como "purple: '#8B5CF6'" no arquivo de tokens e importar de lá.
const roxoMatematica = '#8B5CF6';
const roxoMatematicaFundo = '#F2ECFE';

export type ChaveCorMateria =
  | 'portugues'
  | 'matematica'
  | 'historia'
  | 'biologia';

export const mapaCoresMaterias: Record<
  ChaveCorMateria,
  { principal: string; suave: string }
> = {
  portugues: { principal: colors.primary, suave: colors.progressBg },
  matematica: { principal: roxoMatematica, suave: roxoMatematicaFundo },
  historia: { principal: colors.accent, suave: '#FDEEE1' },
  biologia: { principal: colors.success, suave: colors.successBg },
};
