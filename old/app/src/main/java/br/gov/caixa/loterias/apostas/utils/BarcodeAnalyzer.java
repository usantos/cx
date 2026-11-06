package br.gov.caixa.loterias.apostas.utils;

import androidx.annotation.OptIn;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;

public class BarcodeAnalyzer implements ImageAnalysis.Analyzer {

    public interface Listener {
        void onScan(String value);
    }

    private final Listener listener;
    private final BarcodeScanner scanner =
            BarcodeScanning.getClient();

    public BarcodeAnalyzer(Listener listener) {
        this.listener = listener;
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    @Override
    public void analyze(ImageProxy imageProxy) {
        if (imageProxy.getImage() == null) {
            imageProxy.close();
            return;
        }

        InputImage image = InputImage.fromMediaImage(
                imageProxy.getImage(),
                imageProxy.getImageInfo().getRotationDegrees()
        );

        scanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    for (Barcode b : barcodes) {
                        if (b.getRawValue() != null) {
                            listener.onScan(b.getRawValue());
                        }
                    }
                })
                .addOnCompleteListener(task -> imageProxy.close());
    }
}
