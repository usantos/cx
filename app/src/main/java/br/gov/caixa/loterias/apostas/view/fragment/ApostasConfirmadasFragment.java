package br.gov.caixa.loterias.apostas.view.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.ApostaConfirmadaDetalhesActivity;
import br.gov.caixa.loterias.apostas.controllers.MinhasApostasActivity;
import br.gov.caixa.loterias.apostas.model.bean.ApostaPageRequest;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostas.model.bo.ApostaMicroServicoBO;
import br.gov.caixa.loterias.apostas.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComprovanteApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComprovanteApostaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumLong;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesHistoricoPremioDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MesAnoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MesAnoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametrosSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOApostaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOHistoricoApostaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacaoResultadoBilheteEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.ConfiguracaoAppSingleton;
import br.gov.caixa.loterias.apostas.model.model.ApostaConfirmadaModel;
import br.gov.caixa.loterias.apostas.model.model.ParametrosSimulacaoModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.AppUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.TestVisao;
import br.gov.caixa.loterias.apostas.utils.helper.AdicionarApostaCarrinhoHelper;
import br.gov.caixa.loterias.apostas.utils.helper.ConferirResultadoHandler;
import br.gov.caixa.loterias.apostas.utils.helper.ConferirResultadoHelper;
import br.gov.caixa.loterias.apostas.view.adapter.baseadapter.FiltroApostasAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ApostasConfirmadasAdapter;
import br.gov.caixa.loterias.apostas.view.holder.ApostaConfirmadaHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnApostaConfirmadaListener;

/**
 * Created by pmotta on 16/03/2018.
 * Class ApostasConfirmadasFragment
 */

@EFragment(R.layout.fragment_apostas_confirmadas)
public class ApostasConfirmadasFragment extends Fragment implements MinhasApostasActivity.OnScrollReachBottomListener, ConferirResultadoHandler {

    private static final int PAGE_SIZE = 30;

    private ApostaBO apostaBO;

    @ViewById
    protected TextView dateResultTxt;

    @ViewById
    protected RecyclerView apostasConfirmadasRecyclerView;

    @ViewById
    protected View apostasLoading;

    @FragmentArg
    protected boolean isHistorico;

    protected FiltroApostasAdapter filtroAdapter;
    private Dialog dialogData;
    private NumberPicker npData;

    private List<MesAnoDTO> mesesAnosDisponiveis;
    private List<String> valoresApresentados;
    private boolean hasMoreItems;
    private boolean isLoading;
    private boolean firstTime = true;
    private boolean reloadingEntireList = false;
    private int numeroDataSelecionada = 1;
    private boolean chamarApostaNovamente = true;
    private MesAnoDTOResponse resultMesAno;
    private Long rowCount = Long.MAX_VALUE;

    private ApostaPageRequest page;

    private ApostaConfirmadaModel model;

    private static ApostasConfirmadasFragment instance = null;
    public static ApostasConfirmadasFragment getInstance(){
        return instance;
    }

