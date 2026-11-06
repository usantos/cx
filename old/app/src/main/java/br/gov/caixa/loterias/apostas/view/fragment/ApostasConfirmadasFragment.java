package br.gov.caixa.loterias.apostas.view.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.ApostaConfirmadaDetalhesActivity;
import br.gov.caixa.loterias.apostas.controllers.DetalheMinhasApostasActivity;
import br.gov.caixa.loterias.apostas.controllers.MinhasApostasActivity;
import br.gov.caixa.loterias.apostas.model.bean.ApostaPageRequest;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.ApostaMicroServicoBO;
import br.gov.caixa.loterias.apostas.model.bo.ApostaSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.NovaAPIBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComprovanteApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComprovanteApostaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfigConsultaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumLong;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesHistoricoPremioDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametrosSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PeriodoConsultaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOApostaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOHistoricoApostaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacaoAposta;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacaoResultadoBilheteEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoConcursoEnum;
import br.gov.caixa.loterias.apostas.model.model.ApostaConfirmadaModel;
import br.gov.caixa.loterias.apostas.model.model.ParametrosSimulacaoModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.ApostaUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.AppUtils;
import br.gov.caixa.loterias.apostas.utils.ConfiguracoesDefaultEnum;
import br.gov.caixa.loterias.apostas.utils.ConfiguracoesEnum;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.MensagemUtils;
import br.gov.caixa.loterias.apostas.utils.NovaApiUtils;
import br.gov.caixa.loterias.apostas.utils.SharedPreferencesUtils;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.SelecaoConcursoUtils;
import br.gov.caixa.loterias.apostas.utils.TestVisao;
import br.gov.caixa.loterias.apostas.utils.helper.AdicionarApostaCarrinhoHelper;
import br.gov.caixa.loterias.apostas.utils.helper.ConferirResultadoHandler;
import br.gov.caixa.loterias.apostas.utils.helper.ConferirResultadoHelper;
import br.gov.caixa.loterias.apostas.view.adapter.baseadapter.FiltroApostasAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ApostasConfirmadasAdapter;
import br.gov.caixa.loterias.apostas.view.holder.ApostaConfirmadaHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnApostaConfirmadaListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogBotaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnModalidadesAbertasListener;

/**
 * Created by pmotta on 16/03/2018.
 * Class ApostasConfirmadasFragment
 */

