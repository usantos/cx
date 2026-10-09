package br.gov.caixa.loterias.apostas.utils.shake;
import org.junit.Test;
import static org.junit.Assert.*;
public class ShakeDetectorTest {
    @Test public void duasAgitacoesComBloqueioAposConfirmacao() {
        ShakeDetector detector = new ShakeDetector();
        assertFalse(detector.detectar(30,0,0,0));
        assertTrue(detector.detectar(-30,0,0,100));
        assertFalse(detector.detectar(30,0,0,200));
        assertFalse(detector.detectar(30,0,0,1600));
        assertTrue(detector.detectar(-30,0,0,1700));
        detector.bloquear(2000);
        assertFalse(detector.detectar(30,0,0,2100));
        assertFalse(detector.detectar(-30,0,0,2200));
        assertFalse(detector.detectar(30,0,0,3500));
        assertTrue(detector.detectar(-30,0,0,3600));
    }
}
