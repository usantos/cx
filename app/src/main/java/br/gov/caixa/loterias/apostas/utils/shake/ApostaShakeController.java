package br.gov.caixa.loterias.apostas.utils.shake;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogDoisBotoesListener;

/** Um controlador por tela de montagem; nunca envia a aposta ao carrinho. */
public final class ApostaShakeController implements SensorEventListener {
    public interface Volante {
        boolean disponivel();
        boolean completo();
        void preencher(boolean renovar);
    }
    private final Activity activity;
    private final Volante volante;
    private final SensorManager sensores;
    private final ShakeDetector detector = new ShakeDetector();
    private Dialog confirmacao;
    private boolean ativo;

    public ApostaShakeController(Activity activity, Volante volante) {
        this.activity = activity;
        this.volante = volante;
        sensores = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
    }

    public void iniciar() {
        parar();
        if (!ApostaShakePreferences.isAtiva() || sensores == null) return;
        Sensor sensor = sensores.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (sensor != null) ativo = sensores.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
    }

    public void parar() {
        ativo = false;
        if (sensores != null) sensores.unregisterListener(this);
        if (confirmacao != null) confirmacao.dismiss();
        confirmacao = null;
        detector.reset();
    }

    @Override public void onSensorChanged(SensorEvent evento) {
        if (!ativo || !ApostaShakePreferences.isAtiva() || activity.isFinishing()
                || !activity.getWindow().getDecorView().hasWindowFocus()
                || confirmacao != null || !volante.disponivel()) {
            detector.reset();
            return;
        }
        if (!detector.detectar(evento.values[0], evento.values[1], evento.values[2], SystemClock.elapsedRealtime())) return;
        if (!volante.completo()) {
            volante.preencher(false);
            return;
        }
        confirmacao = DialogUtils.dialogTituloDoisBotoesReturn(activity,
                activity.getString(R.string.label_atencao), activity.getString(R.string.aposta_shake_confirmacao),
                activity.getString(R.string.aposta_shake_sim), activity.getString(R.string.aposta_shake_nao),
                new OnDialogDoisBotoesListener() {
                    @Override public void PositiveButton(DialogInterface dialog, int which) {
                        if (ativo && volante.disponivel()) volante.preencher(true);
                    }
                    @Override public void NegativeButton(DialogInterface dialog, int which) { }
                });
        if (confirmacao != null) {
            confirmacao.setOnDismissListener(dialog -> { confirmacao = null; detector.bloquear(SystemClock.elapsedRealtime()); });
            confirmacao.show();
        }
    }

    @Override public void onAccuracyChanged(Sensor sensor, int accuracy) { }
}
