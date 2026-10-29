*** Settings ***
Resource    ../Pages/Login.resource
Resource    ../Pages/Home.resource

*** Keywords ***
O aplicativo esta na entrada sem sessao lembrada
    Confirmar Tela De Entrada
    Confirmar Ausencia Do Alvo SIMLO    login.trocar_usuario

As opcoes de entrada estao disponiveis
    Confirmar Tela De Entrada
    Confirmar Opcao De Biometria

A pessoa escolhe experimentar o aplicativo
    Entrar Como Visitante

A home publica deve estar acessivel
    Esperar Alvo SIMLO    home.menu
    Esperar Alvo SIMLO    home.apostar

A pessoa encerra o acesso de visitante
    Sair Do Modo Visitante

O aplicativo deve retornar ao login
    Confirmar Tela De Entrada
    Confirmar Ausencia Do Alvo SIMLO    home.menu
