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
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.dao.crud.CarrinhoFavoritoCRUD;
import br.gov.caixa.loterias.apostas.model.model.CarrinhoModel;
import br.gov.caixa.loterias.apostas.model.sp.LoginSP;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.ModoVisualizacaoBolaoEnum;
import br.gov.caixa.loterias.apostas.view.activity.DetalhesBolaoActivity;
import br.gov.caixa.loterias.apostas.view.fragment.NumerosSuperSeteFragment;
import br.gov.caixa.loterias.apostas.view.holder.ApostaCarrinhoHolder;
import br.gov.caixa.loterias.apostas.view.listener.ApostaCarrinhoListener;
import br.gov.caixa.loterias.apostas.view.listener.BolaoCarrinhoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogBotaoListener;


public class CarrinhoApostasListViewAdapter extends RecyclerView.Adapter<ApostaCarrinhoHolder> {
	private Context context;
	private ArrayList<IdentificaoDeUmaApostaDas8Modalidades> apostas;
	private CarrinhoActivity parentActivity;
	private NumerosSuperSeteFragment fragment;
	private boolean animacao, isBolao;
	private static final String TAG = "TAG_CARRINHO";
	private CarrinhoModel model;

	public CarrinhoApostasListViewAdapter(CarrinhoActivity parentActivity, ArrayList<IdentificaoDeUmaApostaDas8Modalidades> apostas, Context context, boolean animacao, CarrinhoModel model, boolean isBolao) {
		this.context = context;
		this.apostas = apostas;
		this.parentActivity = parentActivity;
		this.animacao = animacao;
		this.isBolao = isBolao;
		this.model = model;
		this.setHasStableIds(true);
	}

	@Override
	public int getItemCount() {
		return apostas.size();
	}

	@Override
	public ApostaCarrinhoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view;
		if (isBolao) {
			view = LayoutInflater.from(context).inflate(R.layout.row_apostas_list_view_boloes, parent, false);
			return new ApostaCarrinhoHolder(view, parentActivity, animacao, isBolao, onApostaCarrinhoListener(), onBolaoCarrinhoListener());

		} else {
			view = LayoutInflater.from(context).inflate(R.layout.row_apostas_list_view, parent, false);
			return new ApostaCarrinhoHolder(view, parentActivity, animacao, isBolao, onApostaCarrinhoListener(), null);
		}
	}

	@Override
	public void onBindViewHolder(ApostaCarrinhoHolder holder, final int position) {
		final IdentificaoDeUmaApostaDas8Modalidades aposta = apostas.get(position);

		holder.bind(aposta, position);
	}

	private BolaoCarrinhoListener onBolaoCarrinhoListener() {
		return aposta -> detalheCotaBolao(aposta);
	}

	private void detalheCotaBolao(IdentificaoDeUmaApostaDas8Modalidades aposta) {
		Bundle bundle = new Bundle();
		bundle.putString(DetalhesBolaoActivity.ARG_CODIGO_BOLAO, aposta.getReservaCotaBolao().getCodigoBolaoReserva());
		bundle.putString(DetalhesBolaoActivity.ARG_MODALIDADE, new Gson().toJson(aposta.getModalidade()));
		bundle.putString(DetalhesBolaoActivity.ARG_MODO_VISUALIZACAO, new Gson().toJson(ModoVisualizacaoBolaoEnum.LEITURA));
		bundle.putString(DetalhesBolaoActivity.ARG_NUMERO_COTA, aposta.getReservaCotaBolao().getNumeroCotaReservada() + "/" +
				aposta.getReservaCotaBolao().getQtdCotaTotalBolao());
		Intent intent = IntentUtil.getIntentOrigemDestino(parentActivity, DetalhesBolaoActivity.class, bundle);
		parentActivity.startActivity(intent);
	}

	private ApostaCarrinhoListener onApostaCarrinhoListener() {
		return aposta -> deletarAposta(aposta);
	}

	private void deletarAposta(final IdentificaoDeUmaApostaDas8Modalidades aposta) {
		DialogUtils.dialogSim(context,
				context.getResources().getString(R.string.MA010),

				new OnDialogBotaoListener() {
					@Override
					public void onButtonClick(DialogInterface dialog, int which) {
						int positionAposta = getPositionAposta(aposta);
						if(positionAposta >= 0) {
							AlertDialogUtils.show(parentActivity);
							model.deletaApostaCarrinho(aposta, onDeletaApostaCarrinho(aposta, positionAposta, isBolao));
						}
					}
				}
		);
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
		for (int i = 0; i <= apostas.size() - 1; i++){
			if (LoginSP.isLoginRealizado()) {
				if (apostas.get(i).getId() == aposta.getId()) {
					position = i;
					break;
				}
			}else {
				if (apostas.get(i).getIdDB() == aposta.getIdDB()){
					position = i;
					break;
				}
			}
		}
		return position;
	}

	public void deletaCarrinhoFavorito() {
		if(this.apostas.size() == 0){
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