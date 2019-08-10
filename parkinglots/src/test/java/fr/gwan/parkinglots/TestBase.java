package fr.gwan.parkinglots;

import java.util.Date;

import fr.gwan.parkinglots.api.model.ParkingSlotType;
import fr.gwan.parkinglots.api.model.Vehicle;
import fr.gwan.parkinglots.domain.ParkingLot;
import fr.gwan.parkinglots.domain.ParkingSlot;
import fr.gwan.parkinglots.domain.ParkingSlotTypeEnum;
import fr.gwan.parkinglots.domain.PricingPolicy;

public class TestBase {

    protected static final String PARKING_LOT_NAME_TEST = "Test parking lot";

    protected static final String PARKING_SLOT_NAME_TEST = "Test parking slot %d";


    protected static final String PRICING_POLICY_SEDAN_TEST = "10 * h";
    protected static final String PRICING_POLICY_20KW_TEST = "10.5 * h + 20";
	protected static final String PRICING_POLICY_50KW_TEST = "12 * h * h + 2";
	protected static final Integer PRICING_POLICY_PAYMENT_TIMEOUT_TEST = 300;
	protected static final Integer PRICING_POLICY_EXIT_TIMEOUT_TEST = 600;

	protected static final String VEHICLE_LICENSE_PLATE_TEST = "BV-765-CS";

	protected static ParkingLot buildParkingLot() {
		ParkingLot parkingLot = new ParkingLot()
				.name(PARKING_LOT_NAME_TEST)
				.lastUpdate(new Date())
				.pricingPolicy(new PricingPolicy()
						.sedanPricingPolicy(PRICING_POLICY_SEDAN_TEST)
						._20kwPricingPolicy(PRICING_POLICY_20KW_TEST)
						._50kwPricingPolicy(PRICING_POLICY_50KW_TEST)
						.paymentTimeout(PRICING_POLICY_PAYMENT_TIMEOUT_TEST)
						.exitTimeout(PRICING_POLICY_EXIT_TIMEOUT_TEST));
		for (int i = 0;i<15;i++)
		{
			ParkingSlotTypeEnum type;
			switch (i%3)
			{
			case 0: 
				type = ParkingSlotTypeEnum.PARKING_SLOT_SEDAN;
				break;
			case 1: 
				type = ParkingSlotTypeEnum.PARKING_SLOT_20KW;
				break;
			default: 
				type = ParkingSlotTypeEnum.PARKING_SLOT_50KW;
				break;
			}
			ParkingSlot parkingSlot = new ParkingSlot()
					.slotName(String.format(PARKING_SLOT_NAME_TEST, i))
					.type(type);
			parkingLot.addParkingSlot(parkingSlot);
		}
		return parkingLot;
	}
	protected static Vehicle buildVehicle() {
		Vehicle vehicle = new Vehicle()
				.licensePlate(VEHICLE_LICENSE_PLATE_TEST)
				.neededParkingSlotType(ParkingSlotType.SEDAN);
		return vehicle;
	}
}