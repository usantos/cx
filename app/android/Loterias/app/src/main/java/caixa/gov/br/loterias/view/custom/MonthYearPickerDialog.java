package caixa.gov.br.loterias.view.custom;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import java.util.Calendar;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.utils.AppUtils;
import caixa.gov.br.loterias.utils.DateUtils;

public class MonthYearPickerDialog extends DialogFragment {

    private DatePickerDialog.OnDateSetListener listener;

    private NumberPicker monthPicker;
    private NumberPicker yearPicker;

    private Calendar cal = Calendar.getInstance();

    public static final String MONTH_KEY = "monthValue";
    public static final String YEAR_KEY = "yearValue";

    public static final String YEAR_RANGE_KEY = "yearRangeValue";

    private int monthVal = -1 , yearVal =-1 ;
    private int [] yearRange = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getArguments();
        if(extras != null){
            monthVal = extras.getInt(MONTH_KEY , -1);
            yearVal = extras.getInt(YEAR_KEY , -1);
            yearRange =  extras.getIntArray(YEAR_RANGE_KEY);
        }
    }

    public static MonthYearPickerDialog newInstance(int monthIndex, int yearIndex, int yearRange[]) {
        MonthYearPickerDialog f = new MonthYearPickerDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt(MONTH_KEY, monthIndex);
        args.putInt(YEAR_KEY, yearIndex);
        args.putIntArray(YEAR_RANGE_KEY, yearRange);

        f.setArguments(args);

        return f;
    }

    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialog = inflater.inflate(R.layout.dialog_month_year, null);
        monthPicker = (NumberPicker) dialog.findViewById(R.id.picker_month);
        yearPicker = (NumberPicker) dialog.findViewById(R.id.picker_year);

        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);

        if(monthVal != -1) {// && (monthVal > 0 && monthVal < 13))
            monthPicker.setValue(monthVal);
        }else{
            monthPicker.setValue(cal.get(Calendar.MONTH) + 1);
        }

        String[] months = DateUtils.getAllMonthsOfYear();
        for (int i =0; i < months.length; i++){
            months[i] = AppUtils.capitalizeFirstChar(months[i]);
        }
        monthPicker.setDisplayedValues(months);

        int minYear = cal.get(Calendar.YEAR);
        int maxYear = cal.get(Calendar.YEAR) + 100;

        if(yearRange != null){
            maxYear = yearRange[yearRange.length - 1];
            minYear = yearRange[0];
        }

        yearPicker.setMinValue(minYear);
        yearPicker.setMaxValue(maxYear);

        if(yearVal != -1) {
            yearPicker.setValue(yearVal);
        }else {
            yearPicker.setValue(minYear);
        }

        builder.setView(dialog)
                .setTitle(R.string.label_selecione_mes)
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDateSet(null, yearPicker.getValue(), monthPicker.getValue() - 1, 0);
                    }
                })
                .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MonthYearPickerDialog.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }
}
