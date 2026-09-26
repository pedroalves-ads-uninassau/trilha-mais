import React from 'react';
import {
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from 'react-native';
import { mapaCoresMaterias } from '../theme/CoresMaterias';
import { colors, radius, spacing, typography } from '../theme/tokens';
import { ContinuarEstudando } from '../types/inicio';

const accentSuave = '#FDEEE1';

interface BannerContinuarProps {
  dados: ContinuarEstudando;
  aoPressionar?: () => void;
}

export default function BannerContinuar({ dados, aoPressionar }: BannerContinuarProps) {
  const corMateria = mapaCoresMaterias[dados.chaveCor];

  return (
    <View style={styles.card}>
      <View style={styles.grupoTexto}>
        <Text style={styles.rotulo}>CONTINUAR DE ONDE PAROU</Text>
        <Text style={styles.nomeAssunto}>{dados.nomeAssunto}</Text>
        <Text style={[styles.nomeMateria, { color: corMateria.principal }]}>
          📘 {dados.nomeMateria} · Em andamento
        </Text>
      </View>

      <TouchableOpacity
        style={styles.botao}
        onPress={aoPressionar}
        activeOpacity={0.8}
      >
        <Text style={styles.iconeBotao}>›</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: accentSuave,
    borderRadius: radius.md,
    padding: spacing.md,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginBottom: spacing.lg - spacing.xs,
  },
  grupoTexto: {
    flex: 1,
    marginRight: spacing.sm + spacing.xs,
  },
  rotulo: {
    fontSize: 11,
    fontWeight: '700',
    color: colors.accentDark,
    marginBottom: spacing.xs,
    letterSpacing: 0.3,
  },
  nomeAssunto: {
    fontSize: typography.bodyBold.fontSize + 1,
    fontWeight: '700',
    color: colors.textPrimary,
    marginBottom: spacing.xs,
  },
  nomeMateria: {
    fontSize: typography.caption.fontSize,
    fontWeight: '600',
  },
  botao: {
    width: 40,
    height: 40,
    borderRadius: radius.pill,
    backgroundColor: colors.accent,
    alignItems: 'center',
    justifyContent: 'center',
  },
  iconeBotao: {
    color: colors.textOnPrimary,
    fontSize: 20,
    fontWeight: '700',
  },
});
