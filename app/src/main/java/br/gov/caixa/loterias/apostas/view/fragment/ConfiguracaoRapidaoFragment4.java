package br.gov.caixa.loterias.apostas.view.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.TipoAposta;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnumSelecaoModalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RapidaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.RapidaoConfigSingleton;
import br.gov.caixa.loterias.apostas.model.model.ModalidadeModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.Aplicacao;
import br.gov.caixa.loterias.apostas.view.adapter.baseadapter.TiposApostasMultiplaSelecaoAdapter;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightGridView;

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

		AlertDialogUtils.show(getActivity());

		new ModalidadeModel(getActivity()).buscaModalidades(new OnSilceListener<List<ModalidadeDTO>>() {
			@Override
			public void success(List<ModalidadeDTO> payload) {
				AlertDialogUtils.dismiss();
				modalidades = payload;
				atualizarLayout();
			}

			@Override
			public void error(VolleyError error) {
				AlertDialogUtils.dismiss();
			}
		});
	}

	private void atualizarLayout() {
		montarArrayTipoApostas();
	}

	private void populaArraysTipoAposta(List<TipoAposta> array, List<ModalidadeDTO> modalidades) {
		for (ModalidadeDTO item : modalidades) {
			TipoAposta tipoAposta = null;
			switch (ModalidadeEnumSelecaoModalidades.toString(item.getDescricao().toLowerCase())) {
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
				case MAIS_MILIONALIA:
					tipoAposta = new TipoAposta(Aplicacao.application.getString(R.string.label_mais_milionaria), R.drawable.trevo_mais_milionaria_nao_selecionado, R.color.mais_milionaria_clara, false, item.getValor(), item.getDescricaoEspecial());
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
