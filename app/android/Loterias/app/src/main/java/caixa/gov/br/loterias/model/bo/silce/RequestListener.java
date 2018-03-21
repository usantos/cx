package caixa.gov.br.loterias.model.bo.silce;

import com.android.volley.VolleyError;

/**
 * Created by pmotta on 15/03/2018.
 */

public interface RequestListener<T> {
    void onResponse(T result);
    void onErrorResponse(VolleyError error);
}
