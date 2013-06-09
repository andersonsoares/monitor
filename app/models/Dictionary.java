package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

import play.data.validation.Constraints.Required;
import system.ValidationError;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;


@Entity("dictionary")
public class Dictionary {
	
	@Id
	private ObjectId id;
	
	@Required
	private String name;
	
	@Indexed
	private String nameLowerCase;
	
	private String descricao;

	private Date createdAt;
	
	public Dictionary() {
		this.createdAt = new Date(System.currentTimeMillis());
	}
	
	public Dictionary(String name) {
		this.createdAt = new Date(System.currentTimeMillis());
		this.name = name;
		this.nameLowerCase = name.toLowerCase();
	}

	public Dictionary(String name, String descricao) {
		this.createdAt = new Date(System.currentTimeMillis());
		this.name = name;
		this.nameLowerCase = name.toLowerCase();
		this.descricao = descricao;
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
		this.nameLowerCase = name.toLowerCase();
	}

	public String getNameLowerCase() {
		return nameLowerCase;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	
	public List<ValidationError> validate() {
		
		List<ValidationError> errors = new ArrayList<ValidationError>();
		
		if (name == null || name.isEmpty()) {
			errors.add(new ValidationError("name","Dictionary name cant be empty"));
		}
		
		return errors;
		
	}
	

}
