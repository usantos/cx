package caixa.gov.br.loterias.view.adapter;

import android.animation.Animator;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bean.SessaoUsuario;
import caixa.gov.br.loterias.model.bo.ApostaBO;
import caixa.gov.br.loterias.model.bo.ApostaBO_;
import caixa.gov.br.loterias.model.bo.RedirectNetwork;
import caixa.gov.br.loterias.model.bo.RequestListener;
import caixa.gov.br.loterias.model.bo.silce.dto.ApostaFavoritaDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.CarrinhoDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.DTOEnumInteger;
import caixa.gov.br.loterias.model.bo.silce.dto.IncluirApostaFavoritaDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.ParametroMesDeSorte;
import caixa.gov.br.loterias.model.bo.silce.dto.ParametroSimulacao;
import caixa.gov.br.loterias.model.bo.silce.dto.RetornoPadraoDTO;
import caixa.gov.br.loterias.model.bo.MensagensNetwork;
import caixa.gov.br.loterias.model.bo.silce.dto.TipoConcursoEnum;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.DadosPessoaisActivity;
import caixa.gov.br.loterias.view.FavoritasActivity;
import caixa.gov.br.loterias.view.custom.AdicionarCarrinhoFavoritasView;
import caixa.gov.br.loterias.view.custom.LoadingViewLoterias;

/**
 * Created by cedesbr450 on 16/02/18.
 */


public final class FavoritasRecyclerViewAdapter extends RecyclerView.Adapter<FavoritasRecyclerViewAdapter.ViewHolder> {


    private final Context context;
    private final Activity activity;
    private final List<ApostaFavoritaDTO> listaApostaFavorita;


