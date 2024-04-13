package com.lcwd.user.service.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "micro_users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    private String uuid;
    private String name;
    @Column(unique = true, nullable = false)
    private String email;
    private String about;

    @Transient
    private List<Rating> ratings = new ArrayList<>();
}
