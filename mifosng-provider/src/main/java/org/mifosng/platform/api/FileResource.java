/**
 * 
 */
package org.mifosng.platform.api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.io.FileUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import org.mifosng.platform.api.data.FileUploadResult;
import org.mifosng.platform.api.data.ListFilesResult;

/**
 * @author sanpatel
 *
 */
@Path("/file")
@Component
public class FileResource {

    @Value("${document.root}")
	private String rootDir;

	public FileResource()
    {
    }

	public FileResource(String rootDir) {
		this.rootDir = rootDir;
	}
	
	@POST
	@Path("{mfid}/{clientId}")
	@Consumes({MediaType.MULTIPART_FORM_DATA})
	@Produces({MediaType.APPLICATION_JSON})
	public Response uploadFile(@PathParam("mfid") final Long mfid,
							   @PathParam("clientId") final Long clientId,
							   @FormDataParam("file") InputStream uploadedInputStream,
						       @FormDataParam("file") FormDataContentDisposition fileDetail)
	{	
		FileUploadResult result = null;
		try {
			
			FileUtils.copyInputStreamToFile(uploadedInputStream, new File(rootDir+File.separator+mfid+File.separator+clientId,
																			fileDetail.getFileName()));
			result = new FileUploadResult(true, fileDetail.getFileName(), mfid, clientId);
		} catch (IOException e) {
			e.printStackTrace();
			result = new FileUploadResult(false, fileDetail.getFileName(), mfid, clientId);
		}
		return Response.ok().entity(result).build();
	}
	
	@GET
	@Path("{mfid}/{clientId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public Response listFiles(@PathParam("mfid") final Long mfid,
			   @PathParam("clientId") final Long clientId) {
		File dir = new File(rootDir + File.separator + mfid + File.separator + clientId);
		String[] files = dir.list();
		ListFilesResult result = new ListFilesResult(files);
		return Response.ok().entity(result).build();
	}
	
	@GET
	@Path("{mfid}/{clientId}/{fileName}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_OCTET_STREAM})
	public Response getFile(@PathParam("mfid") final Long mfid,
			   @PathParam("clientId") final Long clientId,
			   @PathParam("fileName") final String fileName) throws IOException {
		File file = new File(rootDir + File.separator + mfid + File.separator + clientId,fileName);
		ResponseBuilder response = Response.ok((Object) file);
		response.header("Content-Disposition",
			"attachment; filename=\"" + fileName + "\"");
		return response.build();
	}
	
}
