package br.gov.caixa.loterias.apostar.interno.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bean.Modalidade;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ResultadoConcursoDTOResponse;
import br.gov.caixa.loterias.apostar.interno.utils.EstiloModalidade;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.animation.AnimacoesResultados;
import br.gov.caixa.loterias.apostar.interno.view.custom.LoadingViewLoterias;
import br.gov.caixa.loterias.apostar.interno.view.custom.ModalidadeSorteioResultadoView;
import br.gov.caixa.loterias.apostar.interno.view.custom.ModalidadeSorteioResultadoView_;

/**
 * Created by brfernan on 26/10/2017.
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private int itemHeight;
    private List<Modalidade> data;
    private EstiloModalidade estilo;
    private Context context;
    private Activity activity;
    private static Button aposta;

    public HomeAdapter(Activity activity, Context context, List<Modalidade> data, View aposta) {
        this.data = data;
        this.context = context;
        this.activity = activity;
        this.aposta = (Button) aposta;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.card_modalidade, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        estilo = new EstiloModalidade(data.get(position).getTipoModalidade());

        if (data.get(position).getConcurso().getTipoConcurso().toString().equals("ESPECIAL")){
            holder.game.setText(data.get(position).getConcurso().getModalidadeDetalhada().getDescricaoEspecial().toLowerCase());
        }else{
            holder.game.setText(ModalidadeEnum.fromString(data.get(position).getTipoModalidade()));
        }

        holder.relativeLayoutTopResultados.setBackgroundColor(ContextCompat.getColor(context, estilo.getCorEscura()));
        holder.linearLayoutResultadosApostas.setBackgroundColor(ContextCompat.getColor(context, estilo.getCorEscura()));
        //TODO: Altera a cor do título do carrosel
        holder.trevo.setBackground(ContextCompat.getDrawable(context, estilo.getTrevoBackground()));
        holder.trevoFundo.setBackground(ContextCompat.getDrawable(context, estilo.getTrevoBackground()));
        holder.titulo.setBackgroundColor(ContextCompat.getColor(context, estilo.getCorClara()));
        holder.valorAcumulado.setTextColor(ContextCompat.getColor(context, estilo.getCorClara()));

        if (data.get(position).getConcurso().getEstimativa().compareTo(BigDecimal.ZERO) != 0) {
            String  estimativaFormatado = ViewUtils.getMoedaFormat(data.get(position).getConcurso().getEstimativa());
            holder.valorAcumulado.setText(estimativaFormatado);
            holder.valorAcumulado.setTextSize(20);
            holder.premioEstimadoConcurso.setText(ViewUtils.textFuturaAndFuturaBold(context, context.getString(R.string.label_premio_estimado_concurso_numero_bold,
                    data.get(position).getConcurso().getNumero())));

            holder.premioEstimadoConcurso.setVisibility(View.VISIBLE);
        } else {
            holder.valorAcumulado.setText(retornaStringConcursoSemEstimativa(data.get(position).getConcurso().getNumero().toString(), estilo.getCorClara()));
            holder.valorAcumulado.setTextSize(14);

            holder.premioEstimadoConcurso.setVisibility(View.INVISIBLE);
        }

        DateFormat formatterDay;
        DateFormat formatterMonth;

        DateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Locale BRAZIL = new Locale("pt","BR");
        formatterDay = SimpleDateFormat.getDateInstance(DateFormat.FULL, BRAZIL);
        formatterMonth = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, BRAZIL);

        Date dataSorteioFormatada = null;
        Date dataFechamentoSorteio = null;

        try {
            dataSorteioFormatada = formatterDate.parse(data.get(position).getConcurso().getDataHoraSorteio());
            dataFechamentoSorteio = formatterDate.parse(data.get(position).getConcurso().getDataFechamento());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (data.get(position).getTipoModalidade() == ModalidadeEnum.LOTECA ||
                data.get(position).getTipoModalidade() == ModalidadeEnum.LOTOGOL) {
            holder.textViewSorteioOuEncerramento.setText(R.string.label_encerramento);

            String[] split = formatterDay.format(dataFechamentoSorteio).split(",");
            String[] splitMedium = formatterMonth.format(dataFechamentoSorteio).split(" ");

            holder.dataSorteio.setText(ViewUtils.capitalized(split[0]) + ", " + splitMedium[0] + " de " + splitMedium[2]);
        } else {
            holder.textViewSorteioOuEncerramento.setText(R.string.label_sorteio_case);

            String[] split = formatterDay.format(dataSorteioFormatada).split(",");
            String[] splitDataFormatadaSilce = data.get(position).getConcurso().getDataSorteio().split(" ");
            holder.dataSorteio.setText(ViewUtils.capitalized(split[0]) + ", " + splitDataFormatadaSilce[0] + " de " + splitDataFormatadaSilce[1]);
        }

        Map mapComDiferencaDeDatas = computeDiff(new Date(), dataFechamentoSorteio);

        holder.tempoRestanteParaAposta.setText(retornaStringDias((Long) mapComDiferencaDeDatas.get(TimeUnit.DAYS))
                +retornaStringHoras((Long) mapComDiferencaDeDatas.get(TimeUnit.DAYS), (Long) mapComDiferencaDeDatas.get(TimeUnit.HOURS)));

        Glide.with(holder.itemView.getContext())
                .load(estilo.getImagemCalendario())
                .into(holder.calendario);
        Glide.with(holder.itemView.getContext())
                .load(estilo.getImagemRelogio())
                .into(holder.relogio);

        holder.buttonActionResultadosModalidade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.linearLayoutResultadosApostas.getHeight() == 0) {
                    if (data.get(position).getResultadoConcursoDTO() == null){
                        callWebserviceResultadoConcurso(holder.relativeLayoutTopResultados,
                                holder.linearLayoutResultadosApostas,
                                ApostaDTO.lowerCaseFromString(data.get(position).getTipoModalidade()),
                                data.get(position).getConcurso().getNumero(), position,
                                holder.setaEsquerdaAcaoRelativeLayout,
                                holder.setaDireitaAcaoRelativeLayout,
                                true);
                    }
                    else{
                        AnimacaoAbrirResultados(context,
                                holder.relativeLayoutTopResultados,
                                holder.linearLayoutResultadosApostas,
                                position,
                                holder.setaEsquerdaAcaoRelativeLayout,
                                holder.setaDireitaAcaoRelativeLayout);
                    }
                } else {
                    data.get(position).setResultadosAberto(false);
                    AnimacaoFecharResultados(context,
                            holder.relativeLayoutTopResultados,
                            holder.linearLayoutResultadosApostas,
                            holder.setaEsquerdaAcaoRelativeLayout,
                            holder.setaDireitaAcaoRelativeLayout);
                }
            }
        });

        if(data.get(position).getResultadosAberto()){
            ViewGroup.LayoutParams layoutParams = holder.linearLayoutResultadosApostas.getLayoutParams();
            layoutParams.height = ViewUtils.getRealScreenSize(context).y - ViewUtils.setDisplayMetric(250, context);
            layoutParams.width = ViewUtils.setDisplayMetric(280, context);
            holder.linearLayoutResultadosApostas.setLayoutParams(layoutParams);

            //holder.setaDireitaAcaoRelativeLayout.setVisibility(View.VISIBLE);
            //holder.setaEsquerdaAcaoRelativeLayout.setVisibility(View.VISIBLE);
        }else{
            ViewGroup.LayoutParams layoutParams = holder.linearLayoutResultadosApostas.getLayoutParams();
            layoutParams.height = 0;
            holder.linearLayoutResultadosApostas.setLayoutParams(layoutParams);
        }
        if (data.get(position).getResultadoConcursoDTO() != null){
            setLayoutResultado(holder.linearLayoutResultadosApostas, position);
        }else {
            holder.linearLayoutResultadosApostas.removeAllViews();
        }

        holder.setaEsquerdaAcaoRelativeLayout.setOnClickListener( view ->
                callWebserviceResultadoConcurso(holder.relativeLayoutTopResultados,
                    holder.linearLayoutResultadosApostas,
                    ApostaDTO.lowerCaseFromString(data.get(position).getTipoModalidade()),
                    data.get(position).getConcurso().getNumero()-1,
                    position,
                    holder.setaEsquerdaAcaoRelativeLayout,
                    holder.setaDireitaAcaoRelativeLayout,
                    false
                ));

        holder.setaDireitaAcaoRelativeLayout.setOnClickListener( view ->
                callWebserviceResultadoConcurso(holder.relativeLayoutTopResultados,
                    holder.linearLayoutResultadosApostas,
                    ApostaDTO.lowerCaseFromString(data.get(position).getTipoModalidade()),
                    data.get(position).getConcurso().getNumero()+1,
                    position,
                    holder.setaEsquerdaAcaoRelativeLayout,
                    holder.setaDireitaAcaoRelativeLayout,
                    false
                ));
    }

    private SpannableStringBuilder retornaStringConcursoSemEstimativa(String numConcurso, int corModalidadeInt) {
        final SpannableStringBuilder sb = new SpannableStringBuilder("Concurso \n\n" + numConcurso + "\n\nainda sem estimativa de prêmio.");

        final ForegroundColorSpan corModalidade = new ForegroundColorSpan(ContextCompat.getColor(context, corModalidadeInt));
        final ForegroundColorSpan corTexto = new ForegroundColorSpan(ContextCompat.getColor(context,R.color.cinzaescuro));

        sb.setSpan(corModalidade, 0, 7, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        sb.setSpan(corTexto, 10, 11 + numConcurso.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        sb.setSpan(corModalidade, 11 + numConcurso.length(), sb.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        return sb;
    }

    public int retornaPosicaoPorModalidade(ModalidadeEnum modalidade){
        for (int i = 0; i < data.size(); i++) {
            if (data !=null && data.get(i).getTipoModalidade().equals(modalidade)) {
                return i;
            }
        }
        return -1;
    }

    private String retornaStringDias(Long dia) {
        if (dia > 0) {
            if (dia == 1) {
                return String.format(Locale.getDefault(), "%d dia", dia);
            }
            return String.format(Locale.getDefault(), "%d dias", dia);
        }
        return "";
    }

    private String retornaStringHoras(Long dia, Long hora) {
        String string = "";
        string = dia > 0 ? " e " : "";

        if (hora > 0){
            if (hora == 1) {
                return string + String.format(Locale.getDefault(), "%d hora", hora);
            }
            return string + String.format(Locale.getDefault(), "%d horas", hora);
        }
        return "";
    }

    private Map<TimeUnit,Long> computeDiff(Date date1, Date date2) {
        long diffInMillies = date2.getTime() - date1.getTime();
        List<TimeUnit> units = new ArrayList<>(EnumSet.allOf(TimeUnit.class));
        Collections.reverse(units);
        Map<TimeUnit,Long> result = new LinkedHashMap<>();
        long milliesRest = diffInMillies;
        for ( TimeUnit unit : units ) {
            long diff = unit.convert(milliesRest,TimeUnit.MILLISECONDS);
            long diffInMilliesForUnit = unit.toMillis(diff);
            milliesRest = milliesRest - diffInMilliesForUnit;
            result.put(unit,diff);
        }
        return result;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private View titulo;
        private TextView game;
        private ImageView relogio;
        private ImageView calendario;
        private TextView valorAcumulado;
        private TextView labelAcumulouTextView;
        private ImageView trevo;
        private RelativeLayout trevoFundo;
        private TextView premioEstimadoConcurso;
        private TextView dataSorteio;
        private TextView textViewSorteioOuEncerramento;
        private TextView tempoRestanteParaAposta;
        private Button buttonActionResultadosModalidade;
        private RelativeLayout relativeLayoutTopResultados;
        private LinearLayout linearLayoutResultadosApostas;
        private RelativeLayout setaDireitaAcaoRelativeLayout;
        private RelativeLayout setaEsquerdaAcaoRelativeLayout;


        public ViewHolder (View itemView) {
            super(itemView);
            game = itemView.findViewById(R.id.textViewCardTitle);
            trevo = itemView.findViewById(R.id.trevoImagem);
            trevoFundo = itemView.findViewById(R.id.RelativeLayoutTrevo) ;
            calendario = itemView.findViewById(R.id.imageCalendar);
            relogio = itemView.findViewById(R.id.imageWatch);
            titulo = itemView.findViewById(R.id.linearLayoutTopModalidades);
            valorAcumulado = itemView.findViewById(R.id.textVieCardAcumulatedValue);
            premioEstimadoConcurso = itemView.findViewById(R.id.textPremioEstimadoConcurso);
            dataSorteio = itemView.findViewById(R.id.textVieCardDrawDate);
            textViewSorteioOuEncerramento = itemView.findViewById(R.id.textViewSorteioOuEncerramento);
            tempoRestanteParaAposta = itemView.findViewById(R.id.textVieCardClosingOfBets);
            tempoRestanteParaAposta = itemView.findViewById(R.id.textVieCardClosingOfBets);
            buttonActionResultadosModalidade = itemView.findViewById(R.id.buttonActionResultadosModalidade);
            relativeLayoutTopResultados = itemView.findViewById(R.id.relativeLayoutTopResultados);
            linearLayoutResultadosApostas = itemView.findViewById(R.id.linearLayoutResultadosApostas);

            setaEsquerdaAcaoRelativeLayout =  itemView.findViewById(R.id.setaEsquerdaAcaoRelativeLayout);
            setaDireitaAcaoRelativeLayout = itemView.findViewById(R.id.setaDireitaAcaoRelativeLayout);

        }

        public void setOverlayColor(Context context, EstiloModalidade estilo) {
            aposta.setBackgroundColor(ContextCompat.getColor(context, estilo.getCorClara()));
        }
    }

    private void AnimacaoAbrirResultados(Context context,
                                         RelativeLayout linearLayoutTopResultados,
                                         LinearLayout linearLayoutResultadosApostas,
                                         int position,
                                         RelativeLayout setaEsquerdaAcaoRelativeLayout,
                                         RelativeLayout setaDireitaAcaoRelativeLayout) {
        AnimacoesResultados animacoesResultados = new AnimacoesResultados(
                linearLayoutTopResultados, linearLayoutResultadosApostas,
                context,
                ViewUtils.setDisplayMetric(240, context),
                ViewUtils.setDisplayMetric(280, context)
        );
        data.get(position).setResultadosAberto(true);
        animacoesResultados.AnimacaoDescerContent(ViewUtils.getRealScreenSize(context).y - ViewUtils.setDisplayMetric(315, context));
        //setaDireitaAcaoRelativeLayout.setVisibility(View.VISIBLE);
        //setaEsquerdaAcaoRelativeLayout.setVisibility(View.VISIBLE);
    }

    private void AnimacaoFecharResultados(Context context,
                                          RelativeLayout linearLayoutTopResultados,
                                          LinearLayout linearLayoutResultadosApostas,
                                          RelativeLayout setaEsquerdaAcaoRelativeLayout,
                                          RelativeLayout setaDireitaAcaoRelativeLayout) {
        AnimacoesResultados animacoesResultados = new AnimacoesResultados(
                linearLayoutTopResultados,
                linearLayoutResultadosApostas,
                context,
                ViewUtils.setDisplayMetric(240, context),
                ViewUtils.setDisplayMetric(280, context)
        );

        animacoesResultados.AnimacaoSubirContent();
        //setaDireitaAcaoRelativeLayout.setVisibility(View.INVISIBLE);
        //setaEsquerdaAcaoRelativeLayout.setVisibility(View.INVISIBLE);
    }


    private void callWebserviceResultadoConcurso(final RelativeLayout relativeLayoutTopResultados,
                                                 final LinearLayout linearLayoutResultadosApostas,
                                                 final String modalidade,
                                                 final int concurso,
                                                 final int position,
                                                 final RelativeLayout setaEsquerdaAcaoRelativeLayout,
                                                 final RelativeLayout setaDireitaAcaoRelativeLayout,
                                                 final boolean animarView
    ) {

        final AlertDialog loadViewProgress = LoadingViewLoterias.show(context);
        final ApostaBO apostaBO = ApostaBO_.getInstance_(context);

        apostaBO.getResultadoModalidade(modalidade, new RequestListener<ResultadoConcursoDTOResponse>() {
            @Override
            public void onResponse(ResultadoConcursoDTOResponse response) {
                loadViewProgress.dismiss();

                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), activity);
                }

                data.get(position).setResultadoConcursoDTO(response.getPayload());
                if (animarView){
                    AnimacaoAbrirResultados(context,
                            relativeLayoutTopResultados,
                            linearLayoutResultadosApostas,
                            position, setaEsquerdaAcaoRelativeLayout,
                            setaDireitaAcaoRelativeLayout);
                }

                setLayoutResultado(linearLayoutResultadosApostas,
                        position);

            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect(error, activity);
            }
        });
    }

    private void setLayoutResultado(LinearLayout linearLayoutResultadosApostas, int position){
        linearLayoutResultadosApostas.removeAllViews();
        ModalidadeSorteioResultadoView view = ModalidadeSorteioResultadoView_.build(context);
        view.setLayout(data.get(position));
        linearLayoutResultadosApostas.addView(view);
    }

}
