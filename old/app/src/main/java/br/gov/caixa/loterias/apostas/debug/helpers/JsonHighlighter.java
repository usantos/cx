package br.gov.caixa.loterias.apostas.debug.helpers;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class JsonHighlighter {

    private JsonHighlighter() {
    }

    public static CharSequence format(String json) {

        if (json == null || json.trim().isEmpty()) {
            return "";
        }

        return highlight(prettyJsonFast(json));
    }

    private static String prettyJsonFast(String json) {

        String trimmed = json.trim();

        if (!(trimmed.startsWith("{")
                || trimmed.startsWith("["))) {
            return json;
        }

        StringBuilder out =
                new StringBuilder(trimmed.length() + 1024);

        int indent = 0;
        boolean inQuotes = false;

        for (int i = 0; i < trimmed.length(); i++) {

            char c = trimmed.charAt(i);

            if (c == '"' &&
                    (i == 0 || trimmed.charAt(i - 1) != '\\')) {

                inQuotes = !inQuotes;
            }

            if (inQuotes) {
                out.append(c);
                continue;
            }

            switch (c) {

                case '{':
                case '[':

                    out.append(c);
                    out.append('\n');

                    indent++;

                    appendIndent(out, indent);

                    break;

                case '}':
                case ']':

                    out.append('\n');

                    indent--;

                    appendIndent(out, indent);

                    out.append(c);

                    break;

                case ',':

                    out.append(c);
                    out.append('\n');

                    appendIndent(out, indent);

                    break;

                case ':':

                    out.append(": ");

                    break;

                default:

                    out.append(c);
            }
        }

        return out.toString();
    }

    private static void appendIndent(
            StringBuilder sb,
            int indent) {

        for (int i = 0; i < indent; i++) {
            sb.append("  ");
        }
    }

    private static CharSequence highlight(String json) {

        SpannableStringBuilder sb =
                new SpannableStringBuilder(json);

        highlightKeys(sb);
        highlightStrings(sb);
        highlightNumbers(sb);
        highlightBooleans(sb);

        return sb;
    }

    private static void highlightKeys(
            SpannableStringBuilder sb) {

        Pattern pattern =
                Pattern.compile("\"[^\"]+\"(?=\\s*:)");

        Matcher matcher =
                pattern.matcher(sb.toString());

        while (matcher.find()) {

            sb.setSpan(
                    new ForegroundColorSpan(0xFF1565C0),
                    matcher.start(),
                    matcher.end(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }
    }

    private static void highlightStrings(
            SpannableStringBuilder sb) {

        Pattern pattern =
                Pattern.compile("(?<=:\\s)\"[^\"]*\"");

        Matcher matcher =
                pattern.matcher(sb.toString());

        while (matcher.find()) {

            sb.setSpan(
                    new ForegroundColorSpan(0xFF2E7D32),
                    matcher.start(),
                    matcher.end(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }
    }

    private static void highlightNumbers(
            SpannableStringBuilder sb) {

        Pattern pattern =
                Pattern.compile("(?<=:\\s)-?\\d+(\\.\\d+)?");

        Matcher matcher =
                pattern.matcher(sb.toString());

        while (matcher.find()) {

            sb.setSpan(
                    new ForegroundColorSpan(0xFF6A1B9A),
                    matcher.start(),
                    matcher.end(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }
    }

    private static void highlightBooleans(
            SpannableStringBuilder sb) {

        Pattern pattern =
                Pattern.compile("(?<=:\\s)(true|false|null)");

        Matcher matcher =
                pattern.matcher(sb.toString());

        while (matcher.find()) {

            sb.setSpan(
                    new ForegroundColorSpan(0xFFF57C00),
                    matcher.start(),
                    matcher.end(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }
    }
}