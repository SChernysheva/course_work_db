package org.example.sport_section.Models.Images;

import jakarta.persistence.*;
import org.example.sport_section.Models.Courts.Court;
import org.example.sport_section.Models.Users.User;

@Entity
@Table(name = "court_images")
public class CourtImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String image_type;

    @OneToOne
    @JoinColumn(name = "court_id", nullable = false)
    private Court court;

    private byte[] image_data;


    public CourtImage() {}

    public Court getCourt() {
        return court;
    }

    public void setCourt(Court court) {
        this.court = court;
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

    public byte[] getImage_data() {
        return image_data;
    }

    public void setImage_data(byte[] image_data) {
        this.image_data = image_data;
    }
}
