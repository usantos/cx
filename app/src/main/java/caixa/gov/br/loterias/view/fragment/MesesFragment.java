package caixa.gov.br.loterias.view.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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
import caixa.gov.br.loterias.model.bo.silce.dto.ParametroMesDeSorte;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.SimularApostaActivity;
import caixa.gov.br.loterias.view.adapter.MesesAdapter;
import caixa.gov.br.loterias.view.custom.ExpandableHeightGridView;
import caixa.gov.br.loterias.view.custom.ItemRetanguloTextView;
import caixa.gov.br.loterias.view.listener.CartelaFragmentListener;

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

    private SimularApostaActivity parentActivity;
    private CartelaFragmentListener cartelaFragmentListener;
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
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            cartelaFragmentListener = (CartelaFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement CartelaFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        cartelaFragmentListener = null;
        limparTodosMeses();
        mesesAdapter.notifyDataSetChanged();
    }

    private void setTelaDiaSorte() {
        escolhaNumerosText.setText(ViewUtils.textFuturaAndFuturaBold(getContext(), getString(R.string.dia_sorte_escolha_mes)));
        escolhaNumerosText.setTextColor(getResources().getColor(R.color.diasortemedio));

        paginaNumeroText.setText(R.string.label_numerico_dois);
        paginaNumeroText.setTextColor(getResources().getColor(R.color.diasortemedio));

        paginaQuantidadeText.setText(getString(R.string.paginacao_quantidade, getString(R.string.label_numerico_dois)));
        paginaQuantidadeText.setTextColor(getResources().getColor(R.color.diasortemedio));

        parentActivity.getBotaoAdicionarCompletarCartela().setText(ViewUtils.textFuturaAndFuturaBold(getContext(), getString(R.string.adicionarAoCarrinho)));
        if(dezenasSelecionadas == null){
            parentActivity.getBotaoLimparAposta().setVisibility(View.VISIBLE);
        }
        initRecyclerView();

        alterarStatusBotaoAdicionar(false);
        alterarStatusBotaoLimpar(false);
        parentActivity.getBotaoLimparAposta().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limparTodosMeses();
                mesesAdapter.notifyDataSetChanged();
                alterarStatusBotaoAdicionar(false);
                alterarStatusBotaoLimpar(false);
                parentActivity.getSalvarApostaLayout().setVisibility(View.GONE);
            }
        });
    }

    private void initRecyclerView() {
        mesesAdapter = new MesesAdapter(parametroJogoDTO.getMeses());
        mesesGridView.setAdapter(mesesAdapter);
        mesesGridView.setExpanded(true);

        mesesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemRetanguloTextView viewClickada = (ItemRetanguloTextView) view;
                limparTodosMeses();
                viewClickada.getMesDeSorte().setSelecionado(true);
                mesesAdapter.notifyDataSetChanged();
                mostrarSalvarAposta();
                alterarStatusBotaoAdicionar(true);
                alterarStatusBotaoLimpar(true);
            }
        });
    }

    private void mostrarSalvarAposta() {
        if(dezenasSelecionadas != null){
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
                        aposta.setMesDeSorte(getItemSelecionado());
                        parentActivity.salvarAposta(aposta, nomeAposta.getText().toString(), dezenasSelecionadas);
                    } else {
                        ViewUtils.alert(getActivity(), getString(R.string.msg_favor_inserir_nome_aposta));
                    }
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
                if (mesDeSorte.isSelecionado())
                    return mesDeSorte;
            }
        }
        return null;
    }
}
