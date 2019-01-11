package br.gov.caixa.loterias.apostar.interno.view.custom;

import android.animation.ValueAnimator;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;
import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.TipoConcursoEnum;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;



/**
 * Created by cedesbr450 on 18/04/18.
 */

public class AdicionarCarrinhoFavoritasView extends DialogFragment implements View.OnClickListener {

    private Button adicionarCarrinhoFavoritosButton;
    private Button cancelarModalFavoritosAddCarrinhoButton;

    private Button selecioneConcursoButton;
    private Button selecioneQntTeimosinhaButton;

    private NumberPicker picker_tipo_concurso;
    private NumberPicker picker_qnt_teimosinhas;

    public List<ParametroSimulacao>  arrayConcursos = new ArrayList<ParametroSimulacao>();
    public Long idApostaFavorita;


    private List<String> listaConcursosString;
    private List<String> listaTeimosinhas;

    public AdicionarCarrinhoFavoritasView() {

    }

    public static AdicionarCarrinhoFavoritasView newInstance() {
        AdicionarCarrinhoFavoritasView frag = new AdicionarCarrinhoFavoritasView();
//        Bundle args = new Bundle();
//        args.putString("title", title);
//        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_adicionar_carrinho_favoritas, container);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        picker_tipo_concurso = (NumberPicker) view.findViewById(R.id.picker_tipo_concurso);

        adicionarCarrinhoFavoritosButton = (Button) view.findViewById(R.id.adicionarCarrinhoFavoritosButton);
        cancelarModalFavoritosAddCarrinhoButton = (Button) view.findViewById(R.id.cancelarModalFavoritosAddCarrinhoButton);
        selecioneConcursoButton = (Button) view.findViewById(R.id.selecioneConcursoButton);
        selecioneQntTeimosinhaButton = (Button) view.findViewById(R.id.selecioneQntTeimosinhaButton);


        cancelarModalFavoritosAddCarrinhoButton.setOnClickListener( this );
        adicionarCarrinhoFavoritosButton.setOnClickListener( this );

        if( arrayConcursos.size() == 1 ){
            if (arrayConcursos.get( 0 ).getParametroJogo().getConcurso().getTipoConcurso() == TipoConcursoEnum.NORMAL && (arrayConcursos.get( 0 ).getParametroJogo().getTeimosinhas().size()) > 0 ){
                selecioneConcursoButton.setVisibility( View.GONE );
                apresentaQtdTeimosinha();
            }
        } else {
            if( arrayConcursos.get( 0 ).getParametroJogo().getConcurso().getTipoConcurso() == TipoConcursoEnum.NORMAL && (arrayConcursos.get( 0 ).getParametroJogo().getTeimosinhas().size()) > 0 ) {
                apresentaQtdTeimosinha();
            }
        }

        listaConcursosString = new ArrayList<String>();
        for (int i= 0;  i < arrayConcursos.size(); i++){
            if (arrayConcursos.get( i ).getParametroJogo().getConcurso().getTipoConcurso() == TipoConcursoEnum.ESPECIAL){
                listaConcursosString.add(arrayConcursos.get( i ).getParametroJogo().getConcurso().getModalidadeDetalhada().getDescricaoEspecial()  );
            }else{
                listaConcursosString.add(arrayConcursos.get( i ).getParametroJogo().getConcurso().getModalidadeDetalhada().getDescricao()  );
            }
        }

        picker_tipo_concurso.setMinValue(0);
        picker_tipo_concurso.setMaxValue(listaConcursosString.size()-1);
        picker_tipo_concurso.setDisplayedValues( listaConcursosString.toArray(new String[listaConcursosString.size()]));
        picker_tipo_concurso.setValue( 0 );

        setPickerTeimosinhas( 0 , view);


        picker_tipo_concurso.setOnValueChangedListener( new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                setPickerTeimosinhas( newVal, view );
            }
        });


        picker_qnt_teimosinhas.setOnValueChangedListener( new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {

            }
        });

        selecioneConcursoButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer alturaAnimacao = 0;
                if (picker_tipo_concurso.getHeight()==0){
                    alturaAnimacao = 100;
                }
                setarAlturaConteudo( alturaAnimacao, picker_tipo_concurso );
            }
        } );

        selecioneQntTeimosinhaButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer alturaAnimacao = 0;
                if (picker_qnt_teimosinhas.getHeight()==0){
                    alturaAnimacao = 100;
                }
                setarAlturaConteudo( alturaAnimacao, picker_qnt_teimosinhas );
            }
        } );

    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.cancelarModalFavoritosAddCarrinhoButton){
            dismiss();
        }
        if (view.getId() == R.id.adicionarCarrinhoFavoritosButton){
            Toast.makeText( getContext(), "teste", Toast.LENGTH_LONG );
            Integer tipoConcurso = 2;
            if (arrayConcursos.get( picker_tipo_concurso.getValue() ).getParametroJogo().getConcurso().getTipoConcurso() == TipoConcursoEnum.NORMAL){
                tipoConcurso = 1;
            }
            adicionarCarrinhoCallWebservice( idApostaFavorita, tipoConcurso , Integer.parseInt( listaTeimosinhas.get( picker_qnt_teimosinhas.getValue() ) ) );
        }
    }

    private void setPickerTeimosinhas(Integer position, View view){
        picker_qnt_teimosinhas = (NumberPicker) view.findViewById(R.id.picker_qnt_teimosinhas);
        listaTeimosinhas = new ArrayList<String>();
        for (int i= 0;  i < arrayConcursos.get( position ).getParametroJogo().getTeimosinhas().size(); i++){
            listaTeimosinhas.add( arrayConcursos.get( position ).getParametroJogo().getTeimosinhas().get( i ).toString() );
        }
        picker_qnt_teimosinhas.setDisplayedValues(null);
        picker_qnt_teimosinhas.setMinValue(0);
        picker_qnt_teimosinhas.setMaxValue(listaTeimosinhas.size()-1);
        picker_qnt_teimosinhas.setDisplayedValues( listaTeimosinhas.toArray(new String[listaTeimosinhas.size()]));
        picker_qnt_teimosinhas.refreshDrawableState();
    }


    private void setarAlturaConteudo(Integer alturaConteudo, final View view){
        ValueAnimator animator = ValueAnimator.ofInt(view.getHeight(), ViewUtils.setDisplayMetric( alturaConteudo, getContext() ));
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = val;
                view.setLayoutParams(layoutParams);
            }
        });
        animator.setDuration(500);
        animator.start();
    }

    private void apresentaQtdTeimosinha () {
        if(selecioneQntTeimosinhaButton.getVisibility() == View.GONE) {
            selecioneQntTeimosinhaButton.setVisibility( View.VISIBLE );
        } else {
            selecioneQntTeimosinhaButton.setVisibility( View.GONE );
        }
    }

    public void adicionarCarrinhoCallWebservice(final Long idApostaFavorita, final int valorTipoConcurso, final int qtdTeimosinhas) {
        final AlertDialog loadViewProgress = LoadingViewLoterias.show(getActivity());
        final ApostaBO apostaBO = ApostaBO_.getInstance_(getActivity());

        apostaBO.postIncluirApostaFavoritaCarrinho(idApostaFavorita, valorTipoConcurso, qtdTeimosinhas, new RequestListener<CarrinhoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoDTOResponse response) {
                loadViewProgress.dismiss();
                dismiss();
                ViewUtils.alert( getContext(), getContext().getResources().getString( R.string.label_aposta_salva_carinha ) );
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect( error, getActivity() );

            }
        });
    }


}


