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

            return;
        } else if (choice==2){
            System.out.print("Insert Brand: ");
            String brand = sc.nextLine();
            System.out.print("Insert Vehicle: ");
            String vehicle = sc.nextLine();
            c = db.findCarByBrand(brand, vehicle);
            return;
        }
    }

    public static void searchUser(MongoDBConnection db){
        System.out.println("Insert user-email: ");
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
        System.out.println("2) Search by PickOffice");
        int choice = Integer.valueOf(sc.nextLine());

        if (choice==0){
            System.out.println("Exit");
            return;
        } else if (choice==1){
            System.out.print("Insert email of user of which you want to search orders: ");
            String email = sc.nextLine();
            //or = db.findOrder(email);
            db.showListOrders(email);
            return;
        } else if (choice == 2){
            System.out.print("Insert pick office: ");
            String pickOffice = sc.nextLine();
            System.out.println("Insert range of dates.");
            System.out.print("Insert first date: ");
            SimpleDateFormat  formatter=new SimpleDateFormat("dd/MM/yyyy");
            Date d = formatter.parse(sc.nextLine());

            System.out.print("Insert second date: ");
            Date d = formatter.parse(sc.nextLine());
            db.showListOrdersByOffice(pickOffice);

        }
        //return or;
        //return null;
    }


}
