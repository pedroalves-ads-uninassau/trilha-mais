import { View, Text } from "react-native";
import { materiasMock } from "../Mocks/materias";

export default function Materias() {
  return (
    <View>
      {materiasMock.map((materia) => (
        <Text key={materia.id}>{materia.nome}</Text>
      ))}
    </View>
  );
}