package br.gov.caixa.loterias.feature.account.presentation.route

import android.content.Intent
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.lifecycle.LifecycleRegistry
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [TEST_SDK], application = android.app.Application::class)
@LooperMode(LooperMode.Mode.PAUSED)
class LoginActivityReturnTest {
    private lateinit var host: ActivityController<ComponentActivity>
    private val activity get() = host.get()
    private val lifecycle get() = activity.lifecycle as LifecycleRegistry

    @Before fun createHost() {
        host = Robolectric.buildActivity(ComponentActivity::class.java).setup()
    }

    @After fun closeHost() {
        host.close()
    }

    @Test fun pausedHostReturnsOnlyAfterThePendingBrowserHasStoppedTheHost() {
        host.pause()
        val observers = lifecycle.observerCount
        returnToLoginActivity(activity)
        assertNull(shadowOf(activity).nextStartedActivity)
        host.stop()
        assertNull(shadowOf(activity).nextStartedActivity)
        shadowOf(Looper.getMainLooper()).idle()
        val intent = requireNotNull(shadowOf(activity).nextStartedActivity)
        assertEquals(activity.componentName, intent.component)
        assertEquals(RETURN_FLAGS, intent.flags)
        assertEquals(observers, lifecycle.observerCount)
    }

    @Test fun stoppedHostStillReturnsImmediately() {
        host.pause().stop()
        returnToLoginActivity(activity)
        assertNotNull(shadowOf(activity).nextStartedActivity)
    }

    @Test fun resumingWithoutStoppingCancelsThePendingReturn() {
        host.pause()
        val observers = lifecycle.observerCount
        returnToLoginActivity(activity)
        host.resume()
        shadowOf(Looper.getMainLooper()).idle()
        assertNull(shadowOf(activity).nextStartedActivity)
        assertEquals(observers, lifecycle.observerCount)
    }

    @Test fun destroyingTheHostRemovesTheAlreadyPostedReturn() {
        host.pause()
        returnToLoginActivity(activity)
        host.stop().destroy()
        shadowOf(Looper.getMainLooper()).idle()
        assertNull(shadowOf(activity).nextStartedActivity)
    }

    @Test fun navigationOrAuthenticationInvalidationPreventsReopeningLogin() {
        host.pause()
        val observers = lifecycle.observerCount
        var stillRequired = true
        returnToLoginActivity(activity) { stillRequired }
        host.stop()
        stillRequired = false
        shadowOf(Looper.getMainLooper()).idle()
        assertNull(shadowOf(activity).nextStartedActivity)
        assertEquals(observers, lifecycle.observerCount)
    }

    @Test fun disposingTheLoginRouteCancelsItsPendingReturn() {
        host.pause()
        val observers = lifecycle.observerCount
        val cancel = returnToLoginActivity(activity)
        cancel()
        host.stop()
        shadowOf(Looper.getMainLooper()).idle()
        assertNull(shadowOf(activity).nextStartedActivity)
        assertEquals(observers, lifecycle.observerCount)
    }
}

private const val TEST_SDK = 35
private const val RETURN_FLAGS = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
