/**
 * 
 */
package com.strandls.utility.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.pac4j.core.profile.CommonProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.strandls.activity.pojo.MailData;
import com.strandls.resource.controllers.ResourceServicesApi;
import com.strandls.resource.pojo.ObservationResourceUser;
import com.strandls.user.controller.UserServiceApi;
import com.strandls.userGroup.controller.UserGroupSerivceApi;
import com.strandls.userGroup.pojo.UserGroupHomePage;
import com.strandls.utility.dao.FlagDao;
import com.strandls.utility.dao.GallerySliderDao;
import com.strandls.utility.dao.HabitatDao;
import com.strandls.utility.dao.HomePageStatsDao;
import com.strandls.utility.dao.LanguageDao;
import com.strandls.utility.dao.TagLinksDao;
import com.strandls.utility.dao.TagsDao;
import com.strandls.utility.pojo.Flag;
import com.strandls.utility.pojo.FlagCreateData;
import com.strandls.utility.pojo.FlagIbp;
import com.strandls.utility.pojo.FlagShow;
import com.strandls.utility.pojo.GallerySlider;
import com.strandls.utility.pojo.Habitat;
import com.strandls.utility.pojo.HomePageData;
import com.strandls.utility.pojo.HomePageStats;
import com.strandls.utility.pojo.Language;
import com.strandls.utility.pojo.ParsedName;
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
	private HomePageStatsDao portalStatusDao;

	@Inject
	private UserGroupSerivceApi ugService;

	@Inject
	private GallerySliderDao gallerSilderDao;

	@Inject
	private HabitatDao habitatDao;

	@Inject
	private ResourceServicesApi resourceService;

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
	public List<FlagShow> createFlag(HttpServletRequest request, String type, Long userId, Long objectId,
			FlagCreateData flagCreateData) {
		if (type.equalsIgnoreCase("observation"))
			type = "species.participation.Observation";

		FlagIbp flagIbp = flagCreateData.getFlagIbp();
		Flag flag = flagDao.findByObjectIdUserId(objectId, userId, type);
		if (flag == null) {
			flag = new Flag(null, 0L, userId, new Date(), flagIbp.getFlag(), flagIbp.getNotes(), objectId, type);
			flag = flagDao.save(flag);
			String description = flag.getFlag() + ":" + flag.getNotes();
			logActivity.LogActivity(request.getHeader(HttpHeaders.AUTHORIZATION), description, objectId, objectId,
					"observaiton", flag.getId(), "Flagged", flagCreateData.getMailData());

			List<FlagShow> flagList = fetchByFlagObject(type, objectId);
			return flagList;

		}
		return null;

	}

	@Override
	public List<FlagShow> removeFlag(HttpServletRequest request, CommonProfile profile, String type, Long objectId,
			Long flagId, MailData mailData) {

		if (type.equalsIgnoreCase("observation"))
			type = "species.participation.Observation";
		Flag flagged = flagDao.findById(flagId);

		JSONArray userRole = (JSONArray) profile.getAttribute("roles");
		Long userId = Long.parseLong(profile.getId());

		if (flagged != null) {
			if (userRole.contains("ROLE_ADMIN") || userId.equals(flagged.getAuthorId())) {

				flagDao.delete(flagged);
				String description = flagged.getFlag() + ":" + flagged.getNotes();
				logActivity.LogActivity(request.getHeader(HttpHeaders.AUTHORIZATION), description, objectId, objectId,
						"observaiton", flagged.getId(), "Flag removed", mailData);

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
	public List<String> createTagsMapping(HttpServletRequest request, String objectType,
			TagsMappingData tagsMappingData) {

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
			logActivity.LogActivity(request.getHeader(HttpHeaders.AUTHORIZATION), description, objectId, objectId,
					"observation", objectId, "Observation tag updated", tagsMappingData.getMailData());
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
	public List<Tags> updateTags(HttpServletRequest request, String objectType, TagsMappingData tagsMappingData) {
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
			logActivity.LogActivity(request.getHeader(HttpHeaders.AUTHORIZATION), description, objectId, objectId,
					"observation", objectId, "Observation tag updated", tagsMappingData.getMailData());
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
	public HomePageData getHomePageData(Long userGroupId) {
		try {

			HomePageData result = null;
			List<GallerySlider> galleryData = gallerSilderDao.getAllGallerySliderInfo(userGroupId);

			for (GallerySlider gallery : galleryData) {
				gallery.setAuthorImage(userService.getUserIbp(gallery.getAuthorId().toString()).getProfilePic());
			}

			HomePageStats homePageStats;
			if (userGroupId == null) {
//				IBP home page DATA
				homePageStats = portalStatusDao.fetchPortalStats();
				result = new HomePageData(true, true, true, true, true, homePageStats, galleryData);
			} else {
//				Group Home Page
				UserGroupHomePage ugHomePageData = ugService.getUserGroupHomePageData(userGroupId.toString());
				homePageStats = new HomePageStats(ugHomePageData.getStats().getSpecies(),
						ugHomePageData.getStats().getObservation(), ugHomePageData.getStats().getMaps(),
						ugHomePageData.getStats().getDocuments(), ugHomePageData.getStats().getDiscussions(),
						ugHomePageData.getStats().getActiveUser());
				result = new HomePageData(ugHomePageData.getShowGallery(), ugHomePageData.getShowStats(),
						ugHomePageData.getShowRecentObservation(), ugHomePageData.getShowGridMap(),
						ugHomePageData.getShowPartners(), homePageStats, galleryData);
			}

			return result;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;

	}

	@Override
	public String getYoutubeTitle(String videoId) {

		URIBuilder builder = new URIBuilder();
		builder.setScheme("https").setHost("www.youtube.com").setPath("/oembed").setParameter("url",
				"https://www.youtube.com/watch?v=" + videoId);

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
					String result = EntityUtils.toString(entity);
					Map<String, Object> resultMap = objectMapper.readValue(result,
							new TypeReference<Map<String, Object>>() {
							});

					String title = resultMap.get("title").toString();
					return title;
				}

			} catch (Exception e) {
				logger.error(e.getMessage());
			}

		} catch (URISyntaxException e1) {
			logger.error(e1.getMessage());
		}

		return null;
	}

	@Override
	public List<Habitat> fetchAllHabitat() {
		List<Habitat> result = habitatDao.findAllHabitat();
		return result;
	}

	@Override
	public Boolean insertGallery(GallerySlider gallery) {
		try {
			gallery = gallerSilderDao.save(gallery);
			if (gallery.getId() != null)
				return true;

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return false;
	}

	@Override
	public void readeCSV() {
		try {
			String file = "/Users/abhishek/Downloads/user_group-csv.xlsx";

			File excel = new File(file);
			FileInputStream fis = new FileInputStream(excel);
			XSSFWorkbook book = new XSSFWorkbook(fis);
			XSSFSheet sheet = book.getSheetAt(0);

			int first = sheet.getFirstRowNum();
			int last = sheet.getLastRowNum();

			for (int i = first + 1; i < last; i++) {
				Row row = sheet.getRow(i);
				Long ugId = null;
				String customDescripition = "";
				String moreLinks = "";

				Cell cdCell = row.getCell(4, Row.RETURN_BLANK_AS_NULL);
				if (cdCell != null)
					customDescripition = cdCell.getStringCellValue();

				Cell moreLinkCell = row.getCell(5, Row.RETURN_BLANK_AS_NULL);
				if (moreLinkCell != null)
					moreLinks = moreLinkCell.getStringCellValue();

				Cell idCell = row.getCell(0, Row.RETURN_BLANK_AS_NULL);
				if (idCell != null) {
					ugId = Long.parseLong(idCell.getStringCellValue());
					if (customDescripition.length() == 0) {
						customDescripition = gallerSilderDao.getDesc(ugId);
						customDescripition = customDescripition.replace("&nbsp;", " ");
					}
				}
				Long observationCell = (long) row.getCell(6).getNumericCellValue();

				String fileName = "";
				Map<String, Long> observation = gallerSilderDao.getObservation(observationCell);
				if (observation == null || observation.isEmpty())
					continue;
				List<ObservationResourceUser> resourceList = resourceService
						.getImageResource(observationCell.toString());
				for (ObservationResourceUser resource : resourceList) {
					if (observation.get("reprImage").equals(resource.getResource().getId()))
						fileName = resource.getResource().getFileName();
				}
				System.out.println("UGID :" + ugId + "       observation: " + observationCell);
				String title = row.getCell(3).getStringCellValue();
				GallerySlider gallerySlider = new GallerySlider(null, ugId, fileName, observationCell,
						observation.get("authorId"), null, title, customDescripition, moreLinks);
				gallerSilderDao.save(gallerySlider);
			}

			book.close();
			System.out.println("COMPLETED");

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {

		}

	}

}
