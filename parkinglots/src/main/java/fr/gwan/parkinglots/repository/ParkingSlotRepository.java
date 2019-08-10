package fr.gwan.parkinglots.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fr.gwan.parkinglots.domain.ParkingSlot;
import fr.gwan.parkinglots.domain.ParkingSlotTypeEnum;


@Repository
public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, UUID> {
    @Query("select parkingSlot from ParkingSlot parkingSlot "
    		+ "inner join parkingSlot.parkingLot parkingLot "
    		+ "where parkingLot.ref = :ref "
    		+ "and parkingSlot.type = :type")
    List<ParkingSlot> findAllWithEagerRelationships(
    		@Param("ref") UUID ref,
    		@Param("type") ParkingSlotTypeEnum type);
    @Query("select parkingSlot from ParkingSlot parkingSlot "
    		+ "inner join parkingSlot.parkingLot parkingLot "
    		+ "where parkingLot.ref = :ref "
    		+ "and parkingSlot.licensePlateParkedVehicle = :license_plate_parked_vehicle")
    ParkingSlot findOneWithEagerRelationships(
    		@Param("ref") UUID ref,
    		@Param("license_plate_parked_vehicle") String licensePlateParkedVehicle);
}