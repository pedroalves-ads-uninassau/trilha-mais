import React, { useEffect, useState } from 'react';
import { View, Text, TouchableOpacity, ScrollView, StyleSheet, SafeAreaView, ActivityIndicator } from 'react-native';
import { c, s, r } from '../theme/screenTheme';
import type { ResultadoQuiz } from './TelaResultado';
import { quizzesApi, ApiError } from '../services/api';
import { avisar, confirmar } from '../utils/alerta';

type Questao = {
  id: string;
  enunciado: string;
  alternativas: string[];
  opcaoIds: number[];
  correta: number;
  assunto?: string;
};

type Props = {
  topicId?: number;
  subjectId?: number;
  questionCount?: number;
  onFinish?: (resultado: ResultadoQuiz) => void;
  onClose?: () => void;
};

export default function QuizScreen({ topicId, subjectId, questionCount = 10, onFinish, onClose }: Props) {
  const [questoes, setQuestoes] = useState<Questao[]>([]);
  const [quizId, setQuizId] = useState<number | null>(null);
  const [carregando, setCarregando] = useState(true);
  const [enviando, setEnviando] = useState(false);
  const total = questoes.length;
  const [indice, setIndice] = useState(0);
  const [respostas, setRespostas] = useState<(number | null)[]>([]);
  const [finalizado, setFinalizado] = useState(false);

  useEffect(() => {
    quizzesApi
      .generate({ topicId, subjectId, questionCount })
      .then((d) => {
        const qs: Questao[] = d.questions.map((q) => ({
          id: String(q.id),
          enunciado: q.text,
          alternativas: q.options.map((o) => o.text),
          opcaoIds: q.options.map((o) => o.id),
          correta: -1, // o gabarito só vem depois do envio
        }));
        setQuizId(d.id);
        setQuestoes(qs);
        setRespostas(Array(qs.length).fill(null));
      })
      .catch((e) => {
        if (e?.status === 401) return;
        avisar('Erro', e instanceof ApiError ? e.message : 'Não foi possível gerar o simulado.');
        onClose?.();
      })
      .finally(() => setCarregando(false));
  }, []);

  const q = questoes[indice];
  const respondidas = respostas.filter((x) => x !== null).length;
  const primeira = indice === 0;
  const ultima = indice === total - 1;
  const acertos = respostas.filter((x, i) => x === questoes[i]?.correta).length;

  function selecionar(i: number) {
    setRespostas((rs) => rs.map((x, k) => (k === indice ? i : x)));
  }

  async function concluir() {
    if (!onFinish || quizId === null || enviando) return;

    const respondidasLista = questoes
      .map((qq, i) =>
        respostas[i] === null
          ? null
          : { questionId: Number(qq.id), selectedOptionId: qq.opcaoIds[respostas[i] as number] },
      )
      .filter((x): x is { questionId: number; selectedOptionId: number } => x !== null);

    if (respondidasLista.length === 0) {
      avisar('Atenção', 'Responda ao menos uma questão.');
      return;
    }

    try {
      setEnviando(true);
      const res = await quizzesApi.submit(quizId, respondidasLista);
      onFinish({
        acertos: res.correctAnswers,
        total: res.totalQuestions,
        revisao: res.results.map((x) => ({
          enunciado: x.questionText,
          respostaUsuario: x.selectedOptionText,
          respostaCorreta: x.correctOptionText,
          acertou: x.isCorrect,
        })),
        fortes: res.fortes,
        fracos: res.fracos,
      });
    } catch (e) {
      avisar('Erro', e instanceof ApiError ? e.message : 'Não foi possível enviar as respostas.');
    } finally {
      setEnviando(false);
    }
  }

  async function finalizar() {
    const faltam = total - respondidas;
    if (faltam > 0) {
      const confirmado = await confirmar(
        'Finalizar quiz?',
        `Você ainda não respondeu ${faltam} ${faltam === 1 ? 'questão' : 'questões'}.`,
        'Finalizar mesmo assim',
        'Continuar respondendo',
      );
      if (!confirmado) return;
    }
    concluir();
  }

  function refazer() {
    setRespostas(Array(total).fill(null));
    setIndice(0);
    setFinalizado(false);
  }

  // ---------- Carregando ----------
  if (carregando) {
    return (
      <SafeAreaView style={st.tela}>
        <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }}>
          <ActivityIndicator size="large" />
          <Text style={{ marginTop: 12 }}>Gerando seu simulado...</Text>
        </View>
      </SafeAreaView>
    );
  }

  // ---------- Sem questões ----------
  if (!finalizado && !q) {
    return (
      <SafeAreaView style={st.tela}>
        <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center', padding: 24 }}>
          <Text style={{ textAlign: 'center', marginBottom: 16 }}>
            Não foi possível carregar as questões.
          </Text>
          <TouchableOpacity style={st.botao} onPress={onClose}>
            <Text style={st.botaoTexto}>Voltar</Text>
          </TouchableOpacity>
        </View>
      </SafeAreaView>
    );
  }

  // ---------- Tela de resultado (usada só se não houver onFinish) ----------
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
          <TouchableOpacity
            style={[st.navBtn, st.navPrimario, enviando && st.off]}
            onPress={finalizar}
            disabled={enviando}
          >
            <Text style={st.navTextoPrim}>{enviando ? 'Enviando...' : 'Finalizar'}</Text>
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