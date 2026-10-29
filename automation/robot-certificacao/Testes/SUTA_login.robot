*** Settings ***
Documentation    Entrada e regressao da saida anonima. Pre-condicao: APK atualizado, tela desbloqueada e sem sessao/biometria lembrada.
Resource    ../Funcionalidades/Login.resource
Test Setup    Iniciar Teste SIMLO
Test Teardown    Finalizar Teste SIMLO
Test Tags    smoke    visitante    sem_compra

*** Test Cases ***
CTA_01_Exibir_Opcoes_De_Entrada
    Dado O aplicativo esta na entrada sem sessao lembrada
    Então As opcoes de entrada estao disponiveis

CTA_02_Experimentar_E_Sair_Sem_Erro_De_Logout
    Dado O aplicativo esta na entrada sem sessao lembrada
    Quando A pessoa escolhe experimentar o aplicativo
    Então A home publica deve estar acessivel
    Quando A pessoa encerra o acesso de visitante
    Então O aplicativo deve retornar ao login
