# Suavez

Aplicativo Android minimalista de lembretes a cada **10 dias**, a partir da data que a pessoa registra.

- Uma tela: quantos dias faltam e a próxima data.
- Um toque para registrar que a aplicação ocorreu hoje.
- Notificação local às 9h no dia programado (o Android pode atrasar o aviso para economizar bateria).
- Os dados ficam **somente no celular**; sem login, internet, publicidade ou servidor.
- A notificação aparece apenas como **Suavez**, sem identificar o tratamento na tela bloqueada.

## Instalação no Android

Baixe o arquivo **suavez.apk** na página de versões deste repositório e instale no celular. Na primeira abertura, defina a data da última aplicação e autorize as notificações. Pode ser necessário permitir a instalação de apps desta origem no Android.

## Código e compilação

O projeto nativo está em `suavez/android`. O GitHub Actions compila o APK e publica uma versão no GitHub Releases. A construção usa Android Gradle Plugin 8.8.2, Gradle 8.10.2 e Java 17.

> Este app é apenas um lembrete. Não define dose, frequência médica nem recomenda tomar medicamentos. Configure o intervalo de 10 dias somente se já for o prescrito pelo profissional responsável. 
