package br.gov.caixa.loterias.apostas.debug.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.debug.activity.NetworkInspectorDetailActivity;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.NetworkInspectorEntry;

public class NetworkInspectorAdapter extends RecyclerView.Adapter<NetworkInspectorAdapter.Holder> {

    private static final int URL_PREVIEW_LIMIT = 300;

    private final Context context;
    private final ArrayList<NetworkInspectorEntry> items = new ArrayList<>();

    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("HH:mm:ss", new Locale("pt", "BR"));

    public NetworkInspectorAdapter(Context context) {
        this.context = context;
    }

    public void submit(ArrayList<NetworkInspectorEntry> newItems) {
        items.clear();

        if (newItems != null) {
            items.addAll(newItems);
        }

        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_network_entry, parent, false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

        NetworkInspectorEntry entry = items.get(position);

        holder.title.setText(
                entry.method + " • " + buildStatus(entry)
        );

        holder.subtitle.setText(buildSubtitle(entry));

        holder.url.setText(preview(entry.url));
        holder.url.setMaxLines(2);

        holder.itemView.setBackgroundColor(resolveColor(entry));

        holder.itemView.setOnClickListener(v -> openDetails(entry));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void openDetails(NetworkInspectorEntry entry) {

        if (entry == null || entry.id == null) {
            return;
        }

        Intent intent = new Intent(
                context,
                NetworkInspectorDetailActivity.class
        );

        intent.putExtra(
                NetworkInspectorDetailActivity.EXTRA_ENTRY_ID,
                entry.id
        );

        context.startActivity(intent);
    }

    private String buildSubtitle(NetworkInspectorEntry entry) {

        StringBuilder sb = new StringBuilder();

        String time = formatTime(entry.createdAt);

        if (!time.isEmpty()) {
            sb.append(time);
        }

        if (entry.durationMs > 0) {
            sb.append(" • ")
                    .append(entry.durationMs)
                    .append("ms");
        }

        return sb.toString();
    }

    private String preview(String value) {

        if (value == null) {
            return "";
        }

        if (value.length() <= URL_PREVIEW_LIMIT) {
            return value;
        }

        return value.substring(0, URL_PREVIEW_LIMIT)
                + "\n... truncado";
    }

    private String formatTime(long time) {
        return time > 0
                ? DATE_FORMAT.format(new Date(time))
                : "";
    }

    private String buildStatus(NetworkInspectorEntry entry) {
        return "(" + entry.statusCode + ")";
    }

    private int resolveColor(NetworkInspectorEntry entry) {

        int statusCode = entry.statusCode;

        if (statusCode >= 200 && statusCode < 300) {
            return Color.parseColor("#E8F5E9");
        }

        if (statusCode >= 300 && statusCode < 400) {
            return Color.parseColor("#E3F2FD");
        }

        if (statusCode >= 400 && statusCode < 500) {
            return Color.parseColor("#FFF3E0");
        }

        if (statusCode >= 500) {
            return Color.parseColor("#FFEBEE");
        }

        return Color.parseColor("#FAFAFA");
    }

    static class Holder extends RecyclerView.ViewHolder {

        TextView title;
        TextView subtitle;
        TextView url;

        Holder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            subtitle = itemView.findViewById(R.id.subtitle);
            url = itemView.findViewById(R.id.url);
        }
    }
}