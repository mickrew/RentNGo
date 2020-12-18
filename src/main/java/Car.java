package main.java;

public class Car {
    private
        String plate;
        String brand;
        String vehicle;
        String engine;
        String power;
        String avgFuelCons;
        String co2;
        String weight;
        String gearBoxType;
        String tyre;
        String tractionType;

    public
        Car(){
        }

        Car(String plate, String brand, String vehicle, String engine, String avgFuelCons, String co2, String weight
        , String gearBoxType, String tyre, String tractionType){
            this.plate = plate;
            this.brand = brand;
            this.vehicle = vehicle;
            this.engine = engine;
            this.power = power;
            this.avgFuelCons = avgFuelCons;
            this.co2 = co2;
            this.weight = weight;
            this.gearBoxType = gearBoxType;
            this.tyre = tyre;
            this.tractionType = tractionType;
        }

        public String getAvgFuelCons() {
            return avgFuelCons;
        }

        public String getBrand() {
            return brand;
        }

        public String getCo2() {
            return co2;
        }

        public String getEngine() {
            return engine;
        }

        public String getGearBoxType() {
            return gearBoxType;
        }

        public String getPlate() {
            return plate;
        }

        public String getPower() {
            return power;
        }

        public String getTractionType() {
            return tractionType;
        }

        public String getTyre() {
            return tyre;
        }

        public String getVehicle() {
            return vehicle;
        }

        public String getWeight() {
            return weight;
        }

        public void setGearBoxType(String gearBoxType) {
            this.gearBoxType = gearBoxType;
        }

        public void setAvgFuelCons(String avgFuelCons) {
            this.avgFuelCons = avgFuelCons;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public void setCo2(String co2) {
            this.co2 = co2;
        }

        public void setEngine(String engine) {
            this.engine = engine;
        }

        public void setPlate(String plate) {
            this.plate = plate;
        }

        public void setPower(String power) {
            this.power = power;
        }

        public void setTractionType(String tractionType) {
            this.tractionType = tractionType;
        }

        public void setTyre(String tyre) {
            this.tyre = tyre;
        }

        public void setVehicle(String vehicle) {
            this.vehicle = vehicle;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }

        public void printCar(){
            System.out.println("Car Plate: " + getPlate() + ", Brand: "+ getBrand() + ", Vehicle: " + getVehicle() + ", Engine: "
            + getEngine() + ", Average Fuel Consumption: " + getAvgFuelCons() + ", CO2: "+ getCo2()+ ", Weight: " + getWeight() +
                    ", GearBoxType: " + getGearBoxType() + ", Tyre: " + getTyre() + ", TractionType: " + getTractionType());
        }
}
