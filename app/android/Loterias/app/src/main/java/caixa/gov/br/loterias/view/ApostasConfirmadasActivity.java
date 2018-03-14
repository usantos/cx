package caixa.gov.br.loterias.view;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.utils.AppUtils;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.custom.MonthYearPickerDialog;

/**
 * Created by pmotta on 13/03/2018.
 */

@OptionsMenu(R.menu.menu_apostas_confirmadas)
@EActivity(R.layout.activity_apostas_confirmadas)
public class ApostasConfirmadasActivity extends AppCompatActivity {

    private static final String MONTH_YEAR_DIALOG = "MonthYearPickerDialog";

    @ViewById(R.id.toolbar)
    protected Toolbar toolbar;

    @ViewById(R.id.verResultadosBtn)
    protected RadioButton verResultadosBtn;

    @ViewById(R.id.historicoApostasBtn)
    protected RadioButton historicoApostasBtn;

    @ViewById
    protected TextView dateResultTxt;

    private Calendar currentCalendar;

    @AfterViews
    protected void init(){
        setupToolbar();
        //TODO: retornar data mais atual do SILCE
        currentCalendar = Calendar.getInstance();
        updateResultDate();
    }

    @OptionsItem(R.id.action_duvida)
    protected void onMenuDuvida(){
        Intent intent = new Intent(getBaseContext(), TermosUsoActivity.class);
        startActivity(intent);
    }

    @Click
    protected void dateSelectorBtn(){
        //TODO: retornar datas do SILCE
        int [] yearRange = new int[]{2017, 2018, 2019, 2020};
        MonthYearPickerDialog pd = MonthYearPickerDialog.newInstance(currentCalendar.get(Calendar.MONTH) + 1, currentCalendar.get(Calendar.YEAR), yearRange);
        pd.show(getSupportFragmentManager(), MONTH_YEAR_DIALOG);
        pd.setListener(new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                currentCalendar.set(year, month, 1);
                updateResultDate();
            }
        });
    }

    private void updateResultDate(){
        String dateStr = new SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                .format(currentCalendar.getTime().getTime());

        dateResultTxt.setText(AppUtils.capitalizeFirstChar(dateStr));
    }

    private void setupToolbar(){
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if(supportActionBar != null){
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setElevation(0);
        }
        setTitle(ViewUtils.textFuturaAndFuturaBold(this, getTitle().toString()));
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

}
