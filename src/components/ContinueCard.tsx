import type { ContinueTopic } from "../types/subject";

interface ContinueCardProps {
  topic: ContinueTopic;
  onContinue?: (topic: ContinueTopic) => void;
}

export default function ContinueCard({ topic, onContinue }: ContinueCardProps) {
  return (
    <button
      type="button"
      className="continue-card"
      onClick={() => onContinue?.(topic)}
      aria-label={`Continuar ${topic.topicName}, ${topic.subjectName}`}
    >
      <span className="continue-card__text">
        <span className="continue-card__label">Continuar de onde parou</span>
        <span className="continue-card__title">{topic.topicName}</span>
        <span className="continue-card__meta">
          {topic.subjectIcon} {topic.subjectName} · {topic.status}
        </span>
      </span>
      <span className="continue-card__go" aria-hidden="true">
        ›
      </span>
    </button>
  );
}
