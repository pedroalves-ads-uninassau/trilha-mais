import { View, Text, Button } from "react-native";
import { NativeStackScreenProps } from "@react-navigation/native-stack";

export default function Login({ navigation }: any) {
  return (
    <View>
      <Text>Tela de Login</Text>
      <Button title="Ir para Home" onPress={() => navigation.navigate("Home")} />
    </View>
  );
}