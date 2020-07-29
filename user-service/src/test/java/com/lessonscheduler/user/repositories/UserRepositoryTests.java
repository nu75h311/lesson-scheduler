package com.lessonscheduler.user.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.lessonscheduler.user.models.User;

@DataJpaTest
public class UserRepositoryTests {

    private final User geddy;
    private final User alex;

    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private UserRepository userRepository;

    public UserRepositoryTests() {
        geddy = User.builder()
                .firstName("Geddy").lastName("Lee").email("geddy.lee@email.com").build();
        alex = User.builder()
                .firstName("Alex").lastName("Lifeson").email("alex.lifeson@email.com").build();
    }

    @BeforeEach
    void setup() {
        this.testEntityManager.persist(geddy);
        this.testEntityManager.persist(alex);
    }

    @AfterEach
    void cleanup() {
        this.testEntityManager.clear();
    }

    @Test
    @DisplayName("Find all users")
    public void testFindAllUsers() {
        List<User> users = userRepository.findAll();

        assertAll(
                () -> assertThat(users.contains(geddy)),
                () -> assertThat(users.contains(alex)),
                () -> assertThat(users.size()).isEqualTo(2));
    }
}
