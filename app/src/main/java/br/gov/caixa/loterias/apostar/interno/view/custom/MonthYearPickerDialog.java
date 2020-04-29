package br.gov.caixa.loterias.apostar.interno.view.custom;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.NumberPicker;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.MesAnoDTO;
import br.gov.caixa.loterias.apostar.interno.utils.AppUtils;
import br.gov.caixa.loterias.apostar.interno.utils.DateUtils;

@EFragment
public class MonthYearPickerDialog extends DialogFragment {

    private DatePickerDialog.OnDateSetListener listener;

    private NumberPicker monthPicker;
    private NumberPicker yearPicker;

    private Calendar cal = Calendar.getInstance();

    @FragmentArg
    protected List<MesAnoDTO> mesesAnos;

    @FragmentArg
    protected Integer anoDefault;

    @FragmentArg
    protected Integer mesDefault;

    private HashMap<Integer, List<Integer>> monthYearsMap = new HashMap<>();

    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    private void setMonthsAvailable(int year){
        setMonthsAvailable(year, null);
    }

    private void setMonthsAvailable(int year, Integer selectedMonth){
        String[] months = DateUtils.getAllMonthsOfYear();
        int minValue;
        int maxValue;
        int currentValue = 0;

        if(!monthYearsMap.isEmpty()){
            List<Integer> list = monthYearsMap.get(year);
            String arr[] = new String[list.size()];
            for (int i = 0; i < list.size(); i++){
                String month = months[list.get(i) - 1];
                arr[i] = AppUtils.capitalizeFirstChar(month);

                if(selectedMonth != null && (selectedMonth.intValue() == list.get(i))){
                    currentValue = i;
                }
            }
            months = arr;
            minValue = 0;
            maxValue = arr.length - 1;
        }else{
            for (int i =0; i < months.length; i++){
                months[i] = AppUtils.capitalizeFirstChar(months[i]);
            }
            currentValue = cal.get(Calendar.MONTH) + 1;
            minValue = 1;
            maxValue = 12;
        }

        monthPicker.setDisplayedValues(null);
        monthPicker.setMinValue(minValue);
        monthPicker.setMaxValue(maxValue);
        monthPicker.setValue(currentValue);

        monthPicker.setDisplayedValues(months);

        yearPicker.setOnValueChangedListener(changeValueListener);
    }

    private String[] getYearsFromMap(){
        String yearValues [] = null;

        if(mesesAnos != null && !mesesAnos.isEmpty()){
            MesAnoDTO mesAnoAtual = mesesAnos.get(0);

            int currentYear = mesAnoAtual.getAno().intValue();
            monthYearsMap.put(currentYear, new ArrayList<>());

            for (MesAnoDTO mesAno : mesesAnos) {
                int year = mesAno.getAno().intValue();
                int month = mesAno.getMes().getValor().intValue();

                if(currentYear != year){
                    currentYear = year;
                    monthYearsMap.put(currentYear, new ArrayList<>());
                }
                List<Integer> monthList = monthYearsMap.get(year);
                monthList.add(month);

            }

            yearValues = new String[monthYearsMap.keySet().size()];
            int i = 0;
            for(Integer year : monthYearsMap.keySet()) {
                yearValues[i++] = year.toString();
            }
        }

        return yearValues;
    }

    private final NumberPicker.OnValueChangeListener changeValueListener = (numberPicker, oldVal, newVal) -> {
        if(monthYearsMap != null){
            setMonthsAvailable(Integer.parseInt(numberPicker.getDisplayedValues()[newVal]));
        }
    };

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String yearValues [] = getYearsFromMap();

        Activity activity = getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        // Get the layout inflater
        LayoutInflater inflater = activity.getLayoutInflater();

        View dialog = inflater.inflate(R.layout.dialog_month_year, null);
        monthPicker = dialog.findViewById(R.id.picker_month);
        yearPicker = dialog.findViewById(R.id.picker_year);

        yearPicker.setOnValueChangedListener(changeValueListener);

        if(monthYearsMap.isEmpty()){
            int minYear = cal.get(Calendar.YEAR) - 200;
            int maxYear = cal.get(Calendar.YEAR);
            yearPicker.setMinValue(minYear);
            yearPicker.setMaxValue(maxYear);
            yearPicker.setValue(maxYear);
            setMonthsAvailable(maxYear);
        }else if(yearValues != null && yearValues.length > 0){
            yearPicker.setDisplayedValues(yearValues);
            yearPicker.setMinValue(0);
            yearPicker.setMaxValue(yearValues.length - 1);

            int yearDefault = Integer.parseInt(yearValues[0]);
            if(anoDefault != null){
                int i = 0;
                for(Integer year : monthYearsMap.keySet()) {
                    if(year == anoDefault.intValue()){
                        yearDefault = year;
                        break;
                    }
                    i++;
                }
                yearPicker.setValue(i);
            }else{
                yearPicker.setValue(0);
            }
            setMonthsAvailable(yearDefault, mesDefault);
        }

        builder.setView(dialog)
                .setTitle(R.string.label_selecione_mes)
                // Add action buttons
                .setPositiveButton(R.string.ok, (dialog12, id) -> {
                    if(listener != null){
                        if(monthYearsMap != null){
                            int year = Integer.parseInt(yearPicker.getDisplayedValues()[yearPicker.getValue()]);
                            String monthName = monthPicker.getDisplayedValues()[monthPicker.getValue()];
                            int month = DateUtils.parseMonthName(monthName);
                            listener.onDateSet
                                    (new DatePicker(getContext()), year, month, 0);
                        }else{
                            listener.onDateSet(new DatePicker(getContext()), yearPicker.getValue(), monthPicker.getValue() - 1, 0);
                        }
                    }
                })
                .setNegativeButton(R.string.cancelar, (dialog1, id) -> MonthYearPickerDialog.this.getDialog().cancel());

        return builder.create();
    }
}
