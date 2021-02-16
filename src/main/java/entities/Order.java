package main.java.entities;

import main.java.entities.Office;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class Order {
    Car car;
    String user;
    Date pickDate = new Date();
    Date deliveryDate = new Date();
    String pickOffice ;
    String deliveryOffice ;
    ArrayList<Service> accessories ;
    Double priceCar;
    Double priceAccessories;


    public String pattern = "dd/MM/yyyy";
    public SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    
    public Order() {

    }

    public Order(Car car,String user,Double priceCar,String pickOffice,Date pickDate,String deliveryOffice, Date deliveryDate,
                 Double priceAccessories, ArrayList<Service> accessories){
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
    
    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public String getUser() {
        return user;
    }

    public ArrayList<Service> getAccessories() {
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

    public String getDeliveryOffice() {
        return deliveryOffice;
    }

    public String getpickOffice() {
        return pickOffice;
    }

    public void setDeliveryOffice(String deliveryOffice) {
        this.deliveryOffice = deliveryOffice;
    }

    public void setAccessories(ArrayList<Service> accessories) {
        this.accessories = accessories;
    }

    public void setUser(String user) {
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

    public void setpickOffice(String pickOffice) {
        this.pickOffice = pickOffice;
    }

    public boolean chooseParameters(ArrayList<Office> offices){
        Scanner sc = new Scanner(System.in);
        System.out.print("Insert the date of pick. ( DD/MM/YYYY ): ");
        Date d;

        String dateString = sc.nextLine();
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        try {
            d = formatter.parse(dateString);
        } catch (ParseException p){
            System.out.println("Error. Wrong Date");
            return false;
        }
        if(d.getTime() < (new Date()).getTime() - 60* 60 * 1000 * 24){
            System.out.println("Wrong date.");
            return false;
        }
        setPickDate(d);

        System.out.println("Insert the pick Office");
        Office pickOffice = Office.selectOffice(offices);
        if(pickOffice == null)
            return false;
        setpickOffice(pickOffice.getName());

        System.out.print("Insert the date of delivery. ( DD/MM/YYYY ): ");
        Date d2;

        String dateString2 = sc.nextLine();
        //System.out.println(dateString);

        try {
            d2 = formatter.parse(dateString2);
        } catch (ParseException p){
            System.out.println("Error. Wrong Date");
            return false;
        }
        if(d2.getTime() <= d.getTime()){
            System.out.println("Wrong Dates");
            return false;
        }
        setDeliveryDate(d2);

        Office deliveryOff = Office.selectOffice(offices);
        if(deliveryOff == null)
            return false;
        setDeliveryOffice(deliveryOff.getName());
        return true;
    }

    public void printOrder(Double discount){
        car.printCar();
        System.out.println("User: " + "\t" +user);
        if (discount == 0 )
            System.out.println("Price car per Day: " + "\t" + priceCar + "€");
        else
            System.out.println("You have a coupon of " + discount + "%\n" +"Price: " + "\t" + priceCar + "€" + "-" + discount + "% = " + (priceCar-(discount/100*priceCar)) + "€");
        System.out.println("Pick Office: " + "\t" + pickOffice);
        System.out.println("Pick Date: " + "\t" + simpleDateFormat.format(pickDate));
        System.out.println("Delivery Office: " + "\t" + deliveryOffice);
        System.out.println("Delivery Date: " + "\t" + simpleDateFormat.format(deliveryDate));
        System.out.println("Price accessories: " + "\t" + priceAccessories + "€");
        for(int i=0; i<accessories.size(); i++)
            accessories.get(i).printService();
    }

}
