import React, { useState } from 'react';
import { View, Text, TouchableOpacity, ScrollView, StyleSheet, StatusBar } from 'react-native';
import { LinearGradient } from 'expo-linear-gradient'; // instalar: npx expo install expo-linear-gradient

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
  fortes?: string[]; // assuntos em que foi bem
  fracos?: string[]; // assuntos a melhorar
};

// Cores do design da tela de resultado
const COR = {
  fundo: '#F8FAFC',
  azulTopo1: '#1E40AF',
  azulTopo2: '#2563EB',
  azul: '#2563EB',
  azulClaro: '#EFF6FF',
  azulBorda: '#BFDBFE',
  verde: '#059669',
  verdeClaro: '#ECFDF5',
  verdeBorda: '#A7F3D0',
  vermelho: '#EF4444',
  vermelhoClaro: '#FEF2F2',
  vermelhoBorda: '#FECACA',
  texto: '#0F172A',
  textoSuave: '#475569',
  borda: '#E2E8F0',
  branco: '#FFFFFF',
};

type Props = {
  resultado?: ResultadoQuiz;
  onRefazer?: () => void;
  onDesempenho?: () => void;
  onVoltar?: () => void;
};

// Dado de exemplo, usado só se a tela abrir sem resultado
const EXEMPLO: ResultadoQuiz = {
  acertos: 3,
  total: 10,
  revisao: [],
  fortes: ['Variação histórica', 'Variação situacional', 'Norma-padrão'],
  fracos: ['Variação regional', 'Variação social'],
};

function mensagemPorPercentual(p: number) {
  if (p === 100) return { emoji: '🏆', titulo: 'Excelente!' };
  if (p >= 70) return { emoji: '🎉', titulo: 'Muito bem!' };
  if (p >= 50) return { emoji: '👏', titulo: 'Bom trabalho!' };
  return { emoji: '💪', titulo: 'Continue praticando!' };
}

export default function ResultadoScreen({ resultado = EXEMPLO, onRefazer, onDesempenho, onVoltar }: Props) {
  const [revisando, setRevisando] = useState(false);

  const { acertos, total, revisao, fortes = [], fracos = [] } = resultado;
  const erros = total - acertos;
  const percentual = total > 0 ? Math.round((acertos / total) * 100) : 0;
  const msg = mensagemPorPercentual(percentual);

  return (
    <View style={st.tela}>
      <StatusBar barStyle="light-content" />
      <ScrollView showsVerticalScrollIndicator={false} contentContainerStyle={{ paddingBottom: 32 }}>
        {/* Topo azul */}
        <LinearGradient colors={[COR.azulTopo1, COR.azulTopo2]} style={st.topo}>
          <View style={st.circulo} />
          <Text style={st.emoji}>{msg.emoji}</Text>
          <Text style={st.titulo}>{msg.titulo}</Text>
          <Text style={st.subtitulo}>Avaliação concluída</Text>

          <View style={st.cardNota}>
            <Text style={st.percentual}>{percentual}%</Text>
            <Text style={st.notaTexto}>{acertos} de {total} corretas</Text>
          </View>
        </LinearGradient>

        <View style={st.corpo}>
          {/* Corretas / Erradas / Total */}
          <View style={st.linha}>
            <View style={[st.caixa, { backgroundColor: COR.verdeClaro, borderColor: COR.verdeBorda }]}>
              <Text style={[st.caixaNumero, { color: COR.verde }]}>{acertos}</Text>
              <Text style={[st.caixaRotulo, { color: COR.verde }]}>Corretas</Text>
            </View>
            <View style={[st.caixa, { backgroundColor: COR.vermelhoClaro, borderColor: COR.vermelhoBorda }]}>
              <Text style={[st.caixaNumero, { color: COR.vermelho }]}>{erros}</Text>
              <Text style={[st.caixaRotulo, { color: COR.vermelho }]}>Erradas</Text>
            </View>
            <View style={[st.caixa, { backgroundColor: COR.azulClaro, borderColor: COR.azulBorda }]}>
              <Text style={[st.caixaNumero, { color: COR.azul }]}>{total}</Text>
              <Text style={[st.caixaRotulo, { color: COR.azul }]}>Total</Text>
            </View>
          </View>

          {/* Análise do desempenho */}
          <View style={st.card}>
            <View style={st.cardCabecalho}>
              <View style={st.icone}>
                <Text style={{ fontSize: 16 }}>📊</Text>
              </View>
              <Text style={st.cardTitulo}>Análise do seu desempenho</Text>
            </View>

            {fortes.length > 0 && (
              <>
                <Text style={[st.secao, { color: COR.verde }]}>✅ Você foi bem em:</Text>
                {fortes.map((a) => (
                  <View key={a} style={st.item}>
                    <View style={[st.ponto, { backgroundColor: COR.verde }]} />
                    <Text style={st.itemTexto}>{a}</Text>
                  </View>
                ))}
              </>
            )}

            {fracos.length > 0 && (
              <>
                <Text style={[st.secao, { color: COR.vermelho, marginTop: 12 }]}>⚠️ Precisa melhorar em:</Text>
                {fracos.map((a) => (
                  <View key={a} style={st.item}>
                    <View style={[st.ponto, { backgroundColor: COR.vermelho }]} />
                    <Text style={st.itemTexto}>{a}</Text>
                  </View>
                ))}
              </>
            )}

            {fortes.length === 0 && fracos.length === 0 && (
              <Text style={st.itemTexto}>Sem assuntos para analisar neste quiz.</Text>
            )}
          </View>

          {/* Revisão das respostas */}
          {revisando &&
            revisao.map((item, i) => (
              <View
                key={i}
                style={[st.card, { borderColor: item.acertou ? COR.verdeBorda : COR.vermelhoBorda }]}
              >
                <Text style={st.revEnunciado}>{i + 1}. {item.enunciado}</Text>
                <Text style={{ color: item.acertou ? COR.verde : COR.vermelho, fontWeight: '700' }}>
                  {item.respostaUsuario === null ? 'Sem resposta' : `Sua resposta: ${item.respostaUsuario}`}
                </Text>
                {!item.acertou && <Text style={st.itemTexto}>Correta: {item.respostaCorreta}</Text>}
              </View>
            ))}

          {/* Botões */}
          {revisao.length > 0 && (
            <TouchableOpacity style={st.botao} onPress={() => setRevisando((v) => !v)}>
              <Text style={st.botaoTexto}>{revisando ? 'Ocultar revisão' : 'Revisar respostas'}</Text>
            </TouchableOpacity>
          )}
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
        </View>
      </ScrollView>
    </View>
  );
}

