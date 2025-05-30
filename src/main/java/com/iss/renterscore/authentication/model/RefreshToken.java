package com.iss.renterscore.authentication.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import java.time.Instant;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Entity
@Table(name = "refresh_token")
public class RefreshToken extends BaseModel{

	@Id
	@Column(name = "token_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "refresh_token_seq")
	@SequenceGenerator(name = "refresh_token_seq", allocationSize = 1)
	private Long id;
	
	@Column(name = "token", nullable = false, unique = true)
	@NaturalId(mutable = true)
	private String token;
	
	@Column(name = "refresh_count")
	private Long refreshCount;
	
	@Column(name = "expiry_date", nullable = false)
	private Instant expiryDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private Users user;

	@Column(name = "device_id")
	private String deviceId;
	
	public void increaseRefreshCount() {
		refreshCount = refreshCount + 1;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		RefreshToken that = (RefreshToken) o;
		return Objects.equals(id, that.id) && Objects.equals(token, that.token) && Objects.equals(refreshCount, that.refreshCount) && Objects.equals(expiryDate, that.expiryDate) && Objects.equals(user, that.user) && Objects.equals(deviceId, that.deviceId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), id, token, refreshCount, expiryDate, user, deviceId);
	}
}
