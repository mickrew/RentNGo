package main.java;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class RentNGo {
    static private MongoDBConnection db;
    static private LevelDBConnection ldb;
    static private User u;

    public static void main(String args[]){
        db = new MongoDBConnection("local");
        ldb = new LevelDBConnection();
        ldb.openDB();
        //        User(String surname, String name, String email, String password, Date dateOfBirth){
        Date d = new Date();
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        try {
            d = formatter.parse("06/05/1993");
        } catch(Exception e){}
        User u = new User("aaron", "billy r", "billyr.aaron@outlook.it", "Vxyy5cpIB5" , d);
        //System.out.println("Add User");
        //db.insertUser();
        //db.getInfo("andrea@live.it", "Email", "users");
        //db.insertNewCar();
        //db.deleteCar("AA111AA");

        //db.insertUser();
        //db.getInfo("andrea@live.it", "Email", "users");
        //db.deleteUser();
        //db.updateUser();
        //System.out.println("FINE");
        Iterator<Car> cars = db.getListOfCars().iterator();
        Car c;
        int i =0;
        while (cars.hasNext() && i!=100){
            c = cars.next();
            ldb.addCarInCart(u.getEmail(),c.getPlate(), c.getBrand(), c.getEngine(), c.getPower(), c.getVehicle());
            i++;
        }
        Iterator<Car> cars1 = ldb.getListOfCarsInCart(u.getEmail()).iterator();
        while (cars1.hasNext() ){
            c = cars1.next();
            c.printCar();
        }
        //ldb.elementInDatabase();

        ldb.closeDB();
        db.closeConnection();
    }
}
