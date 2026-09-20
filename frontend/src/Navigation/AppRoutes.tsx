import { NavigationContainer } from "@react-navigation/native";
import { createNativeStackNavigator } from "@react-navigation/native-stack";
import Login from "../pages/Login";
import Home from "../pages/Home";
import Materias from "../pages/Materias";
import Assuntos from "../pages/Assuntos";
import Chat from "../pages/Chat";
import Avaliacao from "../pages/Avaliacao";
import Resultado from "../pages/Resultado";
import Perfil from "../pages/Perfil";

const Stack = createNativeStackNavigator();

export default function AppRoutes() {
  return (
    <NavigationContainer>
      <Stack.Navigator initialRouteName="Login">
        <Stack.Screen name="Login" component={Login} />
        <Stack.Screen name="Home" component={Home} />
        <Stack.Screen name="Materias" component={Materias} />
        <Stack.Screen name="Assuntos" component={Assuntos} />
        <Stack.Screen name="Chat" component={Chat} />
        <Stack.Screen name="Avaliacao" component={Avaliacao} />
        <Stack.Screen name="Resultado" component={Resultado} />
        <Stack.Screen name="Perfil" component={Perfil} />
      </Stack.Navigator>
    </NavigationContainer>
  );
}