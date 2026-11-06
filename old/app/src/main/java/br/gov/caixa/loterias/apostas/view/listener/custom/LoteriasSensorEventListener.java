package br.gov.caixa.loterias.apostas.view.listener.custom;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class LoteriasSensorEventListener {

	public LoteriasSensorEventListener(float mAccel, float mAccelCurrent, float mAccelLast) {
		this.mAccel = mAccel;
		this.mAccelCurrent = mAccelCurrent;
		this.mAccelLast = mAccelLast;
	}

	private float mAccel; // acceleration apart from gravity
	private float mAccelCurrent; // current acceleration including gravity
	private float mAccelLast; // last acceleration including gravity

	public SensorEventListener getSensor() {
		return new SensorEventListener() {

			public void onSensorChanged(SensorEvent se) {
				float x = se.values[0];
				float y = se.values[1];
				float z = se.values[2];
				mAccelLast = mAccelCurrent;
				mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
				float delta = mAccelCurrent - mAccelLast;
				mAccel = mAccel * 0.9f + delta; // perform low-cut filter

				if (mAccel > 10 && mAccel < 16) {
					//if (!isNumerosAleatoriosShake) {
                    /*isNumerosAleatoriosShake = true;
                    botaoLimparAposta.performClick();
                    preencheNumerosAleatorios();*/
					//}
				}
				//System.out.println(mAccel);
			}

			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}

		};
	}
}
