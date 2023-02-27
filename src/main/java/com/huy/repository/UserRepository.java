package com.huy.repository;

import com.huy.model.Role;
import com.huy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findAllByDeletedIsFalseAndRolesNotContainsIgnoreCaseAndUsernameNot(Role role, String username);


    Optional<User> findByPhone(String phone);

    Optional<User> findByUsername(String username);
    List<User> findAllByDeletedIsFalse();

    List<User> findAllByDeletedIsFalseAndUsernameNot(String username);

}
