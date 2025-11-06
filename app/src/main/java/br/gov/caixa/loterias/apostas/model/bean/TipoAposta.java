package br.gov.caixa.loterias.apostas.model.bean;

/**
 * Created by cedesbr450 on 19/12/17.
 */

public class TipoAposta {
    private String Titulo;
    private Integer ImagemTrevo;
    private Integer ImagemTrevoFundoCor;
    private int CorBackground;
    private Integer valor;
    private String DescricaoEspecial;
    private Boolean isSelect;
    private Integer textColor;
    private Integer textSelectColor;

    public TipoAposta(String Titulo, int ImagemTrevo, int ImagemTrevoFundoCor, int CorBackground, Boolean isSelect, Integer valor, String DescricaoEspecial, Integer textColor) {
        this.Titulo = Titulo;
        this.ImagemTrevo = ImagemTrevo;
        this.ImagemTrevoFundoCor = ImagemTrevoFundoCor;
        this.CorBackground = CorBackground;
        this.isSelect = isSelect;
        this.valor = valor;
        this.DescricaoEspecial = DescricaoEspecial;
        this.textColor = textColor;
    }
    public TipoAposta(String Titulo, int ImagemTrevo, int CorBackground, Boolean isSelect, Integer valor, String DescricaoEspecial, Integer textColor) {
        this.Titulo = Titulo;
        this.ImagemTrevo = ImagemTrevo;
        this.CorBackground = CorBackground;
        this.isSelect = isSelect;
        this.valor = valor;
        this.DescricaoEspecial = DescricaoEspecial;
        this.textColor = textColor;
    }

    public TipoAposta(String Titulo, int ImagemTrevo, int CorBackground, Boolean isSelect, Integer valor, String DescricaoEspecial){
        this.Titulo = Titulo;
        this.ImagemTrevo = ImagemTrevo;
        this.CorBackground = CorBackground;
        this.isSelect = isSelect;
        this.valor = valor;
        this.DescricaoEspecial = DescricaoEspecial;
    }

    public TipoAposta(String Titulo, int CorBackground, Boolean isSelect, Integer textColor) {
        this.Titulo = Titulo;
        this.CorBackground = CorBackground;
        this.isSelect = isSelect;
        this.textColor = textColor;
    }

    public String getTitulo() {
        return Titulo;
    }

    public void setTitulo(String titulo) {
        Titulo = titulo;
    }

    public Integer getImagemTrevo() {
        return ImagemTrevo;
    }

    public void setImagemTrevo(Integer imagemTrevo) {
        ImagemTrevo = imagemTrevo;
    }

    public int getCorBackground() {
        return CorBackground;
    }

    public void setCorBackground(int corBackground) {
        CorBackground = corBackground;
    }
    public Boolean getSelect() {
        return isSelect;
    }

    public void setSelect(Boolean select) {
        isSelect = select;
    }

    public Integer getValor() {
        return valor;
    }

    public void setValor(Integer valor) {
        this.valor = valor;
    }

    public String getDescricaoEspecial() {
        return DescricaoEspecial;
    }

    public void setDescricaoEspecial(String descricaoEspecial) {
        DescricaoEspecial = descricaoEspecial;
    }

    public Integer getTextColor() {
        return textColor;
    }

    public void setTextColor(Integer textColor) {
        this.textColor = textColor;
    }

    public Integer getImagemTrevoFundoCor() {
        return ImagemTrevoFundoCor;
    }

    public void setImagemTrevoFundoCor(Integer imagemTrevoFundoCor) {
        ImagemTrevoFundoCor = imagemTrevoFundoCor;
    }

    public Integer getTextSelectColor() {
        return textSelectColor;
    }

    public void setTextSelectColor(Integer textSelectColor) {
        this.textSelectColor = textSelectColor;
    }
}
