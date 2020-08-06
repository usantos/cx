package br.gov.caixa.loterias.apostas.model.bo.silce.data;

public final class LoginReq {
    String email;
    String senha;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LoginReq that = (LoginReq) o;

        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        return senha != null ? senha.equals(that.senha) : that.senha == null;

    }

    @Override
    public int hashCode() {
        int result = email != null ? email.hashCode() : 0;
        result = 31 * result + (senha != null ? senha.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LoginReq{" +
                "email='" + email + '\'' +
                ", senha='" + senha + '\'' +
                '}';
    }
}
