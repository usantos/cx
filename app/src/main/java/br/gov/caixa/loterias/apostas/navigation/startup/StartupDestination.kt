package br.gov.caixa.loterias.apostas.navigation.startup

import br.gov.caixa.loterias.core.auth.session.ForegroundSessionStatus

/** Somente uma sessão já estabelecida pela autenticação pode pular a tela de entrada. */
internal fun startupDestination(status: ForegroundSessionStatus, hasIdentity: Boolean, rememberedLoginAvailable: Boolean = false): String =
    when {
        status == ForegroundSessionStatus.ACTIVE && hasIdentity -> "home"

        rememberedLoginAvailable && status != ForegroundSessionStatus.AUTHORIZATION_PENDING ->
            br.gov.caixa.loterias.core.navigation.workflow.LoginNavigation.authorize

        else -> "account/login"
    }
