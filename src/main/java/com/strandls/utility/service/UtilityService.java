/**
 * 
 */
package com.strandls.utility.service;

import java.util.List;

import com.strandls.utility.pojo.Flag;
import com.strandls.utility.pojo.FlagIbp;
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

	public List<Flag> fetchByFlagObject(String objectType, Long objectId);

	public List<Flag> fetchFlagByUserId(Long id);

	public List<Flag> createFlag(String type, Long userId, Long objectId, FlagIbp flagIbp);

	public List<Flag> removeFlag(String type, Long userId, Long objectId);

	

	public List<Tags> fetchTags(String objectType, Long id);

	public List<String> createTagsMapping(String objectType, TagsMapping tagsMapping);

	public ParsedName findParsedName(String scientificName);

	public List<Language> findAllLanguages(Boolean isDirty);

	public List<Tags> updateTags(String objectType, TagsMapping tagsMapping);

	public List<Tags> tagsAutoSugguest(String phrase);

}
