package com.iss.renterscore.authentication.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iss.renterscore.authentication.model.RentStatus;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RentRequest {

    @JsonProperty("property_id")
    private Long propertyId;
    @JsonProperty("rent_status")
    RentStatus rentStatus;
    @JsonProperty("start_date")
    private String startDate;
    @JsonProperty("end_date")
    private String endDate;
}
