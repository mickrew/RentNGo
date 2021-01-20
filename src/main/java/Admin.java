package main.java;

import com.mongodb.client.MongoDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Filters.eq;

public class Admin extends  UnregisteredUser{
    Date workertoAdmin =  new Date();
    int salary;
    Date hiringDate = new Date();

    public Admin (){
        super();
    }

    public Admin(String surname, String name, String email, String password, Date dateofbirth, int salary, Date hiringDate, Date workertoAdmin){
        super(surname, name, email, password, dateofbirth);
        this.salary =salary;
        this.hiringDate = hiringDate;
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

    public static void addRemoveWorker (MongoDBConnection db) throws ParseException {
        Integer i=0;
        System.out.println("1) Add Worker");
        System.out.println("2) Remove Worker");
        Scanner sc =new Scanner(System.in);
        try{
            i = Integer.valueOf(sc.nextLine());
        }
        catch(Exception e){
            System.out.println("Error. Didn't insert an integer");

        }

        switch (i) {
            case 1:
                System.out.println("Insert the Email of Worker: ");
                String emailWorker = sc.nextLine();
                Worker w = db.findWorker(emailWorker);
                if (w != null){
                    System.out.println("Worker already exists!");
                }
                w = new Worker();
                w.setEmail(emailWorker);
                System.out.print("Insert the worker password: ");
                assert w != null;
                w.setPassword(sc.nextLine());

                System.out.print("Insert the date of birth. ( DD/MM/YYYY ): ");
                Date d= new Date();


                String dateString = sc.nextLine();
                //System.out.println(dateString);

                DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                String today = formatter.format(d);
                try {
                    d = formatter.parse(dateString);
                } catch (Exception p){
                    System.out.println("Error");
                }
                w.setDateofbirth(d);

                System.out.print("Insert the salary: ");
                w.setSalary(Integer.valueOf(sc.nextLine()));

                w.setHiringDate(today);

                db.insertWorker(w);
                break;

            case 2:
                System.out.println("Insert the Email of Worker: ");
                emailWorker = sc.nextLine();
                w = db.findWorker(emailWorker);
                if (w == null){
                    System.out.println("Worker doesn't exists!");
                }
                db.deleteWorker(emailWorker);
                break;
        }

    }
    public void promoteWorker(MongoDBConnection db) throws ParseException {
        Scanner sc =new Scanner(System.in);
        System.out.println("Insert the Email of Worker: ");
        String emailWorker = sc.nextLine();
        Worker w = db.findWorker(emailWorker);
        if (w == null){
            System.out.println("Worker doesn't exists!");
            return;
        }
        Admin a = db.findAdmin(emailWorker);
        if (a != null){
            System.out.println("Is already an Admin!");
            return;
        }
        Date d = new Date();

        System.out.println("Insert the new Salary: ");
        Integer salary = Integer.valueOf(sc.nextLine());
        a  = new Admin(w.getSurname(), w.getName(), emailWorker, w.getPassword(), w.getDateOfBirth(), salary, w.getHiringDate(), d);
        db.insertAdmin(a);
        db.deleteWorker(emailWorker);

    }


    public Date getWorkertoAdmin() {
        return workertoAdmin;
    }

    public void setWorkertoAdmin(Date hiringDate){
        this.hiringDate = hiringDate;
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

    public int getSalary(){
        return salary;
    }

    public void setSalary(int salary){
        this.salary = salary;
    }

    public Date getHiringDate(){
        return hiringDate;
    }

    public void setHiringDate(Date hiringDate){
        this.hiringDate = hiringDate;
    }

    public static void modifyWorker(MongoDBConnection db) throws ParseException {
        Scanner sc =new Scanner(System.in);
        System.out.println("Insert the Email of Worker: ");
        String emailWorker = sc.nextLine();
        Worker w = db.findWorker(emailWorker);
        if (w == null){
            System.out.println("Worker doesn't exists!");
            return;
        }
        System.out.println("Which field do you want to modify ? ");
        System.out.println("1) Salary");
        System.out.println("2) Office"); //even delete or modify
        sc =new Scanner(System.in);
        Integer i=0;
        try{
            i = Integer.valueOf(sc.nextLine());
        }
        catch(Exception e){
            System.out.println("Error. Didn't insert an integer");
        }
        switch (i){
            case 1:
                System.out.println("Insert the new salary: ");
                Integer salary=0;
                try{
                    salary = Integer.valueOf(sc.nextLine());
                }
                catch(Exception e){
                    System.out.println("Error. Didn't insert an integer");
                }
                db.updateWorkerSalary(salary, emailWorker);
                break;
            case 2:
                System.out.println("Insert the new office: ");
                String office = sc.nextLine();
                Office o = db.findOfficeByName(office);
                if (o == null){
                    System.out.println("Office doesn't exists!");
                    return;
                }
                Integer position = o.getPosition();
                db.updateWorkerOffice(emailWorker, position);
                break;
        }
    }

    public void modifyCar(MongoDBConnection db){
        Scanner sc =new Scanner(System.in);
        System.out.println("Insert the carplate: ");
        String carPlate = sc.nextLine();
        Car c = db.findCar(carPlate);
        if (c == null){
            System.out.println("Car doesn't exists!");
            return;
        }
        System.out.println("Insert the new office: ");
        String office = sc.nextLine();
        Office o = db.findOfficeByName(office.trim());
        if (o == null){
            System.out.println("Office doesn't exists!");
            return;
        }
        db.updateCarOffice(carPlate, o.getPosition());

    }

    public void showMenu(){
        System.out.println("0) Exit");
        System.out.println("1) Modify Car");
        System.out.println("2) Add/Remove Cars");
        System.out.println("3) Find Worker");
        System.out.println("4) Add/Remove worker");
        System.out.println("5) Promote Worker to Admin");
        System.out.println("6) Modify Worker");
        System.out.println("7) Remove user");
         //es. Salary




    }

    public void findWorker(MongoDBConnection db) throws ParseException {
        Scanner sc =new Scanner(System.in);
        System.out.println("Insert the Email of Worker: ");
        String emailWorker = sc.nextLine();
        Worker w = db.findWorker(emailWorker);
        if (w == null){
            System.out.println("Worker doesn't exists!");
            return;
        }
    }

    public void removeUser(MongoDBConnection db) {
        Scanner sc =new Scanner(System.in);
        System.out.println("Insert the Email of User: ");
        String emailUser = sc.nextLine();
        User u = db.findUser(emailUser);
        if (u == null){
            System.out.println("User doesn't exists!");
            return;
        }
        db.deleteUser(emailUser);
    }
}
