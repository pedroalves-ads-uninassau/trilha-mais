import React from 'react';
import {
  SafeAreaView,
  ScrollView,
  StatusBar,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from 'react-native';
import { useNavigation } from '@react-navigation/native';

import BarraNavegacao, { ChaveAba } from '../components/BarraNavegacao';
import { mapaCoresMaterias, ChaveCorMateria } from '../theme/CoresMaterias';
import { colors, radius, spacing, typography } from '../theme/tokens';

interface Compromisso {
  id: string;
  titulo: string;
  materia: string;
  cor: ChaveCorMateria;
  icone: string;
  horario: string;
}

const gruposAgenda: { diasAdiante: number; compromissos: Compromisso[] }[] = [
  {
    diasAdiante: 0,
    compromissos: [
      { id: 'variacoes', titulo: 'Variações linguísticas', materia: 'Português', cor: 'portugues', icone: '📔', horario: '14:00' },
      { id: 'funcao', titulo: 'Função quadrática', materia: 'Matemática', cor: 'matematica', icone: '📐', horario: '16:00' },
    ],
  },
  {
    diasAdiante: 1,
    compromissos: [
      { id: 'revolucao', titulo: 'Revolução Industrial', materia: 'História', cor: 'historia', icone: '📖', horario: '10:00' },
      { id: 'zoologia', titulo: 'Zoologia', materia: 'Biologia', cor: 'biologia', icone: '🔬', horario: '15:00' },
    ],
  },
  {
    diasAdiante: 2,
    compromissos: [
      { id: 'classes', titulo: 'Classes gramaticais', materia: 'Português', cor: 'portugues', icone: '📔', horario: '09:00' },
      { id: 'probabilidade', titulo: 'Probabilidade', materia: 'Matemática', cor: 'matematica', icone: '📐', horario: '14:00' },
      { id: 'guerra', titulo: 'Primeira Guerra Mundial', materia: 'História', cor: 'historia', icone: '📖', horario: '17:00' },
    ],
  },
];

function formatarData(diasAdiante: number) {
  const data = new Date();
  data.setHours(12, 0, 0, 0);
  data.setDate(data.getDate() + diasAdiante);

  const diaSemana = new Intl.DateTimeFormat('pt-BR', { weekday: 'long' }).format(data);
  const diaMes = new Intl.DateTimeFormat('pt-BR', { day: 'numeric', month: 'short' })
    .format(data)
    .replace('.', '');

  return `${diaSemana.charAt(0).toUpperCase()}${diaSemana.slice(1)}, ${diaMes}`;
}

export default function Agenda() {
  const navigation = useNavigation<any>();

  const aoMudarAba = (novaAba: ChaveAba) => {
    if (novaAba === 'inicio') navigation.navigate('Home');
    if (novaAba === 'materias') navigation.navigate('Materias');
    if (novaAba === 'progresso') navigation.navigate('Progresso');
    if (novaAba === 'perfil') navigation.navigate('Perfil');
  };

  return (
    <SafeAreaView style={styles.areaSegura}>
      <StatusBar barStyle="light-content" backgroundColor={colors.primary} />

      <View style={styles.cabecalho}>
        <Text style={styles.titulo}>Minha Agenda</Text>
        <Text style={styles.subtitulo}>Organize sua rotina de estudos</Text>
      </View>

      <ScrollView
        style={styles.scroll}
        contentContainerStyle={styles.conteudoScroll}
        showsVerticalScrollIndicator={false}
      >
        {gruposAgenda.map((grupo) => (
          <View key={grupo.diasAdiante} style={styles.grupoDia}>
            <View style={styles.cabecalhoDia}>
              <Text style={styles.tituloDia}>
                {grupo.diasAdiante === 0
                  ? 'Hoje'
                  : grupo.diasAdiante === 1
                    ? 'Amanhã'
                    : formatarData(grupo.diasAdiante)}
              </Text>
              {grupo.diasAdiante < 2 ? (
                <Text style={styles.dataDia}>{formatarData(grupo.diasAdiante)}</Text>
              ) : null}
              {grupo.diasAdiante === 0 ? (
                <View style={styles.seloHoje}>
                  <Text style={styles.textoSeloHoje}>HOJE</Text>
                </View>
              ) : null}
            </View>

            {grupo.compromissos.map((compromisso) => {
              const corMateria = mapaCoresMaterias[compromisso.cor];
              return (
                <TouchableOpacity
                  key={compromisso.id}
                  style={[styles.cardCompromisso, { borderLeftColor: corMateria.principal }]}
                  activeOpacity={0.8}
                >
                  <View style={[styles.iconeMateria, { backgroundColor: corMateria.suave }]}>
                    <Text style={styles.emojiMateria}>{compromisso.icone}</Text>
                  </View>
                  <View style={styles.infoCompromisso}>
                    <Text style={styles.tituloCompromisso} numberOfLines={2}>
                      {compromisso.titulo}
                    </Text>
                    <Text style={styles.nomeMateria}>{compromisso.materia}</Text>
                  </View>
                  <View style={[styles.seloHorario, { backgroundColor: corMateria.suave }]}>
                    <Text style={[styles.textoHorario, { color: corMateria.principal }]}>
                      {compromisso.horario}
                    </Text>
                  </View>
                </TouchableOpacity>
              );
            })}
          </View>
        ))}
      </ScrollView>

      <BarraNavegacao ativa="agenda" aoMudar={aoMudarAba} />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  areaSegura: {
    flex: 1,
    backgroundColor: colors.surface,
  },
  cabecalho: {
    backgroundColor: colors.primary,
    paddingHorizontal: spacing.lg,
    paddingTop: spacing.xl,
    paddingBottom: spacing.xl,
    borderBottomLeftRadius: radius.lg,
    borderBottomRightRadius: radius.lg,
  },
  titulo: {
    color: colors.textOnPrimary,
    fontSize: typography.h1.fontSize + 2,
    fontWeight: '800',
  },
  subtitulo: {
    color: 'rgba(255,255,255,0.84)',
    fontSize: typography.caption.fontSize + 1,
    marginTop: spacing.xs,
  },
  scroll: {
    flex: 1,
  },
  conteudoScroll: {
    paddingHorizontal: spacing.lg,
    paddingTop: spacing.md,
    paddingBottom: spacing.md,
  },
  grupoDia: {
    marginBottom: spacing.md,
  },
  cabecalhoDia: {
    minHeight: 30,
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: spacing.sm,
    gap: spacing.sm,
  },
  tituloDia: {
    color: colors.textPrimary,
    fontSize: typography.bodyBold.fontSize,
    fontWeight: '700',
  },
  dataDia: {
    color: colors.textSecondary,
    fontSize: typography.caption.fontSize,
  },
  seloHoje: {
    borderWidth: 1,
    borderColor: '#F6C7A3',
    backgroundColor: '#FFF7F0',
    borderRadius: radius.pill,
    paddingHorizontal: spacing.sm,
    paddingVertical: 3,
  },
  textoSeloHoje: {
    color: colors.accentDark,
    fontSize: 9,
    fontWeight: '700',
  },
  cardCompromisso: {
    minHeight: 58,
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: colors.background,
    borderRadius: radius.md,
    borderWidth: 1,
    borderColor: colors.border,
    borderLeftWidth: 3,
    padding: spacing.sm + spacing.xs,
    marginBottom: spacing.sm,
  },
  iconeMateria: {
    width: 36,
    height: 36,
    borderRadius: radius.sm,
    alignItems: 'center',
    justifyContent: 'center',
    marginRight: spacing.md,
  },
  emojiMateria: {
    fontSize: 17,
  },
  infoCompromisso: {
    flex: 1,
    paddingRight: spacing.xs,
  },
  tituloCompromisso: {
    color: colors.textPrimary,
    fontSize: typography.body.fontSize,
    fontWeight: '600',
  },
  nomeMateria: {
    color: colors.textSecondary,
    fontSize: typography.caption.fontSize,
    marginTop: 2,
  },
  seloHorario: {
    minWidth: 44,
    alignItems: 'center',
    borderRadius: radius.pill,
    paddingHorizontal: spacing.xs + 2,
    paddingVertical: spacing.xs,
  },
  textoHorario: {
    fontSize: typography.caption.fontSize - 1,
    fontWeight: '700',
  },
});