package main.java;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

public class UnregisteredUser {
    String email;
    String password;
    String name;
    String surname;
    Date dateOfBirth = new Date();

    public UnregisteredUser(String surname, String name, String email, String password, Date dateofbirth) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.dateOfBirth = dateofbirth;
        this.password = password;
    }

    public UnregisteredUser(){

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

    public static ArrayList<String> logIn() {
        ArrayList<String> r =new ArrayList<>();
        System.out.print("Insert the email ");
        Scanner sc = new Scanner(System.in);
        r.add(sc.nextLine());

        System.out.print("Insert the password ");
        r.add(sc.nextLine());
        return r;
    }

    public void printUser(){
        System.out.println("Surname: "+ getSurname() +", Name: " +getName() +", E-mail: " +getEmail() + ", Password: " + getPassword() + ", Date of birth: " +getDateOfBirth());
    }

    public static User  signIn(){
        User u= new User();
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
                a = u.getEmail().split("@");
            } while(Arrays.stream(a).count() != 2);

            System.out.print("Insert the user password: ");
            u.setPassword(sc.nextLine());

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
            return u;
    }

    public void setDateofbirth(Date dateOfBirth){
        this.dateOfBirth = dateOfBirth;
    }
}
