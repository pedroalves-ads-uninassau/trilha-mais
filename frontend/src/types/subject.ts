export interface Subject {
  id: string;
  name: string;
  /** Emoji do ícone da matéria (ex.: "📒"). */
  icon: string;
  /** Cor de destaque da matéria (borda, barra e porcentagem). */
  color: string;
  topicsTotal: number;
  topicsDone: number;
  topicsInProgress: number;
}

export interface ContinueTopic {
  subjectId: string;
  topicName: string;
  subjectName: string;
  subjectIcon: string;
  status: string; // ex.: "Em andamento"
}

export type TopicStatus = "done" | "in_progress" | "pending";

export interface Topic {
  id: string;
  name: string;
  status: TopicStatus;
}
