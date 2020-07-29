package com.lessonscheduler.user.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lessonscheduler.user.models.User;

public interface UserRepository extends JpaRepository<User, UUID> {
}
