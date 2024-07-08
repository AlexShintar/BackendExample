package ru.shintar.shopbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.shintar.shopbackend.entity.Ad;
import ru.shintar.shopbackend.entity.User;

import java.util.List;

@Repository
public interface AdRepository extends JpaRepository<Ad, Integer> {
    List<Ad> findAllByAuthor(User author);
}
