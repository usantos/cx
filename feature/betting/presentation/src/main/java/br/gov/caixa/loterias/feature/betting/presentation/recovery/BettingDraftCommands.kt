package br.gov.caixa.loterias.feature.betting.presentation.recovery

import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameAction
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.reduceBettingGameState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

/** Cada comando calcula a partir do valor confirmado anterior, confirma a gravação e então publica. */
internal class BettingDraftCommands(
    scope: CoroutineScope,
    initial: BettingGameUiState,
    private val persist: suspend (BettingGameUiState) -> Unit,
    private val isCurrent: () -> Boolean,
    private val messages: BettingRecoveryMessages = BettingRecoveryMessages.Identifiers,
    internal val working: ReduxStateStore<BettingGameUiState, BettingGameAction> =
        reduxStateStore(initial, ::reduceBettingGameState),
    private val onSelectionChange: (BettingGameUiState, BettingGameUiState) -> Unit = { _, _ -> },
) {
    private val published = reduxStateStore<BettingGameUiState, BettingGameAction>(initial, ::reduceBettingGameState)
    val state = published.state
    private val queue = Channel<Command>(Channel.UNLIMITED)
    private var capturingThread: Thread? = null
    private var captured = mutableListOf<BettingGameAction>()
    private var failed = false

    private sealed interface Command {
        data class Change(val action: () -> Unit, val durable: Boolean, val requireHealthy: Boolean = false) : Command
        data class Navigate(val action: () -> Unit) : Command
    }

    private val worker = scope.launch {
        for (command in queue) {
            if (!isCurrent()) continue
            val before = published.value
            // Este é o ponto de recuperação da transação confirmada, nunca uma substituição arbitrária da interface.
            working.dispatch(BettingGameAction.RestoreConfirmedDraft(before))
            try {
                when (command) {
                    is Command.Change -> applyChange(command, before)

                    is Command.Navigate -> {
                        if (!failed) {
                            persist(before)
                            if (isCurrent()) command.action()
                        }
                    }
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                working.dispatch(BettingGameAction.RestoreConfirmedDraft(before))
                failed = true
                if (isCurrent()) published.dispatch(BettingGameAction.DraftWriteFailed(messages.text(BettingRecoveryMessage.WRITE_FAILED)))
            }
        }
    }

    private suspend fun applyChange(command: Command.Change, before: BettingGameUiState) {
        if (command.requireHealthy && failed) return
        captured = mutableListOf()
        capturingThread = Thread.currentThread()
        try {
            command.action()
        } finally {
            capturingThread = null
        }
        val candidate = working.value
        if (command.durable) persist(candidate)
        if (!isCurrent()) {
            working.dispatch(BettingGameAction.RestoreConfirmedDraft(before))
            return
        }
        // A reprodução contém resultados imutáveis dos efeitos; a publicação não pode regenerar aleatoriedade nem identidades.
        published.dispatch(BettingGameAction.DraftCommitted(captured.toList()))
        val substantiveChange = candidate.copy(errorMessage = null, loading = before.loading) !=
            before.copy(errorMessage = null, loading = before.loading)
        if (!command.durable || substantiveChange) failed = false
    }

    fun edit(action: BettingGameAction) {
        if (capturingThread === Thread.currentThread()) applyEdit(action) else execute { applyEdit(action) }
    }

    private fun applyEdit(action: BettingGameAction) {
        synchronized(working) {
            val before = working.value
            working.dispatch(action)
            captured += action
            if (action !is BettingGameAction.CheckpointRestored && action !is BettingGameAction.RecoveryRejected) {
                onSelectionChange(before, working.value)
            }
        }
    }

    fun execute(action: () -> Unit) {
        queue.trySend(Command.Change(action, durable = true))
    }

    fun action(action: () -> Unit) {
        queue.trySend(Command.Change(action, durable = true, requireHealthy = true))
    }

    /** Usado apenas para instalar o estado inicial ou restaurado recém-validado, nunca uma edição do usuário. */
    fun initialize(action: () -> Unit) {
        queue.trySend(Command.Change(action, durable = false))
    }

    fun navigate(action: () -> Unit) {
        queue.trySend(Command.Navigate(action))
    }

    fun close() {
        queue.close()
        worker.cancel()
        working.close()
        published.close()
    }
}
