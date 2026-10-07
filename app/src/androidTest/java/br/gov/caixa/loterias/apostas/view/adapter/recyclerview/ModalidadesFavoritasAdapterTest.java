package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.widget.FrameLayout;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import java.util.Collections;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import static org.junit.Assert.*;

public class ModalidadesFavoritasAdapterTest {
    @Test public void rowSelectsModalityAndDeleteOnlyRemoves() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            Context context = new ContextThemeWrapper(InstrumentationRegistry.getInstrumentation().getTargetContext(),
                    androidx.appcompat.R.style.Theme_AppCompat);
            ModalidadeEnum[] selected = {null};
            ModalidadeEnum[] removed = {null};
            ModalidadesFavoritasAdapter adapter = new ModalidadesFavoritasAdapter(
                    modalidade -> removed[0] = modalidade, modalidade -> selected[0] = modalidade);
            adapter.setFavorites(Collections.singletonList(ModalidadeEnum.LOTOFACIL));
            ModalidadesFavoritasAdapter.Holder holder = adapter.onCreateViewHolder(new FrameLayout(context), 0);
            adapter.onBindViewHolder(holder, 0);
            holder.itemView.performClick();
            assertEquals(ModalidadeEnum.LOTOFACIL, selected[0]);
            assertNull(removed[0]);
            selected[0] = null;
            holder.itemView.findViewById(R.id.excluirModalidadeFavorita).performClick();
            assertEquals(ModalidadeEnum.LOTOFACIL, removed[0]);
            assertNull(selected[0]);
        });
    }
}
