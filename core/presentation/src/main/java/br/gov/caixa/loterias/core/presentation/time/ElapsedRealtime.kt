package br.gov.caixa.loterias.core.presentation.time

/** Adaptador do relógio Android; consumidores injetam a função para testes determinísticos de tempo decorrido. */
fun elapsedRealtimeMillis(): Long = android.os.SystemClock.elapsedRealtime()
