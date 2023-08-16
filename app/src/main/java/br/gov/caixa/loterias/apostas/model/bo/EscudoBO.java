package br.gov.caixa.loterias.apostas.model.bo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.android.volley.VolleyError;

import java.util.List;

import br.gov.caixa.loterias.apostas.controllers.PrincipalActivity;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.EquipeEscudoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.EscudoEquipeEsportivaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.EscudoEquipeEsportivaDTOResponse;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;

public class EscudoBO {

    private static EscudoBO instance;

    private List<EscudoEquipeEsportivaDTO> listEscudoEquipeEsportivaDTO;

    private EscudoBO(){
    }

    public static EscudoBO getInstance(){
        if (instance == null){
            instance = new EscudoBO();
        }
        return instance;
    }

    public void carregaEscudos(Context context) {
        if (listEscudoEquipeEsportivaDTO == null) {
            AlertDialogUtils.show(context);
            ApostaBO apostaBO = ApostaBO_.getInstance_(context);
            apostaBO.getEscudosEquipes(new RequestListener<EscudoEquipeEsportivaDTOResponse>() {
                @Override
                public void onResponse(EscudoEquipeEsportivaDTOResponse response) {
                    if (response != null && response.getPayload() != null) {
                        listEscudoEquipeEsportivaDTO = response.getPayload();
                    }
                    AlertDialogUtils.dismiss();
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    AlertDialogUtils.dismiss();
                }
            });
        }
    }

    public List<EscudoEquipeEsportivaDTO> getListEscudo() {
        return listEscudoEquipeEsportivaDTO;
    }

    public Bitmap buscaEscudo(Integer numeroEquipe) {
        if (listEscudoEquipeEsportivaDTO != null) {
            for (EscudoEquipeEsportivaDTO item : listEscudoEquipeEsportivaDTO) {
                if (item.getEquipe().getId().intValue() == numeroEquipe.intValue()) {
                    String imagemString = item.getImagem();
                    if (imagemString != null) {
                        try {
                            byte[] bytes = Base64.decode(imagemString, Base64.DEFAULT);
                            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        } catch (Exception exception) {
                        }
                    }
                }
            }
        }
        return null;
    }

}
