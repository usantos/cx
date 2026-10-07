package br.gov.caixa.loterias.apostas.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;

public final class IconAliasManager {

    private IconAliasManager() {
    }

    public static void applyIcon(Context context, IconeAppEnum icon) {
        String currentSuffix = getCurrentAliasSuffix();

        if (icon.get().equals(currentSuffix)) {
            return;
        }

        ComponentName target = new ComponentName(context, aliasClassName(
                IconAliasManager.class.getPackage().getName(),
                icon.get()
        ));
        setIconStatus(context, target, true);

        if (currentSuffix != null) {
            setIconStatus(context, new ComponentName(context, aliasClassName(
                    IconAliasManager.class.getPackage().getName(),
                    currentSuffix
            )), false);
        }
    }

    static String aliasClassName(String implementationPackage, String aliasSuffix) {
        return aliasClassPrefix(implementationPackage) + aliasSuffix;
    }

    private static String aliasClassPrefix(String implementationPackage) {
        int separator = implementationPackage.lastIndexOf('.');
        if (separator < 1) {
            throw new IllegalArgumentException("Expected an app implementation package");
        }
        return implementationPackage.substring(0, separator);
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
