package br.gov.caixa.loterias.core.auth.foreground

/** Retido pela vinculação singleton do Hilt neste processo; não contém Activity, rota nem credenciais. */
class ForegroundSessionBackgroundState {
    var pending = false
    var hadAuthenticatedSessionWhenStopped = false
}
