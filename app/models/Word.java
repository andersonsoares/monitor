package models;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import system.ValidationError;

import com.google.code.morphia.Key;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;

import dao.WordDAO;

@Entity("words")
public class Word {

	@Id
	private ObjectId id;
	
	private String name;
	
	@Indexed
	private Key<Dictionary> dictionary;

	private boolean removed;
	
	public Word() {}

	public Word(String name) {
		this.name = name;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Key<Dictionary> getDictionary() {
		return dictionary;
	}

	public void setDictionary(Key<Dictionary> dictionary) {
		this.dictionary = dictionary;
	}

	public boolean isRemoved() {
		return removed;
	}

	public void setRemoved(boolean removed) {
		this.removed = removed;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public List<ValidationError> validate() {
		
		List<ValidationError> errors = new ArrayList<ValidationError>();
		
		if (name == null || name.isEmpty()) {
			errors.add(new ValidationError("name", "Word name cant be empty"));
		}
		
		WordDAO wordDAO = new WordDAO();
		Word word = wordDAO.findByName(name, (ObjectId) dictionary.getId());
		
		if (word != null) {
			errors.add(new ValidationError("name", "Word name already exists on this dictionary"));
		}
		
		return errors;
	}
	
}
