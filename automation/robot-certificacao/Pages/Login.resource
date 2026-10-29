*** Settings ***
Resource    ../Resources/common.resource

*** Keywords ***
Confirmar Tela De Entrada
    Esperar Alvo SIMLO    login.experimente
    Esperar Alvo SIMLO    login.entrar

Entrar Como Visitante
    Confirmar Tela De Entrada
    Tocar Alvo SIMLO    login.experimente
    Esperar Alvo SIMLO    home.menu
    Esperar Alvo SIMLO    home.apostar

Confirmar Opcao De Biometria
    Esperar Alvo SIMLO    login.biometria
