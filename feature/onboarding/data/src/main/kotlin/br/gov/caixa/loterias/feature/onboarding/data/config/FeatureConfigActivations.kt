package br.gov.caixa.loterias.feature.onboarding.data.config

import br.gov.caixa.loterias.core.common.config.ConfigurationActivationEvents
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import java.io.Closeable

/** Conecta os adaptadores Firebase ao contrato de ativação compartilhado. */
internal fun observeFeatureConfigActivations(remote: FirebaseRemoteConfig, observer: () -> Unit): Closeable =
    ConfigurationActivationEvents.observe(remote, observer)

internal fun publishFeatureConfigActivations(remote: FirebaseRemoteConfig) = ConfigurationActivationEvents.publish(remote)
