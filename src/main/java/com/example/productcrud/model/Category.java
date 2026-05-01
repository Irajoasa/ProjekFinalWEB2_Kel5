package com.example.productcrud.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;


@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

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

    public  User getUser() {
    return user;
    }

    public void setUser(User user) {
    this.user = user;
    }
}

