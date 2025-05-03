package com.iss.renterscore.authentication.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iss.renterscore.authentication.model.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PropertyRequest {
    @JsonProperty("title")
    String title;
    @JsonProperty("description")
    String description;
    @JsonProperty("property_type")
    PropertyType propertyType;
    @JsonProperty("amenities")
    String amenities;
    @JsonProperty("block_no")
    String blockNo;
    @JsonProperty("unit_no")
    String unitNo;
    @JsonProperty("street")
    String street;
    @JsonProperty("postal_code")
    String postalCode;
    @JsonProperty("bedrooms")
    Integer bedrooms;
    @JsonProperty("bathrooms")
    Integer bathrooms;
    @JsonProperty("region")
    Regions regions;
    @JsonProperty("available_date")
    String availableDate;
    @JsonProperty("property_status")
    PropertyStatus propertyStatus;
    @JsonProperty("price")
    Integer price;
    @JsonProperty("size")
    String size;
    @JsonProperty("rent_type")
    RentType rentType;
    @JsonProperty("property_state")
    PropertyState propertyState;
    @JsonProperty("is_hero_image_changed")
    Boolean isHeroImageChanged;
}
