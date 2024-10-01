package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.Modalidade;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidade;

public class SuperSeteResultadoLayout extends LinearLayout {

	private boolean alreadyInflated = false;

	private TextView tv_numero_1,tv_numero_2,tv_numero_3,tv_numero_4,
					   tv_numero_5,tv_numero_6,tv_numero_7;
	private LinearLayout  item_resultado_content_view;
	private View tracoViewDivisoriaResultado;
	private PartidaViewDetalhesResultado partidaViewDetalhesResultadoView;

	public static SuperSeteResultadoLayout build(Context context) {
		SuperSeteResultadoLayout instance = new SuperSeteResultadoLayout(context);
		instance.onFinishInflate();
		return instance;
	}

	@Override
	public void onFinishInflate() {
		if (!alreadyInflated) {
			alreadyInflated = true;
			inflate(getContext(), R.layout.item_resultado_supersete, this);
		}
		super.onFinishInflate();
		init();
	}

	protected void init() {
		this.tv_numero_1 = findViewById(R.id.tv_numero_1);
		this.tv_numero_2 = findViewById(R.id.tv_numero_2);
		this.tv_numero_3 = findViewById(R.id.tv_numero_3);
		this.tv_numero_4 = findViewById(R.id.tv_numero_4);
		this.tv_numero_5 = findViewById(R.id.tv_numero_5);
		this.tv_numero_6 = findViewById(R.id.tv_numero_6);
		this.tv_numero_7 = findViewById(R.id.tv_numero_7);
		this.item_resultado_content_view = findViewById(R.id.item_resultado_content_view);
		this.tracoViewDivisoriaResultado = findViewById(R.id.tracoViewDivisoriaResultado);
		this.partidaViewDetalhesResultadoView = findViewById(R.id.partidaViewDetalhesResultadoView);
	}

	public void setLayout(Modalidade modalidade) {

		EstiloModalidade estilo = new EstiloModalidade(modalidade.getTipoModalidade());
		tracoViewDivisoriaResultado.setBackground(getContext().getResources().getDrawable(estilo.getCorClara()));

		List<List<Integer>> matrizResultado = modalidade.getResultadoConcursoDTO().getMatrizNumerosSorteadosPrimeiroSorteio();
		tv_numero_1.setText(matrizResultado.get(0).get(0).toString());
		tv_numero_2.setText(matrizResultado.get(1).get(0).toString());
		tv_numero_3.setText(matrizResultado.get(2).get(0).toString());
		tv_numero_4.setText(matrizResultado.get(3).get(0).toString());
		tv_numero_5.setText(matrizResultado.get(4).get(0).toString());
		tv_numero_6.setText(matrizResultado.get(5).get(0).toString());
		tv_numero_7.setText(matrizResultado.get(6).get(0).toString());

		this.partidaViewDetalhesResultadoView.setLayout(modalidade);

	}

	public void setLayout(List<List<Integer>> matrizResultado) {

		tracoViewDivisoriaResultado.setVisibility(View.GONE);

		tv_numero_1.setText(matrizResultado.get(0).get(0).toString());
		tv_numero_2.setText(matrizResultado.get(1).get(0).toString());
		tv_numero_3.setText(matrizResultado.get(2).get(0).toString());
		tv_numero_4.setText(matrizResultado.get(3).get(0).toString());
		tv_numero_5.setText(matrizResultado.get(4).get(0).toString());
		tv_numero_6.setText(matrizResultado.get(5).get(0).toString());
		tv_numero_7.setText(matrizResultado.get(6).get(0).toString());
	}


	public SuperSeteResultadoLayout(Context context) {
		super(context);
	}

	public SuperSeteResultadoLayout(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public SuperSeteResultadoLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
}