public class ApostasConfirmadasFragment extends Fragment implements MinhasApostasActivity.OnScrollReachBottomListener,
        ConferirResultadoHandler {
    private static final int PAGE_SIZE = 30;
    private static final String ARG_IS_HISTORICO = "ARG_IS_HISTORICO";
    private static final String ARG_CONFIG_CONSULTA = "ARG_CONFIG_CONSULTA";

    private ApostaSilceBO apostaSilceBO;
    private TextView dateResultTxt;
    private RecyclerView apostasConfirmadasRecyclerView;
    private View view;
    private ProgressBar apostasLoading;
    private LinearLayout dataBtn;
    private boolean isHistorico;

    protected FiltroApostasAdapter filtroAdapter;
    private Dialog dialogData;
    private NumberPicker npData;
    private List<PeriodoConsultaDTO> mesesAnosDisponiveis;
    private List<String> valoresApresentados;
    private boolean hasMoreItems;
    private boolean isLoading;
    private boolean firstTime = true;
    private boolean reloadingEntireList = false;
    private int numeroDataSelecionada = 0;
    private boolean chamarApostaNovamente = true;
    private Long rowCount = Long.MAX_VALUE;

    private ApostaPageRequest page;

    private ApostaConfirmadaModel model;


    private boolean isOnCreate;
    private int ordenacaoDefault = 3;
    private ConfigConsultaDTO configConsulta;

    public ApostasConfirmadasFragment(){}

    public static ApostasConfirmadasFragment newInstance(boolean isHistorico, ConfigConsultaDTO configConsulta){
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_HISTORICO, isHistorico);
        args.putSerializable(ARG_CONFIG_CONSULTA, configConsulta);

        ApostasConfirmadasFragment fragment = new ApostasConfirmadasFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configConsulta = (ConfigConsultaDTO) getArguments().getSerializable(ARG_CONFIG_CONSULTA);
        //model = new ApostaConfirmadaModel(getActivity());

        isOnCreate = true;
        apostaSilceBO = ApostaSilceBO.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_apostas_confirmadas, container, false);
        dateResultTxt = view.findViewById(R.id.dateResultTxt);
        apostasConfirmadasRecyclerView = view.findViewById(R.id.apostasConfirmadasRecyclerView);
        apostasLoading = view.findViewById(R.id.apostasLoading);
        dataBtn = view.findViewById(R.id.dateSelectorBtn);
        dataBtn.setOnClickListener(v -> dateSelectorBtn());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(isOnCreate) {
            if (AlertDialogUtils.isShow()) {
                AlertDialogUtils.dismiss();
            }

            if (firstTime || page == null) {
                AppCenterManager.registraEvento("ENTROU_GERENCIAR_APOSTAS");

                if (getActivity() != null) {
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    apostasConfirmadasRecyclerView.setLayoutManager(layoutManager);
                    apostasConfirmadasRecyclerView.setNestedScrollingEnabled(false);
                    requestMesesAnosDisponiveis();
                    firstTime = false;
                }
            } else {
                if (page == null) {
                    page = new ApostaPageRequest(getPageSize());
                    page.setOffset(0);
                } else {
                    page.setSize(getPageSize());
                    page.setOffset(0);
                    hasMoreItems = true;
                }
                requestApostasConfirmadas();
            }
            isOnCreate = false;
        }
    }

    private int getPageSize(){
        if (isFiltro45Dias())
            return 32;
        else
            return PAGE_SIZE;
    }

    private boolean isFiltro45Dias() {
        return mesesAnosDisponiveis != null && !mesesAnosDisponiveis.isEmpty() &&
                mesesAnosDisponiveis.get(numeroDataSelecionada) != null &&
                mesesAnosDisponiveis.get(numeroDataSelecionada).getMes() != null &&
                (mesesAnosDisponiveis.get(numeroDataSelecionada).getMes().getValor() == 45 ||
                        mesesAnosDisponiveis.get(numeroDataSelecionada).getMes().getValor().equals(45l));
    }

    private void dateSelectorBtn() {
        if (dialogData != null){
            dialogData.show();
        }
    }

    private void setaDialogFiltrar() {
        dialogData = new Dialog(getContext());
        dialogData.setContentView(R.layout.dialog_filtro_apostas);
        npData = dialogData.findViewById(R.id.np_data_aposta);
        Button btnOk = dialogData.findViewById(R.id.btn_ok);
        Button btnCancelar = dialogData.findViewById(R.id.btn_cancelar);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogData.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;

        btnOk.setOnClickListener(v -> filtrarData());
        btnCancelar.setOnClickListener(v -> dialogData.dismiss());
        dialogData.getWindow().setAttributes(lp);

        npData = dialogData.findViewById(R.id.np_data_aposta);
        npData.setMinValue(1);
        npData.setMaxValue(mesesAnosDisponiveis.size());
        npData.setValue(numeroDataSelecionada + 1);
        npData.setDisplayedValues(valoresApresentados.toArray(new String[valoresApresentados.size()]));
        npData.setOnValueChangedListener(new ListenerData());
        npData.setWrapSelectorWheel(false);

    }

    private void filtrarData(){
        PeriodoConsultaDTO periodo = mesesAnosDisponiveis.get(numeroDataSelecionada);
        if(page == null){
            page = new ApostaPageRequest(getPageSize());
        } else {
            page.setSize(getPageSize());
        }
        if (periodo.getAno() != null){
            page.setAno(periodo.getAno().intValue());
        } else {
            page.setAno(0);
        }
        page.setMes(periodo.getMes().getValor().intValue());

        page.setOffset(0);

        hasMoreItems = true;

        updateResultDate();
        requestApostasConfirmadas();
        dialogData.dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AlertDialogUtils.dismiss();
    }

    @Override
    public void onScrollReachBottom() {
        if (!isLoading && hasMoreItems) {
            requestApostasConfirmadas();
        }
    }

    private class ListenerData implements NumberPicker.OnValueChangeListener {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            numeroDataSelecionada = newVal - 1; }
    }

    private void requestApostasConfirmadas() {
        if (page == null) {
            return;
        }

        isLoading = true;
        apostasLoading.setVisibility(View.VISIBLE);

        if (isHistorico) {
            apostaSilceBO.getApostasHistorico(page, new RequestListener<ResultadoPesquisaPaginadaDTOHistoricoApostaDTOResponse>() {
                @Override
                public void onResponse(ResultadoPesquisaPaginadaDTOHistoricoApostaDTOResponse result) {
                    onApostasConfirmadasResult(result.getPayload().getLista());
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    onApostasConfirmadasError(error);
                }
            });
        } else {
            if (NovaApiUtils.isPossoBuscarNovaAPI()) {
                requestApostasNovaApi();
            } else if (Boolean.TRUE.equals(
                            SharedPreferencesUtils.getValorBoolean(
                            ConfiguracoesEnum.IS_MICRO_SERVICO.get(),
                            ConfiguracoesDefaultEnum.IS_MICRO_SERVICO.asBoolean()
                    ))
            ){
                requestApostasMicroServico();
            }else {
                requesteApostasSilce();
            }
        }
    }

    private void requestApostasNovaApi() {
        NovaAPIBO.getInstance().getApostasConfirmadas(page, new RequestListener<ResultadoPesquisaPaginadaDTOApostaDTOResponse>() {
            @Override
            public void onResponse(ResultadoPesquisaPaginadaDTOApostaDTOResponse result) {
                chamarApostaNovamente = false;
                for (ApostaDTO aposta: result.getPayload().getLista()){
                    aposta.setValor(new BigDecimal(1));
                }
                rowCount = result.getPayload().getRowCount();
                onApostasConfirmadasResult(result.getPayload().getLista());
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                onApostasConfirmadasError500(error);
            }
        });
    }
    private void requestApostasMicroServico() {
        ApostaMicroServicoBO.getInstance().getApostasConfirmadasMicroServico(page, new RequestListener<ResultadoPesquisaPaginadaDTOApostaDTOResponse>() {
            @Override
            public void onResponse(ResultadoPesquisaPaginadaDTOApostaDTOResponse result) {
                chamarApostaNovamente = false;
                for (ApostaDTO aposta: result.getPayload().getLista()){
                    aposta.setValor(new BigDecimal(1));
                }
                rowCount = result.getPayload().getRowCount();
                onApostasConfirmadasResult(result.getPayload().getLista());
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                onApostasConfirmadasError500(error);
            }
        });
    }

    private void requesteApostasSilce() {
        apostaSilceBO.getApostasConfirmadasSilce(page, new RequestListener<ResultadoPesquisaPaginadaDTOApostaDTOResponse>() {
            @Override
            public void onResponse(ResultadoPesquisaPaginadaDTOApostaDTOResponse result) {
                chamarApostaNovamente = false;
                onApostasConfirmadasResult(result.getPayload().getLista());
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                onApostasConfirmadasError500(error);
            }
        });
    }

    public void filtraDados(){
        if (page == null){
            page = new ApostaPageRequest(getPageSize());
        }
        page.setModalidade(0);
        page.setSituacao(1);
        page.setOrdenarPor(ordenacaoDefault);
        page.setTipoAposta(1);
        if(!isHistorico){
            if(filtroAdapter != null){
                if(filtroAdapter.getNpSituacao() != null) {
                    //page.setSituacao(filtroAdapter.getNpSituacao().getValue());
                    page.setSituacao(filtroAdapter.getKeySituacaoSelecionada());
                }
                if(filtroAdapter.getAdapterModalidades() != null){
                    if(filtroAdapter.getAdapterModalidades().getModalidadesSelecionadas().size() > 0){
                        page.setModalidade(filtroAdapter.getAdapterModalidades().getModalidadesSelecionadas().get(0).getValor());
                    }
                }
                if(filtroAdapter.getNpOrdenarPor() != null) {
                    page.setOrdenarPor(filtroAdapter.getNpOrdenarPor().getValue());
                }
                if(filtroAdapter.getNpTipoAposta() != null) {
                    page.setTipoAposta(filtroAdapter.getNpTipoAposta().getValue());
                }
                if(filtroAdapter.getNpTipoConcurso() != null) {
                    page.setTipoConcurso(filtroAdapter.getNpTipoConcurso().getValue());
                }

                if (filtroAdapter.getBotoesFiltroApostas() != null){
                    page.setTeimosinha(filtroAdapter.getBotoesFiltroApostas().getTeimosinha());
                    page.setSurpresinha(filtroAdapter.getBotoesFiltroApostas().getSurpresinha());
                    page.setCombo(filtroAdapter.getBotoesFiltroApostas().getCombo());
                }


                page.setOffset(0);
                reloadingEntireList = true;
                requestApostasConfirmadas();
                hasMoreItems = true;
            }
        }
    }

    private void clearItemDecorations() {
        while (apostasConfirmadasRecyclerView.getItemDecorationCount() != 0) {
            apostasConfirmadasRecyclerView.removeItemDecorationAt(0);
        }
    }

    private void onApostasConfirmadasResult(final List<ApostaDTO> apostasList) {
        isLoading = false;

        if (getActivity() == null) {
            return;
        }

        AlertDialogUtils.dismiss();

        if (apostasList != null) {
            if (page.getOffset() == 0) {
                reloadingEntireList = true;
            }
            page.setOffset(page.getOffset() + apostasList.size());

            ApostasConfirmadasAdapter adapter = (ApostasConfirmadasAdapter) apostasConfirmadasRecyclerView.getAdapter();
            if (adapter == null || reloadingEntireList) {
                reloadingEntireList = false;
                if (apostasList.size() == 0) {
                    clearItemDecorations();
                    apostasConfirmadasRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), 0));
                } else {
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    DividerItemDecoration divider = new DividerItemDecoration(getActivity(), layoutManager.getOrientation());
                    Drawable dividerDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.recyclerview_divider);
                    if (dividerDrawable != null) {
                        divider.setDrawable(dividerDrawable);
                    }
                    clearItemDecorations();
                    apostasConfirmadasRecyclerView.addItemDecoration(divider);
                }
                adapter = new ApostasConfirmadasAdapter(((MinhasApostasActivity)getActivity()), getActivity(), apostasList, isHistorico, this);
                adapter.setOnApostasConfirmadasListener(getOnApostasConfirmadasListener(apostasList));
                apostasConfirmadasRecyclerView.setAdapter(adapter);
            } else {
                int cont = adapter.addApostasList(apostasList) ;
                adapter.notifyItemRangeInserted(cont, apostasList.size());
            }

            try {
                if (apostasConfirmadasRecyclerView != null && apostasConfirmadasRecyclerView.getAdapter() != null){
                    if (((ApostasConfirmadasAdapter) apostasConfirmadasRecyclerView.getAdapter()).getItemCount() >= rowCount) {
                        hasMoreItems = false;
                    }
                }
            } catch (Exception e){}

        }
        if (TestVisao.isTeste()){
            try {
                TestVisao.toast("Tamanho carregado: " + apostasConfirmadasRecyclerView.getAdapter().getItemCount() + "\nTotal: " + rowCount);
            }catch (Exception e){}
        }
        apostasLoading.setVisibility(View.GONE);
    }

    private OnApostaConfirmadaListener getOnApostasConfirmadasListener(List<ApostaDTO> apostasList) {
        return new OnApostaConfirmadaListener() {
            @Override
            public void onEmptyCellClick() {
                getActivity().onBackPressed();
            }

            @Override
            public void onReload(int position) {
                if (!AdicionarApostaCarrinhoHelper.usuarioEstaSuspenso(getActivity())) {
                    AdicionarApostaCarrinhoHelper.checagemModadalidadesAbertas(apostasList.get(position).getModalidade(), new OnModalidadesAbertasListener() {
                        @Override
                        public void ambasAbertas(List<ParametroSimulacao> arrayConcursos) {
                            ApostaDTO apostaDTO = apostasList.get(position);
                            SelecaoConcursoUtils.selecionarConcurso(getActivity(), arrayConcursos,
                                    (tipoConcurso) -> {
                                        if (tipoConcurso.toString().equalsIgnoreCase(apostaDTO.getTipoConcurso().getValor())){
                                            AdicionarApostaCarrinhoHelper.incluirApostaCarrinhoMinhasApostas(getActivity(), apostaDTO);
                                        } else {
                                            AdicionarApostaCarrinhoHelper.incluirApostaCarrinhoMinhasApostas(getActivity(),
                                                    ApostaUtils.converteTipoDeConcurso(apostaDTO));
                                        }
                                    });
                        }

                        @Override
                        public void ambasFechadas() {
                            AlertDialogUtils.dismiss();
                            DialogUtils.dialogEntendi(getContext(),"Modalidade não disponível no momento.");
                        }

                        @Override
                        public void unicaModalidade(List<ParametroSimulacao> arrayConcursos, TipoConcursoEnum tipoConcursoEnum) {
                            ApostaDTO apostaDTO = apostasList.get(position);

                            if (tipoConcursoEnum.getValor().equals(apostaDTO.getTipoConcurso().getValor())){
                                AdicionarApostaCarrinhoHelper.incluirApostaCarrinhoMinhasApostas(getActivity(), apostaDTO);
                            } else {
                                String msg = MensagemUtils.getMensagemConverteModalidade(apostaDTO.getModalidade(), apostaDTO.getTipoConcurso().getValor());
                                DialogUtils.dialogConfirmar(getContext(), msg,

                                        new OnDialogBotaoListener() {
                                            @Override
                                            public void onButtonClick(DialogInterface dialog, int which) {
                                                AdicionarApostaCarrinhoHelper.incluirApostaCarrinhoMinhasApostas(getActivity(),
                                                ApostaUtils.converteTipoDeConcurso(apostaDTO));
                                            }
                                        }
                                );
                            }
                        }
                    });

                }
            }

            @Override
            public void onItemReload(int position, ApostaConfirmadaHolder holder) {
                NovaAPIBO.getInstance().getRecuperarAposta(apostasList.get(position).getId(), new RequestListener<ApostaDTOResponse>() {
                    @Override
                    public void onResponse(ApostaDTOResponse response) {
                        //"id": 2154911539,
                        //Combo
                        //"especialInclusoTeimosinha": false,
                        //"geraEspelho": false,
                        //"horaCompra": "14:16:34",
                        //"surpresinha": false,
                        apostasList.get(position).setSituacao(response.getPayload().getApostaDTO().getSituacao());
                        apostasList.get(position).setModalidade(response.getPayload().getApostaDTO().getModalidade());
                        apostasList.get(position).setConcursoInicial(response.getPayload().getApostaDTO().getConcursoInicial());
                        apostasList.get(position).setDataCompra(response.getPayload().getApostaDTO().getDataCompra());
                        apostasList.get(position).setEspelho(response.getPayload().getApostaDTO().getEspelho());
                        apostasList.get(position).setIndicadorCotaBolao(response.getPayload().getApostaDTO().getIndicadorCotaBolao());
                        apostasList.get(position).setIndicadorSurpresinha(response.getPayload().getApostaDTO().getIndicadorSurpresinha());
                        apostasList.get(position).setNumerosSelecionados(response.getPayload().getApostaDTO().getNumerosSelecionados());
                        apostasList.get(position).setQuantidadeApostas(response.getPayload().getApostaDTO().getQuantidadeApostas());
                        apostasList.get(position).setQuantidadeTeimosinhas(response.getPayload().getApostaDTO().getQuantidadeTeimosinhas());
                        apostasList.get(position).setTipoConcurso(response.getPayload().getApostaDTO().getTipoConcurso());
                        apostasList.get(position).setTroca(response.getPayload().getApostaDTO().getTroca());

                        holder.bind(apostasList.get(position), position);
                        //holder.showReloadImg(false);
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        RedirectNetwork.checkRedirect(error, getActivity());
                    }
                });
            }

            @Override
            public void onItemClick(final int position, ApostaConfirmadaHolder holder) {
                AlertDialogUtils.show(getActivity());
                ApostaDTO apostaDTO = apostasList.get(position);
                if (isHistorico) {
                    handleOnItemClickHistorico(apostaDTO);
                } else {
                    handleOnClickItemConfirmadas(position, apostaDTO, holder);
                }
            }
        };
    }

    private void handleOnClickItemConfirmadas(int position, ApostaDTO apostaDTO, ApostaConfirmadaHolder holder) {
        if (holder.naoConseguiuApresentar()){
            AlertDialogUtils.show(getContext());
            holder.showProgressBarSituacao(true);
            new ParametrosSimulacaoModel(getActivity()).buscaParametroSiumulacao(new OnSilceListener<ParametrosSimulacao>() {
                @Override
                public void success(ParametrosSimulacao payload) {
                    AlertDialogUtils.dismiss();
                    holder.showProgressBarSituacao(false);
                    SessaoUsuario.getInstance().setParametrosSimulacao(payload);
                    apostasConfirmadasRecyclerView.getAdapter().notifyDataSetChanged();
                }

                @Override
                public void error(VolleyError error) {
                    AlertDialogUtils.dismiss();
                    holder.showProgressBarSituacao(false);
                }
            });
        } else if (holder.precisaConferirSituacao()){
            conferirResultado(position, apostaDTO, holder);
        }else {
            if (Objects.equals(apostaDTO.getSituacao().getValor(), SituacaoResultadoBilheteEnum.PAGA.getValor())) {

                apostaSilceBO.getApostaComprovantePremio(apostaDTO.getId(), new RequestListener<DetalhesPremioDTOResponse>() {
                    @Override
                    public void onResponse(DetalhesPremioDTOResponse result) {
                        AlertDialogUtils.dismiss();

                        //SERVICO /apostas/{id}/comprovante-premio Informacao COMBO está errada
                        result.getPayload().getAposta().setCombo(apostaDTO.getCombo());

//                        boolean isTeimosinha = result.getPayload().getAposta().getQuantidadeTeimosinhas() > 0;
//                        boolean isBolao = result.getPayload().getAposta().getIndicadorCotaBolao();
//                        boolean isSimples = !isTeimosinha && !isBolao;
//                        boolean isLoteca = result.getPayload().getAposta().getModalidade() == ModalidadeEnum.LOTECA;
//
//                        if (isTeimosinha || (isBolao && !isLoteca) || (isSimples && !isLoteca)) {
                            Intent intent = IntentUtil.getIntentOrigemDestino(getActivity(), DetalheMinhasApostasActivity.class,
                                    DetalheMinhasApostasActivity.ARG_DETALHES_PREMIO, result.getPayload());
                            startActivity(intent);
//                        } else {
//                            Intent intent = IntentUtil.getIntentOrigemDestino(getActivity(), ApostaConfirmadaDetalhesActivity.class,
//                                    ApostaConfirmadaDetalhesActivity.ARG_DETALHES_PREMIO, result.getPayload());
//                            startActivity(intent);
//                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        onApostasConfirmadasError(error);
                    }
                });
            } else {
                apostaSilceBO.getApostaDetalheComprovante(apostaDTO.getId(), new RequestListener<ComprovanteApostaDTOResponse>() {
                    @Override
                    public void onResponse(ComprovanteApostaDTOResponse result) {
                        AlertDialogUtils.dismiss();
                        ComprovanteApostaDTO comprovante = result.getPayload();
                        if (SharedPreferencesUtils.getValorBoolean(ConfiguracoesEnum.IS_MICRO_SERVICO.get(), ConfiguracoesDefaultEnum.IS_MICRO_SERVICO.asBoolean())){
                            comprovante.getAposta().setSituacao(apostaDTO.getSituacao());
                        }

                        //Utilizada na Teimosinha, apostaDTO-Situacao.PREMIADA e comprovante-Situacao-EFETIVADA
                        if (comprovante.getAposta().getQuantidadeTeimosinhas() >0) {
                            if (apostaDTO.getSituacao().getValor() == SituacaoAposta.PREMIADA && comprovante.getAposta().getSituacao().getValor() == SituacaoAposta.EFETIVADA) {
                                comprovante.getAposta().setSituacao(apostaDTO.getSituacao());
                            }
                        }

                        //SERVICO /apostas/:idAposta/comprovante  Informacao COMBO está errada
                        comprovante.getAposta().setCombo(apostaDTO.getCombo());

                        if (NovaApiUtils.isPossoBuscarNovaAPI()){
                            comprovante.getAposta().setSituacao(apostaDTO.getSituacao());
                        }

                        startDetalheComprovante(comprovante);
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        onApostasConfirmadasError(error);
                    }
                });
            }
        }
    }

    private void startDetalheComprovante(ComprovanteApostaDTO comprovante) {

//        boolean isTeimosinha = comprovante.getAposta().getQuantidadeTeimosinhas() > 0;
//        boolean isBolao = comprovante.getAposta().getIndicadorCotaBolao();
//        boolean isSimples = !isTeimosinha && !isBolao;
//        boolean isLoteca = comprovante.getAposta().getModalidade() == ModalidadeEnum.LOTECA;

//        if (isTeimosinha || (isBolao && !isLoteca) || (isSimples && !isLoteca)) {
            Intent intent = IntentUtil.getIntentOrigemDestino(getActivity(), DetalheMinhasApostasActivity.class,
                    DetalheMinhasApostasActivity.ARG_COMPROVANTE, comprovante);
            startActivity(intent);
//        } else {
//            Intent intent = IntentUtil.getIntentOrigemDestino(getActivity(), ApostaConfirmadaDetalhesActivity.class,
//                    ApostaConfirmadaDetalhesActivity.ARG_COMPROVANTE, comprovante);
//            startActivity(intent);
//        }
    }

    private void handleOnItemClickHistorico(ApostaDTO apostaDTO) {
        if (Objects.equals(apostaDTO.getSituacao().getValor(), SituacaoResultadoBilheteEnum.PAGA_HISTORICO.getValor())) {
            apostaSilceBO.getApostaHistoricoComprovante(apostaDTO.getId(), new RequestListener<DetalhesHistoricoPremioDTOResponse>() {
                @Override
                public void onResponse(DetalhesHistoricoPremioDTOResponse result) {
                    Intent intent = IntentUtil.getIntentOrigemDestino(getActivity(), ApostaConfirmadaDetalhesActivity.class,
                            ApostaConfirmadaDetalhesActivity.ARG_DETALHES_HISTORICO, result.getPayload());
                    startActivity(intent);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    onApostasConfirmadasError(error);
                }
            });
        } else {
            String                                json                  = new Gson().toJson(apostaDTO);
            IdentificaoDeUmaApostaDas8Modalidades apostaDas8Modalidades = new Gson().fromJson(json, IdentificaoDeUmaApostaDas8Modalidades.class);
            ComprovanteApostaDTO                  comprovanteApostaDTO  = new ComprovanteApostaDTO();
            comprovanteApostaDTO.setAposta(apostaDas8Modalidades);
            AlertDialogUtils.dismiss();
            startDetalheComprovante(comprovanteApostaDTO);
        }
    }

    private void requestMesesAnosDisponiveis() {
        //AlertDialogUtils.show(getActivity());
        //model.requestConfigConsulta(onConfigConsultaListener());
        onMesesAnosDisponiveisResult();
    }