    public FavoritasRecyclerViewAdapter(final Activity activity, final Context context, final List<ApostaFavoritaDTO> listaApostaFavorita) {
        this.context = context;
        this.listaApostaFavorita = listaApostaFavorita;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final Context context = parent.getContext();
        final View view = LayoutInflater.from(context).inflate(R.layout.linearlayout_favoritas, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final ApostaFavoritaDTO aposta = listaApostaFavorita.get(position);

        final RelativeLayout relativeLayoutFavoritasAdapter = (RelativeLayout) holder.itemView.findViewById(R.id.RelativeLayoutFavoritasAdapter);

        final RelativeLayout relativeLayoutContentFavoritas = (RelativeLayout) holder.itemView.findViewById(R.id.RelativeLayoutContentFavoritas);

        final RelativeLayout relativeLayoutContentAlterarFavoritas = (RelativeLayout) holder.itemView.findViewById(R.id.RelativeLayoutContentAlterarFavoritas);

        final RelativeLayout relativeLayoutContentAlterarFavoritasEditar = (RelativeLayout) holder.itemView.findViewById( R.id.relativeLayoutContentAlterarFavoritasEditar );

        final LinearLayout linearLayoutLayoutContentAlterarFavoritasSalvo = (LinearLayout) holder.itemView.findViewById( R.id.linearLayoutLayoutContentAlterarFavoritasSalvo );

        final HorizontalScrollView horizontalScrollViewFavoritasItem = (HorizontalScrollView) holder.itemView.findViewById(R.id.HorizontalScrollViewFavoritasItem);

        final EditText editTextNomeDaAposta = (EditText) holder.itemView.findViewById(R.id.editTextNomeDaAposta);

        final TextView tituloApostaFavorita = (TextView) holder.itemView.findViewById(R.id.tituloApostaFavorita);
        tituloApostaFavorita.setText(listaApostaFavorita.get(position).getModalidade().getDescricao());

        final TextView tituloNomeApostaFavorita = (TextView) holder.itemView.findViewById(R.id.tituloNomeApostaFavorita);
        tituloNomeApostaFavorita.setText(" | " + listaApostaFavorita.get(position).getNome());

        String numeroStr = "";
        for (Integer num : aposta.getNumerosSelecionados()) {
            if (num == (aposta.getNumerosSelecionados().get(aposta.getNumerosSelecionados().size() - 1))) {
                numeroStr += String.format("%02d", num);
            } else {
                numeroStr += String.format("%02d - ", num);
            }
        }
        holder.numerosApostaFavorita.setText(numeroStr);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewUtils.getAppUsableScreenSize(context).x, ViewUtils.setDisplayMetric(100, context));
        relativeLayoutFavoritasAdapter.setLayoutParams(params);

        final Button editarButtonFavoritos = (Button) holder.itemView.findViewById(R.id.editarButtonFavoritos);
        final Button exluirButtonFavoritos = (Button) holder.itemView.findViewById(R.id.exluirButtonFavoritos);
        final Button buttonSalvarNomeAposta = (Button) holder.itemView.findViewById(R.id.buttonSalvarNomeAposta);
        final Button editarButtonAddCarrinho = (Button) holder.itemView.findViewById(R.id.editarButtonAddCarrinho);


        final TextView nomeApostaSalvoTextView = (TextView) holder.itemView.findViewById(R.id.nomeApostaSalvoTextView);

        editarButtonFavoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translatePosicaoVoltarCard(relativeLayoutContentFavoritas,
                        ViewUtils.setDisplayMetric(
                                ViewUtils.getAppUsableScreenSize(context).x/2 -
                                        ViewUtils.setDisplayMetric(40,
                                                context) , context));
                editTextNomeDaAposta.setEnabled(true);
                buttonSalvarNomeAposta.setEnabled(true);
            }
        });

        exluirButtonFavoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFavoritaCallWebservice(position);
            }
        });

        editarButtonAddCarrinho.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adicionarCarrinho(listaApostaFavorita.get(position).getId(), listaApostaFavorita.get( position ).getModalidade());
            }
        } );

        buttonSalvarNomeAposta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextNomeDaAposta.getText().toString().equals( "" )){
                    ViewUtils.alert( context, context.getResources().getString( R.string.MA002 ) + " nome da aposta" );
                }else {
                    alterarFavoritaCallWebservice(listaApostaFavorita.get(position),
                            relativeLayoutContentFavoritas,
                            relativeLayoutContentAlterarFavoritasEditar,
                            linearLayoutLayoutContentAlterarFavoritasSalvo,
                            editTextNomeDaAposta,
                            buttonSalvarNomeAposta,
                            tituloNomeApostaFavorita,
                            nomeApostaSalvoTextView);
                }
            }
        });



        horizontalScrollViewFavoritasItem.setOnTouchListener(new View.OnTouchListener() {
            float initialX = 0;
            boolean cancelarAnimacao = false;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (relativeLayoutContentFavoritas.getX() == ViewUtils.setDisplayMetric(
                                ViewUtils.getAppUsableScreenSize(context).x/2 -
                                        ViewUtils.setDisplayMetric(40,
                                                context) , context)){
                            if (event.getX()<relativeLayoutContentFavoritas.getX()){
                                cancelarAnimacao = true;
                            }else {
                                cancelarAnimacao = false;
                                linearLayoutLayoutContentAlterarFavoritasSalvo.setVisibility( View.INVISIBLE );
                                relativeLayoutContentAlterarFavoritasEditar.setVisibility( View.VISIBLE );
                                nomeApostaSalvoTextView.setText( "" );
                            }
                            initialX = -10;
                        }else{
                            initialX = event.getX();
                        }
                    case MotionEvent.ACTION_MOVE:
                        Log.d("ACTION_MOVE", "ACTION_MOVE: "+ event.getX());
                        Log.d("initialX", "initialX: "+ initialX);
                        if (event.getX()>= initialX && initialX != -10){
                            relativeLayoutContentFavoritas.setX(event.getX()-initialX);
                        }
                    break;
                    case MotionEvent.ACTION_UP:
                        if (relativeLayoutContentFavoritas.getX() > relativeLayoutContentFavoritas.getWidth()/2 && initialX != -10){
                            translatePosicaoVoltarCard(relativeLayoutContentFavoritas,
                                    ViewUtils.setDisplayMetric(
                                            ViewUtils.getAppUsableScreenSize(context).x/2 -
                                                    ViewUtils.setDisplayMetric(40,
                                                            context) , context));
                            editTextNomeDaAposta.setEnabled(true);
                            buttonSalvarNomeAposta.setEnabled(true);
                        }else{
                            if (cancelarAnimacao != true){
                                translatePosicaoVoltarCard(relativeLayoutContentFavoritas,
                                        ViewUtils.setDisplayMetric(0, context));
                                editTextNomeDaAposta.setEnabled(false);
                                buttonSalvarNomeAposta.setEnabled(false);
                            }
                        }
                    case MotionEvent.ACTION_CANCEL:


                        break;
                    case MotionEvent.ACTION_OUTSIDE:

                        break;
                }
                return horizontalScrollViewFavoritasItem.onTouchEvent(event);
            }
        });


        }



    @Override
    public int getItemCount() {
        return listaApostaFavorita.size();
    }

    public List<ApostaFavoritaDTO> getCompras() {
        return Collections.unmodifiableList(listaApostaFavorita);
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        protected RelativeLayout relativeLayoutContentFavoritas;
        protected RelativeLayout relativeLayoutContentAlterarFavoritas;
        protected RelativeLayout relativeLayoutContentAlterarFavoritasEditar;
        protected LinearLayout linearLayoutLayoutContentAlterarFavoritasSalvo;
        protected TextView tituloApostaFavorira;
        protected TextView numerosApostaFavorita;
        protected TextView nomeApostaSalvoTextView;

        public ViewHolder(final View itemView) {
            super(itemView);
            relativeLayoutContentFavoritas = (RelativeLayout) itemView.findViewById(R.id.RelativeLayoutContentFavoritas);
            relativeLayoutContentAlterarFavoritas = (RelativeLayout) itemView.findViewById(R.id.RelativeLayoutContentAlterarFavoritas);
            relativeLayoutContentAlterarFavoritasEditar = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutContentAlterarFavoritasEditar);
            linearLayoutLayoutContentAlterarFavoritasSalvo = (LinearLayout) itemView.findViewById(R.id.linearLayoutLayoutContentAlterarFavoritasSalvo);
            tituloApostaFavorira = (TextView) itemView.findViewById(R.id.tituloApostaFavorira);
            numerosApostaFavorita = (TextView) itemView.findViewById(R.id.numerosApostaFavorita);
            nomeApostaSalvoTextView = (TextView) itemView.findViewById(R.id.nomeApostaSalvoTextView);
        }
    }


    private void translatePosicaoVoltarCard(final RelativeLayout favoritasRelativeLayout, int eixoX){

        favoritasRelativeLayout.animate().translationX(eixoX).translationY(0).setDuration(500).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {}

                @Override
                public void onAnimationEnd(Animator animation) {

                }

                @Override
                public void onAnimationCancel(Animator animation) {}

                @Override
                public void onAnimationRepeat(Animator animation) {}
            });
    }


    public void removeAt(int position) {
        listaApostaFavorita.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, listaApostaFavorita.size());
    }


    private void deleteFavoritaCallWebservice(final int position) {
        final AlertDialog loadViewProgress = LoadingViewLoterias.show(activity);
        final ApostaBO apostaBO = ApostaBO_.getInstance_(activity);

        apostaBO.deleteApostasFavoritas(listaApostaFavorita.get(position).getId() + "", new RequestListener<RetornoPadraoDTO>() {
            @Override
            public void onResponse(RetornoPadraoDTO response) {
                loadViewProgress.dismiss();
                    removeAt(position);
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), activity);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect( error, activity );

            }
        });
    }


    public void adicionarCarrinhoCallWebservice(final Long idApostaFavorita, final int valorTipoConcurso, final int qtdTeimosinhas) {
        final AlertDialog loadViewProgress = LoadingViewLoterias.show(activity);
        final ApostaBO apostaBO = ApostaBO_.getInstance_(activity);

        apostaBO.postIncluirApostaFavoritaCarrinho(idApostaFavorita, valorTipoConcurso, qtdTeimosinhas, new RequestListener<CarrinhoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoDTOResponse response) {
                loadViewProgress.dismiss();
                ViewUtils.alert( context, context.getResources().getString( R.string.label_aposta_salva_carinha ) );
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), activity);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect( error, activity );

            }
        });
    }

    private void alterarFavoritaCallWebservice(final ApostaFavoritaDTO apostaFavoritaDTO,
                                               final RelativeLayout favoritasRelativeLayout,
                                               final RelativeLayout relativeLayoutContentAlterarFavoritasEditar,
                                               final LinearLayout linearLayoutLayoutContentAlterarFavoritasSalvo,
                                               final EditText editTextNomeDaAposta,
                                               final Button buttonSalvarNomeAposta,
                                               final TextView nomeApostaTextView,
                                                final TextView nomeApostaSalvoTextView) {
        final AlertDialog loadViewProgress = LoadingViewLoterias.show(activity);
        final ApostaBO apostaBO = ApostaBO_.getInstance_(activity);
        final ApostaFavoritaDTO apostaFavoritaDTOSalvar = apostaFavoritaDTO;
        apostaFavoritaDTOSalvar.setNome(editTextNomeDaAposta.getText().toString());
        ParametroMesDeSorte parametroMesDeSorte = new ParametroMesDeSorte();
        parametroMesDeSorte.setAbreviacao( "" );
        parametroMesDeSorte.setNome( "" );
        parametroMesDeSorte.setNumero( 0 );
        apostaFavoritaDTO.setMesDeSorte( parametroMesDeSorte );
        apostaBO.postApostasFavoritas(apostaFavoritaDTOSalvar, new RequestListener<Boolean>() {
            @Override
            public void onResponse(Boolean response) {
                apostaFavoritaDTOSalvar.setNome(editTextNomeDaAposta.getText().toString());
                nomeApostaTextView.setText(" | "+ apostaFavoritaDTO.getNome());
//                translatePosicaoVoltarCard(favoritasRelativeLayout,
//                        ViewUtils.setDisplayMetric(0, context));
                nomeApostaSalvoTextView.setText( "\""+ editTextNomeDaAposta.getText() +"\"");
                editTextNomeDaAposta.setText("");
                editTextNomeDaAposta.setEnabled(false);
                buttonSalvarNomeAposta.setEnabled(false);
                loadViewProgress.dismiss();
                linearLayoutLayoutContentAlterarFavoritasSalvo.setVisibility( View.VISIBLE );
                relativeLayoutContentAlterarFavoritasEditar.setVisibility( View.GONE );
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect( error, activity );

            }
        });
    }


    private void adicionarCarrinho(Long idApostaFavorita, DTOEnumInteger modalidadeEnum){
        SessaoUsuario sessaoUsuario =  SessaoUsuario.getInstance();

        List<ParametroSimulacao> arrayConcursos;


        arrayConcursos = new ArrayList<ParametroSimulacao>();

        List<ParametroSimulacao> parametroSimulacaoList  = sessaoUsuario.getParametrosSimulacao().getParametros();
        for (ParametroSimulacao concurso : parametroSimulacaoList){
            if (concurso.getParametroJogo().getConcurso().getModalidadeDetalhada().getDescricao().equals( modalidadeEnum.getDescricao() )){
                arrayConcursos.add( concurso );
            }
        }

        if (arrayConcursos.size() > 1){
            Boolean temEspecialENormal = false;
            for (ParametroSimulacao modalidade : arrayConcursos){
                if(modalidade.getParametroJogo().getConcurso().getTipoConcurso() == TipoConcursoEnum.NORMAL) {
                    temEspecialENormal = true;
                    if (modalidade.getParametroJogo().getTeimosinhas().size() > 0) {
                        popupAdicionarCarrinhoFavoritas( arrayConcursos, idApostaFavorita );
                        return;
                    }
                } else {
                    if (temEspecialENormal) {
                        popupAdicionarCarrinhoFavoritas( arrayConcursos, idApostaFavorita );
                        return;
                    }
                }
            }
        }else{
            if(arrayConcursos.get( 0 ).getParametroJogo().getConcurso().getTipoConcurso() == TipoConcursoEnum.ESPECIAL) {
                adicionarCarrinhoCallWebservice( idApostaFavorita,2, 0 );
            } else {
                if (arrayConcursos.get( 0 ).getParametroJogo().getTeimosinhas().size() > 0) {
                    popupAdicionarCarrinhoFavoritas( arrayConcursos, idApostaFavorita );
                    return;
                }
            }
        }
    }

    private void popupAdicionarCarrinhoFavoritas(List<ParametroSimulacao> arrayConcursos, Long idApostaFavorita){
        android.support.v4.app.FragmentManager fm = ((FavoritasActivity)context).getSupportFragmentManager();
        AdicionarCarrinhoFavoritasView adicionarCarrinhoFavoritasView = AdicionarCarrinhoFavoritasView.newInstance();
        adicionarCarrinhoFavoritasView.arrayConcursos = arrayConcursos;
        adicionarCarrinhoFavoritasView.idApostaFavorita = idApostaFavorita;
        adicionarCarrinhoFavoritasView.show( fm, "adicionarCarrinhoFavoritasView" );
    }

}


