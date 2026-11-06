package br.gov.caixa.loterias.apostas.utils;

import android.view.View;
import android.widget.Button;

import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;

public class AccessibilityUtils {
    public static void setBotao(
            View component
    ){
        ViewCompat.setAccessibilityDelegate(
                component,
                new AccessibilityDelegateCompat() {

                    @Override
                    public void onInitializeAccessibilityNodeInfo(
                            View host,
                            AccessibilityNodeInfoCompat info
                    ) {
                        super.onInitializeAccessibilityNodeInfo(
                                host,
                                info
                        );

                        info.setClassName(
                                Button.class.getName()
                        );
                    }
                }
        );
    }
}
