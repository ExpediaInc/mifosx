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
import org.apache.commons.io.FilenameUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import org.mifosng.platform.api.data.FileUploadResult;
import org.mifosng.platform.api.data.ListFilesResult;

/**
 * @author sanpatel
 */
@Path("/file")
@Component
public class FileResource
{

    @Value("${document.root}")
    private String rootDir;

    public FileResource()
    {
    }

    public FileResource(String rootDir)
    {
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
        try
        {
            String fileName = createPath(rootDir,
                                         String.valueOf(mfid), String.valueOf(clientId), fileDetail.getFileName());
            FileUtils.copyInputStreamToFile(uploadedInputStream, new File(fileName));
            result = new FileUploadResult(true, fileDetail.getFileName(), mfid, clientId);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            result = new FileUploadResult(false, fileDetail.getFileName(), mfid, clientId);
        }
        return Response.ok().entity(result).build();
    }

    private String createPath(String rootDir, String... segments)
    {
        String path = rootDir;
        if (segments != null)
        {
            for (String segment : segments)
            {
                path = FilenameUtils.concat(path, segment);
            }
        }
        return path;
    }

    @GET
    @Path("{mfid}/{clientId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response listFiles(@PathParam("mfid") final Long mfid,
                              @PathParam("clientId") final Long clientId)
    {
        String fileName = createPath(rootDir,
                                     String.valueOf(mfid), String.valueOf(clientId));
        File dir = new File(fileName);
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
                            @PathParam("fileName") final String fileName) throws IOException
    {
        String fullPathName = createPath(rootDir,
                                     String.valueOf(mfid), String.valueOf(clientId), fileName);
        File file = new File(fullPathName);
        ResponseBuilder response = Response.ok(file);
        response.header("Content-Disposition",
                        "attachment; filename=\"" + fileName + "\"");
        return response.build();
    }

}
