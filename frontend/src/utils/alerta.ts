import { Alert, Platform } from 'react-native';

declare const window: {
  alert: (mensagem: string) => void;
  confirm: (mensagem: string) => boolean;
};

// No navegador o Alert do React Native não abre nada, então usamos o do próprio navegador.
export function avisar(titulo: string, mensagem?: string) {
  if (Platform.OS === 'web') {
    window.alert(mensagem ? `${titulo}\n\n${mensagem}` : titulo);
    return;
  }
  Alert.alert(titulo, mensagem);
}

export function confirmar(
  titulo: string,
  mensagem: string,
  rotuloConfirmar = 'Confirmar',
  rotuloCancelar = 'Cancelar',
): Promise<boolean> {
  if (Platform.OS === 'web') {
    return Promise.resolve(window.confirm(`${titulo}\n\n${mensagem}`));
  }
  return new Promise((resolve) => {
    Alert.alert(
      titulo,
      mensagem,
      [
        { text: rotuloCancelar, style: 'cancel', onPress: () => resolve(false) },
        { text: rotuloConfirmar, onPress: () => resolve(true) },
      ],
      { cancelable: true, onDismiss: () => resolve(false) },
    );
  });
}
