#!/usr/bin/env bash
set -euo pipefail

apk=${1:?Supply the debug APK path}
report=${2:?Supply the report path}
package=com.mamxanh.app
test -f "$apk"

serial=$(adb get-serialno)
if [[ ! "$serial" =~ ^emulator-[0-9]+$ ]]; then
    printf 'Expected an isolated emulator, found %s\n' "$serial" >&2
    exit 1
fi
device=(adb -s "$serial")
avd=$("${device[@]}" emu avd name | tr -d '\r')

installed=$(mktemp "${RUNNER_TEMP:-${TMPDIR:-/tmp}}/habitree-installed.XXXXXX")
trap 'rm -f -- "$installed"' EXIT
mkdir -p -- "$(dirname -- "$report")"
{
    printf 'Serial: %s\nAVD response: %s\n' "$serial" "$avd"
    "${device[@]}" install -r "$apk"
    package_path=$("${device[@]}" shell pm path "$package" | tr -d '\r')
    if [[ "$package_path" != package:/*/base.apk || "$package_path" == *$'\n'* ]]; then
        printf 'Unexpected installed package paths: %s\n' "$package_path" >&2
        exit 1
    fi
    "${device[@]}" pull "${package_path#package:}" "$installed"
    cmp -- "$apk" "$installed"
    printf 'APK SHA-256: '
    sha256sum -- "$apk"

    launch=$("${device[@]}" shell am start -W -n "$package/.MainActivity" | tr -d '\r')
    printf '%s\n' "$launch"
    grep -Fxq 'Status: ok' <<< "$launch"
    "${device[@]}" shell pidof "$package"
    "${device[@]}" shell am force-stop "$package"
} | tee "$report"