//    private OnSilceListener<ConfigConsultaDTO> onConfigConsultaListener() {
//        return new OnSilceListener<ConfigConsultaDTO>() {
//            @Override
//            public void success(ConfigConsultaDTO payload) {
//                configConsulta = payload;
//                onMesesAnosDisponiveisResult();
//            }
//
//            @Override
//            public void error(VolleyError error) {
//                onMesesAnosDisponiveisError(error);
//            }
//        };
//    }

    private void onMesesAnosDisponiveisResult() {
        if (getActivity() == null) {
            return;
        }

        //AlertDialogUtils.dismiss();
        mesesAnosDisponiveis = new ArrayList<>();
        mesesAnosDisponiveis.addAll(configConsulta.getPeriodos());

        if (mesesAnosDisponiveis != null && !mesesAnosDisponiveis.isEmpty()) {
            PeriodoConsultaDTO periodo;
            if (configConsulta.getPeriodoPadrao() != null && listaPeriodoContemPadrao()){
                periodo = configConsulta.getPeriodoPadrao();
                numeroDataSelecionada = getIndicePeriodo(periodo);
            } else {
                periodo = mesesAnosDisponiveis.get(0);
                numeroDataSelecionada = 0;
            }

            int mes = periodo.getMes().getValor().intValue();
            int ano = 0;
            if(periodo.getAno() != null){
                ano = periodo.getAno().intValue();
            }

            if(page == null){
                page = new ApostaPageRequest(getPageSize());
            } else {
                page.setSize(getPageSize());
            }
            page.setAno(ano);
            page.setMes(mes);
            page.setOffset(0);
            page.setOrdenarPor(ordenacaoDefault);
            page.setSituacao(1);
            setValoresApresentados(mesesAnosDisponiveis);
            setaDialogFiltrar();

            hasMoreItems = true;
            requestApostasConfirmadas();
        } else {
            page = null;
            hasMoreItems = false;
        }

        updateResultDate();
    }

    private int getIndicePeriodo(PeriodoConsultaDTO periodo) {
        for (int i = 0; i < configConsulta.getPeriodos().size(); i++){
            if (periodo.getMes().equals(configConsulta.getPeriodos().get(i).getMes())){
                return i;
            }
        }
        return 0;
    }

    private boolean listaPeriodoContemPadrao() {
        if (configConsulta.getPeriodoPadrao() != null && configConsulta.getPeriodos() != null &&
            !configConsulta.getPeriodos().isEmpty()){
            for (PeriodoConsultaDTO periodo : configConsulta.getPeriodos()){
                if (periodo.getMes().getValor().equals(configConsulta.getPeriodoPadrao().getMes().getValor())){
                    return true;
                }
            }
        }
        return false;
    }

    private void setValoresApresentados(@NonNull List<PeriodoConsultaDTO> listaMesesAnos){
        valoresApresentados = new ArrayList<>();
        for (int contMesAno = 0; contMesAno < listaMesesAnos.size(); contMesAno++) {
            valoresApresentados.add(getTextoDataSelecionada(contMesAno));
        }
    }

