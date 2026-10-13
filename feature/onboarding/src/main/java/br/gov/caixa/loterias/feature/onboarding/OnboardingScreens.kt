package br.gov.caixa.loterias.feature.onboarding

import br.gov.caixa.loterias.core.presentation.WorkflowField
import br.gov.caixa.loterias.core.presentation.WorkflowFieldKind
import br.gov.caixa.loterias.core.presentation.WorkflowSpec

val onboardingScreens = listOf(
    WorkflowSpec("onboarding/splash", "Loterias CAIXA", "Carregando configurações e verificando a versão do aplicativo.", primaryAction = "Entrar"),
    WorkflowSpec("onboarding/introduction", "Bem-vindo", "Conheça as apostas, resultados, favoritos e formas de pagamento do Loterias CAIXA.", primaryAction = "Começar"),
    WorkflowSpec("onboarding/tutorial", "Como apostar", "Escolha uma modalidade, marque seus números e confira a aposta antes do pagamento.", primaryAction = "Avançar", secondaryAction = "Pular"),
    WorkflowSpec("onboarding/age", "Jogo responsável", "Este aplicativo é destinado exclusivamente a maiores de 18 anos.", listOf(WorkflowField("adult", "Confirmo que tenho 18 anos ou mais", WorkflowFieldKind.CHECKBOX, true)), "Confirmar"),
    WorkflowSpec("onboarding/update", "Atualização disponível", "Uma nova versão do aplicativo está disponível para manter segurança e compatibilidade.", primaryAction = "Atualizar"),
    WorkflowSpec("onboarding/unavailable", "Aplicativo indisponível", "O serviço está temporariamente indisponível. Tente novamente em alguns instantes.", primaryAction = "Tentar novamente"),
)
