package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NumerosSelecionadosSerializer implements JsonSerializer<List<?>>, JsonDeserializer<List<?>> {
	@Override
	public List<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		List<Integer>       listaNumerosSelecionados = new ArrayList<>();
		List<List<Integer>> matrizSuperSete          = new ArrayList<>();
		Gson                gson                     = new Gson();
		if (json instanceof JsonArray) {
			try {
				Type tipoLista = new TypeToken<List<Integer>>() {
				}.getType();
				listaNumerosSelecionados = gson.fromJson(json, tipoLista);
			} catch (Exception e){
				Type tipoMatriz = new TypeToken<List<List<Integer>>>() {
				}.getType();
				matrizSuperSete = gson.fromJson(json, tipoMatriz);
			}
		}
		return listaNumerosSelecionados.size() > 0 ? listaNumerosSelecionados: matrizSuperSete;
	}

	@Override
	public JsonElement serialize(List<?> src, Type typeOfSrc, JsonSerializationContext context) {
		JsonArray          array                   = new JsonArray();
		List<Integer>       listaNumerosSelecionados = new ArrayList<>();
		List<List<Integer>> matrizSuperSete          = new ArrayList<>();

		Type tipoLista = new TypeToken<List<Integer>>() {}.getType();
		Type tipoMatriz = new TypeToken<List<List<Integer>>>() {}.getType();
		if (typeOfSrc.equals(tipoLista)) {
			for (Integer i: (List<Integer>)src) {
				array.add(i);
			}
		}else {
			for (List<Integer> listaInteiros: (List<List<Integer>>)src) {
				array.add((JsonElement) listaInteiros);
			}
		}
		return array;
	}
}
