package br.gov.caixa.loterias.apostar.interno.view.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bean.TipoAposta;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ModalidadeDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ModalidadeDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ModalidadeEnumSelecaoModalidades;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.RapidaoDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.internals.RapidaoConfigSingleton;
import br.gov.caixa.loterias.apostar.interno.view.adapter.TiposApostasMultiplaSelecaoAdapter;
import br.gov.caixa.loterias.apostar.interno.view.custom.ExpandableHeightGridView;
import br.gov.caixa.loterias.apostar.interno.view.custom.LoadingViewLoterias;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConfiguracaoRapidaoFragment4 extends Fragment {

	List<ModalidadeDTO> modalidades = new ArrayList<>();
	List<TipoAposta> tiposAposta = new ArrayList<>();
	List<TipoAposta> tiposApostasSalvas = new ArrayList<>();
	ScrollView scrollModalides;
	ExpandableHeightGridView gridViewTiposAposta;
	private View view = null;
	private RapidaoDTO rapidaoConfig = RapidaoConfigSingleton.getInstance().getRapidaoConfig();

	public ConfiguracaoRapidaoFragment4() {
		populaArraysTipoAposta(tiposApostasSalvas,rapidaoConfig.getModalidades());
	}

	public static ConfiguracaoRapidaoFragment4 newInstance() {
		ConfiguracaoRapidaoFragment4 fragment = new ConfiguracaoRapidaoFragment4();
		return fragment;
	}


	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		if(view == null){
			view = inflater.inflate(R.layout.fragment_configuracao_rapidao_fragment4, container, false);
			setaViews(view);
		}
		return view;
	}

	private void gridViewTipoAposta () {
		TiposApostasMultiplaSelecaoAdapter tiposApostasAdapter = new TiposApostasMultiplaSelecaoAdapter(getActivity(), tiposAposta);
		gridViewTiposAposta.setAdapter(tiposApostasAdapter);
		gridViewTiposAposta.setExpanded(true);
	}

	private void setaViews (View view){
		scrollModalides = view.findViewById(R.id.sv_modalidades_rapidao_config);
		gridViewTiposAposta = view.findViewById(R.id.ehgv_grid_modalidades);
		callWebservice();
	}

	private void callWebservice() {

		final AlertDialog loadViewProgress = LoadingViewLoterias.show(getContext());
		final ApostaBO apostaBO = ApostaBO_.getInstance_(getContext());

		apostaBO.getModalidades(new RequestListener<ModalidadeDTOResponse>() {
			@Override
			public void onResponse(ModalidadeDTOResponse response) {
				loadViewProgress.dismiss();
				modalidades = response.getPayload();

				if (response.getRedirect() != null) {
					RedirectNetwork.checkRedirectSucesso(response.getRedirect(), getActivity());
				}
				atualizarLayout();
			}

			@Override
			public void onErrorResponse(VolleyError error) {
				loadViewProgress.dismiss();
				RedirectNetwork.checkRedirect(error, getActivity());
			}
		});
	}

	private void atualizarLayout() {
		montarArrayTipoApostas();
	}

	private void populaArraysTipoAposta(List<TipoAposta> array, List<ModalidadeDTO> modalidades) {
		for (ModalidadeDTO item : modalidades) {
			TipoAposta tipoAposta = null;
			switch (ModalidadeEnumSelecaoModalidades.toString(item.getDescricao())) {
				case QUINA:
					tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo_5, R.color.quinaclaro, false, item.getValor(), item.getDescricaoEspecial());
					break;
				case LOTOFACIL:
					tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo_6, R.color.lotofacilclaro, false, item.getValor(), item.getDescricaoEspecial());
					break;
				case LOTOMANIA:
					tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo_4, R.color.lotomaniaclaro, false, item.getValor(), item.getDescricaoEspecial());
					break;
				case MEGA_SENA:
					tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo_megasena, R.color.megasenaclaro, false, item.getValor(), item.getDescricaoEspecial());
					break;
				case TIMEMANIA:
					tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo_8, R.color.timemaniaclaro, false, item.getValor(), item.getDescricaoEspecial());
					break;
				case DUPLA_SENA:
					tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo_9, R.color.duplasenaclaro, false, item.getValor(), item.getDescricaoEspecial());
					break;
				case DIA_DE_SORTE:
					tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo_2, R.color.diasorteclaro, false, item.getValor(), item.getDescricaoEspecial());
					break;
				case SUPER_SETE:
					tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo_8, R.color.superseteclaro	, false, item.getValor(), item.getDescricaoEspecial());
					break;
				default:
					break;
			}
			if (tipoAposta != null) {
				tipoAposta.setTitulo(tipoAposta.getTitulo().toLowerCase(new Locale("pt", "BR")));
				array.add(tipoAposta);
			}
		}
	}

	private void montarArrayTipoApostas() {
		populaArraysTipoAposta(tiposAposta, modalidades);
		if (tiposAposta != null || tiposAposta.size() > 0) {
			for (TipoAposta tipoAposta : tiposAposta) {
				for (TipoAposta tipoApostaSalva : tiposApostasSalvas) {
					if (tipoApostaSalva.getTitulo().equals(tipoAposta.getTitulo())) {
						tipoAposta.setSelect(true);
					}
				}
			}
			gridViewTipoAposta();
		}
		scrollModalides.post(() -> scrollModalides.fullScroll(ScrollView.FOCUS_UP));
	}
}
