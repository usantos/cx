package br.gov.caixa.loterias.apostas.view.fragment;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.ApostaConfirmadaDetalhesActivity_;
import br.gov.caixa.loterias.apostas.controllers.MinhasApostasActivity;
import br.gov.caixa.loterias.apostas.model.bean.ApostaPageRequest;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostas.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComprovanteApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComprovanteApostaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesHistoricoPremioDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MesAnoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MesAnoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MesDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOApostaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOHistoricoApostaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacaoResultadoBilheteEnum;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.AppUtils;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.helper.AdicionarApostaCarrinhoHelper;
import br.gov.caixa.loterias.apostas.view.adapter.ApostasConfirmadasAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.FiltroApostasAdapter;

/**
 * Created by pmotta on 16/03/2018.
 * Class ApostasConfirmadasFragment
 */

@EFragment(R.layout.fragment_apostas_confirmadas)
public class ApostasConfirmadasFragment extends Fragment implements MinhasApostasActivity.OnScrollReachBottomListener {

    private static final int PAGE_SIZE = 30;

    @Bean
    protected ApostaBO apostaBO;

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
    private int numeroDataSelecionada = 0;

    private ApostaPageRequest page;

    private static ApostasConfirmadasFragment instance = null;
    public static ApostasConfirmadasFragment getInstance(){
        return instance;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(DialogUtils.isShow()){
            DialogUtils.dismiss();
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
                page = new ApostaPageRequest(PAGE_SIZE);
                page.setOffset(0);
            } else {
                page.setSize(PAGE_SIZE);
                page.setOffset(0);
                hasMoreItems = true;
            }
            requestApostasConfirmadas();
        }
    }

    @Click
    protected void dateSelectorBtn() {
        dialogData.show();
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
        npData.setDisplayedValues(valoresApresentados.toArray(new String[valoresApresentados.size()]));
        npData.setOnValueChangedListener(new ListenerData());
        npData.setWrapSelectorWheel(false);

    }

    private void filtrarData(){
        MesAnoDTO mesAnoSelecionado =  mesesAnosDisponiveis.get(numeroDataSelecionada);
        if(page == null){
            page = new ApostaPageRequest(PAGE_SIZE);
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
        DialogUtils.dismiss();
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
            apostaBO.getApostasConfirmadas(page, new RequestListener<ResultadoPesquisaPaginadaDTOApostaDTOResponse>() {
                @Override
                public void onResponse(ResultadoPesquisaPaginadaDTOApostaDTOResponse result) {
                    onApostasConfirmadasResult(result.getPayload().getLista());
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    onApostasConfirmadasError(error);
                }
            });
        }
    }

    public void filtraDados(){
        page.setModalidade(0);
        page.setSituacao(0);
        page.setOrdenarPor(0);
        if(!isHistorico){
            if(filtroAdapter != null){
                if(filtroAdapter.getNpSituacao() != null) {
                    page.setSituacao(filtroAdapter.getNpSituacao().getValue());
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

        DialogUtils.dismiss();
        apostasLoading.setVisibility(View.GONE);

        if (apostasList != null) {
            if (PAGE_SIZE > apostasList.size()) {
                hasMoreItems = false;
            }

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
                adapter = new ApostasConfirmadasAdapter(((MinhasApostasActivity)getActivity()), getActivity(), apostasList, isHistorico);
                adapter.setOnApostasConfirmadasListener(new ApostasConfirmadasAdapter.OnApostasConfirmadasListener() {
                    @Override
                    public void onEmptyCellClick() {
                        getActivity().onBackPressed();
                    }

                    @Override
                    public void onReload(ApostaDTO apostaDTO) {
                        AdicionarApostaCarrinhoHelper.incluirApostaCarrinhoMinhasApostas(getActivity(), apostaDTO);
                    }

                    @Override
                    public void onItemClick(final ApostaDTO apostaDTO) {
                        DialogUtils.show(getActivity());

                        if (isHistorico) {
                            if (Objects.equals(apostaDTO.getSituacao().getValor(), SituacaoResultadoBilheteEnum.PAGA_HISTORICO.getValor())) {
                                apostaBO.getApostaHistoricoComprovante(apostaDTO.getId(), new RequestListener<DetalhesHistoricoPremioDTOResponse>() {
                                    @Override
                                    public void onResponse(DetalhesHistoricoPremioDTOResponse result) {
                                        ApostaConfirmadaDetalhesActivity_.intent(getActivity()).detalhesHistoricoPremioDTO(result.getPayload()).start();
                                    }

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        onApostasConfirmadasError(error);
                                    }
                                });
                            } else {
                                String json = new Gson().toJson(apostaDTO);
                                IdentificaoDeUmaApostaDas8Modalidades apostaDas8Modalidades = new Gson().fromJson(json, IdentificaoDeUmaApostaDas8Modalidades.class);
                                ComprovanteApostaDTO comprovanteApostaDTO = new ComprovanteApostaDTO();
                                comprovanteApostaDTO.setAposta(apostaDas8Modalidades);
                                DialogUtils.dismiss();
                                ApostaConfirmadaDetalhesActivity_.intent(getActivity()).comprovanteApostaDTO(comprovanteApostaDTO).start();
                            }
                        } else {
                            if (Objects.equals(apostaDTO.getSituacao().getValor(), SituacaoResultadoBilheteEnum.PAGA.getValor())) {

                                apostaBO.getApostaComprovantePremio(apostaDTO.getId(), new RequestListener<DetalhesPremioDTOResponse>() {
                                    @Override
                                    public void onResponse(DetalhesPremioDTOResponse result) {
                                        DialogUtils.dismiss();
                                        ApostaConfirmadaDetalhesActivity_.intent(getActivity()).detalhesPremioDTO(result.getPayload()).start();
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
                                        DialogUtils.dismiss();
                                        ApostaConfirmadaDetalhesActivity_.intent(getActivity()).comprovanteApostaDTO(result.getPayload()).start();
                                    }

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        onApostasConfirmadasError(error);
                                    }
                                });
                            }
                        }
                    }
                });
                apostasConfirmadasRecyclerView.setAdapter(adapter);
            } else {
                adapter.addApostasList(apostasList);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void requestMesesAnosDisponiveis() {
        DialogUtils.show(getActivity());

        RequestListener listener = new RequestListener<MesAnoDTOResponse>() {
            @Override
            public void onResponse(MesAnoDTOResponse result) {
                onMesesAnosDisponiveisResult(result);
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

        DialogUtils.dismiss();
        mesesAnosDisponiveis = new ArrayList<>();
        if(!isHistorico){
            mesesAnosDisponiveis.addAll(getMesesPadrao());
        }
        mesesAnosDisponiveis.addAll(result.getPayload());

        if (mesesAnosDisponiveis != null && !mesesAnosDisponiveis.isEmpty()) {
            MesAnoDTO mesAno = mesesAnosDisponiveis.get(0);
            int mes = mesAno.getMes().getValor().intValue();
            int ano = mesAno.getAno().intValue();
            if(page == null){
                page = new ApostaPageRequest(PAGE_SIZE);
            }
            page.setAno(ano);
            page.setMes(mes);
            page.setOffset(0);
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

    private List<MesAnoDTO> getMesesPadrao(){
        List<MesAnoDTO> mesesPadrao = new ArrayList<>();
        mesesPadrao.add(setMesAnoPadroes(90l));
        mesesPadrao.add(setMesAnoPadroes(30l));
        return mesesPadrao;
    }

    private MesAnoDTO setMesAnoPadroes(Long dias){
        MesAnoDTO mesAno = new MesAnoDTO();
        mesAno.setAno(0l);
        mesAno.setMes(setMesPadrao(dias));
        return mesAno;
    }

    private MesDTO setMesPadrao(Long dias){
        MesDTO  mes = new MesDTO();
        mes.setValor(dias);
        mes.setDescricao(getString(R.string.ultimos_qtd_dias).replace("{quantidade}",dias.toString()));
        return mes;
    }


    private void onMesesAnosDisponiveisError(VolleyError error) {
        if (getActivity() != null) {
            DialogUtils.dismiss();
            String msgError = MensagensNetwork.setMensagem(error, getActivity());
            page = null;
            hasMoreItems = false;
            updateResultDate();
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
        if(indice > 1 || isHistorico){
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
            DialogUtils.dismiss();
            apostasLoading.setVisibility(View.GONE);
            RedirectNetwork.checkRedirect(error, getActivity());
        }
    }

    public void setFiltroAdapter(FiltroApostasAdapter adapter){
        this.filtroAdapter = adapter;
    }
}