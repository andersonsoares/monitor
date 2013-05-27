package models;

import java.util.HashSet;

import org.bson.types.ObjectId;

import com.google.code.morphia.Key;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;

/**
 * Class that will represent word root's to be saved
 * Example:
 * 
 * Word: aprendizado / aprendiz / aprender
 * Root: aprend
 * 
 * @author Anderson Soares < aersandersonsoares@gmail.com >
 */

@Entity("roots")
public class Root {

	@Id
	private ObjectId id;
	
	@Indexed
	private String rootWord;
	
	@Indexed
	private Key<Glossary> glossary;
	
	// how many times the root was captured
	private int count;
	
	private HashSet<String> wordsGenerators;
	
	private boolean removed;
	
	
	/*
	 * Default Constructor
	 */
	public Root() {
		wordsGenerators = new HashSet<String>();
	}
	
	public Root(String rootWord, String tokenGenerator) {
		this.rootWord = rootWord;
		wordsGenerators = new HashSet<String>();
		wordsGenerators.add(tokenGenerator);
	}

	/*
	 * Getters / Setters
	 */
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getRootWord() {
		return rootWord;
	}

	public void setRootWord(String rootWord) {
		this.rootWord = rootWord;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public HashSet<String> getWordsGenerators() {
		return wordsGenerators;
	}

	public void setWordsGenerators(HashSet<String> wordsGenerators) {
		this.wordsGenerators = wordsGenerators;
	}

	public boolean isRemoved() {
		return removed;
	}

	public void setRemoved(boolean removed) {
		this.removed = removed;
	}

	public Key<Glossary> getGlossary() {
		return glossary;
	}

	public void setGlossary(Key<Glossary> glossary) {
		this.glossary = glossary;
	}
	
	
}
