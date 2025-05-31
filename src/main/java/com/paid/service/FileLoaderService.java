package com.paid.service;

import com.paid.entity.CallDetailRecordsEntity;
import com.paid.entity.CdrLogsEntity;
import com.paid.repository.CallDetailRecordsRepository;
import com.paid.repository.CdrLogsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Service
public class FileLoaderService {

    @Value("${monitor.directory}")
    private String directoryPath;

    private final CdrLogsRepository cdrLogsRepository;

    private final CallDetailRecordsRepository callDetailRecordsRepository;

    public FileLoaderService(CdrLogsRepository cdrLogsRepository, CallDetailRecordsRepository callDetailRecordsRepository) {
        this.cdrLogsRepository = cdrLogsRepository;
        this.callDetailRecordsRepository = callDetailRecordsRepository;
    }

    private Boolean isFileExists(String fileName) {
        return cdrLogsRepository.findByFileName(fileName) != null;
    }

    @Scheduled(fixedRate = 60000)
    public void scanDirectory() {
        File folder = new File(directoryPath);

        if (!folder.exists() || !folder.isDirectory()) {
            System.err.println("📛 Carpeta no existe: " + directoryPath);
            return;
        }

        File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            System.out.println("📂 No hay archivos nuevos.");
            return;
        }

        for (File file : files) {
            if (file.isFile()) {
                String fileName = file.getName();
                if (!isFileExists(fileName)) {
                    System.out.println("🆕 Procesando archivo nuevo: " + fileName);

                    CdrLogsEntity cdrLogsEntity = processFile(file);

                    System.out.println("🆕 Procesando archivo nuevo: " + cdrLogsEntity.getFileName() +
                            " con " + cdrLogsEntity.getNumberOfSuccessfullyLoadedRecords() +
                            " registros exitosos y " + cdrLogsEntity.getNumberOfFailedRecords() +
                            " registros fallidos. Inicio en : " + cdrLogsEntity.getUploadStartTime() +
                            " y fin en : " + cdrLogsEntity.getUploadEndTime());

                    cdrLogsRepository.save(cdrLogsEntity);
                } else {
                    System.out.println("📂 No hay archivo nuevo: " + fileName + ", ya existe en la base de datos.");
                }
            }
        }
    }

    private CdrLogsEntity processFile(File file) {
        Long numberOfFailedRecords = 0L;
        Long numberOfSuccessfullyLoadedRecords = 0L;
        CdrLogsEntity cdrLogsEntity = new CdrLogsEntity();
        cdrLogsEntity.setFileName(file.getName());
        cdrLogsEntity.setUploadStartTime(java.time.OffsetTime.now());
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                CallDetailRecordsEntity callDetailRecordsEntity = new CallDetailRecordsEntity();
                callDetailRecordsEntity.setFileName(file.getName());
                callDetailRecordsEntity.setRecordLine(line);

                String[] segment = line.split("\\|");

                if (segment.length > 25) {
                    String status = segment[25];
                    if("SUCCESS".equalsIgnoreCase(status)){
                        numberOfSuccessfullyLoadedRecords++;
                    } else {
                        numberOfFailedRecords++;
                    }
                } else {
                    numberOfFailedRecords++;
                }
                lineNumber++;
                callDetailRecordsRepository.save(callDetailRecordsEntity);
            }

        } catch (IOException e) {
            System.err.println("❌ Error leyendo archivo: " + file.getName());
            e.printStackTrace();
        }
        cdrLogsEntity.setNumberOfFailedRecords(numberOfFailedRecords);
        cdrLogsEntity.setNumberOfSuccessfullyLoadedRecords(numberOfSuccessfullyLoadedRecords);
        cdrLogsEntity.setUploadEndTime(java.time.OffsetTime.now());
        return cdrLogsEntity;
    }
}
