package main.java;

import com.mongodb.client.*;
import com.mongodb.ConnectionString;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;

import com.mongodb.client.result.UpdateResult;

import java.util.function.Consumer;
import java.util.regex.Pattern;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.json.JsonWriterSettings;

import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Updates.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Accumulators.*;
import static com.mongodb.client.model.Sorts.*;

public class MongoDBConnection
{
    private static Consumer<Document> printDocuments() {
        return doc -> System.out.println(doc.toJson());
    }
    Consumer<Document> printFormattedDocuments;
    private MongoClient mongoClient;
    private MongoDatabase db;
    private ArrayList<Office> offices = new ArrayList<Office>();
    private ArrayList<Service> services = new ArrayList<Service>();

    public MongoDBConnection(String database){
        mongoClient = MongoClients.create();
        db = mongoClient.getDatabase("local");
        Consumer<Document> printFormattedDocuments = new Consumer<Document>() {
            @Override
            public void accept(Document document) {
                System.out.println(document.toJson(JsonWriterSettings.builder().indent(true).build()));
            }
        };
        MongoCollection<Document> myColl = db.getCollection("offices");
        MongoCursor<Document> cursor  = myColl.find().iterator();
        while(cursor.hasNext()){
            Document d = cursor.next();
            Office o = new Office();
            o.setCapacity(Integer.valueOf(d.getString("Capacity")));
            o.setCity(d.getString("City"));
            o.setId(d.getString("ID"));
            o.setName(d.getString("Name"));
            o.setRegion(d.getString("Region"));
            offices.add(o);
        }

        myColl = db.getCollection("services");
        cursor  = myColl.find().iterator();
        while(cursor.hasNext()){
            Document d = cursor.next();
            Service s = new Service();
            s.setMultiplicator(d.getString("MULTIPLICATOR"));
            s.setPrice(Double.valueOf(d.getString(" PRICE VAT INCLUDED ")));
            s.setNameService(d.getString("SERVICES"));
            services.add(s);
        }

    }
    /*
    public static void main(String[] args) {
        //-------------------------------
        //-----Connect to the MongoDB----
        //-------------------------------
        // 1 - Default URI "mongodb://localhost:27017"
        //ConnectionString uri = new ConnectionString("mongodb://localhost:27017");
        //MongoClient mongoClient = MongoClients.create(uri);

        // 2 - Connection uri (Atlas)
      /*  ConnectionString uri = new ConnectionString(
                "mongodb+srv://admin:<password>@largescaleandmultistruc.jhdlw.mongodb.net/<dbname>?retryWrites=true&w=majority");
        MongoClient mongoClientAtlas = MongoClients.create(uri); */

        //-------------------------------
        //---------Get database----------
        //-------------------------------
        //If the database does not exists, mongoDB will create a new one
      /*  Consumer<Document> printFormattedDocuments = new Consumer<Document>() {
            @Override
            public void accept(Document document) {
                System.out.println(document.toJson(JsonWriterSettings.builder().indent(true).build()));
            }
        }; */


