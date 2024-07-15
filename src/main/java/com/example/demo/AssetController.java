package com.example.demo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.AssetController.ResourceNotFoundException;


@RestController //Spring MVC annoation to mark the class as a REST controller, tells Spring that this class will handle incoming HTTP requests and handle responses
@RequestMapping("/assets") //this class handles all /assets requests
public class AssetController {

  @Autowired //This automatically injects the AssetRepository bean into the class providing an instance of AssetRepository so you can interact with the database
  private AssetRepository assetRepository;
  
  @PostMapping // Post request
  public ResponseEntity<Asset> registerAsset(@RequestBody Asset asset) {
    try {
      Asset savedAsset = assetRepository.save(asset);
      return new ResponseEntity<>(savedAsset, HttpStatus.CREATED); //Response entity is a lass in spring that is a HTTP response with headers,body,status code
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
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
  public ResponseEntity<List< Asset >> getAllAssets() {
    try {
      List<Asset> allAsset = assetRepository.findAll(); //findAll() retrives all entites from a database
      if(allAsset.isEmpty()){
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }
      return new ResponseEntity<>(allAsset, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
  
  @GetMapping("/{id}") //Get Request, or else null in case ID is not in database.
  public ResponseEntity<Asset> getAssetByID(@PathVariable Long id) {
    try {
        Asset asset = assetRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Asset: " + id + " Not found in databse")); //findByID() retrives all entites from a database
        return new ResponseEntity<>(asset, HttpStatus.OK);
    }catch (ResourceNotFoundException e){
      return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    }

  //Returning a ResponseEntity containing an Asset object, meaning it will return a HTTP response with the updated asset data
  // PATH VARIABLE: We are inputting an URL, and extract the Id so we use @PathVariable
  //PUT REQUEST: Updates an entire resource with new repersentation provided by requestBody
  @PutMapping("/{id}")
  public synchronized ResponseEntity<Asset> updateAsset(@PathVariable Long id, @RequestBody Asset assetDetails) {
    try {
      Asset asset = assetRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Asset: " + id + "Not found"));
      Thread.sleep(1000);
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
        
    } catch (ResourceNotFoundException  e) {
      return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    } catch (Exception e) {
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    }

    public class ResourceNotFoundException extends RuntimeException {
      private static final long serialVersionUID = 1L;
  
      public ResourceNotFoundException(String message) {
          super(message);
      }
  }

}