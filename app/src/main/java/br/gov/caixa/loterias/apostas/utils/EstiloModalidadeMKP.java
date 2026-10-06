package br.gov.caixa.loterias.apostas.utils;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoConcursoEnum;

public class EstiloModalidadeMKP {
    private int corClara;
    private int corEscura;
    private int trapezio;
    private int shapeBtnDetalhes;
    private int imagemEspecialCarrossel;
    private int imagemTriangulo;
    private int imagemTrianguloOutline;
    private int imagemEspecialSimples;
    private int imagemEspecialDupla;
    private int imagemEspecialQuadrada;
    private int imagemEspecialDetalhes;
    private int corLetraLista;
    private int corItemDezena;
    private int corFonteFundoClaro;
    private int corFonteFundoEscuro;
    private int trevoFundoClaro;
    private int trevoFundoEscuro;
    private int tarjaEspecialTrevoCombo;
    private int trevoFundoDetalheCombo;
    private int imagemSubCabecalho;

    private int trapezioEspecial;
    private int fundoEspecial;

    public EstiloModalidadeMKP(ModalidadeEnum modalidade) {
        createEstilo(modalidade);
    }

    public EstiloModalidadeMKP(ModalidadeEnum modalidadeEnum, Boolean isMega30Anos ){
        createEstilo(modalidadeEnum);
        if (isMega30Anos){
            //MEGASENA
            imagemEspecialCarrossel = R.drawable.cabecalho_mega_trinta_anos_carrossel;
            imagemEspecialDetalhes = R.drawable.cabecalho_mega_trinta_anos_detalhe;
            imagemSubCabecalho = R.drawable.rodape_mega_trinta_anos_inferior;
        }
    }

    public static EstiloModalidadeMKP createModalidadeConcursoEsp(ModalidadeEnum modalidade, Integer numeroConcurso, TipoConcursoEnum tipoConcursoEnum) {

        EstiloModalidadeMKP estilo = new EstiloModalidadeMKP(modalidade);

        if (EspecialUtils.isMega30(modalidade, numeroConcurso, tipoConcursoEnum)) {
            estilo.aplicarMega30Anos();
        }
        if (EspecialUtils.isLotecaPais(modalidade, numeroConcurso, tipoConcursoEnum)) {
            estilo.aplicarLotecaPais();
        }

        return estilo;
    }

    public static EstiloModalidadeMKP createModalidadeConcursoEsp(ModalidadeEnum modalidade, Integer numeroConcurso, Boolean isEspecial) {

        EstiloModalidadeMKP estilo = new EstiloModalidadeMKP(modalidade);

        if (EspecialUtils.isMega30(modalidade, numeroConcurso, isEspecial)) {
            estilo.aplicarMega30Anos();
        }
        if (EspecialUtils.isLotecaPais(modalidade, numeroConcurso, isEspecial)) {
            estilo.aplicarLotecaPais();
        }

        return estilo;
    }

    private void aplicarMega30Anos() {
        imagemEspecialCarrossel = R.drawable.cabecalho_mega_trinta_anos_carrossel;
        imagemEspecialDetalhes = R.drawable.cabecalho_mega_trinta_anos_detalhe;
        imagemSubCabecalho = R.drawable.rodape_mega_trinta_anos_inferior;
        imagemEspecialQuadrada = R.drawable.mega_30_quadrado;
        imagemEspecialSimples = R.drawable.mega_virada_simples;
        imagemEspecialDupla = R.drawable.mega_virada_dupla;
    }

    private void aplicarLotecaPais() {
        imagemEspecialCarrossel = R.drawable.cabecalho_loteca_pais_carrossel_alta;
        imagemEspecialDetalhes = R.drawable.cabecalho_loteca_pais_detalhe_simples;
        imagemSubCabecalho = R.drawable.rodape_mega_trinta_anos_inferior;
        imagemEspecialQuadrada = R.drawable.loteca_pais_quadrado_alta;
        imagemEspecialSimples = R.drawable.cabecalho_loteca_pais_detalhe_simples;
        imagemEspecialDupla = R.drawable.loteca_pais_dupla;
    }


