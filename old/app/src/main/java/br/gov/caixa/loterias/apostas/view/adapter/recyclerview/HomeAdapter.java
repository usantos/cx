package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.Modalidade;
import br.gov.caixa.loterias.apostas.model.bo.ApostaSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursoDTOResponse;
import br.gov.caixa.loterias.apostas.view.custom.LoadingViewLoterias;
import br.gov.caixa.loterias.apostas.view.custom.ModalidadeSorteioResultadoView;
import br.gov.caixa.loterias.apostas.view.holder.HomeViewHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnHomeListener;

/**
 * Created by brfernan on 26/10/2017.
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeViewHolder> implements OnHomeListener {

    private List<Modalidade> data;
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
    public HomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_modalidade, parent, false);

        return new HomeViewHolder(v, this, activity);
    }

    @Override
    public void onBindViewHolder(final HomeViewHolder holder, final int position) {
        Modalidade modalidade = data.get(position);
        holder.bind(modalidade, retornaPosicaoPorModalidade(modalidade.getTipoModalidade()));
    }

    public int retornaPosicaoPorModalidade(ModalidadeEnum modalidade) {
        for (int i = 0; i < data.size(); i++) {
            if (data != null && data.get(i).getTipoModalidade().equals(modalidade)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setModalidades(List<Modalidade> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public void callWebserviceResultadoConcurso(final RelativeLayout relativeLayoutTopResultados,
                                                 final LinearLayout linearLayoutResultadosApostas,
                                                 final String modalidade,
                                                 final int concurso,
                                                 final int position,
                                                 final RelativeLayout setaEsquerdaAcaoRelativeLayout,
                                                 final RelativeLayout setaDireitaAcaoRelativeLayout,
                                                 final boolean animarView, final HomeViewHolder holder
                                                ) {

        final AlertDialog loadViewProgress = LoadingViewLoterias.show(context);
        ApostaSilceBO.getInstance().getResultadoModalidade(modalidade, new RequestListener<ResultadoConcursoDTOResponse>() {
            @Override
            public void onResponse(ResultadoConcursoDTOResponse response) {
                loadViewProgress.dismiss();
                if (response.getRedirect() != null) {
                    RedirectNetwork.checkRedirectSucesso(response.getRedirect(), activity);
                }

                data.get(position).setResultadoConcursoDTO(response.getPayload());
                if (animarView) {
                    holder.animacaoAbrirResultados(context,
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

    @Override
    public void setLayoutResultado(LinearLayout linearLayoutResultadosApostas, int position) {
        linearLayoutResultadosApostas.removeAllViews();
        ModalidadeSorteioResultadoView view = ModalidadeSorteioResultadoView.build(context);
        view.setLayout(data.get(position));
        linearLayoutResultadosApostas.addView(view);
    }
}