        //getInfo(db, printFormattedDocuments, "", "Power (hp - kW /rpm)\", power", "cars");
        //getInfo(db, printFormattedDocuments, "AAAAAAA", "CarPlate", "orders"); //gibby
        //getInfo(db, printFormattedDocuments, " edward w ", " Surname", "orders"); //gibby
        //getMostUsedCars(db, printFormattedDocuments, 5);
        //System.out.println("Insert the new car");
        //insertNewCar(db);
        //getInfo(db, printFormattedDocuments, " AA001AA", " CarPlate", "cars");
        //System.out.println("Delete the car");
        //deleteCar(db, printFormattedDocuments, "AAAAAAA");
        //insertOrder(db);
        //deleteOrder(db, printFormattedDocuments, "AAAAAAA", "pippo", "pippo");
        //insertUser(db);
        //deleteUser(db, printFormattedDocuments);
        //getInfo(db, printFormattedDocuments, "andrea", "E-mail", "users"); //gibby
   /*     //---List all the collection names---
        for(String name : db.listCollectionNames()) {
            System.out.println(name);
        }

        //-------------------------------
        //---Get a specific collection---
        //-------------------------------
        MongoCollection<Document> myColl = db.getCollection("students");
        //---Count # of documents in a collection---
        System.out.println(myColl.countDocuments());
        //---Empty a collection---
        myColl.drop();
        myColl.deleteMany(new Document());

        //-------------------------------
        //--------Insert documents-------
        //-------------------------------
        // 1 - Insert a single document
        Document student = new Document("name", "Laura")
                .append("age", 25)
                .append("gender", "F")
                .append("grades", Arrays.asList(
                        new Document("mark",  25).append("DateOfExam", new Date()).append("name", "PerformanceEvaluation"),
                        new Document("mark",  30).append("DateOfExam", new Date()).append("name", "ComputerArchitecture"),
                        new Document("mark",  28).append("DateOfExam", new Date()).append("name", "LargeScale")
                ))
                .append("location", new Document("x", 203).append("y", 102));
        myColl.insertOne(student);

        // 2 -Insert multiple documents
        List<Document> documents = new ArrayList<>();
        List<String> names = Arrays.asList("Gionatan", "Luigi", "Marco", "Federico", "Paolo");
        for(String name: names)
        {
            int markPE = (int)((Math.random() * (30 - 18)) + 18);
            int markCA = (int)((Math.random() * (30 - 18)) + 18);
            int markLS = (int)((Math.random() * (30 - 18)) + 18);
            student = new Document("name", name)
                    .append("age", 25 + (int)((Math.random() * 5) - 2))
                    .append("gender", "M")
                    .append("grades", Arrays.asList(
                            new Document("mark",  markPE).append("DateOfExam", new Date()).append("name", "PerformanceEvaluation"),
                            new Document("mark",  markCA).append("DateOfExam", new Date()).append("name", "ComputerArchitecture"),
                            new Document("mark",  markLS).append("DateOfExam", new Date()).append("name", "LargeScale")
                    ))
                    .append("location", new Document("x", 203).append("y", 102));
            documents.add(student);
        }
        myColl.insertMany(documents);


        //-------------------------------
        //---------Find documents--------
        //-------------------------------
        // 1 - Find the all document
        try (MongoCursor<Document> cursor = myColl.find().iterator()) {
            while (cursor.hasNext()) {
                System.out.println(cursor.next().toJson());
            }
        }

        // 2 - Find the first document
        Document firstDoc = myColl.find().first();
        if(firstDoc != null) System.out.println(firstDoc.toJson());

        // 3 - Find documents through filters
        //      Possible filters: eq. lt, lte, gt, gte, and, or, ...
        Document dbDoc = myColl.find(eq("name", "Federico")).first();
        if(dbDoc != null) System.out.println(dbDoc.toJson());

        // a - Define a consumers statically: printDocuments()
        //      --> defined as a private static member function (defined above)
        // b - Define a consumer locally
        Consumer<Document> printFormattedDocuments = new Consumer<Document>() {
            @Override
            public void accept(Document document) {
                System.out.println(document.toJson(JsonWriterSettings.builder().indent(true).build()));
            }
        };
        //----Lambda version (more compact)----
        //Consumer<Document> printFormattedDocuments2 =
        //        document -> System.out.println(document.toJson(JsonWriterSettings.builder().indent(true).build()));

        // 25 <= age < 27
        myColl.find(and(gte("age", 25), lt("age", 27)))
                .forEach(printFormattedDocuments);

        //-------------------------------
        //--------Update documents-------
        //-------------------------------
        // 1 - Update a single document
        myColl.updateOne(eq("name", "Federico"), set("age", 25));
        Document newGrade = new Document("mark",  27).append("DateOfExam", new Date()).append("name", "Intelligent Systems");
        myColl.updateOne(eq("name", "Federico"), Updates.push("grades", newGrade));
        //myColl.updateOne(eq("name", "Federico"), addToSet("grades", newGrade));

        // 2 - Update many documents
        UpdateResult ur = myColl.updateMany(gt("age", 24), inc("age", 1));
        System.out.println("Modified documents: " + ur.getModifiedCount());
        myColl.updateMany(new Document(), rename("name", "student"));
        myColl.find().forEach(printDocuments());

        //-------------------------------
        //--------Delete documents-------
        //-------------------------------
        //Delete a single document
        myColl.deleteOne(eq("student", "Gionatan"));
        //Delete many documents
        DeleteResult dr = myColl.deleteMany(lt("age", 25));
        System.out.println("Deleted documents: " + dr.getDeletedCount());
        myColl.find().forEach(printDocuments());

        //Create an index
        myColl.createIndex(new Document("age", 1));
        // Execute in the MONGO SHELL
        // myColl.find().sort({"age": 1}).explain().queryPlanner.winningPlan
        // myColl.find().sort({"student": 1}).explain().queryPlanner.winningPlan

        mongoClient.close();
        //mongoClientAtlas.close();
    } */

