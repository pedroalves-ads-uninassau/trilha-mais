import React from 'react';
import { StyleSheet, Text, View } from 'react-native';
import { colors, radius, spacing, typography } from '../theme/tokens';
import BarraProgresso from './BarraProgresso';

interface CardProgressoGeralProps {
  porcentagem: number;
  diasSeguidos: number;
  mensagem: string;
}

// Texto translúcido sobre o header azul (opacidade do branco, não é um
// token de cor — apenas uma variação visual do textOnPrimary).
const textoHeaderSuave = 'rgba(255,255,255,0.85)';

export default function CardProgressoGeral({
  porcentagem,
  diasSeguidos,
  mensagem,
}: CardProgressoGeralProps) {
  return (
    <View style={styles.card}>
      <View style={styles.linhaSuperior}>
        <Text style={styles.rotulo}>Progresso geral</Text>
        <View style={styles.linhaSequencia}>
          <Text style={styles.emojiSequencia}>🔥</Text>
          <Text style={styles.textoSequencia}>{diasSeguidos} dias seguidos</Text>
        </View>
      </View>

      <View style={styles.linhaMeio}>
        <Text style={styles.porcentagem}>{porcentagem}%</Text>
        <Text style={styles.mensagem}>{mensagem}</Text>
      </View>

      <BarraProgresso
        progresso={porcentagem}
        corPreenchimento={colors.accent}
        corTrilho="rgba(255,255,255,0.35)"
        altura={8}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: 'rgba(255,255,255,0.12)',
    borderRadius: radius.lg,
    padding: spacing.md + spacing.xs,
  },
  linhaSuperior: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: spacing.sm + spacing.xs,
  },
  rotulo: {
    color: textoHeaderSuave,
    fontSize: typography.body.fontSize,
    fontWeight: '500',
  },
  linhaSequencia: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  emojiSequencia: {
    fontSize: 13,
    marginRight: spacing.xs,
  },
  textoSequencia: {
    color: colors.textOnPrimary,
    fontSize: typography.caption.fontSize,
    fontWeight: '600',
  },
  linhaMeio: {
    flexDirection: 'row',
    alignItems: 'baseline',
    marginBottom: spacing.md - spacing.xs,
  },
  porcentagem: {
    color: colors.textOnPrimary,
    fontSize: 34,
    fontWeight: '800',
    marginRight: spacing.sm + spacing.xs,
  },
  mensagem: {
    color: textoHeaderSuave,
    fontSize: typography.caption.fontSize + 1,
  },
});
