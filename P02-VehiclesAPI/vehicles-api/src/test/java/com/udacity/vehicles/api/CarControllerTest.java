package com.udacity.vehicles.api;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.Condition;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.Details;
import com.udacity.vehicles.domain.manufacturer.Manufacturer;
import com.udacity.vehicles.service.CarService;
import java.net.URI;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Implements testing of the CarController class.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class CarControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<Car> json;

    @MockBean
    private CarService carService;

    @MockBean
    private PriceClient priceClient;

    @MockBean
    private MapsClient mapsClient;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Creates pre-requisites for testing, such as an example car.
     */
    @Before
    public void setup() {
        Car car = getCar();
        car.setId(1L);
        given(carService.save(any())).willReturn(car);
        given(carService.findById(any())).willReturn(car);
        given(carService.list()).willReturn(Collections.singletonList(car));
    }

    /**
     * Tests for successful creation of new car in the system
     * @throws Exception when car creation fails in the system
     */
    @Test
    public void createCar() throws Exception {
        Car car = getCar();
        mvc.perform(
                post(new URI("/cars"))
                        .content(json.write(car).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isCreated());
    }

    /**
     * Tests if the read operation appropriately returns a list of vehicles.
     * @throws Exception if the read operation of the vehicle list fails
     */
    @Test
    public void listCars() throws Exception {
        /**
         * TODO: Add a test to check that the `get` method works by calling
         *   the whole list of vehicles. This should utilize the car from `getCar()`
         *   below (the vehicle will be the first in the list).
         */
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                        .get("/cars"))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseContent);
        JsonNode carDetails = jsonNode.path("_embedded").path("carList").get(0);
        assertThat(carDetails.get("id").asLong()).isEqualTo(1);
        assertThat(carDetails.get("condition").asText()).isEqualTo(getCar().getCondition().toString());
        assertThat(carDetails.get("details").get("body").asText()).isEqualTo(getCar().getDetails().getBody());
        assertThat(carDetails.get("details").get("model").asText()).isEqualTo(getCar().getDetails().getModel());
        assertThat(carDetails.get("details").get("manufacturer").get("code").asInt()).isEqualTo(getCar().getDetails().getManufacturer().getCode());
        assertThat(carDetails.get("details").get("manufacturer").get("name").asText()).isEqualTo(getCar().getDetails().getManufacturer().getName());
        assertThat(carDetails.get("details").get("numberOfDoors").asInt()).isEqualTo(getCar().getDetails().getNumberOfDoors());
        assertThat(carDetails.get("details").get("fuelType").asText()).isEqualTo(getCar().getDetails().getFuelType());
        assertThat(carDetails.get("details").get("engine").asText()).isEqualTo(getCar().getDetails().getEngine());
        assertThat(carDetails.get("details").get("mileage").asInt()).isEqualTo(getCar().getDetails().getMileage());
        assertThat(carDetails.get("details").get("modelYear").asInt()).isEqualTo(getCar().getDetails().getModelYear());
        assertThat(carDetails.get("details").get("productionYear").asInt()).isEqualTo(getCar().getDetails().getProductionYear());
        assertThat(carDetails.get("details").get("externalColor").asText()).isEqualTo(getCar().getDetails().getExternalColor());
        assertThat(carDetails.get("location").get("lat").asDouble()).isEqualTo(getCar().getLocation().getLat());
        assertThat(carDetails.get("location").get("lon").asDouble()).isEqualTo(getCar().getLocation().getLon());
    }

    /**
     * Tests the read operation for a single car by ID.
     * @throws Exception if the read operation for a single car fails
     */
    @Test
    public void findCar() throws Exception {
        /**
         * TODO: Add a test to check that the `get` method works by calling
         *   a vehicle by ID. This should utilize the car from `getCar()` below.
         */
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                        .get("/cars/1"))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseContent);
        assertThat(jsonNode.get("id").asLong()).isEqualTo(1);
        assertThat(jsonNode.get("condition").asText()).isEqualTo(getCar().getCondition().toString());
        assertThat(jsonNode.get("details").get("body").asText()).isEqualTo(getCar().getDetails().getBody());
        assertThat(jsonNode.get("details").get("model").asText()).isEqualTo(getCar().getDetails().getModel());
        assertThat(jsonNode.get("details").get("manufacturer").get("code").asInt()).isEqualTo(getCar().getDetails().getManufacturer().getCode());
        assertThat(jsonNode.get("details").get("manufacturer").get("name").asText()).isEqualTo(getCar().getDetails().getManufacturer().getName());
        assertThat(jsonNode.get("details").get("numberOfDoors").asInt()).isEqualTo(getCar().getDetails().getNumberOfDoors());
        assertThat(jsonNode.get("details").get("fuelType").asText()).isEqualTo(getCar().getDetails().getFuelType());
        assertThat(jsonNode.get("details").get("engine").asText()).isEqualTo(getCar().getDetails().getEngine());
        assertThat(jsonNode.get("details").get("mileage").asInt()).isEqualTo(getCar().getDetails().getMileage());
        assertThat(jsonNode.get("details").get("modelYear").asInt()).isEqualTo(getCar().getDetails().getModelYear());
        assertThat(jsonNode.get("details").get("productionYear").asInt()).isEqualTo(getCar().getDetails().getProductionYear());
        assertThat(jsonNode.get("details").get("externalColor").asText()).isEqualTo(getCar().getDetails().getExternalColor());
        assertThat(jsonNode.get("location").get("lat").asDouble()).isEqualTo(getCar().getLocation().getLat());
        assertThat(jsonNode.get("location").get("lon").asDouble()).isEqualTo(getCar().getLocation().getLon());
    }

    /**
     * Tests the deletion of a single car by ID.
     * @throws Exception if the delete operation of a vehicle fails
     */
    @Test
    public void deleteCar() throws Exception {
        /**
         * TODO: Add a test to check whether a vehicle is appropriately deleted
         *   when the `delete` method is called from the Car Controller. This
         *   should utilize the car from `getCar()` below.
         */
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                        .delete("/cars/1"))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    /**
     * Creates an example Car object for use in testing.
     * @return an example Car object
     */
    private Car getCar() {
        Car car = new Car();
        car.setLocation(new Location(40.730610, -73.935242));
        Details details = new Details();
        Manufacturer manufacturer = new Manufacturer(101, "Chevrolet");
        details.setManufacturer(manufacturer);
        details.setModel("Impala");
        details.setMileage(32280);
        details.setExternalColor("white");
        details.setBody("sedan");
        details.setEngine("3.6L V6");
        details.setFuelType("Gasoline");
        details.setModelYear(2018);
        details.setProductionYear(2018);
        details.setNumberOfDoors(4);
        car.setDetails(details);
        car.setCondition(Condition.USED);
        return car;
    }
}