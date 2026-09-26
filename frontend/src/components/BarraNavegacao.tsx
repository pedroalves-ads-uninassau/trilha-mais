import React from 'react';
import { StyleSheet, Text, TouchableOpacity, View } from 'react-native';
import { colors, spacing, typography } from '../theme/tokens';

export type ChaveAba = 'inicio' | 'agenda' | 'materias' | 'progresso' | 'perfil';

interface ItemAba {
  chave: ChaveAba;
  rotulo: string;
  icone: string;
}

const abas: ItemAba[] = [
  { chave: 'inicio', rotulo: 'Início', icone: '⌂' },
  { chave: 'agenda', rotulo: 'Agenda', icone: '🗓' },
  { chave: 'materias', rotulo: 'Matérias', icone: '📖' },
  { chave: 'progresso', rotulo: 'Progresso', icone: '📊' },
  { chave: 'perfil', rotulo: 'Perfil', icone: '👤' },
];

interface BarraNavegacaoProps {
  ativa: ChaveAba;
  aoMudar?: (chave: ChaveAba) => void;
}

export default function BarraNavegacao({ ativa, aoMudar }: BarraNavegacaoProps) {
  return (
    <View style={styles.container}>
      {abas.map((aba) => {
        const estaAtiva = aba.chave === ativa;
        return (
          <TouchableOpacity
            key={aba.chave}
            style={styles.aba}
            onPress={() => aoMudar?.(aba.chave)}
            activeOpacity={0.7}
          >
            <Text style={[styles.icone, estaAtiva && styles.iconeAtivo]}>
              {aba.icone}
            </Text>
            <Text style={[styles.rotulo, estaAtiva && styles.rotuloAtivo]}>
              {aba.rotulo}
            </Text>
          </TouchableOpacity>
        );
      })}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    backgroundColor: colors.background,
    borderTopWidth: 1,
    borderTopColor: colors.border,
    paddingTop: spacing.sm,
    paddingBottom: spacing.lg - spacing.xs,
  },
  aba: {
    flex: 1,
    alignItems: 'center',
  },
  icone: {
    fontSize: 18,
    color: colors.placeholder,
    marginBottom: 2,
  },
  iconeAtivo: {
    color: colors.primary,
  },
  rotulo: {
    fontSize: typography.caption.fontSize - 1,
    color: colors.placeholder,
  },
  rotuloAtivo: {
    color: colors.primary,
    fontWeight: '600',
  },
});
