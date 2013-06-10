package models.forms;

import models.Dictionary;

public class DictionaryForm {

	private String name;
	
	private String descricao;
	
	// preencher o dicionario com o txt ja existente
	private Boolean fillDictionary;
	
	private Boolean considerAccents;
	
	public DictionaryForm() {}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Boolean getFillDictionary() {
		return fillDictionary;
	}

	public void setFillDictionary(Boolean fillDictionary) {
		this.fillDictionary = fillDictionary;
	}
	
	public Boolean getConsiderAccents() {
		return considerAccents;
	}
	
	public void setConsiderAccents(Boolean considerAccents) {
		this.considerAccents = considerAccents;
	}

	public Dictionary getDictionary() {
		Dictionary d = new Dictionary();
		d.setName(name);
		d.setDescricao(descricao);
		d.setConsiderAccents(considerAccents);
		
		return d;
	}

	
	
}
