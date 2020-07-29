package br.gov.caixa.loterias.apostas.view.fragment;

import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfiguracaoNumerosSuperSete;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.utils.AppUtils;
import br.gov.caixa.loterias.apostas.utils.Utils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.DezenasAdapter;

/**
 * Created by pmotta on 23/03/2018.
 * Class ApostaConfirmadaNumeroDetalhes
 */

@EFragment(R.layout.fragment_aposta_confirmada_numero_detalhes)
public class ApostaConfirmadaNumeroDetalhes extends Fragment {

	@Bean
	protected ApostaBO apostaBO;

	@ViewById
	protected TextView concursoTxt, quantidadeApostadasTxt, dataApostaTxt, resultTxt, modalidadeTxt, seusNumerosTxt,
					   apostaTrocaTxt, informacaoAdicionalTxt, labelInformacaoAdicionalTxt, tv_colunas, tv_sua_aposta;

	@ViewById
	protected RelativeLayout informacaoAdicionalLayout, rl_container_fragment;

	@ViewById
	protected RecyclerView dezenasRecyclerView;

	@FragmentArg
	protected IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> aposta;

	@ViewById
	protected TextView tv_val_premio, tv_val_premio_tit;

	@ViewById
	protected ConstraintLayout cl_premio_pago;

	@FragmentArg
	protected BigDecimal valorPremio;

	@AfterViews
	protected void init() {
		concursoTxt.setText(String.format(Locale.getDefault(), getResources().getString(R.string.percent_d), aposta.getConcursoInicial()));
		if (!StringUtils.isEmpty(aposta.getHoraEfetivacao())) {
			dataApostaTxt.setText(aposta.getDataEfetivacao() + getResources().getString(R.string.string_vazia) + aposta.getHoraEfetivacao());
		}

		if (valorPremio != null) {
			tv_val_premio.setVisibility(View.VISIBLE);
			tv_val_premio_tit.setVisibility(View.VISIBLE);
			cl_premio_pago.setVisibility(View.VISIBLE);
			resultTxt.setVisibility(View.GONE);
			tv_val_premio.setText(ViewUtils.getMoedaFormat(valorPremio));
		} else  {
			resultTxt.setText(aposta.getSituacao().getDescricao());

		}

		if (aposta.getTroca()) {
			apostaTrocaTxt.setVisibility(View.VISIBLE);
		}
		if (aposta.getQuantidadeTeimosinhas() > 0) {
			modalidadeTxt.setVisibility(View.VISIBLE);
			quantidadeApostadasTxt.setVisibility(View.VISIBLE);

			Integer quantidade      = aposta.getQuantidadeTeimosinhas();
			String  textoTeimosinha = "";
			if (quantidade == 1) {
				textoTeimosinha = String.format(Locale.getDefault(), "%d %s", quantidade, getActivity().getString(R.string.label_aposta));
			} else {
				textoTeimosinha = String.format(Locale.getDefault(), "%d %s", quantidade, getActivity().getString(R.string.label_apostas));
			}
			quantidadeApostadasTxt.setText(textoTeimosinha);
		}
		if (aposta.getTimeDoCoracao() != null) {
			informacaoAdicionalLayout.setVisibility(View.VISIBLE);
			informacaoAdicionalTxt.setText(aposta.getTimeDoCoracao().getNome());
		} else if (aposta.getMesDeSorte() != null && aposta.getMesDeSorte().getNumero() != null) {
			informacaoAdicionalLayout.setVisibility(View.VISIBLE);
			labelInformacaoAdicionalTxt.setText(R.string.label_mes_sorte);
			informacaoAdicionalTxt.setText(aposta.getMesDeSorte().getNome());
		}

		initdezenasRecyclerView();
	}

	private void initdezenasRecyclerView() {
		if (!aposta.getNumerosSelecionados().isEmpty()) {
			if (aposta.getModalidade() == ModalidadeEnum.SUPER_7) {
				dezenasRecyclerView.setVisibility(View.GONE);
				configuraFragment(aposta);
			} else {
				dezenasRecyclerView.setAdapter(new DezenasAdapter(AppUtils.listIntegerToListString(aposta.getListaNumerosSelecionados())));
				dezenasRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
			}
		}
	}

	private void configuraFragment(IdentificaoDeUmaApostaDas8Modalidades aposta) {
		FragmentManager fm          = getActivity().getSupportFragmentManager();
		int             containerId = rl_container_fragment.getId();
		Fragment        oldFragment = fm.findFragmentById(containerId);
		if (oldFragment != null) {
			fm.beginTransaction().remove(oldFragment).commit();
		}

		int newContainerId = (int) Utils.getIdUnico();
		rl_container_fragment.setId(newContainerId);
		NumerosSuperSeteFragment fragment = NumerosSuperSeteFragment.newInstance(aposta.getMatrizNumerosSelecionados(), configuraEstiloSuperSete());
		fm.beginTransaction().replace(newContainerId, fragment).commit();
		rl_container_fragment.setVisibility(View.VISIBLE);
		tv_colunas.setVisibility(View.VISIBLE);
		tv_sua_aposta.setVisibility(View.VISIBLE);
		seusNumerosTxt.setVisibility(View.GONE);
	}

	private ConfiguracaoNumerosSuperSete configuraEstiloSuperSete() {
		return new ConfiguracaoNumerosSuperSete(R.color.branco,
												R.color.branco,
												R.color.black,
												R.color.black,
												R.color.supersetemedio,
												R.color.supersetemedio,
												R.color.supersetemedio,
												R.color.superseteescuro,
												R.color.supersetemedio);
	}
}
