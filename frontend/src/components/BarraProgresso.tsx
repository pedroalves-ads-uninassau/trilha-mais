import React from 'react';
import { StyleSheet, View } from 'react-native';
import { colors, radius } from '../theme/tokens';

interface BarraProgressoProps {
  progresso: number; // 0 a 100
  corPreenchimento?: string;
  corTrilho?: string;
  altura?: number;
}

export default function BarraProgresso({
  progresso,
  corPreenchimento = colors.textOnPrimary,
  corTrilho = colors.pendingBg,
  altura = 6,
}: BarraProgressoProps) {
  const limitado = Math.max(0, Math.min(100, progresso));

  return (
    <View style={[styles.trilho, { backgroundColor: corTrilho, height: altura }]}>
      <View
        style={[
          styles.preenchimento,
          { width: `${limitado}%`, backgroundColor: corPreenchimento, height: altura },
        ]}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  trilho: {
    width: '100%',
    borderRadius: radius.pill,
    overflow: 'hidden',
  },
  preenchimento: {
    borderRadius: radius.pill,
  },
});
