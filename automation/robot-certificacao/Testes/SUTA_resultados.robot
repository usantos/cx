*** Settings ***
Documentation    Navegacao publica por resultados; nao depende de usuario autenticado nem efetiva apostas.
Resource    ../Funcionalidades/Resultados.resource
Test Setup    Iniciar Teste SIMLO
Test Teardown    Finalizar Teste SIMLO
Test Tags    smoke    visitante    resultados    sem_compra

*** Test Cases ***
CTA_03_Consultar_Modalidades_De_Resultados_Como_Visitante
    Dado O aplicativo esta na entrada sem sessao lembrada
    Quando A pessoa escolhe experimentar o aplicativo
    E A pessoa consulta resultados publicos
    Então A lista de modalidades deve aparecer
    Quando A pessoa retorna e encerra o acesso
    Então O aplicativo deve retornar ao login
