package main.java;

import com.mongodb.client.*;
import com.mongodb.ConnectionString;
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

    public MongoDBConnection(String database){
        mongoClient = MongoClients.create();
        db = mongoClient.getDatabase("local");
        Consumer<Document> printFormattedDocuments = new Consumer<Document>() {
            @Override
            public void accept(Document document) {
                System.out.println(document.toJson(JsonWriterSettings.builder().indent(true).build()));
            }
        };
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

    public void deleteUser() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Insert the user email: ");
        MongoCollection<Document> myColl = db.getCollection("users");

        String email = sc.nextLine();

        MongoCursor<Document> cursor  = myColl.find(eq("E-mail", email)).iterator();
        if (!cursor.hasNext()) {
            System.out.println("User not found");
            return ;
        }
        cursor.forEachRemaining(printFormattedDocuments);
        System.out.print("Do you want to proceed with the delete operation? (Y/N) ");
        String r = sc.nextLine();

        if(r.equals("Y")){
            myColl.deleteOne(eq("E-mail", email));
            System.out.println("User deleted successfully");
        } else {
            System.out.println("Operation failed");
        }

    }

    public void insertUser() {
        MongoCollection<Document> myColl = db.getCollection("users");
        Scanner sc = new Scanner(System.in);

        System.out.print("Insert the user name: ");
        String name = sc.nextLine();

        System.out.print("Insert the user surname: ");
        String surname = sc.nextLine();

        System.out.print("Insert the user email: ");
        String email = sc.nextLine();
        //check email
        String[] a = email.split("@");
        if(Arrays.stream(a).count() != 2){
            System.out.println("Wrong email");
            return ;
        } else {
            if(!a[1].equals("outlook.it") && !a[1].equals("outlook.com") && !a[1].equals("live.it") && !a[1].equals("live.com") && !a[1].equals("gmail.it"))
                return ;
        }
        //CHECK IF USER ALREADY PRESENT !!! (WITH ITERATOR)
        /*
        System.out.print("Insert the user password: ");
        String password = sc.nextLine();

        Document user = new Document("Name", name)
                .append("Surname", surname)
                .append("E-mail", email)
                .append("Password", password);
        myColl.insertOne(user); */
    }

    public void deleteOrder(String plate, String name,String surname) {
        MongoCollection<Document> myColl = db.getCollection("orders");
        MongoCursor<Document> cursor  = myColl.find(and(eq("CarPlate", plate),
                eq(" Name", name),eq(" Surname", surname) )).iterator();
        if(!cursor.hasNext()) {
            System.out.println("There are no orders");
            return ;
        }
        do {
            cursor.forEachRemaining(printFormattedDocuments);
        }  while (cursor.hasNext());

        System.out.print("Which one do you want to delete? (Insert date of pick) ");
        Scanner sc = new Scanner(System.in);
        String datePick = sc.nextLine();

        MongoCursor<Document> cursor2  = myColl.find(and(eq("CarPlate", plate),
                eq(" Name", name),eq(" Surname", surname), eq(" DatePick", datePick) )).iterator();


        if(cursor2.hasNext()) {
            myColl.deleteOne(and(eq("CarPlate", plate),
                    eq(" Name", name),eq(" Surname", surname), eq(" DatePick", datePick) ));
            System.out.println("Order deleted successfully");
        }  else {
            System.out.println("Operation failed");
        }

    }

    public void insertOrder() {
        MongoCollection<Document> myColl = db.getCollection("cars");

        Scanner sc = new Scanner(System.in);
        System.out.print("Insert the car plate");
        String carPlate = sc.nextLine();

        MongoCursor<Document> cursor  = myColl.find(eq(" CarPlate", carPlate)).iterator();
        if (!cursor.hasNext()) {
            System.out.println("Car not found");
            return ;
        }

        System.out.print("Insert the user name");
        String name = sc.nextLine();

        System.out.print("Insert the user surname");
        String surname = sc.nextLine();

        System.out.print("Insert the date of pick");
        String datePick = sc.nextLine();

        System.out.print("Insert the date of delivery");
        String dateDelivery = sc.nextLine();

        MongoCollection<Document> myCollOrder = db.getCollection("orders");
        Document order = new Document("CarPlate", carPlate)
                .append(" Name", name)
                .append(" Surname", surname)
                .append(" DatePick", datePick)
                .append(" DateDelivery", dateDelivery);
        myCollOrder.insertOne(order);
    }

    public void deleteCar(String plate) {
        MongoCollection<Document> myColl = db.getCollection("cars");
        MongoCursor<Document> cursor  = myColl.find(eq(" CarPlate", plate)).iterator();

        if (cursor.hasNext()) {
            Document d = cursor.next();
            Car c = getCarFromDocument(d);
            c.printCar();
        }  else {
            System.out.println("Car not found !");
            return ;
        }

        System.out.print("Do you want to proceed with the delete operation? (Y/N) ");
        Scanner sc = new Scanner(System.in);
        String a = sc.nextLine();
        if(a.equals("Y")) {
            myColl.deleteOne(eq(" CarPlate", plate));
            System.out.println("Car deleted succesfully");
        }  else {
            System.out.println("Operation failed");
        }
    }

    public void insertNewCar() {
        Scanner sc = new Scanner(System.in);

        Car c = new Car();
        String carPlate ;
        String regex = "[A-Z][A-Z][0-9][0-9][0-9][A-Z][A-Z]";
        Pattern pattern = Pattern.compile(regex);

        do {
            System.out.print("Insert the Car Plate: ");
            carPlate = sc.nextLine();

        } while(!pattern.matcher(carPlate).matches());
        MongoCollection<Document> myColl = db.getCollection("cars");

        try (MongoCursor<Document> cursor = myColl.find(eq(" CarPlate", carPlate)).iterator()) {
            while (cursor.hasNext()) {
                System.out.println("Car's plate already present in the database");
                return ;
            }
        }
        c.setPlate(carPlate);

        System.out.print("Insert the Brand: ");
        c.setBrand(sc.nextLine());

        System.out.print("Insert the Vehicle: ");
        c.setVehicle(sc.nextLine());


        System.out.print("Insert the Engine: ");
        c.setEngine(sc.nextLine());


        System.out.print("Insert the Power: ");
        c.setPower(sc.nextLine());


        System.out.print("Insert the average fuel consumption: ");
        c.setAvgFuelCons(sc.nextLine());


        System.out.print("Insert the CO2: ");
        c.setCo2(sc.nextLine());


        System.out.print("Insert the weight: ");
        c.setWeight(sc.nextLine());

        System.out.print("Insert the gearBox type: ");
        c.setGearBoxType(sc.nextLine());

        System.out.print("Insert the Tyre: ");
        c.setTyre(sc.nextLine());

        System.out.print("Insert the Traction type: ");
        c.setTractionType(sc.nextLine());

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
                .append(" CarPlate", c.getPlate());
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

    public void getInfo(String plate, String field, String collection) {
        MongoCollection<Document> myColl = db.getCollection(collection);
        myColl.find(eq(field, plate))
                .forEach(printFormattedDocuments);
        //myColl.find( exists("Power (hp - kW /rpm)", false) ).forEach(printFormattedDocuments);
        /*DeleteResult dr = myColl.deleteMany( exists(" CarPlate", false) ); //100464
        System.out.println("Deleted documents: " + dr.getDeletedCount()); */

    }

    public Car getCarFromDocument(Document d){
        Car c = new Car(d.getString(" CarPlate"), d.getString("Brand"), d.getString("Vehicle"),
                d.getString("Engine"), d.getString("Average fuel consumption (l/100 km)"), d.getString("CO2 (g/km)"),
                d.getString("Weight(3p/5p) kg"), d.getString("GearBox type"), d.getString("Tyre"),
                d.getString("Traction type"));
        return c;
    }

}