    private void createEstilo(ModalidadeEnum modalidade){
        switch (modalidade) {
            case QUINA:
                corClara = R.color.quina_claro_mkp;
                corEscura = R.color.quina_escuro_mkp;
                trapezio = R.drawable.background_trapeze_quina_mkp;
                trapezioEspecial = R.drawable.quina_trapezio;
                fundoEspecial = R.drawable.quina_sao_joao_background;
                shapeBtnDetalhes = R.drawable.btn_rounded_quina_outlined;
                imagemEspecialCarrossel = R.drawable.quina_sao_joao_novo;
                imagemTriangulo = R.drawable.ic_tag_bolao_quina;
                imagemTrianguloOutline= R.drawable.ic_tag_bolao_outline_quina;
                imagemEspecialSimples = R.drawable.quina_sao_joao_simples;
                imagemEspecialDupla = R.drawable.quina_sao_joao_dupla;
                imagemEspecialQuadrada = R.drawable.quina_sao_joao_quadrado;
                corLetraLista = R.color.quina_claro_mkp;
                corItemDezena = R.color.quina_claro_mkp;
                corFonteFundoClaro = R.color.branco;
                corFonteFundoEscuro = R.color.branco;
                trevoFundoClaro = R.drawable.trevo_quina_fundo_branco;
                trevoFundoEscuro = R.drawable.trevo_quina_fundo_escuro;
                tarjaEspecialTrevoCombo = R.drawable.tarja_trevo_quina_especial_combos;
                trevoFundoDetalheCombo = R.drawable.trevo_quina_detalhe_combo;
                imagemSubCabecalho = R.drawable.rodape_sao_joao_inferior;
                break;
            case DUPLA_SENA:
                corClara = R.color.dupla_sena_claro_mkp;
                corEscura = R.color.dupla_sena_escuro_mkp;
                trapezio = R.drawable.background_trapeze_dupla_sena_mkp;
                trapezioEspecial = R.drawable.dupa_sena_trapezio;
                fundoEspecial = R.drawable.dupla_pascoa_background;
                shapeBtnDetalhes = R.drawable.btn_rounded_dupla_sena_outlined;
                imagemTriangulo = R.drawable.ic_tag_bolao_dupla_sena;
                imagemTrianguloOutline= R.drawable.ic_tag_bolao_outline_dupla_sena;
                imagemEspecialCarrossel = R.drawable.dupla_pascoa;
                imagemEspecialSimples = R.drawable.dupla_pascoa_simples;
                imagemEspecialDupla = R.drawable.dupla_pascoa_dupla;
                imagemEspecialQuadrada = R.drawable.dupla_pascoa_quadrado;
                imagemEspecialDetalhes = R.drawable.cabecalho_dupla_pascoa_carrossel;
                corLetraLista = R.color.dupla_sena_claro_mkp;
                corItemDezena = R.color.dupla_sena_claro_mkp;
                corFonteFundoClaro = R.color.branco;
                corFonteFundoEscuro = R.color.branco;
                trevoFundoClaro = R.drawable.trevo_dupla_sena_fundo_branco;
                trevoFundoEscuro = R.drawable.trevo_dupla_sena_fundo_escuro;
                tarjaEspecialTrevoCombo = R.drawable.tarja_trevo_dupla_especial_combos;
                trevoFundoDetalheCombo = R.drawable.trevo_dupla_sena_detalhe_combo;
                imagemSubCabecalho = R.drawable.rodape_dupla_de_pascoa_inferior;
                break;
            case LOTOFACIL:
                corClara = R.color.lotofacil_claro_mkp;
                corEscura = R.color.lotofacil_escuro_mkp;
                trapezio = R.drawable.background_trapeze_lotofacil_mkp;
                trapezioEspecial = R.drawable.lotofacil_trapezio;
                fundoEspecial = R.drawable.lotofacil_indep_backgound;
                shapeBtnDetalhes = R.drawable.btn_rounded_lotofacil_outlined;
                imagemTriangulo = R.drawable.ic_tag_bolao_loto_facil;
                imagemTrianguloOutline= R.drawable.ic_tag_bolao_outline_loto_facil;
                imagemEspecialCarrossel = R.drawable.lotofacil_independencia;
                imagemEspecialSimples = R.drawable.lotofacil_indepedencia_simples;
                imagemEspecialDupla = R.drawable.lotofacil_indepedencia_dupla;
                imagemEspecialQuadrada = R.drawable.lotofacil_independencia_quadrado;
                imagemEspecialDetalhes = R.drawable.lotofacil_indepedencia_simples;
                corLetraLista = R.color.lotofacil_claro_mkp;
                corItemDezena = R.color.lotofacil_claro_mkp;
                corFonteFundoClaro = R.color.branco;
                corFonteFundoEscuro = R.color.branco;
                trevoFundoClaro = R.drawable.trevo_lotofacil_fundo_branco;
                trevoFundoEscuro = R.drawable.trevo_lotofacil_fundo_escuro;
                tarjaEspecialTrevoCombo = R.drawable.tarja_trevo_lotofacil_especial_combos;
                trevoFundoDetalheCombo = R.drawable.trevo_lotofacil_detalhe_combo;
                imagemSubCabecalho = R.drawable.rodape_lotofacil_da_independencia_inferior;
                break;
            case LOTOMANIA:
                corClara = R.color.lotomania_claro_mkp;
                corEscura = R.color.lotomania_escuro_mkp;
                trapezio = R.drawable.background_trapeze_lotomanial_mkp;
                trapezioEspecial = -1;
                fundoEspecial = -1;
                shapeBtnDetalhes = R.drawable.btn_rounded_lotomania_outlined;
                imagemTriangulo = R.drawable.ic_tag_bolao_lotomania;
                imagemTrianguloOutline= R.drawable.ic_tag_bolao_outline_lotomania;
                imagemEspecialCarrossel = -1;
                imagemEspecialSimples = -1;
                imagemEspecialDupla = -1;
                imagemEspecialQuadrada = -1;
                corLetraLista = R.color.lotomania_claro_mkp;
                corItemDezena = R.color.lotomania_claro_mkp;
                corFonteFundoClaro = R.color.branco;
                corFonteFundoEscuro = R.color.branco;
                trevoFundoClaro = R.drawable.trevo_lotomania_fundo_branco;
                trevoFundoEscuro = R.drawable.trevo_lotomania_fundo_escuro;
                tarjaEspecialTrevoCombo = -1;
                trevoFundoDetalheCombo = R.drawable.trevo_lotomania_detalhe_combo;
                break;
            case MAIS_MILIONARIA:
                corClara = R.color.milionaria_claro_mkp;
                corEscura = R.color.milionaria_escuro_mkp;
                trapezio = R.drawable.background_trapeze_milionaria_mkp;
                trapezioEspecial = -1;
                fundoEspecial = -1;
                shapeBtnDetalhes = R.drawable.btn_rounded_milionaria_outlined;
                imagemTriangulo = R.drawable.ic_tag_bolao_milionaria;
                imagemTrianguloOutline= R.drawable.ic_tag_bolao_outline_milionaria;
                imagemEspecialCarrossel = -1;
                imagemEspecialSimples = -1;
                imagemEspecialDupla = -1;
                imagemEspecialQuadrada = -1;
                corLetraLista = R.color.milionaria_claro_mkp;
                corItemDezena = R.color.milionaria_claro_mkp;
                corFonteFundoClaro = R.color.branco;
                corFonteFundoEscuro = R.color.branco;
                trevoFundoClaro = R.drawable.trevo_milionaria_fundo_branco;
                trevoFundoEscuro = R.drawable.trevo_milionaria_fundo_escuro;
                tarjaEspecialTrevoCombo = -1;
                trevoFundoDetalheCombo = R.drawable.trevo_mais_milionaria_detalhe_combo;
                break;
            case SUPER_7:
                corClara = R.color.super_sete_claro_mkp;
                corEscura = R.color.super_sete_escuro_mkp;
                trapezio = R.drawable.background_trapeze_super_sete_mkp;
                trapezioEspecial = -1;
                fundoEspecial = -1;
                shapeBtnDetalhes = R.drawable.btn_rounded_super_sete_outlined;
                imagemTriangulo = R.drawable.ic_tag_bolao_super_sete;
                imagemTrianguloOutline= R.drawable.ic_tag_bolao_outline_super_sete;
                imagemEspecialCarrossel = -1;
                imagemEspecialSimples = -1;
                imagemEspecialDupla = -1;
                imagemEspecialQuadrada = -1;
                corLetraLista = R.color.super_sete_letra_mkp;
                corItemDezena = R.color.super_sete_letra_mkp;
                corFonteFundoClaro = R.color.super_sete_letra_mkp;
                corFonteFundoEscuro = R.color.branco;
                trevoFundoClaro = R.drawable.trevo_super_sete_fundo_branco;
                trevoFundoEscuro = R.drawable.trevo_super_sete_fundo_escuro;
                tarjaEspecialTrevoCombo = -1;
                trevoFundoDetalheCombo = R.drawable.trevo_super_sete_detalhe_combo;
                break;
            case LOTECA:
                corClara = R.color.loteca_claro_mkp;
                corEscura = R.color.loteca_escuro_mkp;
                trapezio = R.drawable.background_trapeze_loteca_mkp;
                trapezioEspecial = R.drawable.loteca_copa_trapezio;
                fundoEspecial = -1;
                shapeBtnDetalhes = R.drawable.btn_rounded_loteca_outlined;
                imagemTriangulo = R.drawable.ic_tag_bolao_loteca;
                imagemTrianguloOutline= R.drawable.ic_tag_bolao_outline_loteca;
                imagemEspecialCarrossel = R.drawable.loteca_copa;
                imagemEspecialSimples = R.drawable.loteca_copa_simples;
                imagemEspecialDupla = R.drawable.loteca_copa_dupla;
                imagemEspecialQuadrada = R.drawable.loteca_copa_quadrado;
                imagemEspecialDetalhes = R.drawable.loteca_copa_simples;
                corLetraLista = R.color.loteca_claro_mkp;
                corItemDezena = R.color.loteca_claro_mkp;
                corFonteFundoClaro = R.color.branco;
                corFonteFundoEscuro = R.color.branco;
                trevoFundoClaro = R.drawable.trevo_loteca_fundo_branco;
                trevoFundoEscuro = R.drawable.trevo_loteca_fundo_escuro;
                tarjaEspecialTrevoCombo = -1;
                trevoFundoDetalheCombo = R.drawable.trevo_loteca_detalhe_combo;
                break;
            case TIMEMANIA:
                corClara = R.color.timemania_claro_mkp;
                corEscura = R.color.timemania_escuro_mkp;
                trapezio = R.drawable.background_trapeze_timemania_mkp;
                trapezioEspecial = -1;
                fundoEspecial = -1;
                shapeBtnDetalhes = R.drawable.btn_rounded_timemania_outlined;
                imagemTriangulo = R.drawable.ic_tag_bolao_timemania;
                imagemTrianguloOutline= R.drawable.ic_tag_bolao_outline_timemania;
                imagemEspecialCarrossel = -1;
                imagemEspecialSimples = -1;
                imagemEspecialDupla = -1;
                imagemEspecialQuadrada = -1;
                corLetraLista = R.color.timemania_letra_mkp;
                corItemDezena = R.color.timemania_claro_mkp;
                corFonteFundoClaro = R.color.timemania_letra_mkp;
                corFonteFundoEscuro = R.color.timemania_letra_mkp;
                trevoFundoClaro = R.drawable.trevo_timemania_fundo_branco;
                trevoFundoEscuro = R.drawable.trevo_timemania_fundo_escuro;
                tarjaEspecialTrevoCombo = -1;
                trevoFundoDetalheCombo = R.drawable.trevo_timemania_detalhe_combo;
                break;
            case DIA_DE_SORTE:
                corClara = R.color.dia_sorte_claro_mkp;
                corEscura = R.color.dia_sorte_escuro_mkp;
                trapezio = R.drawable.background_trapeze_dia_sorte_mkp;
                trapezioEspecial = -1;
                fundoEspecial = -1;
                shapeBtnDetalhes = R.drawable.btn_rounded_dia_sorte_outlined;
                imagemTriangulo = R.drawable.ic_tag_bolao_dia_sorte;
                imagemTrianguloOutline= R.drawable.ic_tag_bolao_outline_dia_sorte;
                imagemEspecialCarrossel = -1;
                imagemEspecialSimples = -1;
                imagemEspecialDupla = -1;
                imagemEspecialQuadrada = -1;
                corLetraLista = R.color.dia_sorte_letra_mkp;
                corItemDezena= R.color.dia_sorte_claro_mkp;
                corFonteFundoClaro = R.color.dia_sorte_letra_mkp;
                corFonteFundoEscuro = R.color.branco;
                trevoFundoClaro = R.drawable.trevo_dia_de_sorte_fundo_branco;
                trevoFundoEscuro = R.drawable.trevo_dia_de_sorte_fundo_escuro;
                tarjaEspecialTrevoCombo = -1;
                trevoFundoDetalheCombo = R.drawable.trevo_dia_de_sorte_detalhe_combo;
                break;
            case INSTANTANEA:
                corClara = R.color.instantanea_claro;
                corEscura = R.color.instantanea_escuro;
                trapezio = R.drawable.background_trapeze_instantanea;
                trapezioEspecial = -1;
                fundoEspecial = -1;
                shapeBtnDetalhes = -1;
                imagemTriangulo = -1;
                imagemTrianguloOutline= -1;
                imagemEspecialCarrossel = -1;
                imagemEspecialSimples = -1;
                imagemEspecialDupla = -1;
                imagemEspecialQuadrada = -1;
                corLetraLista = R.color.instantanea_claro;
                corItemDezena = R.color.instantanea_claro;
                corFonteFundoClaro = R.color.branco;
                corFonteFundoEscuro = R.color.branco;
                trevoFundoClaro = R.drawable.trevos_instantanea_fundo_branco;
                trevoFundoEscuro = R.drawable.trevos_instantanea_fundo_escuro;
                tarjaEspecialTrevoCombo =-1;
                trevoFundoDetalheCombo = -1;
                break;
            case COMBO:
                corClara = R.color.combo_claro;
                corEscura = R.color.combo_escuro;
                trapezio = R.drawable.background_trapeze_combo;
                trapezioEspecial = -1;
                fundoEspecial = -1;
                shapeBtnDetalhes = -1;
                imagemTriangulo = -1;
                imagemTrianguloOutline= -1;
                imagemEspecialCarrossel = -1;
                imagemEspecialSimples = -1;
                imagemEspecialDupla = -1;
                imagemEspecialQuadrada = -1;
                corLetraLista = R.color.combo_claro;
                corItemDezena = R.color.combo_claro;
                corFonteFundoClaro = R.color.branco;
                corFonteFundoEscuro = R.color.branco;
                trevoFundoClaro = R.drawable.trevos_combo_fundo_escuro;
                trevoFundoEscuro = R.drawable.trevos_combo_fundo_escuro;
                tarjaEspecialTrevoCombo =-1;
                trevoFundoDetalheCombo = -1;
                break;
            default:
                //MEGASENA
                if (EspecialUtils.isParametrosOutubroRosa()){
                    preencheOutubroRosa();
                } else {
                    corClara = R.color.mega_verde_claro_mkp;
                    corEscura = R.color.mega_verde_escuro_mkp;
                    trapezio = R.drawable.background_trapeze_megasena_mkp;
                    trapezioEspecial = R.drawable.mega_sena_trapezio;
                    fundoEspecial = R.drawable.mega_virada_background;
                    shapeBtnDetalhes = R.drawable.btn_rounded_green_outlined;
                    imagemTriangulo = R.drawable.ic_tag_bolao;
                    imagemTrianguloOutline= R.drawable.ic_tag_bolao_outline;
                    imagemEspecialCarrossel = R.drawable.mega_virada_novo;
                    imagemEspecialSimples = R.drawable.mega_virada_simples;
                    imagemEspecialDupla = R.drawable.mega_virada_dupla;
                    imagemEspecialQuadrada = R.drawable.mega_virada_quadrado;
                    imagemEspecialDetalhes = R.drawable.cabecalho_mega_da_virada_detalhe_aposta;
                    corLetraLista = R.color.mega_verde_claro_mkp;
                    corItemDezena = R.color.mega_verde_claro_mkp;
                    corFonteFundoClaro = R.color.branco;
                    corFonteFundoEscuro = R.color.branco;
                    trevoFundoClaro = R.drawable.trevo_megasena_fundo_branco;
                    trevoFundoEscuro = R.drawable.trevo_megasena_fundo_escuro;
                    tarjaEspecialTrevoCombo = R.drawable.tarja_trevo_mega_especial_combos;
                    trevoFundoDetalheCombo = R.drawable.trevo_mega_sena_detalhe_combo;
                    imagemSubCabecalho = R.drawable.rodape_mega_da_virada_inferior;
                }
        }

    }

