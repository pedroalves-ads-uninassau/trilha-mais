import React from 'react';
import { Platform, StyleSheet, View } from 'react-native';
import AppRoutes from './src/Navigation/AppRoutes';

// O Trilha+ foi desenhado para celular. No navegador (versão do Vercel) ele fica
// centralizado em uma coluna com largura de celular, em vez de esticar na tela toda.
export default function App() {
  if (Platform.OS !== 'web') {
    return <AppRoutes />;
  }

  return (
    <View style={styles.fundoWeb}>
      <View style={styles.colunaApp}>
        <AppRoutes />
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  fundoWeb: {
    flex: 1,
    alignItems: 'center',
    backgroundColor: '#E8ECF4',
  },
  colunaApp: {
    flex: 1,
    width: '100%',
    maxWidth: 480,
    backgroundColor: '#FFFFFF',
    overflow: 'hidden',
  },
});
