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
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "pricing_policy")
public class PricingPolicy implements Serializable {

    private static final long serialVersionUID = 2L;

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

    @Column(name = "payment_timeout", columnDefinition = "INT", nullable = false)
    private Integer paymentTimeout;

    @Column(name = "exit_timeout", columnDefinition = "INT", nullable = false)
    private Integer exitTimeout;

    @Version
    private long version;

    public String getSedanPricingPolicy() {
        return sedanPricingPolicy;
    }

    public PricingPolicy sedanPricingPolicy(String sedanPricingPolicy) {
        this.sedanPricingPolicy = sedanPricingPolicy;
        return this;
    }
    
    public void setSedanPricingPolicy(String sedanPricingPolicy) {
        this.sedanPricingPolicy = sedanPricingPolicy;
    }

    public String get20kwPricingPolicy() {
        return _20kwPricingPolicy;
    }

    public PricingPolicy _20kwPricingPolicy(String _20kwPricingPolicy) {
        this._20kwPricingPolicy = _20kwPricingPolicy;
        return this;
    }
    
    public void set20kwPricingPolicy(String _20kwPricingPolicy) {
        this._20kwPricingPolicy = _20kwPricingPolicy;
    }

    public String get50kwPricingPolicy() {
        return _50kwPricingPolicy;
    }

    public PricingPolicy _50kwPricingPolicy(String _50kwPricingPolicy) {
        this._50kwPricingPolicy = _50kwPricingPolicy;
        return this;
    }
    
    public void set50kwPricingPolicy(String _50kwPricingPolicy) {
        this._50kwPricingPolicy = _50kwPricingPolicy;
    }

    public Integer getPaymentTimeout() {
        return paymentTimeout;
    }

    public PricingPolicy paymentTimeout(Integer paymentTimeout) {
        this.paymentTimeout = paymentTimeout;
        return this;
    }
    
    public void setPaymentTimeout(Integer paymentTimeout) {
        this.paymentTimeout = paymentTimeout;
    }

    public Integer getExitTimeout() {
        return exitTimeout;
    }

    public PricingPolicy exitTimeout(Integer exitTimeout) {
        this.exitTimeout = exitTimeout;
        return this;
    }
    
    public void setExitTimeout(Integer exitTimeout) {
        this.exitTimeout = exitTimeout;
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
        return String.format("PricingPolicy [sedanPricingPolicy=%s, _20kwPricingPolicy=%s, _50kwPricingPolicy=%s, paymentTimeout=%d, exitTimeout=%d]", 
            getSedanPricingPolicy(), get20kwPricingPolicy(), get50kwPricingPolicy(), getPaymentTimeout(), getExitTimeout());
    }
}