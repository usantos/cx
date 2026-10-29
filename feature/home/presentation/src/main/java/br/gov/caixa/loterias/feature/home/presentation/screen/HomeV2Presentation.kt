package br.gov.caixa.loterias.feature.home.presentation.screen

import androidx.compose.runtime.*
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CxHomeV2Colors
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.home.presentation.mapper.HomeV2Modality
import br.gov.caixa.loterias.feature.home.presentation.mapper.name
import br.gov.caixa.loterias.feature.home.R

/** Somente apresentação: navegação, autenticação e compras permanecem em PrincipalActivity. */
class HomeV2Presentation(private val orderProvider: () -> List<String>, private val onMove: (String, String) -> Unit, private val actionProvider: () -> Actions) {
    internal val actions get() = actionProvider()
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

    internal val navy = CxHomeV2Colors.CFF003B71
    internal val blue = CxHomeV2Colors.CFF005CA9
    internal val regular = FontFamily(Font(R.font.caixa_std_regular))
    internal val bold = FontFamily(Font(R.font.caixa_std_bold))
    internal val order = listOf(HomeV2Modality.MEGA_SENA, HomeV2Modality.MAIS_MILIONARIA,
        HomeV2Modality.LOTECA, HomeV2Modality.DIA_DE_SORTE, HomeV2Modality.DUPLA_SENA,
        HomeV2Modality.LOTOMANIA, HomeV2Modality.QUINA, HomeV2Modality.TIMEMANIA,
        HomeV2Modality.LOTOFACIL, HomeV2Modality.SUPER_7)


    internal val cardOrder get() = orderProvider()

    internal fun moveModality(from: HomeV2Modality, to: HomeV2Modality) = onMove(from.name, to.name)

    @Composable fun Home(modalidades: List<HomeLottery>, loading: Boolean, results: Map<String, DrawResult>) {
        HomeComposition(modalidades, loading, results)
    }
}
