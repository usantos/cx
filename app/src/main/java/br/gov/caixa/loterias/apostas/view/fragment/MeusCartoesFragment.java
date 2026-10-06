package br.gov.caixa.loterias.apostas.view.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoCartao;
import br.gov.caixa.loterias.apostas.model.model.CartoesModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.MeioPagamentoUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.MeusCartoesRecyclerViewAdapter;
import br.gov.caixa.loterias.apostas.view.config.MeusCartoesConfig;
import br.gov.caixa.loterias.apostas.view.holder.MeuCartaoHolder;
import br.gov.caixa.loterias.apostas.view.listener.MeusCartoesListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogBotaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnMeusCartoesListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MeusCartoesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MeusCartoesFragment extends Fragment {
	private static final String ARG_CONFIG = "ARG_CONFIG";
	private static final String ARG_CARTOES = "ARG_CARTOES";

	private MeusCartoesConfig config;
	private View view;
	private View margemCabecalho, margemConteudo;
	private TextView subtitulo;
	private AppCompatImageView imagem;
	private ConstraintLayout conteudo;

	private RecyclerView listaCartoes;
	private MeusCartoesRecyclerViewAdapter adapter;

	private List<RetornoCartao> cartoes;

	private CartoesModel model;
	private MeusCartoesListener listener;

	public MeusCartoesFragment() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param config Parameter 1.
	 * @return A new instance of fragment MeioPagamentoFragment.
	 */
	public static MeusCartoesFragment newInstance(List<RetornoCartao> cartoes, MeusCartoesConfig config) {
		MeusCartoesFragment fragment = new MeusCartoesFragment();
		Bundle              args     = new Bundle();
		if(config != null){
			args.putSerializable(ARG_CONFIG, config);
		}

		if (cartoes != null){
			args.putString(ARG_CARTOES, new Gson().toJson(cartoes));
		}

		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		listener = (MeusCartoesListener) context;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			config  = (MeusCartoesConfig) getArguments().getSerializable(ARG_CONFIG);

			TypeToken<List<RetornoCartao>> token = new TypeToken<List<RetornoCartao>>() {};
			String                         cartoesString = getArguments().getString(ARG_CARTOES);
			if(cartoesString != null && cartoesString.length() > 0){
				cartoes = new Gson().fromJson(cartoesString, token.getType());
			} else {
				cartoes = new ArrayList<>();
			}

			model = new CartoesModel(getActivity());
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		view = inflater.inflate(R.layout.fragment_meus_cartoes, container, false);
		margemCabecalho = view.findViewById(R.id.margemCabecalho);
		margemConteudo = view.findViewById(R.id.margemConteudo);
		imagem = view.findViewById(R.id.imagemMeioPagamento);
		subtitulo = view.findViewById(R.id.subtitulo_cartoes);
		conteudo = view.findViewById(R.id.conteudoCartoes);
		listaCartoes = view.findViewById(R.id.listaCartoes);

		adapter = new MeusCartoesRecyclerViewAdapter(cartoes, getContext(), onRecyclerlistener());
		RecyclerView.LayoutManager lm = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL,
																false);
		listaCartoes.setLayoutManager(lm);
		listaCartoes.setAdapter(adapter);

		aplicaConfig();

		return view;
	}

	@NonNull
	private OnMeusCartoesListener<MeuCartaoHolder> onRecyclerlistener() {
		return new OnMeusCartoesListener<MeuCartaoHolder>() {
			@Override
			public void favoritar(MeuCartaoHolder holder, int position) {
//				RetornoCartao cartao = cartoes.get(position);
//				if (!cartoes.get(position).isFav()){
//					if (contemCartaoFavorito()) {
//						Dialog dialogConfirmacao = ViewUtils.alertTitleCustomPositiveListener(getContext(), getString(R.string.alterar_favorito_interrogacao),
//																							  getString(R.string.cancelar),
//																							  getString(R.string.confirmar),
//																							  getString(R.string.confirmar_alterar_cartao_favorito),
//																							  (dialog, which) -> {
//																								  adapter.atualizaCartaoFavorito(cartao);
//																							  });
//						dialogConfirmacao.show();
//					} else {
//						adapter.atualizaCartaoFavorito(cartao);
//					}
//				}
			}

			@Override
			public void deletar(MeuCartaoHolder holder, int position) {
				if (cartoes.get(position).isFav()){
					DialogUtils.dialogTituloEntendi(
							getContext(),
							getString(R.string.excluir_cartao_interrogacao),
							getString(R.string.nao_e_possivel_eliminar_cartao_favorito)
					);
				} else {
					String mensagem = getString(R.string.deseja_realmente_exluir_cartao) + " " + cartoes.get(position).getUltimosDigitos() + "?";

					DialogUtils.dialogTituloSim(
							getContext(),
							getString(R.string.excluir_cartao_interrogacao),
							mensagem,
							new OnDialogBotaoListener() {
								@Override
								public void onButtonClick(DialogInterface dialog, int which) {
									deletaCartao(cartoes.get(position), position);
								}
							}

					);
				}
			}
		};
	}

	private void deletaCartao(RetornoCartao cartao, int position) {
		AlertDialogUtils.show(getContext());
		if (model.isMercadoPago(config.getValueMeioPagamento())){
			model.deletaCartao(cartao.getIdCartao().toString(), MeioPagamentoUtils.MP_MERCADO_PAGO, onDeletaCartao(position));
		} else if (model.isRecargaPay(config.getValueMeioPagamento())){
			model.deletaCartao(cartao.getTokenCartao(), MeioPagamentoUtils.MP_RECARGA_PAY, onDeletaCartao(position));
		}
	}

	private OnSilceListener<List<RetornoCartao>> onDeletaCartao(int position) {
		return new OnSilceListener<List<RetornoCartao>>() {
			@Override
			public void success(List<RetornoCartao> payload) {
				AlertDialogUtils.dismiss();

				cartoes.remove(position);
				adapter.notifyDataSetChanged();
				if (cartoes.isEmpty()){
					listener.semCartao();
				}
			}

			@Override
			public void error(VolleyError error) {
				AlertDialogUtils.dismiss();
			}
		};
	}

	private boolean contemCartaoFavorito() {
		for (RetornoCartao cartao: cartoes){
			if (cartao.isFav()){
				return true;
			}
		}
		return false;
	}

	private void aplicaConfig() {
		if (config != null){
			margemCabecalho.setBackgroundColor(getResources().getColor(config.getIdCorBarra()));
			margemConteudo.setBackgroundColor(getResources().getColor(config.getIdCorBarra()));
			conteudo.setBackground(getActivity().getDrawable(config.getIdCorBarra()));
			conteudo.getBackground().setAlpha(20);
			imagem.setBackground(getActivity().getDrawable(config.getIdImagem()));
			subtitulo.setText(getString(config.getIdMensagem()));
		}
	}
}