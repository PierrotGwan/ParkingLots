package fr.gwan.parkinglots.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
import fr.gwan.parkinglots.domain.ParkingLot;
import fr.gwan.parkinglots.domain.ParkingSlot;
import fr.gwan.parkinglots.domain.ParkingSlotTypeEnum;
import fr.gwan.parkinglots.domain.PricingPolicy;
import fr.gwan.parkinglots.domain.converter.ParkingLotConverter;
import fr.gwan.parkinglots.repository.ParkingLotRepository;

@RunWith(SpringRunner.class)
@WebMvcTest(ParkingLotsController.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ParkingLotControllerTest extends TestBase {

	private static final String BASE_PATH = "/admin/parkingLots";

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ParkingLotRepository repository;

	@Autowired
	private ParkingLotConverter converter;

	@Autowired
	private ObjectMapper jsonMapper;

	@Test
	@Transactional
	public void getAllParkingLots() throws Exception {
		ParkingLot parkingLot = buildParkingLot();

		repository.saveAndFlush(parkingLot);

		mvc.perform(get(BASE_PATH)
				.accept("application/json"))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.[*].ref").value(hasItem(parkingLot.getRef().toString())))
		.andExpect(jsonPath("$.[*].name").value(hasItem(PARKING_LOT_NAME_TEST)))
		.andExpect(jsonPath("$.[*].parkingSlots[*].name").value(hasItem(String.format(PARKING_SLOT_NAME_TEST, 1))))
		.andExpect(jsonPath("$.[*].pricingPolicy.sedanPricingPolicy").value(hasItem(PRICING_POLICY_SEDAN_TEST)))
		.andExpect(jsonPath("$.[*].pricingPolicy.20kwPricingPolicy").value(hasItem(PRICING_POLICY_20KW_TEST)))
		.andExpect(jsonPath("$.[*].pricingPolicy.50kwPricingPolicy").value(hasItem(PRICING_POLICY_50KW_TEST)));
	}

	@Test
	@Transactional
	public void createParkingLot() throws Exception {
		int databaseSizeBeforeCreate = repository.findAll().size();

		// Create the ModuleConfig
		ParkingLot parkingLot = buildParkingLot();

		mvc.perform(post(BASE_PATH)
				.accept("application/json")//
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonMapper.writeValueAsBytes(converter.toApi(parkingLot))))
		.andExpect(status().isCreated());

		// Validate the ModuleConfig in the database
		List<ParkingLot> parkingLotsList = repository.findAll();
		assertThat(parkingLotsList).hasSize(databaseSizeBeforeCreate + 1);
		ParkingLot testParkingLot = parkingLotsList.get(parkingLotsList.size() - 1);
		assertThat(testParkingLot.getName()).isEqualTo(PARKING_LOT_NAME_TEST);
		assertThat(testParkingLot.getParkingSlots().size()).isEqualTo(parkingLot.getParkingSlots().size());
	}


	@Test
	@Transactional
	public void createParkingLotWithExistingRef() throws Exception {
		int databaseSizeBeforeCreate = (int) repository.count();

		// Create the ModuleConfig
		ParkingLot parkingLot = buildParkingLot().ref(UUID.randomUUID());
		mvc.perform(post(BASE_PATH)
				.accept("application/json")//
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonMapper.writeValueAsBytes(converter.toApi(parkingLot))))
		.andExpect(status().isBadRequest());

		// Validate the ModuleConfig in the database
		List<ParkingLot> parkingLotList = repository.findAll();
		assertThat(parkingLotList).hasSize(databaseSizeBeforeCreate);
	}

	@Test
	@Transactional
	public void createParkingLotWithEmptyParkingSlotList() throws Exception {
		int databaseSizeBeforeCreate = (int) repository.count();

		// Create the ModuleConfig
		@SuppressWarnings("unchecked")
		ParkingLot parkingLot = buildParkingLot().parkingSlots(Collections.EMPTY_SET);
		mvc.perform(post(BASE_PATH)
				.accept("application/json")//
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonMapper.writeValueAsBytes(converter.toApi(parkingLot))))
		.andExpect(status().isBadRequest());

		// Validate the ModuleConfig in the database
		List<ParkingLot> parkingLotList = repository.findAll();
		assertThat(parkingLotList).hasSize(databaseSizeBeforeCreate);
	}

	@Test
	@Transactional
	public void getParkingLot() throws Exception {
		// Initialize the database
		ParkingLot parkingLot = buildParkingLot();

		repository.saveAndFlush(parkingLot);

		// Get the moduleConfig
		String ref = parkingLot.getRef().toString();
		mvc.perform(get(BASE_PATH + "/{ref}", ref)
				.accept("application/json"))
		.andExpect(status().isOk())//
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.ref").value(ref))
		.andExpect(jsonPath("$.name").value(PARKING_LOT_NAME_TEST))
		.andExpect(jsonPath("$.parkingSlots[*].name").value(hasItem(String.format(PARKING_SLOT_NAME_TEST, 1))))
		.andExpect(jsonPath("$.pricingPolicy.sedanPricingPolicy").value(PRICING_POLICY_SEDAN_TEST))
		.andExpect(jsonPath("$.pricingPolicy.20kwPricingPolicy").value(PRICING_POLICY_20KW_TEST))
		.andExpect(jsonPath("$.pricingPolicy.50kwPricingPolicy").value(PRICING_POLICY_50KW_TEST));
	}

	
    @Test
    @Transactional
    public void getNonExistingParkingLot() throws Exception {
        // Get the moduleConfig
        String ref = UUID.randomUUID().toString();
		mvc.perform(get(BASE_PATH + "/{ref}", ref)
				.accept("application/json"))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void deleteParkingLot() throws Exception {
        // Initialize the database
		ParkingLot parkingLot = buildParkingLot();

		repository.saveAndFlush(parkingLot);
        int databaseSizeBeforeDelete = (int) repository.count();

        UUID ref = parkingLot.getRef();
        // Delete the moduleConfig
        mvc.perform(delete(BASE_PATH + "/{ref}", ref.toString()))
        		.andExpect(status().isNoContent());

        // Validate the database is empty
        List<ParkingLot> parkingLotList = repository.findAll();
        assertThat(parkingLotList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void deleteNonExistingParkingLot() throws Exception {
        String ref = UUID.randomUUID().toString();

        // Delete the moduleConfig
        mvc.perform(delete(BASE_PATH + "/{ref}", ref.toString()))
            .andExpect(status().isNotFound());
    }

	private static ParkingLot buildParkingLot() {
		ParkingLot parkingLot = new ParkingLot()
				.name(PARKING_LOT_NAME_TEST)
				.lastUpdate(new Date())
				.pricingPolicy(new PricingPolicy()
						.sedanPricingPolicy(PRICING_POLICY_SEDAN_TEST)
						._20kwPricingPolicy(PRICING_POLICY_20KW_TEST)
						._50kwPricingPolicy(PRICING_POLICY_50KW_TEST));
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
}