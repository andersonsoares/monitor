package models;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

import ptstemmer.Stemmer.StemmerType;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;

@Entity("glossaries")
public class Glossary {
	
	@Id
	private ObjectId id;
	
	private String name;
	private Date createdAt;
	private boolean active;
	private int cutValue;
	private StemmerType algoritm;

	private List<ObjectId> eventsList;
	
	
	public Glossary() {
		this.createdAt = new Date(System.currentTimeMillis());
	}
	
	public Glossary(String name, int cutValue, StemmerType algoritm) {
		super();
		this.createdAt = new Date(System.currentTimeMillis());
		this.name = name;
		this.cutValue = cutValue;
		this.algoritm = algoritm;
	}

	public Glossary(String name, int cutValue, StemmerType algoritm,
			List<ObjectId> eventsList) {
		super();
		this.createdAt = new Date(System.currentTimeMillis());
		this.name = name;
		this.cutValue = cutValue;
		this.algoritm = algoritm;
		this.setEventsList(eventsList);
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

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public StemmerType getAlgoritm() {
		return algoritm;
	}

	public void setAlgoritm(StemmerType algoritm) {
		this.algoritm = algoritm;
	}

	public int getCutValue() {
		return cutValue;
	}

	public void setCutValue(int cutValue) {
		this.cutValue = cutValue;
	}

	public List<ObjectId> getEventsList() {
		return eventsList;
	}

	public void setEventsList(List<ObjectId> eventsList) {
		this.eventsList = eventsList;
	}
	
	
}
