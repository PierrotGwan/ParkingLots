package fr.gwan.parkinglots.domain;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


/**
 * Resource representing a configuration of the ThingPark Push module.
 */
@Entity
@Table(name = "parking_slot")
public class ParkingSlot {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID ref;

    public UUID getRef() {
		return ref;
	}

	public void setRef(UUID ref) {
		this.ref = ref;
	}

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ParkingLot parkingLot;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ParkingSlotTypeEnum type;

    @Column(name = "slot_name", columnDefinition = "VARCHAR(128)", nullable = false)
    private String slotName;

    @Column(name = "license_plate_parked_vehicle", columnDefinition = "VARCHAR(128)", nullable = true)
    private String licensePlateParkedVehicle = null;

    @Column(name = "park_time", columnDefinition = "DATETIME", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date parkTime;
    
    @Column(name = "price_computing_time", columnDefinition = "DATETIME", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date priceComputingTime;
    
    public ParkingLot getParkingLot() { return parkingLot; }

    public void setParkingLot(ParkingLot parkingLot) { this.parkingLot = parkingLot; }


    public ParkingSlotTypeEnum getType() {
        return type;
    }

    public ParkingSlot type(ParkingSlotTypeEnum type) {
        this.type = type;
        return this;
    }

    public void setType(ParkingSlotTypeEnum type) {
        this.type = type;
    }

    public String getLicensePlateParkedVehicle() {
        return licensePlateParkedVehicle;
    }

    public ParkingSlot licensePlateParkedVehicle(String licensePlateParkedVehicle) {
        this.licensePlateParkedVehicle = licensePlateParkedVehicle;
        return this;
    }
    
    public void setLicensePlateParkedVehicle(String licensePlateParkedVehicle) {
        this.licensePlateParkedVehicle = licensePlateParkedVehicle;
    }

    public String getSlotName() {
        return slotName;
    }

    public ParkingSlot slotName(String slotName) {
        this.slotName = slotName;
        return this;
    }
    
    public void setSlotName(String slotName) {
        this.slotName = slotName;
    }

    public Date getParkTime() {
        return parkTime;
    }
    
    public ParkingSlot parkTime(Date parkTime) {
        this.parkTime = parkTime;
        return this;
    }
    
    public void setParkTime(Date parkTime) {
        this.parkTime = parkTime;
    }

    public Date getPriceComputingTime() {
        return priceComputingTime;
    }
    
    public ParkingSlot priceComputingTime(Date priceComputingTime) {
        this.priceComputingTime = priceComputingTime;
        return this;
    }
    
    public void setPriceComputingTime(Date priceComputingTime) {
        this.priceComputingTime = priceComputingTime;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ref);
    }

    @Override
    public String toString() {
        return String.format("ParkingSlot [slotName=%s, type=%s, licensePlateParkedVehicle=%s, parkTime=%s]", 
            getSlotName(), getType(), getLicensePlateParkedVehicle(), (getParkTime()!=null)?getParkTime().toString():null);
    }
}