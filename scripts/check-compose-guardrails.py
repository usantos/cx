#!/usr/bin/env python3
"""Exercise the actual Gradle UI gates with negative fixtures, restoring every file.

Run separately from compilation/instrumentation: fixtures temporarily live in src/.
No emulator or backend is needed. Logs stay under build/verification/.
"""

import json
import subprocess
from copy import deepcopy
from pathlib import Path
from xml.etree import ElementTree as ET


ROOT = Path(__file__).resolve().parent.parent
OUTPUT = ROOT / "build/verification/compose-guardrails"
ANDROID = "{http://schemas.android.com/apk/res/android}"


def check_debug_manifest(results):
    manifest = ROOT / "app/src/debug/AndroidManifest.xml"
    original = manifest.read_bytes()
    root = ET.fromstring(original)
    application = root.find("application")
    host = application.find(f"activity[@{ANDROID}name='.HomeDrawerTestActivity']")
    if host is None or len(application) != 1:
        raise RuntimeError("O manifesto debug deve preservar somente o hospedeiro Compose interno")

    # Cada mutação deve ser rejeitada pela tarefa real, sem adicionar componentes ao contrato.
    mutations = {
        "permission": lambda r, a, h: ET.SubElement(r, "uses-permission", {ANDROID + "name": "android.permission.CAMERA"}),
        "root-attribute": lambda r, a, h: r.set("package", "other.package"),
        "wrong-root": lambda r, a, h: setattr(r, "tag", "other"),
        "extra-namespace": lambda r, a, h: r.set("xmlns:other", "https://invalid.example/android"),
        "application-attribute": lambda r, a, h: a.set(ANDROID + "debuggable", "true"),
        "extra-application": lambda r, a, h: ET.SubElement(r, "application"),
        "extra-activity": lambda r, a, h: ET.SubElement(a, "activity", {ANDROID + "name": ".ForbiddenActivity"}),
        "removed-inspector": lambda r, a, h: ET.SubElement(a, "activity", {ANDROID + "name": ".NetworkInspectorActivity"}),
        "duplicate-host": lambda r, a, h: a.append(deepcopy(h)),
        "missing-host": lambda r, a, h: a.remove(h),
        "renamed-host": lambda r, a, h: h.set(ANDROID + "name", ".ForbiddenActivity"),
        "exported-host": lambda r, a, h: h.set(ANDROID + "exported", "true"),
        "host-filter": lambda r, a, h: ET.SubElement(h, "intent-filter"),
        "host-attribute": lambda r, a, h: h.set(ANDROID + "theme", "@style/Forbidden"),
        "text": lambda r, a, h: setattr(h, "text", "unexpected content"),
    }
    for component in ("service", "provider", "receiver", "activity-alias"):
        mutations[component] = lambda r, a, h, tag=component: ET.SubElement(a, tag, {ANDROID + "name": ".ForbiddenComponent"})

    ET.register_namespace("android", ANDROID[1:-1])
    try:
        results.append(run("debug-valid", "verifyComposeMigration"))
        for name, mutate in mutations.items():
            changed = deepcopy(root)
            app = changed.find("application")
            test_host = app.find(f"activity[@{ANDROID}name='.HomeDrawerTestActivity']")
            mutate(changed, app, test_host)
            manifest.write_bytes(ET.tostring(changed, encoding="utf-8"))
            results.append(run(f"debug-{name}", "verifyComposeMigration", "Manifest debug"))
    finally:
        manifest.write_bytes(original)


