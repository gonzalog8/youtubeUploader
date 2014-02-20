package com.gonza.youtubeupldr.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;
import com.google.common.collect.Lists;

/**
 * Upload a video to the authenticated user's channel. Use OAuth 2.0 to
 * authorize the request. Note that you must add your video files to the project
 * folder to upload them with this application.
 * 
 * @author Jeremy Walker
 */
@Service
public class UploadVideo {

	private static final Logger logger = LoggerFactory.getLogger(UploadVideo.class);
	/**
	 * Define a global instance of a Youtube object, which will be used to make
	 * YouTube Data API requests.
	 */
	private static YouTube youtube;

	/**
	 * Define a global variable that specifies the MIME type of the video being
	 * uploaded.
	 */
	private static final String VIDEO_FILE_FORMAT = "video/*";

	private static final String SAMPLE_VIDEO_FILENAME = "small.mp4";

	/**
	 * Upload the user-selected video to the user's YouTube channel. The code
	 * looks for the video in the application's project folder and uses OAuth
	 * 2.0 to authorize the API request.
	 * 
	 * @param args
	 *            command line args (not used).
	 */
	public static String init(String completeFileName, String accessToken, String refreshToken) {

		// This OAuth 2.0 access scope allows an application to upload files
		// to the authenticated user's YouTube channel, but doesn't allow
		// other types of access.
		logger.info("Scopes Arraylist");
		List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube.upload");

		try {
			Credential credential;
			// Authorize the request.
			logger.info("Authorize the request.");
			
			if(accessToken == null){
				logger.info("Authentincating via client_secrets");
				credential = Auth.authorize(scopes, "uploadvideo");
			}else{
				logger.info("Authenticating via token: " + accessToken);
				credential = new GoogleCredential().setAccessToken(accessToken);
			}
			
			// This object is used to make YouTube Data API requests.
			logger.info("Create object used to make YouTube Data API requests.");
			youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
					.setApplicationName("VideoReview")
					.setHttpRequestInitializer(credential)
					.build();

			logger.info("Uploading: " + SAMPLE_VIDEO_FILENAME);

			// Add extra information to the video before uploading.
			Video videoObjectDefiningMetadata = new Video();

			// Set the video to be publicly visible. This is the default
			// setting. Other supporting settings are "unlisted" and "private."
			VideoStatus status = new VideoStatus();
			status.setPrivacyStatus("public");
			videoObjectDefiningMetadata.setStatus(status);

			// Most of the video's metadata is set on the VideoSnippet object.
			VideoSnippet snippet = new VideoSnippet();

			// This code uses a Calendar instance to create a unique name and
			// description for test purposes so that you can easily upload
			// multiple files. You should remove this code from your project
			// and use your own standard names instead.
			Calendar cal = Calendar.getInstance();
			snippet.setTitle("Test Upload via Java on " + cal.getTime());
			snippet.setDescription("Video uploaded via YouTube Data API V3 using the Java library on " + cal.getTime());

			// Set the keyword tags that you want to associate with the video.
			List<String> tags = new ArrayList<String>();
			tags.add("test");
			tags.add("example");
			tags.add("java");
			tags.add("YouTube Data API V3");
			tags.add("erase me");
			snippet.setTags(tags);

			// Add the completed snippet object to the video resource.
			logger.info("Add the completed snippet object to the video resource.");
			videoObjectDefiningMetadata.setSnippet(snippet);

			InputStream is = null;
			if(completeFileName != null && completeFileName.length() > 0){
				logger.info("Attempting to load video from: " + completeFileName);
				is = new FileInputStream(completeFileName); 
			}
			 
			if(is == null){
				logger.error("reading file from " + completeFileName + " FAILED, using resouces one");
				is = UploadVideo.class.getResourceAsStream("/" + SAMPLE_VIDEO_FILENAME);
			}
			
			logger.info("Creating mediaContent object ");
			InputStreamContent mediaContent = new InputStreamContent(VIDEO_FILE_FORMAT, is);

			// Insert the video. The command sends three arguments. The first
			// specifies which information the API request is setting and which
			// information the API response should return. The second argument
			// is the video resource that contains metadata about the new video.
			// The third argument is the actual video content.
			logger.info("videoInsert object");
			YouTube.Videos.Insert videoInsert = youtube.videos().insert("snippet,statistics,status", videoObjectDefiningMetadata, mediaContent);

			// Set the upload type and add an event listener.
			logger.info("Set the upload type and add an event listener.");
			MediaHttpUploader uploader = videoInsert.getMediaHttpUploader();

			// Indicate whether direct media upload is enabled. A value of
			// "True" indicates that direct media upload is enabled and that
			// the entire media content will be uploaded in a single request.
			// A value of "False," which is the default, indicates that the
			// request will use the resumable media upload protocol, which
			// supports the ability to resume an upload operation after a
			// network interruption or other transmission failure, saving
			// time and bandwidth in the event of network failures.
			uploader.setDirectUploadEnabled(false);

			MediaHttpUploaderProgressListener progressListener = new MediaHttpUploaderProgressListener() {
				public void progressChanged(MediaHttpUploader uploader) throws IOException {
					Date time = Calendar.getInstance().getTime();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:S");
					switch (uploader.getUploadState()) {
					case INITIATION_STARTED:
						System.out.println("Initiation Started at: " + sdf.format(time) );
						break;
					case INITIATION_COMPLETE:
						System.out.println("Initiation Completed at: " + sdf.format(time) );
						break;
					case MEDIA_IN_PROGRESS:
						System.out.println("Upload in progress at: " + sdf.format(time) );
						System.out.println("Upload percentage: " + uploader.getProgress());
						break;
					case MEDIA_COMPLETE:
						System.out.println("Upload Completed at: " + sdf.format(time) );
						break;
					case NOT_STARTED:
						System.out.println("Upload Not Started at: " + sdf.format(time) );
						break;
					}
				}
			};
			logger.info("Setting Progress Listener");
			uploader.setProgressListener(progressListener);

			// Call the API and upload the video.
			logger.info("Call the API and upload the video. ");
			Video returnedVideo = videoInsert.execute();

			// Print data about the newly inserted video from the API response.
			System.out.println("\n================== Returned Video ==================\n");
			System.out.println("  - Id: " + returnedVideo.getId());
			System.out.println("  - Title: " + returnedVideo.getSnippet().getTitle());
			System.out.println("  - Tags: " + returnedVideo.getSnippet().getTags());
			System.out.println("  - Privacy Status: " + returnedVideo.getStatus().getPrivacyStatus());
			System.out.println("  - Video Count: " + returnedVideo.getStatistics().getViewCount());
			
			//Returning the link of the recently uploaded video
			return "http://www.youtube.com/watch?v=" + returnedVideo.getId();

		} catch (GoogleJsonResponseException e) {
			System.err.println("FUCK.GoogleJsonResponseException " + e.getMessage() + "\n" + e.getDetails()); // + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
			System.err.println("e.getHeaders(): " + e.getHeaders());
			System.err.println("e.getStatusCode(): " + e.getStatusCode());
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			System.err.println("IOException: " + e.getMessage());
			e.printStackTrace();
			return null;
		} catch (Throwable t) {
			System.err.println("Throwable: " + t.getMessage());
			t.printStackTrace();
			return null;
		}
	}
}