//    private void onMesesAnosDisponiveisError(VolleyError error) {
//        if (getActivity() != null) {
//            AlertDialogUtils.dismiss();
//            String msgError = MensagensNetwork.setMensagem(error, getActivity());
//            page = null;
//            hasMoreItems = false;
//            updateResultDate();
//        }
//        if (error != null && error.networkResponse != null && error.networkResponse.statusCode == 401) {
//            isOnCreate = true;
//        }
//        RedirectNetwork.checkRedirect(error, getActivity());
//    }

    private void updateResultDate() {
        if (page != null) {
            dateResultTxt.setText(getTextoDataSelecionada(numeroDataSelecionada));
        } else {
            dateResultTxt.setText("Nenhuma aposta disponível.");
        }
    }

    private String getTextoDataSelecionada(int indice){
        return mesesAnosDisponiveis.get(indice).getMes().getDescricao();
    }

    private String formatMesAno(int mes, int ano) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, mes - 1);
        cal.set(Calendar.YEAR, ano);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        String dateStr = new SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                .format(cal.getTime().getTime());
        return AppUtils.capitalizeFirstChar(dateStr.replace(" "," de "));
    }

    private void onApostasConfirmadasError(VolleyError error) {
        isLoading = false;
        if (getActivity() != null) {
            AlertDialogUtils.dismiss();
            apostasLoading.setVisibility(View.GONE);
            RedirectNetwork.checkRedirect(error, getActivity());
        }
    }

    private void onApostasConfirmadasError500(VolleyError error) {
        isLoading = false;
        if (getActivity() != null) {
            AlertDialogUtils.dismiss();
            apostasLoading.setVisibility(View.GONE);

            if (chamarApostaNovamente && error != null && error.networkResponse != null && error.networkResponse.statusCode == 500) {
                chamarApostaNovamente = false;
                numeroDataSelecionada = 0;
                onMesesAnosDisponiveisResult();
            }else {
                RedirectNetwork.checkRedirect(error, getActivity());
            }
        }
    }

    public void setFiltroAdapter(FiltroApostasAdapter adapter){
        this.filtroAdapter = adapter;
    }

    public void conferirResultado(int position, ApostaDTO aposta, ApostaConfirmadaHolder holder) {
        holder.showProgressBarSituacao(true);
        ConferirResultadoHelper.conferirResultadoHandler = this;
        ConferirResultadoHelper.conferirResultadoWithoutLoading(position, holder, aposta, getActivity());
    }

    @Override
    public void handle(DTOEnumLong situacao) {

    }

    @Override
    public void handle(int position, ApostaConfirmadaHolder holder, DTOEnumLong situacao) {
        if(apostasConfirmadasRecyclerView != null && apostasConfirmadasRecyclerView.getAdapter() != null){
            ((ApostasConfirmadasAdapter)apostasConfirmadasRecyclerView.getAdapter()).atualizaSituacaoAposta(position, situacao);
            holder.handle(position, holder, situacao);
        }
    }

    @Override
    public void handleError(ApostaConfirmadaHolder holder, VolleyError error) {
        holder.handleError(holder, error);
    }

    private void ajusteCombo() {

    }
}