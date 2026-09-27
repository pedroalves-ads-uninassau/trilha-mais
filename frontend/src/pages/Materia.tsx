import React from 'react';
import {
  View,
  Text,
  StyleSheet,
  SafeAreaView,
  ScrollView,
  TouchableOpacity,
  StatusBar
} from 'react-native';
import { useNavigation, useRoute } from '@react-navigation/native';

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
  const navigation = useNavigation();
  const route = useRoute();
  
  // Recebe os dados da matéria passados pela navegação
  const materia = route.params as MateriaParams;

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
            <Text style={styles.subtitle}>{materia.assuntosTotal} assuntos</Text>
          </View>
        </View>

        {/* Cards de Estatísticas */}
        <View style={styles.statsContainer}>
          <View style={styles.statCard}>
            <Text style={styles.statValue}>{materia.concluidos}</Text>
            <Text style={styles.statLabel}>Concluídos</Text>
          </View>
          <View style={styles.statCard}>
            <Text style={styles.statValue}>{materia.emAndamento}</Text>
            <Text style={styles.statLabel}>Andamento</Text>
          </View>
          <View style={styles.statCard}>
            <Text style={styles.statValue}>{materia.pendentes}</Text>
            <Text style={styles.statLabel}>Pendentes</Text>
          </View>
        </View>

        {/* Barra de Progresso */}
        <View style={styles.progressContainer}>
          <View style={styles.progressTextRow}>
            <Text style={styles.progressLabel}>Progresso</Text>
            <Text style={styles.progressPercent}>{materia.progresso}%</Text>
          </View>
          <View style={styles.progressBarBackground}>
            <View style={[styles.progressBarFill, { width: `${materia.progresso}%` }]} />
          </View>
        </View>
      </View>

      {/* Área Branca de Lista de Assuntos */}
      <View style={styles.content}>
        <View style={styles.listHeader}>
          <Text style={styles.listTitle}>Assuntos para estudar</Text>
          <TouchableOpacity style={styles.btnNovo}>
            <Text style={styles.btnNovoText}>+ Novo</Text>
          </TouchableOpacity>
        </View>

        <ScrollView showsVerticalScrollIndicator={false} contentContainerStyle={styles.scrollList}>
          {materia.assuntosLista.map((assunto) => {
            const config = getStatusConfig(assunto.status);
            
            return (
              <TouchableOpacity key={assunto.id} style={styles.assuntoCard} activeOpacity={0.7}>
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