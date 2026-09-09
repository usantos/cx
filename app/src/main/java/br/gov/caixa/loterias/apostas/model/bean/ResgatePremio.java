package br.gov.caixa.loterias.apostas.model.bean;

public class ResgatePremio {
    private int icon_reward;
    private String title;
    private String subtitle;
    private int order;

    public ResgatePremio(int icon_reward, String title, String subtitle, int order) {
        this.icon_reward = icon_reward;
        this.title = title;
        this.subtitle = subtitle;
        this.order = order;
    }

    public int getIcon_reward() {
        return icon_reward;
    }

    public void setIcon_reward(int icon_reward) {
        this.icon_reward = icon_reward;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
