package main.java.actors;

import main.java.DBcreator;
import main.java.connections.LevelDBConnection;
import main.java.connections.MongoDBConnection;
import main.java.entities.Car;
import main.java.entities.Office;
import main.java.entities.Order;
import main.java.entities.Service;
import org.jasypt.util.text.BasicTextEncryptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class User {
    String email;
    String password;
    String name;
    String surname;
    Date dateOfBirth = new Date();

    static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    String pattern = "dd/MM/yyyy";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

    public static boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }


    public User(String surname, String name, String email, String password, Date dateofbirth) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.dateOfBirth = dateofbirth;
        this.password = password;
    }

    public User(){
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void printUser(){

        System.out.println("Surname: "+ getSurname() +", Name: " +getName() +", E-mail: " +getEmail() + ", Password: **********" + ", Date of birth: " +simpleDateFormat.format(getDateOfBirth()));
    }


    public void setDateofbirth(Date dateOfBirth){
        this.dateOfBirth = dateOfBirth;
    }



    public static User login(){
            try{
                System.out.println("Insert the email");
                Scanner sc = new Scanner(System.in);
                String email = sc.nextLine();
                System.out.println("Insert the password");
                String password = sc.nextLine();
                MongoDBConnection db = new MongoDBConnection("RentNGO");

                User u =  db.login(email, password);
                db.closeConnection();
                return u;
            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
    }

    public static User signin(){
        User u = new User();
        Scanner sc = new Scanner(System.in);
        System.out.print("Insert the user name: ");
        u.setName(sc.nextLine());

        System.out.print("Insert the user surname: ");
        u.setSurname(sc.nextLine());

        //check email
        String[] a;
        do{
            System.out.print("Insert the user email: ");
            u.setEmail(sc.nextLine());
        } while(validateEmail(u.getEmail())==false);


        BasicTextEncryptor bte = new BasicTextEncryptor();
        bte.setPassword("rentngo");
        System.out.print("Insert the user password: ");
        String psw = sc.nextLine();

        //String encrypted = bte.encrypt(psw);
        String encrypted = bte.encrypt(psw);
        /*
        String encrypted1 = bte.encrypt(psw);
        String encrypted3 = bte.decrypt(encrypted);
        String encrypted4 = bte.decrypt(encrypted1);
        */

        u.setPassword(encrypted);


        System.out.print("Insert the date of birth. ( DD/MM/YYYY ): ");
        Date d= new Date();

        String dateString = sc.nextLine();
        //System.out.println(dateString);

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        try {
            d = formatter.parse(dateString);
        } catch (Exception p){
            System.out.println("Error");
        }
        u.setDateofbirth(d);
        try{
            MongoDBConnection db = new MongoDBConnection("RentNGO");
            db.insertUser(u);
            db.closeConnection();
        }catch (Exception e){
            e.printStackTrace();
        }
        return u;
        //return new User(u.getSurname(), u.getName(), u.getEmail(), u.getPassword(), u.getDateOfBirth());
    }

    public void showMenu(){
                System.out.println("0) Exit");
                System.out.println("1) Create new Order");
                System.out.println("2) Show Orders"); //even delete or modify
                System.out.println("3) Show Cart");
                System.out.println("4) Delete Account");
        }

    public boolean searchForCar(MongoDBConnection db, LevelDBConnection ldb, User u, Scanner sc) {
        Order o = u.createOrder(db.listOffices());
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
        String choice;
        System.out.println("Do you want to look for a specific Brand? Y/N");
        choice = sc.nextLine();
        String brand;
        if(choice.equals("Y")) {
            System.out.println("Select the brand of the car or let it empty to get all the brands");
            brand = sc.nextLine();
        } else {
            brand = "";
        }
       // ldb.searchCar(o.getpickOffice(),o.getDeliveryOffice(), o.getPickDate(), o.getDeliveryDate(), db.getListOfCars(o.getpickOffice(),
         //       category, o.getPickDate().getTime(), o.getDeliveryDate().getTime(), brand), u);
        ldb.searchCar(o.getpickOffice(),o.getDeliveryOffice(), o.getPickDate(), o.getDeliveryDate(), db.getMostUsedCars(o.getpickOffice(),
                brand, category, o.getPickDate().getTime(), o.getDeliveryDate().getTime()), u);
        return true;
    }

    public Car chooseCar(ArrayList<Car> cars){
            int i=0;
            for(Car c: cars){
                System.out.print(i++ +") ");
                c.printCar();
            }
            //System.out.println("To proceed with the payment you need to select a car");
            Scanner sc=new Scanner(System.in);
            try {
                i = Integer.valueOf(sc.nextLine());
            }catch (Exception e){
                return null;
            }
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

    public void showCart(MongoDBConnection db, LevelDBConnection ldb, Scanner sc, User u) {

        ArrayList<Car> cars = ldb.getListOfCarsInCart(u.getEmail());
        double total;

        if(cars.isEmpty()){
            System.out.println("Cart is empty\n");
            return ;
        }

        for(Car c: cars){
            c.printCar();
            System.out.println("-The car price per day is: "+ Math.ceil(c.calcolatePrice()) + "€\n");
        }

        Order o = ldb.showOrderInfo(u.getEmail());
        if(o == null)
            return;
        //o.printOrder();
        System.out.println(o.getpickOffice()+ ", "+ o.getPickDate() + ", "+ o.getDeliveryOffice()+ ", "+ o.getDeliveryDate()+"\n");
        System.out.println("Insert 'Yes' if you want to proceed with the payment or 'Delete' to delete a car from the cart.");
        System.out.println("Insert another button to show the main menu");
        String  choice = sc.nextLine();
        if(choice.equals("Yes")){
            //Choose accessories

            ArrayList<Service> services = Service.chooseServices(db.getServices());

            System.out.println("Choose one car:");
            Car c = u.chooseCar(cars);
            if(c == null)
                return;
            db.procedeWithOrder(c, o.getPickDate().getTime(), o.getDeliveryDate().getTime(), email, o.getpickOffice(), o.getDeliveryOffice(), services);
            ldb.deleteUserCart(email);
        } else if (choice.equals("Delete")) {
            System.out.println("Choose a car:");
            Car c = u.chooseCar(cars);
            if(c == null)
                return;
            ldb.deleteCarFromCart(email, c.getBrand(), c.getVehicle(), c.getPower());
        }
    }

    public void showListOrder(MongoDBConnection db, User u) {
        db.showListOrders(u.getEmail());
    }

    public void deleteAccount(MongoDBConnection db, LevelDBConnection ldb, User u) {
        db.deleteUser(u.getEmail());
        ldb.deleteUserCart(u.getEmail());
        u = null;

    }
}


