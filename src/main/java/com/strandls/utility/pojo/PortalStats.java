package com.strandls.utility.pojo;

public class PortalStats {

	private Long species;
	private Long observation;
	private Long maps;
	private Long documents;
	private Long userGroups;
	private Long discussions;
	private Long activeUser;

	/**
	 * 
	 */
	public PortalStats() {
		super();
	}

	/**
	 * @param species
	 * @param observation
	 * @param maps
	 * @param documents
	 * @param userGroups
	 * @param discussions
	 * @param activeUser
	 */
	public PortalStats(Long species, Long observation, Long maps, Long documents, Long userGroups, Long discussions,
			Long activeUser) {
		super();
		this.species = species;
		this.observation = observation;
		this.maps = maps;
		this.documents = documents;
		this.userGroups = userGroups;
		this.discussions = discussions;
		this.activeUser = activeUser;
	}

	public Long getSpecies() {
		return species;
	}

	public void setSpecies(Long species) {
		this.species = species;
	}

	public Long getObservation() {
		return observation;
	}

	public void setObservation(Long observation) {
		this.observation = observation;
	}

	public Long getMaps() {
		return maps;
	}

	public void setMaps(Long maps) {
		this.maps = maps;
	}

	public Long getDocuments() {
		return documents;
	}

	public void setDocuments(Long documents) {
		this.documents = documents;
	}

	public Long getUserGroups() {
		return userGroups;
	}

	public void setUserGroups(Long userGroups) {
		this.userGroups = userGroups;
	}

	public Long getDiscussions() {
		return discussions;
	}

	public void setDiscussions(Long discussions) {
		this.discussions = discussions;
	}

	public Long getActiveUser() {
		return activeUser;
	}

	public void setActiveUser(Long activeUser) {
		this.activeUser = activeUser;
	}

}
