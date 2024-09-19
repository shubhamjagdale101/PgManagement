package com.shubham.UserServer.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private Integer roomNo;

    private Integer extraChargePerMonth;

    private Integer availableSeats;

    @CreationTimestamp
    private Date creationDate;

    @UpdateTimestamp
    private Date updationDate;
}