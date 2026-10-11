import React from 'react';
import {
  SafeAreaView,
  ScrollView,
  StatusBar,
  StyleSheet,
  Text,
  View,
} from 'react-native';
import { useNavigation } from '@react-navigation/native';
import BarraNavegacao, { ChaveAba } from '../components/BarraNavegacao'; // Ajuste o caminho conforme seu projeto

// Dados mockados para as matérias
const desempenhoMaterias = [
  { id: '1', nome: 'Português', porcentagem: 72, cor: '#2F68E9' },
  { id: '2', nome: 'Matemática', porcentagem: 50, cor: '#8B5CF6' },
  { id: '3', nome: 'Biologia', porcentagem: 85, cor: '#10B981' },
  { id: '4', nome: 'História', porcentagem: 17, cor: '#F59E0B' },
];

export default function Progresso() {
  const navigation = useNavigation<any>();

  const aoMudarAba = (novaAba: ChaveAba) => {
    if (novaAba === 'inicio') navigation.navigate('Home');
    if (novaAba === 'materias') navigation.navigate('Materias');
    if (novaAba === 'perfil') navigation.navigate('Perfil');
  };

  return (
    <SafeAreaView style={styles.areaSegura}>
      <StatusBar barStyle="light-content" backgroundColor="#2F68E9" />

      <ScrollView
        style={styles.scroll}
        contentContainerStyle={styles.conteudoScroll}
        showsVerticalScrollIndicator={false}
      >
        {/* Cabeçalho Azul */}
        <View style={styles.cabecalho}>
          <Text style={styles.tituloSecao}>Meu Desempenho</Text>
          <Text style={styles.subtituloSecao}>Acompanhe sua evolução</Text>

          {/* Card de Média Geral */}
          <View style={styles.cardMediaGeral}>
            <View style={styles.blocoMedia}>
              <Text style={styles.textoMediaGeral}>MÉDIA GERAL</Text>
              <Text style={styles.valorMediaGeral}>82%</Text>
            </View>

            <View style={styles.divisor} />

            <View style={styles.blocoEstatisticas}>
              <View style={styles.estatisticaItem}>
                <Text style={styles.estatisticaValor}>8</Text>
                <Text style={styles.estatisticaRotulo}>Avaliações</Text>
              </View>
              <View style={styles.estatisticaItem}>
                <Text style={styles.estatisticaValor}>17</Text>
                <Text style={styles.estatisticaRotulo}>Estudados</Text>
              </View>
              <View style={styles.estatisticaItem}>
                <Text style={styles.estatisticaValor}>🔥 5</Text>
                <Text style={styles.estatisticaRotulo}>Sequência</Text>
              </View>
            </View>
          </View>
        </View>

        {/* Conteúdo Principal Branco */}
        <View style={styles.conteudoBranco}>
          <Text style={styles.tituloDesempenho}>Desempenho por matéria</Text>

          {/* Lista de Barras de Progresso */}
          {desempenhoMaterias.map((materia) => (
            <View key={materia.id} style={styles.cardMateria}>
              <View style={styles.infoMateria}>
                <Text style={styles.nomeMateria}>{materia.nome}</Text>
                <Text style={[styles.porcentagemMateria, { color: materia.cor }]}>
                  {materia.porcentagem}%
                </Text>
              </View>
              <View style={styles.barraFundo}>
                <View
                  style={[
                    styles.barraPreenchida,
                    { backgroundColor: materia.cor, width: `${materia.porcentagem}%` },
                  ]}
                />
              </View>
            </View>
          ))}

          {/* Cards de Destaque */}
          <View style={styles.linhaCardsDestaque}>
            {/* Card Melhor Matéria */}
            <View style={[styles.cardDestaque, { backgroundColor: '#F0FDF4', borderColor: '#BBF7D0' }]}>
              <Text style={[styles.tituloDestaque, { color: '#059669' }]}>MELHOR MATÉRIA</Text>
              <Text style={styles.nomeDestaque}>🔬 Biologia</Text>
              <Text style={[styles.descDestaque, { color: '#059669' }]}>85% de{'\n'}aproveitamento</Text>
            </View>

            {/* Card Precisa Revisar */}
            <View style={[styles.cardDestaque, { backgroundColor: '#FFFBEB', borderColor: '#FDE68A' }]}>
              <Text style={[styles.tituloDestaque, { color: '#D97706' }]}>PRECISA REVISAR</Text>
              <Text style={styles.nomeDestaque}>📖 História</Text>
              <Text style={[styles.descDestaque, { color: '#D97706' }]}>17% de{'\n'}aproveitamento</Text>
            </View>
          </View>
        </View>
      </ScrollView>

      {/* Barra de Navegação Inferior */}
      <BarraNavegacao ativa="progresso" aoMudar={aoMudarAba} />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  areaSegura: {
    flex: 1,
    backgroundColor: '#2F68E9', 
  },
  scroll: {
    flex: 1,
    backgroundColor: '#FFFFFF',
  },
  conteudoScroll: {
    flexGrow: 1,
    paddingBottom: 20,
  },
  cabecalho: {
    backgroundColor: '#2F68E9',
    paddingHorizontal: 24,
    paddingTop: 30,
    paddingBottom: 30,
  },
  tituloSecao: {
    color: '#FFFFFF',
    fontSize: 28,
    fontWeight: 'bold',
    marginBottom: 4,
  },
  subtituloSecao: {
    color: 'rgba(255, 255, 255, 0.8)',
    fontSize: 16,
    marginBottom: 24,
  },
  cardMediaGeral: {
    backgroundColor: 'rgba(255, 255, 255, 0.15)',
    borderRadius: 16,
    borderWidth: 1,
    borderColor: 'rgba(255, 255, 255, 0.2)',
    flexDirection: 'row',
    padding: 20,
    alignItems: 'center',
  },
  blocoMedia: {
    flex: 1,
  },
  textoMediaGeral: {
    color: 'rgba(255, 255, 255, 0.9)',
    fontSize: 12,
    fontWeight: 'bold',
    marginBottom: 4,
  },
  valorMediaGeral: {
    color: '#FFFFFF',
    fontSize: 40,
    fontWeight: '900',
  },
  divisor: {
    width: 1,
    height: '100%',
    backgroundColor: 'rgba(255, 255, 255, 0.2)',
    marginHorizontal: 16,
  },
  blocoEstatisticas: {
    flex: 1.5,
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  estatisticaItem: {
    alignItems: 'center',
  },
  estatisticaValor: {
    color: '#FFFFFF',
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 2,
  },
  estatisticaRotulo: {
    color: 'rgba(255, 255, 255, 0.8)',
    fontSize: 10,
  },
  conteudoBranco: {
    backgroundColor: '#FFFFFF',
    paddingHorizontal: 24,
    paddingTop: 24,
  },
  tituloDesempenho: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#0F172A',
    marginBottom: 20,
  },
  cardMateria: {
    backgroundColor: '#FFFFFF',
    borderWidth: 1,
    borderColor: '#F1F5F9',
    borderRadius: 16,
    padding: 16,
    marginBottom: 16,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.03,
    shadowRadius: 4,
    elevation: 2,
  },
  infoMateria: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 12,
  },
  nomeMateria: {
    fontSize: 16,
    fontWeight: '600',
    color: '#0F172A',
  },
  porcentagemMateria: {
    fontSize: 16,
    fontWeight: 'bold',
  },
  barraFundo: {
    height: 8,
    backgroundColor: '#E2E8F0',
    borderRadius: 4,
    overflow: 'hidden',
  },
  barraPreenchida: {
    height: '100%',
    borderRadius: 4,
  },
  linhaCardsDestaque: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginTop: 8,
    marginBottom: 24,
  },
  cardDestaque: {
    flex: 1,
    borderWidth: 1,
    borderRadius: 16,
    padding: 16,
    marginHorizontal: 4,
  },
  tituloDestaque: {
    fontSize: 10,
    fontWeight: 'bold',
    marginBottom: 8,
  },
  nomeDestaque: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#0F172A',
    marginBottom: 8,
  },
  descDestaque: {
    fontSize: 12,
    fontWeight: '500',
  },
});