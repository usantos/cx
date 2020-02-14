package br.gov.caixa.loterias.apostar.interno.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.SuperSeteResultadoFragment;
import br.gov.caixa.loterias.apostar.interno.controllers.FavoritasActivity;
import br.gov.caixa.loterias.apostar.interno.model.bean.ApostaFavorita;
import br.gov.caixa.loterias.apostar.interno.model.bean.Modalidade;
import br.gov.caixa.loterias.apostar.interno.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.DTOEnumInteger;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.RetornoPadraoDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.TipoConcursoEnum;
import br.gov.caixa.loterias.apostar.interno.utils.AppUtils;
import br.gov.caixa.loterias.apostar.interno.utils.DialogUtils;
import br.gov.caixa.loterias.apostar.interno.utils.EstiloModalidade;
import br.gov.caixa.loterias.apostar.interno.utils.FirebaseManager;
import br.gov.caixa.loterias.apostar.interno.utils.Utils;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.custom.AdicionarCarrinhoFavoritasView;
import br.gov.caixa.loterias.apostar.interno.view.custom.LoadingViewLoterias;

/**
 * Created by cedesbr450 on 16/02/18.
 */
public final class FavoritasRecyclerViewAdapter extends RecyclerView.Adapter<FavoritasRecyclerViewAdapter.ViewHolder> {

    private final Context context;
    private final FavoritasActivity activity;
    private final List<ApostaFavoritaDTO> listaApostaFavorita;
    private ViewTreeObserver.OnGlobalLayoutListener swipeGlobalLayoutListener;
    private ExcluiuTudo excluiuTudo;
    private static final AtomicLong TS = new AtomicLong();

    public FavoritasRecyclerViewAdapter(final FavoritasActivity activity, final Context context, final List<ApostaFavoritaDTO> listaApostaFavorita) {
        this.context = context;
        this.listaApostaFavorita = listaApostaFavorita;
        this.activity = activity;
    }

