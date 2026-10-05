package br.gov.caixa.loterias.apostas.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;

public final class IconAliasManager {

    private IconAliasManager() {
    }

    public static void applyIcon(Context context, IconeAppEnum icon) {
        String pkg = context.getPackageName().replace(".hmp", "");
        String currentSuffix = getCurrentAliasSuffix();

        if (icon.get().equals(currentSuffix)) {
            return;
        }

        ComponentName target = new ComponentName(context, pkg + icon.get());
        setIconStatus(context, target, true);

        if (currentSuffix != null) {
            setIconStatus(context, new ComponentName(context, pkg + currentSuffix), false);
        }
    }

    private static String getCurrentAliasSuffix() {
        return SharedPreferencesUtils.getValorString(ConfiguracoesEnum.LAUNCHER_ICON.get(), IconeAppEnum.DEFAULT.get());
    }

    private static void setIconStatus(Context context, ComponentName alias, boolean enabled) {
        int state;
        if (enabled) {
            state = PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
        } else {
            boolean isDefaultAlias = alias.getClassName().endsWith(IconeAppEnum.DEFAULT.get());
            state = isDefaultAlias
                    ? PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                    : PackageManager.COMPONENT_ENABLED_STATE_DEFAULT;
        }
        context.getPackageManager().setComponentEnabledSetting(
                alias,
                state,
                PackageManager.DONT_KILL_APP
        );
    }
}
