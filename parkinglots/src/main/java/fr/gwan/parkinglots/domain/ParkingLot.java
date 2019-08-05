package fr.gwan.parkinglots.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;


/**
 * Parking lot persistence class.
 */
@Entity
@Table(name = "parking_lot")
@Inheritance(strategy = InheritanceType.JOINED)
public class ParkingLot implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID ref;

    @Column(name = "name", columnDefinition = "CHAR(20)", nullable = false)
    private String name;

    @Column(name = "last_update", columnDefinition = "DATETIME", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;
    
    @OneToMany(mappedBy = "parkingLot", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ParkingSlot> parkingSlots = new HashSet<>();

    
    @OneToOne(cascade=CascadeType.REMOVE)
    private ParkingLotPricingPolicy parkingLotPricingPolicy;
    
    public UUID getRef() {
        return ref;
    }

    public ParkingLot ref(UUID ref) {
        this.ref = ref;
        return this;
    }

    public void setRef(UUID ref) {
        this.ref = ref;
    }

    public String getName() {
        return name;
    }
    
    public ParkingLot name(String name) {
        this.name = name;
        return this;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Date getLastUpdate() {
        return lastUpdate;
    }
    
    public ParkingLot lastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
        return this;
    }
    
    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void addParkingSlot(ParkingSlot parkingSlot) {
    	parkingSlots.add(parkingSlot);
        parkingSlot.setParkingLot(this);
    }

    public void removeParkingSlot(ParkingSlot parkingSlot) {
    	parkingSlots.remove(parkingSlot);
    	parkingSlot.setParkingLot(null);
    }

    public Set<ParkingSlot> getParkingSlots() {
        return parkingSlots;
    }

    public void setParkingSlots(Set<ParkingSlot> parkingSlots) {
        this.parkingSlots = parkingSlots;
    }

    public ParkingLotPricingPolicy getParkingLotPricingPolicy() {
        return parkingLotPricingPolicy;
    }
    
    public ParkingLot parkingLotPricingPolicy(ParkingLotPricingPolicy parkingLotPricingPolicy) {
        this.parkingLotPricingPolicy = parkingLotPricingPolicy;
        return this;
    }
    
    public void setParkingLotPricingPolicy(ParkingLotPricingPolicy parkingLotPricingPolicy) {
        this.parkingLotPricingPolicy = parkingLotPricingPolicy;
    }
    

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ParkingLot parkingLot = (ParkingLot) o;
        if (parkingLot.ref == null || ref == null) {
            return false;
        }
        return Objects.equals(ref, parkingLot.ref);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(ref);
    }

    @Override
    public String toString() {
        return String.format("ParkingLot [ref=%s, parkingSlots=%s, parkingLotPricingPolicy=%s]", ref, parkingSlots, parkingLotPricingPolicy);
    }
}