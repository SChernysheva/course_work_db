package org.example.sport_section.front.Views.DataBaseBackup;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.example.sport_section.Services.Database.DatabaseAdminService;
import org.example.sport_section.front.Views.Home.HomePage;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileInputStream;
import java.io.InputStream;

@Route("admin/database")
@PageTitle("Управление резервными копиями")
public class AdminDatabaseView extends VerticalLayout {
    private final DatabaseAdminService databaseAdminService;

    @Autowired
    public AdminDatabaseView(DatabaseAdminService databaseAdminService) {
        this.databaseAdminService = databaseAdminService;
        Span title = new Span("Управление копиями базы данных");
        Span info = new Span("Для восстановления базы данных из копии загрузите ранее скачанный архив .tar");
        Span info2 = new Span("Для скачивания копии текущего состояния базы данных из копии нажмите кнопку ниже");
        title.getStyle().set("font-size", "18px");
        title.getStyle().set("color", "#555555");
        title.getStyle().set("font-weight", "bold");
        // Установка серого фона
        getStyle().set("background", "#f2f2f2");
        getStyle().set("border-radius", "8px");
        setAlignItems(Alignment.CENTER);
        setHeightFull();
        setWidthFull();
        getStyle().set("margin", "auto");

        // Кнопка для скачивания резервной копии
        Button downloadBackupButton = new Button("Скачать резервную копию");
        Anchor downloadLink = new Anchor();
        downloadLink.add(downloadBackupButton);
        downloadBackupButton.getStyle()
                .set("background-color", "#555555")
                .set("color", "white")
                .set("border-radius", "5px");

        downloadLink.setHref(createBackupAsStream());
        downloadLink.getElement().setAttribute("download", true); // Устанавливаем атрибут для скачивания файла

        // Загрузка файла резервной копии
        MemoryBuffer memoryBuffer = new MemoryBuffer();
        Upload upload = new Upload(memoryBuffer);
        upload.setAcceptedFileTypes("application/x-tar");
        upload.addSucceededListener(event -> restoreBackup(event, memoryBuffer));

        upload.getStyle()
                .set("background", "#e6e6e6")
                .set("border", "1px solid #ccc")
                .set("border-radius", "5px");

        Button backButton = new Button("Назад", click -> UI.getCurrent().navigate(HomePage.class));
        backButton.getStyle()
                .set("background", "#999999")
                .set("color", "white")
                .set("border-radius", "5px");

        add(title, info, info2, downloadLink, upload, backButton);
    }

    private StreamResource createBackupAsStream() {
        return new StreamResource("backup.tar", () -> {
            try {
                String backupPath = databaseAdminService.createBackup(); // Генерация резервной копии
                return new FileInputStream(backupPath); // Возвращаем поток с копией
            } catch (Exception e) {
                Notification.show("Ошибка : " + e.getMessage(), 5000, Notification.Position.MIDDLE);
                return InputStream.nullInputStream(); // Пустой поток при ошибке
            }
        });
    }


    private void restoreBackup(SucceededEvent event, MemoryBuffer memoryBuffer) {
        try (InputStream inputStream = memoryBuffer.getInputStream()) {
            databaseAdminService.restoreDatabaseFromFile(inputStream);
            Notification.show("База данных успешно восстановлена.");
        } catch (Exception e) {
            Notification.show("Ошибка восстановления базы: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
        }
    }
}
