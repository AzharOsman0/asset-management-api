package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;



public class AssetTest {

	@Test
    public void getterAndSetterUnitTest(){
        Asset asset = new Asset();

		//Setting up an Asset object for assertion testing
    	asset.setName("HP Laptop");
		asset.setDeviceType("Laptop");
        asset.setStatus("Active");
        asset.setLocation("CDW Chicago Adams Office");
        asset.setAssignedTo("Azhar Osman");
        asset.setPurchaseDate("06-12-2024");
        asset.setWarrantyExpiry("06-13-2024");

		//asserts that are setters set properly to our set fields
		assertEquals("HP Laptop", asset.getName()); 
        assertEquals("Laptop", asset.getDeviceType()); 
        assertEquals("Active", asset.getStatus()); 
        assertEquals("CDW Chicago Adams Office", asset.getLocation()); 
        assertEquals("Azhar Osman", asset.getAssignedTo()); 
        assertEquals("06-12-2024", asset.getPurchaseDate()); 
        assertEquals("06-13-2024", asset.getWarrantyExpiry());
    }

}
