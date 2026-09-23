import { useMemo } from "react";
import ContinueCard from "../components/ContinueCard";
import StatCard from "../components/StatCard";
import SubjectCard from "../components/SubjectCard";
import type { ContinueTopic, Subject } from "../types/subject";
import "../styles/tokens.css";
import "./Home.css";

interface HomeProps {
  userName?: string;
  streakDays?: number;
  assessmentsCount?: number;
  /** Vem do back (Allan). Sem isso, usa os dados de exemplo do Figma. */
  subjects?: Subject[];
  continueTopic?: ContinueTopic | null;
  onOpenSubject?: (id: string) => void;
  onSeeAllSubjects?: () => void;
  onContinue?: (topic: ContinueTopic) => void;
  onOpenProfile?: () => void;
}

const MOCK_SUBJECTS: Subject[] = [
  { id: "port", name: "Português", icon: "📒", color: "var(--subject-blue)", topicsTotal: 5, topicsDone: 1, topicsInProgress: 1 },
  { id: "mat", name: "Matemática", icon: "📐", color: "var(--subject-purple)", topicsTotal: 8, topicsDone: 3, topicsInProgress: 1 },
  { id: "hist", name: "História", icon: "📖", color: "var(--subject-amber)", topicsTotal: 6, topicsDone: 1, topicsInProgress: 0 },
  { id: "bio", name: "Biologia", icon: "🔬", color: "var(--subject-green)", topicsTotal: 7, topicsDone: 5, topicsInProgress: 0 },
];

const MOCK_CONTINUE: ContinueTopic = {
  subjectId: "port",
  topicName: "Variações linguísticas",
  subjectName: "Português",
  subjectIcon: "📒",
  status: "Em andamento",
};

export default function Home({
  userName = "Luiz Henrique",
  streakDays = 5,
  assessmentsCount = 8,
  subjects = MOCK_SUBJECTS,
  continueTopic = MOCK_CONTINUE,
  onOpenSubject,
  onSeeAllSubjects,
  onContinue,
  onOpenProfile,
}: HomeProps) {
  const { percent, totalTopics } = useMemo(() => {
    const done = subjects.reduce((acc, s) => acc + s.topicsDone, 0);
    const total = subjects.reduce((acc, s) => acc + s.topicsTotal, 0);
    return { percent: total > 0 ? Math.round((done / total) * 100) : 0, totalTopics: total };
  }, [subjects]);

  return (
    <div className="home">
      <header className="home__hero">
        <div className="home__hero-top">
          <div>
            <p className="home__hello">Olá, {userName} 👋</p>
            <h1 className="home__title">Vamos continuar seus estudos?</h1>
          </div>
          <button
            type="button"
            className="home__profile"
            onClick={onOpenProfile}
            aria-label="Abrir perfil"
          >
            🎓
          </button>
        </div>

        <section className="progress-card" aria-label="Progresso geral">
          <div className="progress-card__row">
            <div>
              <p className="progress-card__label">Progresso geral</p>
              <p className="progress-card__value">{percent}%</p>
            </div>
            <div className="progress-card__side">
              <p className="progress-card__streak">🔥 {streakDays} dias seguidos</p>
              <p className="progress-card__hint">Você está indo bem!</p>
            </div>
          </div>
          <div
            className="progress-card__bar"
            role="progressbar"
            aria-label="Progresso geral"
            aria-valuemin={0}
            aria-valuemax={100}
            aria-valuenow={percent}
          >
            <span className="progress-card__bar-fill" style={{ width: `${percent}%` }} />
          </div>
        </section>
      </header>

      <main className="home__content">
        <section className="home__stats" aria-label="Resumo">
          <StatCard icon="📚" value={subjects.length} label="Matérias" />
          <StatCard icon="📝" value={totalTopics} label="Assuntos" />
          <StatCard icon="✅" value={assessmentsCount} label="Avaliações" />
        </section>

        <section aria-labelledby="subjects-title">
          <div className="home__section-head">
            <h2 id="subjects-title" className="home__section-title">
              Minhas matérias
            </h2>
            <button type="button" className="home__link" onClick={onSeeAllSubjects}>
              Ver todas
            </button>
          </div>

          {subjects.length === 0 ? (
            <p className="home__empty">
              Você ainda não tem matérias. Adicione a primeira para começar a estudar.
            </p>
          ) : (
            <div className="home__list">
              {subjects.map((subject) => (
                <SubjectCard key={subject.id} subject={subject} onOpen={onOpenSubject} />
              ))}
            </div>
          )}
        </section>

        {continueTopic && <ContinueCard topic={continueTopic} onContinue={onContinue} />}
      </main>
    </div>
  );
}
