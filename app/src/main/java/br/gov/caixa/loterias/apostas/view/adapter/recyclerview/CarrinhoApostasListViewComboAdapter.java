package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.content.Context;
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
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.dao.crud.CarrinhoFavoritoCRUD;
import br.gov.caixa.loterias.apostas.model.model.CarrinhoModel;
import br.gov.caixa.loterias.apostas.model.sp.LoginSP;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.activity.DetalhesComboActivity;
import br.gov.caixa.loterias.apostas.view.fragment.NumerosSuperSeteFragment;
import br.gov.caixa.loterias.apostas.view.holder.ApostaCarrinhoComboHolder;
import br.gov.caixa.loterias.apostas.view.listener.ApostaCarrinhoListener;
import br.gov.caixa.loterias.apostas.view.listener.ComboCarrinhoListener;

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
		return new ApostaCarrinhoComboHolder(view, parentActivity, onApostaCarrinhoListener(), onComboCarrinhoListener());
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

	private ApostaCarrinhoListener onApostaCarrinhoListener() {
		return aposta -> deletarAposta(aposta);
	}

	private void deletarAposta(final IdentificaoDeUmaApostaDas8Modalidades aposta) {

		ViewUtils.alertTitleButtonYesOrNotListener(context, context.getResources().getString(R.string.MA010), (dialogInterface, i) -> {
			int positionAposta = getPositionAposta(aposta);
			if(positionAposta >= 0) {
				AlertDialogUtils.show(parentActivity);
				//???model.deletaApostaCarrinho(aposta, onDeletaApostaCarrinho(aposta, positionAposta, isBolao));
			}
		});
	}

	private OnSilceListener<CarrinhoDTO> onDeletaApostaCarrinho(IdentificaoDeUmaApostaDas8Modalidades aposta, int positionAposta, boolean isBolao) {
		return new OnSilceListener<CarrinhoDTO>() {
			@Override
			public void success(CarrinhoDTO payload) {
				parentActivity.setCarrinho(payload);
				deletarApostaBaseLocal(aposta, Boolean.FALSE);
				parentActivity.deletaAposta(positionAposta, aposta, isBolao);
				deletaCarrinhoFavorito();
			}

			@Override
			public void error(VolleyError error) {
				AlertDialogUtils.dismiss();
				deletarApostaBaseLocal(aposta, Boolean.TRUE);
				parentActivity.deletaAposta(positionAposta, aposta, isBolao);
			}
		};
	}

	private int getPositionAposta(IdentificaoDeUmaApostaDas8Modalidades aposta) {
		int position = -1;
		for (int i = 0; i <= comboApostas.size() - 1; i++){
			if (LoginSP.isLoginRealizado()) {
				if (comboApostas.get(i).getId() == aposta.getId()) {
					position = i;
					break;
				}
			}else {
//				if (comboApostas.get(i).getIdDB() == aposta.getIdDB()){
//					position = i;
//					break;
//				}
			}
		}
		return position;
	}

	public void deletaCarrinhoFavorito() {
		if(this.comboApostas.size() == 0){
			CarrinhoFavoritoCRUD crud = new CarrinhoFavoritoCRUD(context);
			crud.deletaTodos();
		}
	}

	private void deletarApostaBaseLocal(IdentificaoDeUmaApostaDas8Modalidades aposta, boolean local) {
		model.deletarApostaLocal(aposta);
		if (local) {
			parentActivity.lerCarrinhoLocal();
		}
	}
}