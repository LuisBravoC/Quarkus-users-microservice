package org.cycles.entites;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(
        uniqueConstraints=
        @UniqueConstraint(columnNames={"user", "product"})
)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user", referencedColumnName = "id")
    @JsonBackReference
    private User user;
    @Column
    private Long product;
    @Transient
    private String name;
    @Transient
    private String code;
    @Transient
    private String description;
}
