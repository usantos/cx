package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;
import android.content.Intent;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.view.fragment.TutorialPassoFragment;

public class TutorialModel extends AppModel{

	public TutorialModel(Activity activity) {
		super(activity);
	}

	public List<TutorialPassoFragment> getFragments(ModalidadeEnum modalidade) {
		List<TutorialPassoFragment> fragmentList = new ArrayList<>();
		switch (modalidade) {
			case MAIS_MILIONARIA:
				fragmentList.add(TutorialPassoFragment.newInstance(R.layout.fragment_introducao_tutorial_mais_milionaria, true));
				fragmentList.add(TutorialPassoFragment.newInstance(R.layout.fragment_tutorial_mm_passo_1, false));
				fragmentList.add(TutorialPassoFragment.newInstance(R.layout.fragment_tutorial_mm_passo_2, false));
				fragmentList.add(TutorialPassoFragment.newInstance(R.layout.fragment_tutorial_mm_passo_3, false));
				fragmentList.add(TutorialPassoFragment.newInstance(R.layout.fragment_tutorial_mm_passo_4, false));
				fragmentList.add(TutorialPassoFragment.newInstance(R.layout.fragment_tutorial_mm_passo_5, false));
				fragmentList.add(TutorialPassoFragment.newInstance(R.layout.fragment_tutorial_mm_final, false));
				break;
			case  BOLAO:
				fragmentList.add(TutorialPassoFragment.newInstance(R.layout.fragment_introducao_tutorial_bolao, true));
				//fragmentList.add(TutorialPassoFragment.newInstance(R.layout.fragment_tutorial_bolao_passo_1, true));
				fragmentList.add(TutorialPassoFragment.newInstance(R.layout.fragment_tutorial_bolao_passo_2, true));
				fragmentList.add(TutorialPassoFragment.newInstance(R.layout.fragment_tutorial_bolao_passo_4, true));
				fragmentList.add(TutorialPassoFragment.newInstance(R.layout.fragment_tutorial_bolao_passo_3, true));
				fragmentList.add(TutorialPassoFragment.newInstance(R.layout.fragment_tutorial_bolao_final, true));
				break;
			default:
				// BOLAO
		}
		return fragmentList;
	}

	public String getSPModalidade(ModalidadeEnum modalidade) {
		switch (modalidade){
			case SUPER_7:
				return DadosUsuarioBO.SUPER_SETE_TUTORIAL;
			case MAIS_MILIONARIA:
				return DadosUsuarioBO.MAIS_MILIONARIA_TUTORIAL;
			case BOLAO:
				return DadosUsuarioBO.BOLAO_TUTORIAL;
			default:
				return "";
		}
	}

	public Intent getIntentModalidade(ModalidadeEnum modalidade, ParametroJogoDTO parametroJogo, Integer idModaliade, Integer tipoConcurso) {
		Intent activity = IntentUtil.getIntentFimTutorial(getActivity(), modalidade, idModaliade, tipoConcurso);

		activity.putExtra(getActivity().getString(R.string.extra_tipo_aposta), modalidade);
		if (parametroJogo != null) {
			activity.putExtra(getActivity().getString(R.string.extra_modalidade), (new Gson()).toJson(parametroJogo));
			activity.putExtra(getActivity().getString(R.string.extra_especial),
							  parametroJogo.getConcurso().getTipoConcurso().toString().equals(getActivity().getResources().getString(R.string.especial_maiusculo)));
		}

		return activity;
	}
}
