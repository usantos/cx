/**
 * Created by cedesbr450 on 18/12/17.
 */

package br.gov.caixa.loterias.apostar.interno.view.adapter;

        import android.content.Context;
        import android.support.v4.content.ContextCompat;
        import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.LinearLayout;
        import android.widget.TextView;

        import com.daimajia.swipe.SwipeLayout;

        import java.util.ArrayList;
        import java.util.Locale;

        import br.gov.caixa.loterias.apostar.interno.R;
        import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;

        import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ModalidadeEnum;
        import br.gov.caixa.loterias.apostar.interno.utils.EstiloModalidade;
        import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
        import br.gov.caixa.loterias.apostar.interno.controllers.ListaRapidaoActivity;

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
        View view = LayoutInflater.from(context).inflate(R.layout.row_apostas_list_view, parent, false);

        return  new ApostaHolder(view);
    }

    @Override
    public void onBindViewHolder(ApostaHolder holder, final int position) {
        EstiloModalidade estilo = new EstiloModalidade(apostas.get(position).getModalidade());

        final SwipeLayout swipeLayout = (SwipeLayout) holder.itemView;
        swipeLayout.setClickToClose(true);
        swipeLayout.getDragEdgeMap().clear();
        swipeLayout.addDrag(SwipeLayout.DragEdge.Left, swipeLayout.findViewById(R.id.dadosApostaCarrinho));
        swipeLayout.setDragEdge(SwipeLayout.DragEdge.Left);

        LinearLayout layoutTimeCoracao = holder.dadosApostaCarrinho.findViewById(R.id.layoutTimeCoracao);
        TextView labelTimeCoracao = holder.dadosApostaCarrinho.findViewById(R.id.labelTimeCoracaoTxt);
        TextView textViewTimeCoracao = holder.dadosApostaCarrinho.findViewById(R.id.textViewTimeCoracao);

        holder.textViewTipoAposta.setTextColor(ContextCompat.getColor(context, estilo.getCorClara()));
        holder.textViewNumeroConcurso.setTextColor(ContextCompat.getColor(context, estilo.getCorClara()));
        holder.valorAposta.setTextColor(ContextCompat.getColor(context, estilo.getCorEscura()));
        holder.textViewTipoAposta.setText(ViewUtils.capitalized(ModalidadeEnum.fromString(apostas.get(position).getModalidade())));
        holder.textViewNumeroConcurso.setText( context.getString(R.string.label_aposta_concurso, apostas.get( position ).getConcursoAlvo()));
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

        quantidadeApostadas.setText(String.format( Locale.getDefault(), "%d %s", quantidadeNumeros, quantidadeApostadasString));
        quantidadeSorteios.setText(String.format(Locale.getDefault(), "%d %s", quantidadeTeimosinhas, quantidadeSorteiosString));

        if (ModalidadeEnum.TIMEMANIA == apostas.get(position).getModalidade()) {
            layoutTimeCoracao.setVisibility(View.VISIBLE);
            textViewTimeCoracao.setText(apostas.get(position).getTimeDoCoracao().getNome());
        } else if (ModalidadeEnum.DIA_DE_SORTE == apostas.get(position).getModalidade()) {
            layoutTimeCoracao.setVisibility(View.VISIBLE);
            labelTimeCoracao.setText(R.string.label_mes_sorte);
            textViewTimeCoracao.setText(apostas.get(position).getMesDeSorte() != null && apostas.get(position).getMesDeSorte().getNome() != null
                    ? apostas.get(position).getMesDeSorte().getNome() : context.getString(R.string.label_tres_interrogacoes));
        }


        holder.itemView.findViewById(R.id.excluirApostaCarrinhoLayout).setOnClickListener(view -> onCarrinhoApostasRapidaoListener.onRemove( apostas.get( position ) ));

        StringBuilder numeroStr = new StringBuilder();
        if (apostas.get(position).getIndicadorSurpresinha().getValor() == 2) {
            for (int i = 0; i < apostas.get(position).getQuantidadeNumeros(); i++) {
                if (i == apostas.get(position).getQuantidadeNumeros() - 1) {
                    numeroStr.append("XX");
                }else {
                    numeroStr.append("XX - ");
                }
            }
        } else {
            for (Integer num: apostas.get(position).getNumerosSelecionados()) {
                if (num.equals(apostas.get(position).getNumerosSelecionados().get(apostas.get(position).getNumerosSelecionados().size() - 1))) {
                    numeroStr.append(String.format("%02d", num));
                }else {
                    numeroStr.append(String.format("%02d - ", num));
                }
            }
        }

        holder.valorAposta.setText(br.gov.caixa.loterias.apostar.interno.utils.ViewUtils.getMoedaFormat(apostas.get(position).getValor()));
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