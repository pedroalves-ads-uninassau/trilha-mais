import React from 'react';
import { StyleSheet, Text, View } from 'react-native';
import { mapaCoresMaterias } from '../theme/CoresMaterias';
import { colors, radius, spacing, typography } from '../theme/tokens';
import { Materia } from '../types/inicio';
import BarraProgresso from './BarraProgresso';

interface CardMateriaProps {
  materia: Materia;
  icone: React.ReactNode;
}

export default function CardMateria({ materia, icone }: CardMateriaProps) {
  const corMateria = mapaCoresMaterias[materia.chaveCor];

  return (
    <View style={[styles.card, { borderLeftColor: corMateria.principal }]}>
      <View style={styles.linhaTopo}>
        <View style={styles.grupoTitulo}>
          <View
            style={[styles.wrapperIcone, { backgroundColor: corMateria.suave }]}
          >
            {icone}
          </View>
          <View>
            <Text style={styles.nome}>{materia.nome}</Text>
            <Text style={styles.quantidadeAssuntos}>
              {materia.quantidadeAssuntos} assuntos
            </Text>
          </View>
        </View>
        <Text style={[styles.porcentagem, { color: corMateria.principal }]}>
          {materia.progresso}%
        </Text>
      </View>

      <BarraProgresso progresso={materia.progresso} corPreenchimento={corMateria.principal} />

      <View style={styles.linhaRodape}>
        <Text style={styles.textoConcluidos}>
          ✓ {materia.concluidos} concluídos
        </Text>
        <Text style={styles.textoPendentes}>○ {materia.pendentes} pendentes</Text>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: colors.background,
    borderRadius: radius.md,
    padding: spacing.md,
    marginBottom: spacing.sm + spacing.xs,
    borderWidth: 1,
    borderColor: colors.border,
    borderLeftWidth: 4,
  },
  linhaTopo: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'flex-start',
    marginBottom: spacing.sm + spacing.xs,
  },
  grupoTitulo: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  wrapperIcone: {
    width: 40,
    height: 40,
    borderRadius: radius.sm,
    alignItems: 'center',
    justifyContent: 'center',
    marginRight: spacing.sm + spacing.xs,
  },
  nome: {
    fontSize: typography.bodyBold.fontSize + 1,
    fontWeight: '700',
    color: colors.textPrimary,
  },
  quantidadeAssuntos: {
    fontSize: typography.caption.fontSize,
    color: colors.textSecondary,
    marginTop: 2,
  },
  porcentagem: {
    fontSize: typography.h2.fontSize - 2,
    fontWeight: '800',
  },
  linhaRodape: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginTop: spacing.sm,
  },
  textoConcluidos: {
    fontSize: typography.caption.fontSize,
    color: colors.success,
    fontWeight: '500',
  },
  textoPendentes: {
    fontSize: typography.caption.fontSize,
    color: colors.textSecondary,
  },
});
