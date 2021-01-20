package main.java.entities;

import main.java.entities.Car;

import java.util.ArrayList;
import java.util.List;

public class Office {
    String city;
    String region;
    String id;
    String name;
    Integer capacity;
    Integer position;
    List<Car> officeCars = new ArrayList<Car>();

    public Office(String city, String region, String name, String id, Integer capacity, Integer position){
        this.city=city;
        this.region=region;
        this.name = name;
        this.id=id;
        this.capacity=capacity;
        this.position = position;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Office(){};

    public void printOffice(){
        System.out.println("Office: " + id + ", City: "+ city+ ", Region: " + region);
    }

    public String getCity() {
        return city;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRegion() {
        return region;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public List<Car> getOfficeCars() {
        return officeCars;
    }

    public void setOfficeCars(List<Car> officeCars) {
        this.officeCars = officeCars;
    }


    /*
    return the list of car belonging to a specific office

    public List<Car> getOfficeCars(String nameOffice){

    }
    */

}
