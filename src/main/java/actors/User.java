package main.java.actors;

import main.java.connections.LevelDBConnection;
import main.java.connections.MongoDBConnection;
import main.java.entities.Car;
import main.java.entities.Office;
import main.java.entities.Order;

import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import static com.mongodb.client.model.Filters.eq;

public class User extends UnregisteredUser {

        public User() {
        }

        public User(String surname, String name, String email, String password, Date dateOfBirth){
            super(surname, name, email, password, dateOfBirth);
        }

        public void showMenu(){
                System.out.println("0) Exit");
                System.out.println("1) Create new Order");
                System.out.println("2) Show Orders"); //even delete or modify
                System.out.println("3) Show Cart");
                System.out.println("4) Delete Account");
                System.out.println("5) Add/remove Accessories");
        }

    public boolean searchForCar(MongoDBConnection db, LevelDBConnection ldb, UnregisteredUser u, Scanner sc) {
        Order o = ((User) u).createOrder(db.listOffices());
        if(o == null){
            return false;
        }
        int category = 0;
        System.out.println("Choose the class: (if different from 1,2,3 --> ALL CLASSES)");
        System.out.println("1) Class  (55-75kw)");
        System.out.println("2) Class  (76-120kw)");
        System.out.println("3) Class  (121-over)");
        try {
            category = Integer.valueOf(sc.nextLine());
        }
        catch(Exception e){
            category = 4;
        }

        ldb.searchCar(o.getpickOffice(),o.getDeliveryOffice(), o.getPickDate(), o.getDeliveryDate(), db.getListOfCars(o.getOfficePickPosition(), category), (User)u);
        return true;
    }

        public Car chooseCar(ArrayList<Car> cars){
            int i=0;
            for(Car c: cars){
                System.out.print(i++ +") ");
                c.printCar();
            }
            System.out.println("To proceed with the payment you need to select a car");
            Scanner sc=new Scanner(System.in);
            i=sc.nextInt();
            if(i >= cars.size() || i < 0){
                System.out.println("Error");
                return null;
            }
            Car c = cars.get(i);
            return c;
        }

        public Order createOrder(ArrayList<Office> offices){
            Order o =new Order();
            if(o.chooseParameters(offices)== false){
                return null;
            }
            return o;
        }

    public void showCart(MongoDBConnection db, LevelDBConnection ldb, Scanner sc, UnregisteredUser u) {

        ArrayList<Car> cars = ldb.getListOfCarsInCart(u.getEmail());
        double total;

        if(cars.isEmpty()){
            System.out.println("Cart is empty");
            return ;
        }

        for(Car c: cars){
            c.printCar();
            System.out.println("The car price per day is: "+ Math.ceil(c.calcolatePrice()) + "€");
        }

        Order o = ldb.showOrderInfo(u.getEmail());
        if(o == null)
            return;
        o.printOrder();

        System.out.println("Do you want to proceed with the payment? Y/N");
        String  choice = sc.nextLine();
        if(choice.equals("Y")){
            ldb.payment(u.getEmail(), ((User)u).chooseCar(cars), o);
            if(db.checkIfCarRented(o)){
                System.out.println("Car is already rented");
            } else {
                o.printOrder();
                Long millisDay = 86400000L;
                Long numDays = (o.getDeliveryDate().getTime() - o.getPickDate().getTime())/(millisDay);
                total = o.getPriceCar() * numDays + o.getPriceAccessories();
                System.out.println("The total is: " + total + "€\n");
                db.insertOrder(o, "Booked");
            }
        }
    }

    public void showListOrder(MongoDBConnection db, UnregisteredUser u) {
        db.showListOrders(u.getEmail());
    }

    public void deleteAccount(MongoDBConnection db, LevelDBConnection ldb, UnregisteredUser u) {
        db.deleteUser(u.getEmail());
        ldb.deleteUserCart(u.getEmail());
        u = null;

    }
}


