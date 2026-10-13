package br.gov.caixa.loterias.core.auth

/** Fixed synthetic session for explicitly guarded debug/mock visual captures only. */
object SyntheticSessionFixture {
    fun install(store: SessionTokenStore): Boolean = store.installVisualFixtureIfEmpty(
        "e30.eyJuYW1lIjoiQXBvc3RhZG9yIGRlIHRlc3RlIiwiY3BmIjoiMDAwMDAwMDAwMDAiLCJlbWFpbCI6InRlc3RlQGV4YW1wbGUuaW52YWxpZCIsImRhdGFOYXNjaW1lbnRvIjoiMDEvMDEvMTk5MCIsImV4cCI6NDEwMjQ0NDgwMH0.visual-fixture",
    )
}
