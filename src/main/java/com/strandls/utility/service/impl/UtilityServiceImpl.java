/**
 * 
 */
package com.strandls.utility.service.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.pac4j.core.profile.CommonProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.strandls.activity.pojo.MailData;
import com.strandls.user.controller.UserServiceApi;
import com.strandls.utility.dao.FlagDao;
import com.strandls.utility.dao.LanguageDao;
import com.strandls.utility.dao.PortalStatsDao;
import com.strandls.utility.dao.TagLinksDao;
import com.strandls.utility.dao.TagsDao;
import com.strandls.utility.pojo.Flag;
import com.strandls.utility.pojo.FlagCreateData;
import com.strandls.utility.pojo.FlagIbp;
import com.strandls.utility.pojo.FlagShow;
import com.strandls.utility.pojo.Language;
import com.strandls.utility.pojo.ParsedName;
import com.strandls.utility.pojo.PortalStats;
import com.strandls.utility.pojo.TagLinks;
import com.strandls.utility.pojo.Tags;
import com.strandls.utility.pojo.TagsMapping;
import com.strandls.utility.pojo.TagsMappingData;
import com.strandls.utility.service.UtilityService;

import net.minidev.json.JSONArray;

/**
 * @author Abhishek Rudra
 *
 */
public class UtilityServiceImpl implements UtilityService {

	private static final Logger logger = LoggerFactory.getLogger(UtilityServiceImpl.class);

	private final CloseableHttpClient httpClient = HttpClients.createDefault();

	@Inject
	private LogActivities logActivity;

	@Inject
	private UserServiceApi userService;

	@Inject
	private FlagDao flagDao;

	@Inject
	private TagLinksDao tagLinkDao;

	@Inject
	private TagsDao tagsDao;

	@Inject
	private ObjectMapper objectMapper;

	@Inject
	private LanguageDao languageDao;

	@Inject
	private PortalStatsDao portalStatusDao;

	@Override
	public Flag fetchByFlagId(Long id) {
		Flag flag = flagDao.findById(id);
		return flag;
	}

	@Override
	public FlagIbp fetchByFlagIdIbp(Long id) {
		Flag flag = flagDao.findById(id);
		if (flag == null)
			return null;
		FlagIbp ibp = new FlagIbp(flag.getFlag(), flag.getNotes());
		return ibp;
	}

