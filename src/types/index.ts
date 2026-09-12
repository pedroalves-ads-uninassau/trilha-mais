export interface Materia {
  id: string;
  nome: string;
  totalAssuntos: number;
}

export interface Assunto {
  id: string;
  materiaId: string;
  titulo: string;
  concluido: boolean;
}