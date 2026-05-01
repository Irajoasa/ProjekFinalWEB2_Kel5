package com.example.productcrud.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import jakarta.persistence.UniqueConstraint;


@Entity
@Table(
        name = "categories",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_categories_user_name",
                columnNames = {"user_id", "name"}
        )
)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name ="user_id", nullable = false)
    private User user;


    public Category() {}
        public Long getId() {
        return id;
    }
        public void setId(Long id) {
        this.id=id;
    }
        public String getName() {
        return name ;
    }
        public void setName(String name) {
        this.name=name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public  User getUser() {
    return user;
    }

    public void setUser(User user) {
    this.user = user;
    }
}

