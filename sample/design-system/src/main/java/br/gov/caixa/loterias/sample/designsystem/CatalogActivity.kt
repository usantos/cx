package br.gov.caixa.loterias.sample.designsystem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

/** Ponto de entrada independente que não inicializa o grafo do aplicativo Loterias. */
class CatalogActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { DesignSystemCatalog() }
    }
}
