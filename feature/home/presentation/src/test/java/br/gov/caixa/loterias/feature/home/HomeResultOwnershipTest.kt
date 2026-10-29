package br.gov.caixa.loterias.feature.home

import br.gov.caixa.loterias.feature.home.presentation.state.HomeUiState
import br.gov.caixa.loterias.feature.home.presentation.viewmodel.HomeViewModel
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeResultOwnershipTest {
    @Test fun homeReduxStateOwnsResultRequestAndCache() {
        assertTrue(
            "Home Redux must own result request/cache state rather than a remembered route coordinator",
            HomeUiState::class.java.declaredFields.any { it.name == "results" }
        )
    }

    @Test fun homeViewModelOwnsResultRequests() {
        assertTrue(
            "Home must issue and invalidate its own result requests",
            HomeViewModel::class.java.declaredFields.any {
                it.name == "resultActions" &&
                    it.type == br.gov.caixa.loterias.feature.home.presentation.coordinator.HomeResultActions::class.java
            }
        )
    }
}
