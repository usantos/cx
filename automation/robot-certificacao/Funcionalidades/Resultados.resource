*** Settings ***
Resource    Login.resource

*** Keywords ***
A pessoa consulta resultados publicos
    Abrir Modalidades De Resultados

A lista de modalidades deve aparecer
    Esperar Alvo SIMLO    resultados.modalidades

A pessoa retorna e encerra o acesso
    Voltar Dos Resultados Para Home
    Sair Do Modo Visitante
