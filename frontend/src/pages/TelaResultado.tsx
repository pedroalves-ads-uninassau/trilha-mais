import React, { useState } from 'react';
import { View, Text, TouchableOpacity, ScrollView, StyleSheet, SafeAreaView } from 'react-native';
import { c, s, r } from '../theme/screenTheme';

export type ItemRevisao = {
  enunciado: string;
  respostaUsuario: string | null; // null = não respondeu
  respostaCorreta: string;
  acertou: boolean;
};

export type ResultadoQuiz = {
  acertos: number;
  total: number;
  revisao: ItemRevisao[];
};

const PONTOS_POR_QUESTAO = 10;

type Props = {
  resultado?: ResultadoQuiz;
  onRefazer?: () => void;
  onDesempenho?: () => void;
  onVoltar?: () => void;
};

// TODO: dado de exemplo, usado só se a tela abrir sem resultado
const EXEMPLO: ResultadoQuiz = {
  acertos: 3,
  total: 4,
  revisao: [
    { enunciado: 'Qual palavra abaixo é um substantivo?', respostaUsuario: 'Felicidade', respostaCorreta: 'Felicidade', acertou: true },
    { enunciado: 'Em "Os alunos estudaram muito", o verbo é:', respostaUsuario: 'muito', respostaCorreta: 'estudaram', acertou: false },
    { enunciado: 'Qual é o antônimo de "generoso"?', respostaUsuario: 'Avarento', respostaCorreta: 'Avarento', acertou: true },
    { enunciado: 'Qual frase está no plural?', respostaUsuario: 'As casas são novas', respostaCorreta: 'As casas são novas', acertou: true },
  ],
};

function feedbackPorPercentual(p: number) {
  if (p === 100) return { titulo: 'Excelente!', texto: 'Você acertou tudo. Continue assim.' };
  if (p >= 70) return { titulo: 'Muito bem!', texto: 'Você domina boa parte do conteúdo. Revise os erros para chegar aos 100%.' };
  if (p >= 50) return { titulo: 'Bom começo', texto: 'Você está no caminho. Reveja os pontos de atenção e tente de novo.' };
  return { titulo: 'Vale revisar o conteúdo', texto: 'Estude os pontos de atenção abaixo e refaça o quiz.' };
}

