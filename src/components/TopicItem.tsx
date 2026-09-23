import type { Topic, TopicStatus } from "../types/subject";

interface TopicItemProps {
  topic: Topic;
  onOpen?: (id: string) => void;
}

const STATUS: Record<TopicStatus, { label: string; glyph: string }> = {
  done: { label: "Concluído", glyph: "✓" },
  in_progress: { label: "Em andamento", glyph: "◐" },
  pending: { label: "Pendente", glyph: "○" },
};

export default function TopicItem({ topic, onOpen }: TopicItemProps) {
  const { id, name, status } = topic;
  const { label, glyph } = STATUS[status];

  return (
    <button
      type="button"
      className={`topic-item topic-item--${status}`}
      onClick={() => onOpen?.(id)}
    >
      <span className="topic-item__status" aria-hidden="true">
        {glyph}
      </span>
      <span className="topic-item__text">
        <span className="topic-item__name">{name}</span>
        <span className="topic-item__badge">{label}</span>
      </span>
      <span className="topic-item__chevron" aria-hidden="true">
        ›
      </span>
    </button>
  );
}
