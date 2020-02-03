/**
 * 
 */
package com.strandls.utility.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.pac4j.core.profile.CommonProfile;

import com.google.inject.Inject;
import com.strandls.authentication_utility.filter.ValidateUser;
import com.strandls.authentication_utility.util.AuthUtil;
import com.strandls.utility.ApiConstants;
import com.strandls.utility.pojo.Flag;
import com.strandls.utility.pojo.FlagIbp;
import com.strandls.utility.pojo.Language;
import com.strandls.utility.pojo.ParsedName;
import com.strandls.utility.pojo.Tags;
import com.strandls.utility.pojo.TagsMapping;
import com.strandls.utility.service.UtilityService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import net.minidev.json.JSONArray;

/**
 * @author Abhishek Rudra
 *
 */

@Api("Utility Service")
@Path(ApiConstants.V1 + ApiConstants.SERVICES)
public class UtilityController {

	@Inject
	private UtilityService utilityService;

	@GET
	@Path(ApiConstants.FLAG + "/{flagId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "Find Flag by Flag ID", notes = "Returns Flag details", response = Flag.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Flag not found", response = String.class) })
	public Response getFlags(@PathParam("flagId") String flagId) {

		try {
			Long id = Long.parseLong(flagId);
			Flag flag = utilityService.fetchByFlagId(id);

			return Response.status(Status.OK).entity(flag).build();

		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

	@GET
	@Path(ApiConstants.FLAG + ApiConstants.IBP + "/{flagId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "Find Flag by Flag ID for IBP", notes = "Returns Flag details for IBP", response = FlagIbp.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Flag not found", response = String.class) })

	public Response getFlagsIbp(@PathParam("flagId") String flagId) {
		try {
			Long id = Long.parseLong(flagId);
			FlagIbp ibp = utilityService.fetchByFlagIdIbp(id);
			return Response.status(Status.OK).entity(ibp).build();

		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

	@GET
	@Path(ApiConstants.OBJECTFLAG + "/{objectType}/{objectId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "Find flag by Observation Id", notes = "Return of Flags", response = Flag.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Flag not found", response = String.class) })

	public Response getFlagByObjectType(@PathParam("objectType") String objectType,
			@PathParam("objectId") String objectId) {

		try {
			Long id = Long.parseLong(objectId);
			List<Flag> flag = utilityService.fetchByFlagObject(objectType, id);
			return Response.status(Status.OK).entity(flag).build();

		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).build();
		}

	}

	@GET
	@Path(ApiConstants.USERFLAG)
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser
	@ApiOperation(value = "Find flag by userId", notes = "Returns List of Flag for a User", response = Flag.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Flag not Found", response = String.class) })

	public Response getFlagByUserId(@Context HttpServletRequest request) {
		try {

			CommonProfile profile = AuthUtil.getProfileFromRequest(request);
			Long id = Long.parseLong(profile.getId());
			List<Flag> flags = utilityService.fetchFlagByUserId(id);
			return Response.status(Status.OK).entity(flags).build();

		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

	@POST
	@Path(ApiConstants.CREATE + ApiConstants.FLAG + "/{type}/{objectId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateUser

	@ApiOperation(value = "Flag a Object", notes = "Return a list of flag to the Object", response = Flag.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Unable to flag a object", response = String.class),
			@ApiResponse(code = 406, message = "User has already flagged", response = String.class) })

	public Response createFlag(@Context HttpServletRequest request, @PathParam("type") String type,
			@PathParam("objectId") String objectId, @ApiParam(name = "flagIbp") FlagIbp flagIbp) {
		try {
			CommonProfile Profile = AuthUtil.getProfileFromRequest(request);
			Long userId = Long.parseLong(Profile.getId());
			Long objId = Long.parseLong(objectId);
			List<Flag> result = utilityService.createFlag(type, userId, objId, flagIbp);
			if (result.isEmpty())
				return Response.status(Status.NOT_ACCEPTABLE).entity("User Allowed Flagged").build();
			return Response.status(Status.OK).entity(result).build();

		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@PUT
	@Path(ApiConstants.UNFLAG + "/{objectType}/{objectId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateUser

	@ApiOperation(value = "Unflag a Object", notes = "Return a list of flag to the Object", response = Flag.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Unable to unflag a object", response = String.class),
			@ApiResponse(code = 406, message = "User is not allowed to unflag", response = String.class) })

	public Response unFlag(@Context HttpServletRequest request, @PathParam("objectType") String objectType,
			@PathParam("objectId") String objectId, @ApiParam(name = "flag") Flag flag) {
		try {
			CommonProfile profile = AuthUtil.getProfileFromRequest(request);
			JSONArray userRole = (JSONArray) profile.getAttribute("roles");
			Long userId = Long.parseLong(profile.getId());
			List<Flag> result = null;
			if (userRole.contains("ROLE_ADMIN") || userId.equals(flag.getAuthorId())) {
				Long objId = Long.parseLong(objectId);
				result = utilityService.removeFlag(objectType, objId, flag);
			}

			if (result == null)
				return Response.status(Status.NOT_ACCEPTABLE).entity("User not allowed to Unflag").build();
			return Response.status(Status.OK).entity(result).build();

		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path(ApiConstants.TAGS + ApiConstants.AUTOCOMPLETE)
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "Find the Sugguestion for tags", notes = "Return list of Top 10 tags matching the phrase", response = Tags.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Unable to fetch the tags", response = String.class) })

	public Response getTagsAutoComplete(@QueryParam("phrase") String phrase) {
		try {
			List<Tags> result = utilityService.tagsAutoSugguest(phrase);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path(ApiConstants.TAGS + "/{objectType}/{objectId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "Find tags", notes = "Return list tags", response = Tags.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Tags not Found", response = String.class) })

	public Response getTags(@PathParam("objectType") String objectType, @PathParam("objectId") String objectId) {
		try {
			Long id = Long.parseLong(objectId);
			List<Tags> tags = utilityService.fetchTags(objectType, id);
			return Response.status(Status.OK).entity(tags).build();

		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

	@POST
	@Path(ApiConstants.TAGS + "/{objectType}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "Create Tags", notes = "Return the id of Tags Links created", response = String.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 409, message = "Error occured in transaction", response = String.class),
			@ApiResponse(code = 400, message = "DB not Found", response = String.class),
			@ApiResponse(code = 206, message = "partial succes ", response = String.class) })

	public Response createTags(@Context HttpServletRequest request, @PathParam("objectType") String objectType,
			@ApiParam(name = "tagsMapping") TagsMapping tagsMapping) {
		try {
			List<String> result = utilityService.createTagsMapping(objectType, tagsMapping);
			if (result == null)
				return Response.status(Status.CONFLICT).entity("Error occured in transaction").build();
			else {
				if (result.get(0).startsWith("Mapping not proper for TagName and id Supplied for ID"))
					return Response.status(206).entity(result).build(); // PARTIAL CONTENT 206
				return Response.status(Status.CREATED).entity(result).build();

			}
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@PUT
	@Path(ApiConstants.TAGS + "/{objectType}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser
	@ApiOperation(value = "Update the tags", notes = "Returns all the current tags", response = Tags.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Unable to edit", response = String.class) })

	public Response updateTags(@Context HttpServletRequest request, @PathParam("objectType") String objectType,
			@ApiParam(name = "tagsMapping") TagsMapping tagsMapping) {
		try {
			List<Tags> result = utilityService.updateTags(objectType, tagsMapping);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path(ApiConstants.NAMEPARSER + "/{scientificName}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "Find the Canonical Form of a Scientific Name", notes = "Returns the Canonical Name of a Scientific Name", response = ParsedName.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Canonical Name not Found", response = String.class) })

	public Response getNameParsed(@PathParam("scientificName") String name) {

		try {
			ParsedName result = utilityService.findParsedName(name);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).build();
		}

	}

	@GET
	@Path(ApiConstants.LANGUAGES)
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "Find all the Languages based on IsDirty field", notes = "Returns all the Languages Details", response = Language.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Languages Not Found", response = String.class) })

	public Response getAllLanguages(@QueryParam("isDirty") Boolean isDirty) {
		try {
			List<Language> result = utilityService.findAllLanguages(isDirty);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

}
