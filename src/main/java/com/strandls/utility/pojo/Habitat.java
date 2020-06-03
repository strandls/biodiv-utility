/**
 * 
 */
package com.strandls.utility.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Abhishek Rudra
 *
 */

@Entity
@Table(name = "habitat")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Habitat implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -175751465352099324L;
	private Long id;
	private Integer habitatOrder;
	private String name;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "habitat_order")
	public Integer getHabitatOrder() {
		return habitatOrder;
	}

	public void setHabitatOrder(Integer habitatOrder) {
		this.habitatOrder = habitatOrder;
	}

	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
