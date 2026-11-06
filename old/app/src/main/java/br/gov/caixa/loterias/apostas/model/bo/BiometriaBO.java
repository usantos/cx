package br.gov.caixa.loterias.apostas.model.bo;

public class BiometriaBO {

    private static BiometriaBO instance;

    private Boolean biometriaHabilitada = false;
    private Boolean isTrocandoUsuario = false;

    private BiometriaBO(){
    }

    public static BiometriaBO getInstance(){
        if (instance == null){
            instance = new BiometriaBO();
        }
        return instance;
    }

    public Boolean getBiometriaHabilitada() {
        return biometriaHabilitada;
    }

    public void setBiometriaHabilitada(Boolean biometriaHabilitada) {
        this.biometriaHabilitada = biometriaHabilitada;
    }

    public Boolean isTrocandoUsuario(){
        return isTrocandoUsuario;
    }

    public void setIsTrocandoUsuario(boolean isTrocandoUsuario){
        this.isTrocandoUsuario = isTrocandoUsuario;
    }

}
