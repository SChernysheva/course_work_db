package org.example.sport_section.Utils;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.StreamResource;

import java.io.ByteArrayInputStream;

public class ImageHelper {

    public static Image createImageFromByteArray(byte[] imageData, String name) {
        // Создаем StreamResource из предоставленного массива байтов
        StreamResource resource = new StreamResource(name, () -> new ByteArrayInputStream(imageData));

        // Создаем вертикаль изображения с помощью ресурса
        Image image = new Image(resource, "Description of image");
        return image;
    }
}
