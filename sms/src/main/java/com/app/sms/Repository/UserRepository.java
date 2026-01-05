package com.app.sms.Repository;

import com.app.sms.Entity.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

    boolean existsByName(String name);

    boolean existsByNameAndPasswordHash(String name, String pass);

    Optional<UserEntity> findByName(String name);

    @Query(value = """
            SELECT name FROM users
            """, nativeQuery = true)
    List<String> getUserNamesPaged(Pageable pageable);
}
