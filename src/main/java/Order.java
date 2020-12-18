package main.java;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

}
