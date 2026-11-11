import React, { useCallback, useState } from 'react';
import {
  ActivityIndicator,
  KeyboardAvoidingView,
  Modal,
  Platform,
  SafeAreaView,
  ScrollView,
  StatusBar,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View,
} from 'react-native';
import { useFocusEffect, useNavigation } from '@react-navigation/native';

import BarraNavegacao, { ChaveAba } from '../components/BarraNavegacao';
import { schedulesApi, subjectsApi, ScheduleResponse, SubjectResponse } from '../services/api';
import { mapaCoresMaterias, normalizarChaveCor } from '../theme/CoresMaterias';
import { colors, radius, spacing, typography } from '../theme/tokens';
import { avisar, confirmar } from '../utils/alerta';

const opcoesDia = [
  { dias: 0, rotulo: 'Hoje' },
  { dias: 1, rotulo: 'Amanhã' },
  { dias: 2, rotulo: 'Em 2 dias' },
  { dias: 3, rotulo: 'Em 3 dias' },
  { dias: 7, rotulo: 'Em 1 semana' },
];

function dataAdiante(diasAdiante: number) {
  const data = new Date();
  data.setHours(12, 0, 0, 0);
  data.setDate(data.getDate() + diasAdiante);
  return data;
}

// Data no formato que a API espera (AAAA-MM-DD), no fuso do aparelho
function dataParaApi(diasAdiante: number) {
  const data = dataAdiante(diasAdiante);
  const mes = String(data.getMonth() + 1).padStart(2, '0');
  const dia = String(data.getDate()).padStart(2, '0');
  return `${data.getFullYear()}-${mes}-${dia}`;
}

function formatarData(diasAdiante: number) {
  const data = dataAdiante(diasAdiante);

  const diaSemana = new Intl.DateTimeFormat('pt-BR', { weekday: 'long' }).format(data);
  const diaMes = new Intl.DateTimeFormat('pt-BR', { day: 'numeric', month: 'short' })
    .format(data)
    .replace('.', '');

  return `${diaSemana.charAt(0).toUpperCase()}${diaSemana.slice(1)}, ${diaMes}`;
}

function tituloDoDia(diasAdiante: number) {
  if (diasAdiante === 0) return 'Hoje';
  if (diasAdiante === 1) return 'Amanhã';
  if (diasAdiante === -1) return 'Ontem';
  return formatarData(diasAdiante);
}

