package com.iss.renterscore.authentication.repos;

import com.iss.renterscore.authentication.model.Bookmark;
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
public interface BookmarkRepo extends JpaRepository<Bookmark, Long> {

    @Query("SELECT b FROM Bookmark b WHERE b.users = :user AND b.property = :property")
    Bookmark findByUserIdAndPropertyId(@Param("user")Users user, @Param("property") Property property);

    @Query("SELECT b FROM Bookmark b WHERE b.property = :property")
    Bookmark findByPropertyId(@Param("property") Property property);

    @Query("SELECT b FROM Bookmark b WHERE b.users = :user ORDER BY b.createdDate DESC")
    List<Bookmark> findAllByUser(@Param("user") Users user);
}
