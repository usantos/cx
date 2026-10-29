package br.gov.caixa.loterias.core.network.mock.fixture

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

internal object MockCartFixtures {
    fun cart(empty: Boolean = false, expiredPool: Boolean = false) = if (empty) {
        "{\"id\":9,\"valorTotal\":0,\"apostasIndividuais\":[],\"cotas\":[],\"combos\":[]}"
    } else """{"id":9,"valorTotal":35,"msgCotaExcluida":$expiredPool,"apostasIndividuais":[
        {"id":12,"modalidade":"MEGA_SENA","numerosSelecionados":[1,2,3,4,5,6],"valor":5,"quantidadeApostas":1}],
        "cotas":[],"combos":[]}"""

    fun poolCart(): String {
        val expiresAt = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"))
            .plusMinutes(15)
            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        return """{"id":9,"valorTotal":25,"apostasIndividuais":[],"combos":[],"cotas":[{
            "id":31,"modalidade":"MEGA_SENA","valor":25,"concursoAlvo":2900,
            "reservaCotaBolao":{"codigoBolaoReserva":"B1","numeroCotaReservada":1,
            "vrTotalCota":25,"qtdCotaTotalBolao":10,"dataHoraExpiracaoReserva":"$expiresAt"}}]}"""
    }

    fun favoriteCartBets() = """[{"id":31,"modalidade":{"valor":2,"descricao":"Mega-Sena",
        "descricaoEspecial":"Mega da Virada"},"numerosSelecionados":[1,2,3,4,5,6],
        "quantidadeTeimosinhas":2,"espelho":false,"indicadorSurpresinha":"NAO_SURPRESINHA"}]"""
}
