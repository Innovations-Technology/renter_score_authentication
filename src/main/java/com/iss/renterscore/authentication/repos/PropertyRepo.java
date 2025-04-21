package com.iss.renterscore.authentication.repos;

import com.iss.renterscore.authentication.model.Property;
import com.iss.renterscore.authentication.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@EnableJpaRepositories
@Repository
public interface PropertyRepo extends JpaRepository<Property, Long> {


    List<Property> findAllByUser(Users user);

    List<Property> findAllByUserId(Long userId);

    Property findByAddress_UnitNoAndAddress_BlockNoAndAddress_PostalCode(String unitNo, String blockNo, String postalCode);

    @Query("SELECT p FROM Property p WHERE p.address.unitNo = :unitNo AND p.address.blockNo = :blockNo AND p.address.postalCode = :postalCode")
    Property existsByUnitNoAndBlockNoAndPostalCode(@Param("unitNo") String unitNo, @Param("blockNo") String blockNo, @Param("postalCode") String postalCode);

}
