package br.gov.caixa.loterias.feature.payment.data.mapper

import br.gov.caixa.loterias.feature.payment.domain.model.CardPaymentRequest
import br.gov.caixa.loterias.feature.payment.domain.model.PaymentCardSelection
import com.google.gson.JsonNull
import com.google.gson.JsonObject

/** Preserva os indicadores textuais e os nulos JSON explícitos do endpoint de registro. */
internal object CardPurchaseRequestMapper {
    fun map(request: CardPaymentRequest, tokenAndMethod: Pair<String, String>): JsonObject {
        val saved = request.selection is PaymentCardSelection.Saved
        val save = (request.selection as? PaymentCardSelection.New)?.draft?.save
        return JsonObject().apply {
            addProperty("meioPagamento", request.paymentMethodId)
            add("parametrosPagamento", JsonObject().apply {
                addProperty("TOKEN", tokenAndMethod.first)
                if (saved) addProperty("SAVED_CARD", "true") else add("SAVED_CARD", JsonNull.INSTANCE)
                addProperty("PAYMENT_METHOD_ID", tokenAndMethod.second)
                if (save != null) addProperty("SAVE_CARD", save.toString()) else add("SAVE_CARD", JsonNull.INSTANCE)
            })
        }
    }
}
