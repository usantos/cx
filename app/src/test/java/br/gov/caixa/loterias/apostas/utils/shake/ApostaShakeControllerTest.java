package br.gov.caixa.loterias.apostas.utils.shake;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.hardware.*;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import org.junit.Test;
import org.mockito.MockedStatic;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicReference;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogDoisBotoesListener;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class ApostaShakeControllerTest {
    @Test public void primeiroShakePreencheESegundoSimRenova() throws Exception {
        testarConfirmacao(true, false);
    }
    @Test public void segundoNaoPreservaAposta() throws Exception {
        testarConfirmacao(false, false);
    }
    @Test public void confirmacaoDepoisDePausarNaoAlteraVolante() throws Exception {
        testarConfirmacao(true, true);
    }
    private void testarConfirmacao(boolean sim, boolean pausar) throws Exception {
        try (MockedStatic<ApostaShakePreferences> prefs = mockStatic(ApostaShakePreferences.class);
             MockedStatic<SystemClock> clock = mockStatic(SystemClock.class);
             MockedStatic<DialogUtils> dialogs = mockStatic(DialogUtils.class)) {
            prefs.when(ApostaShakePreferences::isAtiva).thenReturn(true);
            Activity activity = mock(Activity.class);
            SensorManager sensores = mock(SensorManager.class);
            when(activity.getSystemService(Context.SENSOR_SERVICE)).thenReturn(sensores);
            when(sensores.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)).thenReturn(mock(Sensor.class));
            when(sensores.registerListener(any(SensorEventListener.class), any(Sensor.class), anyInt())).thenReturn(true);
            Window window = mock(Window.class);
            View decor = mock(View.class);
            when(activity.getWindow()).thenReturn(window);
            when(window.getDecorView()).thenReturn(decor);
            when(decor.hasWindowFocus()).thenReturn(true);
            when(activity.getString(anyInt())).thenReturn("texto");
            ApostaShakeController.Volante volante = mock(ApostaShakeController.Volante.class);
            when(volante.disponivel()).thenReturn(true);
            Dialog dialog = mock(Dialog.class);
            AtomicReference<OnDialogDoisBotoesListener> listener = new AtomicReference<>();
            dialogs.when(() -> DialogUtils.dialogTituloDoisBotoesReturn(any(), anyString(),
                    anyString(), anyString(), anyString(), any())).thenAnswer(call -> {
                listener.set(call.getArgument(5));
                return dialog;
            });
            ApostaShakeController controller = new ApostaShakeController(activity, volante);
            controller.iniciar();
            evento(controller, clock, 30, 0);
            evento(controller, clock, -30, 100);
            verify(volante).preencher(false);
            when(volante.completo()).thenReturn(true);
            evento(controller, clock, 30, 1600);
            evento(controller, clock, -30, 1700);
            assertNotNull(listener.get());
            verify(dialog).show();
            if (pausar) controller.parar();
            if (sim) listener.get().PositiveButton(dialog, -1);
            else listener.get().NegativeButton(dialog, -2);
            verify(volante, times(sim && !pausar ? 1 : 0)).preencher(true);
        }
    }
    private void evento(ApostaShakeController controller, MockedStatic<SystemClock> clock,
                        float x, long tempo) throws Exception {
        clock.when(SystemClock::elapsedRealtime).thenReturn(tempo);
        SensorEvent evento = mock(SensorEvent.class);
        Field values = SensorEvent.class.getField("values");
        values.setAccessible(true);
        values.set(evento, new float[] {x,0,0});
        controller.onSensorChanged(evento);
    }
}
