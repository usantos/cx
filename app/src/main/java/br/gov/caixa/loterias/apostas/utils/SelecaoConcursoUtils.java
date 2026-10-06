package br.gov.caixa.loterias.apostas.utils;

import android.app.Dialog;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoConcursoEnum;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogListener;
import br.gov.caixa.loterias.apostas.view.listener.OnSelecionaConcursoDialogListener;

public class SelecaoConcursoUtils {

    public static void selecionarConcurso(Context context, List<ParametroSimulacao> arrayConcursos,
                                          OnSelecionaConcursoDialogListener listener) {
        if (arrayConcursos.size() <= 1) {
            listener.selecionado(isEspecial(arrayConcursos, 0) ? 2 : 1);
            return;
        }

        Dialog dialog = buildDialogConcursos(context, arrayConcursos, new OnDialogListener() {
            @Override
            public void itemSelecionado(int position) {}

            @Override
            public void ok(int position) {
                listener.selecionado(isEspecial(arrayConcursos, position) ? 2 : 1);
            }

            @Override
            public void cancelar() {}
        });

        if (dialog != null) dialog.show();
    }

    static Dialog buildDialogConcursos(Context context, List<ParametroSimulacao> arrayConcursos,
                                               OnDialogListener listener) {
        return DialogUtils.buildDialogListItensNovo(
                context,
                context.getString(R.string.tipo_de_concurso),
                null,
                context.getString(R.string.escolha_o_tipo_de_concurso),
                buildConcursosLabels(arrayConcursos),
                context.getString(R.string.confirmar),
                context.getString(R.string.cancelar),
                listener
        );
    }

    static boolean isEspecial(List<ParametroSimulacao> arrayConcursos, int index) {
        return arrayConcursos.get(index).getParametroJogo().getConcurso().getTipoConcurso() == TipoConcursoEnum.ESPECIAL;
    }

    private static List<String> buildConcursosLabels(List<ParametroSimulacao> arrayConcursos) {
        List<String> labels = new ArrayList<>();
        for (ParametroSimulacao ps : arrayConcursos) {
            if (ps.getParametroJogo().getConcurso().getTipoConcurso() == TipoConcursoEnum.ESPECIAL) {
                labels.add(ps.getParametroJogo().getConcurso().getModalidadeDetalhada().getDescricaoEspecial());
            } else {
                labels.add(ps.getParametroJogo().getConcurso().getModalidadeDetalhada().getDescricao());
            }
        }
        return labels;
    }
}