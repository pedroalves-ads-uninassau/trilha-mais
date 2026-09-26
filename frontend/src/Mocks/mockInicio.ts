import {
  ContinuarEstudando,
  EstatisticaRapida,
  Materia,
  ProgressoGeral,
} from '../types/inicio';

export const nomeUsuario = '';

export const mockProgressoGeral: ProgressoGeral = {
  porcentagem: 37,
  diasSeguidos: 5,
  mensagem: 'Você está indo bem!',
};

export const mockEstatisticasRapidas: EstatisticaRapida[] = [
  { id: 'materias', icone: 'book-copy', valor: 4, rotulo: 'Matérias' },
  { id: 'assuntos', icone: 'file-text', valor: 26, rotulo: 'Assuntos' },
  { id: 'avaliacoes', icone: 'check-square', valor: 8, rotulo: 'Avaliações' },
];

export const mockMaterias: Materia[] = [
  {
    id: 'portugues',
    nome: 'Português',
    chaveCor: 'portugues',
    quantidadeAssuntos: 5,
    progresso: 20,
    concluidos: 1,
    pendentes: 3,
  },
  {
    id: 'matematica',
    nome: 'Matemática',
    chaveCor: 'matematica',
    quantidadeAssuntos: 8,
    progresso: 38,
    concluidos: 3,
    pendentes: 4,
  },
  {
    id: 'historia',
    nome: 'História',
    chaveCor: 'historia',
    quantidadeAssuntos: 6,
    progresso: 17,
    concluidos: 1,
    pendentes: 5,
  },
  {
    id: 'biologia',
    nome: 'Biologia',
    chaveCor: 'biologia',
    quantidadeAssuntos: 7,
    progresso: 71,
    concluidos: 5,
    pendentes: 2,
  },
];

export const mockContinuarEstudando: ContinuarEstudando = {
  nomeMateria: 'Português',
  nomeAssunto: 'Variações linguísticas',
  chaveCor: 'portugues',
};
