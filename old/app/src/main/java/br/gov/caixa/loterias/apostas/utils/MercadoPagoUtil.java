package br.gov.caixa.loterias.apostas.utils;


import android.content.Context;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.ErrorResponse;
import br.gov.caixa.loterias.apostas.model.bean.MPErrorResponse;
import br.gov.caixa.loterias.apostas.model.dao.crud.MercadoPagoCRUD;

public class MercadoPagoUtil {

    public static final String CUSTOMER_NOT_ALLOWED_TO_OPERATE = "2021";
    public static final String COLLECTOR_NOT_ALLOWED_TO_OPERATE = "2022";
    public static final String INVALID_USERS_INVOLVED = "2035";
    public static final String CUSTOMER_EQUAL_TO_COLLECTOR = "3000";
    public static final String INVALID_CARD_HOLDER_NAME = "E208";
    public static final String UNAUTHORIZED_CLIENT = "3010";
    public static final String PAYMENT_METHOD_NOT_FOUND = "3012";
    public static final String INVALID_SECURITY_CODE = "E203";
    public static final String INVALID_SECURITY_CODE_2 = "3013";
    public static final String SECURITY_CODE_REQUIRED = "3014";
    public static final String INVALID_PAYMENT_METHOD = "3015";
    public static final String INVALID_CARD_NUMBER = "3017";
    public static final String EMPTY_EXPIRATION_MONTH = "3019";
    public static final String EMPTY_EXPIRATION_YEAR = "3020";
    public static final String EMPTY_CARD_HOLDER_NAME = "3021";
    public static final String EMPTY_DOCUMENT_NUMBER = "3022";
    public static final String EMPTY_DOCUMENT_TYPE = "3023";
    public static final String INVALID_PAYMENT_TYPE_ID = "3028";
    public static final String INVALID_SECURITY_CODE_LENGTH = "3032";
    public static final String INVALID_PAYMENT_METHOD_ID = "3029";
    public static final String INVALID_CARD_EXPIRATION_MONTH_2 = "3030";
    public static final String INVALID_CARD_EXPIRATION_MONTH = "E204";
    public static final String INVALID_CARD_EXPIRATION_YEAR_2 = "4000";
    public static final String INVALID_CARD_EXPIRATION_YEAR = "E205";
    public static final String INVALID_PAYER_EMAIL = "4050";
    public static final String INVALID_PAYMENT_WITH_ESC = "2107";
    public static final String INVALID_IDENTIFICATION_NUMBER = "2067";
    public static final String INVALID_CARD_HOLDER_IDENTIFICATION_NUMBER = "324";
    public static final String INVALID_ESC = "E216";
    public static final String INVALID_EXPIRATION_DATE = "301";
    public static final String INVALID_FINGERPRINT = "E217";

