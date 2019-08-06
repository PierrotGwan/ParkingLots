package fr.gwan.parkinglots.domain;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "parking_lot_pricing_policy")
public class ParkingLotPricingPolicy implements Serializable {

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

	@OneToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ParkingLot parkingLot;

    @Column(name = "sedan_pricing_policy", columnDefinition = "VARCHAR(512)", nullable = false)
    private String sedanPricingPolicy;

    @Column(name = "20kw_pricing_policy", columnDefinition = "VARCHAR(512)", nullable = false)
    private String _20kwPricingPolicy;

    @Column(name = "50kw_pricing_policy", columnDefinition = "VARCHAR(512)", nullable = false)
    private String _50kwPricingPolicy;

    public String getSedanPricingPolicy() {
        return sedanPricingPolicy;
    }

    public ParkingLotPricingPolicy sedanPricingPolicy(String sedanPricingPolicy) {
        this.sedanPricingPolicy = sedanPricingPolicy;
        return this;
    }
    
    public void setSedanPricingPolicy(String sedanPricingPolicy) {
        this.sedanPricingPolicy = sedanPricingPolicy;
    }

    public String get20kwPricingPolicy() {
        return _20kwPricingPolicy;
    }

    public ParkingLotPricingPolicy _20kwPricingPolicy(String _20kwPricingPolicy) {
        this._20kwPricingPolicy = _20kwPricingPolicy;
        return this;
    }
    
    public void set20kwPricingPolicy(String _20kwPricingPolicy) {
        this._20kwPricingPolicy = _20kwPricingPolicy;
    }

    public String get50kwPricingPolicy() {
        return _50kwPricingPolicy;
    }

    public ParkingLotPricingPolicy _50kwPricingPolicy(String _50kwPricingPolicy) {
        this._50kwPricingPolicy = _50kwPricingPolicy;
        return this;
    }
    
    public void set50kwPricingPolicy(String _50kwPricingPolicy) {
        this._50kwPricingPolicy = _50kwPricingPolicy;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return String.format("ParkingLotPricingPolicy [sedanPricingPolicy=%s, _20kwPricingPolicy=%s, _50kwPricingPolicy=%s]", 
            getSedanPricingPolicy(), get20kwPricingPolicy(), get50kwPricingPolicy());
    }
}