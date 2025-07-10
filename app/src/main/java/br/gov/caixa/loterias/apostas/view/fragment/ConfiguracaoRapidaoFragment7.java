package br.gov.caixa.loterias.apostas.view.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.RapidaoConfigSingleton;
import br.gov.caixa.loterias.apostas.utils.ServicoFactoryUtil;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.FavoritasRapidaoRecyclerAdapter;
import br.gov.caixa.loterias.apostas.view.custom.LoadingViewLoterias;

public class ConfiguracaoRapidaoFragment7 extends Fragment {

	private View view = null;
	protected RecyclerView recycleApostasFavoritasRapidao;
	private List<ApostaFavoritaDTO> apostaFavoritaLista = new ArrayList<>();
	private ArrayList<ApostaFavoritaDTO> apostasFavoritaListaSelecionadas = new ArrayList<>();

	private int offsetItem = 0;
	private Boolean bloquearRequestScroll = false;
	private Boolean isMaisCompras = true;
	public  ProgressBar progressLoadingMore;
	private FavoritasRapidaoRecyclerAdapter apostaFavoritaAdapter;

	public ConfiguracaoRapidaoFragment7() { }

	public static ConfiguracaoRapidaoFragment7 newInstance() {
		ConfiguracaoRapidaoFragment7 fragment = new ConfiguracaoRapidaoFragment7();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.fragment_configuracao_rapidao_fragment7, container, false);
			setaViews(view);
		}
		setaMetodos();
		return view;
	}

	private void setaViews (View view) {
		progressLoadingMore = view.findViewById(R.id.favoritas_loading_more_rapidao);
		recycleApostasFavoritasRapidao = view.findViewById(R.id.recycleApostasFavoritasRapidao);


	}

	public void setaMetodos(){
		apostasFavoritaListaSelecionadas = (ArrayList<ApostaFavoritaDTO>) RapidaoConfigSingleton.getInstance().getRapidaoConfig().getApostasFavoritas();
		callWebservice();
	}

	private void callWebservice() {
		offsetItem = 0;
		isMaisCompras = true;
		bloquearRequestScroll = false;

		final AlertDialog loadViewProgress = LoadingViewLoterias.show(getActivity());
		ServicoFactoryUtil.getApostaService().getApostasFavoritas(offsetItem, new RequestListener<ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse>() {
			@Override
			public void onResponse(ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse response) {
				loadViewProgress.dismiss();
				if (response.getRedirect() != null){
					RedirectNetwork.checkRedirectSucesso(response.getRedirect(), getActivity());
				}
				LinearLayoutManager layoutManager;

				offsetItem = 8;

				apostaFavoritaLista = response.getPayload().getLista();
				//recyclerView
				apostaFavoritaAdapter  = new FavoritasRapidaoRecyclerAdapter(getActivity(),
																			 getActivity(),
																			 apostaFavoritaLista, apostasFavoritaListaSelecionadas,
																			 false);

				layoutManager = new LinearLayoutManager(getActivity());

				recycleApostasFavoritasRapidao.setHasFixedSize(true);
				recycleApostasFavoritasRapidao.setAdapter(apostaFavoritaAdapter);
				recycleApostasFavoritasRapidao.setLayoutManager(layoutManager);

				recycleApostasFavoritasRapidao.addOnScrollListener(new RecyclerView.OnScrollListener() {
					@Override
					public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
						super.onScrollStateChanged(recyclerView, newState);

						View view = recyclerView.getChildAt(recyclerView.getChildCount() - 1);
						int diff = (view.getBottom() - (recyclerView.getHeight() + recyclerView.getScrollY()));

						// if diff is zero, then the bottom has been reached
						if (diff <= 0 && bloquearRequestScroll == false) {
							// do stuff
							getFavoritasScroll();
							bloquearRequestScroll = true;
						}

					}
				});

				apostaFavoritaAdapter.setOnFavorito(new FavoritasRapidaoRecyclerAdapter.Favorito() {
					@Override
					public void onRemover(ApostaFavoritaDTO aposta) {
						for (ApostaFavoritaDTO apostasFavoritaListaSelecionada : apostasFavoritaListaSelecionadas) {
							if (apostasFavoritaListaSelecionada.getId().compareTo(aposta.getId()) == 0) {
								apostasFavoritaListaSelecionadas.remove(apostasFavoritaListaSelecionadas.indexOf(apostasFavoritaListaSelecionada));
								RapidaoConfigSingleton.getInstance().getRapidaoConfig().setApostasFavoritas(apostasFavoritaListaSelecionadas);
								break;
							}
						}
					}

					@Override
					public void onIncluso(ApostaFavoritaDTO aposta) {
						boolean duplicado = false;
						for (ApostaFavoritaDTO apostasFavoritaListaSelecionada : apostasFavoritaListaSelecionadas) {
							if (apostasFavoritaListaSelecionada.getId().compareTo(aposta.getId()) == 0) {
								duplicado = true;
							}
						}

						if (!apostasFavoritaListaSelecionadas.contains(aposta) && !duplicado) {
							apostasFavoritaListaSelecionadas.add(aposta);
							RapidaoConfigSingleton.getInstance().getRapidaoConfig().setApostasFavoritas(apostasFavoritaListaSelecionadas);
						}
					}
				});
			}

			@Override
			public void onErrorResponse(VolleyError error) {
				loadViewProgress.dismiss();
				RedirectNetwork.checkRedirect( error, getActivity());

			}
		});
	}

	private void getFavoritasScroll(){
		if (isMaisCompras == false) {
			return;
		}
		progressLoadingMore.setVisibility(View.VISIBLE);

		ServicoFactoryUtil.getApostaService().getApostasFavoritas(offsetItem ,new RequestListener<ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse>() {
			@Override
			public void onResponse(ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse response) {
				progressLoadingMore.setVisibility(View.GONE);
				if(response.getPayload().getLista().size() == 0){
					isMaisCompras = false;
				}else{
					apostaFavoritaLista.addAll(response.getPayload().getLista());
					apostaFavoritaAdapter.notifyDataSetChanged();
					offsetItem += 8;
				}
				bloquearRequestScroll = false;
				if (response.getRedirect() != null){
					RedirectNetwork.checkRedirectSucesso( response.getRedirect(), getActivity());
				}
			}

			@Override
			public void onErrorResponse(VolleyError error) {
				progressLoadingMore.setVisibility(View.GONE);
				bloquearRequestScroll = false;
				RedirectNetwork.checkRedirect( error, getActivity());
			}
		});
	}
}
