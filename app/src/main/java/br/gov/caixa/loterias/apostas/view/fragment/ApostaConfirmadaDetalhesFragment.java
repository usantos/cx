package br.gov.caixa.loterias.apostas.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.io.Serializable;
import java.math.BigDecimal;

import br.gov.caixa.loterias.apostas.view.activity.ResultadoApostaConfirmadaActivity;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComprovanteApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfiguracaoNumeros;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfiguracaoNumerosSuperSete;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacaoAposta;
import br.gov.caixa.loterias.apostas.model.model.ApostaConfirmadaDetalheModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;

public abstract class ApostaConfirmadaDetalhesFragment extends Fragment {
	private static final String ARG_RESULTADO = "ARG_RESULTADO";
	private static final String ARG_COMPROVANTE = "ARG_COMPROVANTE";
	private static final String ARG_VALOR_PREMIO = "ARG_VALOR_PREMIO";

	private ResultadoConcursoDTO resultadoConcurso;
	private ComprovanteApostaDTO comprovanteApostaDTO;
	private BigDecimal valorDoPremio;
	private DetalhesPremioDTO detalhesPremio;

	private ApostaConfirmadaDetalheModel model;


	public Bundle getBundle(ResultadoConcursoDTO resultado,
							ComprovanteApostaDTO comprovante,
							BigDecimal valorPremio){

		Bundle               args     = new Bundle();
		args.putSerializable(ARG_RESULTADO, new Gson().toJson(resultado));
		args.putSerializable(ARG_COMPROVANTE, new Gson().toJson(comprovante));
		args.putSerializable(ARG_VALOR_PREMIO, new Gson().toJson(valorPremio));

		return args;
	}

	public void recuperaArgs(){
		if (getArguments() != null) {
			String resultadoJson = getArguments().getString(ARG_RESULTADO);
			String comprovanteJson = getArguments().getString(ARG_COMPROVANTE);
			String valorPremioJson = getArguments().getString(ARG_VALOR_PREMIO);
			if (resultadoJson != null && !resultadoJson.isEmpty()){
				resultadoConcurso = new Gson().fromJson(resultadoJson, ResultadoConcursoDTO.class);
			}
			if (comprovanteJson != null && !comprovanteJson.isEmpty()){
				comprovanteApostaDTO = new Gson().fromJson(comprovanteJson, ComprovanteApostaDTO.class);
			}
			if (valorPremioJson != null && !valorPremioJson.isEmpty()){
				valorDoPremio = new Gson().fromJson(valorPremioJson, BigDecimal.class);
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		recuperaArgs();
		model = new ApostaConfirmadaDetalheModel(getActivity());
	}

	public void irParaResgate(Long id) {
		if (detalhesPremio != null){
			startResgateActivity();
		} else {
			buscaDetalhePremio(id, new OnSilceListener<DetalhesPremioDTO>() {
				@Override
				public void success(DetalhesPremioDTO payload) {
					startResgateActivity();
				}

				@Override
				public void error(VolleyError error) {}
			});
		}
	}

	public void buscaDetalhePremio(Long id, OnSilceListener<DetalhesPremioDTO> listener){
		AlertDialogUtils.show(getActivity());
		model.buscaDetalhePremio(id, new OnSilceListener<DetalhesPremioDTO>() {
			@Override
			public void success(DetalhesPremioDTO payload) {
				AlertDialogUtils.dismiss();

				detalhesPremio = payload;
				listener.success(payload);
			}

			@Override
			public void error(VolleyError error) {
				AlertDialogUtils.dismiss();
				listener.error(error);
			}
		});
	}

	private void startResgateActivity() {
		//ResultadoApostaConfirmadaActivity_.intent(getContext()).detalhesPremioDTO(detalhesPremio)
		//		.comprovante(comprovanteApostaDTO).start();

		Intent intent = IntentUtil.getIntentOrigemDestino((Activity) getContext(), ResultadoApostaConfirmadaActivity.class);
		intent.putExtra(ResultadoApostaConfirmadaActivity.DETALHES_PREMIO_DTO_EXTRA, ((Serializable) detalhesPremio));
		intent.putExtra(ResultadoApostaConfirmadaActivity.COMPROVANTE_EXTRA, ((Serializable) comprovanteApostaDTO));
		((Activity) getContext()).startActivity(intent);

	}

	public boolean isApostaPremiada(IdentificaoDeUmaApostaDas8Modalidades aposta) {
		return aposta.getSituacao().getValor() == SituacaoAposta.PREMIADA;
	}
	public void setDetalhesPremio(DetalhesPremioDTO detalhe){
		detalhesPremio = detalhe;
	}

	public BigDecimal getValorDoPremio(){
		return this.valorDoPremio;
	}

	public void setValorDoPremio(BigDecimal valor){
		this.valorDoPremio = valor;
	}

	public ResultadoConcursoDTO getResultadoConcurso(){
		return this.resultadoConcurso;
	}

	public ConfiguracaoNumerosSuperSete getEstiloSuperSete(IdentificaoDeUmaApostaDas8Modalidades aposta){
		return model.configuraEstiloSuperSete(aposta);
	}

	public ConfiguracaoNumeros getEstiloNumeros(IdentificaoDeUmaApostaDas8Modalidades aposta){
		return model.configuraEstiloNumeros(aposta);
	}

}