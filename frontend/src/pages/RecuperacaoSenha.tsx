import React, { useState } from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  KeyboardAvoidingView,
  Platform,
  SafeAreaView,
  ActivityIndicator,
} from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { authApi } from '../services/api';
import { avisar } from '../utils/alerta';

type Etapa = 'email' | 'codigo';

export default function RecuperacaoSenha() {
  const navigation = useNavigation<any>();
  const [etapa, setEtapa] = useState<Etapa>('email');
  const [email, setEmail] = useState<string>('');
  const [codigo, setCodigo] = useState<string>('');
  const [novaSenha, setNovaSenha] = useState<string>('');
  const [confirmarSenha, setConfirmarSenha] = useState<string>('');
  const [erro, setErro] = useState<string>('');
  const [carregando, setCarregando] = useState<boolean>(false);

  const emailLimpo = email.trim().toLowerCase();

  // Etapa 1: pede o código de 6 dígitos para o e-mail informado
  const handleRecuperacao = async () => {
    if (carregando) return;
    if (!/^\S+@\S+\.\S+$/.test(emailLimpo)) {
      setErro('Digite um e-mail válido.');
      return;
    }

    try {
      setCarregando(true);
      setErro('');
      await authApi.forgotPassword(emailLimpo);
      setEtapa('codigo');
    } catch (e: any) {
      setErro(e?.message ?? 'Não foi possível enviar o código.');
    } finally {
      setCarregando(false);
    }
  };

  // Etapa 2: confirma o código e grava a nova senha
  const handleNovaSenha = async () => {
    if (carregando) return;
    if (!codigo.trim()) {
      setErro('Digite o código recebido.');
      return;
    }
    if (novaSenha.length < 6) {
      setErro('A senha deve ter no mínimo 6 caracteres.');
      return;
    }
    if (novaSenha !== confirmarSenha) {
      setErro('As senhas não conferem.');
      return;
    }

    try {
      setCarregando(true);
      setErro('');
      await authApi.resetPassword(emailLimpo, codigo.trim(), novaSenha);
      avisar('Senha alterada', 'Entre com a sua nova senha.');
      navigation.navigate('Login');
    } catch (e: any) {
      setErro(e?.message ?? 'Não foi possível alterar a senha.');
    } finally {
      setCarregando(false);
    }
  };

  const voltar = () => {
    if (etapa === 'codigo') {
      setEtapa('email');
      setErro('');
      return;
    }
    navigation.goBack();
  };

  return (
    <SafeAreaView style={styles.safeArea}>
      <KeyboardAvoidingView
        behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
        style={styles.container}
      >
        <View style={styles.header}>
          <TouchableOpacity onPress={voltar} style={styles.backButton}>
            <Text style={styles.backButtonText}>{'<'}</Text>
          </TouchableOpacity>
        </View>

        <View style={styles.content}>
          <Text style={styles.title}>
            {etapa === 'email' ? 'Recuperar senha' : 'Criar nova senha'}
          </Text>
          <Text style={styles.subtitle}>
            {etapa === 'email'
              ? 'Digite o e-mail cadastrado. Enviaremos um código para você criar uma nova senha.'
              : `Se ${emailLimpo} estiver cadastrado, o código de 6 dígitos foi enviado. Digite o código e a nova senha.`}
          </Text>

          <View style={styles.form}>
            {etapa === 'email' ? (
              <TextInput
                style={styles.input}
                placeholder="E-mail"
                placeholderTextColor="#888"
                keyboardType="email-address"
                autoCapitalize="none"
                autoCorrect={false}
                value={email}
                onChangeText={(texto) => {
                  setEmail(texto);
                  setErro('');
                }}
                onSubmitEditing={handleRecuperacao}
              />
            ) : (
              <>
                <TextInput
                  style={styles.input}
                  placeholder="Código de 6 dígitos"
                  placeholderTextColor="#888"
                  keyboardType="number-pad"
                  maxLength={6}
                  value={codigo}
                  onChangeText={(texto) => {
                    setCodigo(texto.replace(/\D/g, ''));
                    setErro('');
                  }}
                />
                <TextInput
                  style={styles.input}
                  placeholder="Nova senha"
                  placeholderTextColor="#888"
                  secureTextEntry
                  value={novaSenha}
                  onChangeText={(texto) => {
                    setNovaSenha(texto);
                    setErro('');
                  }}
                />
                <TextInput
                  style={styles.input}
                  placeholder="Confirmar nova senha"
                  placeholderTextColor="#888"
                  secureTextEntry
                  value={confirmarSenha}
                  onChangeText={(texto) => {
                    setConfirmarSenha(texto);
                    setErro('');
                  }}
                  onSubmitEditing={handleNovaSenha}
                />
              </>
            )}

            {erro ? <Text style={styles.erroTexto}>{erro}</Text> : null}

            <TouchableOpacity
              style={[styles.button, carregando && { opacity: 0.7 }]}
              onPress={etapa === 'email' ? handleRecuperacao : handleNovaSenha}
              activeOpacity={0.8}
              disabled={carregando}
            >
              {carregando ? (
                <ActivityIndicator color="#FFFFFF" />
              ) : (
                <Text style={styles.buttonText}>
                  {etapa === 'email' ? 'Enviar código' : 'Salvar nova senha'}
                </Text>
              )}
            </TouchableOpacity>

            {etapa === 'codigo' ? (
              <TouchableOpacity onPress={handleRecuperacao} style={styles.reenviar} disabled={carregando}>
                <Text style={styles.reenviarTexto}>Reenviar código</Text>
              </TouchableOpacity>
            ) : null}
          </View>
        </View>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  erroTexto: {
    color: '#DC2626',
    fontSize: 13,
    fontWeight: '600',
    marginTop: -8,
    marginBottom: 16,
  },
  reenviar: {
    alignItems: 'center',
    paddingVertical: 16,
  },
  reenviarTexto: {
    color: '#4299E1',
    fontSize: 14,
    fontWeight: '600',
  },
  safeArea: {
    flex: 1,
    backgroundColor: '#F8F9FA',
  },
  container: {
    flex: 1,
  },
  header: {
    paddingHorizontal: 24,
    paddingTop: 16,
    paddingBottom: 16,
  },
  backButton: {
    padding: 8,
    alignSelf: 'flex-start',
    marginLeft: -8, // Ajuste fino para alinhar com o texto abaixo
  },
  backButtonText: {
    color: '#2D3748',
    fontSize: 24,
    fontWeight: 'bold',
  },
  content: {
    flex: 1,
    paddingHorizontal: 24,
    justifyContent: 'center',
    paddingBottom: 100,
  },
  title: {
    fontSize: 32,
    fontWeight: 'bold',
    color: '#2D3748',
    marginBottom: 12,
  },
  subtitle: {
    fontSize: 16,
    color: '#718096',
    marginBottom: 32,
    lineHeight: 24,
  },
  form: {
    width: '100%',
  },
  input: {
    backgroundColor: '#FFFFFF',
    borderWidth: 1,
    borderColor: '#E2E8F0',
    borderRadius: 12,
    padding: 16,
    fontSize: 16,
    color: '#2D3748',
    marginBottom: 16,
  },
  button: {
    backgroundColor: '#4299E1',
    borderRadius: 12,
    padding: 16,
    alignItems: 'center',
    elevation: 2,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
  },
  buttonText: {
    color: '#FFFFFF',
    fontSize: 16,
    fontWeight: 'bold',
  },
});