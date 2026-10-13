import React, { useState } from 'react';
import { View, Text, TouchableOpacity, ScrollView, StyleSheet, SafeAreaView, Alert } from 'react-native';
import { c, s, r } from '../theme/screenTheme';
import type { ResultadoQuiz } from './TelaResultado';

type Questao = { id: string; enunciado: string; alternativas: string[]; correta: number; assunto?: string };

// TODO: troque pelo seu mock em src/Mocks ou pela API
const QUESTOES: Questao[] = [
  { id: 'q1', enunciado: 'Qual palavra abaixo é um substantivo?', alternativas: ['Correr', 'Felicidade', 'Rapidamente', 'Azul'], correta: 1, assunto: 'Classes gramaticais' },
  { id: 'q2', enunciado: 'Em "Os alunos estudaram muito", o verbo é:', alternativas: ['Os', 'alunos', 'estudaram', 'muito'], correta: 2, assunto: 'Verbos' },
  { id: 'q3', enunciado: 'Qual é o antônimo de "generoso"?', alternativas: ['Bondoso', 'Avarento', 'Gentil', 'Honesto'], correta: 1, assunto: 'Vocabulário' },
  { id: 'q4', enunciado: 'Qual frase está no plural?', alternativas: ['O livro caiu', 'As casas são novas', 'Ela chegou cedo', 'Meu amigo veio'], correta: 1, assunto: 'Classes gramaticais' },
];

type Props = {
  questoes?: Questao[];
  onFinish?: (resultado: ResultadoQuiz) => void; // ex.: levar para a tela de resultado
  onClose?: () => void;
};

export default function QuizScreen({ questoes = QUESTOES, onFinish, onClose }: Props) {
  const total = questoes.length;
  const [indice, setIndice] = useState(0);
  const [respostas, setRespostas] = useState<(number | null)[]>(Array(total).fill(null));
  const [finalizado, setFinalizado] = useState(false);

  const q = questoes[indice];
  const respondidas = respostas.filter((x) => x !== null).length;
  const primeira = indice === 0;
  const ultima = indice === total - 1;
  const acertos = respostas.filter((x, i) => x === questoes[i].correta).length;

  function selecionar(i: number) {
    setRespostas((rs) => rs.map((x, k) => (k === indice ? i : x)));
  }

  function concluir() {
    if (onFinish) {
      const revisao = questoes.map((qq, i) => ({
        enunciado: qq.enunciado,
        respostaUsuario: respostas[i] === null ? null : qq.alternativas[respostas[i] as number],
        respostaCorreta: qq.alternativas[qq.correta],
        acertou: respostas[i] === qq.correta,
      }));
      // assuntos: "foi bem" = acertou todas as questões do assunto; "melhorar" = errou ou deixou em branco alguma
      const porAssunto: Record<string, boolean> = {};
      questoes.forEach((qq, i) => {
        if (!qq.assunto) return;
        const ok = respostas[i] === qq.correta;
        porAssunto[qq.assunto] = (porAssunto[qq.assunto] ?? true) && ok;
      });
      const fortes = Object.keys(porAssunto).filter((a) => porAssunto[a]);
      const fracos = Object.keys(porAssunto).filter((a) => !porAssunto[a]);
      onFinish({ acertos, total, revisao, fortes, fracos });
    } else {
      setFinalizado(true); // sem navegação ligada, mostra o resultado aqui mesmo
    }
  }

  function finalizar() {
    const faltam = total - respondidas;
    if (faltam > 0) {
      Alert.alert(
        'Finalizar quiz?',
        `Você ainda não respondeu ${faltam} ${faltam === 1 ? 'questão' : 'questões'}.`,
        [
          { text: 'Continuar respondendo', style: 'cancel' },
          { text: 'Finalizar mesmo assim', onPress: concluir },
        ],
      );
    } else {
      concluir();
    }
  }

  function refazer() {
    setRespostas(Array(total).fill(null));
    setIndice(0);
    setFinalizado(false);
  }

  // ---------- Tela de resultado (Finalização) ----------
  if (finalizado) {
    const nota = Math.round((acertos / total) * 100);
    return (
      <SafeAreaView style={st.tela}>
        <ScrollView contentContainerStyle={st.conteudo}>
          <View style={st.resumo}>
            <Text style={st.nota}>{nota}%</Text>
            <Text style={st.resumoTexto}>{acertos} de {total} questões corretas</Text>
          </View>

          <Text style={st.secao}>Revisão</Text>
          {questoes.map((qq, i) => {
            const resp = respostas[i];
            const certa = resp === qq.correta;
            return (
              <View key={qq.id} style={[st.cardRev, { borderColor: certa ? c.success : c.error }]}>
                <Text style={st.revEnunciado}>{i + 1}. {qq.enunciado}</Text>
                <Text style={{ color: certa ? c.success : c.error, fontWeight: '700' }}>
                  {resp === null ? 'Sem resposta' : `Sua resposta: ${qq.alternativas[resp]}`}
                </Text>
                {!certa && <Text style={st.revCerta}>Correta: {qq.alternativas[qq.correta]}</Text>}
              </View>
            );
          })}

          <TouchableOpacity style={st.botao} onPress={onClose}>
            <Text style={st.botaoTexto}>Concluir</Text>
          </TouchableOpacity>
          <TouchableOpacity style={st.botaoSec} onPress={refazer}>
            <Text style={st.botaoSecTexto}>Refazer quiz</Text>
          </TouchableOpacity>
        </ScrollView>
      </SafeAreaView>
    );
  }

  // ---------- Tela da questão ----------
  return (
    <SafeAreaView style={st.tela}>
      <View style={st.topo}>
        {onClose && (
          <TouchableOpacity onPress={onClose} accessibilityLabel="Sair do quiz">
            <Text style={st.fechar}>✕</Text>
          </TouchableOpacity>
        )}
        <Text style={st.contador}>Questão {indice + 1} de {total}</Text>
        <Text style={st.respondidas}>{respondidas}/{total} respondidas</Text>
      </View>

      <View style={st.trilho}>
        <View style={[st.preenchido, { width: `${((indice + 1) / total) * 100}%` }]} />
      </View>

      <ScrollView contentContainerStyle={st.conteudo}>
        <Text style={st.enunciado}>{q.enunciado}</Text>

        {q.alternativas.map((texto, i) => {
          const marcada = respostas[indice] === i;
          return (
            <TouchableOpacity
              key={i}
              style={[st.alt, marcada && st.altMarcada]}
              onPress={() => selecionar(i)}
              accessibilityRole="radio"
              accessibilityState={{ selected: marcada }}
            >
              <Text style={st.letra}>{String.fromCharCode(65 + i)}</Text>
              <Text style={st.altTexto}>{texto}</Text>
            </TouchableOpacity>
          );
        })}
      </ScrollView>

      <View style={st.rodape}>
        <TouchableOpacity
          style={[st.navBtn, primeira && st.off]}
          disabled={primeira}
          onPress={() => setIndice((i) => i - 1)}
        >
          <Text style={st.navTexto}>Anterior</Text>
        </TouchableOpacity>

        {ultima ? (
          <TouchableOpacity style={[st.navBtn, st.navPrimario]} onPress={finalizar}>
            <Text style={st.navTextoPrim}>Finalizar</Text>
          </TouchableOpacity>
        ) : (
          <TouchableOpacity style={[st.navBtn, st.navPrimario]} onPress={() => setIndice((i) => i + 1)}>
            <Text style={st.navTextoPrim}>Próxima</Text>
          </TouchableOpacity>
        )}
      </View>
    </SafeAreaView>
  );
}

