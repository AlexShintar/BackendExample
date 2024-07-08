package ru.shintar.shopbackend.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;


import javax.persistence.*;

@Entity
@Table(name = "authorities")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Authorities {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String username;

    String authority;
}