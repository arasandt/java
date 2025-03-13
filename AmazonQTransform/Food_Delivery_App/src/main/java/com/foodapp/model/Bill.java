package com.foodapp.model;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Bill {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer billId;
	private LocalDateTime billDate;
	private Double totalCost;
	private Integer totalItem;
	
	
	@OneToOne(cascade = CascadeType.ALL)
	private OrderDetails order;
	
}
