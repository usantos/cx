package br.gov.caixa.loterias.apostas.debug.adapter;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.debug.helpers.NetworkInspectorSection;
import br.gov.caixa.loterias.apostas.debug.activity.PayloadViewerActivity;
import br.gov.caixa.loterias.apostas.debug.helpers.JsonHighlighter;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.NetworkInspector;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.NetworkInspectorEntry;

public class NetworkInspectorSectionAdapter extends RecyclerView.Adapter<NetworkInspectorSectionAdapter.Holder> {

    private static final int PREVIEW_LIMIT = 700;

    private final Context context;
    private final String entryId;
    private final ArrayList<NetworkInspectorSection> sections;
    private final LruCache<String, CharSequence> previewCache =
            new LruCache<>(50);

    public NetworkInspectorSectionAdapter(
            Context context,
            String entryId,
            ArrayList<NetworkInspectorSection> sections
    ) {
        this.context = context;
        this.entryId = entryId;
        this.sections = sections != null ? sections : new ArrayList<>();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_network_section, parent, false);

        return new Holder(view);
    }

    private static final int PRETTY_LIMIT = 50000;

    @Override
    public void onBindViewHolder(Holder holder, int position) {

        NetworkInspectorSection section = sections.get(position);

        String value = getValue(section.key);

        holder.title.setText(section.title);

        holder.preview.setTypeface(Typeface.MONOSPACE);
        holder.preview.setMaxLines(8);

        String cacheKey = entryId + "_" + section.key;

        CharSequence cached = previewCache.get(cacheKey);

        if (cached == null) {

            cached = buildPreview(value);

            previewCache.put(cacheKey, cached);
        }

        holder.preview.setText(cached);

        holder.itemView.setOnClickListener(v -> openPayload(section));

        holder.copy.setOnClickListener(v -> copy(value));
    }

    private CharSequence buildPreview(String value) {

        if (value == null || value.trim().isEmpty()) {
            return "";
        }

        if (value.length() > PRETTY_LIMIT) {

            return JsonHighlighter.format(
                            preview(value, PREVIEW_LIMIT)
            );
        }

        return JsonHighlighter.format(value);
    }

    @Override
    public int getItemCount() {
        return sections.size();
    }

    private void openPayload(NetworkInspectorSection section) {
        Intent intent = new Intent(context, PayloadViewerActivity.class);
        intent.putExtra(PayloadViewerActivity.EXTRA_ENTRY_ID, entryId);
        intent.putExtra(PayloadViewerActivity.EXTRA_SECTION_KEY, section.key);
        intent.putExtra(PayloadViewerActivity.EXTRA_SECTION_TITLE, section.title);
        context.startActivity(intent);
    }

    private String getValue(String key) {
        NetworkInspectorEntry entry = NetworkInspector.getEntryById(entryId);

        if (entry == null || key == null) {
            return "";
        }

        switch (key) {

            case "http":
                return safe(entry.method);

            case "status":
                return String.valueOf(entry.statusCode);

            case "url":
                return safe(entry.url);

            case "path":
                return safe(entry.path);

            case "duration":
                return entry.durationMs > 0 ? entry.durationMs + "ms" : "";

            case "queryParams":
                return safe(entry.queryParams);

            case "headers":
                return safe(entry.headers);

            case "originalBody":
                return safe(entry.originalBody);

            case "preparedBody":
                return safe(entry.preparedBody);

            case "responseBody":
                return safe(entry.responseBody);

            case "errorMessage":
                return safe(entry.errorMessage);

            case "errorBody":
                return safe(entry.errorBody);

            default:
                return "";
        }
    }

    private String safe(String value) {
        return value != null ? value : "";
    }

    private String preview(String value, int limit) {
        if (value == null) {
            return "";
        }

        if (value.length() <= limit) {
            return value;
        }

        return value.substring(0, limit)
                + "\n\n... toque para abrir completo em blocos";
    }

    private void copy(String content) {
        try {
            ClipboardManager clipboard =
                    (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

            if (clipboard == null) {
                Toast.makeText(context, "Clipboard indisponível", Toast.LENGTH_SHORT).show();
                return;
            }

            ClipData clip = ClipData.newPlainText("network_section", content);
            clipboard.setPrimaryClip(clip);

            Toast.makeText(context, "Seção copiada", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(context, "Conteúdo muito grande para copiar", Toast.LENGTH_LONG).show();
        }
    }

    static class Holder extends RecyclerView.ViewHolder {

        TextView title;
        TextView preview;
        TextView copy;

        Holder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.sectionTitle);
            preview = itemView.findViewById(R.id.sectionPreview);
            copy = itemView.findViewById(R.id.sectionCopy);
        }
    }
}
