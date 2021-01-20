package main.java;

import com.mongodb.client.*;

import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import java.util.function.Consumer;

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.MergeOptions;
import com.mongodb.client.model.UnwindOptions;
import org.bson.*;
import org.bson.conversions.Bson;
import org.bson.json.JsonWriterSettings;

import javax.print.Doc;
import javax.swing.event.DocumentEvent;

import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Updates.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Accumulators.*;
import static com.mongodb.client.model.Sorts.*;

public class MongoDBConnection
{
    //private static Consumer<Document> printDocuments() {
      //  return doc -> System.out.println(doc.toJson());
    //}
    //Consumer<Document> printFormattedDocuments;
    private MongoClient mongoClient;
    private MongoDatabase db;
    private ArrayList<Office> offices = new ArrayList<Office>();
    private ArrayList<Service> services = new ArrayList<Service>();
    private ArrayList<Service> servicesWorker = new ArrayList<Service>();

    public MongoDBConnection(String database){
        //mongoClient = MongoClients.create();
         mongoClient = MongoClients.create(
                "mongodb://172.16.3.134:27022, 172.16.3.135:27022, 172.16.3.137:27022/");
        db = mongoClient.getDatabase("RentNGO");
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
        int i=0;
        while(cursor.hasNext()){
            Document d = cursor.next();
            Service s = new Service();
            s.setMultiplicator(d.getString("MULTIPLICATOR"));
            s.setPrice(Double.valueOf(d.getString("PRICE VAT INCLUDED ")));
            s.setNameService(d.getString("SERVICES"));
            switch (s.getNameService()){
                case "Deductible for insolvency or passive claim / car accident":
                    servicesWorker.add(s);
                    break;
                case "Administrative expenses for fines/tolls/parking":
                    servicesWorker.add(s);
                    break;
                case "Special vehicle clean":
                    servicesWorker.add(s);
                    break;
                case "Nav system loss":
                    servicesWorker.add(s);
                    break;
                case "Refective Jacket Loss":
                    servicesWorker.add(s);
                    break;
                case "Administrative expenses for damages":
                    servicesWorker.add(s);
                    break;
                case "One Way Same Area":
                    servicesWorker.add(s);
                    break;
                case "One Way Mainland":
                    servicesWorker.add(s);
                    break;
                case "One Way Mainland-Island":
                    servicesWorker.add(s);
                    break;
                case "Plate loss":
                    servicesWorker.add(s);
                    break;
                case "Keys Loss":
                    servicesWorker.add(s);
                    break;
                case "Car documents loss":
                    servicesWorker.add(s);
                    break;
                case "Truck Service":
                    servicesWorker.add(s);
                    break;
                default:
                    if(!s.getNameService().equals("Young Driver 19/20") && !s.getNameService().equals("Young Driver 21/24"))
                        services.add(s);
            }
            i++;
            //services.add(s);
        }

    }

    // String

    public ArrayList<Service> getServicesWorker(){
        return servicesWorker;
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
            return null;
        } else {
            Document d = cursor.next();
            u = createUser(d);
        }
        //u.printUser();

