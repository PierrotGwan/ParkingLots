package fr.gwan.parkinglots.domain.converter;

import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import fr.gwan.parkinglots.domain.ParkingSlotTypeEnum;
import fr.gwan.parkinglots.domain.ParkingSlot;

@Component
public class ParkingSlotConverter
    implements EntityConverter<fr.gwan.parkinglots.api.model.ParkingSlot, ParkingSlot> {


    @Override
    public ParkingSlot toEntity(fr.gwan.parkinglots.api.model.ParkingSlot parkingSlotApi) {
    	ParkingSlot parkingSlotEntity = new ParkingSlot();
    	parkingSlotEntity.setLicensePlateParkedVehicle(parkingSlotApi.getLicensePlateParkedVehicle());
    	parkingSlotEntity.setSlotName(parkingSlotApi.getName());
    	parkingSlotEntity.setType(ParkingSlotTypeEnum.fromValue(parkingSlotApi.getParkingSlotType().toString()));
    	parkingSlotEntity.setParkTime(parkingSlotApi.getParkTime()!=null?Date.from(parkingSlotApi.getParkTime().toInstant()):null);
        return parkingSlotEntity;
    }

    @Override
    public fr.gwan.parkinglots.api.model.ParkingSlot toApi(ParkingSlot parkingSlotEntity) {
    	fr.gwan.parkinglots.api.model.ParkingSlot parkingSlotApi = new fr.gwan.parkinglots.api.model.ParkingSlot();
    	parkingSlotApi.setName(parkingSlotEntity.getSlotName());
    	parkingSlotApi.setParkingSlotType(fr.gwan.parkinglots.api.model.ParkingSlot.ParkingSlotTypeEnum.fromValue(parkingSlotEntity.getType().toString()));
    	parkingSlotApi.setLicensePlateParkedVehicle(parkingSlotEntity.getLicensePlateParkedVehicle());
    	parkingSlotApi.setParkTime(parkingSlotEntity.getParkTime()!=null?parkingSlotEntity.getParkTime().toInstant().atOffset(ZoneOffset.UTC):null);
        return parkingSlotApi;
    }

}