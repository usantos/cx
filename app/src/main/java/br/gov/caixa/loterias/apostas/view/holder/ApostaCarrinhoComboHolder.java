package br.gov.caixa.loterias.apostas.view.holder;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.daimajia.swipe.SwipeLayout;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComboApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IndicadorSurpresinha;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.TimerSingleton;
import br.gov.caixa.loterias.apostas.utils.ContagemRegressiva;
import br.gov.caixa.loterias.apostas.utils.DateUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidade;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.ListaUtils;
import br.gov.caixa.loterias.apostas.utils.Utils;
import br.gov.caixa.loterias.apostas.utils.VectorUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.utils.VolanteUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.DetalheApostaLotecaAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.DetalheApostaLotogolAdapter;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightRecyclerView;
import br.gov.caixa.loterias.apostas.view.fragment.NumerosSuperSeteFragment;
import br.gov.caixa.loterias.apostas.view.listener.ApostaCarrinhoListener;
import br.gov.caixa.loterias.apostas.view.listener.BolaoCarrinhoListener;

public class ApostaCarrinhoComboHolder extends LoteriasHolder<ComboApostaDTO> {
	private TextView tvDescricaoCombo;
	private TextView tvQtdApostasCombo;
	private TextView valorAposta;
	//private ImageView imageDetalheCombos;

	private Context context;
	private AppCompatActivity activity;
	private ApostaCarrinhoListener listener;
	private BolaoCarrinhoListener listenerBolao;


	public ApostaCarrinhoComboHolder(View view, AppCompatActivity activity, ApostaCarrinhoListener listener, BolaoCarrinhoListener listenerBolao) {
		super(view);

		this.context = view.getContext();
		this.activity = activity;
		this.listener = listener;
		this.listenerBolao = listenerBolao;

		tvDescricaoCombo = view.findViewById(R.id.descricaoCombo);
		valorAposta = view.findViewById(R.id.valorAposta);
		tvQtdApostasCombo = view.findViewById(R.id.tvQtdApostasCombo);
		// = view.findViewById(R.id.imageViewDetalheCombos);
	}

	@Override
	public void bind(ComboApostaDTO comboAposta, int position) {

		this.tvDescricaoCombo.setText(comboAposta.getTipoCombo().getNome());

		if (comboAposta.getApostas() != null && comboAposta.getApostas().size() > 0) {
			BigDecimal valAposta = BigDecimal.ZERO;
			for (IdentificaoDeUmaApostaDas8Modalidades umaAposta : comboAposta.getApostas()) {
				valAposta = valAposta.add(umaAposta.getValor());
			}
			ViewUtils.setMoedaFormatHtml(valAposta, valorAposta);

			if (comboAposta.getApostas().size() == 1)
				this.tvQtdApostasCombo.setText(comboAposta.getApostas().size() + " aposta");
			else
				this.tvQtdApostasCombo.setText(comboAposta.getApostas().size() + " apostas");
		}
	}

}
