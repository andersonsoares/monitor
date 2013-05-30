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
	
	@Indexed
	private String name;
	
	@Indexed
	private Key<Glossary> glossary;

	private int count;
	
	private boolean removed;
	
	public Word() {}

	public Word(String name, Integer count) {
		this.name = name;
		this.count = count; 	
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

	public Key<Glossary> getGlossary() {
		return glossary;
	}

	public void setGlossary(Key<Glossary> glossary) {
		this.glossary = glossary;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public boolean isRemoved() {
		return removed;
	}

	public void setRemoved(boolean removed) {
		this.removed = removed;
	}
	
}
