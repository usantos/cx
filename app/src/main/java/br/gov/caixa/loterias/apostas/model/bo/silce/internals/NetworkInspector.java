package br.gov.caixa.loterias.apostas.model.bo.silce.internals;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import br.gov.caixa.loterias.apostas.BuildConfig;

public final class NetworkInspector {

    private static final int MAX_ITEMS = 200;

    public static final String KEY_URL = "url";
    public static final String KEY_PATH = "path";
    public static final String KEY_DURATION = "duration";
    public static final String KEY_QUERY_PARAMS = "queryParams";
    public static final String KEY_HEADERS = "headers";
    public static final String KEY_ORIGINAL_BODY = "originalBody";
    public static final String KEY_PREPARED_BODY = "preparedBody";
    public static final String KEY_RESPONSE_BODY = "responseBody";
    public static final String KEY_ERROR_MESSAGE = "errorMessage";
    public static final String KEY_ERROR_BODY = "errorBody";

    private static final Gson GSON = new Gson();
    private static final List<NetworkInspectorEntry> ENTRIES = new ArrayList<>();

    private NetworkInspector() {
    }

    public static String createEntry(String method,
                                     String url,
                                     String path,
                                     Map<String, String> queryParams,
                                     Object originalBody,
                                     String preparedBody) {

        if (!BuildConfig.ENABLE_NETWORK_INSPECTOR) {
            return null;
        }


        String id = UUID.randomUUID().toString();

        NetworkInspectorEntry entry = new NetworkInspectorEntry();
        entry.id = id;
        entry.method = safe(method);
        entry.url = safe(url);
        entry.path = safe(path);
        entry.queryParams = toJson(copy(queryParams));
        entry.originalBody = toJson(originalBody);
        entry.preparedBody = safe(preparedBody);
        entry.createdAt = System.currentTimeMillis();

        synchronized (ENTRIES) {
            ENTRIES.add(0, entry);

            while (ENTRIES.size() > MAX_ITEMS) {
                ENTRIES.remove(ENTRIES.size() - 1);
            }
        }

        return id;
    }

    public static void setStatusCode(String id, int statusCode) {
        if (!BuildConfig.ENABLE_NETWORK_INSPECTOR) {
            return;
        }

        if (id == null || statusCode <= 0) {
            return;
        }

        synchronized (ENTRIES) {
            NetworkInspectorEntry entry = findLocked(id);

            if (entry == null) {
                return;
            }

            entry.statusCode = statusCode;
        }
    }

    public static void markRequestSent(String id,
                                       String method,
                                       String url,
                                       Map<String, String> headers) {

        if (!BuildConfig.ENABLE_NETWORK_INSPECTOR) {
            return;
        }

        if (id == null) {
            return;
        }

        synchronized (ENTRIES) {
            NetworkInspectorEntry entry = findLocked(id);

            if (entry == null) {
                return;
            }

            entry.method = safe(method);
            entry.url = safe(url);
            entry.headers = toJson(maskHeaders(headers));
            entry.sentAt = System.currentTimeMillis();
        }
    }

    public static void markSuccess(String id,
                                   String method,
                                   String url,
                                   Object response) {
        if (!BuildConfig.ENABLE_NETWORK_INSPECTOR) {
            return;
        }

        if (id == null) {
            return;
        }

        long now = System.currentTimeMillis();

        synchronized (ENTRIES) {
            NetworkInspectorEntry entry = findLocked(id);

            if (entry == null) {
                return;
            }

            entry.method = safe(method);
            entry.url = safe(url);
            entry.responseBody = toJson(response);
            entry.finished = true;
            entry.finishedAt = now;
            entry.durationMs = duration(entry, now);
        }
    }

