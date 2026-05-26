package br.gov.caixa.loterias.apostas.model.model;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;

public class AccordionModel {

    private String bodyText;
    private boolean expanded;
    private final ModalidadeEnum modalidadeEnum;

    private boolean isEspecial = false;

    public AccordionModel(ModalidadeEnum modalidade, boolean isEspecial) {
        this.isEspecial = isEspecial;
        this.modalidadeEnum = modalidade;
        createString(modalidade);
    }

    private void createString(ModalidadeEnum modalidade){
        switch (modalidade) {
            case DIA_DE_SORTE:
                this.bodyText = "Escolha de 7 a 15 números entre os 31 disponíveis, logo após escolha o mês de sorte e ganhe prêmios acertando 7, 6, 5 ou 4 dos números sorteados e/ou acertando o mês de sorte. Você pode concorrer com a mesma aposta por 3, 6, 9 ou 12 concursos consecutivos com a opção teimosinha. Com a opção surpresinha, o sistema escolhe os números por você, tornando sua aposta mais prática, nesta opção os números apenas serão revelados a você após o pagamento.";
                break;
            case DUPLA_SENA:
                this.bodyText = "Escolha de 6 a 15 números entre os 50 disponíveis e ganhe prêmios acertando 3 (terno), 4 (quadra), 5 (quina) ou 6 (sena) dos números sorteados em um ou nos dois sorteios. Você pode concorrer com a mesma aposta por 2, 3, 4, 6, 8, 9 ou 12 concursos consecutivos com a opção teimosinha. Com a opção surpresinha, o sistema escolhe os números por você, tornando sua aposta mais prática, nesta opção os números apenas serão revelados a você após o pagamento.";
                break;
            case LOTECA:
                this.bodyText = "Escolha os resultados dos jogos abaixo.\\nVocê precisa apostar em todos os jogos, sendo que pelo menos 1 palpite deve ser duplo\n" +
                        "        (apostar nos dois times ou em um time e no empate) ou triplo (apostar em empate e nos dois times).\n" +
                        "        Você ganha com 13 ou 14 palpites corretos.";
                break;
            case LOTOFACIL:
                this.bodyText = "Escolha de 15 a 20 números entre os 25 números disponíveis e ganhe prêmios acertando 11, 12, 13, 14 ou 15 dos números sorteados. Você pode concorrer com a mesma aposta por 2, 3, 4, 6, 8, 9, 12, 18 ou 24 concursos consecutivos com a opção teimosinha. Com a opção surpresinha, o sistema escolhe os números por você, tornando sua aposta mais prática, nesta opção os números apenas serão revelados a você após o pagamento.";
                break;
            case LOTOGOL:
                this.bodyText = "";
                break;
            case LOTOMANIA:
                this.bodyText = "Escolha 50 números entre os 100 disponíveis e ganhe prêmios acertando 20, 19, 18, 17, 16, 15 ou nenhum dos números sorteados. Escolhendo a opção \"aposta-espelho\" o sistema realiza um jogo automaticamente com os 50 números que você não escolheu, aumentando suas chances. Você pode concorrer com a mesma aposta por 2, 3, 4, 6, 8, 9 ou 12 concursos consecutivos com a opção teimosinha. Com a opção surpresinha, o sistema escolhe os números por você, tornando sua aposta mais prática, nesta opção os números apenas serão revelados a você após o pagamento.";
                break;
            case MEGA_SENA:
                this.bodyText = "Escolha de 6 a 20 números entre os 60 disponíveis e ganhe prêmios acertando 6 (sena), 5 (quina) ou 4 (quadra) dos números sorteados. Você pode concorrer com a mesma aposta por 2, 3, 4, 6, 8, 9 ou 12 concursos consecutivos com a opção teimosinha. Com a opção surpresinha, o sistema escolhe os números por você, tornando sua aposta mais prática, nesta opção os números apenas serão revelados a você após o pagamento.";
                break;
            case QUINA:
                this.bodyText = "Escolha de 5 a 15 números entre os 80 disponíveis e ganhe prêmios acertando 2 (duque), 3 (terno) ou 4 (quadra) ou 5 (quina) dos números sorteados. Você pode concorrer com a mesma aposta por 3, 6, 12, 18 ou 24 concursos consecutivos com a opção teimosinha. Com a opção surpresinha, o sistema escolhe os números por você, tornando sua aposta mais prática, nesta opção os números apenas serão revelados a você após o pagamento.";
                break;
            case TIMEMANIA:
                this.bodyText = "Escolha 10 números entre os 80 disponíveis, logo após escolha o time do coração e ganhe prêmios acertando 7, 6, 5, 4 e 3 dos números sorteados e/ou acertando o time do coração. Você pode concorrer com a mesma aposta por 3, 6, 9 ou 12 concursos consecutivos com a opção teimosinha. Com a opção surpresinha, o sistema escolhe os números por você, tornando sua aposta mais prática, nesta opção os números apenas serão revelados a você após o pagamento.";
                break;
            case SUPER_7:
                this.bodyText = "O Super sete tem 7 colunas, cada uma com os números de 0 a 9. Você deve escolher, no mínimo, 1 número em cada coluna. Se quiser, pode marcar mais números, até 3 por coluna, aumentando suas chances de ganhar. No sorteio, é escolhido 1 número de cada coluna (total de 7 números). Ganha quem acertar a partir de 3 números na ordem correta das colunas.";
                break;
            case MAIS_MILIONARIA:
                this.bodyText = "Escolha de 6 a 12 números entre os 50 disponíveis, logo após escolha de 2 a 6 trevos dos 6 disponiveis e ganhe prêmios acertando 6 números + 1, 2 ou nenhum trevo; 5 números + 1, 2 ou nenhum trevo; 4 números + 1, 2 ou nenhum trevo; 3 números + 1 ou 2 trevos; 2 números + 1 ou 2 trevos. Você pode concorrer com a mesma aposta por 2, 3, 4 ou 5 concursos consecutivos com a opção teimosinha. Com a opção surpresinha, o sistema escolhe os números por você, tornando sua aposta mais prática, nesta opção os números apenas serão revelados a você após o pagamento.";
                break;
            case INSTANTANEA:
                this.bodyText = "";
                break;
            case COMBO:
                this.bodyText = "";
                break;
            case BOLAO:
                this.bodyText = "";
        }
    }

    public String getBodyText() {
        return bodyText;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public ModalidadeEnum getModalidadeEnum() {
        return modalidadeEnum;
    }

    public boolean isEspecial() {
        return isEspecial;
    }

    public void setEspecial(boolean especial) {
        isEspecial = especial;
    }
}
