package com.iss.renterscore.authentication.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "property_image")
public class PropertyImage {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "property_image_seq")
    @SequenceGenerator(name = "property_image_seq", allocationSize = 1)

    @JsonProperty(value = "image_id")
    @Column(name = "image_id")
    private Long id;

    @JsonProperty(value = "image_url")
    @Column(name = "image_url")
    private String image;

    @JsonProperty(value = "property_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

}
