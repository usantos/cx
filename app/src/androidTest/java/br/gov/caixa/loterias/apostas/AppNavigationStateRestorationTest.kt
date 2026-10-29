package br.gov.caixa.loterias.apostas

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.gov.caixa.loterias.core.navigation.state.AppNavigationState
import br.gov.caixa.loterias.core.navigation.state.AppSessionState
import br.gov.caixa.loterias.core.navigation.state.DetailRestorationOutcome
import br.gov.caixa.loterias.core.navigation.state.rememberAppNavigationState
import br.gov.caixa.loterias.core.navigation.state.rememberAppSessionState
import br.gov.caixa.loterias.feature.home.presentation.mapper.prize
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilter
import br.gov.caixa.loterias.feature.tickets.domain.model.Purchase
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppNavigationStateRestorationTest {
    @get:Rule val compose = createAndroidComposeRule<HomeDrawerTestActivity>()

    @Test fun recreationRetainsPublicRevisionButRequiresEncryptedRecoveryForPrivateDetails() {
        val restoration = StateRestorationTester(compose)
        lateinit var navigation: AppNavigationState
        lateinit var session: AppSessionState
        restoration.setContent {
            navigation = rememberAppNavigationState("owner")
            session = rememberAppSessionState()
        }
        compose.runOnIdle {
            navigation.details.selectedPurchase = Purchase(17, "17/10/2026", "Paga", "Pix", BigDecimal.TEN)
            navigation.marketplaceRevision = 7
            navigation.requestedHomeModalityId = 2
            session.pendingRestoredRoute = "tickets/purchase-detail"
            session.visitorLoginRequest = "tickets/my-bets"
            session.visitorReturnRoute = "retail/prize/42"
            session.visitorReturnOwnerKey = "owner"
            session.resumedHomeAction = "home/rapid"
            session.accountCompletionRequired = true
            session.foregroundExpiryMarker = "opaque-marker"
            session.prizeChannelHost = true
        }

        restoration.emulateSavedInstanceStateRestore()

        compose.runOnIdle {
            assertNull(navigation.details.selectedPurchase)
            assertEquals(7, navigation.marketplaceRevision)
            assertNull(navigation.requestedHomeModalityId)
            assertNull(session.pendingRestoredRoute)
            assertNull(session.visitorLoginRequest)
            assertNull(session.visitorReturnRoute)
            assertNull(session.visitorReturnOwnerKey)
            assertNull(session.resumedHomeAction)
            assertFalse(session.accountCompletionRequired)
            assertNull(session.foregroundExpiryMarker)
            assertFalse(session.prizeChannelHost)
        }
    }

    @Test fun ownerChangeDiscardsPrivateDetailsAndResetsOwnerScopedRevision() {
        val owner = mutableStateOf("owner")
        lateinit var navigation: AppNavigationState
        compose.setContent { navigation = rememberAppNavigationState(owner.value) }
        compose.runOnIdle {
            navigation.marketplaceRevision = 7
            navigation.details.marketplaceFilter = MarketplaceFilter.forModality(2, 1)
            navigation.details.selectedPurchase = Purchase(17, "17/10/2026", "Paga", "Pix", BigDecimal.TEN)
            owner.value = "other"
        }
        compose.waitForIdle()

        compose.runOnIdle {
            assertEquals(0, navigation.marketplaceRevision)
            assertNull(navigation.details.selectedPurchase)
            assertEquals(MarketplaceFilter.empty(), navigation.details.marketplaceFilter)
            assertEquals(DetailRestorationOutcome.Discarded, navigation.details.restorationOutcome)
        }
    }
}
