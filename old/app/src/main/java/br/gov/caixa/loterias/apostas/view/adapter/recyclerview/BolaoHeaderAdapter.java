package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.FiltroAplicadoMarketplace;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CotasBolaoDTO;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.view.custom.AccessibleSpringDotsIndicator;
import br.gov.caixa.loterias.apostas.view.listener.OnItemCarrosselBolaoListener;

public class BolaoHeaderAdapter extends RecyclerView.Adapter<BolaoHeaderAdapter.HeaderViewHolder> {

    private final Activity activity;
    private final OnItemCarrosselBolaoListener listener;
    private final List<CotasBolaoDTO> carrosselList = new ArrayList<>();
    private FiltroAplicadoMarketplace filtro;
    private CarrosselBolaoAdapter carrosselAdapter;
    private boolean isVerBolao;

    public BolaoHeaderAdapter(Activity activity,
                              OnItemCarrosselBolaoListener listener,
                              FiltroAplicadoMarketplace filtro,
                              boolean isVerBolao) {
        this.activity = activity;
        this.listener = listener;
        this.filtro = filtro;
        this.isVerBolao = isVerBolao;
    }

    public void atualizaHeader(List<CotasBolaoDTO> novaLista, FiltroAplicadoMarketplace novoFiltro) {
        carrosselList.clear();
        if (novaLista != null) {
            carrosselList.addAll(novaLista);
        }
        this.filtro = novoFiltro;
        notifyItemChanged(0);
    }

    @NonNull
    @Override
    public HeaderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_header_boloes, parent, false);
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HeaderViewHolder holder, int position) {
        holder.bind();
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        DiscreteScrollView carrosselView;
        AccessibleSpringDotsIndicator dotsIndicator;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            carrosselView = itemView.findViewById(R.id.bolao_carousel);
            dotsIndicator = itemView.findViewById(R.id.dotsIndicator);

            carrosselView.setItemTransformer(new ScaleTransformer.Builder()
                    .setMinScale(0.8f)
                    .build());

            carrosselView.addOnItemChangedListener((viewHolder, position) -> {
                AlertDialogUtils.dismiss();
                dotsIndicator.setSelectedIndex(position);
            });

            dotsIndicator.setOnDotClickListener(index ->
                    carrosselView.smoothScrollToPosition(index)
            );
        }

        void bind() {
            carrosselAdapter = new CarrosselBolaoAdapter(
                    activity,
                    carrosselList,
                    listener,
                    filtro,
                    isVerBolao
            );

            carrosselView.setAdapter(carrosselAdapter);
            dotsIndicator.setDotCount(carrosselAdapter.getItemCount());
            dotsIndicator.setSelectedIndex(0);
        }
    }
}