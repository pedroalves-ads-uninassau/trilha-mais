import React, { useCallback, useState } from 'react';
import {
  ActivityIndicator,
  SafeAreaView,
  ScrollView,
  StatusBar,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from 'react-native';
import { useFocusEffect, useNavigation } from '@react-navigation/native';

import BannerContinuar from '../components/BannerContinuar';
import BarraNavegacao, { ChaveAba } from '../components/BarraNavegacao';
import CardEstatistica from '../components/CardEstatistica';
import CardMateria from '../components/CardMateria';
import CardProgressoGeral from '../components/CardProgressoGeral';

import { dashboardApi, DashboardResponse, paraMateriaTela } from '../services/api';
import { normalizarChaveCor } from '../theme/CoresMaterias';
import { colors, radius, spacing, typography } from '../theme/tokens';

const textoHeaderSuave = 'rgba(255,255,255,0.85)';

export default function Home() {
  const navigation = useNavigation<any>();
  const [dados, setDados] = useState<DashboardResponse | null>(null);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState('');

  const carregar = useCallback(() => {
    dashboardApi
      .get()
      .then((resposta) => {
        setDados(resposta);
        setErro('');
      })
      .catch((e) => {
        // 401 já leva de volta ao login; aqui ficam os outros erros
        if (e?.status !== 401) setErro(e?.message ?? 'Não foi possível carregar seus dados.');
      })
      .finally(() => setCarregando(false));
  }, []);

  // Recarrega sempre que a tela volta a aparecer (após criar matéria, fazer quiz etc.)
  useFocusEffect(carregar);

  // Função para a mudança na Barra de Navegação
  const aoMudarAba = (novaAba: ChaveAba) => {
    if (novaAba === 'agenda') {
      navigation.navigate('Agenda');
    }
    if (novaAba === 'perfil') {
      navigation.navigate('Perfil');
    }
    if (novaAba === 'materias') {
      navigation.navigate('Materias');
    }
    if (novaAba === 'progresso') {
      navigation.navigate('Progresso');
    }
  };

  const materias = (dados?.subjects ?? []).map(paraMateriaTela);
  const primeiroNome = (dados?.userName ?? '').trim().split(' ')[0];

  const estatisticas = [
    { id: 'materias', icone: '📚', valor: dados?.totalSubjects ?? 0, rotulo: 'Matérias' },
    { id: 'assuntos', icone: '📄', valor: dados?.totalTopics ?? 0, rotulo: 'Assuntos' },
    { id: 'avaliacoes', icone: '✅', valor: dados?.totalEvaluations ?? 0, rotulo: 'Avaliações' },
  ];

  const temContinuar = Boolean(dados?.continueTopicId && dados?.continueTopicName);

  return (
    <SafeAreaView style={styles.areaSegura}>
      <StatusBar barStyle="light-content" />

      <ScrollView
        style={styles.scroll}
        contentContainerStyle={styles.conteudoScroll}
        showsVerticalScrollIndicator={false}
      >
        {/* Cabeçalho */}
        <View style={styles.cabecalho}>
          <View style={styles.linhaTopoCabecalho}>
            <View>
              <Text style={styles.saudacao}>
                Olá{primeiroNome ? `, ${primeiroNome}` : ''} 👋
              </Text>
              <Text style={styles.titulo}>
                Vamos continuar seus estudos?
              </Text>
            </View>

            <TouchableOpacity 
              style={styles.botaoAvatar} 
              activeOpacity={0.8}
              onPress={() => navigation.navigate('Perfil')}
            >
              <Text style={styles.emojiAvatar}>🎓</Text>
            </TouchableOpacity>
          </View>

          <CardProgressoGeral
            porcentagem={dados?.overallProgressPercent ?? 0}
            diasSeguidos={dados?.streakDays ?? 0}
            mensagem={dados?.motivationMessage ?? ''}
          />
        </View>

        {/* Conteúdo */}
        <View style={styles.conteudo}>
          {/* Estatísticas rápidas */}
          <View style={styles.linhaEstatisticas}>
            {estatisticas.map((estatistica) => (
              <CardEstatistica
                key={estatistica.id}
                valor={estatistica.valor}
                rotulo={estatistica.rotulo}
                icone={<Text style={{ fontSize: 18 }}>{estatistica.icone}</Text>}
              />
            ))}
          </View>

          {/* Minhas matérias */}
          <View style={styles.linhaTituloSecao}>
            <Text style={styles.tituloSecao}>Minhas matérias</Text>
            <TouchableOpacity
              activeOpacity={0.7}
              onPress={() => navigation.navigate('Materias')}
            >
              <Text style={styles.linkSecao}>Ver todas</Text>
            </TouchableOpacity>
          </View>

          {carregando ? (
            <ActivityIndicator color={colors.primary} style={{ marginVertical: spacing.lg }} />
          ) : null}

          {erro ? (
            <TouchableOpacity style={styles.caixaAviso} activeOpacity={0.8} onPress={carregar}>
              <Text style={styles.textoAviso}>{erro}</Text>
              <Text style={styles.linkSecao}>Tentar novamente</Text>
            </TouchableOpacity>
          ) : null}

          {!carregando && !erro && materias.length === 0 ? (
            <TouchableOpacity
              style={styles.caixaAviso}
              activeOpacity={0.8}
              onPress={() => navigation.navigate('Materias')}
            >
              <Text style={styles.textoAviso}>Você ainda não cadastrou nenhuma matéria.</Text>
              <Text style={styles.linkSecao}>Cadastrar a primeira</Text>
            </TouchableOpacity>
          ) : null}

          {materias.map((materia) => (
            <TouchableOpacity 
              key={materia.id}
              activeOpacity={0.8}
              onPress={() => navigation.navigate('Materia', materia)}
            >
              <CardMateria
                materia={{
                  id: materia.id,
                  nome: materia.nome,
                  chaveCor: normalizarChaveCor(materia.chaveCor),
                  quantidadeAssuntos: materia.assuntosTotal,
                  progresso: materia.progresso,
                  concluidos: materia.concluidos,
                  pendentes: materia.pendentes,
                }}
                icone={
                  <Text style={{ fontSize: 18 }}>
                    {materia.icone}
                  </Text>
                }
              />
            </TouchableOpacity>
          ))}

          {/* Continuar de onde parou */}
          {temContinuar ? (
            <BannerContinuar
              dados={{
                nomeMateria: dados?.continueSubjectName ?? '',
                nomeAssunto: dados?.continueTopicName ?? '',
                chaveCor: normalizarChaveCor(dados?.continueColorKey),
              }}
              aoPressionar={() =>
                navigation.navigate('Assuntos', {
                  assuntoId: String(dados?.continueTopicId),
                  status: 'andamento',
                  titulo: dados?.continueTopicName,
                  materiaNome: dados?.continueSubjectName,
                })
              }
            />
          ) : null}
        </View>
      </ScrollView>

      <BarraNavegacao ativa="inicio" aoMudar={aoMudarAba} />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  caixaAviso: {
    backgroundColor: colors.surface,
    borderRadius: radius.md,
    borderWidth: 1,
    borderColor: colors.border,
    padding: spacing.md,
    marginBottom: spacing.md,
    alignItems: 'center',
  },
  textoAviso: {
    color: colors.textSecondary,
    fontSize: 14,
    textAlign: 'center',
    marginBottom: spacing.sm,
  },
  areaSegura: {
    flex: 1,
    backgroundColor: colors.surface,
  },
  scroll: {
    flex: 1,
  },
  conteudoScroll: {
    paddingBottom: spacing.md - spacing.xs,
  },
  cabecalho: {
    backgroundColor: colors.primary,
    paddingHorizontal: spacing.lg - spacing.xs,
    paddingTop: spacing.md - spacing.xs,
    paddingBottom: spacing.lg,
    borderBottomLeftRadius: radius.lg + spacing.xs,
    borderBottomRightRadius: radius.lg + spacing.xs,
  },
  linhaTopoCabecalho: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'flex-start',
    marginBottom: spacing.lg - spacing.xs,
  },
  saudacao: {
    color: textoHeaderSuave,
    fontSize: typography.body.fontSize,
    marginBottom: spacing.xs + 2,
  },
  titulo: {
    color: colors.textOnPrimary,
    fontSize: typography.h1.fontSize,
    fontWeight: '800',
    maxWidth: 230,
    lineHeight: 28,
  },
  botaoAvatar: {
    width: 44,
    height: 44,
    borderRadius: 22,
    backgroundColor: 'rgba(255,255,255,0.2)',
    alignItems: 'center',
    justifyContent: 'center',
  },
  emojiAvatar: {
    fontSize: 20,
  },
  conteudo: {
    paddingHorizontal: spacing.lg - spacing.xs,
    paddingTop: spacing.lg - spacing.xs,
  },
  linhaEstatisticas: {
    flexDirection: 'row',
    marginBottom: spacing.lg,
    marginHorizontal: -spacing.xs,
  },
  linhaTituloSecao: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: spacing.sm + spacing.xs,
  },
  tituloSecao: {
    fontSize: typography.h1.fontSize - 4,
    fontWeight: '800',
    color: colors.textPrimary,
  },
  linkSecao: {
    fontSize: typography.caption.fontSize + 1,
    fontWeight: '600',
    color: colors.primary,
  },
});