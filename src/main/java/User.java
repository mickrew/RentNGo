package main.java;

import com.mongodb.client.MongoCursor;
import org.bson.Document;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
                System.out.println("5) Add/remove Accessories");
        }

        public Car chooseCar(ArrayList<Car> cars){
            int i=0;
            for(Car c: cars){
                System.out.print(i++ +") ");
                c.printCar();
            }
            System.out.println("To proceed with the payment you need to select a car");
            Scanner sc=new Scanner(System.in);
            i=sc.nextInt();
            if(i >= cars.size() || i < 0){
                System.out.println("Error");
                return null;
            }
            Car c = cars.get(i);
            return c;
        }

        public Order createOrder(ArrayList<Office> offices){
            Order o =new Order();
            o.chooseParameters(offices);
            return o;
        }

}


