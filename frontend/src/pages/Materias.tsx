import React, { useState } from 'react';
import {
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
import { useNavigation } from '@react-navigation/native';

import BarraNavegacao, { ChaveAba } from '../components/BarraNavegacao';
import CardMateria from '../components/CardMateria';
import { materiasDetalhadas } from '../Mocks/mockInicio';
import { mapaCoresMaterias, ChaveCorMateria } from '../theme/CoresMaterias';
import { colors, radius, spacing, typography } from '../theme/tokens';
import type { Materia as DadosCardMateria } from '../types/inicio';

const opcoesCor: { chave: ChaveCorMateria; rotulo: string; icone: string }[] = [
  { chave: 'portugues', rotulo: 'Azul', icone: '📘' },
  { chave: 'matematica', rotulo: 'Roxo', icone: '📐' },
  { chave: 'historia', rotulo: 'Laranja', icone: '📙' },
  { chave: 'biologia', rotulo: 'Verde', icone: '🔬' },
];

export default function Materias() {
  const navigation = useNavigation<any>();
  const [materias, setMaterias] = useState(materiasDetalhadas);
  const [modalVisivel, setModalVisivel] = useState(false);
  const [nomeMateria, setNomeMateria] = useState('');
  const [corSelecionada, setCorSelecionada] = useState<ChaveCorMateria>('portugues');
  const [erroCadastro, setErroCadastro] = useState('');

  const aoMudarAba = (novaAba: ChaveAba) => {
    if (novaAba === 'inicio') navigation.navigate('Home');
    if (novaAba === 'agenda') navigation.navigate('Agenda');
    if (novaAba === 'progresso') navigation.navigate('Progresso');
    if (novaAba === 'perfil') navigation.navigate('Perfil');
  };

  const fecharModal = () => {
    setModalVisivel(false);
    setNomeMateria('');
    setErroCadastro('');
  };

  const adicionarMateria = () => {
    const nome = nomeMateria.trim();
    if (!nome) {
      setErroCadastro('Digite o nome da matéria.');
      return;
    }

    if (materias.some((materia) => materia.nome.toLocaleLowerCase() === nome.toLocaleLowerCase())) {
      setErroCadastro('Já existe uma matéria com esse nome.');
      return;
    }

    const opcaoCor = opcoesCor.find((opcao) => opcao.chave === corSelecionada)!;
    const novaMateria = {
      id: `materia-${Date.now()}`,
      nome,
      chaveCor: corSelecionada,
      corPrimaria: mapaCoresMaterias[corSelecionada].principal,
      icone: opcaoCor.icone,
      assuntosTotal: 0,
      concluidos: 0,
      emAndamento: 0,
      pendentes: 0,
      progresso: 0,
      assuntosLista: [],
    };

    setMaterias((listaAtual) => [...listaAtual, novaMateria]);
    fecharModal();
  };

  return (
    <SafeAreaView style={styles.areaSegura}>
      <StatusBar barStyle="light-content" backgroundColor={colors.primary} />

      <View style={styles.cabecalho}>
        <Text style={styles.titulo}>Matérias</Text>
        <Text style={styles.subtitulo}>
          {materias.length} matérias cadastradas
        </Text>
      </View>

      <ScrollView
        style={styles.scroll}
        contentContainerStyle={styles.conteudoScroll}
        showsVerticalScrollIndicator={false}
      >
        {materias.map((materia) => (
          <TouchableOpacity
            key={materia.id}
            activeOpacity={0.82}
            onPress={() => navigation.navigate('Materia', materia)}
          >
            <CardMateria
              materia={{
                id: materia.id,
                nome: materia.nome,
                chaveCor: materia.chaveCor as DadosCardMateria['chaveCor'],
                quantidadeAssuntos: materia.assuntosTotal,
                progresso: materia.progresso,
                concluidos: materia.concluidos,
                pendentes: materia.pendentes,
              }}
              icone={<Text style={styles.iconeMateria}>{materia.icone}</Text>}
            />
          </TouchableOpacity>
        ))}

        <View style={styles.acoes}>
          <TouchableOpacity
            style={styles.botaoAcao}
            onPress={() => navigation.navigate('Quiz')}
          >
            <Text style={styles.botaoAcaoTexto}>Fazer quiz</Text>
          </TouchableOpacity>

          <TouchableOpacity
            style={styles.botaoAcao}
            onPress={() => navigation.navigate('ChatIA')}
          >
            <Text style={styles.botaoAcaoTexto}>Tirar dúvidas com a IA</Text>
          </TouchableOpacity>
        </View>

        <TouchableOpacity
          style={styles.botaoNovaMateria}
          activeOpacity={0.75}
          onPress={() => setModalVisivel(true)}
        >
          <Text style={styles.textoNovaMateria}>＋  Nova matéria</Text>
        </TouchableOpacity>
      </ScrollView>

      <BarraNavegacao ativa="materias" aoMudar={aoMudarAba} />

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
                <Text style={styles.tituloModal}>Nova matéria</Text>
                <Text style={styles.subtituloModal}>Adicione uma matéria à sua lista</Text>
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

            <Text style={styles.rotuloCampo}>Nome</Text>
            <TextInput
              style={styles.campoNome}
              value={nomeMateria}
              onChangeText={(texto) => {
                setNomeMateria(texto);
                setErroCadastro('');
              }}
              placeholder="Ex.: Física"
              placeholderTextColor={colors.placeholder}
              maxLength={40}
              returnKeyType="done"
              onSubmitEditing={adicionarMateria}
              accessibilityLabel="Nome da matéria"
            />
            {erroCadastro ? <Text style={styles.textoErro}>{erroCadastro}</Text> : null}

            <Text style={styles.rotuloCampo}>Cor</Text>
            <View style={styles.opcoesCor}>
              {opcoesCor.map((opcao) => {
                const selecionada = opcao.chave === corSelecionada;
                return (
                  <TouchableOpacity
                    key={opcao.chave}
                    style={styles.opcaoCor}
                    onPress={() => setCorSelecionada(opcao.chave)}
                    accessibilityRole="button"
                    accessibilityLabel={`Selecionar cor ${opcao.rotulo}`}
                    accessibilityState={{ selected: selecionada }}
                  >
                    <View
                      style={[
                        styles.amostraCor,
                        { backgroundColor: mapaCoresMaterias[opcao.chave].principal },
                        selecionada && styles.amostraCorSelecionada,
                      ]}
                    >
                      {selecionada ? <Text style={styles.marcaSelecionada}>✓</Text> : null}
                    </View>
                    <Text style={styles.rotuloCor}>{opcao.rotulo}</Text>
                  </TouchableOpacity>
                );
              })}
            </View>

            <View style={styles.acoesModal}>
              <TouchableOpacity style={styles.botaoCancelar} onPress={fecharModal}>
                <Text style={styles.textoCancelar}>Cancelar</Text>
              </TouchableOpacity>
              <TouchableOpacity style={styles.botaoCriar} onPress={adicionarMateria}>
                <Text style={styles.textoCriar}>Adicionar</Text>
              </TouchableOpacity>
            </View>
          </View>
        </KeyboardAvoidingView>
      </Modal>
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
    paddingTop: spacing.md,
    paddingBottom: spacing.lg,
    borderBottomLeftRadius: radius.lg,
    borderBottomRightRadius: radius.lg,
  },
  titulo: {
    color: colors.textOnPrimary,
    fontSize: typography.h1.fontSize,
    fontWeight: '800',
  },
  subtitulo: {
    color: 'rgba(255,255,255,0.82)',
    fontSize: typography.caption.fontSize + 1,
    marginTop: spacing.xs,
  },
  scroll: {
    flex: 1,
  },
  conteudoScroll: {
    paddingHorizontal: spacing.lg,
    paddingTop: spacing.md,
    paddingBottom: spacing.sm,
  },
  iconeMateria: {
    fontSize: 18,
  },
  botaoNovaMateria: {
    minHeight: 48,
    borderWidth: 1,
    borderStyle: 'dashed',
    borderColor: '#BBD2FF',
    borderRadius: radius.md,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#F1F6FF',
  },
  textoNovaMateria: {
    color: colors.primary,
    fontSize: typography.body.fontSize,
    fontWeight: '600',
  },
  fundoModal: {
    flex: 1,
    justifyContent: 'center',
    padding: spacing.lg,
    backgroundColor: 'rgba(21,26,45,0.45)',
  },
  conteudoModal: {
    width: '100%',
    maxWidth: 420,
    alignSelf: 'center',
    backgroundColor: colors.background,
    borderRadius: radius.md,
    padding: spacing.lg,
  },
  topoModal: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    justifyContent: 'space-between',
    marginBottom: spacing.lg,
  },
  tituloModal: {
    color: colors.textPrimary,
    fontSize: typography.h2.fontSize,
    fontWeight: '700',
  },
  subtituloModal: {
    color: colors.textSecondary,
    fontSize: typography.caption.fontSize,
    marginTop: spacing.xs,
  },
  botaoFechar: {
    width: 32,
    height: 32,
    alignItems: 'center',
    justifyContent: 'center',
  },
  iconeFechar: {
    color: colors.textSecondary,
    fontSize: 26,
    lineHeight: 28,
  },
  rotuloCampo: {
    color: colors.textPrimary,
    fontSize: typography.caption.fontSize + 1,
    fontWeight: '600',
    marginBottom: spacing.xs,
  },
  campoNome: {
    height: 46,
    borderWidth: 1,
    borderColor: colors.border,
    borderRadius: radius.sm,
    paddingHorizontal: spacing.md,
    color: colors.textPrimary,
    fontSize: typography.body.fontSize,
    marginBottom: spacing.md,
  },
  textoErro: {
    color: '#C53A3A',
    fontSize: typography.caption.fontSize,
    marginTop: -spacing.sm,
    marginBottom: spacing.md,
  },
  opcoesCor: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginTop: spacing.xs,
    marginBottom: spacing.lg,
  },
  opcaoCor: {
    alignItems: 'center',
    minWidth: 54,
    gap: spacing.xs,
  },
  amostraCor: {
    width: 34,
    height: 34,
    borderRadius: 17,
    alignItems: 'center',
    justifyContent: 'center',
    borderWidth: 2,
    borderColor: 'transparent',
  },
  amostraCorSelecionada: {
    borderColor: colors.textPrimary,
  },
  marcaSelecionada: {
    color: colors.textOnPrimary,
    fontWeight: '700',
  },
  rotuloCor: {
    color: colors.textSecondary,
    fontSize: typography.caption.fontSize - 1,
  },
  acoesModal: {
    flexDirection: 'row',
    justifyContent: 'flex-end',
    gap: spacing.sm,
  },
  botaoCancelar: {
    minHeight: 42,
    minWidth: 92,
    alignItems: 'center',
    justifyContent: 'center',
    borderRadius: radius.sm,
    borderWidth: 1,
    borderColor: colors.border,
  },
  textoCancelar: {
    color: colors.textSecondary,
    fontSize: typography.body.fontSize,
    fontWeight: '600',
  },
  botaoCriar: {
    minHeight: 42,
    minWidth: 108,
    alignItems: 'center',
    justifyContent: 'center',
    borderRadius: radius.sm,
    backgroundColor: colors.primary,
  },
  textoCriar: {
    color: colors.textOnPrimary,
    fontSize: typography.body.fontSize,
    fontWeight: '600',
      },
    acoes: {
      marginTop: spacing.lg,
      gap: spacing.md,
    },
    botaoAcao: {
      backgroundColor: colors.primary,
      borderRadius: radius.md,
      paddingVertical: spacing.md,
      alignItems: 'center',
    },
    botaoAcaoTexto: {
      color: '#FFFFFF',
      fontWeight: '700',
      },
   });