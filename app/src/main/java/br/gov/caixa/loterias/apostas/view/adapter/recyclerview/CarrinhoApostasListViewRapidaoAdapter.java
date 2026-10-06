/**
 * Created by cedesbr450 on 18/12/17.
 */

package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.ListaRapidaoActivity;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IndicadorSurpresinha;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;

public class CarrinhoApostasListViewRapidaoAdapter extends RecyclerView.Adapter<CarrinhoApostasListViewRapidaoAdapter.ApostaHolder> {
    private Context context;
    private ArrayList<IdentificaoDeUmaApostaDas8Modalidades> apostas;
    private ListaRapidaoActivity parentActivity;

    private OnCarrinhoApostasRapidaoListener onCarrinhoApostasRapidaoListener;

    public CarrinhoApostasListViewRapidaoAdapter(ListaRapidaoActivity parentActivity,
                                                 ArrayList<IdentificaoDeUmaApostaDas8Modalidades> apostas,
                                                 Context context) {
        this(parentActivity, apostas, context, null);
    }

    public CarrinhoApostasListViewRapidaoAdapter(ListaRapidaoActivity parentActivity,
                                                 ArrayList<IdentificaoDeUmaApostaDas8Modalidades> apostas,
                                                 Context context, OnCarrinhoApostasRapidaoListener listener){
        this.context = context;
        this.apostas = apostas;
        this.parentActivity = parentActivity;
        this.onCarrinhoApostasRapidaoListener = listener;
    }

    static class ApostaHolder extends RecyclerView.ViewHolder{
        final TextView textViewTipoAposta;
        final TextView textViewNumeroConcurso;
        final TextView valorAposta;
        final LinearLayout dadosApostaCarrinho;

        ApostaHolder(View view){
            super(view);

            textViewTipoAposta = view.findViewById(R.id.tipoAposta);
            textViewNumeroConcurso = view.findViewById(R.id.numeroConcurso);
            valorAposta = view.findViewById(R.id.valorAposta);
            dadosApostaCarrinho = view.findViewById(R.id.dadosApostaCarrinho);

        }
    }

