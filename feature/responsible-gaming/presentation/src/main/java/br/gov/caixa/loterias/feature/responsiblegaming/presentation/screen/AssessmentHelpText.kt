package br.gov.caixa.loterias.feature.responsiblegaming.presentation.screen

import br.gov.caixa.loterias.feature.responsiblegaming.presentation.formatting.localizedMessage as messageText

/** Texto de ajuda nativo de strings_jr, resolvido no catálogo do módulo para a apresentação Compose. */
internal object AssessmentHelpText {
    val Title get() = messageText("final_help_title")
    val Description get() = messageText("se_voce_precisa_de_orientacao_de_3b93e0")
    val CaixaTitle get() = messageText("final_help_caixa_title")
    val CaixaPhoneLabel get() = messageText("final_help_caixa_phone_label")
    val CaixaMetropolitanPhone get() = messageText("4004_0104_capitais_e_regioes_met_6b2fb5")
    val CaixaOtherRegionsPhone get() = messageText("0800_104_0104_demais_regioes_c86b7a")
    val LotteryOption get() = messageText("opcao_6_loterias_083c40")
    val ResponsibleGamingOption get() = messageText("opcao_2_jogo_responsavel_03fb59")
    val CapsTitle get() = messageText("centros_de_atencao_psicossocial_38d323")
    const val CAPS_URL = "https://sage.saude.gov.br/paineis/planoCrack/lista_caps.php?output=html&%20-"
    val CapsLink get() = messageText("clique_aqui_e_localize_uma_unida_033ccc")
    val ProamjoTitle get() = messageText("proamjo_programa_ambulatorial_do_fb8407")
    val ProamjoAddress get() = messageText("rua_dr_ovidio_pires_de_campos_78_3f937f")
    val ProamjoPhones get() = messageText("final_help_proamjo_phones")
    val ProamjoEmail get() = messageText("final_help_proamjo_email")
    val ViverBemTitle get() = messageText("associacao_viver_bem_52a905")
    val ViverBemAddress get() = messageText("rua_artur_de_azevedo_147_cerquei_94f21f")
    val ViverBemPhone get() = messageText("final_help_viver_bem_phone")
    val ViverBemEmail get() = messageText("final_help_viver_bem_email")
    val ProadTitle get() = messageText("proad_programa_de_orientacao_e_a_28c49a")
    val ProadAddress get() = messageText("av_ascendino_reis_763_vila_cleme_aa091c")
    val ProadPhones get() = messageText("final_help_proad_phones")
    val ProadEmail get() = messageText("final_help_proad_email")
    val SantaCasaTitle get() = messageText("santa_casa_de_misericordia_c096e2")
    val SantaCasaAddress get() = messageText("rua_santa_luzia_206_centro_rio_d_f91b64")
    val SantaCasaPhone get() = messageText("final_help_santa_casa_phone")
    val AnonymousGamblersTitle get() = messageText("final_help_anonymous_gamblers_title")
    val AnonymousGamblersPhones get() = messageText("final_help_anonymous_gamblers_phones")
    val AnonymousGamblersLink get() = messageText("final_help_anonymous_gamblers_link")
    const val ANONYMOUS_GAMBLERS_URL = "https://www.jogadoresanonimos.com.br"
}
