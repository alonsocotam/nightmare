package com.dashboard.nightmare.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dashboard.nightmare.domain.GoogleResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

@Service
public class GoogleDriveService {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String SERVICE_ACCOUNT_KEY_PATH = getPathToGoogleCredentials();
    private static final String FOLDER_ID = "12iC9VS968HkIwlMCDmDZH4lbOQCXlfBa";
    private Drive driveService;

    private static String getPathToGoogleCredentials() {
        String currentDir = System.getProperty("user.dir");
        return Paths.get(currentDir, "cred.json").toString();
    }

    public GoogleDriveService() throws FileNotFoundException, IOException, GeneralSecurityException {
        GoogleCredential credentials = GoogleCredential.fromStream(new FileInputStream(SERVICE_ACCOUNT_KEY_PATH))
                .createScoped(Collections.singleton(DriveScopes.DRIVE));

        driveService = new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                credentials)
                .build();
    }

    public GoogleResponse uploadFile(java.io.File pdfFile) {
        GoogleResponse response = new GoogleResponse();
        File fileMetaData = new File();
        fileMetaData.setName(pdfFile.getName());
        fileMetaData.setParents(Arrays.asList(FOLDER_ID));

        try {
            File uploadedFile = driveService.files().create(fileMetaData,
                    new FileContent("application/pdf", pdfFile))
                    .setFields("id")
                    .execute();

            response.setMessage("Receipt Saved Successfully");
            response.setStatus(200);
            response.setUrl("https://drive.google.com/uc?export=view&id=" + uploadedFile.getId());
            return response;
        } catch (IOException e) {
            System.out.println(e.getMessage());

            response.setMessage(e.getMessage());
            response.setStatus(500);
            return response;
        }
    }

    public List<File> getFilesFromGoogleDrive() {
        String query = "'" + FOLDER_ID + "' in parents and trashed = false";
        FileList result;
        try {
            result = driveService.files().list()
                    .setQ(query)
                    .setFields("files(id, name, mimeType, size, createdTime)")
                    .execute();
        return result.getFiles();

        } catch (IOException e) {
            return null;
        }

    }

}
