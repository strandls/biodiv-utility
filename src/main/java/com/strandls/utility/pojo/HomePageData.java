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
	private Boolean showRecentObs;
	private Boolean showGridMap;
	private Boolean showPartners;
	private HomePageStats stats;
	private List<GallerySlider> gallerySlider;

	/**
	 * 
	 */
	public HomePageData() {
		super();
	}

	/**
	 * @param showGallery
	 * @param showStats
	 * @param showRecentObs
	 * @param showGridMap
	 * @param showPartners
	 * @param stats
	 * @param gallerySlider
	 */
	public HomePageData(Boolean showGallery, Boolean showStats, Boolean showRecentObs, Boolean showGridMap,
			Boolean showPartners, HomePageStats stats, List<GallerySlider> gallerySlider) {
		super();
		this.showGallery = showGallery;
		this.showStats = showStats;
		this.showRecentObs = showRecentObs;
		this.showGridMap = showGridMap;
		this.showPartners = showPartners;
		this.stats = stats;
		this.gallerySlider = gallerySlider;
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

	public Boolean getShowRecentObs() {
		return showRecentObs;
	}

	public void setShowRecentObs(Boolean showRecentObs) {
		this.showRecentObs = showRecentObs;
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

}
