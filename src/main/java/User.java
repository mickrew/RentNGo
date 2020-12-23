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

public class User {
        String surname;
        String name;
        String email;
        String password;
        Date dateOfBirth = new Date();
        String role;

        User() {
        }

        User(String surname, String name, String email, String password, Date dateOfBirth){
            this.surname = surname;
            this.name = name;
            this.email = email;
            this.password = password;
            this.dateOfBirth = dateOfBirth;
        }

        public static User logIn(MongoDBConnection db){
            String email;
            String password;
            User u;


                System.out.println("Insert the Email");
                Scanner sc = new Scanner(System.in);
                email = sc.nextLine();

                System.out.println("Insert the Password");
                password = sc.nextLine();

                u = db.findUser(email);

                if (u==null){
                    Worker w = db.findWorker(email);
                    if (w==null){
                        Admin a = db.findAdmin(email);
                    }
                }


                /*
                if (u.getRole().equals("worker")){
                    return new Worker(u.getSurname(), u.getName(), u.getEmail(), u.getPassword(), u.getDateOfBirth());
                }
                */
            return u;


        }

        public void signIn(){
            Scanner sc = new Scanner(System.in);

            System.out.print("Insert the user name: ");
            setName(sc.nextLine());

            System.out.print("Insert the user surname: ");
            setSurname(sc.nextLine());

            //check email
            String[] a;
            do{
                System.out.print("Insert the user email: ");
                setEmail(sc.nextLine());
                a = getEmail().split("@");
            } while(Arrays.stream(a).count() != 2);

            System.out.print("Insert the user password: ");
            setPassword(sc.nextLine());

            System.out.print("Insert the date of birth. ( DD/MM/YYYY ): ");
            Date d= new Date();

            String dateString = sc.nextLine();
            //System.out.println(dateString);

            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            try {
                d = formatter.parse(dateString);
            } catch (ParseException p){
                System.out.println("Error");
            }
            setDateofbirth(d);
        }

    public String getRole() {
        return role;
    }

    public void setRole(String role){
            this.role = role;
    }

    public String getSurname(){
            return surname;
        }

        public void setSurname(String surname){
            this.surname = surname;
        }

        public String getName(){
            return name;
        }

        public void setName(String name){
            this.name = name;
        }

        public String getEmail(){
            return email;
        }

        public void setEmail(String email){
            this.email = email;
        }

        public String getPassword(){
            return password;
        }

        public void setPassword(String password){
            this.password = password;
        }

        public Date getDateOfBirth(){
            return dateOfBirth;
        }

        public void setDateofbirth(Date dateOfBirth){
            this.dateOfBirth = dateOfBirth;
        }

        public void printUser(){
            System.out.println("Surname: "+ getSurname() +", Name: " +getName() +", E-mail: " +getEmail() + ", Password: " + getPassword() + ", Date of birth: " +getDateOfBirth());
        }
}