    public static void markError(String id,
                                 String method,
                                 String url,
                                 VolleyError error,
                                 int statusCode,
                                 String errorBody) {
        if (!BuildConfig.ENABLE_NETWORK_INSPECTOR) {
            return;
        }

        if (id == null) {
            return;
        }

        long now = System.currentTimeMillis();

        synchronized (ENTRIES) {
            NetworkInspectorEntry entry = findLocked(id);

            if (entry == null) {
                return;
            }

            entry.method = safe(method);
            entry.url = safe(url);
            entry.finished = true;
            entry.finishedAt = now;
            entry.durationMs = duration(entry, now);

            if (statusCode > 0) {
                entry.statusCode = statusCode;
            }

            if (errorBody != null && !errorBody.isEmpty()) {
                entry.errorBody = errorBody;
            }

            if (error != null) {
                entry.errorMessage = safe(error.toString());

                if (entry.statusCode <= 0 && error.networkResponse != null) {
                    entry.statusCode = error.networkResponse.statusCode;
                }

                if ((entry.errorBody == null || entry.errorBody.isEmpty())
                        && error.networkResponse != null
                        && error.networkResponse.data != null) {
                    try {
                        entry.errorBody = new String(error.networkResponse.data, "UTF-8");
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }

    public static ArrayList<NetworkInspectorEntry> getEntriesSnapshot() {
        synchronized (ENTRIES) {
            ArrayList<NetworkInspectorEntry> list = new ArrayList<>();

            for (NetworkInspectorEntry entry : ENTRIES) {
                list.add(copyEntry(entry));
            }

            return list;
        }
    }

    public static ArrayList<NetworkInspectorEntry> getEntriesLightSnapshot() {
        synchronized (ENTRIES) {
            ArrayList<NetworkInspectorEntry> list = new ArrayList<>();

            for (NetworkInspectorEntry entry : ENTRIES) {
                list.add(copyLightEntry(entry));
            }

            return list;
        }
    }

    public static NetworkInspectorEntry getEntryById(String id) {
        if (id == null) {
            return null;
        }

        synchronized (ENTRIES) {
            NetworkInspectorEntry entry = findLocked(id);

            if (entry == null) {
                return null;
            }

            return copyEntry(entry);
        }
    }

    /**
     * Novo método para PayloadViewerActivity.
     *
     * Permite buscar só uma seção específica por id/key.
     * Evita passar payload gigante por Intent.
     */
    public static String getValueByKey(String id, String key) {
        if (id == null || key == null) {
            return "";
        }

        synchronized (ENTRIES) {
            NetworkInspectorEntry entry = findLocked(id);

            if (entry == null) {
                return "";
            }

            return getValueByKeyLocked(entry, key);
        }
    }

    public static void clear() {
        synchronized (ENTRIES) {
            ENTRIES.clear();
        }
    }

    public static boolean hasEntry(String id) {
        if (id == null) {
            return false;
        }

        synchronized (ENTRIES) {
            return findLocked(id) != null;
        }
    }

    // ===================
    // helpers
    // ===================

    private static NetworkInspectorEntry findLocked(String id) {
        for (NetworkInspectorEntry entry : ENTRIES) {
            if (id.equals(entry.id)) {
                return entry;
            }
        }

        return null;
    }

    private static String getValueByKeyLocked(NetworkInspectorEntry entry, String key) {
        switch (key) {
            case KEY_URL:
                return safe(entry.url);

            case KEY_PATH:
                return safe(entry.path);

            case KEY_DURATION:
                return entry.durationMs > 0 ? entry.durationMs + "ms" : "";

            case KEY_QUERY_PARAMS:
                return safe(entry.queryParams);

            case KEY_HEADERS:
                return safe(entry.headers);

            case KEY_ORIGINAL_BODY:
                return safe(entry.originalBody);

            case KEY_PREPARED_BODY:
                return safe(entry.preparedBody);

            case KEY_RESPONSE_BODY:
                return safe(entry.responseBody);

            case KEY_ERROR_MESSAGE:
                return safe(entry.errorMessage);

            case KEY_ERROR_BODY:
                return safe(entry.errorBody);

            default:
                return "";
        }
    }

    private static long duration(NetworkInspectorEntry entry, long now) {
        if (entry.sentAt > 0) {
            return now - entry.sentAt;
        }

        if (entry.createdAt > 0) {
            return now - entry.createdAt;
        }

        return 0;
    }

    private static NetworkInspectorEntry copyLightEntry(NetworkInspectorEntry src) {
        NetworkInspectorEntry dst = new NetworkInspectorEntry();

        dst.id = src.id;
        dst.method = src.method;
        dst.url = src.url;
        dst.path = src.path;
        dst.statusCode = src.statusCode;
        dst.createdAt = src.createdAt;
        dst.sentAt = src.sentAt;
        dst.finishedAt = src.finishedAt;
        dst.durationMs = src.durationMs;
        dst.finished = src.finished;

        dst.queryParams = buildSizeLabel(src.queryParams);
        dst.headers = buildSizeLabel(src.headers);
        dst.originalBody = buildSizeLabel(src.originalBody);
        dst.preparedBody = buildSizeLabel(src.preparedBody);
        dst.responseBody = buildSizeLabel(src.responseBody);
        dst.errorBody = buildSizeLabel(src.errorBody);
        dst.errorMessage = src.errorMessage;

        return dst;
    }

    private static NetworkInspectorEntry copyEntry(NetworkInspectorEntry src) {
        NetworkInspectorEntry dst = new NetworkInspectorEntry();

        dst.id = src.id;
        dst.method = src.method;
        dst.url = src.url;
        dst.path = src.path;
        dst.queryParams = src.queryParams;
        dst.headers = src.headers;
        dst.originalBody = src.originalBody;
        dst.preparedBody = src.preparedBody;
        dst.responseBody = src.responseBody;
        dst.errorBody = src.errorBody;
        dst.errorMessage = src.errorMessage;
        dst.statusCode = src.statusCode;
        dst.createdAt = src.createdAt;
        dst.sentAt = src.sentAt;
        dst.finishedAt = src.finishedAt;
        dst.durationMs = src.durationMs;
        dst.finished = src.finished;

        return dst;
    }

    private static String buildSizeLabel(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }

        return value.length() + " chars";
    }

    private static String toJson(Object value) {
        if (value == null) {
            return "";
        }

        try {
            return GSON.toJson(value);
        } catch (Exception e) {
            return String.valueOf(value);
        }
    }

    private static Map<String, String> copy(Map<String, String> map) {
        return map == null ? null : new HashMap<>(map);
    }

    private static Map<String, String> maskHeaders(Map<String, String> headers) {
        if (headers == null) {
            return null;
        }

        Map<String, String> result = new HashMap<>();

        for (Map.Entry<String, String> item : headers.entrySet()) {
            String key = item.getKey();
            String value = item.getValue();

            if (key == null) {
                continue;
            }

            if (isSensitive(key)) {
                result.put(key, "***");
            } else {
                result.put(key, value);
            }
        }

        return result;
    }

    private static boolean isSensitive(String key) {
        String k = key.toLowerCase(Locale.ROOT);

        return k.contains("authorization")
                || k.contains("token")
                || k.contains("cookie")
                || k.contains("apikey")
                || k.contains("api-key")
                || k.contains("ocp-apim-subscription-key");
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}