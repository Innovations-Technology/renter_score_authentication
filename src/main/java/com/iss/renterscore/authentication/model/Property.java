package com.iss.renterscore.authentication.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "property")
public class Property extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "property_seq")
    @SequenceGenerator(name = "property_seq", allocationSize = 1)

    @JsonProperty(value = "property_id")
    @Column(name = "property_id")
    private Long id;

    @JsonProperty("title")
    @Column(name = "title")
    String title;
    @JsonProperty("description")
    @Column(name = "description")
    String description;
    @JsonProperty("hero_image")
    @Column(name = "hero_image")
    String heroImage;
    @JsonProperty("images")
    @JsonManagedReference
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(name = "images")
    List<PropertyImage> images = new ArrayList<>();
    @JsonProperty("property_type")
    @Column(name = "property_type", nullable = false)
    @Enumerated(EnumType.STRING)
    PropertyType propertyType;
    @JsonProperty("amenities")
    @Column(name = "amenities")
    String amenities;
    @JsonProperty("bedrooms")
    @Column(name = "bedrooms")
    Integer bedrooms;
    @JsonProperty("bathrooms")
    @Column(name = "bathrooms")
    Integer bathrooms;
    @Embedded
    Address address;
    @JsonProperty("available_date")
    @Column(name = "available_date")
    String availableDate;
    @JsonProperty("property_status")
    @Column(name = "property_status", nullable = false)
    @Enumerated(EnumType.STRING)
    PropertyStatus propertyStatus;
    @JsonProperty("price")
    @Column(name = "price")
    Integer price;
    @JsonProperty("currency")
    @Column(name = "currency")
    String currency;
    @JsonProperty("size")
    @Column(name = "size")
    String size;
    @JsonProperty("rent_type")
    @Column(name = "rent_type", nullable = false)
    @Enumerated(EnumType.STRING)
    RentType  rentType;
    @JsonProperty(value = "user")
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;
    @Column(name = "property_state", nullable = false)
    @Enumerated(EnumType.STRING)
    PropertyState propertyState;

}
