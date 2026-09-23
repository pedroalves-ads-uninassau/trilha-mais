import { useMemo } from "react";
import type { CSSProperties } from "react";
import TopicItem from "../components/TopicItem";
import type { Topic } from "../types/subject";
import "../styles/tokens.css";
import "./SubjectDetail.css";

interface SubjectDetailProps {
  name?: string;
  icon?: string;
  /** Cor da matéria (a mesma usada no card da Home). */
  color?: string;
  /** Vem do back (Allan). Sem isso, usa os dados de exemplo do Figma. */
  topics?: Topic[];
  onBack?: () => void;
  onAddTopic?: () => void;
  onOpenTopic?: (id: string) => void;
}

const MOCK_TOPICS: Topic[] = [
  { id: "1", name: "Variações linguísticas", status: "in_progress" },
  { id: "2", name: "Figuras de linguagem", status: "done" },
  { id: "3", name: "Classes gramaticais", status: "pending" },
  { id: "4", name: "Interpretação de texto", status: "pending" },
  { id: "5", name: "Concordância verbal", status: "pending" },
];

export default function SubjectDetail({
  name = "Português",
  icon = "📒",
  color = "var(--subject-blue)",
  topics = MOCK_TOPICS,
  onBack,
  onAddTopic,
  onOpenTopic,
}: SubjectDetailProps) {
  const { done, inProgress, pending, percent } = useMemo(() => {
    const done = topics.filter((t) => t.status === "done").length;
    const inProgress = topics.filter((t) => t.status === "in_progress").length;
    const pending = topics.length - done - inProgress;
    const percent = topics.length > 0 ? Math.round((done / topics.length) * 100) : 0;
    return { done, inProgress, pending, percent };
  }, [topics]);

  return (
    <div className="subject" style={{ "--subject-color": color } as CSSProperties}>
      <header className="subject__hero">
        <div className="subject__hero-top">
          <button type="button" className="subject__back" onClick={onBack} aria-label="Voltar">
            ‹
          </button>
          <span className="subject__icon" aria-hidden="true">
            {icon}
          </span>
          <div>
            <h1 className="subject__title">{name}</h1>
            <p className="subject__count">{topics.length} assuntos</p>
          </div>
        </div>

        <div className="subject__stats">
          <div className="subject__stat">
            <span className="subject__stat-value">{done}</span>
            <span className="subject__stat-label">Concluídos</span>
          </div>
          <div className="subject__stat">
            <span className="subject__stat-value">{inProgress}</span>
            <span className="subject__stat-label">Andamento</span>
          </div>
          <div className="subject__stat">
            <span className="subject__stat-value">{pending}</span>
            <span className="subject__stat-label">Pendentes</span>
          </div>
        </div>

        <div className="subject__progress-head">
          <span>Progresso</span>
          <span>{percent}%</span>
        </div>
        <div
          className="subject__bar"
          role="progressbar"
          aria-label={`Progresso em ${name}`}
          aria-valuemin={0}
          aria-valuemax={100}
          aria-valuenow={percent}
        >
          <span className="subject__bar-fill" style={{ width: `${percent}%` }} />
        </div>
      </header>

      <main className="subject__content">
        <div className="subject__section-head">
          <h2 className="subject__section-title">Assuntos para estudar</h2>
          <button type="button" className="subject__new" onClick={onAddTopic}>
            + Novo
          </button>
        </div>

        {topics.length === 0 ? (
          <p className="subject__empty">
            Esta matéria ainda não tem assuntos. Toque em “Novo” para adicionar o primeiro.
          </p>
        ) : (
          <div className="subject__list">
            {topics.map((topic) => (
              <TopicItem key={topic.id} topic={topic} onOpen={onOpenTopic} />
            ))}
          </div>
        )}
      </main>
    </div>
  );
}
