import React from 'react';
import {
  View,
  Text,
  StyleSheet,
  SafeAreaView,
  ScrollView,
  TouchableOpacity,
  StatusBar,
  Modal,
  TextInput,
  KeyboardAvoidingView,
  Platform,
} from 'react-native';
import { useNavigation, useRoute } from '@react-navigation/native';
import { useState } from 'react';

// Tipagens para o nosso mock de dados recebido por parâmetro
export interface Assunto {
  id: string;
  titulo: string;
  status: 'concluido' | 'andamento' | 'pendente';
}

export interface MateriaParams {
  id: string;
  nome: string;
  corPrimaria: string;
  icone: string;
  assuntosTotal: number;
  concluidos: number;
  emAndamento: number;
  pendentes: number;
  progresso: number;
  assuntosLista: Assunto[];
}

export default function Materia() {
  const navigation = useNavigation<any>();
  const route = useRoute();
  
  // Recebe os dados da matéria passados pela navegação
  const materia = route.params as MateriaParams;
  const [assuntosLista, setAssuntosLista] = useState<Assunto[]>(materia.assuntosLista ?? []);
  const [modalVisivel, setModalVisivel] = useState(false);
  const [novoAssunto, setNovoAssunto] = useState('');
  const [erroCadastro, setErroCadastro] = useState('');

  const concluidos = assuntosLista.filter((assunto) => assunto.status === 'concluido').length;
  const emAndamento = assuntosLista.filter((assunto) => assunto.status === 'andamento').length;
  const pendentes = assuntosLista.filter((assunto) => assunto.status === 'pendente').length;
  const progresso = assuntosLista.length
    ? Math.round((concluidos / assuntosLista.length) * 100)
    : 0;

  const fecharModal = () => {
    setModalVisivel(false);
    setNovoAssunto('');
    setErroCadastro('');
  };

  const adicionarAssunto = () => {
    const titulo = novoAssunto.trim();
    if (!titulo) {
      setErroCadastro('Digite o nome do assunto.');
      return;
    }

    if (assuntosLista.some((assunto) => assunto.titulo.toLowerCase() === titulo.toLowerCase())) {
      setErroCadastro('Esse assunto já está cadastrado nesta matéria.');
      return;
    }

    setAssuntosLista((listaAtual) => [
      ...listaAtual,
      {
        id: `assunto-${Date.now()}`,
        titulo,
        status: 'pendente',
      },
    ]);
    fecharModal();
  };

  // Função auxiliar para renderizar o ícone e cor do status do assunto
  const getStatusConfig = (status: string) => {
    switch (status) {
      case 'concluido':
        return { icone: '✓', cor: '#10B981', bg: '#D1FAE5', texto: 'Concluído' };
      case 'andamento':
        return { icone: '◐', cor: '#3B82F6', bg: '#DBEAFE', texto: 'Em andamento' };
      case 'pendente':
      default:
        return { icone: '○', cor: '#9CA3AF', bg: '#F3F4F6', texto: 'Pendente' };
    }
  };

  return (
    <SafeAreaView style={[styles.safeArea, { backgroundColor: materia.corPrimaria }]}>
      <StatusBar barStyle="light-content" backgroundColor={materia.corPrimaria} />
      
      {/* Cabeçalho Colorido */}
      <View style={[styles.header, { backgroundColor: materia.corPrimaria }]}>
        
        {/* Bolhas decorativas de fundo (Design idêntico à imagem) */}
        <View style={styles.bolhaDecorativa1} />
        <View style={styles.bolhaDecorativa2} />

        {/* Topo do header: Voltar e Título */}
        <View style={styles.topoHeader}>
          <TouchableOpacity onPress={() => navigation.goBack()} style={styles.backButton}>
            <View style={styles.iconeFundo}>
              <Text style={styles.iconeTexto}>{materia.icone}</Text>
            </View>
            <View style={styles.botaoVoltarOverlay}>
               <Text style={styles.setaVoltar}>{'<'}</Text>
            </View>
          </TouchableOpacity>
          <View style={styles.tituloContainer}>
            <Text style={styles.title}>{materia.nome}</Text>
            <Text style={styles.subtitle}>
              {assuntosLista.length} {assuntosLista.length === 1 ? 'assunto' : 'assuntos'}
            </Text>
          </View>
        </View>

        {/* Cards de Estatísticas */}
        <View style={styles.statsContainer}>
          <View style={styles.statCard}>
            <Text style={styles.statValue}>{concluidos}</Text>
            <Text style={styles.statLabel}>Concluídos</Text>
          </View>
          <View style={styles.statCard}>
            <Text style={styles.statValue}>{emAndamento}</Text>
            <Text style={styles.statLabel}>Andamento</Text>
          </View>
          <View style={styles.statCard}>
            <Text style={styles.statValue}>{pendentes}</Text>
            <Text style={styles.statLabel}>Pendentes</Text>
          </View>
        </View>

        {/* Barra de Progresso */}
        <View style={styles.progressContainer}>
          <View style={styles.progressTextRow}>
            <Text style={styles.progressLabel}>Progresso</Text>
            <Text style={styles.progressPercent}>{progresso}%</Text>
          </View>
          <View style={styles.progressBarBackground}>
            <View style={[styles.progressBarFill, { width: `${progresso}%` }]} />
          </View>
        </View>
      </View>

      {/* Área Branca de Lista de Assuntos */}
      <View style={styles.content}>
        <View style={styles.listHeader}>
          <Text style={styles.listTitle}>Assuntos para estudar</Text>
          <TouchableOpacity
            style={styles.btnNovo}
            activeOpacity={0.75}
            onPress={() => {
              setErroCadastro('');
              setModalVisivel(true);
            }}
            accessibilityRole="button"
            accessibilityLabel="Adicionar novo assunto"
          >
            <Text style={styles.btnNovoText}>+ Novo</Text>
          </TouchableOpacity>
        </View>

        <ScrollView showsVerticalScrollIndicator={false} contentContainerStyle={styles.scrollList}>
          {assuntosLista.length === 0 ? (
            <View style={styles.estadoVazio}>
              <Text style={styles.estadoVazioIcone}>📚</Text>
              <Text style={styles.estadoVazioTitulo}>Nenhum assunto por aqui</Text>
              <Text style={styles.estadoVazioTexto}>
                Adicione o primeiro assunto para começar seus estudos.
              </Text>
            </View>
          ) : null}
          {assuntosLista.map((assunto) => {
            const config = getStatusConfig(assunto.status);
            
            return (
              <TouchableOpacity 
                key={assunto.id} 
                style={styles.assuntoCard} 
                activeOpacity={0.7}
                onPress={() => 
                  navigation.navigate('Assuntos', {
                    titulo: assunto.titulo,
                    materiaNome: materia.nome,
                  })
                }
              >
                <View style={[styles.assuntoIconeBase, { borderColor: config.cor }]}>
                  <Text style={{ color: config.cor, fontSize: 16, fontWeight: 'bold' }}>
                    {config.icone}
                  </Text>
                </View>

                <View style={styles.assuntoInfo}>
                  <Text style={styles.assuntoTitulo}>{assunto.titulo}</Text>
                  <View style={[styles.badge, { backgroundColor: config.bg }]}>
                    <Text style={[styles.badgeText, { color: config.cor }]}>{config.texto}</Text>
                  </View>
                </View>

                <Text style={styles.chevron}>{'>'}</Text>
              </TouchableOpacity>
            );
          })}
        </ScrollView>
      </View>

      <Modal
        visible={modalVisivel}
        transparent
        animationType="fade"
        onRequestClose={fecharModal}
      >
        <KeyboardAvoidingView
          style={styles.fundoModal}
          behavior={Platform.OS === 'ios' ? 'padding' : undefined}
        >
          <View style={styles.conteudoModal}>
            <View style={styles.topoModal}>
              <View>
                <Text style={styles.tituloModal}>Novo assunto</Text>
                <Text style={styles.subtituloModal}>
                  Adicione um assunto em {materia.nome}
                </Text>
              </View>
              <TouchableOpacity
                style={styles.botaoFechar}
                onPress={fecharModal}
                accessibilityRole="button"
                accessibilityLabel="Fechar formulário"
              >
                <Text style={styles.iconeFechar}>×</Text>
              </TouchableOpacity>
            </View>

            <Text style={styles.rotuloCampo}>Nome do assunto</Text>
            <TextInput
              style={styles.campoNome}
              value={novoAssunto}
              onChangeText={(texto) => {
                setNovoAssunto(texto);
                setErroCadastro('');
              }}
              placeholder="Ex.: Equações do segundo grau"
              placeholderTextColor="#9CA3AF"
              maxLength={60}
              returnKeyType="done"
              onSubmitEditing={adicionarAssunto}
              accessibilityLabel="Nome do assunto"
              autoFocus
            />
            {erroCadastro ? <Text style={styles.textoErro}>{erroCadastro}</Text> : null}

            <View style={styles.acoesModal}>
              <TouchableOpacity style={styles.botaoCancelar} onPress={fecharModal}>
                <Text style={styles.textoCancelar}>Cancelar</Text>
              </TouchableOpacity>
              <TouchableOpacity
                style={[styles.botaoCriar, { backgroundColor: materia.corPrimaria }]}
                onPress={adicionarAssunto}
              >
                <Text style={styles.textoCriar}>Adicionar assunto</Text>
              </TouchableOpacity>
            </View>
          </View>
        </KeyboardAvoidingView>
      </Modal>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: { flex: 1 },
  header: {
    paddingHorizontal: 24,
    paddingTop: 20,
    paddingBottom: 32,
    overflow: 'hidden',
  },
  // Efeito visual dos círculos no topo direito
  bolhaDecorativa1: {
    position: 'absolute',
    top: -50,
    right: -20,
    width: 150,
    height: 150,
    borderRadius: 75,
    backgroundColor: 'rgba(255,255,255,0.08)',
  },
  bolhaDecorativa2: {
    position: 'absolute',
    top: 60,
    right: 40,
    width: 100,
    height: 100,
    borderRadius: 50,
    backgroundColor: 'rgba(0,0,0,0.05)',
  },
  topoHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 32,
    zIndex: 10,
  },
  backButton: {
    width: 50,
    height: 50,
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: 16,
  },
  iconeFundo: {
    width: 48,
    height: 48,
    borderRadius: 16,
    backgroundColor: 'rgba(255,255,255,0.2)',
    justifyContent: 'center',
    alignItems: 'center',
  },
  iconeTexto: { fontSize: 24 },
  botaoVoltarOverlay: {
    position: 'absolute',
    left: 4,
    top: 10,
    padding: 4,
  },
  setaVoltar: {
    color: '#FFFFFF',
    fontSize: 20,
    fontWeight: 'bold',
  },
  tituloContainer: { flex: 1 },
  title: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#FFFFFF',
    marginBottom: 4,
  },
  subtitle: {
    fontSize: 14,
    color: 'rgba(255,255,255,0.8)',
  },
  statsContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 24,
  },
  statCard: {
    flex: 1,
    backgroundColor: 'rgba(255,255,255,0.2)',
    borderRadius: 16,
    paddingVertical: 16,
    alignItems: 'center',
    marginHorizontal: 4,
  },
  statValue: {
    fontSize: 22,
    fontWeight: 'bold',
    color: '#FFFFFF',
    marginBottom: 4,
  },
  statLabel: {
    fontSize: 12,
    color: 'rgba(255,255,255,0.9)',
    fontWeight: '500',
  },
  progressContainer: { width: '100%' },
  progressTextRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 8,
  },
  progressLabel: {
    color: '#FFFFFF',
    fontSize: 14,
    fontWeight: '600',
  },
  progressPercent: {
    color: '#FFFFFF',
    fontSize: 14,
    fontWeight: 'bold',
  },
  progressBarBackground: {
    height: 6,
    backgroundColor: 'rgba(255,255,255,0.3)',
    borderRadius: 3,
  },
  progressBarFill: {
    height: 6,
    backgroundColor: '#FFFFFF',
    borderRadius: 3,
  },
  content: {
    flex: 1,
    backgroundColor: '#FFFFFF',
    borderTopLeftRadius: 0, // Design reto na separação
    borderTopRightRadius: 0,
    paddingHorizontal: 24,
    paddingTop: 24,
  },
  listHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 16,
  },
  listTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#1F2937',
  },
  btnNovo: {
    paddingVertical: 6,
    paddingHorizontal: 12,
    borderRadius: 20,
    backgroundColor: '#F0F9FF',
  },
  btnNovoText: {
    color: '#2563EB',
    fontWeight: 'bold',
    fontSize: 14,
  },
  scrollList: {
    paddingBottom: 40,
  },
  estadoVazio: {
    alignItems: 'center',
    backgroundColor: '#F9FAFB',
    borderRadius: 16,
    paddingHorizontal: 24,
    paddingVertical: 32,
  },
  estadoVazioIcone: {
    fontSize: 32,
    marginBottom: 12,
  },
  estadoVazioTitulo: {
    color: '#1F2937',
    fontSize: 16,
    fontWeight: '700',
    marginBottom: 6,
  },
  estadoVazioTexto: {
    color: '#6B7280',
    fontSize: 13,
    lineHeight: 19,
    textAlign: 'center',
  },
  fundoModal: {
    flex: 1,
    justifyContent: 'center',
    padding: 24,
    backgroundColor: 'rgba(21,26,45,0.45)',
  },
  conteudoModal: {
    width: '100%',
    maxWidth: 420,
    alignSelf: 'center',
    backgroundColor: '#FFFFFF',
    borderRadius: 16,
    padding: 24,
  },
  topoModal: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    justifyContent: 'space-between',
    marginBottom: 24,
  },
  tituloModal: {
    color: '#151A2D',
    fontSize: 18,
    fontWeight: '700',
  },
  subtituloModal: {
    color: '#6B7280',
    fontSize: 12,
    marginTop: 4,
  },
  botaoFechar: {
    width: 32,
    height: 32,
    alignItems: 'center',
    justifyContent: 'center',
  },
  iconeFechar: {
    color: '#6B7280',
    fontSize: 26,
    lineHeight: 28,
  },
  rotuloCampo: {
    color: '#151A2D',
    fontSize: 13,
    fontWeight: '600',
    marginBottom: 4,
  },
  campoNome: {
    height: 46,
    borderWidth: 1,
    borderColor: '#E4E7EC',
    borderRadius: 8,
    paddingHorizontal: 16,
    color: '#151A2D',
    fontSize: 14,
  },
  textoErro: {
    color: '#C53A3A',
    fontSize: 12,
    marginTop: 8,
  },
  acoesModal: {
    flexDirection: 'row',
    justifyContent: 'flex-end',
    marginTop: 24,
    gap: 12,
  },
  botaoCancelar: {
    minHeight: 44,
    justifyContent: 'center',
    paddingHorizontal: 16,
    borderRadius: 10,
    backgroundColor: '#F3F4F6',
  },
  textoCancelar: {
    color: '#4B5563',
    fontSize: 14,
    fontWeight: '600',
  },
  botaoCriar: {
    minHeight: 44,
    justifyContent: 'center',
    paddingHorizontal: 16,
    borderRadius: 10,
  },
  textoCriar: {
    color: '#FFFFFF',
    fontSize: 14,
    fontWeight: '600',
  },
  assuntoCard: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#FFFFFF',
    borderWidth: 1,
    borderColor: '#F3F4F6',
    borderRadius: 16,
    padding: 16,
    marginBottom: 12,
    elevation: 1, // Sombra suave Android
    shadowColor: '#000', // Sombra suave iOS
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.05,
    shadowRadius: 2,
  },
  assuntoIconeBase: {
    width: 40,
    height: 40,
    borderRadius: 12,
    borderWidth: 1.5,
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: 16,
  },
  assuntoInfo: { flex: 1 },
  assuntoTitulo: {
    fontSize: 15,
    fontWeight: '600',
    color: '#1F2937',
    marginBottom: 6,
  },
  badge: {
    alignSelf: 'flex-start',
    paddingVertical: 2,
    paddingHorizontal: 8,
    borderRadius: 12,
  },
  badgeText: {
    fontSize: 11,
    fontWeight: 'bold',
  },
  chevron: {
    color: '#D1D5DB',
    fontSize: 18,
    fontWeight: 'bold',
    marginLeft: 8,
  },
});