    public void closeConnection(){
        mongoClient.close();
    }


    public User findUser(String email){
        MongoCollection<Document> myColl = db.getCollection("users");
        MongoCursor<Document> cursor  = myColl.find(eq("Email", email)).iterator();
        User u;

        if (!cursor.hasNext()) {
            System.out.println("User not found");
            return null;
        } else {
            Document d = cursor.next();
            u = createUser(d);
        }

        //u.printUser();

        return u;
    }
    public Worker findWorker(String email){
        MongoCollection<Document> myColl = db.getCollection("workers");
        MongoCursor<Document> cursor  = myColl.find(eq("Email", email)).iterator();
        Worker w;

        if (!cursor.hasNext()) {
            System.out.println("Worker not found");
            return null;
        } else {
            Document d = cursor.next();
            w = createWorker(d);
        }

        w.printUser();

        return w;
    }

    private Worker createWorker(Document d) {
        //String surname, String name, String email, String password, Date dateOfBirth
        Worker w = new Worker(d.getString("Surname"), d.getString("Name"),d.getString("Email"), d.getString("Password"), d.getDate("DateOfBirth"), d.getInteger("Salary"), d.getDate("Date of hiring"));
        return w;
    }

    private Admin createAdmin(Document d) {
        //String surname, String name, String email, String password, Date dateOfBirth
        Admin a = new Admin(d.getString("Surname"), d.getString("Name"),d.getString("Email"), d.getString("Password"), d.getDate("DateOfBirth"), d.getInteger("Salary"), d.getDate("Date of hiring"), d.getDate("Date WtoA"));
        return a;
    }

    public Admin findAdmin(String email){
        MongoCollection<Document> myColl = db.getCollection("admins");
        MongoCursor<Document> cursor  = myColl.find(eq("Email", email)).iterator();
        Admin a;

        if (!cursor.hasNext()) {
            System.out.println("Admin not found");
            return null;
        } else {
            Document d = cursor.next();
            a = createAdmin(d);
        }

        a.printUser();

        return a;
    }
    public void deleteUser(String email) {
        MongoCollection<Document> myColl = db.getCollection("users");
        MongoCursor<Document> cursor  = myColl.find(eq("Email", email)).iterator();
        myColl.deleteOne(eq("Email", email));
        System.out.println("User deleted successfully");
    }

    private User createUser(Document d) {
        ////////
        //////// errore d.getDate("DateOfBirth")
        ////////
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date;
        try {
            date = formatter.parse(d.getString("DateOfBirth"));
        } catch (ParseException e){
            System.out.println("Error");
            return null;
        }
        User u = new User(d.getString("Surname"), d.getString("Name"),d.getString("Email"), d.getString("Password"), date);
        return u;
    }

    public boolean insertUser(User u)  {
        MongoCollection<Document> myColl = db.getCollection("users");

        //check email
        MongoCursor<Document> cursor = myColl.find(eq("Email", u.getEmail())).iterator();
        if(cursor.hasNext()){
            System.out.println("User already present in the database");
            return false;
        }
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        Document user = new Document("Name", u.getName())
                    .append("Surname", u.getSurname())
                    .append("Email", u.getEmail())
                    .append("Password", u.getPassword())
                    .append("DateOfBirth",formatter.format(u.getDateOfBirth()));
        myColl.insertOne(user);

        return true;
    }

    public User logInUser(String email, String password){
        User u = new User();
        MongoCollection<Document> myColl = db.getCollection("users");
        MongoCursor<Document> cursor  = myColl.find(and(eq("Email", email),
                eq("Password", password))).iterator();
        if(!cursor.hasNext()) {
            System.out.println("Email or Password wrong, try again");
        } else{
            Document d = cursor.next();
            u = createUser(d);
        }
        return u;
    }

    public ArrayList<Office> listOffices(){
        return offices;
    }

