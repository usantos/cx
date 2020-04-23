package br.gov.caixa.loterias.apostar.interno.view.fragment;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;

/**
 * A simple {@link Fragment} subclass.
 */


public class BottomSheetFragment  extends BottomSheetDialogFragment {

    private TextView tvCaixa, tvProamjo, tvProamjoEmail, tvAvbEmail, tvProad, tvProadEmail;
    private Resources src;

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

        src = getActivity().getResources();
        tvCaixa.setText(ViewUtils.textFuturaAndFuturaBold(getActivity(),src.getString(R.string.jr_bs_caixa_tel)));
        tvProamjo.setText(ViewUtils.textFuturaAndFuturaBold(getActivity(),src.getString(R.string.jr_bs_proamjo)));
        tvProad.setText(ViewUtils.textFuturaAndFuturaBold(getActivity(),src.getString(R.string.jr_bs_proad)));


    }

    private void setaMetodos() {
        tvProamjoEmail.setOnClickListener(v-> enviaEmail(getActivity(),src.getString(R.string.jr_bs_proamjo_email)));
        tvAvbEmail.setOnClickListener(v-> enviaEmail(getActivity(),src.getString(R.string.jr_bs_avb_email)));
        tvProadEmail.setOnClickListener(v-> enviaEmail(getActivity(),src.getString(R.string.jr_bs_proad_email)));
    }

    private static void enviaEmail(Activity activity, String email) {
        final Intent shareIntent = new Intent(Intent.ACTION_SENDTO);
        shareIntent.setData(Uri.parse( "mailto:"));
        shareIntent.putExtra(Intent.EXTRA_EMAIL,new String[] {email});
        try {
            activity.startActivity(Intent.createChooser(shareIntent, activity.getString(R.string.enviando_email)));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(activity, activity.getResources().getString(R.string.erro_mail_provider), Toast.LENGTH_SHORT).show();
        }
    }
}