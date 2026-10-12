import React, { useEffect, useRef, useState } from 'react';
import {
  View, Text, TextInput, FlatList, TouchableOpacity, ActivityIndicator,
  KeyboardAvoidingView, Platform, StyleSheet, SafeAreaView,
} from 'react-native';
import { c, s, r } from '../theme/screenTheme';
import { perguntarGroq } from '../services/groq';

type Autor = 'usuario' | 'ia';
type Mensagem = { id: string; autor: Autor; texto: string };
type Estado = 'ocioso' | 'digitando' | 'erro';

const SUGESTOES = [
  'Explique classes gramaticais',
  'Me ajude a interpretar um texto',
  'Crie um resumo de Português',
];

type Props = { onBack?: () => void };

export default function ChatIAScreen({ onBack }: Props) {
  const [mensagens, setMensagens] = useState<Mensagem[]>([]);
  const [entrada, setEntrada] = useState('');
  const [estado, setEstado] = useState<Estado>('ocioso');
  const [ultimaPergunta, setUltimaPergunta] = useState('');
  const [erroMsg, setErroMsg] = useState('');
  const listaRef = useRef<FlatList<Mensagem>>(null);

  useEffect(() => {
    listaRef.current?.scrollToEnd({ animated: true });
  }, [mensagens, estado]);

  async function enviar(texto: string, reenvio = false) {
    const pergunta = texto.trim();
    if (!pergunta || estado === 'digitando') return;

    const nova: Mensagem = { id: String(Date.now()), autor: 'usuario', texto: pergunta };
    const base = reenvio ? mensagens : [...mensagens, nova];

    if (!reenvio) setMensagens(base);
    setEntrada('');
    setUltimaPergunta(pergunta);
    setErroMsg('');
    setEstado('digitando');

    try {
      const resposta = await perguntarGroq(
        base.map((m) => ({
          role: m.autor === 'usuario' ? ('user' as const) : ('assistant' as const),
          content: m.texto,
        })),
      );
      setMensagens((m) => [...m, { id: String(Date.now() + 1), autor: 'ia', texto: resposta }]);
      setEstado('ocioso');
    } catch (e: any) {
      setErroMsg(e?.message ?? 'Não foi possível obter a resposta.');
      setEstado('erro');
    }
  }

  return (
    <SafeAreaView style={st.tela}>
      <View style={st.cabecalho}>
        {onBack && (
          <TouchableOpacity onPress={onBack} accessibilityLabel="Voltar">
            <Text style={st.voltar}>‹</Text>
          </TouchableOpacity>
        )}
        <Text style={st.titulo}>Assistente IA</Text>
      </View>

      <KeyboardAvoidingView
        style={{ flex: 1 }}
        behavior={Platform.OS === 'ios' ? 'padding' : undefined}
      >
        <FlatList
          ref={listaRef}
          data={mensagens}
          keyExtractor={(m) => m.id}
          contentContainerStyle={st.lista}
          ListEmptyComponent={
            <View style={st.vazio}>
              <Text style={st.vazioTitulo}>Pergunte o que quiser estudar</Text>
              <Text style={st.vazioTexto}>Toque em uma sugestão ou escreva sua dúvida.</Text>
              {SUGESTOES.map((sg) => (
                <TouchableOpacity key={sg} style={st.sugestao} onPress={() => enviar(sg)}>
                  <Text style={st.sugestaoTexto}>{sg}</Text>
                </TouchableOpacity>
              ))}
            </View>
          }
          renderItem={({ item }) => (
            <View style={[st.balao, item.autor === 'usuario' ? st.balaoUsuario : st.balaoIA]}>
              <Text style={item.autor === 'usuario' ? st.textoUsuario : st.textoIA}>
                {item.texto}
              </Text>
            </View>
          )}
          ListFooterComponent={
            <>
              {estado === 'digitando' && (
                <View style={[st.balao, st.balaoIA, st.digitando]}>
                  <ActivityIndicator size="small" color={c.primary} />
                  <Text style={st.textoIA}>  A IA está respondendo…</Text>
                </View>
              )}
              {estado === 'erro' && (
                <View style={st.erro}>
                  <Text style={st.erroTexto}>{erroMsg}</Text>
                  <TouchableOpacity onPress={() => enviar(ultimaPergunta, true)}>
                    <Text style={st.erroAcao}>Tentar novamente</Text>
                  </TouchableOpacity>
                </View>
              )}
            </>
          }
        />

        <View style={st.barra}>
          <TextInput
            style={st.input}
            value={entrada}
            onChangeText={setEntrada}
            placeholder="Escreva sua dúvida"
            placeholderTextColor={c.textMuted}
            multiline
            editable={estado !== 'digitando'}
          />
          <TouchableOpacity
            style={[st.enviar, (!entrada.trim() || estado === 'digitando') && st.enviarOff]}
            disabled={!entrada.trim() || estado === 'digitando'}
            onPress={() => enviar(entrada)}
            accessibilityLabel="Enviar mensagem"
          >
            <Text style={st.enviarTexto}>Enviar</Text>
          </TouchableOpacity>
        </View>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}

const st = StyleSheet.create({
  tela: { flex: 1, backgroundColor: c.background },
  cabecalho: {
    flexDirection: 'row', alignItems: 'center', padding: s.lg,
    backgroundColor: c.surface, borderBottomWidth: 1, borderBottomColor: c.border,
  },
  voltar: { fontSize: 28, color: c.text, marginRight: s.md },
  titulo: { fontSize: 18, fontWeight: '700', color: c.text },
  lista: { padding: s.lg, flexGrow: 1 },
  vazio: { alignItems: 'center', paddingTop: s.xl * 2 },
  vazioTitulo: { fontSize: 18, fontWeight: '700', color: c.text, marginBottom: s.xs },
  vazioTexto: { color: c.textMuted, marginBottom: s.lg, textAlign: 'center' },
  sugestao: {
    borderWidth: 1, borderColor: c.border, backgroundColor: c.surface,
    borderRadius: r.pill, paddingVertical: s.sm, paddingHorizontal: s.lg, marginBottom: s.sm,
  },
  sugestaoTexto: { color: c.primary, fontWeight: '600' },
  balao: { maxWidth: '82%', padding: s.md, borderRadius: r.lg, marginBottom: s.sm },
  balaoUsuario: { alignSelf: 'flex-end', backgroundColor: c.primary, borderBottomRightRadius: r.sm },
  balaoIA: {
    alignSelf: 'flex-start', backgroundColor: c.surface, borderBottomLeftRadius: r.sm,
    borderWidth: 1, borderColor: c.border,
  },
  textoUsuario: { color: c.onPrimary, fontSize: 15 },
  textoIA: { color: c.text, fontSize: 15 },
  digitando: { flexDirection: 'row', alignItems: 'center' },
  erro: {
    backgroundColor: c.surface, borderWidth: 1, borderColor: c.error,
    borderRadius: r.md, padding: s.md, marginTop: s.sm,
  },
  erroTexto: { color: c.error, marginBottom: s.xs },
  erroAcao: { color: c.primary, fontWeight: '700' },
  barra: {
    flexDirection: 'row', alignItems: 'flex-end', padding: s.md,
    backgroundColor: c.surface, borderTopWidth: 1, borderTopColor: c.border,
  },
  input: {
    flex: 1, maxHeight: 110, backgroundColor: c.background, borderRadius: r.md,
    paddingHorizontal: s.md, paddingVertical: s.sm, color: c.text, marginRight: s.sm,
  },
  enviar: { backgroundColor: c.primary, borderRadius: r.md, paddingVertical: s.md, paddingHorizontal: s.lg },
  enviarOff: { opacity: 0.4 },
  enviarTexto: { color: c.onPrimary, fontWeight: '700' },
});
