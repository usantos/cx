package caixa.gov.br.loterias.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class DialogListViewAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> valoresList;
    private int selectedIndex;
    private int selectedColor = Color.parseColor("#edf5f6");
    private int selectedTextColor = Color.parseColor("#54bbab");
    private int notSelectedTextColor = Color.parseColor("#48586c");

    public DialogListViewAdapter(Context ctx, ArrayList<String> valoresList){
        this.context = ctx;
        this.valoresList = valoresList;
        selectedIndex = -1;
    }

    public void setSelectedIndex(int ind) {
        selectedIndex = ind;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return valoresList.size();
    }

    @Override
    public Object getItem(int position) {
        return valoresList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView textView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder;
        if(convertView == null) {
            vi = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, null);
            holder = new ViewHolder();

            holder.textView = (TextView) vi;

            vi.setTag(holder);
        }
        else {
            holder = (ViewHolder) vi.getTag();
        }

        if(selectedIndex!= -1 && position == selectedIndex) {
            holder.textView.setBackgroundColor(selectedColor);
            holder.textView.setTextColor(selectedTextColor);
            holder.textView.setTypeface(null, Typeface.BOLD);
        }
        else {
            holder.textView.setBackgroundColor(Color.WHITE);
            holder.textView.setTypeface(null, Typeface.NORMAL);
            holder.textView.setTextColor(notSelectedTextColor);
        }

        holder.textView.setText(valoresList.get(position));

        return vi;
    }

}
