package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.util.ArrayList;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.CarrinhoActivity;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComboApostaDTO;
import br.gov.caixa.loterias.apostas.model.model.CarrinhoModel;
import br.gov.caixa.loterias.apostas.model.sp.LoginSP;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.view.activity.DetalhesComboActivity;
import br.gov.caixa.loterias.apostas.view.fragment.NumerosSuperSeteFragment;
import br.gov.caixa.loterias.apostas.view.holder.ApostaCarrinhoComboHolder;
import br.gov.caixa.loterias.apostas.view.listener.ApostaComboCarrinhoListener;
import br.gov.caixa.loterias.apostas.view.listener.ComboCarrinhoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogBotaoListener;

public class CarrinhoApostasListViewComboAdapter extends RecyclerView.Adapter<ApostaCarrinhoComboHolder> {
	private Context context;
	private ArrayList<ComboApostaDTO> comboApostas;
	private CarrinhoActivity parentActivity;
	private NumerosSuperSeteFragment fragment;
	private boolean animacao;
	private static final String TAG = "TAG_CARRINHO";
	private CarrinhoModel model;

	public CarrinhoApostasListViewComboAdapter(CarrinhoActivity parentActivity, ArrayList<ComboApostaDTO> comboApostas, Context context, CarrinhoModel model) {
		this.context = context;
		this.comboApostas = comboApostas;
		this.parentActivity = parentActivity;
		this.animacao = animacao;
		this.model = model;
		this.setHasStableIds(true);
	}

	@Override
	public int getItemCount() {
		return comboApostas.size();
	}

	@Override
	public ApostaCarrinhoComboHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view;
		view = LayoutInflater.from(context).inflate(R.layout.row_apostas_list_view_combos, parent, false);
		return new ApostaCarrinhoComboHolder(view, parentActivity, onApostaComboCarrinhoListener(), onComboCarrinhoListener());
	}

	@Override
	public void onBindViewHolder(ApostaCarrinhoComboHolder holder, final int position) {
		final ComboApostaDTO comboAposta = comboApostas.get(position);

		holder.bind(comboAposta, position);
	}

	private ComboCarrinhoListener onComboCarrinhoListener() {
		return comboAposta -> detalheComboAposta(comboAposta);
	}

	private void detalheComboAposta(ComboApostaDTO comboAposta) {
		Bundle bundle = new Bundle();
		bundle.putString(DetalhesComboActivity.ARG_COMBO_APOSTA_DTO, new Gson().toJson(comboAposta));
		Intent intent = IntentUtil.getIntentOrigemDestino(parentActivity, DetalhesComboActivity.class, bundle);
		parentActivity.startActivity(intent);
	}

	private ApostaComboCarrinhoListener onApostaComboCarrinhoListener() {
		return comboAposta -> deletarComboAposta(comboAposta);
	}

	private void deletarComboAposta(final ComboApostaDTO comboApostaDTO) {
		DialogUtils.dialogSim(context,
				context.getResources().getString(R.string.MA010),

				new OnDialogBotaoListener() {
					@Override
					public void onButtonClick(DialogInterface dialog, int which) {
						int positionCombo = getPositionCombo(comboApostaDTO);
						if(positionCombo >= 0) {
							AlertDialogUtils.show(parentActivity);
							model.deletaComboCarrinho(comboApostaDTO, onDeletaComboCarrinho(comboApostaDTO, positionCombo));
						}
					}
				}
		);
	}

	private OnSilceListener<CarrinhoDTO> onDeletaComboCarrinho(ComboApostaDTO comboApostaDTO, int positionCombo) {
		return new OnSilceListener<CarrinhoDTO>() {
			@Override
			public void success(CarrinhoDTO payload) {
				parentActivity.setCarrinho(payload);
				//deletarApostaBaseLocal(aposta, Boolean.FALSE);
				parentActivity.deletaCombo(positionCombo, comboApostaDTO);
				//deletaCarrinhoFavorito();
			}

			@Override
			public void error(VolleyError error) {
				AlertDialogUtils.dismiss();
				//deletarApostaBaseLocal(aposta, Boolean.TRUE);
				parentActivity.deletaCombo(positionCombo, comboApostaDTO);
			}
		};
	}

	private int getPositionCombo(ComboApostaDTO aposta) {
		int position = -1;
		for (int i = 0; i <= comboApostas.size() - 1; i++) {
			if (LoginSP.isLoginRealizado()) {
				if (comboApostas.get(i).getId() == aposta.getId()) {
					position = i;
					break;
				}
			}
		}
		return position;
	}
}