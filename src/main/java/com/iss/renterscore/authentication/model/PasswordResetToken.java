package com.iss.renterscore.authentication.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.NaturalId;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Entity
@Table(name = "password_reset_token")
public class PasswordResetToken {
	
	@Id
	@Column(name = "token_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reset_token_seq")
	@SequenceGenerator(name = "reset_token_seq", allocationSize = 1)
	private Long id;
	
	@NaturalId
	@Column(name = "token", nullable = false, unique = true)
	private String token;
	
	@Column(name = "expiry_date", nullable = false)
	private Instant expiryDate;
	
	@OneToOne(targetEntity = Users.class, fetch = FetchType.EAGER)
	@JoinColumn(nullable = false, name = "user_id")
	private Users user;
	

}
