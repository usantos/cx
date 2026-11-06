package br.gov.caixa.loterias.core.queue.android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat

/** Entrega restrita ao mesmo UID no Android moderno; usa permissão de assinatura como alternativa no Android antigo. */
fun registerPrivateQueueReceiver(context: Context, receiver: BroadcastReceiver?, filter: IntentFilter): Intent? =
    ContextCompat.registerReceiver(context, receiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
