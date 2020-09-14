/**
 * 
 */
package com.strandls.utility.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.pac4j.core.profile.CommonProfile;

import com.strandls.activity.pojo.MailData;
import com.strandls.utility.pojo.Flag;
import com.strandls.utility.pojo.FlagCreateData;
import com.strandls.utility.pojo.FlagIbp;
import com.strandls.utility.pojo.FlagShow;
import com.strandls.utility.pojo.GallerySlider;
import com.strandls.utility.pojo.Habitat;
import com.strandls.utility.pojo.HomePageData;
import com.strandls.utility.pojo.Language;
import com.strandls.utility.pojo.ParsedName;
import com.strandls.utility.pojo.Tags;
import com.strandls.utility.pojo.TagsMappingData;

/**
 * @author Abhishek Rudra
 *
 */
public interface UtilityService {

	public Flag fetchByFlagId(Long id);

	public FlagIbp fetchByFlagIdIbp(Long id);

	public List<FlagShow> fetchByFlagObject(String objectType, Long objectId);

	public List<Flag> fetchFlagByUserId(Long id);

	public List<FlagShow> createFlag(HttpServletRequest request, String type, Long userId, Long objectId,
			FlagCreateData flagCreateData);

	public List<FlagShow> removeFlag(HttpServletRequest request, CommonProfile profile, String type, Long objectId,
			Long flagId, MailData mailData);

	public List<Tags> fetchTags(String objectType, Long id);

	public List<String> createTagsMapping(HttpServletRequest request, String objectType,
			TagsMappingData tagsMappingData);

	public ParsedName findParsedName(String scientificName);

	public List<Language> findAllLanguages(Boolean isDirty);

	public List<Tags> updateTags(HttpServletRequest request, String objectType, TagsMappingData tagsMappingData);

	public List<Tags> tagsAutoSugguest(String phrase);

	public Language getLanguageByTwoLetterCode(String language);

	public HomePageData getHomePageData();

	public String getYoutubeTitle(String videoId);

	public List<Habitat> fetchAllHabitat();

	public Boolean insertGallery(GallerySlider gallery);

}
