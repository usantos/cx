#!/usr/bin/env python3
"""Confere o Play compartilhado e migra configurações locais antigas do app."""
import argparse
from pathlib import Path
import shutil
import tempfile
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
MODULE = "simlo-app-android.app"
MAIN = "br.gov.caixa.loterias.apostas.MainActivity"
OPTIONS = {
    "MODE": "specific_activity",
    "ACTIVITY_CLASS": MAIN,
    "CLEAR_APP_STORAGE": "false",
    "SKIP_ACTIVITY_VALIDATION": "false",
    "ALWAYS_INSTALL_WITH_PM": "true",
    "ALLOW_ASSUME_VERIFIED": "false",
}


def configuration_problems(root):
    try:
        configuration = ET.parse(root / ".run/SIMLO_App.run.xml").getroot().find("configuration")
        if configuration is None:
            return ["Configuração compartilhada SIMLO App ausente."]
        module = configuration.find("module")
        if configuration.get("type") != "AndroidRunConfigurationType" or module is None or module.get("name") != MODULE:
            return ["SIMLO App deve usar a configuração Android do módulo app."]
        actual = {option.get("name"): option.get("value") for option in configuration.findall("option")}
        errors = [f"SIMLO App: {name} deve ser {value}." for name, value in OPTIONS.items() if actual.get(name) != value]
        if configuration.get("name") != "SIMLO App":
            errors.append("Use o nome SIMLO App para evitar conflito com a configuração local app.")
        return errors
    except (OSError, ET.ParseError):
        return ["Configuração compartilhada SIMLO App ausente ou inválida."]


def repair_workspace(root):
    path = root / ".idea/workspace.xml"
    if not path.exists():
        return False
    tree = ET.parse(path)
    manager = tree.getroot().find("component[@name='RunManager']")
    if manager is None:
        manager = ET.SubElement(tree.getroot(), "component", {"name": "RunManager"})
    changed = manager.get("selected") != "Android App.SIMLO App"
    manager.set("selected", "Android App.SIMLO App")
    for configuration in manager.findall("configuration"):
        module = configuration.find("module")
        if configuration.get("type") != "AndroidRunConfigurationType" or module is None:
            continue
        name = module.get("name", "")
        if name != "app" and not name.endswith(".app"):
            continue
        changed |= module.get("name") != MODULE
        module.set("name", MODULE)
        for key, value in OPTIONS.items():
            option = configuration.find(f"option[@name='{key}']")
            if option is None:
                option = ET.SubElement(configuration, "option", {"name": key})
            changed |= option.get("value") != value
            option.set("value", value)
    if changed:
        backups = root / ".local/android-studio"
        backups.mkdir(parents=True, exist_ok=True)
        with path.open("rb") as source, tempfile.NamedTemporaryFile(
            dir=backups, prefix="workspace-", suffix=".xml", delete=False
        ) as backup:
            shutil.copyfileobj(source, backup)
        ET.indent(tree, space="  ")
        tree.write(path, encoding="utf-8", xml_declaration=True)
    return changed


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--check", action="store_true", help="Somente valida o XML versionado; não modifica o IDE.")
    arguments = parser.parse_args()
    errors = configuration_problems(ROOT)
    if errors:
        parser.exit(1, "\n".join(errors) + "\n")
    if not arguments.check:
        try:
            changed = repair_workspace(ROOT)
        except (OSError, ET.ParseError) as failure:
            parser.exit(1, f"Não foi possível migrar o workspace: {failure}\n")
        print("Workspace migrado com backup local." if changed else "Workspace já configurado ou ainda não criado.")
    print("SIMLO App: Play usa MainActivity; dados e verificação de segurança preservados.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
