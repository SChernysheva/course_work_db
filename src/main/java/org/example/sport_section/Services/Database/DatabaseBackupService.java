package org.example.sport_section.Services.Database;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.concurrent.TimeUnit;

@Service
public class DatabaseBackupService {
    final String PG_DUMP_PATH = "C:\\Program Files\\PostgreSQL\\17\\bin\\pg_dump.exe";
    final String PG_RESTORE_PATH = "C:\\Program Files\\PostgreSQL\\17\\bin\\pg_restore.exe";
    @Value("${app.password}")
    private String PASSWORD;
    @Value("${app.db_name}")
    private String DB_NAME;
    @Value("${app.username}")
    private String USERNAME_DB;

    public void createBackup(String filePath, String dbName, String username) throws IOException, InterruptedException {
        // Конфигурация ProcessBuilder
        ProcessBuilder pb = new ProcessBuilder(
                PG_DUMP_PATH,
                "-U", USERNAME_DB,
                "-F", "t", // Формат TAR
                "-f", filePath, // Указываем путь для сохранения
                DB_NAME // Имя базы данных
        );

        pb.environment().put("PGPASSWORD", PASSWORD);

        Process process = pb.start();

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            String errorOutput = new String(process.getErrorStream().readAllBytes()); // Ошибки
            String output = new String(process.getInputStream().readAllBytes()); // Общий лог
            throw new IOException("Ошибка при создании резервной копии!\n" + errorOutput + "\nЛог программы:\n" + output);
        }
    }

    public void restoreDatabase(String filePath, String dbName, String username) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(
                PG_RESTORE_PATH,
                "-U", USERNAME_DB,         // Имя пользователя
                "-d", DB_NAME,           // Название базы данных
                "-F", "t",              // Формат резервной копии (tar)
                "--clean",              // Уничтожить объект перед перезаписью
                "--if-exists",          // Игнорировать ошибки при отсутствии объекта
                filePath                // Путь к файлу резервной копии
        );

        pb.environment().put("PGPASSWORD", "2005");
        Process process = pb.start();

        //тут была какая-то проблема с заполнением буфера и вечной загрузкой то что ниже это починило
        //---
        // Чтение stdout (вывода команды) и stderr (ошибок)
        Thread stdoutReader = new Thread(() -> readStream(process.getInputStream(), System.out));
        Thread stderrReader = new Thread(() -> readStream(process.getErrorStream(), System.err));
        stdoutReader.start();
        stderrReader.start();
        //---

        boolean finished = process.waitFor(60, TimeUnit.SECONDS);
        if (!finished) { // Если процесс завис
            process.destroy();
            throw new IOException("Восстановление базы данных превысило время ожидания.");
        }

        if (process.exitValue() != 0) {
            throw new IOException("Ошибка при восстановлении базы данных. Код ошибки: " + process.exitValue());
        }
    }

    private void readStream(InputStream inputStream, PrintStream outputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                outputStream.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
