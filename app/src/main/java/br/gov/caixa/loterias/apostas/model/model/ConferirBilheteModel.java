package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;

import com.android.volley.VolleyError;

import br.gov.caixa.loterias.apostas.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RedirectEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConsultaBilheteDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConsultaBilheteDTOResponse;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;

public class ConferirBilheteModel {
	private Activity activity;
	private final ApostaBO apostaBO;

	public ConferirBilheteModel(Activity activity) {
		this.activity = activity;
		this.apostaBO = ApostaBO.getInstance();
	}

	public void conferirBilhete(String codBarras, OnSilceListener<ResultadoConsultaBilheteDTO> listener){
		this.apostaBO.postConferirBilhetes(codBarras, new RequestListener<ResultadoConsultaBilheteDTOResponse>() {
			@Override
			public void onResponse(ResultadoConsultaBilheteDTOResponse response) {
				checkRedirect(response.getRedirect());
				listener.success(response.getPayload());
			}

			@Override
			public void onErrorResponse(VolleyError error) {
				onError(error);
			}
		});
	}

	public void conferirBilheteQrCode(String qrCode, OnSilceListener<ResultadoConsultaBilheteDTO> listener){
		this.apostaBO.postConferirBilhetesNovo(qrCode, new RequestListener<ResultadoConsultaBilheteDTOResponse>() {
			@Override
			public void onResponse(ResultadoConsultaBilheteDTOResponse response) {
				checkRedirect(response.getRedirect());
				listener.success(response.getPayload());
			}
			@Override
			public void onErrorResponse(VolleyError error) {
				onError(error);
			}
		});
	}

	private void checkRedirect(RedirectEnum redirect) {
		if (redirect != null){
			RedirectNetwork.checkRedirectSucesso(redirect, activity);
		}
	}

	private void onError(VolleyError error) {
		RedirectNetwork.checkRedirect(error, activity);
		AlertDialogUtils.dismiss();
	}
}
