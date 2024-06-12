//Interface of Spring Data JPA repo that provides CRUD operations, it lets use interact with database without boiler platecode
//Extends jpaRepository and inherits basic CRUD operations
//This is a Reposistory: Encapsulates of Data Access: Provides a way to seperate the data access logic from the buisness logic, ...
//Instead of writing SQL queries directly in your service layer, you define methods in a repository interface

//imports
package com.example.demo;

//retrives the Asset class from model

//gets the JPA repository that allows us to do CRUD commands
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository //<Assets - Specfies the type that the repository managees, Long  - Specifies the type of entitys primary key>
public interface AssetRepository extends JpaRepository<Asset, Long>{

}