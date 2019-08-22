/**
 * 
 */
package com.strandls.utility.controller;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.inject.Inject;
import com.strandls.utility.ApiConstants;
import com.strandls.utility.pojo.Flag;
import com.strandls.utility.pojo.FlagIbp;
import com.strandls.utility.pojo.Follow;
import com.strandls.utility.service.UtilityService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

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
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success", response = Flag.class),
			@ApiResponse(code = 404, message = "Flag not found", response = String.class) })
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
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success", response = FlagIbp.class),
			@ApiResponse(code = 404, message = "Flag not found", response = String.class) })

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

	@ApiOperation(value = "Find flag by Observation Id", notes = "Return of Flags", response = Flag.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success", response = Flag.class),
			@ApiResponse(code = 400, message = "Flag not found", response = String.class) })

	public Response getFlagByObservation(@PathParam("objectType") String objectType,
			@PathParam("objectId") String objectId) {

		try {
			Long id = Long.parseLong(objectId);
			Flag flag = utilityService.fetchByFlagObject(objectType, id);
			return Response.status(Status.OK).entity(flag).build();

		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).build();
		}

	}

	@GET
	@Path(ApiConstants.USERFLAG + "/{userId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "Find flag by userId", notes = "Returns List of Flag for a User", response = Flag.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Success", response = Flag.class, responseContainer = "List"),
			@ApiResponse(code = 400, message = "Flag not Found", response = String.class) })

	public Response getFlagByUserId(@PathParam("userId") String userId) {
		try {
			Long id = Long.parseLong(userId);
			List<Flag> flags = utilityService.fetchFlagByUserId(id);
			return Response.status(Status.OK).entity(flags).build();

		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

	@GET
	@Path(ApiConstants.FOLLOW + "/{followId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "Find follow by followid", notes = "Return follows", response = Follow.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success", response = Follow.class),
			@ApiResponse(code = 400, message = "Follow not Found", response = String.class) })

	public Response getByFollowID(@PathParam("followId") String followId) {

		try {
			Long id = Long.parseLong(followId);
			Follow follow = utilityService.fetchByFollowId(id);
			return Response.status(Status.OK).entity(follow).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

	@GET
	@Path(ApiConstants.OBJECTFOLLOW + "/{objectType}/{objectId}/{authorId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "Find follow by objectId", notes = "Return follows", response = Follow.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success", response = Follow.class),
			@ApiResponse(code = 400, message = "Follow not Found", response = String.class) })

	public Response getByObject(@PathParam("objectType") String objectType, @PathParam("objectId") String objectId,
			@PathParam("authorId") String authorId) {
		try {
			Long objId = Long.parseLong(objectId);
			Long authId = Long.parseLong(authorId);
			Follow follow = utilityService.fetchByFollowObject(objectType, objId, authId);
			return Response.status(Status.OK).entity(follow).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

	@GET
	@Path(ApiConstants.USERFOLLOW + "/{userId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "Find follow by userID", notes = "Return list follows", response = Follow.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Success", response = Follow.class, responseContainer = "List"),
			@ApiResponse(code = 400, message = "Follow not Found", response = String.class) })

	public Response getFollowbyUser(@PathParam("userId") String userId) {

		try {
			Long id = Long.parseLong(userId);
			List<Follow> follows = utilityService.fetchFollowByUser(id);
			return Response.status(Status.OK).entity(follows).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).build();
		}

	}

}
