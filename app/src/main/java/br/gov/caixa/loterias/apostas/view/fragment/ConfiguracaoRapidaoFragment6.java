package br.gov.caixa.loterias.apostas.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.NumerosSelecionadosRapidao;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.RapidaoConfigSingleton;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.view.adapter.baseadapter.NumeroSelecionadoRapidaoAdapter;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightGridView;

public class ConfiguracaoRapidaoFragment6 extends Fragment implements NumeroSelecionadoRapidaoAdapter.NumeroSelecionadoListener{

	private View view = null;
	private static NumerosSelecionadosRapidao[] listaNumerosSelecionadosRapidao;
	private static List<Integer> numerosLista = new ArrayList<>();
	private static List<Integer> listaNumerosProibidos = new ArrayList<>();
	private static Button btnLimpaNumeros;
	private static TextView tvTitulo;
	private static TextView tvDescricao;
	private static ExpandableHeightGridView gridNumeros;
	private RapidaoConfigSingleton rapidaoConfigSingleton;

	public ConfiguracaoRapidaoFragment6() { }

	public static ConfiguracaoRapidaoFragment6 newInstance() {
		return  new ConfiguracaoRapidaoFragment6();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.fragment_configuracao_rapidao_fragment5, container, false);
			setaViews(view);
			setaClicks();
			setaMetodos();
		}

		return view;
	}

	private void setaViews (View view) {
		gridNumeros = view.findViewById(R.id.ehgv_grid_numeros);
		tvDescricao = view.findViewById(R.id.tv_descricao);
		tvTitulo = view.findViewById(R.id.tv_titulo);
		btnLimpaNumeros = view.findViewById(R.id.btn_limpar_selecao);

		btnLimpaNumeros.setVisibility(View.GONE);
	}

	private void setaClicks(){
		btnLimpaNumeros.setOnClickListener(view -> {
			for (NumerosSelecionadosRapidao numero : listaNumerosSelecionadosRapidao){
				numero.setSelecionado(false);
			}
			reloadGridView();
		});
	}

	public void setaMetodos(){
		rapidaoConfigSingleton = RapidaoConfigSingleton.getInstance();

		listaNumerosProibidos = rapidaoConfigSingleton.getRapidaoConfig().getPrognosticosObrigatorios();

		numerosLista = rapidaoConfigSingleton.getRapidaoConfig().getPrognosticosProibidos();
		AppCenterManager.registraEvento("ENTROU_SELECIONA_NUMEROS_PROIBIDOS_CONFIG_RAPIDAO");
		tvTitulo.setText(getString(R.string.conf_rapd_proibidos_titulo));
		tvDescricao.setText(getString(R.string.conf_rapd_proibidos_desc));

		gridViewNumeros();
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	private List<Integer> getNumerosSelecionados(){

		List<Integer> numSelecionados = new ArrayList<>();
		for (NumerosSelecionadosRapidao num: listaNumerosSelecionadosRapidao) {
			if (num.getSelecionado() == true) {
				numSelecionados.add(num.getNumero());
			}
		}
		return  numSelecionados;
	}

	private void reloadGridView(){
		NumeroSelecionadoRapidaoAdapter numerosSlcRapidaoAdapter = new NumeroSelecionadoRapidaoAdapter(getActivity(),
																									   getActivity(),
																									   listaNumerosSelecionadosRapidao,
																									   false,
																									   listaNumerosProibidos,
																									   this);
		gridNumeros.setAdapter(numerosSlcRapidaoAdapter);
		gridNumeros.setExpanded(true);
		mudarLayoutBotaoLimparSelecao(false);
	}

	private void gridViewNumeros(){
		listaNumerosSelecionadosRapidao = new NumerosSelecionadosRapidao[100];
		for (int i =0 ; i<100; i++) {
			NumerosSelecionadosRapidao numeSel = new NumerosSelecionadosRapidao(i+1, false);
			for(Integer numerosalvo : numerosLista){
				if (numerosalvo == i+1) {
					numeSel.setSelecionado(true);
					btnLimpaNumeros.setVisibility(View.VISIBLE);
				}
			}
			listaNumerosSelecionadosRapidao[i] = numeSel;
		}


		NumeroSelecionadoRapidaoAdapter numerosSlcRapidaoAdapter = new NumeroSelecionadoRapidaoAdapter(getActivity(),
																									   getActivity(),
																									   listaNumerosSelecionadosRapidao,
																									   false,
																									   listaNumerosProibidos,
																									   this);
		gridNumeros.setAdapter(numerosSlcRapidaoAdapter);
		gridNumeros.setExpanded(true);
	}

	@Override
	public void mudarLayoutBotaoLimparSelecao(Boolean visible) {
		if (visible) {
			btnLimpaNumeros.setVisibility(View.VISIBLE);
		}else {
			btnLimpaNumeros.setVisibility(View.GONE);
		}
		rapidaoConfigSingleton.getRapidaoConfig().setPrognosticosProibidos(getNumerosSelecionados());
	}
}
