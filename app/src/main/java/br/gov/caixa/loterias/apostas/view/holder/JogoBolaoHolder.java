package br.gov.caixa.loterias.apostas.view.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Dezena;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaDezenaRecyclerView;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;

public class JogoBolaoHolder extends LoteriasHolder<ApostaBolaoDTO> {
	private TextView txtNumero;
	private RecyclerView listView;
	private Context context;

	public JogoBolaoHolder(View itemView, Context context) {
		super(itemView);
		txtNumero = itemView.findViewById(R.id.id_numero_jogo);
		listView = itemView.findViewById(R.id.id_lista);
		this.context = context;
	}

	@Override
	public void bind(ApostaBolaoDTO aposta, int position) {
		txtNumero.setText("Jogo " + (position + 1));

		List<Dezena> dezenas = new ArrayList<>();
		for (int i = 1; i <= aposta.getDezenas().size(); i++) {
			dezenas.add(new Dezena("" + i, Boolean.FALSE, "" + i));
		}



		List<Integer> selecionados = new ArrayList<>();
		ListaDezenaRecyclerView adapter = new ListaDezenaRecyclerView(dezenas, selecionados,
				R.color.mega_verde_escuro_mkp, onItemClickListener());
		listView.setAdapter(adapter);

		RecyclerView.LayoutManager layot = new GridLayoutManager(context, 6);
		listView.setLayoutManager(layot);
	}

	private OnItemClickListener onItemClickListener() {
		return (holder, position) -> {

		};
	}
}
