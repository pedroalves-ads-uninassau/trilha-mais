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

export const materiasDetalhadas = [
  {
    id: '1',
    nome: 'Português',
    chaveCor: 'portugues', // <--- Adicionado para corrigir o erro no CardMateria
    corPrimaria: '#2563EB', 
    icone: '📔',
    assuntosTotal: 5,
    concluidos: 1,
    emAndamento: 1,
    pendentes: 3,
    progresso: 20,
    assuntosLista: [
      { id: 'p1', titulo: 'Variações linguísticas', status: 'andamento' },
      { id: 'p2', titulo: 'Figuras de linguagem', status: 'concluido' },
      { id: 'p3', titulo: 'Classes gramaticais', status: 'pendente' },
      { id: 'p4', titulo: 'Interpretação de texto', status: 'pendente' },
      { id: 'p5', titulo: 'Concordância verbal', status: 'pendente' },
    ]
  },
  {
    id: '2',
    nome: 'Matemática',
    chaveCor: 'matematica', // <--- Adicionado
    corPrimaria: '#8B5CF6', 
    icone: '📐',
    assuntosTotal: 8,
    concluidos: 3,
    emAndamento: 1,
    pendentes: 4,
    progresso: 38,
    assuntosLista: [
      { id: 'm1', titulo: 'Função quadrática', status: 'andamento' },
      { id: 'm2', titulo: 'Equações', status: 'concluido' },
      { id: 'm3', titulo: 'Porcentagem', status: 'concluido' },
      { id: 'm4', titulo: 'Progressão aritmética', status: 'pendente' },
      { id: 'm5', titulo: 'Probabilidade', status: 'pendente' },
      { id: 'm6', titulo: 'Matrizes', status: 'pendente' },
    ]
  },
  {
    id: '3',
    nome: 'História',
    chaveCor: 'historia', // <--- Adicionado
    corPrimaria: '#EA580C', 
    icone: '📗',
    assuntosTotal: 6,
    concluidos: 1,
    emAndamento: 0,
    pendentes: 5,
    progresso: 17,
    assuntosLista: [
      { id: 'h1', titulo: 'Brasil Colônia', status: 'concluido' },
      { id: 'h2', titulo: 'Revolução Industrial', status: 'pendente' },
      { id: 'h3', titulo: 'Primeira Guerra Mundial', status: 'pendente' },
      { id: 'h4', titulo: 'Segunda Guerra Mundial', status: 'pendente' },
      { id: 'h5', titulo: 'República Velha', status: 'pendente' },
    ]
  },
  {
    id: '4',
    nome: 'Biologia',
    chaveCor: 'biologia', // <--- Adicionado
    corPrimaria: '#10B981', 
    icone: '🔬',
    assuntosTotal: 7,
    concluidos: 5,
    emAndamento: 0,
    pendentes: 2,
    progresso: 71,
    assuntosLista: [
      { id: 'b1', titulo: 'Citologia', status: 'concluido' },
      { id: 'b2', titulo: 'Genética', status: 'concluido' },
      { id: 'b3', titulo: 'Ecologia', status: 'concluido' },
      { id: 'b4', titulo: 'Fisiologia humana', status: 'concluido' },
      { id: 'b5', titulo: 'Botânica', status: 'concluido' },
      { id: 'b6', titulo: 'Evolução', status: 'pendente' },
    ]
  }
];
