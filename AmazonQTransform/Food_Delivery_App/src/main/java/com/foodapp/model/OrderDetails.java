package com.foodapp.model;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class OrderDetails {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer orderId;
	private LocalDateTime orderDate;
	private String orderStatus;
	
	
	@OneToOne(cascade = CascadeType.ALL)
	@JsonIgnore
	private FoodCart cart;
	
	
	@OneToOne(cascade = CascadeType.ALL)
	@JsonIgnore
	private  Bill bill;
}
