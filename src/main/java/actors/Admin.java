package main.java.actors;

import com.mongodb.client.MongoDatabase;
import main.java.RentNGo;
import main.java.connections.MongoDBConnection;
import main.java.entities.Car;
import main.java.entities.Office;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Filters.eq;

public class Admin extends Worker {
    Date workertoAdmin =  new Date();

    public Admin (){
        super();
    }

    public Admin(String surname, String name, String email, String password, Date dateofbirth, int salary, Date hiringDate,String office,  Date workertoAdmin){
        super(surname, name, email,password, dateofbirth, salary, hiringDate, office);
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
                boolean check = true;
                Worker w;
                String emailWorker="";
                while (check) {

                    System.out.print("Insert the Email of Worker: ");
                    emailWorker = sc.nextLine();
                    w = db.findWorker(emailWorker.trim());

                    if (w != null ) {
                        System.out.println("Worker already exists!");
                    }
                    else if (w.validateEmail(emailWorker)==false){
                        System.out.println("Incorrect email !");
                    }
                    else{
                        check = false;
                    }
                }

                w = new Worker();
                w.setEmail(emailWorker);

                System.out.print("Insert the name of Worker: ");
                w.setName(sc.nextLine());

                System.out.print("Insert the surname of Worker: ");
                w.setSurname(sc.nextLine());

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


                System.out.print("Choose the office: ");
                ArrayList<Office> offices = db.listOffices();
                i = 1;
                for(Office o : offices){
                    System.out.print(i++ + ") ");
                    o.printOffice();
                }
                try {
                    i = Integer.valueOf(sc.nextLine());
                } catch (Exception p){
                    System.out.println("Error. Didn't insert an integer");
                    return;
                }
                int idOfficePick = i;
                if(i > offices.size() || i<1) {
                    System.out.println("Index out of range");
                    return;
                }
                w.setOffice(offices.get(i-1).getName());

                w.setHiringDate(today);

                db.insertWorker(w);

                break;

            case 2:
                System.out.println("Insert the Email of Worker: ");
                emailWorker = sc.nextLine();
                w = db.findWorker(emailWorker);
                if (w == null){
                    System.out.println("Worker doesn't exist!");
                    break;
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
            System.out.println("Worker doesn't exist!");
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
        a  = new Admin(w.getSurname(), w.getName(), emailWorker, w.getPassword(), w.getDateOfBirth(), salary, w.getHiringDate(),"", d);
        //db.insertAdmin(a);
        //db.deleteWorker(emailWorker);
        db.promoteWorker(a);

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

        boolean check = false;

        while(!check){
            System.out.print("Insert the Car Plate: ");
            carPlate = sc.nextLine();
            if (!pattern.matcher(carPlate).matches()) {
                System.out.println("Wrong plate! Try again");
                continue;
            }
            c = db.findCar(carPlate.trim());
            if (c != null){
                System.out.println("Car already present");
            } else
                check = true;
        }

        c = new Car();
        c.setPlate(carPlate.trim());

        System.out.print("Insert the Brand: ");
        c.setBrand((sc.nextLine()).trim());

        System.out.print("Insert the Vehicle: ");
        c.setVehicle(sc.nextLine());


        System.out.print("Insert the Engine: ");
        c.setEngine(sc.nextLine());

        /*
            Pattern power
         */
        check =true;
        System.out.print("Insert the Power: ");
        while(check){
            String tmpPower = sc.nextLine();
            String[] firstSplit = tmpPower.split("/");
            if (firstSplit.length == 2){
                String[] secondSplit = firstSplit[0].split("-");
                if (secondSplit.length == 2)
                    if (Integer.valueOf(firstSplit[1]) instanceof Integer && Integer.valueOf(secondSplit[0]) instanceof Integer && Integer.valueOf(firstSplit[1]) instanceof Integer){
                        c.setPower(tmpPower.trim());
                        check = false;
                        continue;
                    }
            }
            System.out.println("Insert the correct value of Power (HP-CV/RPM)!");
        }


        check = true;
        System.out.print("Insert the average fuel consumption (l/100km): ");
        while(check){
            String tmpAFC = sc.nextLine();
            try {
                if (Double.valueOf(tmpAFC) instanceof Double){
                    c.setAvgFuelCons(tmpAFC);
                    check=false;
                    continue;
                }
            } catch (Exception e){
                System.out.println("Insert the correct value of Average Fuel Consumption!");
            }
        }

        check = true;
        System.out.print("Insert the CO2: ");
        while(check){
            try {
                String tmpCO2 = sc.nextLine();
                if (Double.valueOf(tmpCO2) instanceof Double){
                    c.setCo2(tmpCO2);
                    check=false;
                    continue;
                }
            } catch (Exception e){
                System.out.println("Insert the correct value of CO2!");
            }


        }

        System.out.print("Insert the weight: ");
        c.setWeight(sc.nextLine());

        System.out.print("Insert the gearBox type: ");
        c.setGearBoxType(sc.nextLine());

        System.out.print("Insert the Tyre: ");
        c.setTyre(sc.nextLine());

        System.out.print("Insert the Traction type: ");
        c.setTractionType(sc.nextLine());

        check = true;
        System.out.print("Insert the Registration Year: ");
        while(check){
            String tmpRegistrationYear = sc.nextLine();
            try {
                if (Integer.valueOf(tmpRegistrationYear) instanceof Integer){
                    c.setRegistrationYear(Integer.valueOf(tmpRegistrationYear));
                    check=false;
                    continue;
                }
            } catch (Exception e){
                System.out.println("Insert the correct value of RegistrationYear!");
            }
        }

        System.out.println("Insert the Office: ");
        Office o = Office.selectOffice(db.listOffices());
        if (o == null)
            return;
        c.setOffice(o.getName());

        db.insertNewCar(c);
        c.printCar();
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
            System.out.println("Worker doesn't exist!");
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
                System.out.println("Choose the new office: ");
                ArrayList<Office> offices = db.listOffices();
                i = 1;
                for(Office o : offices){
                    System.out.print(i++ + ") ");
                    o.printOffice();
                }
                try {
                    i = Integer.valueOf(sc.nextLine());
                } catch (Exception p){
                    System.out.println("Error. Didn't insert an integer");
                    return;
                }
                int idOfficePick = i;
                if(i > offices.size() || i<1) {
                    System.out.println("Index out of range");
                    return;
                }


                /*
                System.out.println("Insert the new office: ");
                String office = sc.nextLine();
                Office o = db.findOfficeByName(office);
                if (o == null){
                    System.out.println("Office doesn't exist!");
                    return;
                }
                Integer position = o.getPosition();

                 */
                db.updateWorkerOffice(emailWorker, offices.get(i-1).getName());
                break;
        }
    }