def check_launcher_resources(results):
    # Os presets são dados gráficos explícitos; nenhuma exceção permite Views ou chaves arbitrárias.
    fixtures = {
        "icon-layout": ("app/src/main/res/mipmap-anydpi/ic_launcher_outubro_rosa.xml", lambda s: s.replace("adaptive-icon", "LinearLayout"), "Ícone adaptativo"),
        "icon-foreign-reference": ("app/src/main/res/mipmap-anydpi/ic_launcher_outubro_rosa.xml", lambda s: s.replace("@mipmap/ic_launcher_outubro_rosa_foreground", "@mipmap/ic_launcher_foreground"), "Ícone adaptativo"),
        "vector-layout": ("app/src/main/res/drawable/ic_launcher_outubro_rosa_background.xml", lambda s: s.replace("</vector>", "<TextView /></vector>"), "Fundo da campanha"),
        "combo-vector-layout": ("core/design-system/src/main/res/drawable/legacy_combo_trevo_loteca_detalhe_combo.xml", lambda s: s.replace("</vector>", "<TextView /></vector>"), "Arte Combo"),
        "drawable-alias-layout": ("core/design-system/src/main/res/values/drawable_aliases.xml", lambda s: s.replace("</resources>", "<TextView /></resources>"), "Aliases devem preservar"),
        "drawable-alias-reference": ("core/design-system/src/main/res/values/drawable_aliases.xml", lambda s: s.replace("@drawable/caixads_home_clover", "@drawable/foreign"), "Aliases devem preservar"),
        "volantes-layout": ("core/design-system/src/main/res/drawable-mdpi/ic_volantes.xml", lambda s: s.replace("</layer-list>", "<TextView /></layer-list>"), "Recurso ic_volantes"),
        "defaults-force-update": ("app/src/main/res/xml/remote_config_defaults.xml", lambda s: s.replace("<value>false</value>", "<value>true</value>"), "Remote Config defaults"),
        "defaults-extra-key": ("app/src/main/res/xml/remote_config_defaults.xml", lambda s: s.replace("</defaultsMap>", "<entry><key>unexpected</key><value>false</value></entry></defaultsMap>"), "Remote Config defaults"),
        "defaults-invalid-calendar": ("app/src/main/res/xml/remote_config_defaults.xml", lambda s: s.replace('"10-31"', '"10-32"'), "Remote Config defaults"),
        "defaults-invalid-json": ("app/src/main/res/xml/remote_config_defaults.xml", lambda s: s.replace('"campaigns": [', '"campaigns": invalid ['), "Remote Config defaults"),
    }
    for name, (relative, mutate, message) in fixtures.items():
        path = ROOT / relative
        original = path.read_bytes()
        changed = mutate(original.decode())
        if changed == original.decode():
            raise RuntimeError(f"A fixture não mudou o recurso: {name}")
        try:
            path.write_text(changed)
            results.append(run(name, "verifyComposeMigration", message))
        finally:
            path.write_bytes(original)
    results.append(run("launcher-resources-valid", "verifyComposeMigration"))


def check_receipt_paths(results):
    paths = ROOT / "app/src/main/res/xml/receipt_file_paths.xml"
    original = paths.read_bytes()
    fixtures = {
        "wrong-root": '<resources><cache-path name="comprovantes" path="receipts/" /></resources>',
        "root-attribute": '<paths other="value"><cache-path name="comprovantes" path="receipts/" /></paths>',
        "empty": '<paths />',
        "extra-cache": '<paths><cache-path name="comprovantes" path="receipts/" /><cache-path name="all" path="." /></paths>',
        "root-path": '<paths><root-path name="comprovantes" path="receipts/" /></paths>',
        "external-path": '<paths><external-path name="comprovantes" path="receipts/" /></paths>',
        "extra-attribute": '<paths><cache-path name="comprovantes" path="receipts/" other="value" /></paths>',
        "nested": '<paths><cache-path name="comprovantes" path="receipts/"><cache-path /></cache-path></paths>',
        "text": '<paths>unexpected content<cache-path name="comprovantes" path="receipts/" /></paths>',
    }
    for name, path in {"all-cache": ".", "absolute": "/", "parent": "../", "traversal": "receipts/../"}.items():
        fixtures[name] = f'<paths><cache-path name="comprovantes" path="{path}" /></paths>'
    try:
        for name, xml in fixtures.items():
            paths.write_text(xml.replace("<paths", f'<paths xmlns:android="{ANDROID[1:-1]}"', 1), encoding="utf-8")
            results.append(run(f"receipts-{name}", "verifyComposeMigration", "FileProvider de comprovantes"))
    finally:
        paths.write_bytes(original)


