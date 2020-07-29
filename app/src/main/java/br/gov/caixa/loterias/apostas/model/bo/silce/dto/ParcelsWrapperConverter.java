package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import org.parceler.Parcels;
import org.parceler.converter.NullableParcelConverter;

public class ParcelsWrapperConverter extends NullableParcelConverter<Object> {

    @Override
    public void nullSafeToParcel(Object input, android.os.Parcel parcel) {
        parcel.writeParcelable(Parcels.wrap(input), 0);
    }

    @Override
    public Object nullSafeFromParcel(android.os.Parcel parcel) {
        return Parcels.unwrap(parcel.readParcelable(ParcelsWrapperConverter.class.getClassLoader()));
    }
}
