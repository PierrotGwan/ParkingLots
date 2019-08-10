package fr.gwan.parkinglots.controller;

import fr.gwan.parkinglots.api.model.ParkingLot;
import fr.gwan.parkinglots.domain.converter.ParkingLotConverter;
import fr.gwan.parkinglots.repository.ParkingLotRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import io.swagger.api.ApiException;
import org.mariuszgromada.math.mxparser.Function;
import org.mariuszgromada.math.mxparser.Expression;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
public class ParkingLotsAdminController extends AbstractController {

    Logger log = LoggerFactory.getLogger(ParkingLotsAdminController.class);

    private final ParkingLotConverter converter;

    private final ParkingLotRepository repository;
    
    public ParkingLotsAdminController(ObjectMapper objectMapper, HttpServletRequest request, ParkingLotConverter converter, ParkingLotRepository repository) {
        super(objectMapper, request);
        this.repository = repository;
        this.converter = converter;
    }

    public Optional<ObjectMapper> getObjectMapper() {
        return Optional.ofNullable(objectMapper);
    }

    public Optional<HttpServletRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    public Optional<String> getAcceptHeader() {
        return getRequest().map(r -> r.getHeader("Accept"));
    }

    
    @ApiOperation(value = "Parking lot list retrieval", nickname = "adminParkingLotsGet", notes = "Retrieves the list of existing parking lots.", response = ParkingLot.class, responseContainer = "List", tags={ "ParkingLot", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Parking lots retrieved successfully.", response = ParkingLot.class, responseContainer = "List") })
    @RequestMapping(value = "/admin/parkingLots",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.GET)
    public ResponseEntity<List<ParkingLot>> adminParkingLotsGet() throws ResponseStatusException {
        try {
        	if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
                if (getAcceptHeader().get().contains("application/json")) {
                	List<fr.gwan.parkinglots.domain.ParkingLot> entityParkingLots = repository.findAll();
					return new ResponseEntity<List<ParkingLot>>(converter.toApi(entityParkingLots), HttpStatus.OK);
                }
        	}
        }
        catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Server error", e);
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @ApiOperation(value = "Parking lot deletion", nickname = "adminParkingLotsParkingLotRefDelete", notes = "Deletes the parking lot corresponding to the provided ref. The parking lot must be empty (i.e. no vehicle parked there)", tags={ "ParkingLot", })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "Parking lot deleted successfully.") })
    @RequestMapping(value = "/admin/parkingLots/{parkingLotRef}",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.DELETE)
    public ResponseEntity<Void> adminParkingLotsParkingLotRefDelete(@ApiParam(value = "Ref of the parking lot to delete.",required=true) @PathVariable("parkingLotRef") String parkingLotRef) throws ResponseStatusException {
        try {
        	if(getObjectMapper().isPresent()) {
               UUID id = converter.map(parkingLotRef);
        		if (repository.existsById(id))
        		{
        			repository.deleteById(converter.map(parkingLotRef));
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
        		}
        		return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        	}
        }
        catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Server error", e);
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @ApiOperation(value = "Parking lot retrieval", nickname = "adminParkingLotsParkingLotRefGet", notes = "Retrieves the parking lot corresponding to the provided ref.", response = ParkingLot.class, tags={ "ParkingLot", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Parking lot retrieved successfully.", response = ParkingLot.class) })
    @RequestMapping(value = "/admin/parkingLots/{parkingLotRef}",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.GET)
    public ResponseEntity<ParkingLot> adminParkingLotsParkingLotRefGet(@ApiParam(value = "Ref of the parking lot to retrieve.",required=true) @PathVariable("parkingLotRef") String parkingLotRef) throws ResponseStatusException {
        try {
        	if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
                if (getAcceptHeader().get().contains("application/json")) {
                    Optional<fr.gwan.parkinglots.domain.ParkingLot> optional = repository.findById(converter.map(parkingLotRef));
                	if (optional.isPresent())
					{
						ParkingLot parkingLot = converter.toApi(optional.get());
				    	return new ResponseEntity<ParkingLot>(parkingLot, HttpStatus.OK);
					}
                }
        	}
        }
        catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Server error", e);
        }
        return new ResponseEntity<ParkingLot>(HttpStatus.NOT_FOUND);
    }

    @ApiOperation(value = "Create a parking lot", nickname = "adminParkingLotsPost", notes = "Creates a new parking lot for later vehicle management.", response = ParkingLot.class, tags={ "ParkingLot", })
    @ApiResponses(value = { 
        @ApiResponse(code = 201, message = "Parking lot created successfully.", response = ParkingLot.class),
        @ApiResponse(code = 400, message = "Provided parking lot check failed.") })
    @RequestMapping(value = "/admin/parkingLots",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.POST)
    public ResponseEntity<ParkingLot> adminParkingLotsPost(@ApiParam(value = "Contents of the new parking lot." ,required=true )  @Valid @RequestBody ParkingLot parkingLot) throws ResponseStatusException {
        try {
	        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
	            if (parkingLot.getRef() != null)
	            	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A new parking lot cannot already have a ref");
	            if (parkingLot.getParkingSlots().size() == 0)
	            	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A parking lot must have parking slots");
	            // Validate parsing policy
	            Function sedan = new Function("f(h) = " + parkingLot.getPricingPolicy().getSedanPricingPolicy());
	            if (sedan.checkSyntax()!=Function.NO_SYNTAX_ERRORS)
	            	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sedan pricing policy syntax error");
	            Function _20kw = new Function("f(h) = " + parkingLot.getPricingPolicy().get20kwPricingPolicy());
	            if (_20kw.checkSyntax()!=Function.NO_SYNTAX_ERRORS)
	            	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "20kw pricing policy syntax error");
	            Function _50kw = new Function("f(h) = " + parkingLot.getPricingPolicy().get50kwPricingPolicy());
	            if (_50kw.checkSyntax()!=Function.NO_SYNTAX_ERRORS)
	            	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "50kw pricing policy syntax error");
	            
	        	fr.gwan.parkinglots.domain.ParkingLot entityParkingLot = converter.toEntity(parkingLot);
	        	entityParkingLot.setLastUpdate(new Date());
	        	entityParkingLot = repository.save(entityParkingLot);
	
		    	return new ResponseEntity<ParkingLot>(converter.toApi(entityParkingLot), HttpStatus.CREATED);
	        }
	
        }
        catch(ResponseStatusException e) {
            throw e;
        }
        catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Server error", e);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
