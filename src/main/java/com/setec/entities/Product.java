package com.setec.entities;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "tbl_product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String name;
	private double price;
	private int qty;
	@JsonIgnore
	private String imageUrl;
	
	public double getAmount() {
		return price*qty;
	}
	
	@JsonProperty("fullImageUrl")
	public String getFullImageUrl() {
		// change when on Local
		return ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()+imageUrl;
		
//		if (imageUrl == null || imageUrl.isBlank()) return null;
//		return ServletUriComponentsBuilder.fromCurrentContextPath()
//				.path(imageUrl)
//				.toUriString();
	}
}
