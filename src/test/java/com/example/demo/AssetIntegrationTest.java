package com.example.demo;


//You do not need a JUnit dependency import in pom.xml because it is automatically imported by Spring Boot
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest // This is not a Unit test like AssetControllerTest we don't just create mocks and inject them into AssetController for isolation
// we are loading the full context of a spring boot applicaiton which is why we autoconfiguremockMVC as well because we just want a quick set up no mocks also Spring
//Boot test which loads the applicaton in full context, including Spring managed beans
@AutoConfigureMockMvc
public class AssetIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AssetRepository assetRepository;

    @BeforeEach
    public void setUp() {
        assetRepository.deleteAll();
        //If we didnt use autowire we would have to instantate MockMVC and assetRepository like this
        // this.assetRepository = new AssetRepository(); // 
        // this.assetRepository.deleteAll();
        // this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
        //This bypass the Spring IOC container leading to more boilerplate code and bad depdency managment
    }

@Test
public void testGetAndRegisterAsset() throws Exception{
    Asset asset = new Asset();
    asset.setName("HP Laptop");

     mockMvc.perform(post("/assets")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(asset)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("HP Laptop"));
    mockMvc.perform(get("/assets/1"))
            .andExpect(status().isOk()) 
            .andExpect(jsonPath("$.name").value("HP Laptop"));
    
}

    
}
