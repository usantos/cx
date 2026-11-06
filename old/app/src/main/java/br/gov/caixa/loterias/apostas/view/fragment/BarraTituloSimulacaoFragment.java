package br.gov.caixa.loterias.apostas.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.TermosUsoActivity;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.utils.EspecialUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.SharedPreferencesUtils;
import br.gov.caixa.loterias.apostas.utils.TextViewUtils;
import br.gov.caixa.loterias.apostas.utils.VectorUtils;

public class BarraTituloSimulacaoFragment extends Fragment {
	private static final String ARG_MODALIDADE = "ARG_MODALIDADE";
	private static final String ARG_CONCURSO = "ARG_CONCURSO";
	private static final String ARG_SORTEIO = "ARG_SORTEIO";
	private static final String ARG_ESPECIAL = "AR_ESPECIAL";
	private static final String ARG_COR_BACK = "AR_COR_BACK";
	private static final String ARG_SHOW_BG = "AR_SHOW_BG";

	private  View view;
	private TextView tvModalidade, tvConcurso, tvSorteio, labelConcurso, labelSorteio;
	private ConstraintLayout background;
	private ImageButton btnVoltar, btnDuvida;
	private LinearLayout containerSorteio, containerConcurso;
	private String concurso;
	private String sorteio;
	private boolean isEspecial, showBackground;
	private int corBackground;
	private ModalidadeEnum modalidade;
	//TODO: MEGA 30 ANOS//
	private boolean isMega30 = false ;
	//TODO: LOTECA PAIS//
	private boolean isLotecaPais = false ;
	private EstiloModalidadeMKP estilo;

	public BarraTituloSimulacaoFragment() {}

	public static BarraTituloSimulacaoFragment newInstance(ModalidadeEnum modalidade, String concuso, String sorteio, boolean especial, int corBackground, boolean showBGEspecial) {
		BarraTituloSimulacaoFragment fragment = new BarraTituloSimulacaoFragment();
		Bundle                  args     = new Bundle();
		args.putString(ARG_MODALIDADE, new Gson().toJson(modalidade));
		args.putSerializable(ARG_CONCURSO, concuso);
		args.putSerializable(ARG_SORTEIO, sorteio);
		args.putBoolean(ARG_ESPECIAL, especial);
		args.putInt(ARG_COR_BACK, corBackground);
		args.putBoolean(ARG_SHOW_BG, showBGEspecial);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			concurso = getArguments().getString(ARG_CONCURSO);
			sorteio = getArguments().getString(ARG_SORTEIO);
			isEspecial = getArguments().getBoolean(ARG_ESPECIAL);
			modalidade = new Gson().fromJson(getArguments().getString(ARG_MODALIDADE), ModalidadeEnum.class);
			corBackground = getArguments().getInt(ARG_COR_BACK);
			isMega30 = EspecialUtils.isMega30(modalidade, Integer.valueOf(concurso), isEspecial);
			isLotecaPais = EspecialUtils.isLotecaPais(modalidade, Integer.valueOf(concurso), isEspecial);
			//TODO: LOTECA PAIS//
			//estilo = new EstiloModalidadeMKP(modalidade, isMega30);
			estilo = EstiloModalidadeMKP.createModalidadeConcursoEsp(modalidade,
					Integer.valueOf(concurso), isEspecial);
			showBackground = getArguments().getBoolean(ARG_SHOW_BG);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_barra_titulo_simulacao, container, false);

		tvModalidade = view.findViewById(R.id.id_modalidade);
		ViewCompat.setAccessibilityHeading(tvModalidade,true);
		tvConcurso = view.findViewById(R.id.id_concurso);
		tvSorteio = view.findViewById(R.id.id_sorteio);
		background = view.findViewById(R.id.layout_fragmento);
		btnVoltar = view.findViewById(R.id.seta_voltar);
		btnDuvida = view.findViewById(R.id.duvida);
		labelConcurso = view.findViewById(R.id.label_concurso);
		labelSorteio = view.findViewById(R.id.label_sorteio);
		containerSorteio = view.findViewById(R.id.container_sorteio);
		containerConcurso = view.findViewById(R.id.container_concurso);
		if(modalidade.equals(ModalidadeEnum.LOTECA)){
			labelSorteio.setText(R.string.label_resultado);
			containerSorteio.setContentDescription(getString(R.string.resultado) + " " + sorteio);
		}

		setClickListener();

