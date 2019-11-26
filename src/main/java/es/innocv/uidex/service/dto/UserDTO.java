package es.innocv.uidex.service.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "User model accepted by Innocv User Rest API ")
public class UserDTO {

	@ApiModelProperty(notes = "The ID of the user", allowEmptyValue = true)
	private Integer id;
	
	@ApiModelProperty(notes = "The name of the user", allowEmptyValue = false)
	@NotEmpty
	private String name;
	
	@ApiModelProperty(notes = "The birthdate of the user", allowEmptyValue = false)
	@JsonFormat(pattern = "dd/MM/yyyy")
	@NotNull
	private LocalDate birthDate;

	public UserDTO() {
		
	}
	
	public UserDTO(Integer id, String name, LocalDate birthDate) {
		this.id = id;
		this.name = name;
		this.birthDate = birthDate;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((birthDate == null) ? 0 : birthDate.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserDTO other = (UserDTO) obj;
		if (birthDate == null) {
			if (other.birthDate != null)
				return false;
		} else if (!birthDate.equals(other.birthDate))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return String.format("[ User %d - %s - %s", 
				this.id, 
				this.name, 
				this.birthDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
				);
	}
	
	public static UserDTO from(Integer id, String name, LocalDate birthDate) {
		return new UserDTO(id, name, birthDate);
	}
}
