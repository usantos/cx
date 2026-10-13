package br.gov.caixa.loterias.feature.support

private val legacyHostname = Regex(
    "(?i)^(?=.{1,253}\\z)[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?\\." +
        "(?:[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?\\.)*[a-z](?:[a-z0-9-]{0,61}[a-z0-9])?\\z",
)

/** DetalheDuvidaHolder.obterUrlSegura; URL ranges come from Android Patterns.WEB_URL. */
internal fun supportAnswerUrl(
    candidate: String,
    start: Int,
    answer: String,
    scheme: String?,
    host: String?,
): String? {
    if (start > 0 && answer.getOrNull(start - 1) == '@') return null
    if (scheme != null && !scheme.equals("http", true) && !scheme.equals("https", true)) return null
    val target = if (scheme == null) "https://$candidate" else candidate
    return target.takeIf { host != null && legacyHostname.matches(host) }
}
