package br.gov.caixa.loterias.core.security.keyset

import android.os.Looper
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class FirebaseTaskCompletionTest {
    @Test fun `successful Void settings and false activation count as completed work`() = runBlocking {
        val settings = async(start = CoroutineStart.UNDISPATCHED) { Tasks.forResult<Void>(null).awaitSuccessful() }
        shadowOf(Looper.getMainLooper()).idle()
        assertTrue(settings.await())
        val alreadyActivated = async(start = CoroutineStart.UNDISPATCHED) { Tasks.forResult(false).awaitSuccessful() }
        shadowOf(Looper.getMainLooper()).idle()
        assertTrue(alreadyActivated.await())
    }

    @Test fun `failed and cancelled SDK tasks do not permit dependent fetching`() = runBlocking {
        val failed =
            async(start = CoroutineStart.UNDISPATCHED) { Tasks.forException<Void>(IllegalStateException("offline")).awaitSuccessful() }
        shadowOf(Looper.getMainLooper()).idle()
        assertFalse(failed.await())
        val token = CancellationTokenSource()
        val source = TaskCompletionSource<Void>(token.token)
        val cancelled = async(start = CoroutineStart.UNDISPATCHED) { source.task.awaitSuccessful() }
        token.cancel()
        shadowOf(Looper.getMainLooper()).idle()
        assertFalse(cancelled.await())
    }

    @Test fun `coroutine cancellation ignores a later SDK success callback`() = runBlocking {
        val source = TaskCompletionSource<Void>()
        val pending = async(start = CoroutineStart.UNDISPATCHED) { source.task.awaitSuccessful() }
        pending.cancel()
        source.setResult(null)
        shadowOf(Looper.getMainLooper()).idle()
        pending.join()
        assertTrue(pending.isCancelled)
    }
}
