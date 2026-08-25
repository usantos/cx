package br.gov.caixa.loterias.apostas.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogDoisBotoesListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogListener;
import br.gov.caixa.loterias.apostas.view.listener.OnEscolheApostaParamsListener;

public class PickerApostaParamsManager {

    private final Context context;
    private final List<ParametroSimulacao> arrayConcursos;
    private final boolean temEspelho;
    private final boolean selecionarConcurso;
    private final OnEscolheApostaParamsListener listener;

    private boolean espelho = false;
    private int concursoSelecionado = 0;
    private Dialog dialogConcursos;

    public PickerApostaParamsManager(Context context, List<ParametroSimulacao> arrayConcursos,
                     boolean temEspelho, OnEscolheApostaParamsListener listener) {
        this.context = context;
        this.arrayConcursos = arrayConcursos;
        this.temEspelho = temEspelho;
        this.listener = listener;
        this.selecionarConcurso = arrayConcursos.size() > 1;
    }

    public void iniciar() {
        dialogConcursos = buildDialogConcursos();
        if (temEspelho) {
            mostrarDialogEspelho();
        } else {
            proximoPasso();
        }
    }

    private void mostrarDialogEspelho() {
        DialogUtils.dialogSimNao(
                context,
                context.getString(R.string.incluir_espelho),
                new OnDialogDoisBotoesListener() {
                    @Override
                    public void PositiveButton(DialogInterface dialog, int which) {
                        espelho = true;
                        proximoPasso();
                    }

                    @Override
                    public void NegativeButton(DialogInterface dialog, int which) {
                        espelho = false;
                        proximoPasso();
                    }
                });
    }

    private void proximoPasso() {
        if (selecionarConcurso && dialogConcursos != null) {
            dialogConcursos.show();
        } else {
            mostrarDialogTeimosinhas();
        }
    }

    private void deConcursoParaTeimosinha(int position) {
        concursoSelecionado = position;
        if (!SelecaoConcursoUtils.isEspecial(arrayConcursos, concursoSelecionado)) {
            mostrarDialogTeimosinhas();
        } else {
            finalizar(0);
        }
    }

    private void mostrarDialogTeimosinhas() {
        ParametroJogoDTO parametro = arrayConcursos.get(concursoSelecionado).getParametroJogo();
        List<String> labels = buildLabelsTeimosinhas(parametro.getTeimosinhas());
        Dialog dialog = DialogUtils.buildDialogListItensNovo(
                context,
                context.getString(R.string.label_teimosinha),
                null,
                context.getString(R.string.teimosinhas_descricao),
                labels,
                context.getString(R.string.confirmar),
                context.getString(R.string.cancelar),
                new OnDialogListener() {
                    @Override
                    public void itemSelecionado(int position) {}

                    @Override
                    public void ok(int position) {
                        int qtd = parametro.getTeimosinhas().get(position);
                        finalizar(qtd);
                    }

                    @Override
                    public void cancelar() {}
                }
        );
        if (dialog != null) {
            dialog.show();
        }
    }

    private void finalizar(int qtdTeimosinhas) {
        int tipoConcurso = SelecaoConcursoUtils.isEspecial(arrayConcursos, concursoSelecionado) ? 2 : 1;
        listener.selecionado(tipoConcurso, qtdTeimosinhas, espelho);
    }

    private Dialog buildDialogConcursos() {
        return SelecaoConcursoUtils.buildDialogConcursos(context, arrayConcursos, new OnDialogListener() {
            @Override
            public void itemSelecionado(int position) {
                if (dialogConcursos == null) return;
                Button btn = dialogConcursos.findViewById(R.id.button_positivo_itens);
                if (btn == null) return;
                btn.setText(SelecaoConcursoUtils.isEspecial(arrayConcursos, position) ? R.string.confirmar : R.string.proximo);
            }

            @Override
            public void ok(int position) {
                deConcursoParaTeimosinha(position);
            }

            @Override
            public void cancelar() {}
        });
    }

    private List<String> buildLabelsTeimosinhas(List<Integer> teimosinhas) {
        List<String> result = new ArrayList<>();
        for (Integer n : teimosinhas) {
            if (n == 0) {
                result.add(context.getString(R.string.sem_teimosinha));
            } else if (n == 1) {
                result.add(n + context.getString(R.string.espaco_concurso));
            } else {
                result.add(n + context.getString(R.string.espaco_concursos));
            }
        }
        return result;
    }
}