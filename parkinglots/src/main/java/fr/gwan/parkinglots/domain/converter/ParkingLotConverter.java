package fr.gwan.parkinglots.domain.converter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import fr.gwan.parkinglots.domain.ParkingLot;
import fr.gwan.parkinglots.domain.ParkingSlot;

@Component
public class ParkingLotConverter
    implements EntityConverter<fr.gwan.parkinglots.api.model.ParkingLot, ParkingLot> {

    private ParkingSlotConverter parkingSlotConverter;
    private PricingPolicyConverter pricingPolicyConverter;

    public ParkingLotConverter(ParkingSlotConverter parkingSlotConverter, PricingPolicyConverter pricingPolicyConverter) {
        this.parkingSlotConverter = parkingSlotConverter;
        this.pricingPolicyConverter = pricingPolicyConverter;
    }

    @Override
    public ParkingLot toEntity(fr.gwan.parkinglots.api.model.ParkingLot parkingLotApi) {
    	ParkingLot parkingLotEntity = new ParkingLot();
    	parkingLotEntity.setRef(map(parkingLotApi.getRef()));
    	parkingLotEntity.setName(parkingLotApi.getName());
    	parkingLotEntity.setParkingSlots(new HashSet<ParkingSlot>(parkingSlotConverter.toEntity(parkingLotApi.getParkingSlots())));
    	parkingLotEntity.setPricingPolicy(pricingPolicyConverter.toEntity(parkingLotApi.getPricingPolicy()));
        return parkingLotEntity;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public fr.gwan.parkinglots.api.model.ParkingLot toApi(ParkingLot parkingLotEntity) {
    	fr.gwan.parkinglots.api.model.ParkingLot parkingLotApi = new fr.gwan.parkinglots.api.model.ParkingLot();
    	parkingLotApi.setRef(map(parkingLotEntity.getRef()));
    	parkingLotApi.setName(parkingLotEntity.getName());
    	parkingLotApi.setParkingSlots(parkingSlotConverter.toApi(new ArrayList(parkingLotEntity.getParkingSlots())));
    	parkingLotApi.setPricingPolicy(pricingPolicyConverter.toApi(parkingLotEntity.getPricingPolicy()));
        return parkingLotApi;
    }
}