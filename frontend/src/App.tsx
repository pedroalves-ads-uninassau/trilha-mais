import React, { useState } from 'react';
import SignUpScreen from './components/SignUpScreen';
import SubjectScreen from './components/SubjectScreen';
import QuizScreen from './components/QuizScreen';
import ProfileScreen from './components/ProfileScreen';
import type { Subject, Quiz, UserProfile, SignUpFormData, Topic } from './types';

type Screen = 'signup' | 'subject' | 'quiz' | 'profile';

// Dados de exemplo — refletem o conteúdo mostrado no protótipo (matéria de Português)
const PORTUGUES_SUBJECT: Subject = {
  id: 'portugues',
  name: 'Português',
  icon: '📘',
  topicsCount: 5,
  concluded: 1,
  inProgress: 1,
  pending: 3,
  progressPercent: 20,
  topics: [
    { id: 't1', title: 'Variações linguísticas', status: 'andamento' },
    { id: 't2', title: 'Figuras de linguagem', status: 'concluido' },
    { id: 't3', title: 'Classes gramaticais', status: 'pendente' },
    { id: 't4', title: 'Interpretação de texto', status: 'pendente' },
    { id: 't5', title: 'Concordância verbal', status: 'pendente' },
  ],
};

const VARIACOES_QUIZ: Quiz = {
  subjectTopic: 'Variações linguísticas',
  questions: [
    {
      id: 'q10',
      prompt: 'O preconceito linguístico acontece quando:',
      options: [
        { id: 'A', text: 'Um falante adapta sua linguagem ao contexto' },
        { id: 'B', text: 'Uma variante é considerada inferior por questões sociais ou regionais' },
        { id: 'C', text: 'A língua evolui e incorpora novos termos' },
        { id: 'D', text: 'Um falante usa a norma-padrão em contextos formais' },
      ],
      correctOptionId: 'B',
    },
  ],
};

const PROFILE: UserProfile = {
  name: 'Luiz Henrique',
  email: 'luiz@email.com',
  studyStreak: 5,
  evaluationsCount: 8,
  subjectsCount: 4,
};

const PHONE_FRAME: React.CSSProperties = {
  width: 375,
  margin: '32px auto',
  border: '1px solid #E4E7EC',
  borderRadius: 32,
  overflow: 'hidden',
  boxShadow: '0 20px 40px rgba(0,0,0,0.12)',
  minHeight: 720,
};

export default function App() {
  const [screen, setScreen] = useState<Screen>('signup');
  const [quizIndex, setQuizIndex] = useState(0);

  const handleSignUp = (data: SignUpFormData) => {
    console.log('Conta criada:', data);
    setScreen('subject');
  };

  const handleSelectTopic = (topic: Topic) => {
    console.log('Assunto selecionado:', topic.title);
    setScreen('quiz');
  };

  const handleFinishQuiz = (answers: Record<string, string>) => {
    console.log('Avaliação finalizada:', answers);
    setScreen('profile');
  };

  return (
    <div style={PHONE_FRAME}>
      {screen === 'signup' && <SignUpScreen onSubmit={handleSignUp} />}

      {screen === 'subject' && (
        <SubjectScreen
          subject={PORTUGUES_SUBJECT}
          onSelectTopic={handleSelectTopic}
          onAddTopic={() => console.log('Novo assunto')}
        />
      )}

      {screen === 'quiz' && (
        <QuizScreen
          subjectTopic={VARIACOES_QUIZ.subjectTopic}
          questions={VARIACOES_QUIZ.questions}
          currentIndex={quizIndex}
          onAnswer={(questionId, optionId) => console.log(questionId, optionId)}
          onFinish={handleFinishQuiz}
        />
      )}

      {screen === 'profile' && (
        <ProfileScreen
          profile={PROFILE}
          activeTab="perfil"
          onSelectTab={(tab) => console.log('Aba:', tab)}
          onSelectMenuItem={(item) => console.log('Menu:', item)}
        />
      )}
    </div>
  );
}
