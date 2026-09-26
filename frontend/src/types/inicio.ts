import { ChaveCorMateria } from '../theme/CoresMaterias';

export interface ProgressoGeral {
  porcentagem: number; // 0 a 100
  diasSeguidos: number;
  mensagem: string;
}

export interface EstatisticaRapida {
  id: string;
  icone: string; // por enquanto uma chave de emoji, ver Mocks/mockInicio.ts
  valor: number;
  rotulo: string;
}

export interface Materia {
  id: string;
  nome: string;
  chaveCor: ChaveCorMateria;
  quantidadeAssuntos: number;
  progresso: number; // 0 a 100
  concluidos: number;
  pendentes: number;
}

export interface ContinuarEstudando {
  nomeMateria: string;
  nomeAssunto: string;
  chaveCor: ChaveCorMateria;
}
