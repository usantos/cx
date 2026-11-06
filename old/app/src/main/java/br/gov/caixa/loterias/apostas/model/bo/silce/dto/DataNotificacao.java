package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

public class DataNotificacao {

    @SerializedName("time")
    private Long time;

    @SerializedName("month")
    private Integer month;

    @SerializedName("year")
    private Integer year;

    @SerializedName("calendar")
    private Long calendar;

    @SerializedName("timeInMillis")
    private Long timeInMillis;

    @SerializedName("day")
    private Integer day;

    @SerializedName("diaSemana")
    private Boolean diaSemana;

    @SerializedName("finalDeSemana")
    private Boolean finalDeSemana;

    @SerializedName("hora")
    private Hora hora;

    @SerializedName("passado")
    private Boolean passado;

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Long getCalendar() {
        return calendar;
    }

    public void setCalendar(Long calendar) {
        this.calendar = calendar;
    }

    public Long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(Long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Boolean getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(Boolean diaSemana) {
        this.diaSemana = diaSemana;
    }

    public Boolean getFinalDeSemana() {
        return finalDeSemana;
    }

    public void setFinalDeSemana(Boolean finalDeSemana) {
        this.finalDeSemana = finalDeSemana;
    }

    public Hora getHora() {
        return hora;
    }

    public void setHora(Hora hora) {
        this.hora = hora;
    }

    public Boolean getPassado() {
        return passado;
    }

    public void setPassado(Boolean passado) {
        this.passado = passado;
    }

    public static class Hora {

        @SerializedName("horaDoDia")
        private Integer horaDoDia;

        @SerializedName("minuto")
        private Integer minuto;

        @SerializedName("segundo")
        private Integer segundo;

        @SerializedName("time")
        private Long time;

        @SerializedName("calendar")
        private Long calendar;

        public Integer getHoraDoDia() {
            return horaDoDia;
        }

        public void setHoraDoDia(Integer horaDoDia) {
            this.horaDoDia = horaDoDia;
        }

        public Integer getMinuto() {
            return minuto;
        }

        public void setMinuto(Integer minuto) {
            this.minuto = minuto;
        }

        public Integer getSegundo() {
            return segundo;
        }

        public void setSegundo(Integer segundo) {
            this.segundo = segundo;
        }

        public Long getTime() {
            return time;
        }

        public void setTime(Long time) {
            this.time = time;
        }

        public Long getCalendar() {
            return calendar;
        }

        public void setCalendar(Long calendar) {
            this.calendar = calendar;
        }
    }
}