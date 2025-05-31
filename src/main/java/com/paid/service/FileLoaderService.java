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
import java.sql.Timestamp;

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

            while ((line = reader.readLine()) != null) {
                CallDetailRecordsEntity callDetailRecordsEntity = new CallDetailRecordsEntity();
                callDetailRecordsEntity.setFileName(file.getName());

                String[] segment = line.split("\\|");

                callDetailRecordsEntity.setRecordDate(Timestamp.valueOf(segment[0].replace(",",".")));
                callDetailRecordsEntity.setlSpc(safeParseInt(segment[1]));
                callDetailRecordsEntity.setlSsn(safeParseInt(segment[2]));
                callDetailRecordsEntity.setlRi(safeParseInt(segment[3]));
                callDetailRecordsEntity.setlGtI(safeParseInt(segment[4]));
                callDetailRecordsEntity.setlGtDigits(segment[5]);
                callDetailRecordsEntity.setrSpc(safeParseInt(segment[6]));
                callDetailRecordsEntity.setrSsn(safeParseInt(segment[7]));
                callDetailRecordsEntity.setrRi(safeParseInt(segment[8]));
                callDetailRecordsEntity.setrGtI(safeParseInt(segment[9]));
                callDetailRecordsEntity.setrGtDigits(segment[10]);
                callDetailRecordsEntity.setServiceCode(segment[11]);
                callDetailRecordsEntity.setOrNature(safeParseInt(segment[12]));
                callDetailRecordsEntity.setOrPlan(safeParseInt(segment[13]));
                callDetailRecordsEntity.setOrDigits(segment[14]);
                callDetailRecordsEntity.setDeNature(safeParseInt(segment[15]));
                callDetailRecordsEntity.setDePlan(safeParseInt(segment[16]));
                callDetailRecordsEntity.setDeDigits(segment[17]);
                callDetailRecordsEntity.setIsdnNature(safeParseInt(segment[18]));
                callDetailRecordsEntity.setIsdnPlan(safeParseInt(segment[19]));
                callDetailRecordsEntity.setMsisdn(segment[20]);
                callDetailRecordsEntity.setVlrNature(safeParseInt(segment[21]));
                callDetailRecordsEntity.setVlrPlan(safeParseInt(segment[22]));
                callDetailRecordsEntity.setVlrDigits(segment[23]);
                callDetailRecordsEntity.setImsi(segment[24]);
                callDetailRecordsEntity.setStatus(segment[25]);
                callDetailRecordsEntity.setType(segment[26]);
                callDetailRecordsEntity.setTstamp(Timestamp.valueOf(segment[27].replace(",",".")));
                callDetailRecordsEntity.setLocalDialogId(safeParseLong(segment[28]));
                callDetailRecordsEntity.setRemoteDialogId(safeParseLong(segment[29]));
                callDetailRecordsEntity.setDialogDuration(safeParseLong(segment[30]));
                callDetailRecordsEntity.setUssdString(segment[31]);
                callDetailRecordsEntity.setId(segment[32]);

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

    private Integer safeParseInt(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            // Aquí puedes loguear si quieres
            return null;
        }
    }

    private Long safeParseLong(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            // Puedes loguear el error si lo deseas
            return null;
        }
    }
}