    public static final String  PRE_EMPTY_CARD_NUMBER = "205";
    public static final String  PRE_EMPTY_EXPIRATION_MONTH = "208";
    public static final String  PRE_EMPTY_EXPIRATION_YEAR = "209";
    public static final String  PRE_EMPTY_DOC_TYPE = "212";
    public static final String  PRE_EMPTY_CARD_HOLDER_DOCUMENT_SUBTYPE = "213";
    public static final String  PRE_EMPTY_DOC_NUMBER = "214";
    public static final String  PRE_EMPTY_CARD_ISSUER_ID = "220";
    public static final String  PRE_EMPTY_CARD_HOLDER_NAME = "221";
    public static final String  PRE_EMPTY_SECUTITY_CODE = "224";
    public static final String  PRE_INVALID_CARD_NUMBER = "E301";
    public static final String  PRE_INVALID_SECURITY_CODE = "E302";
    public static final String  PRE_INVALID_CARD_HOLDER_NAME = "316";
    public static final String  PRE_INVALID_DOC_TYPE = "322";
    public static final String  PRE_INVALID_CARD_HOLDER_DOCUMENT_SUBTYPE = "323";
    public static final String  PRE_INVALID_EXPIRATION_MONTH = "325";
    public static final String  PRE_INVALID_EXPIRATION_YEAR = "326";
    public static final String  PRE_INVALID_COUNTRIES = "106";
    public static final String  PRE_INVALID_CARD_NUMBER_PAYMENT_METHOD = "109";
    public static final String  PRE_INVALID_ACTION_PAYMENTE_STATE = "126";
    public static final String  PRE_INVALID_PAY_AMOUNT_PAYMENT_METHOD = "129";
    public static final String  PRE_INVALID_INVALID_USER = "145";
    public static final String  PRE_INVALID_PAYER_ID_BLOCKED = "150";
    public static final String  PRE_INVALID_PAYER_ID_BLOCKED_PAYMENT_METHOD = "151";
    public static final String  PRE_INVALID_NOT_PERMISSION = "160";
    public static final String  PRE_INVALID_UNAVALIABLE_PAYMENT_METHOD = "204";
    public static final String  PRE_INVALID_MAIL_MP = "801";
    public static final String  PRE_INVALID_PAYMENT_METHOD = "MP_PAYMENT_METHOD_NOT_FOUND";


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
            case INVALID_SECURITY_CODE :
            case INVALID_SECURITY_CODE_2:
            case INVALID_SECURITY_CODE_LENGTH:
                return context.getString(R.string.mercado_pago_codigo_seguranca_invalido);
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
            case INVALID_CARD_EXPIRATION_MONTH :
            case INVALID_CARD_EXPIRATION_MONTH_2:
                return context.getString(R.string.mercado_pago_mes_invalido);
            case INVALID_CARD_EXPIRATION_YEAR :
            case INVALID_CARD_EXPIRATION_YEAR_2:
                return context.getString(R.string.mercado_pago_ano_invalido);
            case INVALID_PAYER_EMAIL : return context.getString(R.string.mercado_pago_email_invalido);
            case INVALID_PAYMENT_WITH_ESC : return context.getString(R.string.mercado_pago_email_invalido);
            case INVALID_IDENTIFICATION_NUMBER : return context.getString(R.string.mercado_pago_digito_verificador_invalido);
            //Token creation error cause codes
            case INVALID_CARD_HOLDER_IDENTIFICATION_NUMBER : return context.getString(R.string.mercado_pago_digito_verificador_titular_invalido);
            //Rever
            //case INVALID_ESC : return context.getString(R.string.);
            case INVALID_FINGERPRINT : return context.getString(R.string.mercado_pago_digital_invalida);
            case INVALID_EXPIRATION_DATE: return context.getString(R.string.mercado_pago_data_expiracao_invalida);
            case PRE_EMPTY_CARD_NUMBER: return context.getString(R.string.mercado_pago_digite_numero_cartao);
            case PRE_EMPTY_EXPIRATION_MONTH: return context.getString(R.string.mercado_pago_escolha_mes);
            case PRE_EMPTY_EXPIRATION_YEAR: return context.getString(R.string.mercado_pago_escolha_ano);
            case PRE_EMPTY_DOC_TYPE: return context.getString(R.string.mercado_pago_informe_documento);
            case PRE_EMPTY_CARD_HOLDER_DOCUMENT_SUBTYPE:
            return context.getString(R.string.mercado_pago_informe_documento);
            case PRE_EMPTY_DOC_NUMBER:
            return context.getString(R.string.mercado_pago_informe_documento);
            case PRE_EMPTY_CARD_ISSUER_ID:
            return context.getString(R.string.mercado_pago_informe_banco_emissor);
            case PRE_EMPTY_CARD_HOLDER_NAME:
            return context.getString(R.string.mercado_pago_digite_nome_sobrenome);
            case PRE_EMPTY_SECUTITY_CODE:
            return context.getString(R.string.mercado_pago_digite_codigo_seguranca);
            case PRE_INVALID_CARD_NUMBER:
            return context.getString(R.string.mercado_pago_erro_numero_cartao_digite_novamente);
            case PRE_INVALID_SECURITY_CODE:
            return context.getString(R.string.mercado_pago_confira_cod_seguranca);
            case PRE_INVALID_CARD_HOLDER_NAME:
            return context.getString(R.string.mercado_pago_digite_nome_valido);
            case PRE_INVALID_DOC_TYPE:
            return context.getString(R.string.mercado_pago_confira_documento);
            case PRE_INVALID_CARD_HOLDER_DOCUMENT_SUBTYPE:
            return context.getString(R.string.mercado_pago_confira_documento);
            case PRE_INVALID_EXPIRATION_MONTH:
            return context.getString(R.string.mercado_pago_confira_data);
            case PRE_INVALID_EXPIRATION_YEAR:
            return context.getString(R.string.mercado_pago_confira_data);
            case PRE_INVALID_COUNTRIES:
            return context.getString(R.string.mercado_pago_sem_pagamento_usuarios_outro_pais);
            case PRE_INVALID_CARD_NUMBER_PAYMENT_METHOD:
            return context.getString(R.string.mercado_pago_escolha_outro_cartao_ou_forma_pagamento);
            case PRE_INVALID_ACTION_PAYMENTE_STATE:
            return context.getString(R.string.mercado_pago_nao_conseguimos_processar_pagamento);
            case PRE_INVALID_PAY_AMOUNT_PAYMENT_METHOD:
            return context.getString(R.string.mercado_pago_nao_processamos_o_pagamento_escolha_outro_cartao_forma_pagamento);
            case PRE_INVALID_INVALID_USER:
            return context.getString(R.string.mercado_pago_usuario_teste_real);
            case PRE_INVALID_PAYER_ID_BLOCKED:
            return context.getString(R.string.mercado_pago_nao_pode_efetuar_pagamento);
            case PRE_INVALID_PAYER_ID_BLOCKED_PAYMENT_METHOD: return context.getString(R.string.mercado_pago_nao_pode_efetuar_pagamento);
            case PRE_INVALID_NOT_PERMISSION: return context.getString(R.string.mercado_pago_nao_conseguimos_processar_pagamento);
            case PRE_INVALID_UNAVALIABLE_PAYMENT_METHOD:
            return context.getString(R.string.mercado_pago_meio_pagamento_nao_disponivel);
            case PRE_INVALID_MAIL_MP:
            return context.getString(R.string.mercado_pago_realizaou_pagamento_similar);
            case PRE_INVALID_PAYMENT_METHOD:
            return context.getString(R.string.mercado_pago_erro_payment_not_found);
            default:
                return context.getString(R.string.mercado_pago_erro_default);
        }
    }

    public static String getPublicKeyMercadoPago(){
        String          publicKey = "";
        MercadoPagoCRUD crud      = new MercadoPagoCRUD(Aplicacao.application.getApplicationContext());

        if (crud.existe((long) 1)){
            publicKey = crud.getPublicKeyMercadoPago();
        }
        return publicKey;
    }

    public static boolean checaPrecisaAtualizarKeyPublica(VolleyError error){
        boolean precisaAtualizar = false;
        String          body          = "";
        MPErrorResponse errorResponse = null;
        if(error.networkResponse.data!=null) {
            try {
                body          = new String(error.networkResponse.data,"UTF-8");
                errorResponse = new Gson().fromJson(body, MPErrorResponse.class);
                if (isStatusKeyInvalida(errorResponse)){
                    precisaAtualizar = true;
                }
            } catch (UnsupportedEncodingException e) {
            } catch (Exception e){
            }
        }

        return precisaAtualizar;
    }

    private static boolean isMessageKeyInvalida(MPErrorResponse errorResponse) {
        return errorResponse.getMessage().contains("invalid request") ||
                errorResponse.getMessage().contains("Field: client_id is required and cannot be null") ||
                errorResponse.getMessage().contains("not found public_key") ||
                errorResponse.getMessage().contains("Invalid access parameters");
    }

    private static boolean isStatusKeyInvalida(MPErrorResponse errorResponse) {
        return errorResponse.getStatus() < 200 || errorResponse.getStatus() > 230;
    }

    public static boolean checaPrecisaAtualizarKeyPublicaComRespostaSilce(VolleyError error){
        boolean precisaAtualizar = false;
        String        body          = "";
        ErrorResponse errorResponse = null;
        if(error.networkResponse.data!=null) {
            try {
                body          = new String(error.networkResponse.data,"UTF-8");
                errorResponse = new Gson().fromJson(body, ErrorResponse.class);
                if (errorResponse.getCodigo().equalsIgnoreCase("019012") && errorResponse.getMensagem().contains("2006")){
                    precisaAtualizar = true;
                }
            } catch (UnsupportedEncodingException e) {
            } catch (Exception e){
            }
        }

        return precisaAtualizar;
    }

}