const st = StyleSheet.create({
  tela: { flex: 1, backgroundColor: COR.fundo },
  topo: { alignItems: 'center', paddingTop: 64, paddingBottom: 36, paddingHorizontal: 24, overflow: 'hidden' },
  circulo: {
    position: 'absolute', top: -30, right: -30, width: 130, height: 130,
    borderRadius: 65, backgroundColor: 'rgba(255,255,255,0.12)',
  },
  emoji: { fontSize: 44, marginBottom: 12 },
  titulo: { fontSize: 24, fontWeight: '800', color: COR.branco },
  subtitulo: { fontSize: 14, color: 'rgba(255,255,255,0.8)', marginTop: 4, marginBottom: 20 },
  cardNota: {
    backgroundColor: 'rgba(255,255,255,0.18)', borderRadius: 22,
    paddingVertical: 20, paddingHorizontal: 40, alignItems: 'center',
  },
  percentual: { fontSize: 52, fontWeight: '800', color: COR.branco },
  notaTexto: { fontSize: 14, color: 'rgba(255,255,255,0.85)', fontWeight: '600', marginTop: 2 },
  corpo: { padding: 16 },
  linha: { flexDirection: 'row', marginBottom: 16 },
  caixa: {
    flex: 1, alignItems: 'center', paddingVertical: 14, borderRadius: 16,
    borderWidth: 1, marginHorizontal: 4,
  },
  caixaNumero: { fontSize: 22, fontWeight: '800' },
  caixaRotulo: { fontSize: 12, fontWeight: '600', marginTop: 2 },
  card: {
    backgroundColor: COR.branco, borderRadius: 18, borderWidth: 1,
    borderColor: COR.borda, padding: 16, marginBottom: 12,
  },
  cardCabecalho: { flexDirection: 'row', alignItems: 'center', marginBottom: 14 },
  icone: {
    width: 36, height: 36, borderRadius: 10, backgroundColor: COR.azul,
    alignItems: 'center', justifyContent: 'center', marginRight: 10,
  },
  cardTitulo: { fontSize: 15, fontWeight: '700', color: COR.texto, flex: 1 },
  secao: { fontSize: 13, fontWeight: '700', marginBottom: 6 },
  item: { flexDirection: 'row', alignItems: 'center', marginBottom: 6, paddingLeft: 2 },
  ponto: { width: 6, height: 6, borderRadius: 3, marginRight: 10 },
  itemTexto: { fontSize: 13, color: COR.textoSuave },
  revEnunciado: { fontWeight: '700', color: COR.texto, marginBottom: 4 },
  botao: { backgroundColor: COR.azul, borderRadius: 14, padding: 16, alignItems: 'center', marginTop: 8 },
  botaoTexto: { color: COR.branco, fontWeight: '700', fontSize: 15 },
  botaoSec: {
    borderWidth: 1, borderColor: COR.azul, borderRadius: 14, padding: 16,
    alignItems: 'center', marginTop: 8,
  },
  botaoSecTexto: { color: COR.azul, fontWeight: '700', fontSize: 15 },
});