    public ArrayList<Service> listServices(){
        return services;
    }

    public void deleteOrder(String email, int choice) {
        MongoCollection<Document> myColl = db.getCollection("orders");
        MongoCursor<Document> cursor = myColl.find(eq("Email", email)).iterator();
        int i=0;
        while(cursor.hasNext()){
            Document d = cursor.next();
            if(choice == i){
                myColl.deleteOne(d);
            }
            i++;
        }

    }

    public void insertOrder(Order o) {
        MongoCollection<Document> myColl = db.getCollection("orders");
        Document order = new Document("CarPlate", o.getCar())
                .append("Email", o.getUser())
                .append("CarPrice",o.getPriceCar().toString())
                .append("StartOffice", o.getpickOffice())
                .append("PickDate", String.valueOf(o.getPickDate().getTime()))
                .append("EndOffice", o.getDeliveryOffice())
                .append("DeliveryDate", String.valueOf(o.getDeliveryDate().getTime()))
                .append("PriceAccessories", o.getPriceAccessories().toString())
                .append("ListAccessories", o.getAccessories());
        myColl.insertOne(order);
    }

    public Car findCar(String plate){
        MongoCollection<Document> myColl = db.getCollection("cars");
        MongoCursor<Document> cursor  = myColl.find(eq("CarPlate", plate)).iterator();
        Car c;

        if (!cursor.hasNext()) {
            System.out.println("Car not found");
            return null;
        } else {
            Document d = cursor.next();
            c = getCarFromDocument(d);
        }

        c.printCar();
        System.out.println();

        return c;
    }
    public void deleteCar(String plate) {
        MongoCollection<Document> myColl = db.getCollection("cars");
        MongoCursor<Document> cursor  = myColl.find(eq("CarPlate", plate)).iterator();
        Car c;
        if (!cursor.hasNext()) {
            System.out.println("Car not found");
            return ;
        } else {
            myColl.deleteOne(eq("CarPlate", plate));
            System.out.println("Car deleted successfully");
        }

    }

    public void insertNewCar(Car c) {

        MongoCollection<Document> myColl = db.getCollection("cars");

        try (MongoCursor<Document> cursor = myColl.find(eq("CarPlate", c.getPlate())).iterator()) {
            while (cursor.hasNext()) {
                System.out.println("Car's plate already present in the database");
                return;
            }
        }
        Document car = new Document("Brand", c.getBrand())
                .append("Vehicle", c.getVehicle())
                .append("Engine", c.getEngine())
                .append("Power (hp - kW /rpm)", c.getPower())
                .append("Average fuel consumption (l/100 km)", c.getAvgFuelCons())
                .append("CO2 (g/km)", c.getCo2())
                .append("Weight(3p/5p) kg", c.getWeight())
                .append("GearBox type", c.getGearBoxType())
                .append("Tyre", c.getTyre())
                .append("Traction type", c.getTractionType())
                .append("CarPlate", c.getPlate());
        myColl.insertOne(car);

        System.out.println();
    }

    public void getMostUsedCars(int i) {
        MongoCollection<Document> myColl = db.getCollection("orders");
        Bson b1 = sort(descending("nUsed"));
        Bson b2 = group("$CarPlate", sum("nUsed", 1));
        Bson project = project(fields(include("CarPlate", "nUsed")));
        Bson b3 = limit(i);
        myColl.aggregate(Arrays.asList(b2, project, b1, b3))
                .forEach(printFormattedDocuments);
    }

    public ArrayList<Car> getListOfCars() {
        MongoCollection<Document> myColl = db.getCollection("cars");
        MongoCursor<Document> cursor = myColl.find().iterator();
        ArrayList<Car> cars = new ArrayList<>();
        while(cursor.hasNext()){
            Document d = cursor.next();
            Car c = getCarFromDocument(d);
            cars.add(c);
        }
        return cars;
    }

    public void updateUser(){
        MongoCollection<Document> myColl = db.getCollection("users");
        MongoCursor<Document> cursor = myColl.find().iterator();
        while(cursor.hasNext()){
            Document d = cursor.next();
            myColl.updateOne(eq("Email", d.get("Email")), set("Email", d.get("Email").toString().trim()));
            myColl.updateOne(eq("Name", d.get("Name")), set("Name", d.get("Name").toString().trim()));
            myColl.updateOne(eq("Password", d.get("Password")), set("Password", d.get("Password").toString().trim()));
            myColl.updateOne(eq("DateOfBirth", d.get("DateOfBirth")), set("DateOfBirth", d.get("DateOfBirth").toString().trim()));
        }
    }

