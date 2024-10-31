package org.example.sport_section.Models.Images;

import jakarta.persistence.*;

@Entity
@Table(name = "ui_images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String image_type;
    private String image_page;
    private byte[] image_data;

    public Image(String image_type, String image_page, byte[] image_data) {
        this.image_type = image_type;
        this.image_page = image_page;
        this.image_data = image_data;
    }
    public Image() {}

    public Image(int id, String image_type, String image_page, byte[] image_data) {
        this.id = id;
        this.image_type = image_type;
        this.image_page = image_page;
        this.image_data = image_data;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage_type() {
        return image_type;
    }

    public void setImage_type(String image_type) {
        this.image_type = image_type;
    }

    public String getImage_page() {
        return image_page;
    }

    public void setImage_page(String image_page) {
        this.image_page = image_page;
    }

    public byte[] getImage_data() {
        return image_data;
    }

    public void setImage_data(byte[] image_data) {
        this.image_data = image_data;
    }
}
