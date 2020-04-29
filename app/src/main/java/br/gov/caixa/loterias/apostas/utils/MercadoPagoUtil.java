package br.gov.caixa.loterias.apostas.utils;


import android.content.Context;

import br.gov.caixa.loterias.apostas.R;

public class MercadoPagoUtil {

    public static final String CUSTOMER_NOT_ALLOWED_TO_OPERATE = "2021";
    public static final String COLLECTOR_NOT_ALLOWED_TO_OPERATE = "2022";
    public static final String INVALID_USERS_INVOLVED = "2035";
    public static final String CUSTOMER_EQUAL_TO_COLLECTOR = "3000";
    public static final String INVALID_CARD_HOLDER_NAME = "E208";
    public static final String UNAUTHORIZED_CLIENT = "3010";
    public static final String PAYMENT_METHOD_NOT_FOUND = "3012";
    public static final String INVALID_SECURITY_CODE = "E203";
    public static final String SECURITY_CODE_REQUIRED = "3014";
    public static final String INVALID_PAYMENT_METHOD = "3015";
    public static final String INVALID_CARD_NUMBER = "3017";
    public static final String EMPTY_EXPIRATION_MONTH = "3019";
    public static final String EMPTY_EXPIRATION_YEAR = "3020";
    public static final String EMPTY_CARD_HOLDER_NAME = "3021";
    public static final String EMPTY_DOCUMENT_NUMBER = "3022";
    public static final String EMPTY_DOCUMENT_TYPE = "3023";
    public static final String INVALID_PAYMENT_TYPE_ID = "3028";
    public static final String INVALID_PAYMENT_METHOD_ID = "3029";
    public static final String INVALID_CARD_EXPIRATION_MONTH = "E204";
    public static final String INVALID_CARD_EXPIRATION_YEAR = "E205";
    public static final String INVALID_PAYER_EMAIL = "4050";
    public static final String INVALID_PAYMENT_WITH_ESC = "2107";
    public static final String INVALID_IDENTIFICATION_NUMBER = "2067";
    public static final String INVALID_CARD_HOLDER_IDENTIFICATION_NUMBER = "324";
    public static final String INVALID_ESC = "E216";
    public static final String INVALID_EXPIRATION_DATE = "301";
    public static final String INVALID_FINGERPRINT = "E217";



    public static String getMessageFromCode(String code){
        Context context = Aplicacao.application.getApplicationContext();
        switch (code){
            case CUSTOMER_NOT_ALLOWED_TO_OPERATE : return context.getString(R.string.mercado_pago_consumidor_nao_permitido);
            case COLLECTOR_NOT_ALLOWED_TO_OPERATE : return context.getString(R.string.mercado_pago_vendedor_nao_permitido);
            case INVALID_USERS_INVOLVED : return context.getString(R.string.mercado_pago_usuarios_invalidos_envolvidos);
            case CUSTOMER_EQUAL_TO_COLLECTOR : return context.getString(R.string.mercado_pago_consumidor_igual_vendedor);
            case INVALID_CARD_HOLDER_NAME : return context.getString(R.string.mercado_pago_nome_titular_invalido);
            case UNAUTHORIZED_CLIENT : return context.getString(R.string.mercado_pago_cliente_nao_autorizado);
            case PAYMENT_METHOD_NOT_FOUND : return context.getString(R.string.mercado_pago_metodo_pagamento_vazio);
            case INVALID_SECURITY_CODE : return context.getString(R.string.mercado_pago_codigo_seguranca_invalido);
            case SECURITY_CODE_REQUIRED : return context.getString(R.string.mercado_pago_codigo_seguranca_vazio);
            case INVALID_PAYMENT_METHOD : return context.getString(R.string.mercado_pago_metodo_pagamento_invalido);
            case INVALID_CARD_NUMBER : return context.getString(R.string.mercado_pago_numero_cartao_Vazio);
            case EMPTY_EXPIRATION_MONTH : return context.getString(R.string.mercado_pago_mes_vazio);
            case EMPTY_EXPIRATION_YEAR : return context.getString(R.string.mercado_pago_ano_vazio);
            case EMPTY_CARD_HOLDER_NAME : return context.getString(R.string.mercado_pago_nome_titular_vazio);
            case EMPTY_DOCUMENT_NUMBER : return context.getString(R.string.mercado_pago_numero_documento_vazio);
            case EMPTY_DOCUMENT_TYPE : return context.getString(R.string.mercado_pago_tipo_vazio);
            case INVALID_PAYMENT_TYPE_ID : return context.getString(R.string.mercado_pago_tipo_metodo_pagamento_invalido);
            case INVALID_PAYMENT_METHOD_ID : return context.getString(R.string.mercado_pago_id_metodo_pagamento_invalido);
            case INVALID_CARD_EXPIRATION_MONTH : return context.getString(R.string.mercado_pago_mes_invalido);
            case INVALID_CARD_EXPIRATION_YEAR : return context.getString(R.string.mercado_pago_ano_invalido);
            case INVALID_PAYER_EMAIL : return context.getString(R.string.mercado_pago_email_invalido);
            case INVALID_PAYMENT_WITH_ESC : return context.getString(R.string.mercado_pago_email_invalido);
            case INVALID_IDENTIFICATION_NUMBER : return context.getString(R.string.mercado_pago_digito_verificador_invalido);
            //Token creation error cause codes
            case INVALID_CARD_HOLDER_IDENTIFICATION_NUMBER : return context.getString(R.string.mercado_pago_digito_verificador_titular_invalido);
            //Rever
            //case INVALID_ESC : return context.getString(R.string.);
            case INVALID_FINGERPRINT : return context.getString(R.string.mercado_pago_digital_invalida);
            case INVALID_EXPIRATION_DATE: return context.getString(R.string.mercado_pago_data_expiracao_invalida);
            default: return context.getString(R.string.mercado_pago_padrao);
        }
    }
}