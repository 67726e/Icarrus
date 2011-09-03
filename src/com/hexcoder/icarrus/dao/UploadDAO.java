package com.hexcoder.icarrus.dao;

import com.hexcoder.icarrus.dto.CredentialHandler;
import com.hexcoder.icarrus.dto.MessageHandler;
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
import java.util.Map;

/**
 * User: 67726e
 * Date: 8/13/11
 * Time: 10:09 PM
 */
public class UploadDAO {
    private String status;
    private String token;
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
        token = "";
        url = "";
        error = "";

        try {
            FileBody binary = new FileBody(file);
            StringBody username = new StringBody(CredentialHandler.getUsername());
            StringBody password = new StringBody(CredentialHandler.getPassword());
            StringBody token = new StringBody(CredentialHandler.getToken());

            MultipartEntity request = new MultipartEntity();
            request.addPart("file", binary);
            request.addPart("username", username);
            request.addPart("token", token);
            request.addPart("password", password);

            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost();
            post.setEntity(request);

            HttpResponse response = client.execute(post);
            HttpEntity responseEntity = response.getEntity();

            parseResponse(responseEntity);                                                                              // Parse the response of the Icarrus server
        } catch (UnsupportedEncodingException e) {
            MessageHandler.postMessage("Encoding Error", "The required data could not be properly encoded.", LoggingDAO.Status.ERROR);
        } catch (ClientProtocolException e) {
            MessageHandler.postMessage("Protocol Error", "An incorrect protocol is being used. Please check the upload URL.", LoggingDAO.Status.ERROR);
        } catch (IOException e) {
            MessageHandler.postMessage("Upload Error", "A connection could not be established with the server", LoggingDAO.Status.ERROR);
        }

        // TODO: Send upload information to HistoryDAO
        if (status.equals("valid")) {
            MessageHandler.postMessage("Upload Successful", "Your file(s) have uploaded successfully.", LoggingDAO.Status.INFORMATION);
            new ClipboardDAO().copyURLToClipboard(url);                                                                 // Copy the URL of the uploaded file to the clipboard (if allowed)
        } else {
            MessageHandler.postMessage("Upload Error",
                    (error == null) ? "The file(s) could not be uploaded to the server." : error,
                    LoggingDAO.Status.ERROR);
        }
    }

    /**
     * Method receives an HttpResponse from the uploaded file. Takes the returned data and attempts to parse it into the
     * key/value pairs to be used for data dissemination throughout the application.
     *
     * @param response the HTTP response given as a result of a file upload
     */
    private void parseResponse(HttpEntity response) {
        try {
            HashMap<String, String> parameters = new HashMap<String, String>();
            BufferedReader in = new BufferedReader(new InputStreamReader(response.getContent()));
            String line = "";

            try {
                while ((line = in.readLine()) != null) {
                    // TODO: Log warning for invalid parameters
                    // TODO: write utility function to combine this with the LoginDAO parsing methods

                    if (line.length() < 4) continue;                                                                    // If the String is less than 4 characters (the minimum for a parameter line) then we can skip this line as unrecognized
                    if (line.charAt(0) != '~') continue;                                                                // Skip this line if it does not start with the designated parameter marker '~'
                    String[] arguments = line.split(":");                                                               // Split the line along the delimiter
                    if (arguments.length > 2) continue;                                                                 // We have illegal parameters

                    parameters.put(arguments[0], arguments[1]);                                                         // Place the parameter/value in the HashMap as it has passed all applicable checks
                }
            } finally { in.close(); }
        } catch (IOException e) {
            MessageHandler.postMessage("Upload Response Error", "The response from the Icarrus server could not be read.", LoggingDAO.Status.ERROR);
        }
    }

    private void retrieveValues(HashMap<String, String> parameters) {
        Iterator iterator = parameters.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry)iterator.next();
            String key = (String)pair.getKey();
            String value = (String)pair.getValue();

            if (key.equals("~status") && value.equals("valid")) status = value;
            else if (key.equals("~token")) token = value;
            else if (key.equals("~url")) url = value;
            else if (key.equals("~error")) error = value;
        }
    }
}