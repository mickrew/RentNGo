package main.java;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Order {
    Car car = new Car();
    User user = new User();
    Date pickDate = new Date();
    Date deliveryDate = new Date();
    Office pickOffice = new Office();
    Office deliveryOffice = new Office();
    List<Service> accessories = new ArrayList<Service>();
    Double priceCar;
    Double priceAccessories;
    
    public Order(Car car, User user, Double priceCar, Date pickDate, Office pickOffice,Date deliveryDate, Office deliveryOffice, Double priceAccessories, List<Service> accessories) {
        this.car=car;
        this.user=user;
        this.priceCar=priceCar;
        this.pickDate=pickDate;
        this.pickOffice=pickOffice;
        this.deliveryDate=deliveryDate;
        this.deliveryOffice=deliveryOffice;
        this.priceAccessories=priceAccessories;
        this.accessories=accessories;
    }

    public Order(){}
    
    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public User getUser() {
        return user;
    }

    public List<Service> getAccessories() {
        return accessories;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public Date getPickDate() {
        return pickDate;
    }

    public Double getPriceCar() {
        return priceCar;
    }

    public Double getPriceAccessories() {
        return priceAccessories;
    }

    public Office getDeliveryOffice() {
        return deliveryOffice;
    }

    public Office getpickOffice() {
        return pickOffice;
    }

    public void setDeliveryOffice(Office deliveryOffice) {
        this.deliveryOffice = deliveryOffice;
    }

    public void setAccessories(List<Service> accessories) {
        this.accessories = accessories;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public void setPickDate(Date pickDate) {
        this.pickDate = pickDate;
    }

    public void setPriceAccessories(Double priceAccessories) {
        this.priceAccessories = priceAccessories;
    }

    public void setPriceCar(Double priceCar) {
        this.priceCar = priceCar;
    }

    public void setpickOffice(Office pickOffice) {
        this.pickOffice = pickOffice;
    }

    public void chooseParameters(ArrayList<Office> offices){
        Scanner sc = new Scanner(System.in);
        System.out.print("Insert the date of pick. ( DD/MM/YYYY ): ");
        Date d= new Date();

        String dateString = sc.nextLine();
        //System.out.println(dateString);

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        try {
            d = formatter.parse(dateString);
        } catch (ParseException p){
            System.out.println("Error");
        }
        setPickDate(d);

        System.out.println("Insert the pick Office");
        int i = 0;
        for(Office o : offices){
            System.out.print(i++ + ") ");
            o.printOffice();
        }
        i = Integer.valueOf(sc.nextLine());
        setpickOffice(offices.get(i));

        System.out.print("Insert the date of delivery. ( DD/MM/YYYY ): ");
        Date d2= new Date();

        String dateString2 = sc.nextLine();
        //System.out.println(dateString);

        try {
            d2 = formatter.parse(dateString2);
        } catch (ParseException p){
            System.out.println("Error");
        }
        setDeliveryDate(d2);

        System.out.println("Insert the delivery Office");
        i = 0;
        for(Office o : offices){
            System.out.print(i++ + ") ");
            o.printOffice();
        }
        i = Integer.valueOf(sc.nextLine());
        setDeliveryOffice(offices.get(i));
    }

    public void printOrder(){
        System.out.println("Car: " + "\t" + car.getPlate());
        System.out.println("User: " + "\t" +user.getEmail());
        System.out.println("Price: " + "\t" + priceCar + "€");
        System.out.println("Pick Office: " + "\t" + pickOffice);
        System.out.println("Pick Date: " + "\t" + pickDate);
        System.out.println("Delivery Office: " + "\t" + deliveryOffice);
        System.out.println("Delivery Date: " + "\t" + deliveryDate);
        System.out.println("Price accessores: " + "\t" + priceAccessories + "€");
        System.out.println("List accessories: " + "\t" + accessories.toString());
    }

}
