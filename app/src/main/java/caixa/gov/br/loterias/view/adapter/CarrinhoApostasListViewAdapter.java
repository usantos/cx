package caixa.gov.br.loterias.view.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.daimajia.swipe.SwipeLayout;

import java.util.ArrayList;
import java.util.Locale;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bean.Modalidade;
import caixa.gov.br.loterias.model.bo.ApostaBO;
import caixa.gov.br.loterias.model.bo.ApostaBO_;
import caixa.gov.br.loterias.model.bo.MensagensNetwork;
import caixa.gov.br.loterias.model.bo.RedirectNetwork;
import caixa.gov.br.loterias.model.bo.RequestListener;
import caixa.gov.br.loterias.model.bo.silce.dto.CarrinhoDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import caixa.gov.br.loterias.model.bo.silce.dto.ModalidadeEnum;
import caixa.gov.br.loterias.model.dao.DBLoteriasCrud;
import caixa.gov.br.loterias.utils.EstiloModalidade;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.CarrinhoActivity;
import caixa.gov.br.loterias.view.DadosPessoaisActivity;
import caixa.gov.br.loterias.view.animation.AnimacaoSwipeList;
import caixa.gov.br.loterias.view.custom.ExpandableHeightRecyclerView;
import caixa.gov.br.loterias.view.custom.LoadingViewLoterias;

public class CarrinhoApostasListViewAdapter extends RecyclerView.Adapter<CarrinhoApostasListViewAdapter.ApostaHolder> {
    private Context context;
    private ArrayList<IdentificaoDeUmaApostaDas8Modalidades> apostas;
    private CarrinhoActivity parentActivity;
    private boolean animacao;
    private static final String TAG = "TAG_CARRINHO";

    public CarrinhoApostasListViewAdapter(CarrinhoActivity parentActivity, ArrayList<IdentificaoDeUmaApostaDas8Modalidades> apostas, Context context, boolean animacao) {
        this.context = context;
        this.apostas = apostas;
        this.parentActivity = parentActivity;
        this.animacao = animacao;
    }


    static class ApostaHolder extends RecyclerView.ViewHolder {
        final TextView textViewTipoAposta;
        final TextView numerosAposta;
        final TextView valorAposta;
        final ExpandableHeightRecyclerView recycleViewDetalheAposta;
        final RelativeLayout layoutCabecalhoLoteca;
        final LinearLayout dadosApostaCarrinho;
        final RelativeLayout dadosCarrinho;
        final TextView textViewNumeroConcurso;

        ApostaHolder(View view) {
            super(view);

            textViewTipoAposta = view.findViewById(R.id.tipoAposta);
            numerosAposta = view.findViewById(R.id.numerosText);
            valorAposta = view.findViewById(R.id.valorAposta);
            recycleViewDetalheAposta = view.findViewById(R.id.recycleViewDetalheAposta);
            layoutCabecalhoLoteca = view.findViewById(R.id.layoutCabecalhoLoteca);
            dadosApostaCarrinho = view.findViewById(R.id.dadosApostaCarrinho);
            dadosCarrinho = view.findViewById(R.id.dadosCarrinho);
            textViewNumeroConcurso = view.findViewById(R.id.numeroConcurso);

        }
    }

