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

import br.gov.caixa.loterias.apostas.BuildConfig;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.debug.adapter.PayloadChunkAdapter;
import br.gov.caixa.loterias.apostas.debug.helpers.JsonHighlighter;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.NetworkInspector;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.NetworkInspectorEntry;

public class PayloadViewerActivity extends AppCompatActivity {

    public static final String EXTRA_ENTRY_ID = "extra_entry_id";
    public static final String EXTRA_SECTION_KEY = "extra_section_key";
    public static final String EXTRA_SECTION_TITLE = "extra_section_title";

    private String payload;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!BuildConfig.ENABLE_NETWORK_INSPECTOR) {
            finish();
            return;
        }

        setContentView(R.layout.activity_payload_viewer);

        String entryId = getIntent().getStringExtra(EXTRA_ENTRY_ID);
        String sectionKey = getIntent().getStringExtra(EXTRA_SECTION_KEY);
        String sectionTitle = getIntent().getStringExtra(EXTRA_SECTION_TITLE);

        setTitle(sectionTitle != null ? sectionTitle : "Payload");

        NetworkInspectorEntry entry = NetworkInspector.getEntryById(entryId);

        if (entry == null) {
            Toast.makeText(this, "Registro não encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        payload = getPayload(entry, sectionKey);

        RecyclerView recycler = findViewById(R.id.recyclerPayload);
        Button copy = findViewById(R.id.btnCopyPayload);
        Button share = findViewById(R.id.btnSharePayload);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setHasFixedSize(false);
        CharSequence formattedPayload =
                JsonHighlighter.format(payload);

        recycler.setAdapter(
                new PayloadChunkAdapter(formattedPayload)
        );

        copy.setOnClickListener(v -> copy(payload));
        share.setOnClickListener(v -> share(payload));
    }

    private String getPayload(NetworkInspectorEntry entry, String key) {
        if (key == null) {
            return "";
        }

        switch (key) {
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

    private void copy(String content) {
        try {
            ClipboardManager clipboard =
                    (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

            if (clipboard == null) {
                Toast.makeText(this, "Clipboard indisponível", Toast.LENGTH_SHORT).show();
                return;
            }

            ClipData clip = ClipData.newPlainText("network_payload", content);
            clipboard.setPrimaryClip(clip);

            Toast.makeText(this, "Payload copiado", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "Payload muito grande para copiar", Toast.LENGTH_LONG).show();
        }
    }

    private void share(String content) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, content);

            startActivity(Intent.createChooser(intent, "Compartilhar payload"));

        } catch (Exception e) {
            Toast.makeText(this, "Payload muito grande para compartilhar", Toast.LENGTH_LONG).show();
        }
    }
}