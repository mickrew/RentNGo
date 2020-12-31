package main.java;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Scanner;

public class Worker extends UnregisteredUser{
    int salary;
    Date hiringDate = new Date();

    public Worker (){
        super();
    }

    public Worker(String surname, String name, String email, String password, Date dateofbirth, int salary, Date hiringDate){
        super(surname, name, email, password, dateofbirth);
        this.salary = salary;
        this.hiringDate = hiringDate;
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

    public void showMenu(){
        System.out.println("0) Exit");
        System.out.println("1) Search Car by Parameters");
        System.out.println("2) Show Orders by Parameters"); //even delete or modify
        System.out.println("3) Search User");
        System.out.println("4) Pick Car");
        System.out.println("5) Delivery Car");
    }


    public static void searchCars(MongoDBConnection db){
        System.out.println("Select the parameter by which you want to search cars. ");
        Scanner sc = new Scanner(System.in);
        System.out.println("0) Exit");
        System.out.println("1) Search by Carplate");
        System.out.println("2) Search by Brand and Vehicle");
        int choice = Integer.valueOf(sc.nextLine());
        Car c;

        if (choice==0){
            System.out.println("Exit");
            return;
        }else if (choice==1){
            System.out.print("Insert carplate: ");
            String plate = sc.nextLine();
            c = db.findCar(plate);
        } else if (choice==2){
            System.out.print("Insert Brand: ");
            String brand = sc.nextLine();
            System.out.print("Insert Vehicle: ");
            String vehicle = sc.nextLine();
            c = db.findCarByBrand(brand, vehicle);
        }
    }

    public static void searchUser(MongoDBConnection db){
        System.out.print("Insert user-email: ");
        Scanner sc = new Scanner(System.in);
        String email;
        email = sc.nextLine();
        User u = new User();
        u = db.findUser(email);
        u.printUser();
    }

    public static void searchOrders(MongoDBConnection db) throws ParseException {
        Scanner sc = new Scanner(System.in);
        Order or = new Order();

        System.out.println("Select the parameter by which you want to search orders. ");
        System.out.println("0) Exit");
        System.out.println("1) Search by email");
        System.out.println("2) Search by carplate");
        System.out.println("3) Search by PickOffice and dates");
        String pickOffice = null;
        String carplate = null;
        String date1 = null;
        String date2 = null;
        int choice = Integer.valueOf(sc.nextLine());

        if (choice==0){
            System.out.println("Exit");
            return;
        } else if (choice==1){
            System.out.print("Insert email of user of which you want to search orders: ");
            String email = sc.nextLine();
            db.showListOrders(email);
        } else if (choice == 2){
            System.out.print("Insert carplate: ");
            carplate = sc.nextLine();
            db.showListOrdersByParameters(carplate, pickOffice, date1, date2);
        } else if (choice == 3){
            System.out.print("Insert pick office: ");
            pickOffice = sc.nextLine();
            System.out.println("Insert range of dates.");
            System.out.print("Insert first date: ");
            SimpleDateFormat  formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date d = (Date)formatter.parse(sc.nextLine());
            date1 = String.valueOf(d.getTime());
            System.out.print("Insert second date: ");
            Date d2 = (Date)formatter.parse(sc.nextLine());
            date2 = String.valueOf(d2.getTime());
            db.showListOrdersByParameters(carplate, pickOffice, date1, date2);
        }

    }


}
