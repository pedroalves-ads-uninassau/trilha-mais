import type { CSSProperties } from "react";
import type { Subject } from "../types/subject";

interface SubjectCardProps {
  subject: Subject;
  onOpen?: (id: string) => void;
}

export default function SubjectCard({ subject, onOpen }: SubjectCardProps) {
  const { id, name, icon, color, topicsTotal, topicsDone, topicsInProgress } = subject;
  const pending = Math.max(topicsTotal - topicsDone - topicsInProgress, 0);
  const percent = topicsTotal > 0 ? Math.round((topicsDone / topicsTotal) * 100) : 0;

  return (
    <button
      type="button"
      className="subject-card"
      style={{ "--subject-color": color } as CSSProperties}
      onClick={() => onOpen?.(id)}
    >
      <span className="subject-card__top">
        <span className="subject-card__icon" aria-hidden="true">
          {icon}
        </span>
        <span className="subject-card__info">
          <span className="subject-card__name">{name}</span>
          <span className="subject-card__count">{topicsTotal} assuntos</span>
        </span>
        <span className="subject-card__percent">{percent}%</span>
      </span>

      <span
        className="subject-card__bar"
        role="progressbar"
        aria-label={`Progresso em ${name}`}
        aria-valuemin={0}
        aria-valuemax={100}
        aria-valuenow={percent}
      >
        <span className="subject-card__bar-fill" style={{ width: `${percent}%` }} />
      </span>

      <span className="subject-card__footer">
        <span className="subject-card__done">✓ {topicsDone} concluídos</span>
        <span className="subject-card__pending">○ {pending} pendentes</span>
      </span>
    </button>
  );
}
