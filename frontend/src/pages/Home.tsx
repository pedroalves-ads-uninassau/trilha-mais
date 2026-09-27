import React, { useState } from 'react';
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

import BannerContinuar from '../components/BannerContinuar';
import BarraNavegacao, { ChaveAba } from '../components/BarraNavegacao';
import CardEstatistica from '../components/CardEstatistica';
import CardMateria from '../components/CardMateria';
import CardProgressoGeral from '../components/CardProgressoGeral';

import {
  mockContinuarEstudando,
  mockEstatisticasRapidas,
  materiasDetalhadas, 
  mockProgressoGeral,
  nomeUsuario,
} from '../Mocks/mockInicio';
import { colors, radius, spacing, typography } from '../theme/tokens';

const iconesEstatisticas: Record<string, string> = {
  'book-copy': '📚',
  'file-text': '📄',
  'check-square': '✅',
};

const textoHeaderSuave = 'rgba(255,255,255,0.85)';

export default function Home() {
  const [abaAtiva, setAbaAtiva] = useState<ChaveAba>('inicio');
  
  const navigation = useNavigation<any>();

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
              <Text style={styles.saudacao}>Olá, {nomeUsuario} 👋</Text>
              <Text style={styles.titulo}>
                Vamos continuar seus estudos?
              </Text>
            </View>

            <TouchableOpacity style={styles.botaoAvatar} activeOpacity={0.8}>
              <Text style={styles.emojiAvatar}>🎓</Text>
            </TouchableOpacity>
          </View>

          <CardProgressoGeral
            porcentagem={mockProgressoGeral.porcentagem}
            diasSeguidos={mockProgressoGeral.diasSeguidos}
            mensagem={mockProgressoGeral.mensagem}
          />
        </View>

        {/* Conteúdo */}
        <View style={styles.conteudo}>
          {/* Estatísticas rápidas */}
          <View style={styles.linhaEstatisticas}>
            {mockEstatisticasRapidas.map((estatistica) => (
              <CardEstatistica
                key={estatistica.id}
                valor={estatistica.valor}
                rotulo={estatistica.rotulo}
                icone={
                  <Text style={{ fontSize: 18 }}>
                    {iconesEstatisticas[estatistica.icone] ?? '•'}
                  </Text>
                }
              />
            ))}
          </View>

          {/* Minhas matérias */}
          <View style={styles.linhaTituloSecao}>
            <Text style={styles.tituloSecao}>Minhas matérias</Text>
            <TouchableOpacity activeOpacity={0.7}>
              <Text style={styles.linkSecao}>Ver todas</Text>
            </TouchableOpacity>
          </View>

          {materiasDetalhadas.map((materia) => (
            <TouchableOpacity 
              key={materia.id}
              activeOpacity={0.8}
              onPress={() => navigation.navigate('Materia', materia)}
            >
              <CardMateria
                materia={materia as any}
                icone={
                  <Text style={{ fontSize: 18 }}>
                    {materia.icone}
                  </Text>
                }
              />
            </TouchableOpacity>
          ))}

          {/* Continuar de onde parou */}
          <BannerContinuar dados={mockContinuarEstudando} />
        </View>
      </ScrollView>

      <BarraNavegacao ativa={abaAtiva} aoMudar={setAbaAtiva} />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
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