package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LotericaDTO;

import br.gov.caixa.loterias.apostas.R;


public class LotericaArrayAdapter extends ArrayAdapter<LotericaDTO> {

    private List<LotericaDTO> originalData;
    private List<LotericaDTO> filteredData;
    private ItemFilter filter = new ItemFilter();

    public LotericaArrayAdapter(Context context, int resource, List<LotericaDTO> objects) {
        super(context, resource, objects);
        this.originalData = objects;
        this.filteredData = objects;
    }

    public void setLotericaDTO(List<LotericaDTO> objects) {
        this.originalData = objects;
        this.filteredData = objects;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Override
    public LotericaDTO getItem(int position) {
        return filteredData.get(position);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_loterica, parent, false);
        }

        TextView tvLinha = convertView.findViewById(R.id.textNomeLoterica);

        LotericaDTO lotericaDTO = getItem(position);

        if (lotericaDTO != null) {
            tvLinha.setText(lotericaDTO.getNome());
        }

        return convertView;
    }


    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint == null || constraint.length() == 0) {
                results.values = originalData;
                results.count = originalData.size();
            } else {
                List<LotericaDTO> filterResultsData = new ArrayList<>();
                for (LotericaDTO lotericaDTO : originalData) {
                    if (lotericaDTO.getNome().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filterResultsData.add(lotericaDTO);
                    }
                }

                results.values = filterResultsData;
                results.count = filterResultsData.size();
            }

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (List<LotericaDTO>) results.values;
            notifyDataSetChanged();
        }
    }
}