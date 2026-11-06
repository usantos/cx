package br.gov.caixa.loterias.apostas.utils;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;


@JsonAdapter(SituacaoCotaEnum.Adapter.class)
public enum SituacaoCotaEnum {
	CRIADA("CRIADA"),

	DISPONIVEL("DISPONIVEL"),

	RESERVADA("RESERVADA"),

	BAIXADA_NAO_IMPRESSA("BAIXADA_NAO_IMPRESSA"),

	BAIXADA_NO_ENCERRAMENTO("BAIXADA_NO_ENCERRAMENTO"),

	FISICA_IMPRESSA("FISICA_IMPRESSA"),

	FISICA_NAO_IMPRESSA("FISICA_NAO_IMPRESSA"),

	BAIXADA_IMPRESSA("BAIXADA_IMPRESSA"),

	VENDIDA("VENDIDA"),

	ESTORNADA("ESTORNADA"),

	SEM_NSBC("SEM_NSBC"),

	FISICA_REIMPRESSA("FISICA_REIMPRESSA");

	private String value;

	SituacaoCotaEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}

	public static SituacaoCotaEnum fromValue(String text) {
		for (SituacaoCotaEnum b : SituacaoCotaEnum.values()) {
			if (String.valueOf(b.value).equals(text)) {
				return b;
			}
		}
		return null;
	}

	public static class Adapter extends TypeAdapter<SituacaoCotaEnum> {
		@Override
		public void write(final JsonWriter jsonWriter, final SituacaoCotaEnum enumeration) throws IOException {
			jsonWriter.value(enumeration.getValue());
		}

		@Override
		public SituacaoCotaEnum read(final JsonReader jsonReader) throws IOException {
			String value = jsonReader.nextString();
			return SituacaoCotaEnum.fromValue(String.valueOf(value));
		}
	}
}
