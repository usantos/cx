package br.gov.caixa.loterias.core.network.mock.response

import br.gov.caixa.loterias.core.network.mock.fixture.MockConfigurationFixtures.appParameters
import br.gov.caixa.loterias.core.network.mock.fixture.MockBettingFixtures.modalities
import br.gov.caixa.loterias.core.network.mock.fixture.MockBettingFixtures.bettingParameters
import br.gov.caixa.loterias.core.network.mock.fixture.MockBettingFixtures.sportsTeamShields
import br.gov.caixa.loterias.core.network.mock.fixture.MockBettingFixtures.combos
import br.gov.caixa.loterias.core.network.mock.fixture.MockBettingFixtures.rapidConfiguration
import br.gov.caixa.loterias.core.network.mock.fixture.MockBettingFixtures.favoriteBetPage
import br.gov.caixa.loterias.core.network.mock.fixture.MockBettingFixtures.groupedFavoriteBets
import br.gov.caixa.loterias.core.network.mock.fixture.MockMarketplaceFixtures.retailers
import br.gov.caixa.loterias.core.network.mock.fixture.MockMarketplaceFixtures.favoriteRetailers
import br.gov.caixa.loterias.core.network.mock.fixture.MockMarketplaceFixtures.marketplace
import br.gov.caixa.loterias.core.network.mock.fixture.MockMarketplaceFixtures.marketplaceDetail
import br.gov.caixa.loterias.core.network.mock.fixture.MockPrizeFixtures.prizeDetails
import br.gov.caixa.loterias.core.network.mock.fixture.MockPrizeFixtures.lotteryRedemptionCode
import br.gov.caixa.loterias.core.network.mock.fixture.MockPrizeFixtures.pixBeneficiary
import br.gov.caixa.loterias.core.network.mock.fixture.MockCartFixtures.cart
import br.gov.caixa.loterias.core.network.mock.fixture.MockCartFixtures.poolCart
import br.gov.caixa.loterias.core.network.mock.fixture.MockCartFixtures.favoriteCartBets
import br.gov.caixa.loterias.core.network.mock.fixture.MockSupportFixtures.support
import br.gov.caixa.loterias.core.network.mock.fixture.MockResultsFixtures.result
import br.gov.caixa.loterias.core.network.mock.fixture.MockResponsibleGamingFixtures.assessment
import br.gov.caixa.loterias.core.network.mock.fixture.MockResponsibleGamingFixtures.socialTransfers
import br.gov.caixa.loterias.core.network.mock.fixture.MockAccountFixtures.profile
import br.gov.caixa.loterias.core.network.mock.fixture.MockAccountFixtures.municipalities
import br.gov.caixa.loterias.core.network.mock.fixture.MockAccountFixtures.neighborhoods
import br.gov.caixa.loterias.core.network.mock.fixture.MockAccountFixtures.session
import br.gov.caixa.loterias.core.network.mock.fixture.MockPaymentFixtures.paymentMethods
import br.gov.caixa.loterias.core.network.mock.fixture.MockPaymentFixtures.paymentMethod
import br.gov.caixa.loterias.core.network.mock.fixture.MockPaymentFixtures.cards
import br.gov.caixa.loterias.core.network.mock.fixture.MockPaymentFixtures.pix
import br.gov.caixa.loterias.core.network.mock.fixture.MockPaymentFixtures.isPurchaseOperation
import br.gov.caixa.loterias.core.network.mock.fixture.MockPaymentFixtures.asyncPurchase
import br.gov.caixa.loterias.core.network.mock.fixture.MockTicketsFixtures.betReceipt
import br.gov.caixa.loterias.core.network.mock.fixture.MockTicketsFixtures.confirmedBets
import br.gov.caixa.loterias.core.network.mock.fixture.MockTicketsFixtures.confirmedBetsConfiguration
import br.gov.caixa.loterias.core.network.mock.fixture.MockTicketsFixtures.purchaseMonths
import br.gov.caixa.loterias.core.network.mock.fixture.MockTicketsFixtures.purchaseStatuses
import br.gov.caixa.loterias.core.network.mock.fixture.MockTicketsFixtures.purchases
import br.gov.caixa.loterias.core.network.mock.fixture.MockTicketsFixtures.purchaseDetails

internal object MockResponsePayloads {
    fun emptyPayload(path: String): String = when {
        path in listOf(
            "publico/modalidades", "logado/combos", "duvidas/secoes", "duvidas",
            "publico/equipes-esportivas/listaEquipes",
            "municipios", "bairros", "logado/carrinhos-favoritos",
            "logado/apostas-favoritas", "logado/apostas-favoritas/",
            "usuarios/cartoes", "meios-pagamento", "compras", "compras/meses", "compras/situacoes-compra",
        ) -> "[]"
        path == "boloes/recuperar-boloes-disponiveis" -> "{\"totalRegistros\":0,\"paginaAtual\":0,\"ultimaPagina\":0,\"cotas\":[]}"
        path == "autoavaliacoes/recuperar-perguntas" -> "{\"retornoPerguntaResposta\":[]}"
        path == "logado/carrinhos" -> cart(empty = true)
        else -> "null"
    }

