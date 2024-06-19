package com.example.demo;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;


import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

 //Spring boot automatically sets up a MockMVC object that you can use for testing
public class AssetControllerTest {

    //Spring MVC test framework that gives you support for testing Spring MVC controllers. Letting you do HTTP requests and assert the results
    private MockMvc mockMvc;

    //Creates a mock  of AssetRepository, which can be used to stub methods and verify interactions. Simulates database operations without hitting
    @Mock
    private AssetRepository assetRepository;

    //creates an instance of AssetController and injects the mock AssetRepository into it it lets you test the controller in isolatation
    @InjectMocks
    private AssetController assetController;

    @BeforeEach //Will be executed before each of the test methods
    public void setUp(){
        //Intalizes the @mock and injects those mocks into @InjectMocks
        MockitoAnnotations.openMocks(this);

        //Builds the mockMvc insntace with the controller for use in the code, like its a real web server
        mockMvc = MockMvcBuilders.standaloneSetup(assetController).build();

       

    }
    //Units Tests if the controller isolated environment that the controller properly saves to a mock repoistory, without interacting with database
    //We are doing two isolation tests here.
    //#1 Isolation test for Save: We are testing if the save method behaves correctly, returning the predefined Asset object, regardless of input
    //#2 Isolation Test JSON Serialization/Deserialzation: Checks than the Asset object can be turned into JSON data, Checks that controller can make JSON data into an Asset Object
    @Test
    public void testRegisterAsset() throws Exception{
        Asset asset = new Asset();
        asset.setName("HP Laptop");
        when(assetRepository.save(any(Asset.class))).thenReturn(asset);

        //specfies the content type of the post request will be 'application/json' (the asset object in the registration post request)
        //In this function we are ensuring the controller correctly deserializes the json request body into a Asset object
        //We also confirim that when you send an asset object it will be seralized into JSON correclty
        mockMvc.perform(post("/assets")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(asset))) //Serializes asset object into a Json String
            .andExpect(status().isOk()) //Ensure the controller returns 200 ok, confirms successful request handling. Indirectly confirms deserilization
            .andExpect(jsonPath("$.name").value("HP Laptop")); //confirms serilization in our response and ensures save method works(DOES NOT ENSURE CORRECTNESS OF SAVE METHOD)
    }

    @Test
    public void testGetAssetbyID() throws Exception {
        Asset asset = new Asset();
        asset.setName("HP Laptop");
        when(assetRepository.findById(anyLong())).thenReturn(Optional.of(asset));

        mockMvc.perform(get("/assets/1"))
                .andExpect(status().isOk()) //confirms request handeling
                .andExpect(jsonPath("$.name").value("HP Laptop")); //confirms handling of get method
    }


    @Test
    public void testGetAllAssets() throws Exception {
        when(assetRepository.findAll()).thenReturn(Collections.singletonList(new Asset())); //Creates an immuatble list with one element, a new asset object, collections provides methods like list sets and maps btw..

        mockMvc.perform(get("/assets"))
                .andExpect(status().isOk()) //confirms request handeling
                .andExpect(jsonPath("$").isArray()); //confirms handling of get method
    }

    @Test
    public void testUpdateAsset() throws Exception {
        Asset asset = new Asset();
        asset.setName("HP Laptop");
        when(assetRepository.findById(anyLong())).thenReturn(Optional.of(asset)); //Returns the created object when doing findById method
        when(assetRepository.save(any(Asset.class))).thenReturn(asset);

        mockMvc.perform(put("/assets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(asset)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("HP Laptop"));
    }
    
}
