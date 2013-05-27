package models;

import org.bson.types.ObjectId;

import com.google.code.morphia.Key;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;


@Entity
public class Acronym {
	
	@Id
	private ObjectId id;
	
	@Indexed
	private Key<Glossary> glossary;
	
	@Indexed
	private String name;
	
	private int count;
	
	private boolean removed;
	
	public Acronym() {}

	public Acronym(Key<Glossary> glossary, String name) {
		super();
		this.glossary = glossary;
		this.name = name;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Key<Glossary> getGlossary() {
		return glossary;
	}

	public void setGlossary(Key<Glossary> glossary) {
		this.glossary = glossary;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
