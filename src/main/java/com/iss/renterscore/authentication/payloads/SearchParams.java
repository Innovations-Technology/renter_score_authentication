package com.iss.renterscore.authentication.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iss.renterscore.authentication.model.PropertyType;
import com.iss.renterscore.authentication.model.Regions;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class SearchParams implements Serializable {

	@JsonProperty("position")
	int position;
	@JsonProperty("property_type")
	PropertyType propertyType;
	@JsonProperty("min_price")
	String minPrice;
	@JsonProperty("max_price")
	String maxPrice;
	@JsonProperty("regions")
	Regions regions;

}
