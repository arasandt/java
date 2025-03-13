package com.foodapp.authmodels;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class LogInModel {
	
	@Id
	@NotNull
	private Integer userId;
	
	private String userName;
	private String password;

}
