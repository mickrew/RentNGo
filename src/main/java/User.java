package main.java;

import java.util.Date;

public class User {
        String surname;
        String name;
        String email;
        String password;
        Date dateOfBirth = new Date();

    User() {
    }

    User(String surname, String name, String email, String password, Date dateOfBirth){
            this.surname = surname;
            this.name = name;
            this.email = email;
            this.password = password;
            this.dateOfBirth = dateOfBirth;
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
            System.out.print("User: \n");
            System.out.print("Surname: "+ getSurname() +", Name: " +getName() +", E-mail: " +getEmail() + ", Password: " + getPassword() + ", Date of birth: " +getDateOfBirth());
        }
}


