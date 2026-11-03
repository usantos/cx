package br.gov.caixa.loterias.feature.support.di

import android.content.Context
import br.gov.caixa.loterias.core.domain.delivery.ModuleInstallationRepository
import br.gov.caixa.loterias.feature.support.data.delivery.PlaySupportModuleDelivery
import br.gov.caixa.loterias.feature.support.domain.delivery.SupportModuleDelivery

/** O host recebe somente a porta de Domain, sem conhecer o SDK ou o adaptador de Data. */
fun createSupportModuleDelivery(context: Context): SupportModuleDelivery = PlaySupportModuleDelivery(context)

fun createSupportModuleDelivery(installation: ModuleInstallationRepository): SupportModuleDelivery = PlaySupportModuleDelivery(installation)
