package com.gonza.youtubeupldr.service;


import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.FileDataStoreFactory;

/**
 * Shared class used by every sample. Contains methods for authorizing a user and caching credentials.
 */
public class Auth {

	private static final Logger logger = LoggerFactory.getLogger(Auth.class);
    /**
     * Define a global instance of the HTTP transport.
     */
    public static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    /**
     * Define a global instance of the JSON factory.
     */
    public static final JsonFactory JSON_FACTORY = new JacksonFactory();

    /**
     * This is the directory that will be used under the user's home directory where OAuth tokens will be stored.
     */
    private static final String CREDENTIALS_DIRECTORY = ".oauth-credentials";

    /**
     * Authorizes the installed application to access user's protected data.
     *
     * @param scopes              list of scopes needed to run youtube upload.
     * @param credentialDatastore name of the credential datastore to cache OAuth tokens
     */
    public static Credential authorize(List<String> scopes, String credentialDatastore) throws IOException {
    	MyLocalServerReceiver localReceiver = new MyLocalServerReceiver();
    	GoogleAuthorizationCodeFlow flow = null;
    	try {
	        // Load client secrets.
	    	logger.info("Load client secrets");
	        Reader clientSecretReader = new InputStreamReader(Auth.class.getResourceAsStream("/client_secrets.json"));
	        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, clientSecretReader);
	
	        // This creates the credentials datastore at ~/.oauth-credentials/${credentialDatastore}
	        logger.info("This creates the credentials datastore ");
	        FileDataStoreFactory fileDataStoreFactory = new FileDataStoreFactory(new File(System.getProperty("user.home") + "/" + CREDENTIALS_DIRECTORY));
	        DataStore<StoredCredential> datastore = fileDataStoreFactory.getDataStore(credentialDatastore);
	
	        logger.info("GoogleAuthorizationCodeFlow");
	        flow = new GoogleAuthorizationCodeFlow.Builder(
	                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, scopes).setCredentialDataStore(datastore)
	                .setAccessType("offline")
	                .build();
	
	        // Build the local server and bind it to port 8080
	        logger.info("Build the local server and bind it to port 8088");
	        localReceiver = new MyLocalServerReceiver.Builder().setPort(8080).build();
	        
	        // Authorize.
	        logger.info("Authorize");
    		} finally {
		    	localReceiver.stop();
		    	return new AuthorizationCodeInstalledApp(flow, localReceiver).authorize("user");
	      }
    }
}