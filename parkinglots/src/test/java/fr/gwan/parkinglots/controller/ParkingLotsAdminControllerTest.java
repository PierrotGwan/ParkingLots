package fr.gwan.parkinglots.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import fr.gwan.parkinglots.TestBase;
import fr.gwan.parkinglots.domain.ParkingLot;
import fr.gwan.parkinglots.domain.ParkingSlot;
import fr.gwan.parkinglots.domain.ParkingSlotTypeEnum;
import fr.gwan.parkinglots.domain.PricingPolicy;
import fr.gwan.parkinglots.domain.converter.ParkingLotConverter;
import fr.gwan.parkinglots.repository.ParkingLotRepository;

@RunWith(SpringRunner.class)
@WebMvcTest(ParkingLotsAdminController.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ParkingLotsAdminControllerTest extends TestBase {

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
				.accept("application/json")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(jsonPath("$.[*].ref").value(hasItem(parkingLot.getRef().toString())))
		.andExpect(jsonPath("$.[*].name").value(hasItem(PARKING_LOT_NAME_TEST)))
		.andExpect(jsonPath("$.[*].parkingSlots[*].name").value(hasItem(String.format(PARKING_SLOT_NAME_TEST, 1))))
		.andExpect(jsonPath("$.[*].pricingPolicy.sedanPricingPolicy").value(hasItem(PRICING_POLICY_SEDAN_TEST)))
		.andExpect(jsonPath("$.[*].pricingPolicy.20kwPricingPolicy").value(hasItem(PRICING_POLICY_20KW_TEST)))
		.andExpect(jsonPath("$.[*].pricingPolicy.50kwPricingPolicy").value(hasItem(PRICING_POLICY_50KW_TEST)))
		.andExpect(jsonPath("$.[*].pricingPolicy.paymentTimeout").value(hasItem(PRICING_POLICY_PAYMENT_TIMEOUT_TEST)))
		.andExpect(jsonPath("$.[*].pricingPolicy.exitTimeout").value(hasItem(PRICING_POLICY_EXIT_TIMEOUT_TEST)));
	}

	@Test
	@Transactional
	public void createParkingLot() throws Exception, UnsupportedEncodingException {
		int databaseSizeBeforeCreate = repository.findAll().size();

		// Create the ModuleConfig
		ParkingLot parkingLot = buildParkingLot();

		MvcResult result = mvc.perform(post(BASE_PATH)
				.accept("application/json")//
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonMapper.writeValueAsBytes(converter.toApi(parkingLot))))
		.andExpect(status().isCreated())
		.andReturn();

		// Validate the ModuleConfig in the database
		List<ParkingLot> parkingLotsList = repository.findAll();
		assertThat(parkingLotsList).hasSize(databaseSizeBeforeCreate + 1);
		ParkingLot testParkingLot = parkingLotsList.stream()
				  .filter(checkParkingLot -> {
					try {
						return converter.map(JsonPath.parse(result.getResponse().getContentAsString()).read("$.ref").toString()).equals(checkParkingLot.getRef());
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return false;
				})
				  .findAny()
				  .orElse(null);
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
				.accept("application/json")
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

		repository.saveAndFlush(parkingLot);
        int databaseSizeBeforeDelete = (int) repository.count();

        UUID ref = parkingLot.getRef();
        // Delete the moduleConfig
        mvc.perform(delete(BASE_PATH + "/{ref}", ref.toString())
				.accept("application/json")
				.contentType(MediaType.APPLICATION_JSON))
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
        mvc.perform(delete(BASE_PATH + "/{ref}", ref.toString())				
        		.accept("application/json")
				.contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

}