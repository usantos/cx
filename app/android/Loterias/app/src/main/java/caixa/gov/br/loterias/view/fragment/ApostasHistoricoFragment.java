package caixa.gov.br.loterias.view.fragment;

import android.app.DatePickerDialog;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bean.ApostaPageRequest;
import caixa.gov.br.loterias.model.bo.ApostaBO;
import caixa.gov.br.loterias.model.bo.MensagensNetwork;
import caixa.gov.br.loterias.model.bo.RequestListener;
import caixa.gov.br.loterias.model.bo.silce.dto.HistoricoApostaDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.MesAnoDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.MesAnoDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOHistoricoApostaDTOResponse;
import caixa.gov.br.loterias.utils.AppUtils;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.ApostasConfirmadasActivity;
import caixa.gov.br.loterias.view.adapter.ApostasHistoricoAdapter;
import caixa.gov.br.loterias.view.custom.LoadingViewLoterias;
import caixa.gov.br.loterias.view.custom.MonthYearPickerDialog;
import caixa.gov.br.loterias.view.custom.MonthYearPickerDialog_;

/**
 * Created by pmotta on 16/03/2018.
 */

@EFragment(R.layout.fragment_apostas_confirmadas)
public class ApostasHistoricoFragment extends Fragment implements ApostasConfirmadasActivity.OnScrollReachBottomListener {
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

    private List<MesAnoDTO> mesesAnosDisponiveis;
    private boolean hasMoreItems;
    private boolean isLoading;
    private ApostaPageRequest page;

    private AlertDialog alertDialog;

    @AfterViews
    protected void init(){
        if(getActivity() != null){
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            apostasConfirmadasRecyclerView.setLayoutManager(layoutManager);
            DividerItemDecoration divider = new DividerItemDecoration(getActivity(), layoutManager.getOrientation());
            Drawable dividerDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.recyclerview_divider);
            if(dividerDrawable != null){
                divider.setDrawable(dividerDrawable);
            }
            apostasConfirmadasRecyclerView.addItemDecoration(divider);
            apostasConfirmadasRecyclerView.setNestedScrollingEnabled(false);
            requestMesesAnosDisponiveis();
        }
    }

    private void requestMesesAnosDisponiveis() {
        alertDialog = LoadingViewLoterias.show(getActivity());
        apostaBO.getDatasApostasHistorico(new RequestListener<MesAnoDTOResponse>() {
            @Override
            public void onResponse(MesAnoDTOResponse result) {
                if(getActivity() != null){
                    alertDialog.dismiss();
                    onMesesAnosDisponiveisResult(result);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                if(getActivity() != null){
                    alertDialog.dismiss();
                    String msgError = MensagensNetwork.setMensagem(error, getActivity());
                    ViewUtils.alert(getActivity(), msgError);
                    page = null;
                    hasMoreItems = false;
                    updateResultDate();
                }
            }
        });
    }

    private void updateResultDate(){
        if(page != null){
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.MONTH, (page.getMes() - 1));
            cal.set(Calendar.YEAR, page.getAno());
            cal.set(Calendar.DAY_OF_MONTH, 1);

            String dateStr = new SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                    .format(cal.getTime().getTime());
            dateResultTxt.setText(AppUtils.capitalizeFirstChar(dateStr));
        }else{
            //TODO: validar mensagem de erro.
            dateResultTxt.setText("Nenhuma aposta disponível.");
        }
    }

    @Click
    protected void dateSelectorBtn(){
        if(mesesAnosDisponiveis != null){
            MonthYearPickerDialog pd = MonthYearPickerDialog_.builder()
                    .mesesAnos(mesesAnosDisponiveis)
                    .mesDefault(page != null? page.getMes() : null)
                    .anoDefault(page != null? page.getAno() : null)
                    .build();

            pd.show(getActivity().getSupportFragmentManager(), MONTH_YEAR_DIALOG);
            pd.setListener(new DatePickerDialog.OnDateSetListener(){
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    page = new ApostaPageRequest(PAGE_SIZE);
                    page.setAno(year);
                    page.setMes(month + 1);
                    page.setOffset(0);

                    hasMoreItems = true;
                    apostasConfirmadasRecyclerView.setAdapter(null);

                    updateResultDate();
                    requestApostasConfirmadas();
                }
            });
        }
    }

    private void onMesesAnosDisponiveisResult(MesAnoDTOResponse result) {
        mesesAnosDisponiveis = result.getPayload();

        if(mesesAnosDisponiveis != null && !mesesAnosDisponiveis.isEmpty()){
            MesAnoDTO mesAno = mesesAnosDisponiveis.get(0);
            int mes = mesAno.getMes().getValor().intValue();
            int ano = mesAno.getAno().intValue();

            page = new ApostaPageRequest(PAGE_SIZE);
            page.setAno(ano);
            page.setMes(mes);
            page.setOffset(0);

            hasMoreItems = true;
            requestApostasConfirmadas();
        }else{
            page = null;
            hasMoreItems = false;
        }

        updateResultDate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        alertDialog.dismiss();
    }

    private void onApostasConfirmadasResult(ResultadoPesquisaPaginadaDTOHistoricoApostaDTOResponse result){
        List<HistoricoApostaDTO> apostasList = result.getPayload().getLista();
        if(apostasList != null){
            if(PAGE_SIZE > apostasList.size()){
                hasMoreItems = false;
            }
            page.setOffset(page.getOffset() + apostasList.size());

            ApostasHistoricoAdapter adapter = (ApostasHistoricoAdapter)apostasConfirmadasRecyclerView.getAdapter();
            if(adapter == null){
                adapter = new ApostasHistoricoAdapter(getActivity(), apostasList);
                apostasConfirmadasRecyclerView.setAdapter(adapter);
            }else{
                adapter.addApostasList(apostasList);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void requestApostasConfirmadas() {
        if(page == null){
            return;
        }

        isLoading = true;
        apostasLoading.setVisibility(View.VISIBLE);
        apostaBO.getApostasHistorico(page, new RequestListener<ResultadoPesquisaPaginadaDTOHistoricoApostaDTOResponse>() {
            @Override
            public void onResponse(ResultadoPesquisaPaginadaDTOHistoricoApostaDTOResponse result) {
                isLoading = false;
                if(getActivity() != null) {
                    alertDialog.dismiss();
                    apostasLoading.setVisibility(View.GONE);

                    onApostasConfirmadasResult(result);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                isLoading = false;
                if(getActivity() != null){
                    alertDialog.dismiss();
                    apostasLoading.setVisibility(View.GONE);

                    String msgError = MensagensNetwork.setMensagem(error, getActivity());
                    ViewUtils.alert(getActivity(), msgError);
                }
            }
        });
    }

    @Override
    public void onScrollReachBottom() {
        if(!isLoading && hasMoreItems){
            requestApostasConfirmadas();
        }
    }
}
