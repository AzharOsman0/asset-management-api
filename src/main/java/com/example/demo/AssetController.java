package com.example.demo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController //Spring MVC annoation to mark the class as a REST controller, tells Spring that this class will handle incoming HTTP requests and handle responses
@RequestMapping("/assets") //this class handles all /assets requests
public class AssetController {

  @Autowired //This automatically injects the AssetRepository bean into the class providing an instance of AssetRepository so you can interact with the database
  private AssetRepository assetRepository;
  
  @PostMapping // Post request
  public Asset registerAsset(@RequestBody Asset asset) {
    return assetRepository.save(asset);
  }

  /**JSON BODY FOR POST REQUEST:
{
  "name": "HP Laptop",
  "deviceType": "Laptop",
  "status": "Active",
  "location": "CDW Chicago Adams Office",
  "assignedTo": "Azhar Osman",
  "purchaseDate": "06-12-2024",
  "warrantyExpiry": "06-13-2024"
}
*/

  @GetMapping //Get Request
  public List < Asset > getAllAssets() {
    return assetRepository.findAll(); //findAll() retrives all entites from a database
  }
  
  @GetMapping("/{id}") //Get Request, or else null in case ID is not in database.
  public Asset getAssetByID(@PathVariable Long id) {
    return assetRepository.findById(id).orElse(null); //findByID() retrives all entites from a database
  }

  //Returning a ResponseEntity containing an Asset object, meaning it will return a HTTP response with the updated asset data
  // PATH VARIABLE: We are inputting an URL, and extract the Id so we use @PathVariable
  //PUT REQUEST: Updates an entire resource with new repersentation provided by requestBody
  @PutMapping("/{id}")
  public ResponseEntity < Asset > updateAsset(@PathVariable Long id, @RequestBody Asset assetDetails) {
    Asset asset = assetRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("Asset not found"));

    asset.setName(assetDetails.getName());
    asset.setDeviceType(assetDetails.getDeviceType());
    asset.setStatus(assetDetails.getStatus());
    asset.setLocation(assetDetails.getLocation());
    asset.setAssignedTo(assetDetails.getAssignedTo());
    asset.setPurchaseDate(assetDetails.getPurchaseDate());
    asset.setWarrantyExpiry(assetDetails.getWarrantyExpiry());
  //Final keyword makes the updatedAsset a constant thus immutable (good practice)
  //assetRepository.save Spring Boot JPA persistance takes care of the rest and updates it in the H2 Database ;)
    final Asset updatedAsset = assetRepository.save(asset);

    return ResponseEntity.ok(updatedAsset);
    }

}