    @Override
    public CarrinhoApostasListViewRapidaoAdapter.ApostaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_apostas_list_view_rapidao, parent, false);

        return  new ApostaHolder(view);
    }

    @Override
    public void onBindViewHolder(ApostaHolder holder, final int position) {
        EstiloModalidadeMKP estilo = new EstiloModalidadeMKP(apostas.get(position).getModalidade());

        final SwipeLayout swipeLayout = (SwipeLayout) holder.itemView;
        swipeLayout.setClickToClose(true);
        swipeLayout.getDragEdgeMap().clear();
        swipeLayout.addDrag(SwipeLayout.DragEdge.Left, swipeLayout.findViewById(R.id.dadosApostaCarrinho));
        swipeLayout.setDragEdge(SwipeLayout.DragEdge.Left);

        LinearLayout layoutTimeCoracao = holder.dadosApostaCarrinho.findViewById(R.id.layoutTimeCoracao);
        TextView labelTimeCoracao = holder.dadosApostaCarrinho.findViewById(R.id.labelTimeCoracaoTxt);
        TextView textViewTimeCoracao = holder.dadosApostaCarrinho.findViewById(R.id.textViewTimeCoracao);

        holder.textViewTipoAposta.setTextColor(ContextCompat.getColor(context, estilo.getCorLetraLista()));
        holder.textViewTipoAposta.setText(ModalidadeEnum.fromString(apostas.get(position).getModalidade()).toLowerCase());
        holder.textViewNumeroConcurso.setTextColor(ContextCompat.getColor(context, estilo.getCorLetraLista()));
        holder.textViewNumeroConcurso.setText(context.getString(R.string.label_aposta_concurso, apostas.get( position ).getConcursoAlvo()));
        holder.valorAposta.setTextColor(ContextCompat.getColor(context, estilo.getCorLetraLista()));
        holder.dadosApostaCarrinho.setBackgroundResource(estilo.getCorEscura());

        TextView quantidadeApostadas = holder.dadosApostaCarrinho.findViewById(R.id.quantidadeApostadas);
        TextView quantidadeSorteios = holder.dadosApostaCarrinho.findViewById(R.id.quantidadeSorteios);


        String quantidadeApostadasString = "";
        String quantidadeSorteiosString = "";

        int quantidadeNumeros = apostas.get(position).getQuantidadeNumeros() != null ? apostas.get(position).getQuantidadeNumeros() : 0;
        int quantidadeTeimosinhas = apostas.get(position).getQuantidadeTeimosinhas() == null || apostas.get(position).getQuantidadeTeimosinhas() == 0 ? 1 : apostas.get(position).getQuantidadeTeimosinhas();

        if (quantidadeNumeros > 1) {
            quantidadeApostadasString = context.getString(R.string.label_numeros);
        } else {
            quantidadeApostadasString = context.getString(R.string.label_numero);
        }

        if (quantidadeTeimosinhas > 1) {
            quantidadeSorteiosString = context.getString(R.string.label_sorteios);
        } else {
            quantidadeSorteiosString = context.getString(R.string.label_sorteio);
        }

        quantidadeApostadas.setText(String.format( Locale.getDefault(), context.getResources().getString(R.string.percent_d_percent_s), quantidadeNumeros, quantidadeApostadasString));
        quantidadeSorteios.setText(String.format(Locale.getDefault(), context.getResources().getString(R.string.percent_d_percent_s), quantidadeTeimosinhas, quantidadeSorteiosString));
        if(apostas != null && apostas.get(position) != null && apostas.get(position).getModalidade() != null){
            if (ModalidadeEnum.TIMEMANIA == apostas.get(position).getModalidade()) {
                layoutTimeCoracao.setVisibility(View.VISIBLE);
                    if (apostas.get(position).getIndicadorSurpresinha() != null &&
                            apostas.get(position).getIndicadorSurpresinha().getValor() != null) {

                            textViewTimeCoracao.setText(apostas.get(position).getTimeDoCoracao().getNome());
                            if (apostas.get(position).getIndicadorSurpresinha().getValor() == IndicadorSurpresinha.SURPRESINHA) {
                                textViewTimeCoracao.setText(context.getString(R.string.label_tres_interrogacoes));
                            }
                    }
                }
            } else if (ModalidadeEnum.DIA_DE_SORTE == apostas.get(position).getModalidade()) {
                layoutTimeCoracao.setVisibility(View.VISIBLE);
                labelTimeCoracao.setText(R.string.label_mes_sorte);
                textViewTimeCoracao.setText(apostas.get(position).getMesDeSorte() != null && apostas.get(position).getMesDeSorte().getNome() != null
                                                    ? apostas.get(position).getMesDeSorte().getNome() : context.getString(R.string.label_tres_interrogacoes));
        }

        //holder.itemView.findViewById(R.id.excluirApostaCarrinhoLayout).setOnClickListener(view -> onCarrinhoApostasRapidaoListener.onRemove( apostas.get( position ) ));

        StringBuilder numeroStr = new StringBuilder();

        if (    apostas != null &&
                apostas.get(position) != null &&
                apostas.get(position).getIndicadorSurpresinha() != null &&
                apostas.get(position).getIndicadorSurpresinha().getValor() != null) {

            switch (apostas.get(position).getIndicadorSurpresinha().getValor()) {
                case IndicadorSurpresinha.NAO_SURPRESINHA:
                    if (apostas.get(position).getModalidade() == ModalidadeEnum.SUPER_7) {

                    } else {
                        IdentificaoDeUmaApostaDas8Modalidades aposta = (IdentificaoDeUmaApostaDas8Modalidades<List<Integer>>)apostas.get(position);
                        if(aposta != null && aposta.getModalidade() != null){
                            if (aposta.getModalidade() == ModalidadeEnum.SUPER_7) {
                                IdentificaoDeUmaApostaDas8Modalidades<List<List<Integer>>> apostaSuperSete = aposta;
                                ArrayList<ArrayList<Integer>> listaApostas  = apostaSuperSete.getMatrizNumerosSelecionados();
                                for (List<Integer> coluna: listaApostas) {
                                    for(Integer linha : coluna) {
                                        numeroStr.append(String.format(context.getResources().getString(R.string.percent_d), linha));
                                    }
                                    numeroStr.append(String.format(context.getResources().getString(R.string.barra)));
                                }

                            } else {
                                IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> apostaNumerica = aposta;
                                if (apostaNumerica.getListaNumerosSelecionados() != null) {
                                    for (Integer num : apostaNumerica.getListaNumerosSelecionados()) {
                                        if (num.equals(apostaNumerica.getNumerosSelecionados().get(apostaNumerica.getListaNumerosSelecionados().size() - 1))) {
                                            numeroStr.append(String.format(context.getResources().getString(R.string.percent_zero_dois_d), num));
                                        } else {
                                            numeroStr.append(String.format(context.getResources().getString(R.string.percent_zero_dois_d_traco), num));
                                        }
                                    }
                                }
                            }
                        }
                    }

                    break;
                case IndicadorSurpresinha.SURPRESINHA:
                case IndicadorSurpresinha.SURPRESINHA_NUMERICA:
                    for (int i = 0; i < apostas.get(position).getQuantidadeNumeros(); i++) {
                        if (i == apostas.get(position).getQuantidadeNumeros() - 1) {
                            numeroStr.append(context.getResources().getString(R.string.x_x));
                        } else {
                            numeroStr.append(context.getResources().getString(R.string.x_x_traco));
                        }
                    }
                    break;

                    default:
                    break;
            }
        }

        holder.valorAposta.setText(br.gov.caixa.loterias.apostas.utils.ViewUtils.getMoedaFormat(apostas.get(position).getValor()));
    }

    public interface OnCarrinhoApostasRapidaoListener {
        void onRemove(IdentificaoDeUmaApostaDas8Modalidades apostaDas8Modalidades);
    }

    public OnCarrinhoApostasRapidaoListener getOnCarrinhoApostasRapidaoListener() {
        return onCarrinhoApostasRapidaoListener;
    }

    public void setOnCarrinhoApostasRapidaoListener(OnCarrinhoApostasRapidaoListener onCarrinhoApostasRapidaoListener) {
        this.onCarrinhoApostasRapidaoListener = onCarrinhoApostasRapidaoListener;
    }

    @Override
    public int getItemCount() {
        return apostas.size();
    }



}