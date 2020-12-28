package main.java;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

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
        System.out.println("3) Seach User");
        System.out.println("4) Pick Car");
        System.out.println("5) Delivery Car");
    }

}
