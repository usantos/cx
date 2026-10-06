package br.gov.caixa.loterias.apostas.model.bo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.EscudoEquipeEsportivaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.EscudoEquipeEsportivaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.EscudoImagem;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.ServicoFactoryUtil;
import br.gov.caixa.loterias.apostas.view.listener.OnEscudoListener;

public class EscudoBO {

    private static EscudoBO instance;

    private List<EscudoImagem> listEscudoImagem;

    private EscudoBO(){
    }

    public static EscudoBO getInstance(){
        if (instance == null){
            instance = new EscudoBO();
        }
        return instance;
    }

    public void carregaEscudos(Context context, OnEscudoListener escudoListener) {
        if (listEscudoImagem == null) {
            AlertDialogUtils.show(context);
            ServicoFactoryUtil.getApostaService().getEscudosEquipes(new RequestListener<EscudoEquipeEsportivaDTOResponse>() {
                @Override
                public void onResponse(EscudoEquipeEsportivaDTOResponse response) {
                    if (response != null && response.getPayload() != null) {
                        listEscudoImagem = new ArrayList<>();
                        List<EscudoEquipeEsportivaDTO> listEscudoEquipeEsportivaDTO = response.getPayload();
                        for (EscudoEquipeEsportivaDTO item : listEscudoEquipeEsportivaDTO) {
                            String imagemString = item.getImagem();
                            if (imagemString != null) {
                                try {
                                    byte[] bytes = Base64.decode(imagemString, Base64.DEFAULT);
                                    Bitmap imgBm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    listEscudoImagem.add(new EscudoImagem(
                                            item.getId(), item.getEquipe().getId().intValue(), imgBm));
                                } catch (Exception exception) {
                                }
                            }
                        }
                    }
                    AlertDialogUtils.dismiss();
                    escudoListener.aposCarregarEscudos();
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    AlertDialogUtils.dismiss();
                    escudoListener.aposCarregarEscudos();
                }
            });
            //escudoListener.antesCarregarEscudos();
            return;
        }
        escudoListener.aposCarregarEscudos();
    }

    public Bitmap buscaEscudo(Integer numeroEquipe) {
        if (listEscudoImagem != null) {
            for (EscudoImagem item : listEscudoImagem) {
                if (item.getEquipeId().intValue() == numeroEquipe.intValue()) {
                    return item.getImagem();
                }
            }
        }
        return null;
    }

}
