package br.gov.caixa.loterias.apostas.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.VectorUtils;

public class BarraTituloComboFragment extends Fragment {
	private static final String ARG_MODALIDADE = "ARG_MODALIDADE";
	private static final String ARG_COR_BACK = "AR_COR_BACK";

	private  View view;
	private TextView tvModalidade, tvConcurso, tvSorteio, labelConcurso, labelSorteio;
	private ConstraintLayout background;
	private ImageButton btnVoltar, btnDuvida;
	private int corBackground;
	private ModalidadeEnum modalidade;
	private EstiloModalidadeMKP estilo;

	public BarraTituloComboFragment() {}

	public static BarraTituloComboFragment newInstance(ModalidadeEnum modalidade, int corBackground) {
		BarraTituloComboFragment fragment = new BarraTituloComboFragment();
		Bundle                  args     = new Bundle();
		args.putString(ARG_MODALIDADE, new Gson().toJson(modalidade));
		args.putInt(ARG_COR_BACK, corBackground);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			modalidade = new Gson().fromJson(getArguments().getString(ARG_MODALIDADE), ModalidadeEnum.class);
			corBackground = getArguments().getInt(ARG_COR_BACK);
			estilo = new EstiloModalidadeMKP(modalidade);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_barra_titulo_simulacao, container, false);

		tvModalidade = view.findViewById(R.id.id_modalidade);
		tvConcurso = view.findViewById(R.id.id_concurso);
		tvSorteio = view.findViewById(R.id.id_sorteio);
		background = view.findViewById(R.id.layout_fragmento);
		btnVoltar = view.findViewById(R.id.seta_voltar);
		btnDuvida = view.findViewById(R.id.duvida);
		labelConcurso = view.findViewById(R.id.label_concurso);
		labelSorteio = view.findViewById(R.id.label_sorteio);

		setClickListener();

		preencheDados();
		aplicaEstilo();
		aplicaEstiloFonte();

		return view;
	}

	private void setClickListener() {
//		btnDuvida.setOnClickListener(v -> {
//			Intent intent = new Intent(getContext(), TermosUsoActivity.class);
//			startActivity(intent);
//		});

		btnVoltar.setOnClickListener(v -> getActivity().onBackPressed());
	}

	private void aplicaEstiloFonte() {
		tvModalidade.setTextColor(ContextCompat.getColor(getContext(), estilo.getCorFonteFundoClaro()));
		btnVoltar.setImageDrawable(VectorUtils.getShape(R.drawable.seta_esquerda, estilo.getCorFonteFundoClaro()));
	}

	private void aplicaEstilo() {
		background.setBackgroundColor(ContextCompat.getColor(getContext(), corBackground));
	}

	private void preencheDados() {
		tvModalidade.setText(ModalidadeEnum.fromString(modalidade));
		labelConcurso.setVisibility(View.GONE);
		tvConcurso.setVisibility(View.GONE);
		labelSorteio.setVisibility(View.GONE);
		tvSorteio.setVisibility(View.GONE);
		btnDuvida.setVisibility(View.GONE);
	}
}