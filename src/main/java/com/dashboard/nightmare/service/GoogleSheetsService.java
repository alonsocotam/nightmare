package com.dashboard.nightmare.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

@Service
public class GoogleSheetsService {

    private final String SPREADSHEET_ID = "1P9t2xiMBBlmPbTrEKy7z1q8iFw5Yj9Dgph_xFuWlDT4";
    private final String SHEET_NAME = "Sheet1";

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String SERVICE_ACCOUNT_KEY_PATH = getPathToGoogleCredentials();
    private Sheets sheetsService;

    private static String getPathToGoogleCredentials() {
        String currentDir = System.getProperty("user.dir");
        return Paths.get(currentDir, "cred.json").toString();
    }

    public GoogleSheetsService() throws IOException, GeneralSecurityException {
        GoogleCredential credentials = GoogleCredential.fromStream(new FileInputStream(SERVICE_ACCOUNT_KEY_PATH))
                .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));

        sheetsService = new Sheets.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                credentials)
                .build();
    }

    public void appendDataToNextRow(List<Object> rowData) {
        try {
            String range = SHEET_NAME + "!A:A";
            ValueRange response = sheetsService.spreadsheets().values()
                    .get(SPREADSHEET_ID, range)
                    .execute();

            List<List<Object>> values = response.getValues();
            int nextRow = (values != null) ? values.size() + 1 : 1;

            String appendRange = SHEET_NAME + "!A" + nextRow;
            ValueRange valueRange = new ValueRange();
            valueRange.setValues(Arrays.asList(rowData));

            sheetsService.spreadsheets().values()
                    .update(SPREADSHEET_ID, appendRange, valueRange)
                    .setValueInputOption("RAW")
                    .execute();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

}
