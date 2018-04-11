package caixa.gov.br.loterias.view.adapter;

import android.animation.Animator;
import android.app.Activity;
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

import java.util.Collections;
import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.ApostaBO;
import caixa.gov.br.loterias.model.bo.ApostaBO_;
import caixa.gov.br.loterias.model.bo.RequestListener;
import caixa.gov.br.loterias.model.bo.silce.dto.ApostaFavoritaDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.CarrinhoDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.IncluirApostaFavoritaDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.RetornoPadraoDTO;
import caixa.gov.br.loterias.model.bo.MensagensNetwork;
import caixa.gov.br.loterias.utils.ViewUtils;
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

        final HorizontalScrollView horizontalScrollViewFavoritasItem = (HorizontalScrollView) holder.itemView.findViewById(R.id.HorizontalScrollViewFavoritasItem);

        final EditText EditTextNomeDaAposta = (EditText) holder.itemView.findViewById(R.id.EditTextNomeDaAposta);

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

        editarButtonFavoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translatePosicaoVoltarCard(relativeLayoutContentFavoritas,
                        ViewUtils.setDisplayMetric(
                                ViewUtils.getAppUsableScreenSize(context).x/2 -
                                        ViewUtils.setDisplayMetric(40,
                                                context) , context));
                EditTextNomeDaAposta.setEnabled(true);
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
                adicionarCarrinhoCallWebservice(position);
            }
        } );

        buttonSalvarNomeAposta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alterarFavoritaCallWebservice(listaApostaFavorita.get(position),
                        relativeLayoutContentFavoritas,
                        EditTextNomeDaAposta,
                        buttonSalvarNomeAposta,
                        tituloNomeApostaFavorita);
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
                            EditTextNomeDaAposta.setEnabled(true);
                            buttonSalvarNomeAposta.setEnabled(true);
                        }else{
                            if (cancelarAnimacao != true){
                                translatePosicaoVoltarCard(relativeLayoutContentFavoritas,
                                        ViewUtils.setDisplayMetric(0, context));
                                EditTextNomeDaAposta.setEnabled(false);
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
        protected TextView tituloApostaFavorira;
        protected TextView numerosApostaFavorita;

        public ViewHolder(final View itemView) {
            super(itemView);
            relativeLayoutContentFavoritas = (RelativeLayout) itemView.findViewById(R.id.RelativeLayoutContentFavoritas);
            relativeLayoutContentAlterarFavoritas = (RelativeLayout) itemView.findViewById(R.id.RelativeLayoutContentAlterarFavoritas);
            tituloApostaFavorira = (TextView) itemView.findViewById(R.id.tituloApostaFavorira);
            numerosApostaFavorita = (TextView) itemView.findViewById(R.id.numerosApostaFavorita);
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
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                ViewUtils.alert(activity, MensagensNetwork.setMensagem(error, context));

            }
        });
    }


    private void adicionarCarrinhoCallWebservice(final int position) {
        final AlertDialog loadViewProgress = LoadingViewLoterias.show(activity);
        final ApostaBO apostaBO = ApostaBO_.getInstance_(activity);

        apostaBO.postIncluirApostaFavoritaCarrinho(listaApostaFavorita.get(position).getId(), new RequestListener<CarrinhoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoDTOResponse response) {
                loadViewProgress.dismiss();
                ViewUtils.alert( context, context.getResources().getString( R.string.label_aposta_salva_carinha ) );
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                ViewUtils.alert(activity, MensagensNetwork.setMensagem(error, context));

            }
        });
    }

    private void alterarFavoritaCallWebservice(final ApostaFavoritaDTO apostaFavoritaDTO,
                                               final RelativeLayout favoritasRelativeLayout,
                                               final EditText editTextNomeDaAposta,
                                               final Button buttonSalvarNomeAposta,
                                               final TextView nomeApostaTextView) {
        final AlertDialog loadViewProgress = LoadingViewLoterias.show(activity);
        final ApostaBO apostaBO = ApostaBO_.getInstance_(activity);
        final ApostaFavoritaDTO apostaFavoritaDTOSalvar = apostaFavoritaDTO;
        apostaFavoritaDTOSalvar.setNome(editTextNomeDaAposta.getText().toString());
        apostaBO.postApostasFavoritas(apostaFavoritaDTOSalvar, new RequestListener<RetornoPadraoDTO>() {
            @Override
            public void onResponse(RetornoPadraoDTO response) {
                apostaFavoritaDTOSalvar.setNome(editTextNomeDaAposta.getText().toString());
                nomeApostaTextView.setText(" | "+ apostaFavoritaDTO.getNome());
                translatePosicaoVoltarCard(favoritasRelativeLayout,
                        ViewUtils.setDisplayMetric(0, context));
                editTextNomeDaAposta.setText("");
                editTextNomeDaAposta.setEnabled(false);
                buttonSalvarNomeAposta.setEnabled(false);
                loadViewProgress.dismiss();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                ViewUtils.alert(activity, MensagensNetwork.setMensagem(error, context));

            }
        });
    }

}


