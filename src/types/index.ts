export type SubjectStatus = 'concluido' | 'andamento' | 'pendente';

export interface Topic {
  id: string;
  title: string;
  status: SubjectStatus;
}

export interface Subject {
  id: string;
  name: string;
  icon: string;
  topicsCount: number;
  concluded: number;
  inProgress: number;
  pending: number;
  progressPercent: number;
  topics: Topic[];
}

export interface QuizOption {
  id: string; // 'A' | 'B' | 'C' | 'D'
  text: string;
}

export interface QuizQuestion {
  id: string;
  prompt: string;
  options: QuizOption[];
  correctOptionId: string;
}

export interface Quiz {
  subjectTopic: string;
  questions: QuizQuestion[];
}

export interface UserProfile {
  name: string;
  email: string;
  studyStreak: number;
  evaluationsCount: number;
  subjectsCount: number;
}

export interface SignUpFormData {
  fullName: string;
  email: string;
  password: string;
  confirmPassword: string;
}
