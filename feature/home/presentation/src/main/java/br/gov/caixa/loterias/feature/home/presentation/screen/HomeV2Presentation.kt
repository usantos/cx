package br.gov.caixa.loterias.feature.home.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CxHomeV2Colors
import br.gov.caixa.loterias.feature.home.R
import br.gov.caixa.loterias.feature.home.presentation.mapper.HomeV2Modality
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult

/** Somente apresentação: navegação, autenticação e compras permanecem em PrincipalActivity. */
class HomeV2Presentation(
    private val orderProvider: () -> List<String>,
    private val onMove: (String, String) -> Unit,
    private val actionProvider: () -> Actions,
) {
    val actions get() = actionProvider()
    interface Actions {
        fun bet(modalidade: HomeLottery)
        fun pool(modalidade: HomeLottery)
        fun results()
        fun modalityResults(modalidade: HomeV2Modality)
        fun games()
        fun favorites()
        fun menu()
        fun notifications()
        fun cart()
        fun profile()
        fun canPool(modalidade: HomeV2Modality): Boolean
    }

    val navy = CxHomeV2Colors.CFF003B71
    val blue = CxHomeV2Colors.CFF005CA9
    val regular = FontFamily(Font(R.font.caixa_std_regular))
    val bold = FontFamily(Font(R.font.caixa_std_bold))
    val order = listOf(
        HomeV2Modality.MEGA_SENA, HomeV2Modality.MAIS_MILIONARIA,
        HomeV2Modality.LOTECA, HomeV2Modality.DIA_DE_SORTE, HomeV2Modality.DUPLA_SENA,
        HomeV2Modality.LOTOMANIA, HomeV2Modality.QUINA, HomeV2Modality.TIMEMANIA,
        HomeV2Modality.LOTOFACIL, HomeV2Modality.SUPER_7
    )

    val cardOrder get() = orderProvider()

    fun moveModality(from: HomeV2Modality, to: HomeV2Modality) = onMove(from.name, to.name)

    @Composable fun Home(modalidades: List<HomeLottery>, loading: Boolean, results: Map<String, DrawResult>) {
        br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent(
            module = br.gov.caixa.loterias.core.domain.configuration.model.AppModule.HOME,
            entryType = br.gov.caixa.loterias.feature.home.presentation.delivery.HomeUiEntry::class.java
        ) { entry -> entry.PresentationHome(this, modalidades, loading, results) }
    }
}
