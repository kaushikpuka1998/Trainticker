package com.kgstrivers.trainticker.Entities;


import com.kgstrivers.trainticker.Enums.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "passengers")
@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class Passenger {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Integer age;
    @Enumerated(EnumType.STRING)
    private Gender gender;
}
