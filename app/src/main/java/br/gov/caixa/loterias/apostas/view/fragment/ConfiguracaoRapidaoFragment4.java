package br.gov.caixa.loterias.apostas.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.TipoAposta;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnumSelecaoModalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RapidaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.RapidaoConfigSingleton;
import br.gov.caixa.loterias.apostas.model.model.ModalidadeModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.Aplicacao;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.TipoApostaMultiplaSelecaoAdapter;
import br.gov.caixa.loterias.apostas.view.listener.TipoApostaListener;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConfiguracaoRapidaoFragment4 extends Fragment {

	List<ModalidadeDTO> modalidades = new ArrayList<>();
	List<TipoAposta> tiposAposta = new ArrayList<>();
	List<TipoAposta> tiposApostasSalvas = new ArrayList<>();
	ScrollView scrollModalides;
	private RecyclerView gridViewTiposAposta;
	private View view = null;
	private RapidaoDTO rapidaoConfig = RapidaoConfigSingleton.getInstance().getRapidaoConfig();
	private TipoApostaMultiplaSelecaoAdapter adapter;

	public ConfiguracaoRapidaoFragment4() {
		populaArraysTipoAposta(tiposApostasSalvas, rapidaoConfig.getModalidades());
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
		adapter = new TipoApostaMultiplaSelecaoAdapter(tiposAposta, onItemClicked());
		gridViewTiposAposta.setLayoutManager(new GridLayoutManager(getContext(), 2));
		gridViewTiposAposta.setAdapter(adapter);
	}

	private TipoApostaListener onItemClicked() {
		return (tipoAposta, position) -> {
			if (tipoAposta.getSelect()){
				tipoAposta.setSelect(false);
			}else{
				tipoAposta.setSelect(true);
			}

			tiposAposta.set(position, tipoAposta);
			adapter.notifyItemChanged(position);
			RapidaoConfigSingleton.getInstance().getRapidaoConfig().setModalidades(convertTipoApostaParaModalidades());
		};
	}

	private List<ModalidadeDTO> convertTipoApostaParaModalidades() {
		List<ModalidadeDTO> modalidades = new ArrayList<>();
		for (TipoAposta tipoAposta: tiposAposta) {
			if(tipoAposta.getSelect()){
				ModalidadeDTO modalidade = new ModalidadeDTO();
				modalidade.setDescricao(tipoAposta.getTitulo());
				modalidade.setDescricaoEspecial(tipoAposta.getDescricaoEspecial());
				modalidade.setValor(tipoAposta.getValor());
				modalidades.add(modalidade);
			}
		}
		return modalidades;
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
			EstiloModalidadeMKP estilo = new EstiloModalidadeMKP(ModalidadeEnum.fromModalidadeDTO(item));

			TipoAposta tipoAposta = null;
			switch (ModalidadeEnumSelecaoModalidades.toString(item.getDescricao().toLowerCase())) {
				case MAIS_MILIONALIA:
					tipoAposta = new TipoAposta("+Milionária",  estilo.getTrevoFundoClaro(), estilo.getTrevoFundoEscuro(), estilo.getCorClara(), false, item.getValor(), item.getDescricaoEspecial(), estilo.getCorFonteFundoClaro());
					break;
				case LOTECA:
				case LOTOGOL:
				case FEDERAL:
					//nao cria tipo para adicionar na tela
					break;
				default:
					tipoAposta = new TipoAposta(item.getDescricao(), estilo.getTrevoFundoClaro(), estilo.getTrevoFundoEscuro(), estilo.getCorClara(), false, item.getValor(), item.getDescricaoEspecial(), estilo.getCorFonteFundoClaro());
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
