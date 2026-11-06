package br.gov.caixa.loterias.apostas.view.fragment;


import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.JustifyContent;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.RapidaoConfigSingleton;
import br.gov.caixa.loterias.apostas.utils.CustomTypefaceSpan;
import br.gov.caixa.loterias.apostas.utils.SafeFlexboxLayoutManager;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.SugestoesPremioMinimoAdapter;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;

import static br.gov.caixa.loterias.apostas.R.color.amareloQueimado;

public class ConfiguracaoRapidaoFragment2 extends Fragment {

	private RecyclerView rvSugestoes;
	private ArrayList<String> listaSugestoes;
	private LinkedHashMap<Long,String> mapValores;
	private LinkedHashMap<Long,String> mapSugestoes;
	private NumberPicker numberPicker;
	private Boolean temValorConfigurado = false;
	private SugestoesPremioMinimoAdapter modalidadesAdapter;
	private TextView tvPmDesc;
	private  View view = null;

	public ConfiguracaoRapidaoFragment2() { }

	public static ConfiguracaoRapidaoFragment2 newInstance() {
		ConfiguracaoRapidaoFragment2 fragment = new ConfiguracaoRapidaoFragment2();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.fragment_configuracao_rapidao_fragment2, container, false);
			setaViews(view);
			setaMetodos();
		}
		refletePremioMinimoNoPicker();
		return view;
	}

	private void refletePremioMinimoNoPicker() {
		Integer rowPremio = RapidaoConfigSingleton.getInstance().getRapidaoConfig().getValorMinimoPremioPrincipal().intValue() / 1000000;

		numberPicker.setValue(rowPremio);

		atualizaSugestoes();
	}

	private void setaViews(View view){
		rvSugestoes = view.findViewById(R.id.rv_sugestoes);
		numberPicker = view.findViewById(R.id.np_premio_minimo);
		tvPmDesc = view.findViewById(R.id.tv_pm_desc);

		tvPmDesc.setText(getStringDescricao(),TextView.BufferType.SPANNABLE);
	}

	private void setaMetodos(){
		mapValores = montaValores();
		mapSugestoes = configuraSugestoes();
		listaSugestoes = new ArrayList(mapSugestoes.values());


		//adapter
		modalidadesAdapter = new SugestoesPremioMinimoAdapter(getContext(),listaSugestoes, onItemClick());
		rvSugestoes.setAdapter(modalidadesAdapter);

		//layout manager
		SafeFlexboxLayoutManager layoutManager = new SafeFlexboxLayoutManager(getActivity());
		layoutManager.setFlexWrap(FlexWrap.WRAP);
		layoutManager.setFlexDirection(FlexDirection.ROW);
		layoutManager.setAlignItems(AlignItems.CENTER);
		layoutManager.setJustifyContent(JustifyContent.CENTER);
		rvSugestoes.setLayoutManager(layoutManager);

		//number picker
		if(!temValorConfigurado){
			numberPicker.setMinValue(0);
		}
		numberPicker.setMaxValue(mapValores.size()-1);
		numberPicker.setDisplayedValues(mapValores.values().toArray(new String[mapValores.size()]));
		numberPicker.setWrapSelectorWheel(true);

		//metodos
//		rvSugestoes.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), (view1, position) -> {
//			modalidadesAdapter.setSelectedPosition(position);
//			modalidadesAdapter.notifyDataSetChanged();
//			numberPicker.setValue(pegaIndiceDoPicker(position));
//			RapidaoConfigSingleton.getInstance().getRapidaoConfig().setValorMinimoPremioPrincipal(new BigDecimal(numberPicker.getValue() * 1000000));
//
//		}));

		numberPicker.setOnValueChangedListener((numberPicker, oldVal, newVal) -> {
			atualizaSugestoes();
		});
	}

	private OnItemClickListener onItemClick() {
		return (holder, position) -> {
			modalidadesAdapter.setSelectedPosition(position);
			modalidadesAdapter.notifyDataSetChanged();
			numberPicker.setValue(pegaIndiceDoPicker(position));
			RapidaoConfigSingleton.getInstance().getRapidaoConfig().setValorMinimoPremioPrincipal(new BigDecimal(numberPicker.getValue() * 1000000));
		};
	}

	private void atualizaSugestoes(){
		switch (numberPicker.getValue()){
			case 0:
				modalidadesAdapter.setSelectedPosition(0);
				break;
			case 20:
				modalidadesAdapter.setSelectedPosition(1);
				break;
			case 40:
				modalidadesAdapter.setSelectedPosition(2);
				break;
			case 60:
				modalidadesAdapter.setSelectedPosition(3);
				break;
			case 80:
				modalidadesAdapter.setSelectedPosition(4);
				break;
			case 100:
				modalidadesAdapter.setSelectedPosition(5);
				break;
				default:modalidadesAdapter.setSelectedPosition(-1);
		}
		modalidadesAdapter.notifyDataSetChanged();
		RapidaoConfigSingleton.getInstance().getRapidaoConfig().setValorMinimoPremioPrincipal(new BigDecimal(numberPicker.getValue() * 1000000));
	}

	private int pegaIndiceDoPicker(int position){
		int cont = 0;
		List<Long> sugestoes = new ArrayList<>(mapSugestoes.keySet());
		List<Long> valores = new ArrayList<>(mapValores.keySet());
		Long keySugestoes = new Long(sugestoes.get(position));
		for (Long val:valores) {
			if(val.equals(keySugestoes)){
				return cont;
			}
			cont++;
		}
		return cont;
	}

	private Long pegaIndiceDoPicker(Long value){
		Long cont = new Long(0);
		List<Long> valores = new ArrayList<>(mapValores.keySet());
		for (Long val:valores) {
			if(val.equals(value)){
				return cont;
			}
			cont++;
		}
		return cont;
	}

	private  LinkedHashMap<Long,String>  configuraSugestoes(){
		LinkedHashMap<Long,String> sugestoes = new LinkedHashMap<>();
		int milhao = 1000000;
		int intervalo = 20;
		int maxSugestoes = 5;

		sugestoes.put(new Long(0), "Qualquer valor");

		for(int cont = 1; cont <= maxSugestoes; cont++) {
			sugestoes.put(new Long(intervalo * cont  * milhao), intervalo * cont +" Mi");
		}

		return sugestoes;
	}

	private LinkedHashMap<Long,String> montaValores() {
		LinkedHashMap<Long,String> valores = new LinkedHashMap<>();
		int qtdMaxMilhoes = 100;
		int milhao = 1000000;

		for(int cont = 0; cont <= qtdMaxMilhoes; cont++) {
			if(cont == 0){
				valores.put(new Long(0),"Qualquer valor");
			} else {
				valores.put(new Long(cont * milhao),"R$ "+cont+".000.000");
			}
		}
		return valores;
	}

	private Spannable getStringDescricao(){
		Typeface font    = ViewUtils.getFontCaixaStdBold(getActivity() );

		String   normal1 = "O ";
		String   amarelo = "Rapidão";
		String   normal2 = " irá escolher apenas concursos com premiação ";
		String   bold1   = "maior";
		String   normal3 = " ou ";
		String   bold2   = "igual";
		String   normal4 = " ao valor selecionado.";

		String    finalString  = normal1+amarelo+normal2+bold1+normal3+bold2+normal4;
		Spannable sb           = new SpannableString(finalString);

		sb.setSpan(new ForegroundColorSpan(getActivity().getResources().getColor(amareloQueimado)),
				   finalString.indexOf(amarelo),
				   finalString.indexOf(amarelo)+ amarelo.length(),
				   Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		sb.setSpan(new CustomTypefaceSpan("", font),
				   finalString.indexOf(amarelo),
				   finalString.indexOf(amarelo)+ amarelo.length(),
				   Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		sb.setSpan(new CustomTypefaceSpan("", font),
				   finalString.indexOf(bold1),
				   finalString.indexOf(bold1)+ bold1.length(),
				   Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		sb.setSpan(new CustomTypefaceSpan("", font),
				   finalString.indexOf(bold2),
				   finalString.indexOf(bold2)+ bold2.length(),
				   Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		return sb;
	}
}