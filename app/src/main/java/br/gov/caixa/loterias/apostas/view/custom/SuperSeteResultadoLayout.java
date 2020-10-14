package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.Modalidade;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidade;

@EViewGroup(R.layout.item_resultado_supersete)
public class SuperSeteResultadoLayout extends LinearLayout {

	@ViewById
	protected TextView tv_numero_1,tv_numero_2,tv_numero_3,tv_numero_4,
					   tv_numero_5,tv_numero_6,tv_numero_7;
	@ViewById
	protected LinearLayout  item_resultado_content_view;

	@ViewById
	protected View tracoViewDivisoriaResultado;

	@ViewById
	protected PartidaViewDetalhesResultado partidaViewDetalhesResultadoView;

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

	@AfterViews
	protected void init() {

	}

}
