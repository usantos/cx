package br.gov.caixa.loterias.apostas.debug.activity;

import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import br.gov.caixa.loterias.apostas.BuildConfig;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.debug.adapter.NetworkInspectorAdapter;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.NetworkInspector;

public class NetworkInspectorActivity extends AppCompatActivity {

    private NetworkInspectorAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!BuildConfig.ENABLE_NETWORK_INSPECTOR) {
            finish();
            return;
        }

        setContentView(R.layout.activity_network_inspector);

        RecyclerView recycler = findViewById(R.id.recycler);
        Button refresh = findViewById(R.id.btnRefresh);
        Button clear = findViewById(R.id.btnClear);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setHasFixedSize(true);

        adapter = new NetworkInspectorAdapter(this);
        recycler.setAdapter(adapter);

        refresh.setOnClickListener(v -> reload());

        clear.setOnClickListener(v -> {
            NetworkInspector.clear();
            reload();
        });

        reload();
    }

    @Override
    protected void onResume() {
        super.onResume();
        reload();
    }

    private void reload() {
        adapter.submit(NetworkInspector.getEntriesSnapshot());
    }
}