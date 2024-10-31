package org.example.sport_section.Models.Images;

import jakarta.persistence.*;

@Entity
@Table(name = "court_images")
public class CourtImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String image_type;
    private int court_id;
    private byte[] image_data;

    public CourtImage(byte[] image_data, int court_id, String image_type) {
        this.image_data = image_data;
        this.court_id = court_id;
        this.image_type = image_type;
    }

    public CourtImage() {}

    public CourtImage(int id, String image_type, int court_id, byte[] image_data) {
        this.id = id;
        this.image_type = image_type;
        this.court_id = court_id;
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

    public int getCourt_id() {
        return court_id;
    }

    public void setCourt_id(int court_id) {
        this.court_id = court_id;
    }

    public byte[] getImage_data() {
        return image_data;
    }

    public void setImage_data(byte[] image_data) {
        this.image_data = image_data;
    }
}
