/**
 * 
 */
package org.mifosng.platform.api.data;

/**
 * @author sanpatel
 *
 */
public class ListFilesResult {
	private String[] files;
	public ListFilesResult(String[] files) {
		this.files = files;
	}
	public String[] getFiles() {
		return files;
	}

	public void setFiles(String[] files) {
		this.files = files;
	}
}
