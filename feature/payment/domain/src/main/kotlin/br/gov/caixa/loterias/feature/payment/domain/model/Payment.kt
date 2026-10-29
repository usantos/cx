package br.gov.caixa.loterias.feature.payment.domain.model

fun cardBrand(number: String): String {
    val digits = number.filter(Char::isDigit)
    return when {
        isElo(digits) -> "ELO"
        isMastercard(digits) -> "MASTERCARD"
        digits.startsWith("605032") -> "GRANDCARD"
        digits.startsWith("9792") -> "TROY"
        digits.startsWith("5067") -> "VERVE"
        digits.startsWith("604") || digits.startsWith("637") -> "CABAL"
        digits.startsWith("6062") || digits.startsWith("384") -> "HIPERCARD"
        digits.startsWith("34") || digits.startsWith("37") -> "AMERICANEXPRESS"
        listOf("30", "36", "38", "39").any(digits::startsWith) -> "DINERS"
        digits.startsWith("35") -> "JCB"
        digits.startsWith("6011") || digits.startsWith("65") -> "DISCOVER"
        digits.startsWith("4") -> "VISA"
        else -> "VISA"
    }
}

internal fun isMastercard(number: String): Boolean {
    val prefix2 = number.take(2).toIntOrNull()
    val prefix4 = number.take(4).toIntOrNull()
    return prefix2 in 51..55 ||
        prefix4 in 2221..2720 ||
        listOf("5031", "5081", "5021", "5899", "5063").any(number::startsWith)
}

internal val eloBinPattern = Regex(
    "^(509(508|509|518|519|918|921|926|929|932|935|938|940|946|948|951|952|957|961|963|971|975|978|979|980|983|986|996|999|" +
        "900|920|924|925|927|930|937|939|943|947|949|950|955|956|958|959|960|972|973|974|977|982|984|998|919|922|923|928|931|" +
        "933|934|936|941|942|944|945|953|954|962|964|976|981|985|995|997)|" +
        "650(948|949|953|955|956|957|960|968|971|946|952|961|962|966|973|977|947|950|951|954|958|959|963|964|965|967|970|972|" +
        "974|975|976|978|058|059|048)|" +
        "(4011(78|79)|431274|438935|451416|45(7393|7631|7632)|5067(00|01|02|04|05|06|08|10|11|12|14|15|16|17|18|19|20|24|25|26|27|28|" +
        "29|30|31|32|33|37|39|40|41|42|43|44|45|46|47|48|49|50|51|52|53|60|61|62|63|64|65|70|71|72|73|74|75|77|78)|5090(00|01|" +
        "02|04|05|06|07|09|10|11|12|13|14|15|16|17|18|19|20|21|22|24|25|26|27|28|29)|509(0[3-9][0-9]|[1-4][0-9]{2}|5[0-2][0-7]|" +
        "5[2-9][0-9]|6[0-9][0-9]|7[0-9][0-9]|8[0-7][0-9]|88[0-7]|8[8-9][0-9]|9[0-9]{3})|636(297|368)|650(4[0-9]{2}|5[0-9]{2}|" +
        "6[0-9]{2}|7[0-9]{2}|8[0-9]{2}|9[0-3][0-9]|9[4-9]|580|581|582|583)|651(6[5-9][0-9]|[7-9][0-9]{2})|655(0[0-9]{2}|1[0-9]{2}|" +
        "2[0-9]{2}|3[0-9]{2}|4[0-9]{2}|5[0-9]{2}|6[0-9]{2}|7[0-9]{2}|8[0-9]{2}|9[0-9]{2})|6500580[1-3]))$",
)

internal fun isElo(number: String): Boolean = number.length >= 6 && eloBinPattern.matches(number.take(6))
