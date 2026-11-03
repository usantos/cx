package br.gov.caixa.loterias.feature.home.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.feature.home.presentation.components.Shortcut
import br.gov.caixa.loterias.feature.home.ui.R as DeliveryR

@Composable
internal fun HomeV2Presentation.ShortcutActions() {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Shortcut(
            uiStrings.getString(DeliveryR.string.ui_meus_jogos_08fc53),
            uiStrings.getString(DeliveryR.string.ui_acompanhe_suas_apostas_7c205a),
            DeliveryR.drawable.ic_home_v2_bilhete,
            (Modifier.weight(1f)).uiId("home.0149"),
            actions::games
        )
        Shortcut(
            "Resultados",
            uiStrings.getString(DeliveryR.string.ui_consulte_historicos_661e76),
            DeliveryR.drawable.ic_home_v2_resultados,
            (Modifier.weight(1f)).uiId("home.0150"),
            actions::results
        )
        Shortcut(
            uiStrings.getString(DeliveryR.string.ui_estatisticas_003851),
            uiStrings.getString(DeliveryR.string.ui_veja_numeros_e_probabilidades_04757e),
            DeliveryR.drawable.ic_home_v2_estatisticas,
            (Modifier.weight(1f)).uiId("home.0151"),
            actions::results
        )
        Shortcut(
            uiStrings.getString(DeliveryR.string.ui_promocoes_2a838e),
            uiStrings.getString(DeliveryR.string.ui_fique_por_dentro_das_novidades_bdfd42),
            DeliveryR.drawable.ic_home_v2_presente,
            (Modifier.weight(1f)).uiId("home.0152"),
            actions::notifications
        )
    }
}
