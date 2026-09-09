package br.gov.caixa.loterias.apostas.view.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.SimularApostaActivity;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroMesDeSorte;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.baseadapter.MesesAdapter;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightGridView;
import br.gov.caixa.loterias.apostas.view.custom.ItemRetanguloTextView;

public class MesesFragment extends Fragment {
    private static final String ARG_MODADLIDADE = "ARG_MODALIDADE";
    private static final String ARG_PARAMENTRO = "ARG_PARAMENTRO";
    private static final String ARG_DEZENAS = "ARG_DEZENAS";

    private ExpandableHeightGridView mesesGridView;

    private ModalidadeEnum modalidadeEnum;
    private ParametroJogoDTO parametroJogoDTO;
    private ArrayList<Integer> dezenasSelecionadas;

    private ParametroMesDeSorte mesDeSorte;
    private SimularApostaActivity parentActivity;
    private MesesAdapter mesesAdapter;
    private View view;

    private MesesFragment(){}

    public static MesesFragment newInstance(ModalidadeEnum modalidade,
                                            ParametroJogoDTO parametroJogoDTO,
                                            ArrayList<Integer> dezenasSelecionadas){
        MesesFragment fragment  = new MesesFragment();
        Bundle        args      = new Bundle();
        args.putSerializable(ARG_MODADLIDADE, modalidade);
        args.putSerializable(ARG_PARAMENTRO, parametroJogoDTO);
        args.putString(ARG_DEZENAS, String.valueOf(dezenasSelecionadas));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            modalidadeEnum = (ModalidadeEnum) getArguments().getSerializable(ARG_MODADLIDADE);
            parametroJogoDTO = (ParametroJogoDTO) getArguments().getSerializable(ARG_PARAMENTRO);
            String json = getArguments().getString(ARG_DEZENAS);
            if(json!= null && json.length() > 0){
                dezenasSelecionadas = new Gson().fromJson(json, new TypeToken<List<Integer>>() {}.getType());
            } else {
                dezenasSelecionadas = new ArrayList<>();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_meses, container, false);
        mesesGridView = view.findViewById(R.id.mesesGridView);
        init();
        return view;
    }

    private void init() {
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
        //parentActivity.getQtdNumerosButton().setOnClickListener(null);

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
                parentActivity.getSalvarApostaLayout().setBackgroundResource(R.drawable.bg_salvar_aposta);
                parentActivity.getTextoSalveEstaAposta().setText(R.string.tituloSalvarAposta);
                parentActivity.getIconStarSalvar().setBackgroundResource(R.drawable.icon_favoritar_novo);
            }
            parentActivity.getSalvarApostaLayout().setVisibility(View.VISIBLE);
            tamanhoBotaoSalvarAposta(parentActivity.getSalvarApostaLayout().getLayoutParams(), 64);
            parentActivity.getSalvarApostaLayout().setOnClickListener(v -> {
                if(!DadosUsuarioBO.checarUsuarioLogado(getActivity())){
                    RedirectNetwork.connectKeycloak(getActivity(), null);
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
                    DialogUtils.dialogEntendi(
                            getActivity(),
                            getString(R.string.msg_favor_inserir_nome_aposta)
                    );
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
            colorBackground = R.color.dia_sorte_escuro_mkp;
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
