import React from 'react';
import { StyleSheet, Text, View } from 'react-native';
import { colors, radius, spacing, typography } from '../theme/tokens';

interface CardEstatisticaProps {
  icone: React.ReactNode;
  valor: number;
  rotulo: string;
}

export default function CardEstatistica({ icone, valor, rotulo }: CardEstatisticaProps) {
  return (
    <View style={styles.card}>
      <View style={styles.wrapperIcone}>{icone}</View>
      <Text style={styles.valor}>{valor}</Text>
      <Text style={styles.rotulo}>{rotulo}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    flex: 1,
    backgroundColor: colors.background,
    borderRadius: radius.md,
    paddingVertical: spacing.md,
    alignItems: 'center',
    marginHorizontal: spacing.xs,
    borderWidth: 1,
    borderColor: colors.border,
  },
  wrapperIcone: {
    width: 36,
    height: 36,
    borderRadius: radius.sm,
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: spacing.sm,
  },
  valor: {
    fontSize: typography.h2.fontSize,
    fontWeight: '800',
    color: colors.textPrimary,
  },
  rotulo: {
    fontSize: typography.caption.fontSize,
    color: colors.textSecondary,
    marginTop: 2,
  },
});
