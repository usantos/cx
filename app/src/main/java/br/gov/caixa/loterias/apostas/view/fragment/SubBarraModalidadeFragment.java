package br.gov.caixa.loterias.apostas.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import br.gov.caixa.loterias.apostas.utils.EspecialUtils;
import br.gov.caixa.loterias.apostas.utils.SharedPreferencesUtils;
import br.gov.caixa.loterias.apostas.utils.TextViewUtils;
import br.gov.caixa.loterias.apostas.utils.VectorUtils;
import com.google.gson.Gson;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;


public class SubBarraModalidadeFragment extends Fragment {
	private static final String ARG_MODALIDADE = "ARG_MODALIDADE";
	private static final String ARG_CONCURSO = "ARG_CONCURSO";
	private static final String ARG_SORTEIO = "ARG_SORTEIO";
	private static final String ARG_ESPECIAL = "AR_ESPECIAL";

	private ConstraintLayout btnVoltar;
	private AppCompatImageView setaVoltar;
	private  View view;
	private TextView tvModalidade, tvConcurso, tvSorteio, labelConcurso, labelSorteio;
	private ConstraintLayout background;

	private String concurso;
	private String sorteio;
	private boolean isEspecial;
	private ModalidadeEnum modalidade;
	private EstiloModalidadeMKP estilo;
	Boolean isMega30 = false;
	Boolean isLotecaPais = false;

	public SubBarraModalidadeFragment() {
		// Required empty public constructor
	}

	public static SubBarraModalidadeFragment newInstance(ModalidadeEnum modalidade, String concuso,
														 String sorteio, boolean especial) {
		SubBarraModalidadeFragment fragment = new SubBarraModalidadeFragment();
		Bundle                  args     = new Bundle();
		args.putString(ARG_MODALIDADE, new Gson().toJson(modalidade));
		args.putSerializable(ARG_CONCURSO, concuso);
		args.putSerializable(ARG_SORTEIO, sorteio);
		args.putBoolean(ARG_ESPECIAL, especial);
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
			isMega30 = EspecialUtils.isMega30(modalidade, Integer.valueOf(concurso), isEspecial);
			isLotecaPais = EspecialUtils.isLotecaPais(modalidade, Integer.valueOf(concurso), isEspecial);
			//estilo = new EstiloModalidadeMKP(modalidade,isMega30);
			estilo = EstiloModalidadeMKP.createModalidadeConcursoEsp(modalidade,
					Integer.valueOf(concurso), isEspecial);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_sub_barra_modalidade, container, false);
		btnVoltar = view.findViewById(R.id.btn_voltar_sub_barra);
		setaVoltar = view.findViewById(R.id.seta_voltar_sub_barra);
		tvModalidade = view.findViewById(R.id.tv_modalidade);
		tvConcurso = view.findViewById(R.id.id_concurso);
		tvSorteio = view.findViewById(R.id.id_sorteio);
		background = view.findViewById(R.id.layout_fragmento);
		labelConcurso = view.findViewById(R.id.label_concurso);
		labelSorteio = view.findViewById(R.id.label_sorteio);
		if (ModalidadeEnum.LOTECA.equals(modalidade)) {
			labelSorteio.setText(getString(R.string.resultado));
		}
		preencheDados();
		aplicaEstilo();
		aplicaEstiloFonte();

		btnVoltar.setOnClickListener(v -> getActivity().onBackPressed());

		return view;
	}

	private void aplicaEstiloFonte() {
		tvModalidade.setTextColor(ContextCompat.getColor(getContext(), estilo.getCorFonteFundoClaro()));
		tvConcurso.setTextColor(ContextCompat.getColor(getContext(), estilo.getCorFonteFundoClaro()));
		tvSorteio.setTextColor(ContextCompat.getColor(getContext(), estilo.getCorFonteFundoClaro()));
		labelConcurso.setTextColor(ContextCompat.getColor(getContext(), estilo.getCorFonteFundoClaro()));
		labelSorteio.setTextColor(ContextCompat.getColor(getContext(), estilo.getCorFonteFundoClaro()));
	}

	private void aplicaEstilo() {
		background.setBackgroundColor(ContextCompat.getColor(getContext(), estilo.getCorClara()));
		setaVoltar.setImageDrawable(VectorUtils.getShape(R.drawable.seta_esquerda, estilo.getCorFonteFundoClaro()));
//		if (isEspecial && estilo.getImagemEspecialDetalhes() != -1){
//			background.setBackground(ContextCompat.getDrawable(getContext(), estilo.getImagemEspecialDetalhes()));
//		}
		if (isEspecial && estilo.getImagemEspecialSimples() != -1){
			background.setBackground(ContextCompat.getDrawable(getContext(), estilo.getImagemEspecialSimples()));
		}
	}

	private void preencheDados() {
		if (isEspecial){
			if (modalidade == ModalidadeEnum.LOTOFACIL){
				tvModalidade.setText(ModalidadeEnum.getDescricaoEspecialDuasLinhas(modalidade).toLowerCase());
				TextViewUtils.mudarTamanhoPorPorcentagem(tvModalidade, -20f);
			} else {
				//TODO: MEGA 30 ANOS//
				if(isMega30) {
					tvModalidade.setText(SharedPreferencesUtils.getValorString(EspecialUtils.MEGA_DUAS_LINHAS,""));
				} else if (isLotecaPais) {
					//tvModalidade.setText(SharedPreferencesUtils.getValorString(EspecialUtils.LOTECA_PAIS_DUAS_LINHAS,""));
					tvModalidade.setText(SharedPreferencesUtils.getValorString(EspecialUtils.LOTECA_PAIS_LINHA,""));
				} else {
					tvModalidade.setText(ModalidadeEnum.getDescricaoEspecial(modalidade).toLowerCase());
				}
			}
		} else {
			tvModalidade.setText(ModalidadeEnum.fromString(modalidade).toLowerCase());
		}
		ViewCompat.setAccessibilityHeading(tvModalidade,true);
		tvConcurso.setText(concurso);
		tvSorteio.setText(sorteio);
	}
}