# 🤖 Geny Assistant

O **Geny Assistant** é um assistente de voz inteligente e moderno, construído sobre a tecnologia **LiveKit Agents** e integrado com **Firebase**. Ele oferece uma interface néon azul elegante, baixa latência de resposta e recursos avançados de automação.

## 🚀 Funcionalidades Principais

*   **Interface Néon Azul**: Identidade visual moderna com tema escuro e elementos brilhantes.
*   **Voz Personalizada**: Agente IA com persona masculina chamada "Geny".
*   **Indicadores de Status**: Veja em tempo real se o Geny está *Ouvindo*, *Pensando* ou *Falando*.
*   **Histórico Local**: Suas conversas são salvas localmente para consulta rápida.
*   **CI/CD Avançado**: 
    *   Builds de Debug automáticos a cada commit.
    *   Releases manuais com assinatura digital e geração de Changelog.
*   **Firebase Integration**: Analytics, Crashlytics e Cloud Messaging (Notificações) integrados.
*   **Verificação de Atualizações**: O app avisa automaticamente quando uma nova versão está disponível no GitHub.

## 🛠 Configuração Necessária

Para que o projeto funcione 100%, você precisa configurar os seguintes itens:

### 1. Firebase
*   Substitua o arquivo `app/google-services.json` pelo seu arquivo gerado no [Console do Firebase](https://console.firebase.google.com/).

### 2. GitHub Secrets (para CI/CD)
No seu repositório GitHub, vá em `Settings > Secrets and variables > Actions` e adicione:
*   `KEYSTORE_FILE`: Seu arquivo `.jks` ou `.keystore` convertido para **Base64**.
*   `KEYSTORE_PASSWORD`: Senha do arquivo keystore.
*   `KEY_ALIAS`: Alias da chave.
*   `KEY_PASSWORD`: Senha da chave.

### 3. LiveKit Connection
Configure os dados de conexão em `app/src/main/java/com/genyassistant/TokenExt.kt`:
*   `sandboxID` ou `hardcodedUrl` e `hardcodedToken`.

## 📦 Downloads

As builds podem ser encontradas na aba [Releases](https://github.com/carsaimz/geny-ia-agent/releases).

---
*Desenvolvido com ❤️ por Manus para Carsaimz.*
