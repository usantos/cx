package br.gov.caixa.loterias.apostas.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.VectorUtils;


public class SubBarraModalidadeFragment extends Fragment {
	private static final String ARG_MODALIDADE = "ARG_MODALIDADE";
	private static final String ARG_CONCURSO = "ARG_CONCURSO";
	private static final String ARG_SORTEIO = "ARG_SORTEIO";
	private static final String ARG_ESPECIAL = "AR_ESPECIAL";

	private  View view;
	private TextView tvModalidade, tvConcurso, tvSorteio;
	private ConstraintLayout background;
	private ImageView trapezio;

	private String concurso;
	private String sorteio;
	private boolean isEspecial;
	private ModalidadeEnum modalidade;
	private EstiloModalidadeMKP estilo;

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
			estilo = new EstiloModalidadeMKP(modalidade);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_sub_barra_modalidade, container, false);

		tvModalidade = view.findViewById(R.id.id_modalidade);
		tvConcurso = view.findViewById(R.id.id_concurso);
		tvSorteio = view.findViewById(R.id.id_sorteio);
		background = view.findViewById(R.id.layout_fragmento);
		trapezio = view.findViewById(R.id.back_money);

		preencheDados();
		aplicaEstilo();

		return view;
	}

	private void aplicaEstilo() {
		background.setBackgroundColor(ContextCompat.getColor(getContext(), estilo.getCorClara()));
		trapezio.setBackground(VectorUtils.getShape(R.drawable.ic_trapezio_mkp, estilo.getCorEscura()));
		if (isEspecial && estilo.getImagemEspecialDetalhes() != -1){
			background.setBackground(ContextCompat.getDrawable(getContext(), estilo.getImagemEspecialDetalhes()));
		}
	}

	private void preencheDados() {
		if (isEspecial){
			if (modalidade == ModalidadeEnum.LOTOFACIL){
				tvModalidade.setText(ModalidadeEnum.getDescricaoEspecialDuasLinhas(modalidade));
			} else {
				tvModalidade.setText(ModalidadeEnum.getDescricaoEspecial(modalidade));
			}
		} else {
			tvModalidade.setText(ModalidadeEnum.fromString(modalidade));
		}
		tvConcurso.setText(concurso);
		tvSorteio.setText(sorteio);
	}
}