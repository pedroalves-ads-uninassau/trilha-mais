import AsyncStorage from '@react-native-async-storage/async-storage';

declare const process: { env: Record<string, string | undefined> };

// Sem EXPO_PUBLIC_API_URL definida (ex.: build do Vercel), usa a API publicada no Render.
// Para rodar com o back-end local, defina EXPO_PUBLIC_API_URL=http://localhost:8080 no .env.
const API_PUBLICADA = 'https://trilha-mais-backend.onrender.com';
const BASE_URL = (process.env.EXPO_PUBLIC_API_URL || API_PUBLICADA).replace(/\/+$/, '');
const TOKEN_KEY = '@trilhamais:token';

export const tokenStorage = {
  get: () => AsyncStorage.getItem(TOKEN_KEY),
  set: (token: string) => AsyncStorage.setItem(TOKEN_KEY, token),
  clear: () => AsyncStorage.removeItem(TOKEN_KEY),
};

export class ApiError extends Error {
  status: number;
  constructor(message: string, status: number) {
    super(message);
    this.status = status;
  }
}

// Avisa a navegação quando o servidor recusa o token (sessão expirada ou inválida)
let aoExpirarSessao: (() => void) | null = null;
export const definirAoExpirarSessao = (fn: (() => void) | null) => {
  aoExpirarSessao = fn;
};

type Options = {
  method?: 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE';
  body?: unknown;
  auth?: boolean;
};

export async function request<T>(path: string, opts: Options = {}): Promise<T> {
  const { method = 'GET', body, auth = true } = opts;
  const headers: Record<string, string> = { 'Content-Type': 'application/json' };

  if (auth) {
    const token = await tokenStorage.get();
    if (token) headers.Authorization = `Bearer ${token}`;
  }

  let res: Response;
  try {
    res = await fetch(`${BASE_URL}${path}`, {
      method,
      headers,
      body: body ? JSON.stringify(body) : undefined,
    });
  } catch {
    throw new ApiError('Não foi possível conectar ao servidor. Confira se a API está no ar.', 0);
  }

  const text = await res.text();
  let data: any = null;
  if (text) {
    try {
      data = JSON.parse(text);
    } catch {
      data = null;
    }
  }

  if (!res.ok) {
    if (res.status === 401 && auth) {
      await tokenStorage.clear();
      aoExpirarSessao?.();
      throw new ApiError('Sua sessão expirou. Entre novamente.', 401);
    }
    // Erros de validação vêm como { message, errors: { campo: 'mensagem' } }
    const detalhes =
      data?.errors && typeof data.errors === 'object'
        ? Object.values(data.errors as Record<string, string>).join(' ')
        : '';
    throw new ApiError(detalhes || data?.message || 'Erro ao comunicar com o servidor.', res.status);
  }
  return data as T;
}

// ---------- Auth ----------
export const authApi = {
  async login(email: string, password: string) {
    const res = await request<{
      accessToken: string;
      tokenType: string;
      expiresIn: number;
      user: { id: number; name: string; email: string };
    }>('/api/auth/login', {
      method: 'POST',
      body: { email, password },
      auth: false,
    });
    await tokenStorage.set(res.accessToken);
    return res.user;
  },
  async register(name: string, email: string, password: string) {
    await request<UserResponse>('/api/users/register', {
      method: 'POST',
      body: { name, email, password },
      auth: false,
    });
    return authApi.login(email, password);
  },
  logout: () => tokenStorage.clear(),
  temSessao: async () => Boolean(await tokenStorage.get()),
  forgotPassword: (email: string) =>
    request<{ message: string }>('/api/auth/forgot-password', {
      method: 'POST',
      body: { email },
      auth: false,
    }),
  resetPassword: (email: string, code: string, newPassword: string) =>
    request<{ message: string }>('/api/auth/reset-password', {
      method: 'POST',
      body: { email, code, newPassword },
      auth: false,
    }),
};

// ---------- Matérias ----------
export type SubjectResponse = {
  id: number;
  name: string;
  description?: string;
  colorKey: string;
  primaryColor: string;
  icon: string;
  topicsCount: number;
  completedCount: number;
  inProgressCount: number;
  pendingCount: number;
  progress: number;
};

export const subjectsApi = {
  list: () => request<SubjectResponse[]>('/api/subjects'),
  create: (data: { name: string; colorKey: string; primaryColor: string; icon: string }) =>
    request<SubjectResponse>('/api/subjects', { method: 'POST', body: data }),
  remove: (id: number | string) =>
    request<void>(`/api/subjects/${id}`, { method: 'DELETE' }),
};

export const paraMateriaTela = (s: SubjectResponse) => ({
  id: String(s.id),
  nome: s.name,
  chaveCor: s.colorKey,
  corPrimaria: s.primaryColor,
  icone: s.icon,
  assuntosTotal: s.topicsCount,
  concluidos: s.completedCount,
  emAndamento: s.inProgressCount,
  pendentes: s.pendingCount,
  progresso: s.progress,
  assuntosLista: [] as any[],
});

// ---------- Assuntos ----------
export type TopicStatus = 'pendente' | 'andamento' | 'concluido';

export type TopicResponse = {
  id: number;
  name: string;
  description?: string;
  status: TopicStatus;
  subjectId: number;
  subjectName: string;
};

