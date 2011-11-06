package com.hexhaus.icarrus.dao;

import com.hexhaus.icarrus.handler.CredentialHandler;
import com.hexhaus.icarrus.handler.MessageHandler;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.HttpClient;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * User: 67726e
 * Date: 8/13/11
 * Time: 10:09 PM
 */
public class UploadDAO {
    private String status;
    private String url;
    private String error;
    public UploadDAO() {}

    /**
     * Method called by the DropForm when a file is dragged and dropped onto the form. This method initiates an upload
     * to the specified Icarrus server by the uploadServer setting.
     *
     * @param file the file to be uploaded that was dropped on the DropForm
     */
    public void uploadFile(File file) {
        status = "";
        url = "";
        error = "";
		List<Map<String, String>> responseData = null;

        try {
            FileBody binary = new FileBody(file);
            StringBody username = new StringBody(CredentialHandler.getUsername());
            StringBody password = new StringBody(CredentialHandler.getPassword());

            MultipartEntity request = new MultipartEntity();
            request.addPart("file", binary);
            request.addPart("username", username);
            request.addPart("password", password);

            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost();
            post.setEntity(request);

            HttpResponse response = client.execute(post);
            HttpEntity responseEntity = response.getEntity();

			BufferedReader in = new BufferedReader(new InputStreamReader(responseEntity.getContent()));					// Create reader for the parser
			responseData = IdatDAO.readIdatFromBuffer(in, "Reponse {");													// Parse out the response from the server
			in.close();
        } catch (UnsupportedEncodingException e) {
            MessageHandler.postMessage("Encoding Error", "The required data could not be properly encoded.", LoggingDAO.Status.Error);
        } catch (ClientProtocolException e) {
            MessageHandler.postMessage("Protocol Error", "An incorrect protocol is being used. Please check the upload URL.", LoggingDAO.Status.Error);
        } catch (IOException e) {
            MessageHandler.postMessage("Upload Error", "A connection could not be established with the server", LoggingDAO.Status.Error);
        }

        // TODO: Send upload information to HistoryDAO
        Map<String, String> responseBlock = null;
		if (responseData != null && responseData.size() > 0) responseBlock = responseData.get(0);						// Retrieve the response data if it is available
		
		if (responseBlock != null && responseBlock.containsKey("status") &&
				responseBlock.get("status").equals("valid")) {															// Check if there was a parsed response and it returned valid

            MessageHandler.postMessage("Upload Successful",
					"Your file(s) have uploaded successfully.", LoggingDAO.Status.Info);
            new ClipboardDAO().copyURLToClipboard((responseBlock.get("url") == null) ? "" : responseBlock.get("url"));	// Copy the URL of the uploaded file to the clipboard (if allowed)
        } else {
            MessageHandler.postMessage("Upload Error",
                    (responseBlock.get("error") == null) ?
							"The file(s) could not be uploaded to the server." : responseBlock.get("error"),
                    LoggingDAO.Status.Error);																			// Output the error if there was an invalid upload or response
        }
    }
}
