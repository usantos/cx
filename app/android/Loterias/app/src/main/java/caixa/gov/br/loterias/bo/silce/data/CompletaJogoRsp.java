package caixa.gov.br.loterias.bo.silce.data;

import java.util.List;

public final class CompletaJogoRsp {
    List<Integer> payload;

    public List<Integer> getPayload() {
        return payload;
    }

    public void setPayload(List<Integer> payload) {
        this.payload = payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CompletaJogoRsp that = (CompletaJogoRsp) o;

        return payload != null ? payload.equals(that.payload) : that.payload == null;

    }

    @Override
    public int hashCode() {
        return payload != null ? payload.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "CompletaJogoRsp{" +
                "payload=" + payload +
                '}';
    }
}
