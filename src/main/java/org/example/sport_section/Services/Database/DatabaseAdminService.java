package org.example.sport_section.Services.Database;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.io.IOException;

@Service
public class DatabaseAdminService {

    private final DatabaseBackupService databaseBackupService;

    public DatabaseAdminService(DatabaseBackupService databaseBackupService) {
        this.databaseBackupService = databaseBackupService;
    }

    public String createBackup() throws IOException, InterruptedException {
        // Определяем путь для временного файла
        String filePath = System.getProperty("java.io.tmpdir") + File.separator + "backup.tar";

        // Вызываем метод для создания резервной копии
        databaseBackupService.createBackup(filePath, "sport_section", "postgres");

        return filePath;
    }


    public void restoreDatabaseFromFile(InputStream inputStream) throws IOException, InterruptedException {
        // Сохраняем переданный файл во временную директорию
        String tempFilePath = System.getProperty("java.io.tmpdir") + "uploaded_backup.tar";
        Files.copy(inputStream, Paths.get(tempFilePath), StandardCopyOption.REPLACE_EXISTING);

        // Восстанавливаем базу данных из резервной копии
        databaseBackupService.restoreDatabase(tempFilePath, "sport_section", "postgres");

        // Удаляем временный файл
        Files.delete(Paths.get(tempFilePath));
    }
}

