*** Settings ***
Library    AppiumLibrary    run_on_failure=No Operation
Library    ${CURDIR}/SimloLibrary.py
Library    Collections
Library    JSONLibrary
Library    OperatingSystem

*** Keywords ***
Iniciar Teste SIMLO
    Abrir Sessao SIMLO

Finalizar Teste SIMLO
    # Evidência de cada caso, inclusive falha; uma captura indisponível não esconde o resultado.
    Run Keyword And Ignore Error    Capture Page Screenshot
    Close All Applications
