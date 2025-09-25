package com.izak.auth_service.user.repository;

import com.izak.auth_service.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {
  Optional<User> findByEmail (String email);
}
