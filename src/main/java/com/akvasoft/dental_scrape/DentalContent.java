package com.akvasoft.dental_scrape;

import javax.persistence.*;

@Entity
@Table(name = "dental")
public class DentalContent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    int id;
    @Column(name = "no")
    String no;
    @Column(name = "name")
    String name;
    @Column(name = "registration")
    String registration;
    @Column(name = "council")
    String council;

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public String getCouncil() {
        return council;
    }

    public void setCouncil(String council) {
        this.council = council;
    }
}
