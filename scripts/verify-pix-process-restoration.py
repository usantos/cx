#!/usr/bin/env python3
"""External Android process-death probe, restricted to an attested mock APK.

Does not install, clear data, force-stop, instrument, or access account storage.
Raw logcat and UI XML are parsed in memory and never written to evidence files.
"""
import argparse
import hashlib
import json
import re
import subprocess
import sys
import time
import uuid
import xml.etree.ElementTree as ET
from pathlib import Path

PACKAGE = "br.gov.caixa.loterias.apostas.hmp.composevalidation"
ACTIVITY = PACKAGE + "/br.gov.caixa.loterias.apostas.MainActivity"
MOCK_CODE = "00020101021226850010014BR.GOV.BCB.PIX0136mock.caixa.gov.br/pix/checkout/99"
PREFIX = MOCK_CODE[:19] + "..."
POST = re.compile(r"curl\s+--request\s+'POST'\s+'[^']*/logado/carrinhos/gerar-cobranca-pix(?:\?[^']*)?'")


class ProbeFailure(Exception):
    pass


def require(condition, reason):
    if not condition:
        raise ProbeFailure(reason)


class Driver:
    def __init__(self, args):
        self.args = args
        self.command = [args.adb, "-s", args.serial]
        self.evidence = {"package": PACKAGE, "scenario": "PIX_PENDING", "passed": False,
                         "initial_posts": None, "restored_posts": None,
                         "mock_purchase_id": 99, "identity_visibility": "mock code prefix only"}

    def adb(self, *args, binary=False, timeout=45):
        result = subprocess.run(self.command + list(args), stdout=subprocess.PIPE,
                                stderr=subprocess.PIPE, timeout=timeout)
        # Do not leak remote output on errors (including logs or XML).
        require(result.returncode == 0, "ADB operation failed: " + args[0])
        return result.stdout if binary else result.stdout.decode("utf-8", errors="replace")

    def pid(self):
        # pidof may return exit 1 for a stopped process; avoid adb status ambiguity.
        output = self.adb("shell", "sh", "-c", "'pidof " + PACKAGE + " || true'").strip()
        require(not output or re.fullmatch(r"\d+(?:\s+\d+)*", output), "Unexpected PID response")
        pids = [int(value) for value in output.split()]
        require(len(pids) <= 1, "Multiple package processes require manual investigation")
        return pids[0] if pids else None

    def own_focus(self):
        # Android 16's `window windows` omits global mCurrentFocus; the full
        # window dump retains it. Do not infer focus merely from an app window.
        windows = self.adb("shell", "dumpsys", "window")
        focus = [line for line in windows.splitlines() if "mCurrentFocus=" in line]
        return any(PACKAGE + "/" in line for line in focus)

    def task_id(self):
        # Persist only the task number for the candidate ActivityRecord, never
        # the activity dump (which can contain other apps or Intent extras).
        activities = self.adb("shell", "dumpsys", "activity", "activities")
        record = r"ActivityRecord\{[^}\n]*\s" + re.escape(PACKAGE) + r"/[^\s}]+\s+t(\d+)\b"
        # Samsung retains obsolete ActivityRecords in orientation/window-color
        # diagnostics. Only actual task history entries authorize this assertion.
        history = r"^\s*\*?\s*Hist\s+#\d+:\s*" + record
        tasks = {int(value) for value in re.findall(history, activities, re.MULTILINE)}
        require(len(tasks) == 1, "Expected exactly one retained candidate Android task")
        task = tasks.pop()
        resumed = r"^\s*topResumedActivity\s*[:=]\s*" + record
        resumed_tasks = {int(value) for value in re.findall(resumed, activities, re.MULTILINE)}
        require(not resumed_tasks or resumed_tasks == {task}, "Candidate resumed Activity does not match its retained task")
        return task

    def ui_state(self):
        if not self.own_focus():
            self.evidence["last_ui_probe"] = {"candidate_focused": False}
            return None
        # Android 16 may report /dev/tty without emitting the XML. Keep the dump
        # transient on-device, parse in host memory, and remove it even on failure.
        remote_xml = "/data/local/tmp/codex-pix-probe-" + uuid.uuid4().hex + ".xml"
        try:
            self.adb("shell", "uiautomator", "dump", remote_xml, timeout=15)
            raw = self.adb("exec-out", "cat", remote_xml)
        except (ProbeFailure, subprocess.TimeoutExpired) as error:
            self.evidence["last_ui_collection_failure"] = type(error).__name__
            self.evidence["last_ui_probe"] = {"candidate_focused": True, "xml_available": False,
                                               "collection_failed": True}
            return None
        finally:
            try:
                self.adb("shell", "rm", "-f", remote_xml)
            except (ProbeFailure, subprocess.TimeoutExpired):
                raise ProbeFailure("Temporary UI dump cleanup failed")
        if not self.own_focus():
            self.evidence["last_ui_probe"] = {"candidate_focused": False}
            return None
        begin, end = raw.find("<?xml"), raw.rfind("</hierarchy>")
        if begin < 0 or end < 0:
            self.evidence["last_ui_probe"] = {"candidate_focused": True, "xml_available": False}
            return None
        try:
            tree = ET.fromstring(raw[begin:end + len("</hierarchy>")])
        except ET.ParseError:
            self.evidence["last_ui_probe"] = {"candidate_focused": True, "xml_available": True, "xml_parsed": False}
            return None
        texts = [node.get("text", "") for node in tree.iter("node") if node.get("package") == PACKAGE]
        timer = next((value for value in texts if re.fullmatch(r"\d{2}:\d{2}", value)), None)
        ready = PREFIX in texts and "Verificar Pagamento" in texts
        self.evidence["last_ui_probe"] = {"candidate_focused": True, "xml_available": True,
                                           "xml_parsed": True, "mock_prefix_visible": PREFIX in texts,
                                           "verify_visible": "Verificar Pagamento" in texts}
        return {"mock_prefix_visible": PREFIX in texts, "verify_visible": "Verificar Pagamento" in texts,
                "timer": timer, "ready": ready}

    def wait_ui(self):
        deadline = time.monotonic() + self.args.timeout
        while time.monotonic() < deadline:
            state = self.ui_state()
            if state and state["ready"]:
                return state
            time.sleep(1)
        raise ProbeFailure("Mock pending Pix UI did not become visible in the candidate package")

    def screenshot(self, name):
        require(self.own_focus(), "Screenshot refused: candidate package is not focused")
        data = self.adb("exec-out", "screencap", "-p", binary=True)
        require(data.startswith(b"\x89PNG\r\n\x1a\n"), "Invalid screenshot response")
        (self.args.output / name).write_bytes(data)

    def posts(self, pid, since):
        # Query only the candidate PID and one tag; never save logcat contents.
        raw = self.adb("logcat", "-d", "--pid", str(pid), "-v", "epoch", "LoteriasHTTP:D", "*:S")
        count = 0
        for line in raw.splitlines():
            stamp = re.match(r"^\s*(\d+\.\d+)", line)
            if stamp and float(stamp[1]) >= since and POST.search(line):
                count += 1
        return count

    def run(self):
        self.evidence["last_stage"] = "build-guards"
        attestation = json.loads(self.args.attestation.read_text())
        expected = {"package": PACKAGE, "debug": True, "mockBackend": True,
                    "mockScenario": "PIX_PENDING", "networkLogging": True,
                    "syntheticSessionOnly": True}
        require(all(attestation.get(k) == v for k, v in expected.items()),
                "Build attestation must confirm isolated debug/mock PIX_PENDING and synthetic-only session")
        apk_hash = hashlib.sha256(self.args.apk.read_bytes()).hexdigest()
        require(attestation.get("apkSha256") == apk_hash, "APK does not match the build attestation")
        package_info = self.adb("shell", "dumpsys", "package", PACKAGE)
        require("DEBUGGABLE" in package_info, "Candidate installed APK must be debuggable")
        paths = self.adb("shell", "pm", "path", PACKAGE).strip().splitlines()
        require(len(paths) == 1 and paths[0].startswith("package:/"), "Expected one installed base APK")
        installed_hash = hashlib.sha256(self.adb("exec-out", "cat", paths[0][8:], binary=True)).hexdigest()
        require(installed_hash == apk_hash, "Installed APK differs from the attested mock build")
        require(self.pid() is None, "Candidate already running; prepare a fresh isolated mock install outside this driver")
        self.evidence["apk_sha256"] = apk_hash
        self.evidence["build_attestation_verified"] = True
        since = float(self.adb("shell", "date", "+%s").strip())
        self.since = since
        self.evidence["last_stage"] = "initial-launch"
        self.adb("shell", "am", "start", "-W", "-n", ACTIVITY,
                 "-a", "android.intent.action.MAIN", "-c", "android.intent.category.LAUNCHER",
                 "-f", "0x10200000", "--es", "visual_test_route", "payment/pix-copy",
                 "--ez", "visual_test_authenticated", "true")
        initial_pid = self.pid()
        require(initial_pid is not None, "Initial candidate process missing")
        self.evidence["initial_pid"] = initial_pid
        self.evidence["last_stage"] = "initial-ui"
        initial_ui = self.wait_ui()
        self.evidence["initial_ui"] = initial_ui
        initial_task = self.task_id()
        self.evidence["initial_task_id"] = initial_task
        self.screenshot("initial-pix.png")
        self.evidence["initial_posts"] = self.posts(initial_pid, since)
        require(self.evidence["initial_posts"] == 1, "Expected exactly one initial Pix generation POST; verify logging and fixture")
        self.evidence["last_stage"] = "background-kill"
        self.adb("shell", "input", "keyevent", "KEYCODE_HOME")
        time.sleep(2)  # Activity must reach stopped state and save its task state.
        require(not self.own_focus(), "Candidate remained focused after HOME; process kill refused")
        require(self.pid() == initial_pid, "Candidate process changed before the background kill")
        background_task = self.task_id()
        self.evidence["background_task_id"] = background_task
        require(background_task == initial_task, "Candidate Android task changed before process death")
        self.adb("shell", "am", "kill", PACKAGE)
        deadline = time.monotonic() + 15
        while self.pid() is not None and time.monotonic() < deadline:
            time.sleep(0.5)
        require(self.pid() is None, "am kill did not terminate the background process")
        self.evidence["pid_absent_after_kill"] = True
        retained_task = self.task_id()
        self.evidence["retained_task_id_after_kill"] = retained_task
        require(retained_task == initial_task, "Candidate Android task was lost after process death")
        # NEW_TASK | RESET_TASK_IF_NEEDED, launcher-equivalent existing-task reentry.
        # The synthetic session is memory-only. Recreate the identical mock owner
        # before composition while retaining the existing Android task/SavedState.
        self.evidence["last_stage"] = "restore-launch"
        self.evidence["restore_identity_setup"] = "same guarded synthetic fixture extras"
        self.adb("shell", "am", "start", "-W", "-n", ACTIVITY,
                 "-a", "android.intent.action.MAIN", "-c", "android.intent.category.LAUNCHER",
                 "-f", "0x10200000", "--es", "visual_test_route", "payment/pix-copy",
                 "--ez", "visual_test_authenticated", "true")
        restored_pid = self.pid()
        require(restored_pid is not None and restored_pid != initial_pid, "Expected a new candidate process after restore")
        self.evidence["restored_pid"] = restored_pid
        restored_task = self.task_id()
        self.evidence["restored_task_id"] = restored_task
        require(restored_task == initial_task, "Launcher reentry created a different Android task")
        self.evidence["last_stage"] = "restored-ui"
        restored_ui = self.wait_ui()
        self.evidence["restored_ui"] = restored_ui
        self.screenshot("restored-pix.png")
        self.evidence["last_stage"] = "restored-observation"
        time.sleep(self.args.observe_seconds)
        require(self.pid() == restored_pid, "Restored candidate process changed during observation")
        self.evidence["initial_posts"] = self.posts(initial_pid, since)
        self.evidence["restored_posts"] = self.posts(restored_pid, since)
        require(self.evidence["initial_posts"] == 1, "Unexpected additional Pix POST before process death")
        require(self.evidence["restored_posts"] == 0, "Pix generation POST repeated after real process death")
        self.evidence["passed"] = True
        self.evidence["last_stage"] = "complete"


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--serial", required=True, help="Explicit adb device serial")
    parser.add_argument("--adb", default="adb")
    parser.add_argument("--apk", type=Path, required=True)
    parser.add_argument("--attestation", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    parser.add_argument("--timeout", type=int, default=40)
    parser.add_argument("--observe-seconds", type=int, default=5)
    args = parser.parse_args()
    require(args.timeout > 0 and args.observe_seconds >= 5, "Positive timeout and at least five observation seconds required")
    args.output.mkdir(parents=True, exist_ok=True)
    require(not any(args.output.iterdir()), "Use an empty evidence output directory")
    driver = Driver(args)
    try:
        driver.run()
    except (ProbeFailure, OSError, ValueError, subprocess.TimeoutExpired) as error:
        # Exception text from ADB/JSON may contain arbitrary content; retain only controlled reasons.
        reason = str(error) if isinstance(error, ProbeFailure) else type(error).__name__
        driver.evidence["failure_reason"] = reason
        # A host/route restore failure still reports any observed generation requests.
        if hasattr(driver, "since"):
            for phase in ("initial", "restored"):
                if driver.evidence.get(phase + "_pid"):
                    try:
                        driver.evidence[phase + "_posts"] = driver.posts(driver.evidence[phase + "_pid"], driver.since)
                    except (ProbeFailure, OSError, subprocess.TimeoutExpired):
                        pass
    (args.output / "result.json").write_text(json.dumps(driver.evidence, indent=2) + "\n")
    print(json.dumps(driver.evidence, indent=2))
    return 0 if driver.evidence["passed"] else 1


if __name__ == "__main__":
    sys.exit(main())
