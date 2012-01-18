package com.hexhaus.icarrus.dao;

import com.hexhaus.icarrus.handler.CredentialHandler;
import com.hexhaus.icarrus.handler.MessageHandler;
import com.hexhaus.icarrus.handler.SettingsHandler;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

/**
 * User: 67726e
 * Date: 8/13/11
 * Time: 10:09 PM
 */
public class UploadDao {
    private String status;
    private String url;
    private String error;
    public UploadDao() {}

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
			post.setURI(new URI(SettingsHandler.getUploadServerURL()));

            HttpResponse response = client.execute(post);
            HttpEntity responseEntity = response.getEntity();

            // Create reader for the parser
			BufferedReader in = new BufferedReader(new InputStreamReader(responseEntity.getContent()));
            // Parse out the response from the server
			responseData = IdatDao.readIdatFromBuffer(in, "Response {");
			in.close();
        } catch (UnsupportedEncodingException e) {
            MessageHandler.postMessage("Encoding Error", "The required data could not be properly encoded.", LoggingDao.Status.Error);
        } catch (ClientProtocolException e) {
            MessageHandler.postMessage("Protocol Error", "An incorrect protocol is being used. Please check the upload URL.", LoggingDao.Status.Error);
        } catch (IOException e) {
            MessageHandler.postMessage("Upload Error", "A connection could not be established with the server", LoggingDao.Status.Error);
        } catch (URISyntaxException e) {
			MessageHandler.postMessage("URL Error", "The upload URL is invalid.", LoggingDao.Status.Error);
		}


		// Evaluate server response
        Map<String, String> responseBlock = null;
        // Retrieve the response data if it is available
		if (responseData != null && responseData.size() > 0) responseBlock = responseData.get(0);

        // Check if the response was parsed, and contains the valid status indicator
		if (responseBlock != null &&
				responseBlock.containsKey("status") && responseBlock.get("status").equals("valid")) {
			// TODO: Send upload data to HistoryTab

            // Inform the user of the valid upload
			MessageHandler.postMessage("Successful Upload",
					"Your file(s) have been uploaded successfully.", LoggingDao.Status.Info);
		} else {
			System.out.println("ERROR");
			// TODO: Form proper error message
		}
    }
}
