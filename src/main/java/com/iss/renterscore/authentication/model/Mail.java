package com.iss.renterscore.authentication.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class Mail implements Serializable {

	private String from;
	private String fromName;
	private String to;
	private String toName;
	private String subject;
	private String content;
	private String title;
	private Map<String, String> model = new HashMap<>();
	
}
