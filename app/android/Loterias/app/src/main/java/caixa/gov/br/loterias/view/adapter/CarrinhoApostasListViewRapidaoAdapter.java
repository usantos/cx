/**
 * Created by cedesbr450 on 18/12/17.
 */

package caixa.gov.br.loterias.view.adapter;

        import android.content.Context;
        import android.graphics.Color;
        import android.support.annotation.ColorInt;
        import android.support.v4.content.ContextCompat;
        import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.TextView;

        import com.android.volley.VolleyError;

        import java.util.ArrayList;
        import caixa.gov.br.loterias.R;
        import caixa.gov.br.loterias.bo.ApostaBO;
        import caixa.gov.br.loterias.bo.silce.SilceClient;
        import caixa.gov.br.loterias.bo.silce.dto.CarrinhoDTOResponse;
        import caixa.gov.br.loterias.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
        import caixa.gov.br.loterias.bo.silce.dto.ModalidadeEnum;
        import caixa.gov.br.loterias.model.Modalidade;
        import caixa.gov.br.loterias.utils.EstiloModalidade;
        import caixa.gov.br.loterias.utils.LoadingViewLoterias;
        import caixa.gov.br.loterias.utils.MensagensNetwork;
        import caixa.gov.br.loterias.utils.ViewUtils;
        import caixa.gov.br.loterias.view.ListaRapidaoActivity;

public class CarrinhoApostasListViewRapidaoAdapter extends RecyclerView.Adapter<CarrinhoApostasListViewRapidaoAdapter.ApostaHolder> {
    private Context context;
    private ArrayList<IdentificaoDeUmaApostaDas8Modalidades> apostas;
    private ListaRapidaoActivity parentActivity;

    public CarrinhoApostasListViewRapidaoAdapter(ListaRapidaoActivity parentActivity, ArrayList<IdentificaoDeUmaApostaDas8Modalidades> apostas, Context context){
        this.context = context;
        this.apostas = apostas;
        this.parentActivity = parentActivity;
    }

    static class ApostaHolder extends RecyclerView.ViewHolder{
        final TextView textViewTipoAposta;
        final TextView numerosAposta;
        final TextView valorAposta;

        ApostaHolder(View view){
            super(view);

            textViewTipoAposta = view.findViewById(R.id.tipoAposta);
            numerosAposta = view.findViewById(R.id.numerosText);
            valorAposta = view.findViewById(R.id.valorAposta);
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

        holder.textViewTipoAposta.setTextColor(ContextCompat.getColor(context, estilo.getCorClara()));
        holder.valorAposta.setTextColor(ContextCompat.getColor(context, estilo.getCorEscura()));
        holder.textViewTipoAposta.setText(ViewUtils.capitalized(ModalidadeEnum.fromString(apostas.get(position).getModalidade())));

        holder.itemView.findViewById(R.id.excluirApostaCarrinho).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletarAposta(apostas.get(position).getId().toString());
            }
        });

        String numeroStr = "";
        if (apostas.get(position).getIndicadorSurpresinha().getValor() == 2) {
            for (int i = 0; i < apostas.get(position).getQuantidadeNumeros(); i++) {
                if (i == apostas.get(position).getQuantidadeNumeros() - 1) {
                    numeroStr += "XX";
                }else {
                    numeroStr += "XX - ";
                }
            }
        } else {
            for (Integer num: apostas.get(position).getNumerosSelecionados()) {
                if (num == (apostas.get(position).getNumerosSelecionados().get(apostas.get(position).getNumerosSelecionados().size() - 1))) {
                    numeroStr += String.format("%02d", num);
                }else {
                    numeroStr += String.format("%02d - ", num);
                }
            }
        }

        holder.numerosAposta.setText(numeroStr);
        holder.valorAposta.setText(caixa.gov.br.loterias.utils.ViewUtils.getMoedaFormat(apostas.get(position).getValor()));
    }

    private void deletarAposta (String idAposta) {
        final ApostaBO apostaBO = new ApostaBO(parentActivity);
        final View loadViewProgress = LoadingViewLoterias.init(parentActivity);

        apostaBO.deleteApostaCarrinho(idAposta, new SilceClient.Listener<CarrinhoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoDTOResponse response) {
                LoadingViewLoterias.stop(loadViewProgress);
                parentActivity.carrinho = response.getPayload();
                parentActivity.atualizaLayout();
            }
        }, new SilceClient.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LoadingViewLoterias.stop(loadViewProgress);
                ViewUtils.alert(parentActivity, MensagensNetwork.setMensagem(error, context));
            }
        });
    }

    @Override
    public int getItemCount() {
        return apostas.size();
    }

}