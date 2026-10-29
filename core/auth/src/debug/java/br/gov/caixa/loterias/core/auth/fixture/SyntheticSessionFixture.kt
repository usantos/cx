package br.gov.caixa.loterias.core.auth.fixture

import br.gov.caixa.loterias.core.auth.session.SessionTokenStore

/** Sessão sintética fixa apenas para capturas visuais de depuração/simulação com proteção explícita. */
object SyntheticSessionFixture {
    fun install(store: SessionTokenStore): Boolean = store.installVisualFixtureIfEmpty(
        "e30.eyJuYW1lIjoiQXBvc3RhZG9yIGRlIHRlc3RlIiwiY3BmIjoiMDAwMDAwMDAwMDAiLCJlbWFpbCI6InRlc3RlQGV4YW1wbGUuaW52YWxpZCIsImRhdGFOYXNjaW1lbnRvIjoiMDEvMDEvMTk5MCIsImV4cCI6NDEwMjQ0NDgwMH0.visual-fixture",
    )
}
