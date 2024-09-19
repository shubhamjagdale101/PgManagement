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
public class Txn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String txnId;

    @OneToOne
    @JoinColumn
    private User user;

    @OneToOne
    @JoinColumn
    private MonthyRent month;

    @Column(nullable = false)
    private Integer amount;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private TxnStatus status;

    @CreationTimestamp
    private Date creationDate;

    @UpdateTimestamp
    private Date updationDate;
}
