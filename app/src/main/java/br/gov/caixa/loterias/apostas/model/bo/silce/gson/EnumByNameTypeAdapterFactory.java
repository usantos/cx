package br.gov.caixa.loterias.apostas.model.bo.silce.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class EnumByNameTypeAdapterFactory implements TypeAdapterFactory {
    public static class EnumByNameTypeAdapter<T extends Enum<T>> extends TypeAdapter<T> {
        private final TypeAdapter<String> stringTypeAdapter;
        private Enum<T> klazz;
        private TypeToken<T> typeToken;

        public EnumByNameTypeAdapter(Enum<T> klazz, TypeAdapter<String> stringTypeAdapter) {
            this.klazz = klazz;
            this.stringTypeAdapter = stringTypeAdapter;
        }

        @Override
        public void write(JsonWriter out, T value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                stringTypeAdapter.write(out, value.name());
            }
        }

        @Override
        public T read(JsonReader in) throws IOException {
            return (T) klazz.valueOf(klazz.getClass(), stringTypeAdapter.read(in));
        }
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (!(type.getType() instanceof Enum<?>)) {
            throw new RuntimeException("EnumByNameTypeAdapterFactory was used for a type + "
                    + type.getType() + ", which is not an Enum");
        }

        return new EnumByNameTypeAdapter((Enum<?>) type.getType(), gson.getAdapter(String.class));
    }
}
