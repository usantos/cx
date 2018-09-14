package caixa.gov.br.loterias.view.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.silce.dto.ApostaFavoritaDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.ModalidadeEnum;
import caixa.gov.br.loterias.model.bo.silce.dto.ParametroJogoDTO;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.SimularApostaActivity;
import caixa.gov.br.loterias.view.adapter.MesesAdapter;
import caixa.gov.br.loterias.view.custom.ExpandableHeightGridView;
import caixa.gov.br.loterias.view.custom.ItemRetanguloTextView;

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

    private ItemRetanguloTextView itemSelecionado;
    private SimularApostaActivity parentActivity;

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

    private void setTelaDiaSorte() {
        escolhaNumerosText.setText(ViewUtils.textFuturaAndFuturaBold(getContext(), getString(R.string.dia_sorte_escolha_mes)));
        escolhaNumerosText.setTextColor(getResources().getColor(R.color.diasortemedio));

        paginaNumeroText.setText(R.string.label_numerico_dois);
        paginaNumeroText.setTextColor(getResources().getColor(R.color.diasortemedio));

        paginaQuantidadeText.setText(getString(R.string.paginacao_quantidade, getString(R.string.label_numerico_dois)));
        paginaQuantidadeText.setTextColor(getResources().getColor(R.color.diasortemedio));

        parentActivity.getBotaoAdicionarCompletarCartela().setText(ViewUtils.textFuturaAndFuturaBold(getContext(), getString(R.string.adicionarAoCarrinho)));
        initRecyclerView();

        alterarStatusBotaoAdicionar(false);
        alterarStatusBotaoLimpar(false);
        parentActivity.getBotaoLimparAposta().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemSelecionado.setSelected(false);
                itemSelecionado = null;
                alterarStatusBotaoAdicionar(false);
                alterarStatusBotaoLimpar(false);
                parentActivity.getSalvarApostaLayout().setVisibility(View.GONE);
            }
        });
    }

    private void initRecyclerView() {
        mesesGridView.setAdapter(new MesesAdapter(parametroJogoDTO.getMeses()));
        mesesGridView.setExpanded(true);

        mesesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemRetanguloTextView viewClickada = (ItemRetanguloTextView) view;
                if (itemSelecionado != null) {
                    if (!itemSelecionado.getMesDTOResponse().getNumero().equals(viewClickada.getMesDTOResponse().getNumero())) {
                        itemSelecionado.setSelected(false);
                        itemSelecionado = viewClickada;
                        itemSelecionado.setSelected(true);
                    } else {
                        itemSelecionado.setSelected(true);
                    }
                } else {
                    itemSelecionado = viewClickada;
                    itemSelecionado.setSelected(true);
                }
                mostrarSalvarAposta();
                alterarStatusBotaoAdicionar(true);
                alterarStatusBotaoLimpar(true);
            }
        });
    }

    private void mostrarSalvarAposta() {

        parentActivity.getSalvarApostaLayout().setVisibility(View.VISIBLE);
        tamanhoBotaoSalvarAposta(parentActivity.getSalvarApostaLayout().getLayoutParams(), 64);
        parentActivity.getSalvarApostaLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tamanhoBotaoSalvarAposta(parentActivity.getSalvarApostaLayout().getLayoutParams(), 140);
                parentActivity.getSalvarApostaLayout().setOnClickListener(null);
            }
        });

        parentActivity.getSalvarApostaLayout().findViewById(R.id.btnSalvarAposta).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nomeAposta = parentActivity.getEditNomeAposta();

                if (!TextUtils.isEmpty(nomeAposta.getText().toString())) {
                    ApostaFavoritaDTO aposta = new ApostaFavoritaDTO();
                    parentActivity.salvarAposta(aposta, nomeAposta.getText().toString(), dezenasSelecionadas);
                } else {
                    ViewUtils.alert(getActivity(), getString(R.string.msg_favor_inserir_nome_aposta));
                }
            }
        });
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
        if (habilitar) {
            statusBotao(parentActivity.getBotaoAdicionarCompletarCartela(), R.color.diasorteclaro, 0, R.color.branco, habilitar);
        } else {
            statusBotao(parentActivity.getBotaoAdicionarCompletarCartela(), R.color.cinzaclaro, 0, R.color.linhaDivisa, habilitar);
        }
    }

    private void alterarStatusBotaoLimpar(boolean habilitar) {
        if (habilitar) {
            statusBotao(parentActivity.getBotaoLimparAposta(), 0, R.drawable.button_white_with_border, R.color.cinza, habilitar);
        } else {
            statusBotao(parentActivity.getBotaoLimparAposta(), 0, R.drawable.button_white_with_border_disabled, R.color.linhaDivisa, habilitar);
        }
    }
}
