package br.gov.caixa.loterias.apostar.interno.view.fragment;

import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bean.ApostaPageRequest;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ComprovanteApostaDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ComprovanteApostaDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.DetalhesHistoricoPremioDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.DetalhesHistoricoPremioDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.DetalhesPremioDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.DetalhesPremioDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.MesAnoDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.MesAnoDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ResultadoConcursoDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOApostaDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOHistoricoApostaDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.SituacaoResultadoBilheteEnum;
import br.gov.caixa.loterias.apostar.interno.utils.AppUtils;
import br.gov.caixa.loterias.apostar.interno.utils.DialogUtils;
import br.gov.caixa.loterias.apostar.interno.utils.FirebaseManager;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.utils.helper.AdicionarApostaCarrinhoHelper;
import br.gov.caixa.loterias.apostar.interno.view.ApostaConfirmadaDetalhesActivity_;
import br.gov.caixa.loterias.apostar.interno.view.ApostasConfirmadasActivity;
import br.gov.caixa.loterias.apostar.interno.view.adapter.ApostasConfirmadasAdapter;
import br.gov.caixa.loterias.apostar.interno.view.custom.MonthYearPickerDialog;
import br.gov.caixa.loterias.apostar.interno.view.custom.MonthYearPickerDialog_;

/**
 * Created by pmotta on 16/03/2018.
 * Class ApostasConfirmadasFragment
 */

@EFragment(R.layout.fragment_apostas_confirmadas)
public class ApostasConfirmadasFragment extends Fragment implements ApostasConfirmadasActivity.OnScrollReachBottomListener {

    private static final String MONTH_YEAR_DIALOG = "MonthYearPickerDialog";
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

    private List<MesAnoDTO> mesesAnosDisponiveis;
    private boolean hasMoreItems;
    private boolean isLoading;
    private boolean firstTime = true;
    private boolean reloadingEntireList = false;

    private ApostaPageRequest page;

    private static ApostasConfirmadasFragment instance = null;
    public static ApostasConfirmadasFragment getInstance(){
        return instance;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (firstTime) {
            FirebaseManager.registraEvento(getContext() ,"ENTROU_GERENCIAR_APOSTAS");

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
                page.setOffset(0);
            }
            requestApostasConfirmadas();
        }
    }

    @Click
    protected void dateSelectorBtn() {
        if (mesesAnosDisponiveis != null) {
            MonthYearPickerDialog pd = MonthYearPickerDialog_.builder()
                    .mesesAnos(mesesAnosDisponiveis)
                    .mesDefault(page != null ? page.getMes() : null)
                    .anoDefault(page != null ? page.getAno() : null)
                    .build();

            pd.show(getActivity().getSupportFragmentManager(), MONTH_YEAR_DIALOG);
            pd.setListener((datePicker, year, month, day) -> {
                page = new ApostaPageRequest(PAGE_SIZE);
                page.setAno(year);
                page.setMes(month + 1);
                page.setOffset(0);

                hasMoreItems = true;
//                apostasConfirmadasRecyclerView.setAdapter(null);

                updateResultDate();
                requestApostasConfirmadas();
            });
        }
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
                adapter = new ApostasConfirmadasAdapter(((ApostasConfirmadasActivity)getActivity()), getActivity(), apostasList, isHistorico);
                adapter.setOnApostasConfirmadasListener(new ApostasConfirmadasAdapter.OnApostasConfirmadasListener() {
                    @Override
                    public void onEmptyCellClick() {
                        getActivity().onBackPressed();
                    }

                    @Override
                    public void onReload(ApostaDTO apostaDTO) {
                        AdicionarApostaCarrinhoHelper.incluirApostaCarrinho(getActivity(), apostaDTO);
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
        mesesAnosDisponiveis = result.getPayload();

        if (mesesAnosDisponiveis != null && !mesesAnosDisponiveis.isEmpty()) {
            MesAnoDTO mesAno = mesesAnosDisponiveis.get(0);
            int mes = mesAno.getMes().getValor().intValue();
            int ano = mesAno.getAno().intValue();

            page = new ApostaPageRequest(PAGE_SIZE);
            page.setAno(ano);
            page.setMes(mes);
            page.setOffset(0);

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
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.MONTH, (page.getMes() - 1));
            cal.set(Calendar.YEAR, page.getAno());
            cal.set(Calendar.DAY_OF_MONTH, 1);

            String dateStr = new SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                    .format(cal.getTime().getTime());
            dateResultTxt.setText(AppUtils.capitalizeFirstChar(dateStr));
        } else {
            //TODO: validar mensagem de erro.
            dateResultTxt.setText("Nenhuma aposta disponível.");
        }
    }


    private void getResultadoModalidadeConcurso
            (ApostaDTO apostaDTO, final Object object) {
        apostaBO.getResultadoModalidadeConcurso(ApostaDTO.lowerCaseFromString(apostaDTO.getModalidade()), apostaDTO.getConcursoAlvo(),
                new RequestListener<ResultadoConcursoDTOResponse>() {
                    @Override
                    public void onResponse(ResultadoConcursoDTOResponse result) {
                        try {
                            DialogUtils.dismiss();
                            if (object instanceof DetalhesHistoricoPremioDTO) {
                                ApostaConfirmadaDetalhesActivity_.intent(getActivity()).detalhesHistoricoPremioDTO((DetalhesHistoricoPremioDTO) object)
                                        .resultadoConcursoDTO(result.getPayload()).start();
                            } else if (object instanceof DetalhesPremioDTO) {
                                ApostaConfirmadaDetalhesActivity_.intent(getActivity()).detalhesPremioDTO((DetalhesPremioDTO) object)
                                        .resultadoConcursoDTO(result.getPayload()).start();
                            }
                        }catch (Exception e){
                            ViewUtils.alert(getContext(), getResources().getString(R.string.erro_500));
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        onApostasConfirmadasError(error);
                    }
                });
    }

    private void onApostasConfirmadasError(VolleyError error) {
        isLoading = false;
        if (getActivity() != null) {
            DialogUtils.dismiss();
            apostasLoading.setVisibility(View.GONE);
            RedirectNetwork.checkRedirect(error, getActivity());
        }
    }
}
