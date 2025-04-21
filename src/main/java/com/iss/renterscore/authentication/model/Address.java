package com.iss.renterscore.authentication.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Embeddable
public class Address {
    @JsonProperty("block_no")
    @Column(name = "block_no")
    String blockNo;
    @JsonProperty("unit_no")
    @Column(name = "unit_no")
    String unitNo;
    @JsonProperty("street")
    @Column(name = "street")
    String street;
    @JsonProperty("postal_code")
    @Column(name = "postal_code")
    String postalCode;
    @JsonProperty("region")
    @Column(name = "region", nullable = false)
    @Enumerated(EnumType.STRING)
    Regions regions;
}
