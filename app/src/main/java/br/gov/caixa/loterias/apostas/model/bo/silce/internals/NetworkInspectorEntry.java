package br.gov.caixa.loterias.apostas.model.bo.silce.internals;
public class NetworkInspectorEntry {

    public String id;

    public String method;
    public String url;
    public String path;

    public String queryParams;
    public String headers;

    public String originalBody;
    public String preparedBody;

    public String responseBody;
    public String errorBody;
    public String errorMessage;

    public int statusCode = -1;

    public long createdAt;
    public long sentAt;
    public long finishedAt;
    public long durationMs;

    public boolean finished;
}
