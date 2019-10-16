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
@Table(name = "tag_links")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TagLinks implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7576446782792031358L;
	private Long id;
	private Long version;
	private Long tagId;
	private Long tagRefer;
	private String type;

	/**
	 * @param id
	 * @param version
	 * @param tagId
	 * @param tagRefer
	 * @param type
	 */
	public TagLinks(Long id, Long version, Long tagId, Long tagRefer, String type) {
		super();
		this.id = id;
		this.version = version;
		this.tagId = tagId;
		this.tagRefer = tagRefer;
		this.type = type;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "version")
	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	@Column(name = "tag_id")
	public Long getTagId() {
		return tagId;
	}

	public void setTagId(Long tagId) {
		this.tagId = tagId;
	}

	@Column(name = "tag_ref")
	public Long getTagRefer() {
		return tagRefer;
	}

	public void setTagRefer(Long tagRefer) {
		this.tagRefer = tagRefer;
	}

	@Column(name = "type")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
