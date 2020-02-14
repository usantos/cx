package br.gov.caixa.loterias.apostar.interno.view.adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.NumerosSuperSeteFragment;
import br.gov.caixa.loterias.apostar.interno.controllers.CarrinhoActivity;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.IndicadorSurpresinha;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostar.interno.model.dao.DBLoteriasCrud;
import br.gov.caixa.loterias.apostar.interno.utils.EstiloModalidade;
import br.gov.caixa.loterias.apostar.interno.utils.Utils;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.animation.AnimacaoSwipeList;
import br.gov.caixa.loterias.apostar.interno.view.custom.ExpandableHeightRecyclerView;
import br.gov.caixa.loterias.apostar.interno.view.custom.LoadingViewLoterias;

public class CarrinhoApostasListViewAdapter extends RecyclerView.Adapter<CarrinhoApostasListViewAdapter.ApostaHolder> {
    private Context context;
    private ArrayList<IdentificaoDeUmaApostaDas8Modalidades> apostas;
    private CarrinhoActivity parentActivity;
    private NumerosSuperSeteFragment fragment;
    private boolean animacao;
    private static final String TAG = "TAG_CARRINHO";
    private static final AtomicLong TS = new AtomicLong();

    public CarrinhoApostasListViewAdapter(CarrinhoActivity parentActivity, ArrayList<IdentificaoDeUmaApostaDas8Modalidades> apostas, Context context, boolean animacao) {
        this.context = context;
        this.apostas = apostas;
        this.parentActivity = parentActivity;
        this.animacao = animacao;
        this.setHasStableIds(true);
    }


    static class ApostaHolder extends RecyclerView.ViewHolder {
        final TextView textViewTipoAposta;
        final TextView numerosAposta;
        final TextView valorAposta;
        final ExpandableHeightRecyclerView recycleViewDetalheAposta;
        final RelativeLayout containerFragment;
        final RelativeLayout layoutCabecalhoLoteca;
        final LinearLayout dadosApostaCarrinho;
        final LinearLayout dadosCarrinho;
        final TextView textViewNumeroConcurso, tvNumeroConcursoNovo;

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
            tvNumeroConcursoNovo = view.findViewById(R.id.tv_numero_concurso_novo);
            containerFragment = view.findViewById(R.id.rl_container_fragment);

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

        if(position >= 4){
            animacao = false;
        }

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

        holder.itemView.findViewById(R.id.excluirApostaCarrinho).setOnClickListener(view -> deletarAposta(aposta));

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
            if (aposta != null && aposta.getIndicadorSurpresinha() != null && aposta.getIndicadorSurpresinha().getValor() != null) {
                textViewTimeCoracao.setText(aposta.getTimeDoCoracao().getNome());
                 if(aposta.getIndicadorSurpresinha().getValor() == IndicadorSurpresinha.SURPRESINHA){
                    textViewTimeCoracao.setText("???");
                }
            }
        } else if (ModalidadeEnum.DIA_DE_SORTE == aposta.getModalidade()) {
            layoutTimeCoracao.setVisibility(View.VISIBLE);
            labelTimeCoracao.setText(R.string.label_mes_sorte);
            textViewTimeCoracao.setText(aposta.getMesDeSorte() != null && aposta.getMesDeSorte().getNome() != null ? aposta.getMesDeSorte().getNome() : context.getString(R.string.label_tres_interrogacoes));
        }

        StringBuilder numeroStr = new StringBuilder();
        if (aposta != null && aposta.getIndicadorSurpresinha() != null && aposta.getIndicadorSurpresinha().getValor() != null) {
            switch (aposta.getIndicadorSurpresinha().getValor()) {
                case IndicadorSurpresinha.NAO_SURPRESINHA:
                    if (aposta.getModalidade() == ModalidadeEnum.SUPER_7) {
                        configuraFragment(aposta,holder);
                        holder.tvNumeroConcursoNovo.setVisibility(View.VISIBLE);
                        holder.textViewNumeroConcurso.setVisibility(View.GONE);
                        holder.tvNumeroConcursoNovo.setTextColor(ContextCompat.getColor(context, estilo.getCorClara()));
                        holder.tvNumeroConcursoNovo.setText(context.getString(R.string.label_aposta_concurso, aposta.getConcursoAlvo()));
                    } else {
                        IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> apostaNumerica = aposta;
                        if (apostaNumerica.getListaNumerosSelecionados() != null) {
                            for (Integer num : apostaNumerica.getListaNumerosSelecionados()) {
                                if (num.equals(apostaNumerica.getNumerosSelecionados().get(apostaNumerica.getListaNumerosSelecionados().size() - 1))) {
                                    numeroStr.append(String.format("%02d", num));
                                } else {
                                    numeroStr.append(String.format("%02d - ", num));
                                }
                            }
                        }
                    }

                    break;
                case IndicadorSurpresinha.SURPRESINHA:
                case IndicadorSurpresinha.SURPRESINHA_NUMERICA:
                    for (int i = 0; i < aposta.getQuantidadeNumeros(); i++) {
                        if (i == aposta.getQuantidadeNumeros() - 1) {
                            numeroStr.append("XX");
                        } else {
                            numeroStr.append("XX - ");
                        }
                    }
                    break;
                    default:
                        break;
            }
        }

        holder.numerosAposta.setText(numeroStr.toString());
        ViewUtils.setMoedaFormatHtml(aposta.getValor(), holder.valorAposta);

        if (animacao) {
            AnimacaoSwipeList.animacaoSwipeList(context, position, swipeLayout);
        }
    }

    private void configuraFragment(IdentificaoDeUmaApostaDas8Modalidades aposta, ApostaHolder holder) {
        FragmentManager     fm                  = parentActivity.getSupportFragmentManager();
        int containerId = holder.containerFragment.getId();
        Fragment oldFragment = fm.findFragmentById(containerId);
        if(oldFragment != null) {
            fm.beginTransaction().remove(oldFragment).commit();
        }

        int newContainerId = (int) Utils.getUniqueTimestamp(TS);
        holder.containerFragment.setId(newContainerId);

        NumerosSuperSeteFragment fragment = NumerosSuperSeteFragment.newInstance(aposta.getMatrizNumerosSelecionados());
        fm.beginTransaction().replace(newContainerId, fragment).commit();

    }


    private void deletarAposta(final IdentificaoDeUmaApostaDas8Modalidades aposta) {

        ViewUtils.alertTitleButtonYesOrNotListener(context, context.getResources().getString(R.string.MA010), (dialogInterface, i) -> {
            int posDeletado = apostas.indexOf(aposta);
            ApostaBO apostaBO = ApostaBO_.getInstance_(parentActivity);
            final Dialog loadViewProgress = LoadingViewLoterias.show(parentActivity);
            apostaBO.deleteApostaCarrinho(aposta.getId().toString(), new RequestListener<CarrinhoDTOResponse>() {
                @Override
                public void onResponse(CarrinhoDTOResponse response) {
                    loadViewProgress.dismiss();
                    parentActivity.setCarrinho(response.getPayload());
                    deletarApostaBaseLocal(aposta, Boolean.FALSE);
                    parentActivity.deletaAposta(posDeletado, aposta);
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
                        parentActivity.deletaAposta(posDeletado, aposta);
                    }
                }
            });
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