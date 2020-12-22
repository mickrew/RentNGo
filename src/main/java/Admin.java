package main.java;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

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
}
