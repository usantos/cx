package br.gov.caixa.loterias.apostas.view.custom;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoConcursoEnum;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.listener.OnEscolheModalidadeDialogListener;


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
    private AppCompatCheckBox checkBox;

    private List<ParametroSimulacao>  arrayConcursos = new ArrayList<>();
    private Long idApostaFavorita;

    private OnEscolheModalidadeDialogListener listener;

    private List<String> listaConcursosString;
    private List<String> listaTeimosinhas;
    private boolean mostraTeimosinha;
    private String txtBotao;

    private Boolean toggleTipo = false;
    private boolean temEspelho;


    public AdicionarCarrinhoFavoritasView() {}

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
        return inflater.inflate(R.layout.dialog_adicionar_carrinho_favoritas, container,false);
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return dialog;
    }


    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView selectText = view.findViewById(R.id.selecioneText);
        selectText.requestFocus();
        selectText.setHint(getString(R.string.titulo));
        picker_tipo_concurso = view.findViewById(R.id.picker_tipo_concurso);
        adicionarCarrinhoFavoritosButton = view.findViewById(R.id.adicionarCarrinhoFavoritosButton);
        cancelarModalFavoritosAddCarrinhoButton = view.findViewById(R.id.cancelarModalFavoritosAddCarrinhoButton);
        selecioneConcursoButton = view.findViewById(R.id.selecioneConcursoButton);
        selecioneConcursoButton.setHint("Recolhido");
        selecioneQntTeimosinhaButton = view.findViewById(R.id.selecioneQntTeimosinhaButton);
        selecioneQntTeimosinhaButton.setHint("Recolhido");
        checkBox = view.findViewById(R.id.checkbox_aposta_espelho);

        if (txtBotao != null){
            adicionarCarrinhoFavoritosButton.setText(txtBotao);
        }

        cancelarModalFavoritosAddCarrinhoButton.setOnClickListener( this );
        adicionarCarrinhoFavoritosButton.setOnClickListener( this );

        if(arrayConcursos.size() == 1){
            if(arrayConcursos.get( 0 ).getParametroJogo().getConcurso().getTipoConcurso() == TipoConcursoEnum.NORMAL
                    && (arrayConcursos.get( 0 ).getParametroJogo().getTeimosinhas().size()) > 0 ){
                selecioneConcursoButton.setVisibility( View.GONE );
                apresentaQtdTeimosinha(TipoConcursoEnum.NORMAL);
                habilitaBotaoAdicionar(true);
            }
        } else {
            if(arrayConcursos.get( 0 ).getParametroJogo().getConcurso().getTipoConcurso() == TipoConcursoEnum.NORMAL
                    && (arrayConcursos.get( 0 ).getParametroJogo().getTeimosinhas().size()) > 0 ) {
                apresentaQtdTeimosinha(TipoConcursoEnum.NORMAL);
            }
        }

        listaConcursosString = new ArrayList<>();
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


        picker_tipo_concurso.setOnValueChangedListener((numberPicker, oldVal, newVal) -> {
            setPickerTeimosinhas( newVal, view );
            apresentaQtdTeimosinha(arrayConcursos.get(newVal).getParametroJogo().getConcurso().getTipoConcurso());
        });

        picker_qnt_teimosinhas.setOnValueChangedListener((numberPicker, oldVal, newVal) -> {});

        selecioneConcursoButton.setOnClickListener(view12 -> {
            habilitaBotaoAdicionar(true);
            Integer alturaAnimacao = 0;
            toggleTipo = !toggleTipo;
            if (picker_tipo_concurso.getHeight()==0){
                alturaAnimacao = 100;
            }

            setarAlturaConteudo( alturaAnimacao, picker_tipo_concurso );

            selecioneConcursoButton.setHint(toggleTipo ? "Expandido" : "Recolhido");
        });


        selecioneQntTeimosinhaButton.setOnClickListener(view1 -> {
            if (picker_qnt_teimosinhas.getVisibility() == View.VISIBLE) {
                selecioneQntTeimosinhaButton.setHint("Recolhido");
                setarAlturaConteudo(0, picker_qnt_teimosinhas);
                picker_qnt_teimosinhas.setVisibility(View.GONE);
               } else {

                picker_qnt_teimosinhas.setVisibility(View.VISIBLE);
                selecioneQntTeimosinhaButton.setHint("Expandido");

                int itemHeightPx = ViewUtils.setDisplayMetric(10, getContext());
                int maxHeightPx = ViewUtils.setDisplayMetric(100, getContext());
                int alturaAnimacao = Math.min(listaTeimosinhas.size() * itemHeightPx, maxHeightPx);

                setarAlturaConteudo(alturaAnimacao, picker_qnt_teimosinhas);

            }
        });

        if (temEspelho){
            checkBox.setVisibility(View.VISIBLE);
        } else {
            checkBox.setVisibility(View.GONE);
        }
    }

    private void habilitaBotaoAdicionar(boolean isEnabled) {
        if (isEnabled){
            adicionarCarrinhoFavoritosButton.setEnabled(true);
            adicionarCarrinhoFavoritosButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.verdeazul));
        } else {
            adicionarCarrinhoFavoritosButton.setEnabled(false);
            adicionarCarrinhoFavoritosButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.cinzaDisable));
        }
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.cancelarModalFavoritosAddCarrinhoButton){
            dismiss();
        }
        if (view.getId() == R.id.adicionarCarrinhoFavoritosButton){
            Integer tipoConcurso = 2;
            if (arrayConcursos.get( picker_tipo_concurso.getValue() ).getParametroJogo().getConcurso().getTipoConcurso() == TipoConcursoEnum.NORMAL){
                tipoConcurso = 1;
            }
            int teimosinha = Integer.parseInt(listaTeimosinhas.get(picker_qnt_teimosinhas.getValue()));
            dismiss();

            listener.selecionado(tipoConcurso, teimosinha, checkBox.getVisibility() == View.VISIBLE ? checkBox.isChecked() : null);
        }
    }

    private void setPickerTeimosinhas(Integer position, View view){
        picker_qnt_teimosinhas = view.findViewById(R.id.picker_qnt_teimosinhas);
        listaTeimosinhas = new ArrayList<>();
        for ( int i = 0;  i < arrayConcursos.get( position ).getParametroJogo().getTeimosinhas().size(); i++ ){
            if (isDadosValidos(arrayConcursos.get(position))) {
                listaTeimosinhas.add( arrayConcursos.get( position ).getParametroJogo().getTeimosinhas().get( i ).toString() );
            }
        }
        picker_qnt_teimosinhas.setDisplayedValues(null);
        picker_qnt_teimosinhas.setMinValue(0);
        picker_qnt_teimosinhas.setMaxValue(listaTeimosinhas.size()-1);
        picker_qnt_teimosinhas.setDisplayedValues( listaTeimosinhas.toArray(new String[listaTeimosinhas.size()]));
        picker_qnt_teimosinhas.refreshDrawableState();
    }

    private boolean isDadosValidos(ParametroSimulacao parametroSimulacao) {
        return parametroSimulacao != null & parametroSimulacao.getParametroJogo() != null &&
                parametroSimulacao.getParametroJogo().getTeimosinhas() != null &&
                !parametroSimulacao.getParametroJogo().getTeimosinhas().isEmpty();
    }

    private void setarAlturaConteudo(Integer alturaConteudo, final View view){
        ValueAnimator animator = ValueAnimator.ofInt(view.getHeight(), ViewUtils.setDisplayMetric( alturaConteudo, getContext() ));
        animator.addUpdateListener(valueAnimator -> {
            int val = (Integer) valueAnimator.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = val;
            view.setLayoutParams(layoutParams);
        });

        animator.setDuration(500);
        animator.start();
    }

    private void apresentaQtdTeimosinha(TipoConcursoEnum tipoConcurso) {
        if (mostraTeimosinha){
            if(tipoConcurso == TipoConcursoEnum.NORMAL) {
                selecioneQntTeimosinhaButton.setVisibility( View.VISIBLE );
            } else {
                selecioneQntTeimosinhaButton.setVisibility( View.GONE );
                picker_qnt_teimosinhas.setVisibility(View.GONE);
            }
        }
    }

    public void setIdApostaFavorita(Long idApostaFavorita) {
        this.idApostaFavorita = idApostaFavorita;
    }

    public void setArrayConcursos(List<ParametroSimulacao> arrayConcursos){
        this.arrayConcursos = arrayConcursos;
    }

    public void setListener(OnEscolheModalidadeDialogListener listener) {
        this.listener = listener;
    }

    public void setMostraTeimosinha(boolean mostraTeimosinha) {
        this.mostraTeimosinha = mostraTeimosinha;
    }

    public void setTextoBotao(String txtBotao){
        this.txtBotao = txtBotao;
    }

    public void setTemEspelho(boolean temEspelho) {
        this.temEspelho = temEspelho;
    }
}


