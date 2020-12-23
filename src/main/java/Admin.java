package main.java;

import com.mongodb.client.MongoDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Filters.eq;

public class Admin extends  Worker{
    Date workertoAdmin =  new Date();

    public Admin (){
        super();
    }

    public Admin(String surname, String name, String email, String password, Date dateofbirth, int salary, Date hiringDate, Date workertoAdmin){
        super(surname, name, email, password, dateofbirth, salary, hiringDate);
        this.workertoAdmin = workertoAdmin;
    }

    public Date getWtoAdDate(){
        return workertoAdmin;
    }

    public void setWtoAdDate(Date workertoAdmin){
        this.workertoAdmin = workertoAdmin;
    }

    public void deleteUser(MongoDBConnection db){
        Scanner sc = new Scanner(System.in);
        System.out.print("Insert the user email: ");
        String email = sc.nextLine();
        User u;
        do {
            u = db.findUser(email);
            if(u==null)
                System.out.println("User Not found");
                email = sc.nextLine();
        }
        while (u== null);

        u.printUser();

        System.out.print("Do you want to proceed with the delete operation? (Y/N) ");
        String r = sc.nextLine();

        if(r.equals("Y")){
            db.deleteUser(u.getEmail());
            System.out.println("User deleted successfully");
        } else {
            System.out.println("Operation failed");
        }
    }

    public void deleteCar(MongoDBConnection db){
        Scanner sc = new Scanner(System.in);
        /*
        System.out.print("Insert the car plate: ");
        String plate = sc.nextLine();
        Car c;

        do {
            c = db.findCar(plate);
            if(c==null)
                System.out.println("Car Not found");
                plate = sc.nextLine();
        }
        while (c == null);
        */

        boolean check = false;
        String regex = "[A-Z][A-Z][0-9][0-9][0-9][A-Z][A-Z]";
        Pattern pattern = Pattern.compile(regex);
        String plate;
        Car c = new Car();
        while(!check) {
            System.out.print("Insert the Car Plate: ");
            plate = sc.nextLine();
            if (!pattern.matcher(plate).matches()) {
                System.out.println("Wrong plate! Try again");
                continue;
            }
            c = db.findCar(plate);
            if (c != null) {
                check = true;
            }
        }
        c.printCar();

        System.out.print("Do you want to proceed with the delete operation? (Y/N) ");
        String r = sc.nextLine();

        if(r.equals("Y")){
            db.deleteCar(c.getPlate());
        } else {
            System.out.println("Operation failed");
        }
    }


    public void insertNewCar(MongoDBConnection db){
        Scanner sc = new Scanner(System.in);

        Car c = new Car();
        String carPlate = null;
        String regex = "[A-Z][A-Z][0-9][0-9][0-9][A-Z][A-Z]";
        Pattern pattern = Pattern.compile(regex);
        /*
        do {
            System.out.print("Insert the Car Plate: ");
            carPlate = sc.nextLine();
            c = db.findCar(carPlate);
            if (c != null){
                System.out.println("Car already present");

            }
        } while(!pattern.matcher(carPlate).matches() && c != null);
        */
        boolean check = false;

        while(!check){
            System.out.print("Insert the Car Plate: ");
            carPlate = sc.nextLine();
            if (!pattern.matcher(carPlate).matches()) {
                System.out.println("Wrong plate! Try again");
                continue;
            }
            c = db.findCar(carPlate);
            if (c != null){
                System.out.println("Car already present");
            } else
                check = true;
        }

        c = new Car();
        c.setPlate(carPlate);

        System.out.print("Insert the Brand: ");
        c.setBrand(sc.nextLine());

        System.out.print("Insert the Vehicle: ");
        c.setVehicle(sc.nextLine());


        System.out.print("Insert the Engine: ");
        c.setEngine(sc.nextLine());


        System.out.print("Insert the Power: ");
        c.setPower(sc.nextLine());


        System.out.print("Insert the average fuel consumption: ");
        c.setAvgFuelCons(sc.nextLine());


        System.out.print("Insert the CO2: ");
        c.setCo2(sc.nextLine());


        System.out.print("Insert the weight: ");
        c.setWeight(sc.nextLine());

        System.out.print("Insert the gearBox type: ");
        c.setGearBoxType(sc.nextLine());

        System.out.print("Insert the Tyre: ");
        c.setTyre(sc.nextLine());

        System.out.print("Insert the Traction type: ");
        c.setTractionType(sc.nextLine());
        
        db.insertNewCar(c);
    }
}
