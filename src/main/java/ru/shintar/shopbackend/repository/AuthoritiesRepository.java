package ru.shintar.shopbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.shintar.shopbackend.entity.Authorities;

import java.util.Optional;

@Repository
public interface AuthoritiesRepository extends JpaRepository<Authorities, Integer> {
    Optional<Authorities> findByUsername(String username);
}
