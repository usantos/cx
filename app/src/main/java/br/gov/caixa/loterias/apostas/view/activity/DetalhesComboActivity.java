package br.gov.caixa.loterias.apostas.view.activity;


import android.os.Bundle;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;


import br.gov.caixa.loterias.apostas.LoteriasAppMarketPlaceActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComboApostaDTO;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.JogosComboRecyclerView;

public class DetalhesComboActivity extends LoteriasAppMarketPlaceActivity {

	public static final String ARG_COMBO_APOSTA_DTO = "ARG_COMBO_APOSTA_DTO";
	private ComboApostaDTO comboApostaDTO;
	private TextView nomeCombo;
	private RecyclerView listaJogos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detalhes_combo);
		configToolbar(R.id.toolbar);
		getExtras();

		setTitle(ViewUtils.textCaixaSTDBoldTitle(this, getResources().getString(R.string.title_activity_carrinho)));
		setViews();
		preencheDados();
	}

	private void preencheDados() {
		if (comboApostaDTO != null && comboApostaDTO.getTipoCombo() != null && comboApostaDTO.getTipoCombo().getNome() != null){
			nomeCombo.setText(comboApostaDTO.getTipoCombo().getNome());
		}
		if (comboApostaDTO != null && comboApostaDTO.getApostas() != null && !comboApostaDTO.getApostas().isEmpty()){
			listaJogos.setAdapter(new JogosComboRecyclerView(comboApostaDTO.getApostas()));
		}
	}

	private void getExtras() {
		comboApostaDTO = new Gson().fromJson(getIntent().getStringExtra(ARG_COMBO_APOSTA_DTO), ComboApostaDTO.class);
	}
	private void setViews() {
		nomeCombo = findViewById(R.id.id_nome_combo);
		listaJogos = findViewById(R.id.id_lista_jogos);
	}
	@Override
	public boolean onSupportNavigateUp() {
		finish();
		return true;
	}

}