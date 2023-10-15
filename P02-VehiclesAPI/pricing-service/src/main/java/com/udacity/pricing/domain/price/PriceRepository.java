package com.udacity.pricing.domain.price;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface PriceRepository extends JpaRepository<Price, Long> {
    Price findByVehicleId(@Param("vehicleId") Long vehicleId);
}
