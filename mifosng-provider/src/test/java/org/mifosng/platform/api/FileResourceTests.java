/**
 * 
 */
package org.mifosng.platform.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.core.Response;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifosng.platform.api.data.FileUploadResult;
import org.mockito.runners.MockitoJUnitRunner;

import com.sun.jersey.core.header.FormDataContentDisposition;

@RunWith(MockitoJUnitRunner.class)
public class FileResourceTests {
	
	@Before
	public void setUpForEachTestCase() {
		
	}

	/**
	 * @throws IOException 
	 */
	@Test
	public void testUploadFile() throws IOException {
		try {
			System.out.println(new File(".").getAbsolutePath());
			FileResource fileResource = new FileResource(".");
			File input = new File("./src/test/java/fileResourceTestUpload.txt");
			InputStream uploadedInputStream = new FileInputStream(input);
			Response response = fileResource.uploadFile(123L, 456L, uploadedInputStream, FormDataContentDisposition.name("file").fileName("userfile").build() );
			FileUploadResult result = (FileUploadResult) response.getEntity();
			Assert.assertTrue(result.isSuccess());
			File output = new File("123");
			Assert.assertTrue(output.exists());
		} finally {			
			FileUtils.deleteDirectory(new File("123"));
		}
	}
}