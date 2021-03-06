/**
 *
 */
package com.weshaka.google.calendar.ole;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.SecurityUtils;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;

/**
 * @author ema
 */
public class CalendarServiceFactory {
    /** Application name. */
    private static final String APPLICATION_NAME = "shaka-ole-calendar";

    /** Directory to store user credentials. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".credentials/shaka-ole-calendar");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart. */
    private static final List<String> SCOPES = Arrays.asList(CalendarScopes.CALENDAR);

    /** Service account ID. */
    private static final String SERVICE_ACCOUNT_ID = "729979937520-86k2ufd1rs6e2h84rf18hgmjjth2sv4a@developer.gserviceaccount.com";

    /** Service account impersonate user ID. */
    private static final String SERVICE_ACCOUNT_IMPERS_USER_ID = "admin@weshaka.com";

    private static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";

    private static GoogleAuthorizationCodeFlow flow = null;

    static GoogleClientSecrets clientSecrets = loadClientSecrets();

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (final Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     *
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
        // InputStream in = CalendarServiceFactory.class.getResourceAsStream("/client_secret.json");
        // System.out.println(in);
        // GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Customize
        final String authorizeUrl = new GoogleAuthorizationCodeRequestUrl(clientSecrets, REDIRECT_URI, SCOPES).setState("").build();
        System.out.println("Paste this URL into a web browser to authorize API Access:\n" + authorizeUrl);
        System.out.println("... and type the code you received here: ");
        final BufferedReader in_authcode = new BufferedReader(new InputStreamReader(System.in));
        final String authorizationCode = in_authcode.readLine();

        final Credential credential = exchangeCode(authorizationCode);
        // Build flow and trigger user authorization request.
        // GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
        // .setDataStoreFactory(DATA_STORE_FACTORY).setAccessType("offline").build();
        // LocalServerReceiver localServerReceiver = new LocalServerReceiver.Builder().setPort(9089).build();//setHost("dev.weshaka.com").
        // System.out.println("Saving credentials...");
        // Credential credential = new AuthorizationCodeInstalledApp(flow, localServerReceiver).authorize("user");
        // System.out.println("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    static Credential authorizeWithServiceAccount() throws IOException, GeneralSecurityException {
        final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        // Build service account credential.
        // String in = CalendarServiceFactory.class.getResource("/shaka-ole-ff5f1899560a.p12").getPath();
        final InputStream in = CalendarServiceFactory.class.getResourceAsStream("/shaka-ole-ff5f1899560a.p12");
        final PrivateKey pk = SecurityUtils.loadPrivateKeyFromKeyStore(SecurityUtils.getPkcs12KeyStore(), in, "notasecret", "privatekey", "notasecret");
        final GoogleCredential credential = new GoogleCredential.Builder().setTransport(HTTP_TRANSPORT).setJsonFactory(jsonFactory).setServiceAccountId(SERVICE_ACCOUNT_ID).setServiceAccountUser(SERVICE_ACCOUNT_IMPERS_USER_ID)
                .setServiceAccountScopes(SCOPES)
                // .setServiceAccountPrivateKeyFromP12File(new File(in))
                .setServiceAccountPrivateKey(pk).build();
        return credential;
    }

    /**
     * Exchange the authorization code for OAuth 2.0 credentials.
     */
    static Credential exchangeCode(String authorizationCode) throws IOException {
        final GoogleAuthorizationCodeFlow flow = getFlow();
        final GoogleTokenResponse response = flow.newTokenRequest(authorizationCode).setRedirectUri(REDIRECT_URI).execute();
        return flow.createAndStoreCredential(response, null);
    }

    /**
     * Build and return an authorized Calendar client service.
     *
     * @return an authorized Calendar client service
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public static com.google.api.services.calendar.Calendar getCalendarService() throws IOException, GeneralSecurityException {
        // Credential credential = authorize(); //Alternate use 3 legs flow.
        final Credential credential = authorizeWithServiceAccount();
        return new com.google.api.services.calendar.Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
    }

    /**
     * Build an authorization flow and store it as a static class attribute.
     */
    static GoogleAuthorizationCodeFlow getFlow() {
        if (flow == null) {
            flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES).setAccessType("offline").setApprovalPrompt("force").build();
        }
        return flow;
    }

    /**
     * Helper to load client ID/Secret from file.
     */
    private static GoogleClientSecrets loadClientSecrets() {
        try {
            // Load client secrets.
            final InputStream in = CalendarServiceFactory.class.getResourceAsStream("/client_secret.json");
            final GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
            return clientSecrets;
        } catch (final Exception e) {
            System.out.println("Could not load clientsecrets.json");
            e.printStackTrace();
        }
        return clientSecrets;
    }
}
