package br.gov.caixa.loterias.feature.home

import java.time.LocalDate

enum class HomeCampaign {
    DEFAULT,
    OUTUBRO_ROSA,

    ;

    val megaSenaPrimaryArgb: Long
        get() = if (this == OUTUBRO_ROSA) 0xFFEE3E7FL else 0xFF00A651L

    val megaSenaDarkArgb: Long
        get() = if (this == OUTUBRO_ROSA) 0xFF9C005CL else 0xFF006B3FL
}

/** The installed TQS build presents the Outubro Rosa home campaign throughout October. */
fun homeCampaignAt(date: LocalDate): HomeCampaign =
    if (date.monthValue == 10) HomeCampaign.OUTUBRO_ROSA else HomeCampaign.DEFAULT
