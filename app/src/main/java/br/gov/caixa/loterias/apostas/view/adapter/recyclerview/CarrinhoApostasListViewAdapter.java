package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;

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
import br.gov.caixa.loterias.apostas.utils.EstiloModalidade;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.fragment.NumerosSuperSeteFragment;
import br.gov.caixa.loterias.apostas.view.holder.ApostaCarrinhoHolder;
import br.gov.caixa.loterias.apostas.view.listener.ApostaCarrinhoListener;

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
		View view = LayoutInflater.from(context).inflate(R.layout.row_apostas_list_view, parent, false);
		return new ApostaCarrinhoHolder(view, parentActivity, animacao, isBolao, onApostaCarrinhoListener());
	}

	@Override
	public void onBindViewHolder(ApostaCarrinhoHolder holder, final int position) {
		final IdentificaoDeUmaApostaDas8Modalidades aposta = apostas.get(position);
		EstiloModalidade                            estilo = new EstiloModalidade(aposta.getModalidade());

		holder.bind(aposta, position);
	}

	private ApostaCarrinhoListener onApostaCarrinhoListener() {
		return aposta -> deletarAposta(aposta);
	}

	private void deletarAposta(final IdentificaoDeUmaApostaDas8Modalidades aposta) {

		ViewUtils.alertTitleButtonYesOrNotListener(context, context.getResources().getString(R.string.MA010), (dialogInterface, i) -> {
			int positionAposta = getPositionAposta(aposta);
			if(positionAposta >= 0) {
				AlertDialogUtils.show(parentActivity);
				model.deletaApostaCarrinho(aposta, onDeletaApostaCarrinho(aposta, positionAposta, isBolao));
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