export const topicsApi = {
  listBySubject: (subjectId: number | string) =>
    request<TopicResponse[]>(`/api/subjects/${subjectId}/topics`),
  create: (subjectId: number | string, data: { name: string; description?: string }) =>
    request<TopicResponse>(`/api/subjects/${subjectId}/topics`, { method: 'POST', body: data }),
  updateStatus: (id: number | string, status: TopicStatus) =>
    request<TopicResponse>(`/api/topics/${id}/status?status=${status}`, { method: 'PATCH' }),
  remove: (id: number | string) =>
    request<void>(`/api/topics/${id}`, { method: 'DELETE' }),
};

export const paraAssuntoTela = (t: TopicResponse) => ({
  id: String(t.id),
  titulo: t.name,
  status: t.status,
});

// ---------- Home ----------
export type DashboardResponse = {
  userName: string;
  streakDays: number;
  overallProgressPercent: number;
  motivationMessage: string;
  totalSubjects: number;
  totalTopics: number;
  totalEvaluations: number;
  continueSubjectId: number | null;
  continueSubjectName: string | null;
  continueTopicId: number | null;
  continueTopicName: string | null;
  continueColorKey: string | null;
  subjects: SubjectResponse[];
};

export const dashboardApi = {
  get: () => request<DashboardResponse>('/api/dashboard'),
};

// ---------- Perfil ----------
export type UserResponse = {
  id: number;
  name: string;
  email: string;
  streakDays: number;
  evaluationsCount: number;
  subjectsCount: number;
  avatarEmoji: string | null;
};

export const usersApi = {
  me: () => request<UserResponse>('/api/users/me'),
  updateProfile: (data: { name?: string; avatarEmoji?: string }) =>
    request<UserResponse>('/api/users/me', { method: 'PUT', body: data }),
  changePassword: (currentPassword: string, newPassword: string) =>
    request<{ message: string }>('/api/users/me/password', {
      method: 'PUT',
      body: { currentPassword, newPassword },
    }),
  settings: () => request<UserSettings>('/api/users/me/settings'),
  updateSettings: (data: Partial<UserSettings>) =>
    request<UserSettings>('/api/users/me/settings', { method: 'PUT', body: data }),
};

export type UserSettings = {
  notificationsEnabled: boolean | null;
  theme: string | null;
};

// ---------- Progresso ----------
export type SubjectPerformance = {
  subjectId: number;
  subjectName: string;
  icon: string;
  colorKey: string;
  primaryColor: string;
  questionsAnswered: number;
  correctAnswers: number;
  accuracyRate: number;
  hasData: boolean;
};

export type ProgressOverview = {
  overallAccuracyRate: number;
  evaluationsCount: number;
  topicsStudied: number;
  streakDays: number;
  subjects: SubjectPerformance[];
  bestSubject: SubjectPerformance | null;
  needsReview: SubjectPerformance | null;
};

export const performanceApi = {
  overview: () => request<ProgressOverview>('/api/performance/overview'),
};

// ---------- Agenda ----------
export type ScheduleResponse = {
  id: number;
  title: string;
  subjectName: string | null;
  colorKey: string | null;
  icon: string | null;
  daysAhead: number;
  scheduledTime: string | null;
  completed: boolean;
  scheduledDate: string | null;
  subjectId: number | null;
  topicId: number | null;
};

export const schedulesApi = {
  list: () => request<ScheduleResponse[]>('/api/schedules'),
  create: (data: {
    title: string;
    subjectId?: number;
    topicId?: number;
    scheduledDate?: string;
    scheduledTime?: string;
  }) => request<ScheduleResponse>('/api/schedules', { method: 'POST', body: data }),
  toggle: (id: number | string) =>
    request<ScheduleResponse>(`/api/schedules/${id}/toggle`, { method: 'PATCH' }),
  remove: (id: number | string) =>
    request<void>(`/api/schedules/${id}`, { method: 'DELETE' }),
};

// ---------- Chat IA ----------
export type MensagemChat = { role: 'user' | 'assistant'; content: string };

export type ExplicacaoIA = {
  answer: string;
  topicName: string | null;
  modelProvider: string | null;
  suggestedQuestions: string[] | null;
};

export const aiApi = {
  explicarAssunto: (topicId: number, titulo: string) =>
    request<ExplicacaoIA>('/api/ai/ask', {
      method: 'POST',
      body: {
        topicId,
        question: `Explique de forma didática e resumida o assunto "${titulo}", com os pontos principais e um exemplo.`,
      },
    }),
  chat: async (messages: MensagemChat[], subjectId?: number) => {
    const res = await request<{ reply: string }>('/api/ai/chat', {
      method: 'POST',
      body: { messages: messages.slice(-12), subjectId },
    });
    return res.reply.trim();
  },
};

// ---------- Simulados ----------
export type QuizDetail = {
  id: number;
  title: string;
  totalQuestions: number;
  questions: { id: number; text: string; options: { id: number; text: string }[] }[];
};

export type QuizResult = {
  quizId: number;
  correctAnswers: number;
  totalQuestions: number;
  percentage: number;
  results: {
    questionId: number;
    questionText: string;
    selectedOptionText: string | null;
    correctOptionText: string;
    isCorrect: boolean;
    explanation: string | null;
  }[];
  fortes: string[];
  fracos: string[];
};

export const quizzesApi = {
  generate: (data: { topicId?: number; subjectId?: number; questionCount?: number }) =>
    request<QuizDetail>('/api/quizzes/generate', { method: 'POST', body: data }),
  submit: (id: number, answers: { questionId: number; selectedOptionId: number }[]) =>
    request<QuizResult>(`/api/quizzes/${id}/submit`, { method: 'POST', body: { answers } }),
};