package fr.gwan.parkinglots.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.gwan.parkinglots.domain.ParkingLot;


@Repository
public interface ParkingLotRepository extends JpaRepository<ParkingLot, UUID> {

}