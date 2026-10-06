package br.gov.caixa.loterias.apostas.view.fragment;


import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.utils.Utils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;


public class BottomSheetFragment  extends BottomSheetDialogFragment {

    private TextView tvCaixa, tvProamjo, tvProamjoEmail, tvAvbEmail, tvProad, tvProadEmail, tvCapsSite, tvJaSite;
    private Resources src;

    public static BottomSheetFragment  newInstance() {
        return new BottomSheetFragment ();
    }

    @Override public int getTheme() {
        return R.style.AppBottomSheetDialogTheme;
    }

    @Nullable
    @Override public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
                                      ) {
        final View view = inflater
                .inflate(R.layout.fragment_bottom_sheet, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setaViews(view);
        setaMetodos();
    }

    private void setaViews(View view) {
        tvCaixa = view.findViewById(R.id.tv_caixa);
        tvProamjo = view.findViewById(R.id.tv_proamjo);
        tvProamjoEmail = view.findViewById(R.id.tv_proamjo_email);
        tvAvbEmail = view.findViewById(R.id.tv_avb_email);
        tvProad = view.findViewById(R.id.tv_proad);
        tvProadEmail = view.findViewById(R.id.tv_proad_email);
        tvCapsSite = view.findViewById(R.id.tv_caps_site);
        tvJaSite = view.findViewById(R.id.tv_ja_site);

        src = getActivity().getResources();
        //tvCaixa.setText(ViewUtils.textFuturaAndFuturaBold(getActivity(),src.getString(R.string.jr_bs_caixa_tel)));
        //tvProamjo.setText(ViewUtils.textFuturaAndFuturaBold(getActivity(),src.getString(R.string.jr_bs_proamjo)));
        //tvProad.setText(ViewUtils.textFuturaAndFuturaBold(getActivity(),src.getString(R.string.jr_bs_proad)));
        tvCaixa.setText(ViewUtils.textCaixaSTDBold(getActivity(),src.getString(R.string.jr_bs_caixa_tel)));
        tvProamjo.setText(ViewUtils.textCaixaSTDBold(getActivity(),src.getString(R.string.jr_bs_proamjo)));
        tvProad.setText(ViewUtils.textCaixaSTDBold(getActivity(),src.getString(R.string.jr_bs_proad)));


    }

    private void setaMetodos() {
        tvProamjoEmail.setOnClickListener(v-> Utils.enviaEmail(getActivity(),src.getString(R.string.jr_bs_proamjo_email)));
        tvAvbEmail.setOnClickListener(v->  Utils.enviaEmail(getActivity(),src.getString(R.string.jr_bs_avb_email)));
        tvProadEmail.setOnClickListener(v->  Utils.enviaEmail(getActivity(),src.getString(R.string.jr_bs_proad_email)));
        tvCapsSite.setOnClickListener(v-> Utils.abreUrl(getActivity(), R.string.jr_caps_url));
        tvJaSite.setOnClickListener(v -> Utils.abreUrl(getActivity(), R.string.jr_ja_url));
    }
}
