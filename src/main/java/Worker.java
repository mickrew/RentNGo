package main.java;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class Worker extends User{
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



}
