package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

import io.swagger.annotations.ApiModel;

@ApiModel(description = "")
public class CardHolderRecargaPay {

	@SerializedName("name")
	private String name;
	@SerializedName("type")
	private String type;
	@SerializedName("email")
	private String email;
	@SerializedName("document")
	private String document;
	@SerializedName("birthdate")
	private String birthdate;
	@SerializedName("phones")
	private List<String> phones;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public String getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}

	public List<String> getPhones() {
		return phones;
	}

	public void setPhones(List<String> phones) {
		this.phones = phones;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		CardHolderRecargaPay that = (CardHolderRecargaPay) o;
		return Objects.equals(name, that.name) && Objects.equals(type, that.type) && Objects.equals(email, that.email) && Objects.equals(document, that.document) && Objects.equals(birthdate, that.birthdate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, type, email, document, birthdate);
	}

	@Override
	public String toString() {
		return "CardHolderRecargaPay{" +
				"name='" + name + '\'' +
				", type='" + type + '\'' +
				", email='" + email + '\'' +
				", document='" + document + '\'' +
				", birthdate='" + birthdate + '\'' +
				'}';
	}
}
