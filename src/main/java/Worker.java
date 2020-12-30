package main.java;

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

/*
    public static Car searchCars(MongoDBConnection db){
        System.out.println("Select the parameter by which you want to search cars. ");
        Scanner sc = new Scanner(System.in);
        System.out.println("0) Exit");
        System.out.println("1) Search by carplate");
        System.out.println("2) Search by brand");
        int i = 0;
        i = sc.nextInt();
        Car c = new Car();

        if (i==0){
            System.out.println("Exit");
            return null;
        }
        if (i==1){
            System.out.println("Insert the carplate: ");
            String carplate = null;
            carplate = sc.nextLine();
            c = db.findCar(carplate);
        }
        return c;
    }*/
/*
    public static User searchUser(MongoDBConnection db){
        System.out.println("Insert user-email: ");
        Scanner sc = new Scanner(System.in);
        String email = null;
        email = sc.nextLine();
        User u = new User();
        u = db.findUser(email);
        return u;
    }

    public static Order searchOrder(MongoDBConnection db){
      /*  System.out.println("Insert user email: ");
        Scanner sc = new Scanner(System.in);
        String email = null;
        email = sc.nextLine();
        Order or = new Order();
        or = db.findOrder(email);

        System.out.println("Select the parameter by which you want to search orders. ");
        Scanner sc = new Scanner(System.in);
        System.out.println("0) Exit");
        System.out.println("1) Search by email");
        //System.out.println("3) Search by DeliveryDate");
        int i = 0;
        i = sc.nextInt();
        Order or = new Order();

        if (i==0){
            System.out.println("Exit");
            return null;
        } else if (i==1){
            System.out.println("Insert user email: ");
            String email = null;
            email = sc.nextLine();
            or = db.findOrder(email);

        }
        return or;
    }
*/
}
