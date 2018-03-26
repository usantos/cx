package caixa.gov.br.loterias.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
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

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.ApostaBO;
import caixa.gov.br.loterias.model.bo.ApostaBO_;
import caixa.gov.br.loterias.model.bo.MensagensNetwork;
import caixa.gov.br.loterias.model.bo.RequestListener;
import caixa.gov.br.loterias.model.bo.silce.dto.ApostaDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.ModalidadeEnum;
import caixa.gov.br.loterias.model.bean.Modalidade;
import caixa.gov.br.loterias.model.bo.silce.dto.ResultadoConcursoDTOResponse;
import caixa.gov.br.loterias.utils.EstiloModalidade;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.PrincipalActivity;
import caixa.gov.br.loterias.view.animation.AnimacoesResultados;
import caixa.gov.br.loterias.view.custom.LoadingViewLoterias;

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

//    @Override
//    public void onAttachedToRecyclerView (RecyclerView recyclerView) {
//        super.onAttachedToRecyclerView(recyclerView);
//        Activity context = (Activity) recyclerView.getContext();
//        Point windowDimensions = new Point();
//        context.getWindowManager().getDefaultDisplay().getSize(windowDimensions);
//        itemHeight = Math.round(windowDimensions.y * 0.4f);
//    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.card_modalidade, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        estilo = new EstiloModalidade(data.get(position).getTipoModalidade());

        if (data.get(position).getConcurso().getTipoConcurso().toString().equals("ESPECIAL")){
            holder.game.setText(data.get(position).getConcurso().getModalidadeDetalhada().getDescricaoEspecial().toLowerCase());
        }else{
            holder.game.setText(ModalidadeEnum.fromString(data.get(position).getTipoModalidade()));
        }

        holder.relativeLayoutTopResultados.setBackgroundColor(ContextCompat.getColor(context, estilo.getCorEscura()));
        holder.trevo.setBackground(ContextCompat.getDrawable(context, estilo.getTrevoBackground()));
        holder.trevoFundo.setBackground(ContextCompat.getDrawable(context, estilo.getTrevoBackground()));
        holder.titulo.setBackgroundColor(ContextCompat.getColor(context, estilo.getCorClara()));
        holder.valorAcumulado.setTextColor(ContextCompat.getColor(context, estilo.getCorClara()));

        String  estimativaFormatado = ViewUtils.getMoedaFormat(data.get(position).getConcurso().getEstimativa());
        holder.valorAcumulado.setText(estimativaFormatado);

        holder.premioEstimadoConcurso.setText("Prêmio estimado do concurso " + data.get(position).getConcurso().getNumero());

        DateFormat formatterDay = new SimpleDateFormat("EEEE");
        DateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Locale BRAZIL = new Locale("pt","BR");
        formatterDay = SimpleDateFormat.getDateInstance(DateFormat.FULL, BRAZIL);
        Date dataSorteioFormatada = null;
        Date dataFechamandoSorteio = null;

        try {
            dataSorteioFormatada = formatterDate.parse(data.get(position).getConcurso().getDataHoraSorteio());
            dataFechamandoSorteio = formatterDate.parse(data.get(position).getConcurso().getDataFechamento());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String[] split = formatterDay.format(dataSorteioFormatada).split(",");
        String[] splitDataFormatadaSilce = data.get(position).getConcurso().getDataSorteio().split(" ");
        holder.dataSorteio.setText(ViewUtils.capitalized(split[0]) + ", " + splitDataFormatadaSilce[0] + " de " + splitDataFormatadaSilce[1]);

        Map mapComDiferencaDeDatas = computeDiff(new Date(), dataFechamandoSorteio);

        holder.tempoRestanteParaAposta.setText(retornaStringDias((Long) mapComDiferencaDeDatas.get(TimeUnit.DAYS))
                +retornaStringHoras((Long) mapComDiferencaDeDatas.get(TimeUnit.DAYS), (Long) mapComDiferencaDeDatas.get(TimeUnit.HOURS)));

        aposta.setText(ViewUtils.fromHtml("<b>Aposte</b><br>por R$" + data.get(position).getValor()));
        Glide.with(holder.itemView.getContext())
                .load(estilo.getImagemCalendario())
                .into(holder.calendario);
        Glide.with(holder.itemView.getContext())
                .load(estilo.getImagemRelogio())
                .into(holder.relogio);

        final LinearLayout linearLayoutContentCardModalide = holder.linearLayoutContentCardModalide;
        final RelativeLayout relativeLayoutTopResultados = holder.relativeLayoutTopResultados;
        holder.buttonActionResultadosModalidade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (linearLayoutContentCardModalide.isEnabled()) {
                    callWebserviceResultadoConcurso(linearLayoutContentCardModalide, relativeLayoutTopResultados, ApostaDTO.lowerCaseFromString(ApostaDTO.toStringModalidade(data.get(position).getNome())), data.get(position).getConcurso().getNumero());
                } else {
                    AnimacaoFecharResultados(context, linearLayoutContentCardModalide, relativeLayoutTopResultados);
                }
            }
        });
    }

    public static String retornaStringDias (Long dia) {
        if (dia > 0) {
            if (dia == 1) {
                return String.format( "%d dia", dia);
            }
            return String.format( "%d dias", dia);
        }
        return "";
    }

    public static String retornaStringHoras (Long dia, Long hora) {
        String string = "";
        string = dia > 0 ? " e " : "";

        if (hora > 0){
            if (hora == 1) {
                return string + String.format( "%d hora", hora);
            }
            return string + String.format( "%d horas", hora);
        }
        return "";
    }

    public static Map<TimeUnit,Long> computeDiff(Date date1, Date date2) {
        long diffInMillies = date2.getTime() - date1.getTime();
        List<TimeUnit> units = new ArrayList<TimeUnit>(EnumSet.allOf(TimeUnit.class));
        Collections.reverse(units);
        Map<TimeUnit,Long> result = new LinkedHashMap<TimeUnit,Long>();
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
        private ImageView trevo;
        private RelativeLayout trevoFundo;
        private TextView premioEstimadoConcurso;
        private TextView dataSorteio;
        private TextView tempoRestanteParaAposta;
        private Button buttonActionResultadosModalidade;
        private LinearLayout linearLayoutContentCardModalide;
        private RelativeLayout relativeLayoutTopResultados;


        public ViewHolder (View itemView) {
            super(itemView);
            game = (TextView) itemView.findViewById(R.id.textViewCardTitle);
            trevo = (ImageView) itemView.findViewById(R.id.trevoImagem);
            trevoFundo = (RelativeLayout) itemView.findViewById(R.id.RelativeLayoutTrevo) ;
            calendario = (ImageView)  itemView.findViewById(R.id.imageCalendar);
            relogio = (ImageView)  itemView.findViewById(R.id.imageWatch);
            titulo = itemView.findViewById(R.id.linearLayoutTopModalidades);
            valorAcumulado = itemView.findViewById(R.id.textVieCardAcumulatedValue);
            premioEstimadoConcurso = (TextView) itemView.findViewById(R.id.textPremioEstimadoConcurso);
            dataSorteio = (TextView) itemView.findViewById(R.id.textVieCardDrawDate);
            tempoRestanteParaAposta = (TextView) itemView.findViewById(R.id.textVieCardClosingOfBets);
            tempoRestanteParaAposta = (TextView) itemView.findViewById(R.id.textVieCardClosingOfBets);
            buttonActionResultadosModalidade = (Button) itemView.findViewById(R.id.buttonActionResultadosModalidade);
            linearLayoutContentCardModalide = (LinearLayout) itemView.findViewById(R.id.linearLayoutContentCardModalide);
            relativeLayoutTopResultados = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutTopResultados);
        }

        public void setOverlayColor(Context context, EstiloModalidade estilo) {
            aposta.setBackgroundColor(ContextCompat.getColor(context, estilo.getCorClara()));
        }
    }

    private void AnimacaoAbrirResultados(Context context, LinearLayout linearLayoutTopModalidades, RelativeLayout linearLayoutTopResultados) {
        linearLayoutTopModalidades.setEnabled(false);
        AnimacoesResultados animacoesResultados = new AnimacoesResultados(
                linearLayoutTopModalidades, linearLayoutTopResultados, context, ViewUtils.setDisplayMetric(240, context), ViewUtils.setDisplayMetric(280, context)
        );
        animacoesResultados.AnimacaoDescerContent(ViewUtils.getRealScreenSize(context).y - ViewUtils.setDisplayMetric(250, context));
    }

    private void AnimacaoFecharResultados(Context context, LinearLayout linearLayoutTopModalidades, RelativeLayout linearLayoutTopResultados) {
        linearLayoutTopModalidades.setEnabled(true);
        AnimacoesResultados animacoesResultados = new AnimacoesResultados(
                linearLayoutTopModalidades, linearLayoutTopResultados, context, ViewUtils.setDisplayMetric(240, context), ViewUtils.setDisplayMetric(280, context)
        );

        animacoesResultados.AnimacaoSubirContent(0);
    }


    private void callWebserviceResultadoConcurso(final LinearLayout linearLayoutContentCardModalide, final RelativeLayout relativeLayoutTopResultados, final String modalidade, final int concurso) {

        final AlertDialog loadViewProgress = LoadingViewLoterias.show(context);
        final ApostaBO apostaBO = ApostaBO_.getInstance_(context);

        apostaBO.getResultadoModalidadeConcurso(modalidade, concurso, new RequestListener<ResultadoConcursoDTOResponse>() {
            @Override
            public void onResponse(ResultadoConcursoDTOResponse response) {
                loadViewProgress.dismiss();

                AnimacaoAbrirResultados(context, linearLayoutContentCardModalide, relativeLayoutTopResultados);

            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                ViewUtils.alert(activity, MensagensNetwork.setMensagem(error, context));

            }
        });
    }

}
