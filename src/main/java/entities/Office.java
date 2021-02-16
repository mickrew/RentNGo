package main.java.entities;

import main.java.actors.Worker;
import main.java.connections.MongoDBConnection;
import main.java.entities.Car;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Office {
    String city;
    String region;
    String id;
    String name;
    String capacity;
    List<Car> officeCars = new ArrayList<Car>();

    public Office(String city, String region, String name, String id, String capacity){
        this.city=city;
        this.region=region;
        this.name = name;
        this.id=id;
        this.capacity=capacity;
    }

    public static Office selectOffice(ArrayList<Office> offices){
        int i = 1;
        Scanner sc = new Scanner(System.in);
        for(Office o : offices){
            System.out.print(i++ + ") ");
            o.printOffice();
        }
        try {
            i = Integer.valueOf(sc.nextLine());
        } catch (Exception p){
            System.out.println("Error. Didn't insert an integer");
            return null;
        }
        if(i > offices.size() || i<1){
            System.out.println("Index out of range");
            return null;
        }
        return offices.get(i-1);
    }

    public Office(){};

    public void printOffice(){
        System.out.println("Office: " + id + ", City: "+ city+ ", Region: " + region+ ", Name: " + name);
    }

    public String getCity() {
        return city;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
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


}