		//TODO: LOTECA PAIS//
		isMega30 = EspecialUtils.isMega30(modalidade, Integer.valueOf(concurso), isEspecial);
		isLotecaPais = EspecialUtils.isLotecaPais(modalidade, Integer.valueOf(concurso), isEspecial);

		preencheDados();
		aplicaEstilo();
		aplicaEstiloFonte();

		return view;
	}

	private void setClickListener() {
		btnDuvida.setOnClickListener(v -> {
			Intent intent = new Intent(getContext(), TermosUsoActivity.class);
			startActivity(intent);
		});

		btnVoltar.setOnClickListener(v -> getActivity().onBackPressed());
	}

	private void aplicaEstiloFonte() {
		tvModalidade.setTextColor(ContextCompat.getColor(getContext(), estilo.getCorFonteFundoClaro()));
		tvConcurso.setTextColor(ContextCompat.getColor(getContext(), estilo.getCorFonteFundoClaro()));
		tvSorteio.setTextColor(ContextCompat.getColor(getContext(), estilo.getCorFonteFundoClaro()));
		labelConcurso.setTextColor(ContextCompat.getColor(getContext(), estilo.getCorFonteFundoClaro()));
		labelSorteio.setTextColor(ContextCompat.getColor(getContext(), estilo.getCorFonteFundoClaro()));
		btnDuvida.setImageDrawable(VectorUtils.getShape(R.drawable.duvidas_toolbar_icon, estilo.getCorFonteFundoClaro()));
		btnVoltar.setImageDrawable(VectorUtils.getShape(R.drawable.seta_esquerda, estilo.getCorFonteFundoClaro()));
	}

	private void aplicaEstilo() {
		background.setBackgroundColor(ContextCompat.getColor(getContext(), corBackground));
		//if (isEspecial && estilo.getImagemEspecialDetalhes() != -1 && showBackground){
			//background.setBackground(ContextCompat.getDrawable(getContext(), estilo.getImagemEspecialDetalhes()));
		if (isEspecial && estilo.getImagemEspecialSimples() != -1 && showBackground){
			background.setBackground(ContextCompat.getDrawable(getContext(), estilo.getImagemEspecialSimples()));
		}

		if (((isEspecial && showBackground) || isMega30 || isLotecaPais) && estilo.getImagemEspecialDetalhes() > 0) {
			background.setBackground(AppCompatResources.getDrawable(getContext(), estilo.getImagemEspecialDetalhes()));
		}

	}

	private void preencheDados() {
		if (isEspecial){
			if (modalidade == ModalidadeEnum.LOTOFACIL){
				tvModalidade.setText(ModalidadeEnum.getDescricaoEspecialDuasLinhas(modalidade).toLowerCase());
				TextViewUtils.mudarTamanhoPorPorcentagem(tvModalidade, -20f);
			} else if (isMega30) {
				tvModalidade.setText(SharedPreferencesUtils.getValorString(EspecialUtils.MEGA_DUAS_LINHAS, "Mega-Sena 30 anos"));
			} else if (isLotecaPais) {
				tvModalidade.setText(SharedPreferencesUtils.getValorString(EspecialUtils.LOTECA_PAIS_DUAS_LINHAS, ""));
			} else {
				if (isMega30) {
					tvModalidade.setText(SharedPreferencesUtils.getValorString(EspecialUtils.MEGA_DUAS_LINHAS, "").toLowerCase());
				} else if (isLotecaPais) {
					tvModalidade.setText(SharedPreferencesUtils.getValorString(EspecialUtils.LOTECA_PAIS_DUAS_LINHAS,"").toLowerCase());
				} else {
					if (modalidade == ModalidadeEnum.LOTECA) {
						tvModalidade.setText(ModalidadeEnum.getDescricaoEspecial(modalidade).toLowerCase());
						TextViewUtils.mudarTamanhoPorPorcentagem(tvModalidade, -20f);
					}else {
						tvModalidade.setText(ModalidadeEnum.getDescricaoEspecialDuasLinhas(modalidade).toLowerCase());
					}
				}
			}
		} else {
			tvModalidade.setText(ModalidadeEnum.fromString(modalidade).toLowerCase());
		}
		tvConcurso.setText(concurso);
		tvSorteio.setText(sorteio);
		if(modalidade.equals(ModalidadeEnum.LOTECA)){
			containerSorteio.setContentDescription(getString(R.string.resultado) + " " + sorteio);
		}
		else {
			containerSorteio.setContentDescription(getString(R.string.sorteio) + " " + sorteio);
		}
		containerConcurso.setContentDescription(getString(R.string.concurso) + " " + concurso);
	}
}