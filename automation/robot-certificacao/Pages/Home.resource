*** Settings ***
Resource    ../Resources/common.resource
Resource    Login.resource

*** Keywords ***
Sair Do Modo Visitante
    Tocar Alvo SIMLO    home.menu
    Tocar Alvo SIMLO    menu.sair
    Tocar Alvo SIMLO    dialogo.positivo
    Confirmar Tela De Entrada
    Confirmar Ausencia Do Alvo SIMLO    home.menu

Abrir Modalidades De Resultados
    Tocar Alvo SIMLO    home.resultados
    Esperar Alvo SIMLO    resultados.modalidades

Voltar Dos Resultados Para Home
    Go Back
    Esperar Alvo SIMLO    home.menu
