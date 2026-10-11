import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import LoginScreen from '../frontend/src/pages/LoginScreen'; 
import CadastroScreen from './src/pages/CadastroScreen';
import RecuperacaoSenha from './src/pages/RecuperacaoSenha';
import Home from './src/pages/Home';
import Agenda from './src/pages/Agenda';
import Materias from './src/pages/Materias';
import Materia from './src/pages/Materia';
import Assuntos from './src/pages/Assuntos';
import Perfil from './src/pages/Perfil';
import Progresso from './src/pages/Progresso';

const Stack = createNativeStackNavigator();

export default function AppRoutes() {
  return (
    <NavigationContainer>
      {/* O screenOptions={{ headerShown: false }} remove a barra de título padrão do topo */}
      <Stack.Navigator screenOptions={{ headerShown: false }}>       
        <Stack.Screen name="Login" component={LoginScreen} />
        <Stack.Screen name="Cadastro" component={CadastroScreen} />
        <Stack.Screen name="RecuperacaoSenha" component={RecuperacaoSenha} />
        <Stack.Screen name="Home" component={Home} />
        <Stack.Screen name="Agenda" component={Agenda} />
        <Stack.Screen name="Materias" component={Materias} />
        <Stack.Screen name="Materia" component={Materia} />
        <Stack.Screen name="Assuntos" component={Assuntos} />
        <Stack.Screen name="Perfil" component={Perfil} />
        <Stack.Screen name="Progresso" component={Progresso} />
      </Stack.Navigator>
    </NavigationContainer>
  );
}


