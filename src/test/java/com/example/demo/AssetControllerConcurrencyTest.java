package com.example.demo;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AssetControllerConcurrencyTest {

    private MockMvc mockMvc;

    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private AssetController assetController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(assetController).build();
    }

    @Test
    public void testConcurrentUpdate() throws Exception {
        Asset asset = new Asset();
        asset.setName("HP Laptop");
        asset.setDeviceType("Laptop");
        asset.setStatus("Active");
        asset.setLocation("CDW Chicago Adams Office");
        asset.setAssignedTo("Azhar Osman");
        asset.setPurchaseDate("06-12-2024");
        asset.setWarrantyExpiry("06-13-2024");

        when(assetRepository.findById(anyLong())).thenReturn(Optional.of(asset));
        when(assetRepository.save(any(Asset.class))).thenReturn(asset);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        Runnable updateTask = () -> {
            try {
                mockMvc.perform(put("/assets/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(asset)))
                    .andExpect(status().isOk());
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        executor.submit(updateTask);
        executor.submit(updateTask);

        executor.shutdown();
    }
}
