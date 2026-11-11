import React, { useEffect, useState } from 'react';
import { ActivityIndicator, Platform, View } from 'react-native';
import { NavigationContainer, createNavigationContainerRef } from "@react-navigation/native";
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

import { authApi, definirAoExpirarSessao, usersApi } from '../services/api';

const Stack = createNativeStackNavigator();
const navegacaoRef = createNavigationContainerRef<any>();

export default function AppRoutes() {
  const [rotaInicial, setRotaInicial] = useState<'Login' | 'Home' | null>(null);

  useEffect(() => {
    // Sessão expirada em qualquer tela: volta para o login
    definirAoExpirarSessao(() => {
      if (navegacaoRef.isReady()) {
        navegacaoRef.reset({ index: 0, routes: [{ name: 'Login' }] });
      }
    });

    // Se já existe um token válido salvo, entra direto
    (async () => {
      if (!(await authApi.temSessao())) {
        setRotaInicial('Login');
        return;
      }
      try {
        await usersApi.me();
        setRotaInicial('Home');
      } catch {
        await authApi.logout();
        setRotaInicial('Login');
      }
    })();

    return () => definirAoExpirarSessao(null);
  }, []);

  useEffect(() => {
    // Web: o native-stack esconde a tela anterior com aria-hidden enquanto o botão
    // clicado nela ainda tem o foco, e o navegador avisa "Blocked aria-hidden".
    // Por isso tiramos o foco do elemento ativo antes de a navegação trocar de tela.
    if (Platform.OS !== 'web') return;

    const tirarFoco = () => {
      const ativo = document.activeElement;
      if (ativo instanceof HTMLElement) ativo.blur();
    };

    // O ref guarda o listener e o liga ao NavigationContainer quando ele monta
    navegacaoRef.addListener('__unsafe_action__', tirarFoco);
    return () => navegacaoRef.removeListener('__unsafe_action__', tirarFoco);
  }, []);

  if (!rotaInicial) {
    return (
      <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center', backgroundColor: '#FFFFFF' }}>
        <ActivityIndicator size="large" color="#2563EB" />
      </View>
    );
  }

  return (
    <NavigationContainer ref={navegacaoRef}>
      <Stack.Navigator initialRouteName={rotaInicial} screenOptions={{ headerShown: false }}>
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
          {({ route, navigation }) => {
            const p = (route.params as any) ?? {};
            return (
              <QuizScreen
                topicId={p.topicId}
                subjectId={p.subjectId}
                questionCount={p.totalQuestoes}
                onClose={() => navigation.goBack()}
                onFinish={(resultado) =>
                  navigation.replace('Resultado', { resultado, quizParams: p })
                }
              />
            );
          }}
        </Stack.Screen>

        {/* Tela de Resultado com revisão e pontos fortes/fracos */}
        <Stack.Screen name="Resultado">
          {({ route, navigation }) => (
            <ResultadoScreen
              resultado={(route.params as any)?.resultado}
              onRefazer={() => navigation.replace('Quiz', (route.params as any)?.quizParams)}
              onDesempenho={() => navigation.navigate('Progresso')}
              onVoltar={() => navigation.navigate('Home')}
            />
          )}
        </Stack.Screen>
      </Stack.Navigator>
    </NavigationContainer>
  );
}