export default function ResultadoScreen({ resultado = EXEMPLO, onRefazer, onDesempenho, onVoltar }: Props) {
  const [revisando, setRevisando] = useState(false);

  const { acertos, total, revisao } = resultado;
  const erros = total - acertos;
  const percentual = total > 0 ? Math.round((acertos / total) * 100) : 0;
  const pontuacao = acertos * PONTOS_POR_QUESTAO;
  const pontuacaoMax = total * PONTOS_POR_QUESTAO;
  const fb = feedbackPorPercentual(percentual);
  const pontosAtencao = revisao.filter((i) => !i.acertou);

  return (
    <SafeAreaView style={st.tela}>
      <ScrollView contentContainerStyle={st.conteudo}>
        {/* Pontuação + percentual */}
        <View style={st.resumo}>
          <Text style={st.percentual}>{percentual}%</Text>
          <Text style={st.pontuacao}>{pontuacao} de {pontuacaoMax} pontos</Text>
        </View>

        {/* Acertos e erros */}
        <View style={st.linha}>
          <View style={[st.caixa, { borderColor: c.success }]}>
            <Text style={[st.caixaNumero, { color: c.success }]}>{acertos}</Text>
            <Text style={st.caixaRotulo}>{acertos === 1 ? 'acerto' : 'acertos'}</Text>
          </View>
          <View style={[st.caixa, { borderColor: c.error }]}>
            <Text style={[st.caixaNumero, { color: c.error }]}>{erros}</Text>
            <Text style={st.caixaRotulo}>{erros === 1 ? 'erro' : 'erros'}</Text>
          </View>
        </View>

        {/* Feedback */}
        <View style={st.card}>
          <Text style={st.cardTitulo}>{fb.titulo}</Text>
          <Text style={st.cardTexto}>{fb.texto}</Text>
        </View>

        {/* Pontos de atenção */}
        <Text style={st.secao}>Pontos de atenção</Text>
        {pontosAtencao.length === 0 ? (
          <View style={st.card}>
            <Text style={st.cardTexto}>Nenhum ponto de atenção. Você acertou todas as questões.</Text>
          </View>
        ) : (
          pontosAtencao.map((item, i) => (
            <View key={i} style={[st.card, { borderColor: c.error }]}>
              <Text style={st.cardTitulo}>{item.enunciado}</Text>
              <Text style={st.cardTexto}>Correta: {item.respostaCorreta}</Text>
            </View>
          ))
        )}

        {/* Revisão (aparece ao tocar no botão) */}
        {revisando && (
          <>
            <Text style={st.secao}>Revisão das respostas</Text>
            {revisao.map((item, i) => (
              <View key={i} style={[st.card, { borderColor: item.acertou ? c.success : c.error }]}>
                <Text style={st.cardTitulo}>{i + 1}. {item.enunciado}</Text>
                <Text style={{ color: item.acertou ? c.success : c.error, fontWeight: '700' }}>
                  {item.respostaUsuario === null ? 'Sem resposta' : `Sua resposta: ${item.respostaUsuario}`}
                </Text>
                {!item.acertou && <Text style={st.cardTexto}>Correta: {item.respostaCorreta}</Text>}
              </View>
            ))}
          </>
        )}

        {/* Botões */}
        <TouchableOpacity style={st.botao} onPress={() => setRevisando((v) => !v)}>
          <Text style={st.botaoTexto}>{revisando ? 'Ocultar revisão' : 'Revisar respostas'}</Text>
        </TouchableOpacity>
        {onRefazer && (
          <TouchableOpacity style={st.botaoSec} onPress={onRefazer}>
            <Text style={st.botaoSecTexto}>Refazer quiz</Text>
          </TouchableOpacity>
        )}
        {onDesempenho && (
          <TouchableOpacity style={st.botaoSec} onPress={onDesempenho}>
            <Text style={st.botaoSecTexto}>Ver desempenho geral</Text>
          </TouchableOpacity>
        )}
        {onVoltar && (
          <TouchableOpacity style={st.botaoSec} onPress={onVoltar}>
            <Text style={st.botaoSecTexto}>Voltar ao início</Text>
          </TouchableOpacity>
        )}
      </ScrollView>
    </SafeAreaView>
  );
}

const st = StyleSheet.create({
  tela: { flex: 1, backgroundColor: c.background },
  conteudo: { padding: s.lg },
  resumo: { backgroundColor: c.primary, borderRadius: r.lg, padding: s.xl, alignItems: 'center', marginBottom: s.lg },
  percentual: { fontSize: 52, fontWeight: '800', color: c.onPrimary },
  pontuacao: { color: c.onPrimary, marginTop: s.xs, fontSize: 16 },
  linha: { flexDirection: 'row', marginBottom: s.lg },
  caixa: {
    flex: 1, alignItems: 'center', backgroundColor: c.surface, borderWidth: 2,
    borderRadius: r.md, padding: s.lg, marginHorizontal: s.xs,
  },
  caixaNumero: { fontSize: 32, fontWeight: '800' },
  caixaRotulo: { color: c.textMuted },
  secao: { fontSize: 16, fontWeight: '700', color: c.text, marginBottom: s.md, marginTop: s.sm },
  card: { backgroundColor: c.surface, borderWidth: 1, borderColor: c.border, borderRadius: r.md, padding: s.lg, marginBottom: s.md },
  cardTitulo: { fontWeight: '700', color: c.text, marginBottom: s.xs },
  cardTexto: { color: c.textMuted, lineHeight: 20 },
  botao: { backgroundColor: c.primary, borderRadius: r.md, padding: s.lg, alignItems: 'center', marginTop: s.md },
  botaoTexto: { color: c.onPrimary, fontWeight: '700' },
  botaoSec: { borderWidth: 1, borderColor: c.primary, borderRadius: r.md, padding: s.lg, alignItems: 'center', marginTop: s.sm },
  botaoSecTexto: { color: c.primary, fontWeight: '700' },
});
