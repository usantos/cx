package caixa.gov.br.loterias.view.adapter;

import android.content.Context;
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
import caixa.gov.br.loterias.utils.EstiloModalidade;
import caixa.gov.br.loterias.view.custom.LoadingViewLoterias;
import caixa.gov.br.loterias.utils.MensagensNetwork;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.CarrinhoActivity;

public class CarrinhoApostasListViewAdapter extends RecyclerView.Adapter<CarrinhoApostasListViewAdapter.ApostaHolder> {
    private Context context;
    private ArrayList<IdentificaoDeUmaApostaDas8Modalidades> apostas;
    private CarrinhoActivity parentActivity;

    public CarrinhoApostasListViewAdapter(CarrinhoActivity parentActivity, ArrayList<IdentificaoDeUmaApostaDas8Modalidades> apostas, Context context){
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
    public CarrinhoApostasListViewAdapter.ApostaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_apostas_list_view, parent, false);

        return  new ApostaHolder(view);
    }

    @Override
    public void onBindViewHolder(ApostaHolder holder, final int position) {
        final IdentificaoDeUmaApostaDas8Modalidades aposta = apostas.get(position);
        EstiloModalidade estilo = new EstiloModalidade(aposta.getModalidade());

        holder.textViewTipoAposta.setTextColor(ContextCompat.getColor(context, estilo.getCorClara()));
        holder.valorAposta.setTextColor(ContextCompat.getColor(context, estilo.getCorEscura()));
        holder.textViewTipoAposta.setText(ViewUtils.capitalized(ModalidadeEnum.fromString(aposta.getModalidade())));

        holder.itemView.findViewById(R.id.excluirApostaCarrinho).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletarAposta(aposta.getId().toString());
            }
        });

        String numeroStr = "";
        if (aposta.getIndicadorSurpresinha().getValor() == 2) {
            for (int i = 0; i < aposta.getQuantidadeNumeros(); i++) {
                if (i == aposta.getQuantidadeNumeros() - 1) {
                    numeroStr += "XX";
                }else {
                    numeroStr += "XX - ";
                }
            }
        } else {
            for (Integer num: aposta.getNumerosSelecionados()) {
                if (num == (aposta.getNumerosSelecionados().get(aposta.getNumerosSelecionados().size() - 1))) {
                    numeroStr += String.format("%02d", num);
                }else {
                    numeroStr += String.format("%02d - ", num);
                }
            }
        }

        holder.numerosAposta.setText(numeroStr);

        ViewUtils.setMoedaFormatHtml(aposta.getValor(), holder.valorAposta);

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