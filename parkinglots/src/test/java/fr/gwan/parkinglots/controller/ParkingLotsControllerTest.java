package fr.gwan.parkinglots.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.gwan.parkinglots.TestBase;
import fr.gwan.parkinglots.api.model.Vehicle;
import fr.gwan.parkinglots.domain.ParkingLot;
import fr.gwan.parkinglots.domain.ParkingSlot;
import fr.gwan.parkinglots.domain.ParkingSlotTypeEnum;
import fr.gwan.parkinglots.domain.converter.ParkingLotConverter;
import fr.gwan.parkinglots.repository.ParkingLotRepository;
import fr.gwan.parkinglots.repository.ParkingSlotRepository;

@RunWith(SpringRunner.class)
@WebMvcTest(ParkingLotsAdminController.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ParkingLotsControllerTest extends TestBase {

	private static final String BASE_PATH = "/parkingLots";

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ParkingLotRepository parkingLotRepository;

	@Autowired
	private ParkingSlotRepository parkingSlotRepository;

	@Autowired
	private ParkingLotConverter converter;

	@Autowired
	private ObjectMapper jsonMapper;

	@Test
	@Transactional
	public void enterParkingLot() throws Exception {
		ParkingLot parkingLot = buildParkingLot();

		parkingLotRepository.saveAndFlush(parkingLot);

		Vehicle vehicle = buildVehicle();
		String ref = parkingLot.getRef().toString();
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH");
		df.setTimeZone(tz);
		mvc.perform(post(BASE_PATH + "/{ref}/vehicle", ref)			
				.accept("application/json")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonMapper.writeValueAsBytes(vehicle)))
		.andExpect(status().isCreated())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.name").value(containsString(PARKING_SLOT_NAME_TEST.substring(0, PARKING_SLOT_NAME_TEST.length()-3))))
		.andExpect(jsonPath("$.licensePlateParkedVehicle").value(VEHICLE_LICENSE_PLATE_TEST))
		.andExpect(jsonPath("$.parkTime").value(containsString(df.format(new Date()))));
	}

	@Test
	@Transactional
	public void enterNonExistingParkingLot() throws Exception {

		Vehicle vehicle = buildVehicle();
		String ref = UUID.randomUUID().toString();
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH");
		df.setTimeZone(tz);
		mvc.perform(post(BASE_PATH + "/{ref}/vehicle", ref)			
				.accept("application/json")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonMapper.writeValueAsBytes(vehicle)))
		.andExpect(status().isNotFound());
	}

	@Test
	@Transactional
	public void requestPayment() throws Exception {
		ParkingLot parkingLot = buildParkingLot();

		parkingLotRepository.saveAndFlush(parkingLot);

		String ref = parkingLot.getRef().toString();
		
		ParkingSlot parkingSlot = parkingSlotRepository.findAllWithEagerRelationships(parkingLot.getRef(), ParkingSlotTypeEnum.PARKING_SLOT_SEDAN).get(0);
		parkingSlot.setLicensePlateParkedVehicle(VEHICLE_LICENSE_PLATE_TEST);
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, -5);
		parkingSlot.setParkTime(calendar.getTime());
		
		parkingSlotRepository.saveAndFlush(parkingSlot);
		
		mvc.perform(get(BASE_PATH + "/{ref}/vehicle/{licencePlate}/payment", ref, VEHICLE_LICENSE_PLATE_TEST)			
				.accept("application/json")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.price").isNumber())
		.andExpect(jsonPath("$.nbHours").value(closeTo(new BigDecimal(5),new BigDecimal(1))))
		.andExpect(jsonPath("$.pricingPolicy.sedanPricingPolicy").value(PRICING_POLICY_SEDAN_TEST))
		.andExpect(jsonPath("$.pricingPolicy.20kwPricingPolicy").value(PRICING_POLICY_20KW_TEST))
		.andExpect(jsonPath("$.pricingPolicy.50kwPricingPolicy").value(PRICING_POLICY_50KW_TEST))
		.andExpect(jsonPath("$.pricingPolicy.paymentTimeout").value(PRICING_POLICY_PAYMENT_TIMEOUT_TEST))
		.andExpect(jsonPath("$.pricingPolicy.exitTimeout").value(PRICING_POLICY_EXIT_TIMEOUT_TEST));
	}

	@Test
	@Transactional
	public void requestPaymentFor20kwVehicle() throws Exception {
		ParkingLot parkingLot = buildParkingLot();

		parkingLotRepository.saveAndFlush(parkingLot);

		String ref = parkingLot.getRef().toString();
		
		ParkingSlot parkingSlot = parkingSlotRepository.findAllWithEagerRelationships(parkingLot.getRef(), ParkingSlotTypeEnum.PARKING_SLOT_20KW).get(0);
		parkingSlot.setLicensePlateParkedVehicle(VEHICLE_LICENSE_PLATE_TEST);
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, -5);
		parkingSlot.setParkTime(calendar.getTime());
		
		parkingSlotRepository.saveAndFlush(parkingSlot);
		
		mvc.perform(get(BASE_PATH + "/{ref}/vehicle/{licencePlate}/payment", ref, VEHICLE_LICENSE_PLATE_TEST)			
				.accept("application/json")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.price").isNumber())
		.andExpect(jsonPath("$.nbHours").value(closeTo(new BigDecimal(5),new BigDecimal(1))))
		.andExpect(jsonPath("$.pricingPolicy.sedanPricingPolicy").value(PRICING_POLICY_SEDAN_TEST))
		.andExpect(jsonPath("$.pricingPolicy.20kwPricingPolicy").value(PRICING_POLICY_20KW_TEST))
		.andExpect(jsonPath("$.pricingPolicy.50kwPricingPolicy").value(PRICING_POLICY_50KW_TEST))
		.andExpect(jsonPath("$.pricingPolicy.paymentTimeout").value(PRICING_POLICY_PAYMENT_TIMEOUT_TEST))
		.andExpect(jsonPath("$.pricingPolicy.exitTimeout").value(PRICING_POLICY_EXIT_TIMEOUT_TEST));
	}

	@Test
	@Transactional
	public void requestPaymentFor50kwVehicle() throws Exception {
		ParkingLot parkingLot = buildParkingLot();

		parkingLotRepository.saveAndFlush(parkingLot);

		String ref = parkingLot.getRef().toString();
		
		ParkingSlot parkingSlot = parkingSlotRepository.findAllWithEagerRelationships(parkingLot.getRef(), ParkingSlotTypeEnum.PARKING_SLOT_50KW).get(0);
		parkingSlot.setLicensePlateParkedVehicle(VEHICLE_LICENSE_PLATE_TEST);
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, -5);
		parkingSlot.setParkTime(calendar.getTime());
		
		parkingSlotRepository.saveAndFlush(parkingSlot);
		
		mvc.perform(get(BASE_PATH + "/{ref}/vehicle/{licencePlate}/payment", ref, VEHICLE_LICENSE_PLATE_TEST)			
				.accept("application/json")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.price").isNumber())
		.andExpect(jsonPath("$.nbHours").value(closeTo(new BigDecimal(5),new BigDecimal(1))))
		.andExpect(jsonPath("$.pricingPolicy.sedanPricingPolicy").value(PRICING_POLICY_SEDAN_TEST))
		.andExpect(jsonPath("$.pricingPolicy.20kwPricingPolicy").value(PRICING_POLICY_20KW_TEST))
		.andExpect(jsonPath("$.pricingPolicy.50kwPricingPolicy").value(PRICING_POLICY_50KW_TEST))
		.andExpect(jsonPath("$.pricingPolicy.paymentTimeout").value(PRICING_POLICY_PAYMENT_TIMEOUT_TEST))
		.andExpect(jsonPath("$.pricingPolicy.exitTimeout").value(PRICING_POLICY_EXIT_TIMEOUT_TEST));
	}

	@Test
	@Transactional
	public void requestPaymentForNonExistingVehicle() throws Exception {
		ParkingLot parkingLot = buildParkingLot();

		parkingLotRepository.saveAndFlush(parkingLot);

		String ref = parkingLot.getRef().toString();
		
		
		mvc.perform(get(BASE_PATH + "/{ref}/vehicle/{licencePlate}/payment", ref, VEHICLE_LICENSE_PLATE_TEST)			
				.accept("application/json")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound());
	}

	@Test
	@Transactional
	public void reportPayment() throws Exception {
		ParkingLot parkingLot = buildParkingLot();

		parkingLotRepository.saveAndFlush(parkingLot);

		String ref = parkingLot.getRef().toString();
		
		ParkingSlot parkingSlot = parkingSlotRepository.findAllWithEagerRelationships(parkingLot.getRef(), ParkingSlotTypeEnum.PARKING_SLOT_SEDAN).get(0);
		parkingSlot.setLicensePlateParkedVehicle(VEHICLE_LICENSE_PLATE_TEST);
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -parkingLot.getPricingPolicy().getPaymentTimeout() / 60);
		parkingSlot.setPriceComputingTime(calendar.getTime());
		calendar.add(Calendar.HOUR_OF_DAY, -5);
		parkingSlot.setParkTime(calendar.getTime());
		
		parkingSlotRepository.saveAndFlush(parkingSlot);
		
		mvc.perform(post(BASE_PATH + "/{ref}/vehicle/{licencePlate}/payment", ref, VEHICLE_LICENSE_PLATE_TEST)			
				.accept("application/json")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isCreated());
	}

	@Test
	@Transactional
	public void reportPaymentForNonExistingVehicle() throws Exception {
		ParkingLot parkingLot = buildParkingLot();

		parkingLotRepository.saveAndFlush(parkingLot);

		String ref = parkingLot.getRef().toString();
		
		mvc.perform(post(BASE_PATH + "/{ref}/vehicle/{licencePlate}/payment", ref, VEHICLE_LICENSE_PLATE_TEST)			
				.accept("application/json")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound());
	}

	@Test
	@Transactional
	public void reportPaymentTooLate() throws Exception {
		ParkingLot parkingLot = buildParkingLot();

		parkingLotRepository.saveAndFlush(parkingLot);

		String ref = parkingLot.getRef().toString();
		
		ParkingSlot parkingSlot = parkingSlotRepository.findAllWithEagerRelationships(parkingLot.getRef(), ParkingSlotTypeEnum.PARKING_SLOT_SEDAN).get(0);
		parkingSlot.setLicensePlateParkedVehicle(VEHICLE_LICENSE_PLATE_TEST);
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -(parkingLot.getPricingPolicy().getPaymentTimeout() / 60 + 1));
		parkingSlot.setPriceComputingTime(calendar.getTime());
		calendar.add(Calendar.HOUR_OF_DAY, -5);
		parkingSlot.setParkTime(calendar.getTime());
		
		parkingSlotRepository.saveAndFlush(parkingSlot);
		
		mvc.perform(post(BASE_PATH + "/{ref}/vehicle/{licencePlate}/payment", ref, VEHICLE_LICENSE_PLATE_TEST)			
				.accept("application/json")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isRequestTimeout());
	}

	@Test
	@Transactional
	public void exitVehicle() throws Exception {
		ParkingLot parkingLot = buildParkingLot();

		parkingLotRepository.saveAndFlush(parkingLot);

		String ref = parkingLot.getRef().toString();
		
		ParkingSlot parkingSlot = parkingSlotRepository.findAllWithEagerRelationships(parkingLot.getRef(), ParkingSlotTypeEnum.PARKING_SLOT_SEDAN).get(0);
		parkingSlot.setLicensePlateParkedVehicle(VEHICLE_LICENSE_PLATE_TEST);
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -parkingLot.getPricingPolicy().getExitTimeout() / 60);
		parkingSlot.setParkTime(calendar.getTime());
		
		parkingSlotRepository.saveAndFlush(parkingSlot);
		
		mvc.perform(delete(BASE_PATH + "/{ref}/vehicle/{licencePlate}", ref, VEHICLE_LICENSE_PLATE_TEST)			
				.accept("application/json")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isNoContent());
	}

	@Test
	@Transactional
	public void exitNonExistingVehicle() throws Exception {
		ParkingLot parkingLot = buildParkingLot();

		parkingLotRepository.saveAndFlush(parkingLot);

		String ref = parkingLot.getRef().toString();
		
		mvc.perform(delete(BASE_PATH + "/{ref}/vehicle/{licencePlate}", ref, VEHICLE_LICENSE_PLATE_TEST)			
				.accept("application/json")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound());
	}

	@Test
	@Transactional
	public void exitVehicleTooLate() throws Exception {
		ParkingLot parkingLot = buildParkingLot();

		parkingLotRepository.saveAndFlush(parkingLot);

		String ref = parkingLot.getRef().toString();
		
		ParkingSlot parkingSlot = parkingSlotRepository.findAllWithEagerRelationships(parkingLot.getRef(), ParkingSlotTypeEnum.PARKING_SLOT_SEDAN).get(0);
		parkingSlot.setLicensePlateParkedVehicle(VEHICLE_LICENSE_PLATE_TEST);
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -(parkingLot.getPricingPolicy().getExitTimeout() / 60 + 1));
		parkingSlot.setParkTime(calendar.getTime());
		
		parkingSlotRepository.saveAndFlush(parkingSlot);
		
		mvc.perform(delete(BASE_PATH + "/{ref}/vehicle/{licencePlate}", ref, VEHICLE_LICENSE_PLATE_TEST)			
				.accept("application/json")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isRequestTimeout());
	}

	/*@Test
	@Transactional
	public void createParkingLot() throws Exception {
		int databaseSizeBeforeCreate = parkingLotRepository.findAll().size();

		// Create the ModuleConfig
		ParkingLot parkingLot = buildParkingLot();

		mvc.perform(post(BASE_PATH)
				.accept("application/json")//
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonMapper.writeValueAsBytes(converter.toApi(parkingLot))))
		.andExpect(status().isCreated());

		// Validate the ModuleConfig in the database
		List<ParkingLot> parkingLotsList = parkingLotRepository.findAll();
		assertThat(parkingLotsList).hasSize(databaseSizeBeforeCreate + 1);
		ParkingLot testParkingLot = parkingLotsList.get(parkingLotsList.size() - 1);
		assertThat(testParkingLot.getName()).isEqualTo(PARKING_LOT_NAME_TEST);
		assertThat(testParkingLot.getParkingSlots().size()).isEqualTo(parkingLot.getParkingSlots().size());
	}


	@Test
	@Transactional
	public void createParkingLotWithExistingRef() throws Exception {
		int databaseSizeBeforeCreate = (int) parkingLotRepository.count();

		// Create the ModuleConfig
		ParkingLot parkingLot = buildParkingLot().ref(UUID.randomUUID());
		mvc.perform(post(BASE_PATH)
				.accept("application/json")//
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonMapper.writeValueAsBytes(converter.toApi(parkingLot))))
		.andExpect(status().isBadRequest());

		// Validate the ModuleConfig in the database
		List<ParkingLot> parkingLotList = parkingLotRepository.findAll();
		assertThat(parkingLotList).hasSize(databaseSizeBeforeCreate);
	}

	@Test
	@Transactional
	public void createParkingLotWithEmptyParkingSlotList() throws Exception {
		int databaseSizeBeforeCreate = (int) parkingLotRepository.count();

		// Create the ModuleConfig
		@SuppressWarnings("unchecked")
		ParkingLot parkingLot = buildParkingLot().parkingSlots(Collections.EMPTY_SET);
		mvc.perform(post(BASE_PATH)
				.accept("application/json")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonMapper.writeValueAsBytes(converter.toApi(parkingLot))))
		.andExpect(status().isBadRequest());

		// Validate the ModuleConfig in the database
		List<ParkingLot> parkingLotList = parkingLotRepository.findAll();
		assertThat(parkingLotList).hasSize(databaseSizeBeforeCreate);
	}

	@Test
	@Transactional
	public void getParkingLot() throws Exception {
		// Initialize the database
		ParkingLot parkingLot = buildParkingLot();

		parkingLotRepository.saveAndFlush(parkingLot);

		// Get the moduleConfig
		String ref = parkingLot.getRef().toString();
		mvc.perform(get(BASE_PATH + "/{ref}", ref)
				.accept("application/json")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.ref").value(ref))
		.andExpect(jsonPath("$.name").value(PARKING_LOT_NAME_TEST))
		.andExpect(jsonPath("$.parkingSlots[*].name").value(hasItem(String.format(PARKING_SLOT_NAME_TEST, 1))))
		.andExpect(jsonPath("$.pricingPolicy.sedanPricingPolicy").value(PRICING_POLICY_SEDAN_TEST))
		.andExpect(jsonPath("$.pricingPolicy.20kwPricingPolicy").value(PRICING_POLICY_20KW_TEST))
		.andExpect(jsonPath("$.pricingPolicy.50kwPricingPolicy").value(PRICING_POLICY_50KW_TEST))
		.andExpect(jsonPath("$.pricingPolicy.paymentTimeout").value(PRICING_POLICY_PAYMENT_TIMEOUT_TEST))
		.andExpect(jsonPath("$.pricingPolicy.exitTimeout").value(PRICING_POLICY_EXIT_TIMEOUT_TEST));
	}

	
    @Test
    @Transactional
    public void getNonExistingParkingLot() throws Exception {
        // Get the moduleConfig
        String ref = UUID.randomUUID().toString();
		mvc.perform(get(BASE_PATH + "/{ref}", ref)
				.accept("application/json")
				.contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void deleteParkingLot() throws Exception {
        // Initialize the database
		ParkingLot parkingLot = buildParkingLot();

		parkingLotRepository.saveAndFlush(parkingLot);
        int databaseSizeBeforeDelete = (int) parkingLotRepository.count();

        UUID ref = parkingLot.getRef();
        // Delete the moduleConfig
        mvc.perform(delete(BASE_PATH + "/{ref}", ref.toString())
				.accept("application/json")
				.contentType(MediaType.APPLICATION_JSON))
        	.andExpect(status().isNoContent());

        // Validate the database is empty
        List<ParkingLot> parkingLotList = parkingLotRepository.findAll();
        assertThat(parkingLotList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void deleteNonExistingParkingLot() throws Exception {
        String ref = UUID.randomUUID().toString();

        // Delete the moduleConfig
        mvc.perform(delete(BASE_PATH + "/{ref}", ref.toString())				
        		.accept("application/json")
				.contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }*/
	
}