/**
 * 
 */
package com.strandls.utility.pojo;

import java.util.List;

/**
 * @author Abhishek Rudra
 *
 */
public class HomePageData {

	private Boolean showGallery;
	private Boolean showStats;
	private Boolean showRecentObservation;
	private Boolean showGridMap;
	private Boolean showPartners;
	private HomePageStats stats;
	private List<GallerySlider> gallerySlider;
	private String ugDescription;

	/**
	 * 
	 */
	public HomePageData() {
		super();
	}

	/**
	 * @param showGallery
	 * @param showStats
	 * @param showRecentObservation
	 * @param showGridMap
	 * @param showPartners
	 * @param stats
	 * @param gallerySlider
	 * @param ugDescription
	 */
	public HomePageData(Boolean showGallery, Boolean showStats, Boolean showRecentObservation, Boolean showGridMap,
			Boolean showPartners, HomePageStats stats, List<GallerySlider> gallerySlider, String ugDescription) {
		super();
		this.showGallery = showGallery;
		this.showStats = showStats;
		this.showRecentObservation = showRecentObservation;
		this.showGridMap = showGridMap;
		this.showPartners = showPartners;
		this.stats = stats;
		this.gallerySlider = gallerySlider;
		this.ugDescription = ugDescription;
	}

	public Boolean getShowGallery() {
		return showGallery;
	}

	public void setShowGallery(Boolean showGallery) {
		this.showGallery = showGallery;
	}

	public Boolean getShowStats() {
		return showStats;
	}

	public void setShowStats(Boolean showStats) {
		this.showStats = showStats;
	}

	public Boolean getShowRecentObservation() {
		return showRecentObservation;
	}

	public void setShowRecentObservation(Boolean showRecentObservation) {
		this.showRecentObservation = showRecentObservation;
	}

	public Boolean getShowGridMap() {
		return showGridMap;
	}

	public void setShowGridMap(Boolean showGridMap) {
		this.showGridMap = showGridMap;
	}

	public Boolean getShowPartners() {
		return showPartners;
	}

	public void setShowPartners(Boolean showPartners) {
		this.showPartners = showPartners;
	}

	public HomePageStats getStats() {
		return stats;
	}

	public void setStats(HomePageStats stats) {
		this.stats = stats;
	}

	public List<GallerySlider> getGallerySlider() {
		return gallerySlider;
	}

	public void setGallerySlider(List<GallerySlider> gallerySlider) {
		this.gallerySlider = gallerySlider;
	}

	public String getUgDescription() {
		return ugDescription;
	}

	public void setUgDescription(String ugDescription) {
		this.ugDescription = ugDescription;
	}

}