    @Override
    public CarrinhoApostasListViewAdapter.ApostaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_apostas_list_view, parent, false);

        return new ApostaHolder(view);
    }

    @Override
    public void onBindViewHolder(ApostaHolder holder, final int position) {
        final IdentificaoDeUmaApostaDas8Modalidades aposta = apostas.get(position);
        EstiloModalidade estilo = new EstiloModalidade(aposta.getModalidade());

        final SwipeLayout swipeLayout = (SwipeLayout) holder.itemView;
        swipeLayout.setClickToClose(true);
        swipeLayout.getDragEdgeMap().clear();
        swipeLayout.addDrag(SwipeLayout.DragEdge.Left, swipeLayout.findViewById(R.id.dadosApostaCarrinho));
        swipeLayout.setDragEdge(SwipeLayout.DragEdge.Left);

        holder.textViewTipoAposta.setTextColor(ContextCompat.getColor(context, estilo.getCorClara()));
        holder.textViewNumeroConcurso.setTextColor(ContextCompat.getColor(context, estilo.getCorClara()));
        holder.valorAposta.setTextColor(ContextCompat.getColor(context, estilo.getCorEscura()));
        holder.textViewTipoAposta.setText(ViewUtils.capitalized(ModalidadeEnum.fromString(aposta.getModalidade())));
        holder.textViewNumeroConcurso.setText(context.getString(R.string.label_aposta_concurso, aposta.getConcursoAlvo()));
        holder.dadosApostaCarrinho.setBackgroundResource(estilo.getCorEscura());

        TextView quantidadeApostadas = holder.dadosApostaCarrinho.findViewById(R.id.quantidadeApostadas);
        TextView quantidadeSorteios = holder.dadosApostaCarrinho.findViewById(R.id.quantidadeSorteios);
        Button buttonApostaEspelho = holder.dadosApostaCarrinho.findViewById(R.id.buttonApostaEspelho);
        LinearLayout layoutTimeCoracao = holder.dadosApostaCarrinho.findViewById(R.id.layoutTimeCoracao);
        TextView labelTimeCoracao = holder.dadosApostaCarrinho.findViewById(R.id.labelTimeCoracaoTxt);
        TextView textViewTimeCoracao = holder.dadosApostaCarrinho.findViewById(R.id.textViewTimeCoracao);

        String quantidadeApostadasString = "";
        String quantidadeSorteiosString = "";

        int quantidadeNumeros = aposta.getQuantidadeNumeros() != null ? aposta.getQuantidadeNumeros() : 0;
        int quantidadeTeimosinhas = aposta.getQuantidadeTeimosinhas() == null || aposta.getQuantidadeTeimosinhas() == 0 ? 1 : aposta.getQuantidadeTeimosinhas();

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

        quantidadeApostadas.setText(String.format(Locale.getDefault(), "%d %s", quantidadeNumeros, quantidadeApostadasString));
        quantidadeSorteios.setText(String.format(Locale.getDefault(), "%d %s", quantidadeTeimosinhas, quantidadeSorteiosString));

        if (aposta.getEspelho()) {
            buttonApostaEspelho.setVisibility(View.VISIBLE);
        }

        holder.itemView.findViewById(R.id.excluirApostaCarrinho).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletarAposta(aposta);
            }
        });

        if (apostas.get(position).getPartidasLoteca() != null && !apostas.get(position).getPartidasLoteca().isEmpty()) {
            DetalheApostaLotecaAdapter detalheApostaLotecaAdapter = new DetalheApostaLotecaAdapter(apostas.get(position).getPartidasLoteca());
            holder.recycleViewDetalheAposta.setLayoutManager(new LinearLayoutManager(parentActivity));
            holder.recycleViewDetalheAposta.setAdapter(detalheApostaLotecaAdapter);
            holder.recycleViewDetalheAposta.setExpanded(true);
            animacao = Boolean.FALSE;
            swipeLayout.setSwipeEnabled(animacao);

            holder.layoutCabecalhoLoteca.setVisibility(View.VISIBLE);
        } else if (apostas.get(position).getPartidasLotogol() != null && !apostas.get(position).getPartidasLotogol().isEmpty()) {
            DetalheApostaLotogolAdapter detalheApostaLotogolAdapter = new DetalheApostaLotogolAdapter(apostas.get(position).getPartidasLotogol());
            holder.recycleViewDetalheAposta.setLayoutManager(new LinearLayoutManager(parentActivity));
            holder.recycleViewDetalheAposta.setAdapter(detalheApostaLotogolAdapter);
            holder.recycleViewDetalheAposta.setExpanded(true);

            holder.layoutCabecalhoLoteca.setVisibility(View.VISIBLE);
            holder.layoutCabecalhoLoteca.getLayoutParams().height = 0;
            RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            relativeLayoutParams.addRule(RelativeLayout.START_OF, R.id.valorAposta);
            relativeLayoutParams.addRule(RelativeLayout.BELOW, R.id.layoutCabecalhoLoteca);
            relativeLayoutParams.addRule(RelativeLayout.ALIGN_START, R.id.layoutCabecalhoLoteca);
            holder.recycleViewDetalheAposta.setLayoutParams(relativeLayoutParams);
            animacao = Boolean.FALSE;
            swipeLayout.setSwipeEnabled(animacao);
        }

        if (ModalidadeEnum.TIMEMANIA == aposta.getModalidade()) {
            layoutTimeCoracao.setVisibility(View.VISIBLE);
            textViewTimeCoracao.setText(aposta.getTimeDoCoracao().getNome());
        } else if (ModalidadeEnum.DIA_DE_SORTE == aposta.getModalidade()) {
            layoutTimeCoracao.setVisibility(View.VISIBLE);
            labelTimeCoracao.setText(R.string.label_mes_sorte);
            textViewTimeCoracao.setText(aposta.getMesDeSorte() != null ? aposta.getMesDeSorte().getNome() : "");
        }

        String numeroStr = "";
        if (aposta.getIndicadorSurpresinha().getValor() == 2) {
            for (int i = 0; i < aposta.getQuantidadeNumeros(); i++) {
                if (i == aposta.getQuantidadeNumeros() - 1) {
                    numeroStr += "XX";
                } else {
                    numeroStr += "XX - ";
                }
            }
        } else {
            for (Integer num : aposta.getNumerosSelecionados()) {
                if (num == (aposta.getNumerosSelecionados().get(aposta.getNumerosSelecionados().size() - 1))) {
                    numeroStr += String.format("%02d", num);
                } else {
                    numeroStr += String.format("%02d - ", num);
                }
            }
        }

        holder.numerosAposta.setText(numeroStr);
        ViewUtils.setMoedaFormatHtml(aposta.getValor(), holder.valorAposta);

        if (animacao) {
            AnimacaoSwipeList.animacaoSwipeList(context, position, swipeLayout);
        }
    }

    private void deletarAposta(final IdentificaoDeUmaApostaDas8Modalidades aposta) {

        ViewUtils.alertTitleButtonYesOrNotListener(context, context.getResources().getString(R.string.MA010), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ApostaBO apostaBO = ApostaBO_.getInstance_(parentActivity);
                final Dialog loadViewProgress = LoadingViewLoterias.show(parentActivity);
                apostaBO.deleteApostaCarrinho(aposta.getId().toString(), new RequestListener<CarrinhoDTOResponse>() {
                    @Override
                    public void onResponse(CarrinhoDTOResponse response) {
                        loadViewProgress.dismiss();
                        parentActivity.setCarrinho(response.getPayload());
                        deletarApostaBaseLocal(aposta, Boolean.FALSE);
                        parentActivity.atualizaLayout(Boolean.FALSE);
                        if (response.getRedirect() != null) {
                            RedirectNetwork.checkRedirectSucesso(response.getRedirect(), parentActivity);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadViewProgress.dismiss();
                        if (error.networkResponse != null && error.networkResponse.statusCode != 401) {
                            RedirectNetwork.checkRedirect(error, parentActivity);

                        } else {
                            deletarApostaBaseLocal(aposta, Boolean.TRUE);
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return apostas.size();
    }

    private void deletarApostaBaseLocal(IdentificaoDeUmaApostaDas8Modalidades aposta, boolean local) {
        DBLoteriasCrud crud = new DBLoteriasCrud(context);
        crud.deleteIdentificaoDeUmaApostaDas8Modalidades(aposta);
        if (local) {
            parentActivity.lerCarrinhoLocal(Boolean.FALSE);
        }
    }


}