package br.gov.caixa.loterias.apostas.view.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaCarrinhoFavoritoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.utils.Aplicacao;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.Utils;
import br.gov.caixa.loterias.apostas.view.fragment.NumerosSuperSeteFragment;
import br.gov.caixa.loterias.apostas.view.listener.OnDetalheCarrinhoFavoritoClickListener;

public class DetalhesCarrinhoFavoritoHolder extends LoteriasHolder<ApostaCarrinhoFavoritoDTO> implements View.OnClickListener {
	private TextView tvModalidade, tvMesDaSorte, tvNumeros, tvTrevos;
	private RelativeLayout containerFragment, trevosRl;
	private ImageButton btnExcluir;
	private ImageView ivTeimosinha;
	private OnDetalheCarrinhoFavoritoClickListener listener;
	private FragmentManager fm;
	private Context ctx;

	public DetalhesCarrinhoFavoritoHolder(View view, OnDetalheCarrinhoFavoritoClickListener listener, FragmentManager fm) {
		super(view);
		tvModalidade = view.findViewById(R.id.tv_modalidade);
		tvMesDaSorte = view.findViewById(R.id.tv_mes_da_sorte);
		tvNumeros = view.findViewById(R.id.tv_numeros);
		btnExcluir = view.findViewById(R.id.ib_excluir);
		ivTeimosinha = view.findViewById(R.id.iv_teimosinha);
		containerFragment = view.findViewById(R.id.rl_container_fragment);
		this.listener = listener;
		this.fm = fm;
		btnExcluir.setOnClickListener(this);
		trevosRl = view.findViewById(R.id.trevosRl);
		tvTrevos = view.findViewById(R.id.tvTrevos);
		ctx = Aplicacao.application.getApplicationContext();
	}

	@Override
	public void bind(ApostaCarrinhoFavoritoDTO apostaAtual, int position) {
		EstiloModalidadeMKP estilo = new EstiloModalidadeMKP(ModalidadeEnum.fromModalidadeDTO(apostaAtual.getModalidade()));

		if (apostaAtual.getModalidade().getValor() == 9){
			tvModalidade.setText(ctx.getResources().getString(R.string.label_mais_milionaria).toLowerCase());
		} else {
			//Timemania
			if (apostaAtual.getModalidade().getValor() == 20) {
				tvModalidade.setText(apostaAtual.getModalidade().getDescricao());
				tvMesDaSorte.setVisibility(View.VISIBLE);
				String timeDoCoracao = "";
				if (apostaAtual.getTimeDoCoracao() != null) {
					if (apostaAtual.getTimeDoCoracao().getNome() != null){
						timeDoCoracao = apostaAtual.getTimeDoCoracao().getNome();
					}
					if (apostaAtual.getTimeDoCoracao().getUf() != null){
						timeDoCoracao = timeDoCoracao + "/" + apostaAtual.getTimeDoCoracao().getUf();
					}
				}
				tvMesDaSorte.setText(timeDoCoracao);
				tvMesDaSorte.setTextColor(ContextCompat.getColor(ctx, estilo.getCorLetraLista()));
				if (isSurpresinha(apostaAtual)){
					tvMesDaSorte.setText("???");
				}
			} else
				tvModalidade.setText(apostaAtual.getModalidade().getDescricao().toLowerCase());
		}

		tvModalidade.setTextColor(ContextCompat.getColor(ctx, estilo.getCorLetraLista()));
		tvNumeros.setText(configuraNumeros(apostaAtual));

		if (apostaAtual.getQuantidadeTeimosinhas() > 0) {
			ivTeimosinha.setVisibility(View.VISIBLE);
		}

		if (apostaAtual.getModalidade().getValor() == 9){
			trevosRl.setVisibility(View.VISIBLE);
			String trevos = " ";

			if (apostaAtual.getIndicadorSurpresinha() != ApostaCarrinhoFavoritoDTO.IndicadorSurpresinhaEnum.NAO_SURPRESINHA) {
				for (int i = 0; i < apostaAtual.getQuantidadeTrevos(); i++) {
					trevos += "XX" + " - ";
				}
			} else {
				for (Object i:  apostaAtual.getParametroTrevo().getTrevosSelecionados()) {
					trevos += i.toString() + " - ";
				}
			}
			tvTrevos.setText("Trevos: " + trevos.substring(0, trevos.length() - 2));
		}

		if (apostaAtual.getModalidade().getDescricao().equalsIgnoreCase("dia de sorte")) {
			if (apostaAtual.getMesDeSorte() != null && apostaAtual.getMesDeSorte().getNome() != null) {
				tvMesDaSorte.setText(apostaAtual.getMesDeSorte().getNome());
				tvMesDaSorte.setVisibility(View.VISIBLE);
				tvMesDaSorte.setTextColor(ContextCompat.getColor(ctx, estilo.getCorLetraLista()));
			}
			if (isSurpresinha(apostaAtual)){
				tvMesDaSorte.setText("???");
			}
		}
	}

	private String configuraNumeros(ApostaCarrinhoFavoritoDTO aposta) {
		StringBuilder numeroStr   = new StringBuilder();
		Boolean       isSuperSete = aposta.getModalidade().getValor().intValue() == 7;
		if (aposta.getIndicadorSurpresinha() != ApostaCarrinhoFavoritoDTO.IndicadorSurpresinhaEnum.NAO_SURPRESINHA) {
			for (int i = 0; i < aposta.getQuantidadeNumeros(); i++) {
				if (i == aposta.getQuantidadeNumeros() - 1) {
					numeroStr.append("XX");
				} else {
					numeroStr.append("XX - ");
				}
			}
		} else {
			if (!isSuperSete) {
				List<Integer> numerosSelecionados = aposta.getListaNumerosSelecionados();
				for (Integer num : numerosSelecionados) {
					if (num.equals(numerosSelecionados.get(numerosSelecionados.size() - 1))) {
						numeroStr.append(String.format("%02d", num));
					} else {
						numeroStr.append(String.format("%02d - ", num));
					}
				}
			} else {
				configuraFragment(aposta);
				tvNumeros.setVisibility(View.GONE);
				containerFragment.setVisibility(View.VISIBLE);
			}
		}

		if (numeroStr.toString().contains("100")){
			return numeroStr.toString().replace("100", "00");
		}

		return numeroStr.toString();
	}

	private void configuraFragment(ApostaCarrinhoFavoritoDTO aposta) {
		int             containerId = containerFragment.getId();
		Fragment oldFragment = fm.findFragmentById(containerId);
		if (oldFragment != null) {
			fm.beginTransaction().remove(oldFragment).commit();
		}

		int newContainerId = (int) Utils.getIdUnico();
		containerFragment.setId(newContainerId);

		NumerosSuperSeteFragment fragment = NumerosSuperSeteFragment.newInstance(aposta.getMatrizNumerosSelecionados());
		fm.beginTransaction().replace(newContainerId, fragment).commit();
	}

	private boolean isSurpresinha(ApostaCarrinhoFavoritoDTO aposta){
		return aposta.getIndicadorSurpresinha() == ApostaCarrinhoFavoritoDTO.IndicadorSurpresinhaEnum.SURPRESINHA;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.ib_excluir:
				listener.onExcluirCarrinho(getAbsoluteAdapterPosition());
				break;
			default:
				break;
		}
	}
}