    private boolean isOnCreate;
    private int ordenacaoDefault = 3;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = new ApostaConfirmadaModel(getActivity());
        isOnCreate = true;
        apostaBO = ApostaBO.getInstance();
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
                    instance = this;

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
        return PAGE_SIZE;
    }

    @Click
    protected void dateSelectorBtn() {
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
        MesAnoDTO mesAnoSelecionado =  mesesAnosDisponiveis.get(numeroDataSelecionada);
        if(page == null){
            page = new ApostaPageRequest(getPageSize());
        }
        page.setAno(mesAnoSelecionado.getAno().intValue());
        page.setMes(mesAnoSelecionado.getMes().getValor().intValue());

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
            apostaBO.getApostasHistorico(page, new RequestListener<ResultadoPesquisaPaginadaDTOHistoricoApostaDTOResponse>() {
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
            if (ConfiguracaoAppSingleton.getInstance() != null
                    && ConfiguracaoAppSingleton.getInstance().isMicroServico() != null
                    && ConfiguracaoAppSingleton.getInstance().isMicroServico()){
                requestApostasMicroServico(ConfiguracaoAppSingleton.getInstance().getUrlBaseBuscaApostas());
            }else {
                requesteApostasSilce();
            }
        }
    }

    private void requestApostasMicroServico(String urlBaseBuscaApostas) {
        ApostaMicroServicoBO.getInstance().getApostasConfirmadasMicroServico(urlBaseBuscaApostas, page, new RequestListener<ResultadoPesquisaPaginadaDTOApostaDTOResponse>() {
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
        apostaBO.getApostasConfirmadasSilce(page, new RequestListener<ResultadoPesquisaPaginadaDTOApostaDTOResponse>() {
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
        page.setModalidade(0);
        page.setSituacao(1);
        page.setOrdenarPor(ordenacaoDefault);
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
        apostasLoading.setVisibility(View.GONE);
        //SessaoUsuario.getInstance().getParametrosSimulacao().setParametros(null);

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
                    ApostaDTO apostaDTO = apostasList.get(position);
                    AdicionarApostaCarrinhoHelper.incluirApostaCarrinhoMinhasApostas(getActivity(), apostaDTO);
                }
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
        } else
                if (holder.precisaConferirSituacao()){
            conferirResultado(position, apostaDTO, holder);
        }else {
            if (Objects.equals(apostaDTO.getSituacao().getValor(), SituacaoResultadoBilheteEnum.PAGA.getValor())) {

                apostaBO.getApostaComprovantePremio(apostaDTO.getId(), new RequestListener<DetalhesPremioDTOResponse>() {
                    @Override
                    public void onResponse(DetalhesPremioDTOResponse result) {
                        AlertDialogUtils.dismiss();
                        Intent intent = IntentUtil.getIntentOrigemDestino(getActivity(), ApostaConfirmadaDetalhesActivity.class,
                                ApostaConfirmadaDetalhesActivity.ARG_DETALHES_PREMIO, result.getPayload());
                        startActivity(intent);
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        onApostasConfirmadasError(error);
                    }
                });
            } else {
                apostaBO.getApostaDetalheComprovante(apostaDTO.getId(), new RequestListener<ComprovanteApostaDTOResponse>() {
                    @Override
                    public void onResponse(ComprovanteApostaDTOResponse result) {
                        AlertDialogUtils.dismiss();
                        ComprovanteApostaDTO comprovante = result.getPayload();
                        if (ConfiguracaoAppSingleton.getInstance().isMicroServico()){
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
        Intent intent = IntentUtil.getIntentOrigemDestino(getActivity(), ApostaConfirmadaDetalhesActivity.class,
                ApostaConfirmadaDetalhesActivity.ARG_COMPROVANTE, comprovante);
        startActivity(intent);
    }

    private void handleOnItemClickHistorico(ApostaDTO apostaDTO) {
        if (Objects.equals(apostaDTO.getSituacao().getValor(), SituacaoResultadoBilheteEnum.PAGA_HISTORICO.getValor())) {
            apostaBO.getApostaHistoricoComprovante(apostaDTO.getId(), new RequestListener<DetalhesHistoricoPremioDTOResponse>() {
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
        AlertDialogUtils.show(getActivity());

        RequestListener listener = new RequestListener<MesAnoDTOResponse>() {
            @Override
            public void onResponse(MesAnoDTOResponse result) {
                resultMesAno = result;
                onMesesAnosDisponiveisResult(resultMesAno);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                onMesesAnosDisponiveisError(error);
            }
        };

        if (isHistorico) {
            apostaBO.getDatasApostasHistorico(listener);
        } else {
            apostaBO.getDatasApostasConfirmadas(listener);
        }
    }

    private void onMesesAnosDisponiveisResult(MesAnoDTOResponse result) {
        if (getActivity() == null) {
            return;
        }

        AlertDialogUtils.dismiss();
        mesesAnosDisponiveis = new ArrayList<>();
        if(!isHistorico){
            mesesAnosDisponiveis.addAll(model.getMesesPadrao());
        }
        mesesAnosDisponiveis.addAll(result.getPayload());

        if (mesesAnosDisponiveis != null && !mesesAnosDisponiveis.isEmpty()) {
            MesAnoDTO mesAno = mesesAnosDisponiveis.get(numeroDataSelecionada);
            int mes = mesAno.getMes().getValor().intValue();
            int ano = mesAno.getAno().intValue();
            if(page == null){
                page = new ApostaPageRequest(getPageSize());
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
        if (result.getRedirect() != null) {
            RedirectNetwork.checkRedirectSucesso(result.getRedirect(), getActivity());
        }
    }

    private void setValoresApresentados(List<MesAnoDTO> listaMesesAnos){
        valoresApresentados = new ArrayList<>();
        for (int contMesAno = 0; contMesAno < listaMesesAnos.size(); contMesAno++) {
            valoresApresentados.add(getTextoDataSelecionada(contMesAno));
        }
    }

    private void onMesesAnosDisponiveisError(VolleyError error) {
        if (getActivity() != null) {
            AlertDialogUtils.dismiss();
            String msgError = MensagensNetwork.setMensagem(error, getActivity());
            page = null;
            hasMoreItems = false;
            updateResultDate();
        }
        if (error != null && error.networkResponse != null && error.networkResponse.statusCode == 401) {
            isOnCreate = true;
        }
        RedirectNetwork.checkRedirect(error, getActivity());

    }

    private void updateResultDate() {
        if (page != null) {
            dateResultTxt.setText(getTextoDataSelecionada(numeroDataSelecionada));
        } else {
            //TODO: validar mensagem de erro.
            dateResultTxt.setText("Nenhuma aposta disponível.");
        }
    }

    private String getTextoDataSelecionada(int indice){
        MesAnoDTO mesAnoSelecionado = mesesAnosDisponiveis.get(indice);
        if(indice > model.getQtdMesAnoPadrao() - 1 || isHistorico){
            return formatMesAno(mesAnoSelecionado.getMes().getValor().intValue(),mesAnoSelecionado.getAno().intValue());
        } else {
            return mesAnoSelecionado.getMes().getDescricao();
        }
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
                numeroDataSelecionada = 2;
                onMesesAnosDisponiveisResult(resultMesAno);
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
}