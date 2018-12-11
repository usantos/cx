package br.gov.caixa.loterias.apostar.model.bean;

import android.media.Image;

/**
 * Created by cedesbr450 on 19/12/17.
 */

public class TipoAposta {


    private String Titulo;
    private int ImagemTrevo;
    private int CorBackground;
    private Integer valor;
    private String DescricaoEspecial;

    private Boolean isSelect;

    public TipoAposta(String Titulo, int ImagemTrevo, int CorBackground, Boolean isSelect, Integer valor, String DescricaoEspecial){
        this.Titulo = Titulo;
        this.ImagemTrevo = ImagemTrevo;
        this.CorBackground = CorBackground;
        this.isSelect = isSelect;
        this.valor = valor;
        this.DescricaoEspecial = DescricaoEspecial;
    }

    public String getTitulo() {
        return Titulo;
    }

    public void setTitulo(String titulo) {
        Titulo = titulo;
    }

    public int getImagemTrevo() {
        return ImagemTrevo;
    }

    public void setImagemTrevo(int imagemTrevo) {
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


}