    public void modifyCar(MongoDBConnection db){
        Scanner sc =new Scanner(System.in);
        System.out.println("Insert the carplate: ");
        String carPlate = sc.nextLine();
        Car c = db.findCar(carPlate);
        if (c == null){
            System.out.println("Car doesn't exist!");
            return;
        }

        System.out.println("Choose the new office: ");
        ArrayList<Office> offices = db.listOffices();
        int i = 1;
        for(Office o : offices){
            System.out.print(i++ + ") ");
            o.printOffice();
        }
        try {
            i = Integer.valueOf(sc.nextLine());
        } catch (Exception p){
            System.out.println("Error. Didn't insert an integer");
            return;
        }
        if(i > offices.size() || i<1) {
            System.out.println("Index out of range");
            return;
        }

        db.updateCarOffice(carPlate, offices.get(i-1).getName());
    }

    public void showMenu(){
        System.out.println("0) Exit");
        System.out.println("1) Show Orders by Parameters");
        System.out.println("2) Search cars by parameters");
        System.out.println("3) Modify Car");
        System.out.println("4) Add/Remove Cars");
        System.out.println("5) Find Worker");
        System.out.println("6) Add/Remove worker");
        System.out.println("7) Promote Worker to Admin");
        System.out.println("8) Modify Worker");
        System.out.println("9) Search User");
        System.out.println("10) Remove user");
        System.out.println("11) Show Analytics");
        System.out.println("12) Add/Remove Offices");
    }

    public void findWorker(MongoDBConnection db) throws ParseException {
        Scanner sc =new Scanner(System.in);
        System.out.println("Insert the Email of Worker: ");
        String emailWorker = sc.nextLine();
        Worker w = db.findWorker(emailWorker);
        if (w == null){
            System.out.println("Worker doesn't exist!");
            return;
        }
        w.printUser();
    }

    public void removeUser(MongoDBConnection db) {
        Scanner sc =new Scanner(System.in);
        System.out.println("Insert the Email of User: ");
        String emailUser = sc.nextLine();
        User u = db.findUser(emailUser);
        if (u == null){
            System.out.println("User doesn't exist!");
            return;
        }
        db.deleteUser(emailUser);
    }

