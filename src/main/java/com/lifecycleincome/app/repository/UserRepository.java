package com.lifecycleincome.app.repository;

import com.lifecycleincome.app.entity.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<SiteUser, Long>{
    Optional<SiteUser> findByUsername(String username);
}
