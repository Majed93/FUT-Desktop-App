package com.fut.desktop.app.futservice.repository;

import com.fut.desktop.app.domain.Platform;
import com.fut.desktop.app.restObjects.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface for database interactions.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    Account findByEmailAndPlatform(String email, Platform platform);
}