export default function Agenda() {
  const navigation = useNavigation<any>();
  const [compromissos, setCompromissos] = useState<ScheduleResponse[]>([]);
  const [materias, setMaterias] = useState<SubjectResponse[]>([]);
  const [carregando, setCarregando] = useState(true);

  // Formulário de novo compromisso
  const [modalVisivel, setModalVisivel] = useState(false);
  const [titulo, setTitulo] = useState('');
  const [horario, setHorario] = useState('');
  const [diasAdiante, setDiasAdiante] = useState(0);
  const [materiaId, setMateriaId] = useState<number | null>(null);
  const [erroCadastro, setErroCadastro] = useState('');
  const [salvando, setSalvando] = useState(false);

  useFocusEffect(
    useCallback(() => {
      Promise.all([schedulesApi.list(), subjectsApi.list()])
        .then(([listaAgenda, listaMaterias]) => {
          setCompromissos(listaAgenda);
          setMaterias(listaMaterias);
        })
        .catch((e) => {
          if (e?.status !== 401) avisar('Erro', e?.message ?? 'Não foi possível carregar a agenda.');
        })
        .finally(() => setCarregando(false));
    }, [])
  );

  const aoMudarAba = (novaAba: ChaveAba) => {
    if (novaAba === 'inicio') navigation.navigate('Home');
    if (novaAba === 'materias') navigation.navigate('Materias');
    if (novaAba === 'progresso') navigation.navigate('Progresso');
    if (novaAba === 'perfil') navigation.navigate('Perfil');
  };

  const fecharModal = () => {
    setModalVisivel(false);
    setTitulo('');
    setHorario('');
    setDiasAdiante(0);
    setMateriaId(null);
    setErroCadastro('');
  };

  // Aceita só números e monta HH:MM enquanto a pessoa digita
  const aoDigitarHorario = (texto: string) => {
    const numeros = texto.replace(/\D/g, '').slice(0, 4);
    setHorario(numeros.length > 2 ? `${numeros.slice(0, 2)}:${numeros.slice(2)}` : numeros);
    setErroCadastro('');
  };

  const adicionarCompromisso = async () => {
    if (salvando) return;

    const tituloLimpo = titulo.trim();
    if (!tituloLimpo) {
      setErroCadastro('Digite o que você vai estudar.');
      return;
    }
    if (horario && !/^([01]\d|2[0-3]):[0-5]\d$/.test(horario)) {
      setErroCadastro('Horário inválido. Use o formato 14:30.');
      return;
    }

    try {
      setSalvando(true);
      const criado = await schedulesApi.create({
        title: tituloLimpo,
        subjectId: materiaId ?? undefined,
        scheduledDate: dataParaApi(diasAdiante),
        scheduledTime: horario || undefined,
      });
      setCompromissos((listaAtual) => [...listaAtual, criado]);
      fecharModal();
    } catch (e: any) {
      setErroCadastro(e?.message ?? 'Não foi possível salvar o compromisso.');
    } finally {
      setSalvando(false);
    }
  };

  const alternarConcluido = async (compromisso: ScheduleResponse) => {
    try {
      const atualizado = await schedulesApi.toggle(compromisso.id);
      setCompromissos((listaAtual) =>
        listaAtual.map((item) => (item.id === atualizado.id ? atualizado : item)),
      );
    } catch (e: any) {
      if (e?.status !== 401) avisar('Erro', e?.message ?? 'Não foi possível atualizar o compromisso.');
    }
  };

  const excluirCompromisso = async (compromisso: ScheduleResponse) => {
    const confirmado = await confirmar('Excluir compromisso', `Excluir "${compromisso.title}"?`, 'Excluir');
    if (!confirmado) return;

    try {
      await schedulesApi.remove(compromisso.id);
      setCompromissos((listaAtual) => listaAtual.filter((item) => item.id !== compromisso.id));
    } catch (e: any) {
      if (e?.status !== 401) avisar('Erro', e?.message ?? 'Não foi possível excluir o compromisso.');
    }
  };

  // Agrupa por dia (atrasados primeiro) e ordena por horário dentro do dia
  const diasComCompromisso = Array.from(new Set(compromissos.map((item) => item.daysAhead))).sort(
    (x, y) => x - y,
  );
  const gruposAgenda = diasComCompromisso.map((dias) => ({
    diasAdiante: dias,
    compromissos: compromissos
      .filter((item) => item.daysAhead === dias)
      .sort((x, y) => (x.scheduledTime ?? '99:99').localeCompare(y.scheduledTime ?? '99:99')),
  }));

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
        {carregando ? (
          <ActivityIndicator color={colors.primary} style={{ marginVertical: spacing.lg }} />
        ) : null}

        {!carregando && compromissos.length === 0 ? (
          <Text style={styles.textoVazio}>
            Nenhum compromisso ainda. Toque em "Novo compromisso" para planejar seus estudos.
          </Text>
        ) : null}

        {compromissos.length > 0 ? (
          <Text style={styles.dica}>Toque para marcar como feito. Segure para excluir.</Text>
        ) : null}

        {gruposAgenda.map((grupo) => (
          <View key={grupo.diasAdiante} style={styles.grupoDia}>
            <View style={styles.cabecalhoDia}>
              <Text style={styles.tituloDia}>{tituloDoDia(grupo.diasAdiante)}</Text>
              {grupo.diasAdiante >= -1 && grupo.diasAdiante <= 1 ? (
                <Text style={styles.dataDia}>{formatarData(grupo.diasAdiante)}</Text>
              ) : null}
              {grupo.diasAdiante === 0 ? (
                <View style={styles.seloHoje}>
                  <Text style={styles.textoSeloHoje}>HOJE</Text>
                </View>
              ) : null}
              {grupo.diasAdiante < 0 ? (
                <View style={styles.seloHoje}>
                  <Text style={styles.textoSeloHoje}>ATRASADO</Text>
                </View>
              ) : null}
            </View>

            {grupo.compromissos.map((compromisso) => {
              const corMateria = mapaCoresMaterias[normalizarChaveCor(compromisso.colorKey)];
              return (
                <TouchableOpacity
                  key={compromisso.id}
                  style={[
                    styles.cardCompromisso,
                    { borderLeftColor: corMateria.principal },
                    compromisso.completed && styles.cardConcluido,
                  ]}
                  activeOpacity={0.8}
                  onPress={() => alternarConcluido(compromisso)}
                  onLongPress={() => excluirCompromisso(compromisso)}
                >
                  <View style={[styles.iconeMateria, { backgroundColor: corMateria.suave }]}>
                    <Text style={styles.emojiMateria}>
                      {compromisso.completed ? '✅' : compromisso.icon || '📌'}
                    </Text>
                  </View>
                  <View style={styles.infoCompromisso}>
                    <Text
                      style={[styles.tituloCompromisso, compromisso.completed && styles.textoConcluido]}
                      numberOfLines={2}
                    >
                      {compromisso.title}
                    </Text>
                    {compromisso.subjectName ? (
                      <Text style={styles.nomeMateria}>{compromisso.subjectName}</Text>
                    ) : null}
                  </View>
                  {compromisso.scheduledTime ? (
                    <View style={[styles.seloHorario, { backgroundColor: corMateria.suave }]}>
                      <Text style={[styles.textoHorario, { color: corMateria.principal }]}>
                        {compromisso.scheduledTime.slice(0, 5)}
                      </Text>
                    </View>
                  ) : null}
                </TouchableOpacity>
              );
            })}
          </View>
        ))}

        <TouchableOpacity
          style={styles.botaoNovo}
          activeOpacity={0.75}
          onPress={() => setModalVisivel(true)}
        >
          <Text style={styles.textoBotaoNovo}>＋  Novo compromisso</Text>
        </TouchableOpacity>
      </ScrollView>

      <BarraNavegacao ativa="agenda" aoMudar={aoMudarAba} />

      <Modal visible={modalVisivel} transparent animationType="fade" onRequestClose={fecharModal}>
        <KeyboardAvoidingView
          style={styles.fundoModal}
          behavior={Platform.OS === 'ios' ? 'padding' : undefined}
        >
          <View style={styles.conteudoModal}>
            <Text style={styles.tituloModal}>Novo compromisso</Text>

            <Text style={styles.rotuloCampo}>O que você vai estudar?</Text>
            <TextInput
              style={styles.campo}
              value={titulo}
              onChangeText={(texto) => {
                setTitulo(texto);
                setErroCadastro('');
              }}
              placeholder="Ex.: Revisar função quadrática"
              placeholderTextColor={colors.placeholder}
              maxLength={80}
              accessibilityLabel="Título do compromisso"
            />

            <Text style={styles.rotuloCampo}>Quando</Text>
            <View style={styles.linhaOpcoes}>
              {opcoesDia.map((opcao) => {
                const selecionada = opcao.dias === diasAdiante;
                return (
                  <TouchableOpacity
                    key={opcao.dias}
                    style={[styles.opcao, selecionada && styles.opcaoSelecionada]}
                    onPress={() => setDiasAdiante(opcao.dias)}
                  >
                    <Text style={[styles.textoOpcao, selecionada && styles.textoOpcaoSelecionada]}>
                      {opcao.rotulo}
                    </Text>
                  </TouchableOpacity>
                );
              })}
            </View>

            <Text style={styles.rotuloCampo}>Horário (opcional)</Text>
            <TextInput
              style={styles.campo}
              value={horario}
              onChangeText={aoDigitarHorario}
              placeholder="14:30"
              placeholderTextColor={colors.placeholder}
              keyboardType="number-pad"
              maxLength={5}
              accessibilityLabel="Horário do compromisso"
            />

            {materias.length > 0 ? (
              <>
                <Text style={styles.rotuloCampo}>Matéria (opcional)</Text>
                <View style={styles.linhaOpcoes}>
                  {materias.map((materia) => {
                    const selecionada = materia.id === materiaId;
                    return (
                      <TouchableOpacity
                        key={materia.id}
                        style={[styles.opcao, selecionada && styles.opcaoSelecionada]}
                        onPress={() => setMateriaId(selecionada ? null : materia.id)}
                      >
                        <Text style={[styles.textoOpcao, selecionada && styles.textoOpcaoSelecionada]}>
                          {materia.icon} {materia.name}
                        </Text>
                      </TouchableOpacity>
                    );
                  })}
                </View>
              </>
            ) : null}

            {erroCadastro ? <Text style={styles.textoErro}>{erroCadastro}</Text> : null}

            <View style={styles.acoesModal}>
              <TouchableOpacity style={styles.botaoCancelar} onPress={fecharModal}>
                <Text style={styles.textoCancelar}>Cancelar</Text>
              </TouchableOpacity>
              <TouchableOpacity
                style={[styles.botaoSalvar, salvando && { opacity: 0.7 }]}
                onPress={adicionarCompromisso}
                disabled={salvando}
              >
                <Text style={styles.textoSalvar}>{salvando ? 'Salvando...' : 'Adicionar'}</Text>
              </TouchableOpacity>
            </View>
          </View>
        </KeyboardAvoidingView>
      </Modal>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  textoVazio: {
    color: colors.textSecondary,
    fontSize: 14,
    textAlign: 'center',
    marginVertical: spacing.lg,
  },
  dica: {
    color: colors.textSecondary,
    fontSize: 12,
    marginBottom: spacing.sm,
  },
  cardConcluido: {
    opacity: 0.6,
  },
  textoConcluido: {
    textDecorationLine: 'line-through',
  },
  botaoNovo: {
    borderWidth: 1,
    borderStyle: 'dashed',
    borderColor: colors.primary,
    borderRadius: radius.md,
    paddingVertical: spacing.md,
    alignItems: 'center',
    marginTop: spacing.sm,
  },
  textoBotaoNovo: {
    color: colors.primary,
    fontSize: 14,
    fontWeight: '700',
  },
  fundoModal: {
    flex: 1,
    backgroundColor: 'rgba(15, 23, 42, 0.45)',
    justifyContent: 'center',
    padding: spacing.lg,
  },
  conteudoModal: {
    width: '100%',
    maxWidth: 420,
    alignSelf: 'center',
    backgroundColor: colors.background,
    borderRadius: radius.lg,
    padding: spacing.lg,
  },
  tituloModal: {
    color: colors.textPrimary,
    fontSize: typography.h2.fontSize,
    fontWeight: '700',
    marginBottom: spacing.sm,
  },
  rotuloCampo: {
    color: colors.textPrimary,
    fontSize: 13,
    fontWeight: '600',
    marginTop: spacing.md,
    marginBottom: spacing.sm,
  },
  campo: {
    borderWidth: 1,
    borderColor: colors.border,
    borderRadius: radius.sm,
    paddingHorizontal: spacing.md,
    paddingVertical: 12,
    fontSize: 14,
    color: colors.textPrimary,
    backgroundColor: colors.surface,
  },
  linhaOpcoes: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: spacing.sm,
  },
  opcao: {
    borderWidth: 1,
    borderColor: colors.border,
    borderRadius: radius.pill,
    paddingHorizontal: 12,
    paddingVertical: 7,
  },
  opcaoSelecionada: {
    backgroundColor: colors.primary,
    borderColor: colors.primary,
  },
  textoOpcao: {
    color: colors.textSecondary,
    fontSize: 13,
    fontWeight: '600',
  },
  textoOpcaoSelecionada: {
    color: colors.textOnPrimary,
  },
  textoErro: {
    color: '#DC2626',
    fontSize: 13,
    fontWeight: '600',
    marginTop: spacing.md,
  },
  acoesModal: {
    flexDirection: 'row',
    gap: spacing.sm,
    marginTop: spacing.lg,
  },
  botaoCancelar: {
    flex: 1,
    borderWidth: 1,
    borderColor: colors.border,
    borderRadius: radius.sm,
    paddingVertical: 12,
    alignItems: 'center',
  },
  textoCancelar: {
    color: colors.textSecondary,
    fontWeight: '700',
  },
  botaoSalvar: {
    flex: 1,
    backgroundColor: colors.primary,
    borderRadius: radius.sm,
    paddingVertical: 12,
    alignItems: 'center',
  },
  textoSalvar: {
    color: colors.textOnPrimary,
    fontWeight: '700',
  },
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