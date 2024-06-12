package com.example.demo;

//Need this import because it allows us to mark class as a JPA entity and map it to the databse table
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

//Class is a JPA entity
@Entity
public class Asset{
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
    private String name; 
    private String deviceType;
    private String status;
    private String location;
    private String assignedTo;
    private String purchaseDate;
    private String warrantyDate;

    //getters

    public Long getid(){
        return id;
    }

    public String getName(){
        return name;
    }
    public String getDeviceType(){
        return deviceType;
    }
    public String getStatus(){
        return status;
    }
    public String getLocation(){
        return location;
    }
    public String getAssignedTo(){
        return assignedTo;
    }
    public String getPurchaseDate(){
        return purchaseDate;
    }
    public String getWarrantyExpiry(){
        return warrantyDate;
    }
    //End of getters

    //setters
    public void setName(String name) {
        this.name = name;
    }
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }
    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
    public void setWarrantyExpiry(String warrantyDate) {
        this.warrantyDate = warrantyDate;
    }  

}