        return u;
    }

    public Worker findWorker(String email) throws ParseException {
        MongoCollection<Document> myColl = db.getCollection("workers");
        MongoCursor<Document> cursor  = myColl.find(eq("Email", email)).iterator();
        Worker w;

        if (!cursor.hasNext()) {
            return null;
        } else {
            Document d = cursor.next();
            w = createWorker(d);
        }

        w.printUser();

        return w;
    }

    private Worker createWorker(Document d) throws ParseException {
        //String surname, String name, String email, String password, Date dateOfBirth
        Date date1=new SimpleDateFormat("dd/MM/yyyy").parse(d.getString("DateOfBirth"));
        Date date2=new SimpleDateFormat("dd/MM/yyyy").parse(d.getString("Date of hiring"));

        Worker w = new Worker(d.getString("Surname"), d.getString("Name"),d.getString("Email"), d.getString("Password"), date1, Integer.valueOf(d.getString("Salary")),date2);
        return w;
    }

    private Admin createAdmin(Document d) throws ParseException {
        //String surname, String name, String email, String password, Date dateOfBirth
        Date date1=new SimpleDateFormat("dd/MM/yyyy").parse(d.getString("DateOfBirth"));
        Date date2=new SimpleDateFormat("dd/MM/yyyy").parse(d.getString("Date of hiring"));
        Date date3=new SimpleDateFormat("dd/MM/yyyy").parse(d.getString("Date WtoA"));
        Admin a = new Admin(d.getString("Surname"), d.getString("Name"),d.getString("Email"), d.getString("Password"), date1, Integer.valueOf(d.getString("Salary")), date2, date3);
        return a;
    }

    public Admin findAdmin(String email) throws ParseException {
        MongoCollection<Document> myColl = db.getCollection("admins");
        MongoCursor<Document> cursor  = myColl.find(eq("Email", email)).iterator();
        Admin a;

        if (!cursor.hasNext()) {
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
                .append("CarPrice",o.getPriceCar())
                .append("StartOffice", o.getpickOffice())
                .append("PickDate", o.getPickDate().getTime())
                .append("EndOffice", o.getDeliveryOffice())
                .append("DeliveryDate", o.getDeliveryDate().getTime())
                .append("PriceAccessories", o.getPriceAccessories())
                .append("ListAccessories", o.getAccessories())
                .append("Status", "Booked");
        myColl.insertOne(order);
    }


    public Car findCarByBrand(String brand, String vehicle){
        MongoCollection<Document> myColl = db.getCollection("cars");
        MongoCursor<Document> cursor  = myColl.find(and(eq("Brand", brand), eq("Vehicle", vehicle))).iterator();
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

   /* public void deleteOrders(){
        MongoCollection<Document> myColl = db.getCollection("orders");
        MongoCursor<Document> cursor  = myColl.find().iterator();
        while(cursor.hasNext()){
            Document d = cursor.next();
            if(d.getString("CarPlate").contains("@")){
                myColl.deleteOne(eq("CarPlate", d.getString("CarPlate")));
            }
        }
    } */

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
                .append("AverageFuelConsumption", Double.valueOf(c.getAvgFuelCons()))
                .append("CO2", Double.valueOf(c.getCo2()))
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
        //myColl.aggregate(Arrays.asList(b2, project, b1, b3))
         //       .forEach(printFormattedDocuments);
    }

    public void getCarsOutOfDate(Long currentDate){
        MongoCollection<Document> myColl = db.getCollection("orders");
        MongoCursor<Document> cursor = myColl.find(and(lt("DeliveryDate", currentDate),
                                                        ne("Status", "Completed"))).iterator();
        while(cursor.hasNext()){
            Document d = cursor.next();
            Date date = new Date(d.getLong("DeliveryDate"));
            System.out.println("CarPlate: "+ d.getString("CarPlate") + " ,DateDelivery: "+ date + " ,Status: " + d.getString("Status"));
        }
    }


    public ArrayList<Car> getListOfCars(int office, int category) {
        MongoCollection<Document> myColl = db.getCollection("cars");
        MongoCursor<Document> cursor = myColl.find(eq("Office", String.valueOf(office))).iterator();
        ArrayList<Car> cars = new ArrayList<>();
        while(cursor.hasNext()){
            Document d = cursor.next();
            Car c = getCarFromDocument(d);
            Double kw = c.getKw(this);
            switch (category){
                case 1:
                    if(kw < 75.0)
                        cars.add(c);
                    break;
                case 2:
                    if(kw >75.0 && kw <= 120.0)
                        cars.add(c);
                    break;
                case 3:
                    if(kw >120.0)
                        cars.add(c);
                    break;

                default:
                    cars.add(c);
            }

        }
        System.out.println(cars.size());
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
        Car c = new Car(d.getString("CarPlate"), d.getString("Brand"),
                d.getString("Vehicle"),
                d.getString("Engine"),
                String.valueOf(d.getDouble("AverageFuelConsumption")),
                String.valueOf(d.getDouble("CO2")),
                d.getString("Weight(3p/5p) kg"),
                d.getString("GearBox type"),
                d.getString("Tyre"),
                d.getString("Traction type"),
                d.getString("Power (hp - kW /rpm)"));
        return c;
    }

    public UnregisteredUser getUser(ArrayList<String> s) throws ParseException {
        //check if user
        if(s == null)
            return null;
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
            System.out.print("CarPrice: " + d.getDouble("CarPrice") + " ");
            Date datPick =new Date(Long.valueOf(d.getLong("PickDate")));
            System.out.print("DatePick: " + datPick.toString() + " ");
            Date datDelivery =new Date(Long.valueOf(d.getLong("DeliveryDate")));
            System.out.print("DateDelivery: " + datDelivery.toString() + " ");
            System.out.print("StartOffice: " + d.getString("StartOffice") + " ");
            System.out.print("EndOffice: " + d.getString("EndOffice") + " ");
            System.out.print("PriceAccessories: " + d.getDouble("PriceAccessories") + " ");
            System.out.print("ListAccessories: " + d.getString("ListAccessories") + " ");
            System.out.println();
            i++;
        }

    }

    public void getMostUsedCarsPerOffice(String startOffice, long date) {
        Consumer<Document> printFormattedDocuments = new Consumer<Document>() {
            @Override
            public void accept(Document document) {
                System.out.println(document.toJson(JsonWriterSettings.builder().indent(true).build()));
            }
        };
        Bson match1 = match(gt("PickDate", date));
        MongoCollection<Document> myColl = db.getCollection("orders");
        Bson sort = sort(descending(Arrays.asList("count")));
        Bson group = group(Arrays.asList("$StartOffice", "$CarPlate"), sum("count", 1));
        Bson match= match(eq("StartOffice", startOffice));
        Bson project = project(fields(include("_id", "count")));
        Bson limit = limit(3);
        myColl.aggregate(Arrays.asList(match1, match,group, sort, limit))
               .forEach(printFormattedDocuments);

    }


    public void getLessEcoFriendlyOffice(){ //BETTER LOWER ECO FRENDLY
        Consumer<Document> printFormattedDocuments = new Consumer<Document>() {
            @Override
            public void accept(Document document) {
                System.out.println(document.toJson(JsonWriterSettings.builder().indent(true).build()));
            }
        };
        MongoCollection<Document> myColl = db.getCollection("cars");
        Bson sort = sort(ascending("_id"));
        Bson group = group("$Office", avg("AvgCO2", "$CO2"));
        //Bson project = project(fields(include( "AvgCO2")));
        Bson limit = limit(3);
        myColl.aggregate(Arrays.asList( group, sort, limit))
                .forEach(printFormattedDocuments);
    }



    public void query4(long currentDate, long lastYearDate){
        Consumer<Document> printFormattedDocuments = new Consumer<Document>() {
            @Override
            public void accept(Document document) {
                System.out.println(document.toJson(JsonWriterSettings.builder().indent(true).build()));
            }
        };
        Bson group = group("$Email", sum("countCurrent", 1));
        Bson group2 = group("$Email", sum("countPrev", 1));
        Bson matchCurrent = match(gt("PickDate", currentDate));
        Bson matchPrev = match(and(gt("PickDate", lastYearDate), lt("PickDate", currentDate)));

        MongoCollection<Document> collection = db.getCollection("orders");

        collection.aggregate(Arrays.asList(
                matchCurrent,
                group,
                out("currentYear"))).toCollection();

        collection.aggregate(Arrays.asList(
                matchPrev,
                group2,
                out("prevYear"))).toCollection();

        Bson merge = merge("prevYear");

        MongoCollection<Document> myColl = db.getCollection("currentYear");
        MongoCursor<Document> cursor = myColl.aggregate(Arrays.asList( merge)).cursor();
        while(cursor.hasNext()){
            Document doc = cursor.next();
            if(doc.getInteger("countPrev") != null && doc.getInteger("countCurrent")!=null && (doc.getInteger("countPrev") - doc.getInteger("countCurrent")) > 4){
                System.out.println("User: "+ doc.getString("_id")
                 + ", Current Year Rent: "+ doc.getInteger("countCurrent") + ", Last Year Rent: "+ doc.getInteger("countPrev"));
            }
        }
    }

    public void changeStatusOrder(String carPlate, String email, String field, Date d, String status, String damage, double damageCost){
        MongoCollection<Document> myColl = db.getCollection("orders");
        MongoCursor<Document> cursor = myColl.find(and(eq("CarPlate", carPlate), lt(field, d.getTime()+30*1000*60*60), gt(field, d.getTime()-30*1000*60*60), eq("Email", email))).iterator();
        if(cursor.hasNext()) {
            myColl.updateOne(
                    (and(eq("CarPlate", carPlate), lt(field, d.getTime() + 12 * 1000 * 60 * 60), gt(field, d.getTime() - 12 * 1000 * 60 * 60), eq("Email", email)))
                    , set("Status", status));
            if(!damage.equals("")) {
                Document doc = cursor.next();
                String damageU = doc.getString("ListAccessories") + damage;
                Double damageCostU = doc.getDouble("PriceAccessories") + damageCost;
                myColl.updateOne(
                        (and(eq("CarPlate", carPlate), lt(field, d.getTime() + 12 * 1000 * 60 * 60), gt(field, d.getTime() - 12 * 1000 * 60 * 60), eq("Email", email)))
                        , set("ListAccessories", damageU));
                myColl.updateOne(
                        (and(eq("CarPlate", carPlate), lt(field, d.getTime() + 12 * 1000 * 60 * 60), gt(field, d.getTime() - 12 * 1000 * 60 * 60), eq("Email", email)))
                        , set("PriceAccessories", damageCost));
            }
        }
    }

    public void showListOrdersByParameters(String carplate, String pickOffice, String pickDate, String deliveryDate) {
        if (carplate != null){
            MongoCollection<Document> myColl = db.getCollection("orders");
            MongoCursor<Document> cursor = myColl.find(eq("CarPlate", carplate)).iterator();
            int j=0;
            while(cursor.hasNext()){
                Document d = cursor.next();
                System.out.print(j + ") ");
                System.out.print("CarPlate: " + d.getString("CarPlate") + " ");
                System.out.print("CarPrice: " + d.getDouble("CarPrice") + " ");
                Date datPick =new Date(Long.valueOf(d.getLong("PickDate")));
                System.out.print("DatePick: " + datPick.toString() + " ");
                Date datDelivery =new Date(Long.valueOf(d.getLong("DeliveryDate")));
                System.out.print("DateDelivery: " + datDelivery.toString() + " ");
                System.out.print("StartOffice: " + d.getString("StartOffice") + " ");
                System.out.print("EndOffice: " + d.getString("EndOffice") + " ");
                System.out.print("PriceAccessories: " + d.getDouble("PriceAccessories") + " ");
                System.out.print("ListAccessories: " + d.getString("ListAccessories") + " ");
                System.out.println();
                j++;
            }
        } else {
            MongoCollection<Document> myColl = db.getCollection("orders");
            MongoCursor<Document> cursor = myColl.find(and(eq("StartOffice", pickOffice), eq("PickDate", pickDate), eq("DeliveryDate", deliveryDate))).iterator();
            int j = 0;
            while (cursor.hasNext()) {
                Document d = cursor.next();
                System.out.print(j + ") ");
                System.out.print("CarPlate: " + d.getString("CarPlate") + " ");
                System.out.print("CarPrice: " + d.getDouble("CarPrice") + " ");
                Date datPick = new Date(Long.valueOf(d.getLong("PickDate")));
                System.out.print("DatePick: " + datPick.toString() + " ");
                Date datDelivery = new Date(Long.valueOf(d.getLong("DeliveryDate")));
                System.out.print("DateDelivery: " + datDelivery.toString() + " ");
                System.out.print("StartOffice: " + d.getString("StartOffice") + " ");
                System.out.print("EndOffice: " + d.getString("EndOffice") + " ");
                System.out.print("PriceAccessories: " + d.getDouble("PriceAccessories") + " ");
                System.out.print("ListAccessories: " + d.getString("ListAccessories") + " ");
                System.out.println();
                j++;
            }
        }
    }


    public ArrayList<Service> getServices() {
        return services;
    }

    public ArrayList<Order> getListOfRecentOrders() {
        MongoCollection<Document> myColl = db.getCollection("orders");
        Date d =new Date();
        MongoCursor<Document> cursor = myColl.find(gt("DeliveryDate", d.getTime())).iterator();
        ArrayList<Order> orders = new ArrayList<>();
        while(cursor.hasNext()){
            Order o =new Order();
            Document doc = cursor.next();
            o.setCar(doc.getString("CarPlate"));
            Date dPick = new Date(Long.valueOf(doc.getLong("PickDate")));
            o.setPickDate(dPick);
            Date dDelivery = new Date(Long.valueOf(doc.getLong("DeliveryDate")));
            o.setDeliveryDate(dDelivery);
            orders.add(o);
        }
        return orders;
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
