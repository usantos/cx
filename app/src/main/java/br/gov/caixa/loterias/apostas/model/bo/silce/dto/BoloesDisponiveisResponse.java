package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * BoloesDisponiveisResponse
 */
@ApiModel(description = "")
public class BoloesDisponiveisResponse extends SilceResponse<BoloesDTO> {
}
