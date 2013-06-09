package models;

import org.bson.types.ObjectId;

import com.google.code.morphia.Key;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;

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
	
}