    private void preencheOutubroRosa(){
        corClara = R.color.outubro_rosa_primario;
        corEscura = R.color.outubro_rosa_secundario;
        trapezio = R.drawable.background_trapeze_megasena_outubro_rosa;
        trapezioEspecial = R.drawable.mega_sena_trapezio_outubro_rosa;
        fundoEspecial = R.drawable.mega_virada_background;
        shapeBtnDetalhes = R.drawable.btn_rounded_rosa_outlined;
        imagemTriangulo = R.drawable.ic_tag_bolao_outubro_rosa;
        imagemTrianguloOutline= R.drawable.ic_tag_bolao_rosa_outline;
        imagemEspecialCarrossel = R.drawable.mega_virada_novo;
        imagemEspecialSimples = R.drawable.mega_virada_simples;
        imagemEspecialDupla = R.drawable.mega_virada_dupla;
        imagemEspecialQuadrada = R.drawable.mega_virada_quadrado;
        imagemEspecialDetalhes = R.drawable.cabecalho_mega_da_virada_detalhe_aposta;
        corLetraLista = R.color.outubro_rosa_secundario;
        corItemDezena = R.color.outubro_rosa_secundario;
        corFonteFundoClaro = R.color.outubro_rosa_texto;
        corFonteFundoEscuro = R.color.branco;
        trevoFundoClaro = R.drawable.trevo_megasena_outubro_rosa_fundo_branco;
        trevoFundoEscuro = R.drawable.trevo_megasena_outubro_rosa_fundo_escuro;
        tarjaEspecialTrevoCombo = R.drawable.tarja_trevo_mega_outubro_rosa_combos;
        trevoFundoDetalheCombo = R.drawable.trevo_mega_sena_detalhe_combo_outubro_rosa;
        imagemSubCabecalho = R.drawable.rodape_mega_da_virada_inferior;
    }