    public void setOnExcluiuTudo(ExcluiuTudo excluiuTudo) {
        this.excluiuTudo = excluiuTudo;
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
        final SwipeLayout swipeLayout = (SwipeLayout) holder.itemView;
        swipeLayout.setClickToClose(true);
        swipeLayout.getDragEdgeMap().clear();
        swipeLayout.addDrag(SwipeLayout.DragEdge.Left, swipeLayout.findViewById(R.id.dadosApostaCarrinho));
        swipeLayout.setDragEdge(SwipeLayout.DragEdge.Left);
        swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {
                listaApostaFavorita.get(position).setOpenSwipeLayout(true);
            }

            @Override
            public void onStartClose(SwipeLayout layout) {
                holder.relativeLayoutContentAlterarFavoritasEditar.setVisibility(View.VISIBLE);
                holder.linearLayoutLayoutContentAlterarFavoritasSalvo.setVisibility(View.GONE);
                listaApostaFavorita.get(position).setOpenSwipeLayout(false);
            }
        });

        swipeGlobalLayoutListener = () -> {
            if (listaApostaFavorita.size() > 0 && position < listaApostaFavorita.size()) {
                if (listaApostaFavorita.get(position).getOpenSwipeLayout() == null) {
                    listaApostaFavorita.get(position).setOpenSwipeLayout(false);
                } else {
                    if (listaApostaFavorita.get(position).getOpenSwipeLayout()) {
                        // Opens the layout without animation
                        swipeLayout.open(false);
                    }
                }
            }
        };

        swipeLayout.getViewTreeObserver().addOnGlobalLayoutListener(swipeGlobalLayoutListener);

        EstiloModalidade estilo = new EstiloModalidade(ModalidadeEnum.toString(listaApostaFavorita.get(position).getModalidade().getDescricao().toLowerCase(new Locale("pt", "BR"))));
        holder.tituloApostaFavorita.setText(listaApostaFavorita.get(position).getModalidade().getDescricao());
        holder.tituloApostaFavorita.setTextColor(context.getResources().getColor(estilo.getCorClara()));
        holder.tituloNomeApostaFavorita.setText(" | " + listaApostaFavorita.get(position).getNome());
        StringBuilder numeroStr = new StringBuilder();
        if(listaApostaFavorita.get(position).getModalidade().getValor().intValue() == 7) {
            configuraFragment(listaApostaFavorita.get(position),holder);
            holder.containerFragment.setVisibility(View.VISIBLE);
        } else {
            List<Integer> numSelecionados = listaApostaFavorita.get(position).getListaNumerosSelecionados();
            for (Integer num : numSelecionados) {
                if (num.equals(numSelecionados.get(listaApostaFavorita.get(position).getListaNumerosSelecionados().size() - 1))) {
                    numeroStr.append(String.format("%02d", num));
                } else {
                    numeroStr.append(String.format("%02d - ", num));
                }
            }
        }

        holder.numerosApostaFavorita.setText(numeroStr.toString());
        holder.editarRelativeLayoutFavoritos.setOnClickListener(v -> swipeLayout.open(true));

        holder.exluirRelativeLayoutFavoritos.setOnClickListener(v -> deleteFavoritaCallWebservice(position));

        holder.editarRelativeLayoutAddCarrinho.setOnClickListener(view -> {
            adicionarApostaCarrinhoRequest(ApostaBO_.getInstance_(activity), listaApostaFavorita.get(position));
        });

        holder.layoutSalvarNomeAposta.setOnClickListener(v -> {
            if (holder.editTextNomeDaAposta.getText().toString().equals("")) {
                ViewUtils.alertTitleButton(context, R.string.label_atencao,
                        context.getResources().getString(R.string.MA002) + " nome da aposta",
                        context.getResources().getString(R.string.ok));
            } else {
                AppUtils.fechaTeclado(activity);
                alterarFavoritaCallWebservice(listaApostaFavorita.get(position), holder);
            }
        });

        boolean isTimeCoracao = listaApostaFavorita.get(position).getTimeDoCoracao() != null && StringUtils.isNotEmpty(listaApostaFavorita.get(position).getTimeDoCoracao().getNome());
        boolean isMesSorte = listaApostaFavorita.get(position).getMesDeSorte() != null &&StringUtils.isNotEmpty(listaApostaFavorita.get(position).getMesDeSorte().getNome());
        if (isTimeCoracao || isMesSorte) {
            holder.maisInformacoesLayout.setVisibility(View.VISIBLE);
            holder.labelInformacaoText.setText(isTimeCoracao ? R.string.label_time_coracao_dois_pontos : R.string.label_mes_sorte_dois_pontos);
            holder.informacaoText.setText(isTimeCoracao ? listaApostaFavorita.get(position).getTimeDoCoracao().getNome() : listaApostaFavorita.get(position).getMesDeSorte().getNome());
        }else{
            holder.maisInformacoesLayout.setVisibility(View.GONE);
        }
    }


    private void configuraFragment(ApostaFavoritaDTO aposta, ViewHolder holder) {
        FragmentManager fm          = activity.getSupportFragmentManager();
        int             containerId = holder.containerFragment.getId();
        Fragment        oldFragment = fm.findFragmentById(containerId);
        if(oldFragment != null) {
            fm.beginTransaction().remove(oldFragment).commit();
        }

        int newContainerId = (int) Utils.getUniqueTimestamp(TS);
        holder.containerFragment.setId(newContainerId);

        SuperSeteResultadoFragment fragment = SuperSeteResultadoFragment.newInstance(aposta.getMatrizNumerosSelecionados());
        fm.beginTransaction().replace(newContainerId, fragment).commit();

    }

    @Override
    public int getItemCount() {
        return listaApostaFavorita.size();
    }

    public List<ApostaFavoritaDTO> getCompras() {
        return Collections.unmodifiableList(listaApostaFavorita);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView tituloApostaFavorita;
        protected TextView tituloNomeApostaFavorita;
        protected TextView numerosApostaFavorita;
        protected TextView nomeApostaSalvoTextView;
        protected RelativeLayout editarRelativeLayoutAddCarrinho;
        protected RelativeLayout editarRelativeLayoutFavoritos;
        protected RelativeLayout exluirRelativeLayoutFavoritos;
        protected RelativeLayout containerFragment;
        protected LinearLayout linearLayoutLayoutContentAlterarFavoritasSalvo;
        protected RelativeLayout relativeLayoutContentAlterarFavoritasEditar;
        protected EditText editTextNomeDaAposta;
        protected RelativeLayout layoutSalvarNomeAposta;
        protected LinearLayout maisInformacoesLayout;
        protected TextView labelInformacaoText;
        protected TextView informacaoText;

        public ViewHolder(final View itemView) {
            super(itemView);
            tituloApostaFavorita = itemView.findViewById(R.id.tituloApostaFavorita);
            tituloNomeApostaFavorita = itemView.findViewById(R.id.tituloNomeApostaFavorita);
            numerosApostaFavorita = itemView.findViewById(R.id.numerosApostaFavorita);
            nomeApostaSalvoTextView = itemView.findViewById(R.id.nomeApostaSalvoTextView);
            editarRelativeLayoutAddCarrinho = itemView.findViewById(R.id.editarRelativeLayoutAddCarrinho);
            editarRelativeLayoutFavoritos = itemView.findViewById(R.id.editarRelativeLayoutFavoritos);
            exluirRelativeLayoutFavoritos = itemView.findViewById(R.id.exluirRelativeLayoutFavoritos);
            linearLayoutLayoutContentAlterarFavoritasSalvo = itemView.findViewById(R.id.linearLayoutLayoutContentAlterarFavoritasSalvo);
            relativeLayoutContentAlterarFavoritasEditar = itemView.findViewById(R.id.relativeLayoutContentAlterarFavoritasEditar);
            editTextNomeDaAposta = itemView.findViewById(R.id.editTextNomeDaAposta);
            layoutSalvarNomeAposta = itemView.findViewById(R.id.layoutSalvarNomeAposta);
            maisInformacoesLayout = itemView.findViewById(R.id.maisInformacoesLayout);
            labelInformacaoText = itemView.findViewById(R.id.labelInformacaoText);
            informacaoText = itemView.findViewById(R.id.informacaoText);
            containerFragment = itemView.findViewById(R.id.rl_container_fragment);
        }
    }

    public void removeAt(int position) {
        listaApostaFavorita.remove(position);
        if (listaApostaFavorita.size() == 0) {
            excluiuTudo.onVazio();
        }
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, listaApostaFavorita.size());
    }

    private void deleteFavoritaCallWebservice(final int position) {
        final AlertDialog loadViewProgress = LoadingViewLoterias.show(activity);
        final ApostaBO apostaBO = ApostaBO_.getInstance_(activity);

        apostaBO.deleteApostasFavoritas(listaApostaFavorita.get(position).getId() + "", new RequestListener<RetornoPadraoDTO>() {
            @Override
            public void onResponse(RetornoPadraoDTO response) {
                FirebaseManager.registraEvento(context ,"EFETUOU_EXCLUSAO_FAVORITO");
                loadViewProgress.dismiss();
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), activity);
                }
                removeAt(position);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect(error, activity);
            }
        });
    }

    public void adicionarCarrinhoCallWebservice(final ApostaBO apostaBO, final Long idApostaFavorita, final int valorTipoConcurso, final int qtdTeimosinhas) {
        apostaBO.postIncluirApostaFavoritaCarrinho(idApostaFavorita, valorTipoConcurso, qtdTeimosinhas, new RequestListener<CarrinhoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoDTOResponse response) {
                FirebaseManager.registraEvento(context ,"EFETUOU_INCLUSAO_FAVORITO_NO_CARRINHO");
                DialogUtils.dismiss();
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), activity);
                }
                ViewUtils.alert(context, context.getResources().getString(R.string.label_aposta_salva_carinha));
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtils.dismiss();
                RedirectNetwork.checkRedirect(error, activity);

            }
        });
    }

    private void alterarFavoritaCallWebservice(final ApostaFavoritaDTO apostaFavoritaDTO, final ViewHolder holder) {
        final AlertDialog loadViewProgress = LoadingViewLoterias.show(activity);
        final ApostaBO apostaBO = ApostaBO_.getInstance_(activity);
        apostaFavoritaDTO.setNome(holder.editTextNomeDaAposta.getText().toString());
        apostaFavoritaDTO.setOpenSwipeLayout(null);
        apostaBO.postApostasFavoritas(apostaFavoritaDTO, new RequestListener<RetornoPadraoDTO>() {
            @Override
            public void onResponse(RetornoPadraoDTO response) {
                loadViewProgress.dismiss();
                FirebaseManager.registraEvento(context ,"EFETUOU_TROCA_DO_NOME_DA_APOSTA_FAVORITA");
                if (response.getRedirect() != null) {
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), activity);
                }

                apostaFavoritaDTO.setOpenSwipeLayout(true);
                apostaFavoritaDTO.setNome(holder.editTextNomeDaAposta.getText().toString());
                holder.tituloNomeApostaFavorita.setText(" | " + holder.editTextNomeDaAposta.getText().toString());
                holder.nomeApostaSalvoTextView.setText("\"" + holder.editTextNomeDaAposta.getText().toString() + "\"");
                holder.editTextNomeDaAposta.setText("");
                holder.linearLayoutLayoutContentAlterarFavoritasSalvo.setVisibility(View.VISIBLE);
                holder.relativeLayoutContentAlterarFavoritasEditar.setVisibility(View.GONE);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                apostaFavoritaDTO.setOpenSwipeLayout(true);
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect(error, activity);
            }
        });
    }

    private void adicionarCarrinho(ApostaBO apostaBO, Long idApostaFavorita, DTOEnumInteger modalidadeEnum) {
        SessaoUsuario sessaoUsuario = SessaoUsuario.getInstance();
        List<ParametroSimulacao> arrayConcursos;
        arrayConcursos = new ArrayList<>();
        List<ParametroSimulacao> parametroSimulacaoList = sessaoUsuario.getParametrosSimulacao().getParametros();
        for (ParametroSimulacao concurso : parametroSimulacaoList) {
            if (concurso.getParametroJogo().getConcurso().getModalidadeDetalhada().getDescricao().equals(modalidadeEnum.getDescricao())) {
                arrayConcursos.add(concurso);
            }
        }

        if (arrayConcursos.size() > 1) {
            DialogUtils.dismiss();
            Boolean temEspecialENormal = false;
            for (ParametroSimulacao modalidade : arrayConcursos) {
                if (modalidade.getParametroJogo().getConcurso().getTipoConcurso() == TipoConcursoEnum.NORMAL) {
                    temEspecialENormal = true;
                    if (modalidade.getParametroJogo().getTeimosinhas().size() > 0) {
                        popupAdicionarCarrinhoFavoritas(arrayConcursos, idApostaFavorita);
                        return;
                    }
                } else {
                    if (temEspecialENormal) {
                        popupAdicionarCarrinhoFavoritas(arrayConcursos, idApostaFavorita);
                        return;
                    }
                }
            }
        } else {
            if (arrayConcursos.get(0).getParametroJogo().getConcurso().getTipoConcurso() == TipoConcursoEnum.ESPECIAL) {
                adicionarCarrinhoCallWebservice(apostaBO, idApostaFavorita, 2, 0);
            } else {
                DialogUtils.dismiss();
                if (arrayConcursos.get(0).getParametroJogo().getTeimosinhas().size() > 0) {
                    popupAdicionarCarrinhoFavoritas(arrayConcursos, idApostaFavorita);
                    return;
                }
            }
        }
    }

    private void popupAdicionarCarrinhoFavoritas(List<ParametroSimulacao> arrayConcursos, Long idApostaFavorita) {
        android.support.v4.app.FragmentManager fm = ((FavoritasActivity) context).getSupportFragmentManager();
        AdicionarCarrinhoFavoritasView adicionarCarrinhoFavoritasView = AdicionarCarrinhoFavoritasView.newInstance();
        adicionarCarrinhoFavoritasView.arrayConcursos = arrayConcursos;
        adicionarCarrinhoFavoritasView.idApostaFavorita = idApostaFavorita;
        adicionarCarrinhoFavoritasView.show(fm, "adicionarCarrinhoFavoritasView");
    }

    private void adicionarApostaCarrinhoRequest(final ApostaBO apostaBO, final ApostaFavoritaDTO apostaFavoritaDTO) {
        DialogUtils.show(activity);
        apostaBO.buscaCarrinho(new RequestListener<CarrinhoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoDTOResponse response) {
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), activity);
                }
                final CarrinhoDTO carrinhoDTO = response.getPayload();

                checarLimiteDiario(apostaBO, apostaFavoritaDTO, carrinhoDTO);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtils.dismiss();
                RedirectNetwork.checkRedirect(error, activity);
            }
        });
    }

    private void checarLimiteDiario(final ApostaBO apostaBO, final ApostaFavoritaDTO apostaFavoritaDTO, final CarrinhoDTO carrinhoDTO) {
        if (SessaoUsuario.getInstance().getParametrosSimulacao().getValorLimiteDiario()
                .compareTo(carrinhoDTO.getValorTotal()
                        .add(IdentificaoDeUmaApostaDas8Modalidades.getValorApostaFavoritaPorProagnostico(apostaFavoritaDTO))) < 0) {
            DialogUtils.dismiss();
            ViewUtils.alertTitleButtonYesOrNotListener(context, context.getResources().getString(R.string.MA014), (dialog, which) -> {
                DialogUtils.show(context);
                checarApostasRepetidas(apostaBO, apostaFavoritaDTO, carrinhoDTO);
            });
        } else {
            checarApostasRepetidas(apostaBO, apostaFavoritaDTO, carrinhoDTO);
        }
    }

    private void checarApostasRepetidas(final ApostaBO apostaBO, final ApostaFavoritaDTO apostaFavoritaDTO, final CarrinhoDTO carrinhoDTO) {
        if (IdentificaoDeUmaApostaDas8Modalidades.checkExisteUmaApostaIgualCarrinho(apostaFavoritaDTO, carrinhoDTO)) {
            DialogUtils.dismiss();
            ViewUtils.alertTitleButtonYesOrNotListener(context, context.getResources().getString(R.string.MA015), (dialog, which) -> {
                DialogUtils.show(activity);
                adicionarCarrinho(apostaBO, apostaFavoritaDTO.getId(), apostaFavoritaDTO.getModalidade());
            });
        } else {
            adicionarCarrinho(apostaBO, apostaFavoritaDTO.getId(), apostaFavoritaDTO.getModalidade());
        }
    }

    public interface ExcluiuTudo {
        void onVazio();
    }
}