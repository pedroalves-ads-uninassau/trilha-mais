import React from 'react';
import { NavigationContainer } from "@react-navigation/native";
import { createNativeStackNavigator } from "@react-navigation/native-stack";
import Login from "../pages/TelaLogin";
import Home from "../pages/Home";
import Agenda from "../pages/Agenda";
import Materias from "../pages/Materias";
import Materia from "../pages/Materia";
import Assuntos from "../pages/Assuntos";
import Perfil from "../pages/Perfil";
import Progresso from "../pages/Progresso";
import Cadastro from "../pages/CadastroScreen";
import RecuperacaoSenha from "../pages/RecuperacaoSenha";
import ChatIAScreen from '../pages/ChatIAScreen';
import QuizScreen from '../pages/TelaQuiz';
import ResultadoScreen from '../pages/TelaResultado';

const Stack = createNativeStackNavigator();

export default function AppRoutes() {
  return (
    <NavigationContainer>
      <Stack.Navigator initialRouteName="Login" screenOptions={{ headerShown: false }}>
        <Stack.Screen name="Login" component={Login} />
        <Stack.Screen name="Cadastro" component={Cadastro} />
        <Stack.Screen name="RecuperacaoSenha" component={RecuperacaoSenha} />
        <Stack.Screen name="Home" component={Home} />
        <Stack.Screen name="Agenda" component={Agenda} />
        <Stack.Screen name="Materias" component={Materias} />
        <Stack.Screen name="Materia" component={Materia} />
        <Stack.Screen name="Assuntos" component={Assuntos} />
        <Stack.Screen name="Perfil" component={Perfil} />
        <Stack.Screen name="Progresso" component={Progresso} />

        {/* Rotas de Chat com IA: suporta ChatIAScreen, ChatIA e Chat para evitar erros de navegação */}
        <Stack.Screen name="ChatIAScreen">
          {({ navigation }) => <ChatIAScreen onBack={() => navigation.goBack()} />}
        </Stack.Screen>
        <Stack.Screen name="ChatIA">
          {({ navigation }) => <ChatIAScreen onBack={() => navigation.goBack()} />}
        </Stack.Screen>
        <Stack.Screen name="Chat">
          {({ navigation }) => <ChatIAScreen onBack={() => navigation.goBack()} />}
        </Stack.Screen>

        {/* Tela de Quiz interativo */}
        <Stack.Screen name="Quiz">
          {({ navigation }) => (
            <QuizScreen
              onClose={() => navigation.goBack()}
              onFinish={(resultado) => navigation.replace('Resultado', { resultado })}
            />
          )}
        </Stack.Screen>

        {/* Tela de Resultado com revisão e pontos fortes/fracos */}
        <Stack.Screen name="Resultado">
          {({ route, navigation }) => (
            <ResultadoScreen
              resultado={(route.params as any)?.resultado}
              onRefazer={() => navigation.replace('Quiz')}
              onDesempenho={() => navigation.navigate('Progresso')}
              onVoltar={() => navigation.navigate('Home')}
            />
          )}
        </Stack.Screen>
      </Stack.Navigator>
    </NavigationContainer>
  );
}