    public void alteraOutubroRosaPorTela(){
        corLetraLista = R.color.outubro_rosa_texto;
    }

    public int getCorClara() {
        return corClara;
    }

    public int getCorEscura() {
        return corEscura;
    }

    public int getTrapezio() {
        return trapezio;
    }

    public int getShapeBtnDetalhes() {
        return shapeBtnDetalhes;
    }

    public int getImagemEspecialCarrossel() {
        return imagemEspecialCarrossel;
    }

    public int getImagemTriangulo() {
        return imagemTriangulo;
    }

    public int getImagemTrianguloOutline() {
        return imagemTrianguloOutline;
    }

    public int getImagemEspecialSimples() {
        return imagemEspecialSimples;
    }

    public int getImagemEspecialDupla() {
        return imagemEspecialDupla;
    }

    public int getImagemEspecialQuadrada() {
        return imagemEspecialQuadrada;
    }

    public int getCorLetraLista() {
        return corLetraLista;
    }

    public int getCorItemDezena() {
        return corItemDezena;
    }

    public int getCorFonteFundoClaro() {
        return corFonteFundoClaro;
    }

    public int getCorFonteFundoEscuro() {
        return corFonteFundoEscuro;
    }

    public int getTrevoFundoClaro() {
        return trevoFundoClaro;
    }

    public int getTrevoFundoEscuro() {
        return trevoFundoEscuro;
    }

    public int getTarjaEspecialTrevoCombo() {
        return tarjaEspecialTrevoCombo;
    }
    public int getTrevoFundoDetalheCombo() {
        return trevoFundoDetalheCombo;
    }

    public int getTrapezioEspecial() {
        return trapezioEspecial;
    }

    public int getFundoEspecial() {
        return fundoEspecial;
    }

    public int getImagemSubCabecalho() {
        return imagemSubCabecalho;
    }

    public void setImagemSubCabecalho(int imagemSubCabecalho) {
        this.imagemSubCabecalho = imagemSubCabecalho;
    }

    public int getImagemEspecialDetalhes() {
        return imagemEspecialDetalhes;
    }

}

