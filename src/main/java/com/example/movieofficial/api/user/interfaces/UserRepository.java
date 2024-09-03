package com.example.movieofficial.api.user.interfaces;

import com.example.movieofficial.api.user.entities.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    @Query("select u from User u where u.id = ?1 and u.verify = false")
    Optional<User> findByIdAndVerifyFalse(String id);

    @Query("select u from User u where u.email = ?1")
    Optional<User> findByEmail(String email);

    @Query("select (count(u) > 0) from User u where u.email = ?1")
    boolean existsByEmail(String email);

    @Query("select u from User u order by u.createDate DESC")
    List<User> findByOrderByCreateDateDesc(Pageable pageable);

    @Query("select u from User u where u.role.id = ?1 order by u.createDate DESC")
    List<User> findByRoleIdOrderByCreateDateDesc(Integer id, Pageable pageable);

    @Query("select u from User u where u.email = ?1 and u.verify = false")
    Optional<User> findByEmailAndVerifyFalse(String email);


}
