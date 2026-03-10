import java.util.*;

class ParkingSpot {
    String licensePlate;
    long entryTime;
    String status;

    ParkingSpot() {
        status = "EMPTY";
    }
}

public class ParkingLot {

    private ParkingSpot[] table;
    private int size;
    private int occupied = 0;
    private int totalProbes = 0;
    private int totalParks = 0;

    public ParkingLot(int capacity) {
        size = capacity;
        table = new ParkingSpot[size];
        for (int i = 0; i < size; i++) {
            table[i] = new ParkingSpot();
        }
    }

    private int hash(String licensePlate) {
        return Math.abs(licensePlate.hashCode()) % size;
    }

    public void parkVehicle(String licensePlate) {

        int index = hash(licensePlate);
        int probes = 0;

        while (!table[index].status.equals("EMPTY") && !table[index].status.equals("DELETED")) {
            index = (index + 1) % size;
            probes++;
        }

        table[index].licensePlate = licensePlate;
        table[index].entryTime = System.currentTimeMillis();
        table[index].status = "OCCUPIED";

        occupied++;
        totalProbes += probes;
        totalParks++;

        System.out.println("Assigned spot #" + index + " (" + probes + " probes)");
    }

    public void exitVehicle(String licensePlate) {

        int index = hash(licensePlate);

        while (!table[index].status.equals("EMPTY")) {

            if (table[index].status.equals("OCCUPIED") &&
                    table[index].licensePlate.equals(licensePlate)) {

                long durationMs = System.currentTimeMillis() - table[index].entryTime;
                double hours = durationMs / 3600000.0;
                double fee = hours * 5;

                table[index].status = "DELETED";
                occupied--;

                System.out.println("Spot #" + index + " freed, Duration: "
                        + String.format("%.2f", hours) + "h, Fee: $" +
                        String.format("%.2f", fee));
                return;
            }

            index = (index + 1) % size;
        }

        System.out.println("Vehicle not found");
    }

    public void getStatistics() {

        double occupancyRate = (occupied * 100.0) / size;
        double avgProbes = totalParks == 0 ? 0 : (totalProbes * 1.0) / totalParks;

        System.out.println("Occupancy: " + String.format("%.2f", occupancyRate) + "%");
        System.out.println("Avg Probes: " + String.format("%.2f", avgProbes));
    }

    public static void main(String[] args) throws Exception {

        ParkingLot lot = new ParkingLot(500);

        lot.parkVehicle("ABC-1234");
        lot.parkVehicle("ABC-1235");
        lot.parkVehicle("XYZ-9999");

        Thread.sleep(2000);

        lot.exitVehicle("ABC-1234");

        lot.getStatistics();
    }
}