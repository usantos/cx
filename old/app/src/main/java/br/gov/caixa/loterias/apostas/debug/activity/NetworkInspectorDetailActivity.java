package br.gov.caixa.loterias.apostas.debug.activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import br.gov.caixa.loterias.apostas.BuildConfig;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.debug.helpers.NetworkInspectorSection;
import br.gov.caixa.loterias.apostas.debug.adapter.NetworkInspectorSectionAdapter;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.NetworkInspector;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.NetworkInspectorEntry;

public class NetworkInspectorDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ENTRY_ID = "extra_entry_id";

    private String entryId;
    private NetworkInspectorEntry entry;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!BuildConfig.ENABLE_NETWORK_INSPECTOR) {
            finish();
            return;
        }

        setContentView(R.layout.activity_network_inspector_detail);

        entryId = getIntent().getStringExtra(EXTRA_ENTRY_ID);
        entry = NetworkInspector.getEntryById(entryId);

        if (entry == null) {
            Toast.makeText(this, "Registro não encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setTitle(buildTitle(entry));

        RecyclerView recycler = findViewById(R.id.recyclerSections);
        Button copyAll = findViewById(R.id.btnCopyAll);
        Button shareAll = findViewById(R.id.btnShareAll);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setHasFixedSize(false);

        NetworkInspectorSectionAdapter adapter =
                new NetworkInspectorSectionAdapter(this, entryId, buildSections(entry));

        recycler.setAdapter(adapter);

        copyAll.setOnClickListener(v -> copy(buildDetail(entry)));
        shareAll.setOnClickListener(v -> share(buildDetail(entry)));
    }

    private String buildTitle(NetworkInspectorEntry entry) {
        String method = entry.method != null ? entry.method : "";
        return method + " (" + entry.statusCode + ")";
    }

    private ArrayList<NetworkInspectorSection> buildSections(NetworkInspectorEntry entry) {
        ArrayList<NetworkInspectorSection> sections = new ArrayList<>();

        add(sections, "HTTP", "http", entry.method);
        add(sections, "STATUS", "status", String.valueOf(entry.statusCode));
        add(sections, "URL", "url", entry.url);
        add(sections, "PATH", "path", entry.path);
        add(sections, "DURAÇÃO", "duration", entry.durationMs > 0 ? entry.durationMs + "ms" : "");
        add(sections, "QUERY PARAMS", "queryParams", entry.queryParams);
        add(sections, "HEADERS", "headers", entry.headers);
        add(sections, "BODY ORIGINAL", "originalBody", entry.originalBody);
        add(sections, "BODY ENVIADO", "preparedBody", entry.preparedBody);
        add(sections, "RESPONSE", "responseBody", entry.responseBody);
        add(sections, "ERROR MESSAGE", "errorMessage", entry.errorMessage);
        add(sections, "ERROR BODY", "errorBody", entry.errorBody);

        return sections;
    }

    private void add(ArrayList<NetworkInspectorSection> sections, String title, String key, String value) {
        if (value == null || value.trim().isEmpty()) {
            return;
        }

        sections.add(new NetworkInspectorSection(title, key, value.length()));
    }

    private String buildDetail(NetworkInspectorEntry entry) {
        StringBuilder sb = new StringBuilder();

        append(sb,"HTTP",entry.method);
        append(sb, "STATUS", String.valueOf(entry.statusCode));
        append(sb, "URL", entry.url);
        append(sb, "PATH", entry.path);
        append(sb, "DURAÇÃO", entry.durationMs > 0 ? entry.durationMs + "ms" : "");
        append(sb, "QUERY PARAMS", entry.queryParams);
        append(sb, "HEADERS", entry.headers);
        append(sb, "BODY ORIGINAL", entry.originalBody);
        append(sb, "BODY ENVIADO", entry.preparedBody);
        append(sb, "RESPONSE", entry.responseBody);
        append(sb, "ERROR MESSAGE", entry.errorMessage);
        append(sb, "ERROR BODY", entry.errorBody);

        return sb.toString();
    }

    private void append(StringBuilder sb, String title, String value) {
        if (value == null || value.trim().isEmpty()) {
            return;
        }

        sb.append("\n\n==== ")
                .append(title)
                .append(" ====\n")
                .append(value);
    }

    private void copy(String content) {
        try {
            ClipboardManager clipboard =
                    (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

            if (clipboard == null) {
                Toast.makeText(this, "Clipboard indisponível", Toast.LENGTH_SHORT).show();
                return;
            }

            ClipData clip = ClipData.newPlainText("network_log", content);
            clipboard.setPrimaryClip(clip);

            Toast.makeText(this, "Copiado", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "Conteúdo muito grande para copiar", Toast.LENGTH_LONG).show();
        }
    }

    private void share(String content) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, content);

            startActivity(Intent.createChooser(intent, "Compartilhar requisição"));

        } catch (Exception e) {
            Toast.makeText(this, "Conteúdo muito grande para compartilhar", Toast.LENGTH_LONG).show();
        }
    }
}