package caixa.gov.br.loterias.model;

import android.media.Image;

/**
 * Created by cedesbr450 on 19/12/17.
 */

public class TipoAposta {


    private String Titulo;
    private int ImagemTrevo;
    private int CorBackground;

    public TipoAposta(String Titulo, int ImagemTrevo, int CorBackground ){
        this.Titulo = Titulo;
        this.ImagemTrevo = ImagemTrevo;
        this.CorBackground = CorBackground;
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

}