    public void addRemoveCars(MongoDBConnection db){
        System.out.println("0) Exit");
        System.out.println("1) Add car");
        System.out.println("2) Remove car");
        Scanner sc = new Scanner(System.in);
        int i=0;
        try {
            i = Integer.valueOf(sc.nextLine());
        } catch (Exception e) {
            System.out.println("Error. Didn't insert an integer");

        }
        switch (i) {
            case 1:
                insertNewCar(db);
                break;
            case 2:
                deleteCar(db);
                break;
            default:
                return;
        }
    }

    public static void addRemoveOffice (MongoDBConnection db) throws ParseException {
        Integer i=0;
        System.out.println("1) Add Office");
        System.out.println("2) Remove Office");
        Scanner sc =new Scanner(System.in);
        try{
            i = Integer.valueOf(sc.nextLine());
        }
        catch(Exception e){
            System.out.println("Error. Didn't insert an integer");

        }

        switch (i) {
            case 1:
                boolean check = true;
                Office o;
                String name="";
                while (check) {

                    System.out.print("Insert the name: ");
                    name = sc.nextLine();
                    o = db.findOfficeByName(name.trim());

                    if (o != null ) {
                        System.out.println("Worker already exists!");
                    }
                    else{
                        check = false;
                    }
                }

                o = new Office();
                o.setName(name);

                System.out.print("Insert the city: ");
                o.setCity(sc.nextLine());

                System.out.print("Insert the region: ");
                o.setRegion(sc.nextLine());

                System.out.print("Insert the ID: ");
                o.setId(sc.nextLine());

                System.out.print("Insert the capacity: ");
                o.setCapacity(sc.nextLine());



                db.insertOffice(o);
                System.out.println("Office inserted successfully");
                break;

            case 2:
                System.out.println("Insert the Name of the office: ");
                name = sc.nextLine();
                o = db.findOfficeByName(name);
                if (o == null){
                    System.out.println("Office doesn't exist!");
                    break;
                }
                db.deleteOffice(name);
                System.out.println("Office deleted successfully");
                break;
        }

    }


    public void performAnalytics(MongoDBConnection db) throws ParseException {
        System.out.println("1) Get most used car per Office");
        System.out.println("2) Get les eco friendly Office");
        System.out.println("3) Search user for future discount");
        System.out.println("4) Most used accessories per year");
        int choice=0;
        Scanner sc = new Scanner(System.in);
        try {
            choice = Integer.valueOf(sc.nextLine());
        }
        catch(Exception e){
            choice = 100;
        }

        /*
         * fare try catch per date
         * */
        switch (choice) {
            case 1:
                System.out.println("Insert the Office: ");
                //String office = sc.nextLine();
                Office o = Office.selectOffice(db.listOffices());
                if(o == null)
                    break;
                System.out.print("Insert the date in which you want to start statistic: ");
                String date = sc.nextLine();
                Date date1;
                try {
                    date1 = new SimpleDateFormat("dd/MM/yyyy").parse(date.trim());
                } catch(Exception e){
                    System.out.println("Wrong date.");
                    break;
                }
                //db.getMostUsedCarsPerOffice(office.trim(), date1.getTime());
                db.getMostUsedCarsPerOffice(o.getName(), date1.getTime());
                break;
            case 2:
                db.getLessEcoFriendlyOffice();
                break;
            case 3:
                System.out.print("Insert the year in which you want to start statistic (ONLY THE YEAR): ");
                String lastYear = sc.nextLine();

                System.out.print("Insert the date in which you want to end statistic (ONLY THE YEAR): ");
                String currentYear = sc.nextLine();
                Date date2;
                try {
                    date1 = new SimpleDateFormat("dd/MM/yyyy").parse("01/01/" + lastYear.trim());
                    if(date1.getTime() > new Date().getTime()) {
                        System.out.println("Wrong date.");
                        break;
                    }
                    date2 = new SimpleDateFormat("dd/MM/yyyy").parse("01/01/" + currentYear.trim());
                    if((date2.getTime() < date1.getTime()) || (date2.getTime() > new Date().getTime())) {
                        System.out.println("Wrong date.");
                        break;
                    }
                } catch(Exception e){
                    System.out.println("Wrong Date.");
                    break;
                }
                db.searchUserForDiscount(date2.getTime(), date1.getTime());
                System.out.println();
                break;
            case 4:
                System.out.println("Insert the year in which you want to perform statistic: ");
                String yearString = sc.nextLine();
                Integer year = 2020;
                try {

                    year = Integer.valueOf(yearString);
                } catch (Exception e){
                    System.out.println("Wrong year");
                    break;
                }
                db.mostUsedAccessories(year);
                break;
            default:
                System.out.println("Wrong choice");
        }
    }
}
