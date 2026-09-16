package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import java.util.Date;

public class NotificacaoDTO2To {

  public static NotificacaoDTO toNotificacaoDTO(NotificacaoCentralDTO dto2) {

    NotificacaoDTO dto = new NotificacaoDTO();

    dto.setId(dto2.getId());
    dto.setTitulo(dto2.getTitulo());
    dto.setConteudo(dto2.getConteudo());

    if (dto2.getDataVisualizacao() != null) {

      Data data = new Data();

      data.setTimeInMillis(dto2.getDataVisualizacao().getTimeInMillis());

      Long calendar = dto2.getDataVisualizacao().getCalendar();
      if (calendar != null) {
        Date dateAux = new Date(calendar);
        data.setCalendar(dateAux);
      }

      dto.setDataVisualizacao(data);
    }

    return dto;
  }
}