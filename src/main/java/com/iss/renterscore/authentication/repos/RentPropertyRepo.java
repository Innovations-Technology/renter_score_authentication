package com.iss.renterscore.authentication.repos;

import com.iss.renterscore.authentication.model.Property;
import com.iss.renterscore.authentication.model.RentProperty;
import com.iss.renterscore.authentication.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@EnableJpaRepositories
@Repository
public interface RentPropertyRepo extends JpaRepository<RentProperty, Long> {

    @Query("SELECT r FROM RentProperty r WHERE r.tenant = :tenant")
    List<RentProperty> getAllRentPropertyByTenant(@Param("tenant") Users tenant);

    @Query("SELECT r FROM RentProperty r WHERE r.owner = :owner OR r.tenant = :owner")
    List<RentProperty> getAllRentPropertyByOwner(@Param("owner") Users owner);

    @Query("SELECT r FROM RentProperty r WHERE r.property = :property")
    List<RentProperty> getAllRentPropertyByProperty(Property property);

    RentProperty findByTenantAndPropertyAndStartDateAndEndDate(Users users, Property property, LocalDate startDate, LocalDate endDate);
}