def run(name, task, expected_error=None):
    result = subprocess.run(
        [str(ROOT / "gradlew"), task, "--offline", "--console=plain"],
        cwd=ROOT, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True,
    )
    (OUTPUT / f"{name}.log").write_text(result.stdout)
    if expected_error is None:
        if result.returncode != 0:
            raise RuntimeError(result.stdout)
    else:
        if result.returncode == 0 or expected_error not in result.stdout:
            raise RuntimeError(result.stdout)
    print(f"PASS {name}", flush=True)
    return {"fixture": name, "expected_rejection": expected_error is not None, "passed": True}


def check_campaign_labels(results):
    labels = ROOT / "feature/home/presentation/src/main/res/values/home_special_contests.xml"
    original = labels.read_bytes()
    xml = original.decode()
    fixtures = {
        "layout": '<LinearLayout />',
        "nested-layout": xml.replace('mega-sena', '<TextView />mega-sena'),
        "extra-label": xml.replace('</resources>', '<string name="unexpected">extra</string></resources>'),
        "duplicate-label": xml.replace('home_loteca_pais_duas_linhas', 'home_mega_sena_30_anos_duas_linhas'),
    }
    try:
        for name, content in fixtures.items():
            labels.write_text(content)
            results.append(run(f"campaign-labels-{name}", "verifyComposeMigration", "layout XML continua proibido"))
    finally:
        labels.write_bytes(original)


def main():
    OUTPUT.mkdir(parents=True, exist_ok=True)
    results = []
    check_debug_manifest(results)
    check_launcher_resources(results)
    check_receipt_paths(results)
    check_campaign_labels(results)
    for feature in ("betting", "onboarding", "cart"):
        manifest = ROOT / f"feature/{feature}/presentation/src/androidTest/AndroidManifest.xml"
        original = manifest.read_bytes()
        xml = original.decode()
        fixtures = {
            "permission": xml.replace("</manifest>", '<uses-permission android:name="android.permission.CAMERA" /></manifest>'),
            "activity": xml.replace("</manifest>", '<application><activity android:name="ForbiddenActivity" /></application></manifest>'),
            "extra-package": xml.replace("</queries>", '<package android:name="other.package" /></queries>'),
        }
        try:
            for name, content in fixtures.items():
                manifest.write_text(content)
                results.append(run(f"{feature}-{name}", "verifyComposeMigration", "query CX"))
        finally:
            manifest.write_bytes(original)

    layout = ROOT / "feature/betting/presentation/src/main/res/layout/ds_gate_forbidden.xml"
    if layout.exists():
        raise FileExistsError(f"Fixture path already exists: {layout}")
    created_directory = not layout.parent.exists()
    try:
        layout.parent.mkdir(exist_ok=True)
        layout.write_text('<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android" android:layout_width="match_parent" android:layout_height="match_parent"/>')
        results.append(run("layout", "verifyComposeMigration", "ds_gate_forbidden.xml"))
    finally:
        layout.unlink(missing_ok=True)
        if created_directory:
            layout.parent.rmdir()

    source = ROOT / "feature/betting/presentation/src/main/java/br/gov/caixa/loterias/feature/betting/DsGateFixture.kt"
    if source.exists():
        raise FileExistsError(f"Fixture path already exists: {source}")
    fixtures = {
        "literal-color": ("val color = androidx.compose.ui.graphics.Color(0xFFA5AAB6)", "Cores literais"),
        "generic-component": ("fun <T> CaixaDSForbidden(value: T) = value", "devem pertencer ao CaixaDS"),
        "local-typography": ("val type = androidx.compose.material3.Typography()", "devem pertencer ao CaixaDS"),
        "material-icon": ("import androidx.compose.material.icons.Icons", "devem pertencer ao CaixaDS"),
    }
    try:
        for name, (content, message) in fixtures.items():
            source.write_text("package br.gov.caixa.loterias.feature.betting\n" + content + "\n")
            results.append(run(name, "verifyCaixaDS", message))
    finally:
        source.unlink(missing_ok=True)

    results.append(run("valid-migration", "verifyComposeMigration"))
    results.append(run("valid-ds", "verifyCaixaDS"))
    (OUTPUT / "results.json").write_text(json.dumps(results, indent=2) + "\n")
    print(f"Guardrails: {len(results)} checks passed; all fixtures restored.")


if __name__ == "__main__":
    main()
