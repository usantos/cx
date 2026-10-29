package br.gov.caixa.loterias.feature.account.presentation.permission

import android.Manifest

/** Subconjunto em execução de TopazUtil.check da CX mais READ_PHONE_NUMBERS declarado pelo AAR exato do SDK. */
fun loginRuntimePermissions(
    sdk: Int,
    declared: Set<String>,
    granted: Set<String>,
    simulated: Boolean,
): List<String> {
    if (simulated || sdk < 23) return emptyList()
    val candidates = buildList {
        add(Manifest.permission.ACCESS_FINE_LOCATION)
        add(Manifest.permission.ACCESS_COARSE_LOCATION)
        add(Manifest.permission.READ_PHONE_STATE)
        if (sdk >= 26) add(Manifest.permission.READ_PHONE_NUMBERS)
        if (sdk <= 32) add(Manifest.permission.READ_EXTERNAL_STORAGE)
        add(Manifest.permission.GET_ACCOUNTS)
        if (sdk <= 36) add(Manifest.permission.READ_CONTACTS)
        if (sdk >= 31) add(Manifest.permission.BLUETOOTH_CONNECT)
        if (sdk >= 33) add(Manifest.permission.POST_NOTIFICATIONS)
    }.filter { it in declared }
    val locationPairRequired = sdk >= 31 && Manifest.permission.ACCESS_FINE_LOCATION in candidates &&
        Manifest.permission.ACCESS_COARSE_LOCATION in candidates && Manifest.permission.ACCESS_FINE_LOCATION !in granted
    return candidates.filter { it !in granted || (locationPairRequired && it == Manifest.permission.ACCESS_COARSE_LOCATION) }
}
