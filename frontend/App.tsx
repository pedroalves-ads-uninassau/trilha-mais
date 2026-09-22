import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import LoginScreen from '../frontend/src/pages/LoginScreen'; 
import CadastroScreen from './src/pages/CadastroScreen';

const Stack = createNativeStackNavigator();

export default function AppRoutes() {
  return (
    <NavigationContainer>
      {/* O screenOptions={{ headerShown: false }} remove a barra de título padrão do topo */}
      <Stack.Navigator screenOptions={{ headerShown: false }}>       
        <Stack.Screen name="Login" component={LoginScreen} />
        <Stack.Screen name="Cadastro" component={CadastroScreen} />
      </Stack.Navigator>
    </NavigationContainer>
  );
}


