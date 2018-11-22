package com.akvasoft.dental_scrape;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Repo extends JpaRepository<DentalContent, Integer> {
    DentalContent getTopByRegistrationEquals(String reg);
}