    fun successPayload(path: String, requestBody: String = "", purchaseId: Long = 99, contests: List<Int> = listOf(2800)): String = when {
        path.startsWith("app/valida-versao/") -> "true"
        path == "parametros/configuraveis-app" -> appParameters()
        path == "publico/modalidades" -> modalities()
        path == "publico/parametros-simulacao" -> bettingParameters()
        path == "publico/equipes-esportivas/listaEquipes" -> sportsTeamShields()
        path == "logado/combos" -> combos()
        path == "logado/rapidoes" -> rapidConfiguration()
        path == "logado/rapidoes/gerar-apostas" -> cart()
        path == "logado/apostas-favoritas" || path == "logado/apostas-favoritas/" -> groupedFavoriteBets()
        path == "logado/apostas-favoritas/modalidade" || path == "logado/apostas-favoritas/lista-unica" -> favoriteBetPage()
        path == "logado/carrinhos-favoritos" -> "[{\"id\":21,\"nome\":\"Meu carrinho favorito\"}]"
        path.contains("carrinhos-favoritos/") && path.endsWith("/apostas") -> favoriteCartBets()
        path.endsWith("gerar-cobranca-pix") -> pix(false)
        path.isPurchaseOperation() -> asyncPurchase(8, purchaseId)
        path == "logado/carrinhos/incluir-aposta" && requestBody.contains("\"indicadorCotaBolao\":true") -> poolCart()
        path.startsWith("logado/carrinhos") -> cart()
        path == "duvidas/secoes" || path == "duvidas" -> support()
        path == "termos-de-uso/termo-vigente" -> "{\"conteudo\":\"<h1>Termos de uso</h1><p>Conteúdo mock.</p>\"}"
        path == "boloes/recuperar-boloes-disponiveis" -> marketplace()
        path == "boloes/detalhar-bolao" -> marketplaceDetail()
        path == "lotericas/nome-loterico" -> retailers()
        path == "boloes/consultar-loterica-favorita" -> favoriteRetailers()
        path.endsWith("/detalhes-premio") -> prizeDetails()
        path.endsWith("/app/gerar-codigo-resgate-loterica") -> lotteryRedemptionCode()
        path.endsWith("/consulta-dados-chave") -> pixBeneficiary()
        path.endsWith("/resgatar-premio") || path.endsWith("/cancelar-resgate-pix") -> "{}"
        path == "usuarios/gerar-token-cadastro-mercado-pago" -> "\"mock-registration-token\""
        path == "usuarios/gerar-token-apostador-recarga-pay" -> "\"mock-recarga-customer-token\""
        path == "resultados" -> contests.joinToString(prefix = "[", postfix = "]") { contest ->
            result().replace("\"numero\":2800", "\"numero\":$contest")
        }
        path.startsWith("resultados/") -> result()
        path.endsWith("/conferir") -> """{"valor":100,"descricao":"PREMIADA"}"""
        path.endsWith("/comprovante") || path.endsWith("/comprovante-premio") -> betReceipt(path)
        path.matches(Regex("[0-9]+/recuperar")) -> betReceipt("apostas/$path")
        path == "autoavaliacoes/recuperar-perguntas" -> assessment()
        path == "repassometro" -> socialTransfers()
        path == "usuarios/recuperar-dados" || path == "usuarios/recalcular-limite-autorizado" -> profile()
        path == "municipios" -> municipalities()
        path == "bairros" -> neighborhoods()
        path == "logado/usuarios/verificar-cadastro" -> session()
        path == "usuarios/suspender-conta-apostador" -> "{\"apostadorSuspenso\":true,\"prazoHorasDias\":\"24 horas\"}"
        path == "meios-pagamento" -> paymentMethods()
        path.startsWith("meios-pagamento/") -> paymentMethod(path.substringAfterLast('/').toIntOrNull())
        path == "apostas/config-consulta" -> confirmedBetsConfiguration()
        path == "compras/meses" -> purchaseMonths()
        path == "compras/situacoes-compra" -> purchaseStatuses()
        path == "compras" -> purchases()
        path.startsWith("compras/") && path.endsWith("/apostas") -> purchaseDetails()
        path == "usuarios/cartoes" -> cards()
        path.matches(Regex("historico-notificacoes/[0-9]+/visualiza")) -> "{}"
        path == "apostas" || path == "consultar" -> confirmedBets()
        path == "v1/payment_methods/installments" -> "[{\"payment_method_id\":\"visa\"}]"
        path == "v1/card_tokens" -> "{\"id\":\"mock-card-token\",\"luhn_validation\":true}"
        path == "cards" || path.startsWith("cards/") -> "{\"customerCardToken\":\"mock-customer-card\",\"brand\":\"VISA\"}"
        else -> "{}"
    }
}
