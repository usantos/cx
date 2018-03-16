package caixa.gov.br.loterias.view.fragment;

import android.app.DatePickerDialog;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import caixa.gov.br.loterias.model.bo.ApostaBO;
import caixa.gov.br.loterias.model.bo.MensagensNetwork;
import caixa.gov.br.loterias.model.bo.silce.RequestListener;
import caixa.gov.br.loterias.model.bo.silce.dto.ApostaDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.MesAnoDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.MesAnoDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOApostaDTOResponse;
import caixa.gov.br.loterias.utils.AppUtils;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.adapter.ApostasConfirmadasAdapter;
import caixa.gov.br.loterias.view.custom.LoadingViewLoterias;
import caixa.gov.br.loterias.view.custom.MonthYearPickerDialog;
import caixa.gov.br.loterias.view.custom.MonthYearPickerDialog_;

/**
 * Created by pmotta on 16/03/2018.
 */

@EFragment(R.layout.fragment_apostas_confirmadas)
public class ApostasConfirmadasFragment extends Fragment {

    private static final String MONTH_YEAR_DIALOG = "MonthYearPickerDialog";

    @Bean
    protected ApostaBO apostaBO;

    @ViewById
    protected TextView dateResultTxt;

    @ViewById
    protected RecyclerView apostasConfirmadasRecyclerView;

    private Calendar currentCalendar;
    private List<MesAnoDTO> mesesAnosDisponiveis;

    private AlertDialog alertDialog;

    @AfterViews
    protected void init(){
        requestMesesAnosDisponiveis();
    }

    private void requestMesesAnosDisponiveis() {
        alertDialog = LoadingViewLoterias.show(getActivity());
        apostaBO.getDatasApostasConfirmadas(new RequestListener<MesAnoDTOResponse>() {
            @Override
            public void onSucess(MesAnoDTOResponse result) {
                onMesesAnosDisponiveisResult(result);
            }

            @Override
            public void onError(VolleyError error) {
                alertDialog.dismiss();
                String msgError = MensagensNetwork.setMensagem(error, getActivity());
                ViewUtils.alert(getActivity(), msgError);

                currentCalendar = null;
                updateResultDate();
            }
        });
    }

    private void updateResultDate(){
        if(currentCalendar != null){
            String dateStr = new SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                    .format(currentCalendar.getTime().getTime());
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
                    .build();

            pd.show(getActivity().getSupportFragmentManager(), MONTH_YEAR_DIALOG);
            pd.setListener(new DatePickerDialog.OnDateSetListener(){
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    currentCalendar.set(year, month, 1);
                    updateResultDate();

                    alertDialog = LoadingViewLoterias.show(getActivity());
                    requestApostasConfirmadas(month, year);
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

            currentCalendar = Calendar.getInstance();
            currentCalendar.set(Calendar.MONTH, mes - 1);
            currentCalendar.set(Calendar.YEAR, ano);

            requestApostasConfirmadas(mes, ano);
        }else{
            currentCalendar = null;
        }

        updateResultDate();
    }

    private void requestApostasConfirmadas(int mes, int ano) {
        apostaBO.getApostasConfirmadas(mes, ano, new RequestListener<ResultadoPesquisaPaginadaDTOApostaDTOResponse>() {
            @Override
            public void onSucess(ResultadoPesquisaPaginadaDTOApostaDTOResponse result) {
                alertDialog.dismiss();

                List<ApostaDTO> apostasList = result.getPayload().getLista();
                if(apostasList != null){
                    apostasConfirmadasRecyclerView.setAdapter(new ApostasConfirmadasAdapter(getActivity(), apostasList));

                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    apostasConfirmadasRecyclerView.setLayoutManager(layoutManager);
                    DividerItemDecoration divider = new DividerItemDecoration(getActivity(), layoutManager.getOrientation());
                    divider.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.drawer_linha_divisao_cinza_claro));
                    apostasConfirmadasRecyclerView.addItemDecoration(divider);
                    apostasConfirmadasRecyclerView.setNestedScrollingEnabled(false);
                }
            }

            @Override
            public void onError(VolleyError error) {
                alertDialog.dismiss();
                String msgError = MensagensNetwork.setMensagem(error, getActivity());
                ViewUtils.alert(getActivity(), msgError);
            }
        });
    }

}