    public Car getCarFromDocument(Document d){
        Car c = new Car(d.getString(" CarPlate"), d.getString("Brand"), d.getString("Vehicle"),
                d.getString("Engine"), d.getString("Average fuel consumption (l/100 km)"), d.getString("CO2 (g/km)"),
                d.getString("Weight(3p/5p) kg"), d.getString("GearBox type"), d.getString("Tyre"),
                d.getString("Traction type"), d.getString("Power (hp - kW /rpm)"));
        return c;
    }

    public UnregisteredUser getUser(ArrayList<String> s) {
        //check if user
        User us = findUser(s.get(0));
        if(us != null){
            if(s.get(1).equals(us.getPassword())) {
                us.printUser();
                return new User(us.getSurname(), us.getName(), us.getEmail(), us.getPassword(), us.getDateOfBirth());
            }
            return null;
            //u=us;
        }

        Worker w = findWorker(s.get(0));
        if(w != null){
            return new Worker(w.getSurname(), w.getName(), w.getEmail(), w.getPassword(), w.getDateOfBirth(), w.getSalary(), w.getHiringDate());
        }

        Admin a = findAdmin(s.get(0));
        if(a != null){
            return new Admin(a.getSurname(), a.getName(), a.getEmail(), a.getPassword(), a.getDateOfBirth(), a.getSalary(), a.getHiringDate(), a.getWtoAdDate());
        }
        System.out.println("User not found");
        return null;
    }

    public void showListOrders(String email) {
        MongoCollection<Document> myColl = db.getCollection("orders");
        MongoCursor<Document> cursor = myColl.find(eq("Email", email)).iterator();
        int i=0;
        while(cursor.hasNext()){
            Document d = cursor.next();
            System.out.print(i + ") ");
            System.out.print("CarPlate: " + d.getString("CarPlate") + " ");
            System.out.print("CarPrice: " + d.getString("CarPrice") + " ");
            Date datPick =new Date(Long.valueOf(d.getString("PickDate")));
            System.out.print("DatePick: " + datPick.toString() + " ");
            Date datDelivery =new Date(Long.valueOf(d.getString("DeliveryDate")));
            System.out.print("DateDelivery: " + datDelivery.toString() + " ");
            System.out.print("StartOffice: " + d.getString("StartOffice") + " ");
            System.out.print("EndOffice: " + d.getString("EndOffice") + " ");
            System.out.print("PriceAccessories: " + d.getString("PriceAccessories") + " ");
            System.out.print("ListAccessories: " + d.getString("ListAccessories") + " ");
            System.out.println();
            i++;
        }

    }

    public ArrayList<Service> getServices() {
        return services;
    }
/*
    public User logIn(ArrayList<String> parameters){
        String email = parameters.get(0);
        User u = findUser(email);
        if (u==null) {
            u = findWorker(email);
            if (u == null) {
                u = findAdmin(email);
            }
        }
        return u;
    }
*/

    /*

    public void createWorker(){
        MongoCollection<Document> myColl = db.getCollection("users");
        MongoCollection<Document> myColl1 = db.getCollection("workers");


        MongoCursor<Document> cursor  = myColl.find().limit(50).iterator();

        while (cursor.hasNext()) {

            Document d = cursor.next();
            d.remove("_id");
            d.append("Salary", 1200);
            d.append("Date of hiring", "01/01/2000");

            myColl1.insertOne(d);
            deleteUser(d.getString("Email"));
        }

    }

    public void createAdmin(){
    MongoCollection<Document> myColl = db.getCollection("users");
    MongoCollection<Document> myColl1 = db.getCollection("admins");


    MongoCursor<Document> cursor  = myColl.find().limit(20).iterator();

    while (cursor.hasNext()) {

        Document d = cursor.next();
        d.remove("_id");
        d.append("Salary", 2000);
        d.append("Date of hiring", "01/01/2000");
        d.append("Date WtoA", "01/01/2010");

        myColl1.insertOne(d);
        deleteUser(d.getString("Email"));
    }

    }
    */

}
