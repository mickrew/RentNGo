package main.java;

import com.mongodb.client.MongoCursor;
import org.bson.Document;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

import static com.mongodb.client.model.Filters.eq;

public class User extends UnregisteredUser{

        User() {
        }

        User(String surname, String name, String email, String password, Date dateOfBirth){
            super(surname, name, email, password, dateOfBirth);
        }

        public void showMenu(){
                System.out.println("0) Exit");
                System.out.println("1) Create new Order");
                System.out.println("2) Show Orders"); //even delete or modify
                System.out.println("3) Show Cart");
                System.out.println("4) Delete Account");
        }

}


