package br.gov.caixa.loterias.feature.onboarding.domain.usecase

import br.gov.caixa.loterias.feature.onboarding.domain.repository.LauncherIconCalendarConfiguration
import java.io.Closeable

class ObserveLauncherIconCalendarUseCase(private val repository: LauncherIconCalendarConfiguration) {
    operator fun invoke(onChanged: () -> Unit): Closeable = repository.observe(onChanged)
}
