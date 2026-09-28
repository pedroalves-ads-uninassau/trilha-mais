import React, { useState } from 'react';
import {
  Modal,
  Pressable,
  SafeAreaView,
  ScrollView,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from 'react-native';
import { useNavigation, useRoute } from '@react-navigation/native';

export default function Assuntos() {
  const navigation = useNavigation<any>();
  const route = useRoute<any>();

  // Recebe parâmetros ou usa padrões
  const assuntoTitulo = route.params?.titulo || 'Variações linguísticas';
  const materiaNome = route.params?.materiaNome || 'Português';

  // Estado do Modal de Avaliação
  const [modalVisivel, setModalVisivel] = useState(false);
  const [qtdQuestoes, setQtdQuestoes] = useState<number>(10);

  const iniciarAvaliacao = () => {
    setModalVisivel(false);
    // Alterado aqui: Redirecionando para a tela de 'Chat' que você pediu
    navigation.navigate('Chat', {
      assunto: assuntoTitulo,
      materia: materiaNome,
      totalQuestoes: qtdQuestoes,
    });
  };

  return (
    <SafeAreaView style={styles.container}>
      {/* Header */}
      <View style={styles.header}>
        <TouchableOpacity
          style={styles.botaoVoltar}
          onPress={() => navigation.goBack()}
        >
          <Text style={styles.iconeVoltar}>‹</Text>
        </TouchableOpacity>

        <View style={styles.avatarHeader}>
          <Text style={{ fontSize: 20 }}>🤖</Text>
        </View>

        <View style={styles.infoHeader}>
          <Text style={styles.tituloHeader} numberOfLines={1}>
            {assuntoTitulo}
          </Text>
          <Text style={styles.subtituloHeader}>
            📔 {materiaNome} · Assistente Trilha+
          </Text>
        </View>

        <View style={styles.badgeStatus}>
          <Text style={styles.textoStatus}>• Online</Text>
        </View>
      </View>

      {/* Conteúdo do Assunto */}
      <ScrollView
        style={styles.scroll}
        contentContainerStyle={styles.scrollConteudo}
        showsVerticalScrollIndicator={false}
      >
        <View style={styles.cardConteudo}>
          <Text style={styles.saudacao}>
            Olá! Sou o <Text style={{ fontWeight: 'bold' }}>Assistente Trilha Mais</Text> 🤖
          </Text>

          <Text style={styles.textoIntroducao}>
            Vou te ajudar a dominar <Text style={{ fontWeight: 'bold' }}>{assuntoTitulo}</Text>. Vamos começar!
          </Text>


          {/*aqui tem que colocar a IA para mandar o conteudo*/}
          <Text style={styles.subtituloSecao}>O que são Variações Linguísticas?</Text>

          <Text style={styles.paragrafo}>
            A língua não é estática — ela varia de acordo com quem fala, onde fala, quando fala e em qual situação. Existem 4 tipos principais:
          </Text>

          <View style={styles.itemTipo}>
            <Text style={styles.itemTitulo}>🗺️ Regional</Text>
            <Text style={styles.itemDescricao}>
              — diferenças entre regiões. Ex: "oxente" no Nordeste, "bah" no Sul.
            </Text>
          </View>

          <View style={styles.itemTipo}>
            <Text style={styles.itemTitulo}>👥 Social</Text>
            <Text style={styles.itemDescricao}>
              — diferenças entre grupos sociais e gerações. Ex: gírias dos jovens.
            </Text>
          </View>

          <View style={styles.itemTipo}>
            <Text style={styles.itemTitulo}>⏳ Histórica</Text>
            <Text style={styles.itemDescricao}>
              — mudanças da língua ao longo do tempo. Ex: "vossa mercê" → "você".
            </Text>
          </View>

          <View style={styles.itemTipo}>
            <Text style={styles.itemTitulo}>🎯 Situacional</Text>
            <Text style={styles.itemDescricao}>
              — adaptação ao contexto. Você fala diferente com o chefe e com amigos.
            </Text>
          </View>

          <Text style={styles.perguntaFinal}>Qual tipo quer aprofundar primeiro? 📚</Text>
        </View>

        {/* Banner do Teste */}
        <View style={styles.bannerTeste}>
          <Text style={styles.tituloBanner}>
            📝 Pronto para testar seus conhecimentos?
          </Text>
          <TouchableOpacity
            style={styles.botaoTestar}
            activeOpacity={0.8}
            onPress={() => setModalVisivel(true)}
          >
            <Text style={styles.textoBotaoTestar}>🎯 Testar meus conhecimentos</Text>
          </TouchableOpacity>
        </View>
      </ScrollView>

      {/* Modal de Configuração do Teste */}
      <Modal
        visible={modalVisivel}
        transparent
        animationType="slide"
        onRequestClose={() => setModalVisivel(false)}
      >
        <Pressable style={styles.modalOverlay} onPress={() => setModalVisivel(false)}>
          <Pressable style={styles.modalContent} onPress={(e) => e.stopPropagation()}>
            <View style={styles.dragHandle} />

            <Text style={styles.modalTitulo}>Como quer testar seus conhecimentos?</Text>
            <Text style={styles.modalSubtitulo}>
              {assuntoTitulo} · {materiaNome}
            </Text>

            <Text style={styles.labelSecao}>NÚMERO DE QUESTÕES</Text>

            <View style={styles.gridOpcoes}>
              <TouchableOpacity
                style={[
                  styles.cardOpcao,
                  qtdQuestoes === 10 && styles.cardOpcaoAtiva,
                ]}
                activeOpacity={0.8}
                onPress={() => setQtdQuestoes(10)}
              >
                <Text
                  style={[
                    styles.numeroOpcao,
                    qtdQuestoes === 10 && styles.numeroOpcaoAtivo,
                  ]}
                >
                  10
                </Text>
                <Text style={styles.rotuloOpcao}>questões</Text>
                <View style={styles.badgeRecomendado}>
                  <Text style={styles.textoBadgeRecomendado}>Recomendado</Text>
                </View>
              </TouchableOpacity>

              <TouchableOpacity
                style={[
                  styles.cardOpcao,
                  qtdQuestoes === 15 && styles.cardOpcaoAtiva,
                ]}
                activeOpacity={0.8}
                onPress={() => setQtdQuestoes(15)}
              >
                <Text
                  style={[
                    styles.numeroOpcao,
                    qtdQuestoes === 15 && styles.numeroOpcaoAtivo,
                  ]}
                >
                  15
                </Text>
                <Text style={styles.rotuloOpcao}>questões</Text>
              </TouchableOpacity>
            </View>

            <View style={styles.boxInfo}>
              <View style={styles.iconeInfo}>
                <Text style={{ color: '#2563EB', fontWeight: 'bold' }}>i</Text>
              </View>
              <Text style={styles.textoInfo}>
                A avaliação será baseada no conteúdo que você estudou sobre {assuntoTitulo.toLowerCase()}.
              </Text>
            </View>

            <TouchableOpacity
              style={styles.botaoIniciar}
              activeOpacity={0.85}
              onPress={iniciarAvaliacao}
            >
              <Text style={styles.textoBotaoIniciar}>Começar avaliação →</Text>
            </TouchableOpacity>
          </Pressable>
        </Pressable>
      </Modal>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F8FAFC',
  },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 16,
    paddingVertical: 12,
    backgroundColor: '#FFFFFF',
    borderBottomWidth: 1,
    borderBottomColor: '#F1F5F9',
  },
  botaoVoltar: {
    width: 36,
    height: 36,
    borderRadius: 12,
    backgroundColor: '#F1F5F9',
    alignItems: 'center',
    justifyContent: 'center',
    marginRight: 10,
  },
  iconeVoltar: {
    fontSize: 24,
    color: '#0F172A',
    marginTop: -2,
  },
  avatarHeader: {
    width: 40,
    height: 40,
    borderRadius: 12,
    backgroundColor: '#2563EB',
    alignItems: 'center',
    justifyContent: 'center',
    marginRight: 10,
  },
  infoHeader: {
    flex: 1,
  },
  tituloHeader: {
    fontSize: 16,
    fontWeight: '700',
    color: '#0F172A',
  },
  subtituloHeader: {
    fontSize: 12,
    color: '#64748B',
  },
  badgeStatus: {
    backgroundColor: '#DCFCE7',
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 12,
  },
  textoStatus: {
    color: '#166534',
    fontSize: 11,
    fontWeight: '600',
  },
  scroll: {
    flex: 1,
  },
  scrollConteudo: {
    padding: 16,
  },
  cardConteudo: {
    backgroundColor: '#FFFFFF',
    borderRadius: 16,
    padding: 18,
    borderWidth: 1,
    borderColor: '#E2E8F0',
    marginBottom: 16,
  },
  saudacao: {
    fontSize: 15,
    color: '#1E293B',
    marginBottom: 8,
  },
  textoIntroducao: {
    fontSize: 14,
    color: '#334155',
    marginBottom: 16,
  },
  subtituloSecao: {
    fontSize: 16,
    fontWeight: '700',
    color: '#0F172A',
    marginBottom: 8,
  },
  paragrafo: {
    fontSize: 14,
    color: '#475569',
    lineHeight: 20,
    marginBottom: 12,
  },
  itemTipo: {
    marginBottom: 10,
  },
  itemTitulo: {
    fontSize: 14,
    fontWeight: '700',
    color: '#1E293B',
  },
  itemDescricao: {
    fontSize: 13,
    color: '#475569',
    lineHeight: 18,
  },
  perguntaFinal: {
    fontSize: 14,
    color: '#334155',
    marginTop: 10,
  },
  bannerTeste: {
    backgroundColor: '#FEF3C7',
    borderRadius: 16,
    padding: 16,
    borderWidth: 1,
    borderColor: '#FDE68A',
  },
  tituloBanner: {
    fontSize: 14,
    fontWeight: '700',
    color: '#92400E',
    marginBottom: 12,
  },
  botaoTestar: {
    backgroundColor: '#EA580C',
    paddingVertical: 12,
    borderRadius: 12,
    alignItems: 'center',
  },
  textoBotaoTestar: {
    color: '#FFFFFF',
    fontWeight: '700',
    fontSize: 15,
  },

  // Modal
  modalOverlay: {
    flex: 1,
    backgroundColor: 'rgba(15, 23, 42, 0.4)',
    justifyContent: 'flex-end',
  },
  modalContent: {
    backgroundColor: '#FFFFFF',
    borderTopLeftRadius: 24,
    borderTopRightRadius: 24,
    paddingHorizontal: 20,
    paddingBottom: 28,
    paddingTop: 12,
  },
  dragHandle: {
    width: 36,
    height: 4,
    backgroundColor: '#CBD5E1',
    borderRadius: 2,
    alignSelf: 'center',
    marginBottom: 16,
  },
  modalTitulo: {
    fontSize: 18,
    fontWeight: '800',
    color: '#0F172A',
    textAlign: 'center',
  },
  modalSubtitulo: {
    fontSize: 13,
    color: '#64748B',
    textAlign: 'center',
    marginTop: 4,
    marginBottom: 20,
  },
  labelSecao: {
    fontSize: 11,
    fontWeight: '700',
    color: '#64748B',
    letterSpacing: 0.5,
    marginBottom: 12,
  },
  gridOpcoes: {
    flexDirection: 'row',
    gap: 12,
    marginBottom: 16,
  },
  cardOpcao: {
    flex: 1,
    borderWidth: 1.5,
    borderColor: '#E2E8F0',
    borderRadius: 16,
    padding: 16,
    alignItems: 'center',
    backgroundColor: '#FFFFFF',
  },
  cardOpcaoAtiva: {
    borderColor: '#2563EB',
    backgroundColor: '#EFF6FF',
  },
  numeroOpcao: {
    fontSize: 28,
    fontWeight: '800',
    color: '#0F172A',
  },
  numeroOpcaoAtivo: {
    color: '#2563EB',
  },
  rotuloOpcao: {
    fontSize: 12,
    color: '#64748B',
    marginTop: 2,
  },
  badgeRecomendado: {
    marginTop: 8,
  },
  textoBadgeRecomendado: {
    fontSize: 10,
    fontWeight: '700',
    color: '#EA580C',
  },
  boxInfo: {
    flexDirection: 'row',
    backgroundColor: '#EFF6FF',
    borderRadius: 12,
    padding: 12,
    alignItems: 'center',
    marginBottom: 20,
  },
  iconeInfo: {
    width: 20,
    height: 20,
    borderRadius: 10,
    backgroundColor: '#DBEAFE',
    alignItems: 'center',
    justifyContent: 'center',
    marginRight: 10,
  },
  textoInfo: {
    flex: 1,
    fontSize: 12,
    color: '#1D4ED8',
    lineHeight: 16,
  },
  botaoIniciar: {
    backgroundColor: '#EA580C',
    borderRadius: 12,
    paddingVertical: 14,
    alignItems: 'center',
  },
  textoBotaoIniciar: {
    color: '#FFFFFF',
    fontWeight: '700',
    fontSize: 16,
  },
});