const st = StyleSheet.create({
  tela: { flex: 1, backgroundColor: c.background },
  topo: { flexDirection: 'row', alignItems: 'center', padding: s.lg },
  fechar: { fontSize: 20, color: c.text, marginRight: s.md },
  contador: { flex: 1, fontSize: 16, fontWeight: '700', color: c.text },
  respondidas: { color: c.textMuted },
  trilho: { height: 8, backgroundColor: c.border, marginHorizontal: s.lg, borderRadius: r.pill, overflow: 'hidden' },
  preenchido: { height: '100%', backgroundColor: c.primary, borderRadius: r.pill },
  conteudo: { padding: s.lg },
  enunciado: { fontSize: 20, fontWeight: '700', color: c.text, marginBottom: s.xl },
  alt: {
    flexDirection: 'row', alignItems: 'center', backgroundColor: c.surface,
    borderWidth: 2, borderColor: c.border, borderRadius: r.md, padding: s.lg, marginBottom: s.md,
  },
  altMarcada: { borderColor: c.primary },
  letra: { fontWeight: '800', color: c.primary, width: 28 },
  altTexto: { flex: 1, color: c.text, fontSize: 16 },
  rodape: {
    flexDirection: 'row', padding: s.lg, backgroundColor: c.surface,
    borderTopWidth: 1, borderTopColor: c.border,
  },
  navBtn: {
    flex: 1, padding: s.lg, borderRadius: r.md, alignItems: 'center',
    borderWidth: 1, borderColor: c.primary, marginHorizontal: s.xs,
  },
  navPrimario: { backgroundColor: c.primary },
  navTexto: { color: c.primary, fontWeight: '700' },
  navTextoPrim: { color: c.onPrimary, fontWeight: '700' },
  off: { opacity: 0.35 },
  resumo: { backgroundColor: c.primary, borderRadius: r.lg, padding: s.xl, alignItems: 'center', marginBottom: s.xl },
  nota: { fontSize: 48, fontWeight: '800', color: c.onPrimary },
  resumoTexto: { color: c.onPrimary, marginTop: s.xs },
  secao: { fontSize: 16, fontWeight: '700', color: c.text, marginBottom: s.md },
  cardRev: { backgroundColor: c.surface, borderWidth: 2, borderRadius: r.md, padding: s.lg, marginBottom: s.md },
  revEnunciado: { color: c.text, fontWeight: '600', marginBottom: s.xs },
  revCerta: { color: c.textMuted, marginTop: s.xs },
  botao: { backgroundColor: c.primary, borderRadius: r.md, padding: s.lg, alignItems: 'center', marginTop: s.md },
  botaoTexto: { color: c.onPrimary, fontWeight: '700' },
  botaoSec: { borderWidth: 1, borderColor: c.primary, borderRadius: r.md, padding: s.lg, alignItems: 'center', marginTop: s.sm },
  botaoSecTexto: { color: c.primary, fontWeight: '700' },
});
