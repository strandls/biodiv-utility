/**
 * 
 */
package com.strandls.utility.service;

import java.util.List;

import org.pac4j.core.profile.CommonProfile;

import com.strandls.utility.pojo.Flag;
import com.strandls.utility.pojo.FlagIbp;
import com.strandls.utility.pojo.FlagShow;
import com.strandls.utility.pojo.Language;
import com.strandls.utility.pojo.ParsedName;
import com.strandls.utility.pojo.Tags;
import com.strandls.utility.pojo.TagsMapping;

/**
 * @author Abhishek Rudra
 *
 */
public interface UtilityService {

	public Flag fetchByFlagId(Long id);

	public FlagIbp fetchByFlagIdIbp(Long id);

	public List<FlagShow> fetchByFlagObject(String objectType, Long objectId);

	public List<Flag> fetchFlagByUserId(Long id);

	public List<FlagShow> createFlag(String type, Long userId, Long objectId, FlagIbp flagIbp);

	public List<FlagShow> removeFlag(CommonProfile profile, String type, Long objectId, Long flagId);

	public List<Tags> fetchTags(String objectType, Long id);

	public List<String> createTagsMapping(String objectType, TagsMapping tagsMapping);

	public ParsedName findParsedName(String scientificName);

	public List<Language> findAllLanguages(Boolean isDirty);

	public List<Tags> updateTags(String objectType, TagsMapping tagsMapping);

	public List<Tags> tagsAutoSugguest(String phrase);

	public Language getLanguageByTwoLetterCode(String language);

}
