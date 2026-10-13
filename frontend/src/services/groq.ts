declare const process: { env: Record<string, string | undefined> };
// Serviço de chat com a Groq (API compatível com OpenAI).
// Docs: https://console.groq.com/docs/api-reference

const GROQ_URL = 'https://api.groq.com/openai/v1/chat/completions';
const API_KEY = process.env.EXPO_PUBLIC_GROQ_API_KEY;
// Os modelos da Groq mudam com o tempo. Confira os disponíveis em
// https://console.groq.com/docs/models e troque aqui se precisar.
const MODELO = process.env.EXPO_PUBLIC_GROQ_MODEL ?? 'llama-3.3-70b-versatile';

export type MensagemChat = { role: 'user' | 'assistant'; content: string };

const SYSTEM_PROMPT =
  'Você é o assistente de estudos do app Trilha+. Responda sempre em português do Brasil, ' +
  'de forma clara, didática e objetiva, com exemplos curtos quando ajudar. ' +
  'Se a pergunta não for sobre estudos, responda brevemente e traga a conversa de volta aos estudos.';

export async function perguntarGroq(historico: MensagemChat[]): Promise<string> {
  if (!API_KEY) {
    throw new Error('Chave da Groq não configurada. Defina EXPO_PUBLIC_GROQ_API_KEY no arquivo .env.');
  }

  // Timeout de 20s para não deixar o app carregando para sempre
  const controller = new AbortController();
  const timer = setTimeout(() => controller.abort(), 20000);

  try {
    const resposta = await fetch(GROQ_URL, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${API_KEY}`,
      },
      body: JSON.stringify({
        model: MODELO,
        messages: [{ role: 'system', content: SYSTEM_PROMPT }, ...historico.slice(-12)],
        temperature: 0.6,
        max_tokens: 800,
      }),
      signal: controller.signal,
    });

    if (!resposta.ok) {
      if (resposta.status === 401) throw new Error('Chave da Groq inválida.');
      if (resposta.status === 429) throw new Error('Muitas requisições. Aguarde um instante e tente de novo.');
      throw new Error(`Erro ao falar com a IA (código ${resposta.status}).`);
    }

    const dados = await resposta.json();
    const texto: string | undefined = dados?.choices?.[0]?.message?.content;
    if (!texto) throw new Error('A IA não retornou uma resposta.');
    return texto.trim();
  } catch (e: any) {
    if (e?.name === 'AbortError') throw new Error('A IA demorou demais para responder.');
    if (e instanceof TypeError) throw new Error('Sem conexão com a internet.');
    throw e;
  } finally {
    clearTimeout(timer);
  }
}