	@Override
	public List<FlagShow> fetchByFlagObject(String objectType, Long objectId) {
		try {
			if (objectType.equalsIgnoreCase("observation"))
				objectType = "species.participation.Observation";
			List<Flag> flagList = flagDao.findByObjectId(objectType, objectId);
			List<FlagShow> flagShow = new ArrayList<FlagShow>();
			for (Flag flag : flagList) {
				flagShow.add(new FlagShow(flag, userService.getUserIbp(flag.getAuthorId().toString())));
			}
			return flagShow;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public List<Flag> fetchFlagByUserId(Long id) {
		List<Flag> flags = flagDao.findByUserId(id);
		return flags;
	}

	@Override
	public List<FlagShow> createFlag(String type, Long userId, Long objectId, FlagCreateData flagCreateData) {
		if (type.equalsIgnoreCase("observation"))
			type = "species.participation.Observation";

		FlagIbp flagIbp = flagCreateData.getFlagIbp();
		Flag flag = flagDao.findByObjectIdUserId(objectId, userId, type);
		if (flag == null) {
			flag = new Flag(null, 0L, userId, new Date(), flagIbp.getFlag(), flagIbp.getNotes(), objectId, type);
			flag = flagDao.save(flag);
			String description = flag.getFlag() + ":" + flag.getNotes();
			logActivity.LogActivity(description, objectId, objectId, "observaiton", flag.getId(), "Flagged",
					flagCreateData.getMailData());

			List<FlagShow> flagList = fetchByFlagObject(type, objectId);
			return flagList;

		}
		return null;

	}

	@Override
	public List<FlagShow> removeFlag(CommonProfile profile, String type, Long objectId, Long flagId,
			MailData mailData) {

		if (type.equalsIgnoreCase("observation"))
			type = "species.participation.Observation";
		Flag flagged = flagDao.findById(flagId);

		JSONArray userRole = (JSONArray) profile.getAttribute("roles");
		Long userId = Long.parseLong(profile.getId());

		if (flagged != null) {
			if (userRole.contains("ROLE_ADMIN") || userId.equals(flagged.getAuthorId())) {

				flagDao.delete(flagged);
				String description = flagged.getFlag() + ":" + flagged.getNotes();
				logActivity.LogActivity(description, objectId, objectId, "observaiton", flagged.getId(), "Flag removed",
						mailData);

				List<FlagShow> flagList = fetchByFlagObject(type, objectId);
				return flagList;
			}
		}
		return null;
	}

	@Override
	public List<Tags> fetchTags(String objectType, Long id) {
		List<TagLinks> tagList = tagLinkDao.findObjectTags(objectType, id);
		List<Tags> tags = new ArrayList<Tags>();
		for (TagLinks tag : tagList) {
			tags.add(tagsDao.findById(tag.getTagId()));
		}
		return tags;
	}

	@Override
	public List<String> createTagsMapping(String objectType, TagsMappingData tagsMappingData) {

		try {
			TagsMapping tagsMapping = tagsMappingData.getTagsMapping();
			Long objectId = tagsMapping.getObjectId();
			List<String> resultList = new ArrayList<String>();
			List<String> errorList = new ArrayList<String>();
			String description = "";
			for (Tags tag : tagsMapping.getTags()) {
				if (tag.getVersion() == null)
					tag.setVersion(0L);
				TagLinks result = null;

				Tags tagsCheck = tagsDao.fetchByName(tag.getName());
				if (tagsCheck != null)
					tag = tagsCheck;
				if (tag.getId() == null) {

					Tags insertedTag = tagsDao.save(tag);
					description = description + insertedTag.getName() + ",";
					if (insertedTag.getId() != null) {
						TagLinks tagLink = new TagLinks(null, insertedTag.getVersion(), insertedTag.getId(), objectId,
								objectType);
						result = tagLinkDao.save(tagLink);
					}
				} else {
					Tags storedTag = tagsDao.findById(tag.getId());
					if (!(storedTag.getName().equals(tag.getName()))) {
						errorList.add("Mapping not proper for TagName and id Supplied for ID" + tag.getName() + " and "
								+ tag.getId());
					} else {
						description = description + storedTag.getName() + ",";
						TagLinks tagLink = new TagLinks(null, tag.getVersion(), tag.getId(), objectId, objectType);
						result = tagLinkDao.save(tagLink);
					}

				}
				if (result.getId() != null)
					resultList.add(result.getId().toString());
			}
			if (!(errorList.isEmpty()))
				return errorList;
			description = description.substring(0, description.length() - 1);
			logActivity.LogActivity(description, objectId, objectId, "observation", objectId, "Observation tag updated",
					tagsMappingData.getMailData());
			return resultList;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;

	}

	@Override
	public ParsedName findParsedName(String scientificName) {

		URIBuilder builder = new URIBuilder();
		builder.setScheme("http").setHost("localhost:9091").setPath("/api").setParameter("q", scientificName);

		List<ParsedName> parsedName = null;

		URI uri = null;
		try {
			uri = builder.build();
			HttpGet request = new HttpGet(uri);

			try (CloseableHttpResponse response = httpClient.execute(request)) {
				System.out.println(response.getStatusLine().toString());

				HttpEntity entity = response.getEntity();
				Header headers = entity.getContentType();
				System.out.println(headers);

				if (entity != null) {
					// return it as a String
					String result = EntityUtils.toString(entity);
					parsedName = Arrays.asList(objectMapper.readValue(result, ParsedName[].class));
				}

			} catch (Exception e) {
				logger.error(e.getMessage());
			}

		} catch (URISyntaxException e1) {
			logger.error(e1.getMessage());
		}

		return parsedName.get(0);
	}

	@Override
	public List<Language> findAllLanguages(Boolean isDirty) {
		List<Language> result = null;
		if (isDirty != null)
			result = languageDao.findAll(isDirty);
		else
			result = languageDao.findAll();
		return result;
	}

	@Override
	public List<Tags> updateTags(String objectType, TagsMappingData tagsMappingData) {
		List<Tags> tags = new ArrayList<Tags>();

		try {
			String description = "";
			TagsMapping tagsMapping = tagsMappingData.getTagsMapping();
			Long objectId = tagsMapping.getObjectId();
			List<TagLinks> previousTags = tagLinkDao.findObjectTags(objectType, objectId);
			List<Tags> newTags = tagsMapping.getTags();
//			DELETE THE TAGS THAT ARE REMOVED
			for (TagLinks tagLinks : previousTags) {
				Tags tag = tagsDao.findById(tagLinks.getTagId());
				if (!(newTags.contains(tag))) {
					tagLinkDao.delete(tagLinks);
				}
			}
//			ADD OR CREATE THE NEW TAGS ADDED
			for (Tags tag : newTags) {

				if (tag.getId() != null) {
					TagLinks result = tagLinkDao.checkIfTagsLinked(objectType, objectId, tag.getId());
					if (result == null) {
						TagLinks tagLink = new TagLinks(null, tag.getVersion(), tag.getId(), objectId, objectType);
						tagLinkDao.save(tagLink);
					}
				} else {
					tag.setVersion(0L);
					tag = tagsDao.save(tag);
					TagLinks tagLink = new TagLinks(null, tag.getVersion(), tag.getId(), objectId, objectType);
					tagLinkDao.save(tagLink);
				}
				description = description + tag.getName() + ",";

			}

			List<TagLinks> presentTags = tagLinkDao.findObjectTags(objectType, objectId);
			for (TagLinks tagLinks : presentTags) {
				tags.add(tagsDao.findById(tagLinks.getTagId()));
			}

			description = description.substring(0, description.length() - 1);
			logActivity.LogActivity(description, objectId, objectId, "observation", objectId, "Observation tag updated",
					tagsMappingData.getMailData());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return tags;
	}

	@Override
	public List<Tags> tagsAutoSugguest(String phrase) {
		List<Tags> result = tagsDao.fetchNameByLike(phrase);
		return result;
	}

	@Override
	public Language getLanguageByTwoLetterCode(String language) {
		Language lang = languageDao.findByPropertyWithCondition("twoLetterCode", language, "=");
		if (lang == null) {
			return getCurrentLanguage();
		}
		return lang;
	}

	private Language getCurrentLanguage() {
		return languageDao.findByPropertyWithCondition("name", Language.DEFAULT_LANGUAGE, "=");
	}

	@Override
	public PortalStats getportalStats() {
		PortalStats result = portalStatusDao.fetchPortalStats();
		return result;
	}

}
