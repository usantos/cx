package br.gov.caixa.loterias.apostar.interno.view.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.gov.caixa.loterias.apostar.interno.R;

/**
 * A simple {@link Fragment} subclass.
 */


public class BottomSheetFragment  extends BottomSheetDialogFragment {

    public static BottomSheetFragment  newInstance() {
        return new BottomSheetFragment ();
    }

    @Override public int getTheme() {
        return R.style.AppBottomSheetDialogTheme;
    }

    @Nullable @Override public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        final View view = inflater
                .inflate(R.layout.fragment_bottom_sheet, container, false);
        setaViews(view);
        return view;
    }


    private void setaViews(View view){
    }

}