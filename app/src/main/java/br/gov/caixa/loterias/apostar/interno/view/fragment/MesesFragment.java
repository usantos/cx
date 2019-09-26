package br.gov.caixa.loterias.apostar.interno.view.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ParametroMesDeSorte;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.SimularApostaActivity;
import br.gov.caixa.loterias.apostar.interno.view.adapter.MesesAdapter;
import br.gov.caixa.loterias.apostar.interno.view.custom.ExpandableHeightGridView;
import br.gov.caixa.loterias.apostar.interno.view.custom.ItemRetanguloTextView;

@EFragment(R.layout.fragment_meses)
public class MesesFragment extends Fragment {

    @ViewById
    protected TextView escolhaNumerosText, paginaNumeroText, paginaQuantidadeText;

    @ViewById
    protected ExpandableHeightGridView mesesGridView;

    @FragmentArg
    protected ModalidadeEnum modalidadeEnum;

    @FragmentArg
    protected ParametroJogoDTO parametroJogoDTO;

    @FragmentArg
    protected List<Integer> dezenasSelecionadas;

    private ParametroMesDeSorte mesDeSorte;
    private SimularApostaActivity parentActivity;
    private MesesAdapter mesesAdapter;

    @AfterViews
    protected void init() {
        parentActivity = ((SimularApostaActivity) getActivity());
        switch (modalidadeEnum) {
            case DIA_DE_SORTE:
                setTelaDiaSorte();
                break;
            default:
                break;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        limparTodosMeses();
        mesesAdapter.notifyDataSetChanged();
        gerenciarStatusBotoes(true);
    }

    private void setTelaDiaSorte() {
        escolhaNumerosText.setText(ViewUtils.textFuturaAndFuturaBold(getContext(), getString(R.string.dia_sorte_escolha_mes)));
        escolhaNumerosText.setTextColor(getResources().getColor(R.color.diasortemedio));

        paginaNumeroText.setText(R.string.label_numerico_dois);
        paginaNumeroText.setTextColor(getResources().getColor(R.color.diasortemedio));

        paginaQuantidadeText.setText(getString(R.string.paginacao_quantidade, getString(R.string.label_numerico_dois)));
        paginaQuantidadeText.setTextColor(getResources().getColor(R.color.diasortemedio));

        parentActivity.getQtdNumerosButton().setOnClickListener(null);

        parentActivity.getBotaoAdicionarCompletarCartela().setText(ViewUtils.textFuturaAndFuturaBold(getContext(), getString(R.string.adicionarAoCarrinho)));
        if (dezenasSelecionadas == null) {
            parentActivity.getBotaoLimparAposta().setVisibility(View.VISIBLE);
        }
        initRecyclerView();
        setMesDeSorte();

        gerenciarStatusBotoes(mesDeSorte != null);
        acaoBotaoLimpar();
    }

    private void acaoBotaoLimpar() {
        parentActivity.getBotaoLimparAposta().setOnClickListener(v -> {
            limparTodosMeses();
            mesesAdapter.notifyDataSetChanged();
            gerenciarStatusBotoes(false);
            parentActivity.getSalvarApostaLayout().setVisibility(View.GONE);
            limparMesDeSorte();
        });
    }

    private void initRecyclerView() {
        mesesAdapter = new MesesAdapter(parametroJogoDTO.getMeses());
        mesesGridView.setAdapter(mesesAdapter);
        mesesGridView.setExpanded(true);

        mesesGridView.setOnItemClickListener((parent, view, position, id) -> {
            ItemRetanguloTextView viewClickada = (ItemRetanguloTextView) view;
            limparTodosMeses();
            viewClickada.getMesDeSorte().setSelecionado(true);
            setMesDeSorte(viewClickada.getMesDeSorte());
            mesesAdapter.notifyDataSetChanged();
            gerenciarStatusBotoes(true);
        });
    }

    private void gerenciarStatusBotoes(boolean habilitar) {
        alterarStatusBotaoAdicionar(habilitar);
        alterarStatusBotaoLimpar(habilitar);
        mostrarSalvarAposta();
    }

    private void mostrarSalvarAposta() {
        if (dezenasSelecionadas != null && getItemSelecionado() != null) {
            if(parentActivity.getFlagApostaSalva()){
                parentActivity.getSalvarApostaLayout().setBackgroundResource(R.color.cinza);
                parentActivity.getTextoSalveEstaAposta().setText(R.string.tituloSalvarAposta);
                parentActivity.getIconStarSalvar().setBackgroundResource(R.drawable.icon_favoritar);
            }
            parentActivity.getSalvarApostaLayout().setVisibility(View.VISIBLE);
            tamanhoBotaoSalvarAposta(parentActivity.getSalvarApostaLayout().getLayoutParams(), 64);
            parentActivity.getSalvarApostaLayout().setOnClickListener(v -> {
                if(!DadosUsuarioBO.checarUsuarioLogado(getActivity())){
                    RedirectNetwork.connectKeycloak(getActivity());
                }
                tamanhoBotaoSalvarAposta(parentActivity.getSalvarApostaLayout().getLayoutParams(), 140);
                parentActivity.getSalvarApostaLayout().setOnClickListener(null);
            });

            parentActivity.getSalvarApostaLayout().findViewById(R.id.btnSalvarAposta).setOnClickListener(v -> {
                EditText nomeAposta = parentActivity.getEditNomeAposta();

                if (!TextUtils.isEmpty(nomeAposta.getText().toString())) {
                    ApostaFavoritaDTO aposta = new ApostaFavoritaDTO();
                    aposta.setMesDeSorte(getItemSelecionado());
                    parentActivity.salvarAposta(aposta, nomeAposta.getText().toString(), dezenasSelecionadas);
                } else {
                    ViewUtils.alert(getActivity(), getString(R.string.msg_favor_inserir_nome_aposta));
                }
            });
        }

    }

    private void tamanhoBotaoSalvarAposta(ViewGroup.LayoutParams layoutParams, int i) {
        float scale = parentActivity.getResources().getDisplayMetrics().density;
        layoutParams.height = (int) (i * scale + 0.5f);
        parentActivity.getSalvarApostaLayout().setLayoutParams(layoutParams);
    }

    private void statusBotao(Button button, int background, int border, int textColor, boolean habilitado) {
        if (background > 0) {
            button.setBackgroundColor(ContextCompat.getColor(parentActivity, background));
        }
        if (border > 0) {
            button.setBackground(ContextCompat.getDrawable(parentActivity, border));
        }
        button.setTextColor(ContextCompat.getColor(parentActivity, textColor));
        button.setEnabled(habilitado);
    }

    private void alterarStatusBotaoAdicionar(boolean habilitar) {
        int colorBackground;
        int colorText;

        if (habilitar) {
            colorBackground = R.color.diasorteclaro;
            colorText = R.color.branco;
        } else {
            colorBackground = R.color.cinzaclaro;
            colorText = R.color.linhaDivisa;
        }
        statusBotao(parentActivity.getBotaoAdicionarCompletarCartela(), colorBackground, 0, colorText, habilitar);
    }

    private void alterarStatusBotaoLimpar(boolean habilitar) {
        int border;
        int colorText;

        if (habilitar) {
            border = R.drawable.button_white_with_border;
            colorText = R.color.cinza;
        } else {
            border = R.drawable.button_white_with_border_disabled;
            colorText = R.color.linhaDivisa;
        }
        statusBotao(parentActivity.getBotaoLimparAposta(), 0, border, colorText, habilitar);
    }

    private void limparTodosMeses() {
        if (parametroJogoDTO.getMeses() != null) {
            for (ParametroMesDeSorte mesDeSorte : parametroJogoDTO.getMeses()) {
                mesDeSorte.setSelecionado(false);
            }
        }
    }

    private ParametroMesDeSorte getItemSelecionado() {
        if (parametroJogoDTO.getMeses() != null) {
            for (ParametroMesDeSorte mesDeSorte : parametroJogoDTO.getMeses()) {
                if (mesDeSorte.isSelecionado()) {
                    return mesDeSorte;
                }
            }
        }
        return null;
    }

    private void setMesDeSorte() {
        if(dezenasSelecionadas == null){
            this.mesDeSorte = parentActivity.getMesDeSorteSurpresinha();
        }else{
            this.mesDeSorte = parentActivity.getMesDeSorteCartela();
        }

        if (parametroJogoDTO.getMeses() != null && this.mesDeSorte != null) {
            for (ParametroMesDeSorte mesDeSorte : parametroJogoDTO.getMeses()) {
                if (mesDeSorte.getNumero().equals(this.mesDeSorte.getNumero())) {
                    mesDeSorte.setSelecionado(true);
                    break;
                }
            }
        }
        mesesAdapter.notifyDataSetChanged();
    }

    private void setMesDeSorte(ParametroMesDeSorte mesDeSorte) {
        if (dezenasSelecionadas != null) {
            parentActivity.setMesDeSorteCartela(mesDeSorte);
        } else {
            parentActivity.setMesDeSorteSurpresinha(mesDeSorte);
        }
    }

    private void limparMesDeSorte() {
        if (dezenasSelecionadas != null) {
            parentActivity.setMesDeSorteCartela(getItemSelecionado());
        }else{
            parentActivity.setMesDeSorteSurpresinha(getItemSelecionado());
        }
    }
}
