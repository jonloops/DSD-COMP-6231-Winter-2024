package Client;

//import ClientLogger;
import ServerObjectInterfaceApp.ServerObjectInterface;
import ServerObjectInterfaceApp.ServerObjectInterfaceHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import java.util.Scanner;

public class Client {

	public static final int USER_TYPE_PATIENT = 1;
    public static final int USER_TYPE_ADMIN = 2;
    public static final int PATIENT_BOOK_APPOINTMENT = 1;
    public static final int PATIENT_GET_APPOINTMENT_SCHEDULE = 2;
    public static final int PATIENT_CANCEL_APPOINTMENT = 3; 
	public static final int PATIENT_SWAP_APPOINTMENT = 4;
    public static final int PATIENT_LOGOUT = 5;
    public static final int ADMIN_ADD_APPOINTMENT = 1;
    public static final int ADMIN_REMOVE_APPOINTMENT = 2;
    public static final int ADMIN_LIST_APPOINTMENT_AVAILABILITY = 3;
    public static final int ADMIN_BOOK_APPOINTMENT = 4;
    public static final int ADMIN_GET_APPOINTMENT_SCHEDULE = 5;
    public static final int ADMIN_CANCEL_APPOINTMENT = 6; 
	public static final int ADMIN_SWAP_APPOINTMENT = 7; 
    public static final int ADMIN_LOGOUT = 8;
    public static final int SHUTDOWN = 0;

    static Scanner input;

    public static void main(String[] args) throws Exception {
        try {
            ORB orb = ORB.init(args, null);
            // -ORBInitialPort 1050 -ORBInitialHost localhost
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            init(ncRef);
        } catch (Exception e) {
            System.out.println("Client ORB init exception: " + e);
            e.printStackTrace();
        }
    }

    public static void init(NamingContextExt ncRef) throws Exception {
        input = new Scanner(System.in);
        String userID;
        System.out.println("Please Enter your UserID: ");
        userID = input.next().trim().toUpperCase();   
        ClientLogger.clientLog(userID, " login attempt");
        switch (checkUserType(userID)) {
            case USER_TYPE_PATIENT:
                try {
                    System.out.println("Patient Login successful (" + userID + ")");
                    ClientLogger.clientLog(userID, " Patient Login successful");
                    patient(userID, ncRef);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case USER_TYPE_ADMIN:
                try {
                    System.out.println("Admin Login successful (" + userID + ")");
                    ClientLogger.clientLog(userID, " Admin Login successful");
                    admin(userID, ncRef);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                System.out.println("!!UserID is not in correct format");
                ClientLogger.clientLog(userID, " UserID is not in correct format");
                ClientLogger.deleteALogFile(userID);
                init(ncRef);
        } 
		}
/*
    private static void startConcurrencyTest(NamingContextExt ncRef) throws Exception {
        System.out.println("Concurrency Test Starting for BookEvent");
        System.out.println("Connecting Montreal Server...");
        String eventType = "Physician";
        String eventID = "MTLE101020";
        ServerObjectInterface servant = ServerObjectInterfaceHelper.narrow(ncRef.resolve_str("MTL"));
        System.out.println("adding " + eventID + " " + eventType + " with capacity 2 to Montreal Server...");
        String response = servant.addAppointment("MTLM1111", eventID, eventType, 2);
        System.out.println(response);
        Runnable task1 = () -> {
            String customerID = "MTLC2345";
//            System.out.println("Connecting Montreal Server for " + customerID);
            String res = servant.bookAppointment(customerID, eventID, eventType);
            System.out.println("Booking response for " + customerID + " " + res);
            res = servant.cancelAppointment(customerID, eventID, eventType);
            System.out.println("Canceling response for " + customerID + " " + res);
        };
        Runnable task2 = () -> {
            String customerID = "MTLC3456";
//            System.out.println("Connecting Montreal Server for " + customerID);
            String res = servant.bookAppointment(customerID, eventID, eventType);
            System.out.println("Booking response for " + customerID + " " + res);
            res = servant.cancelAppointment(customerID, eventID, eventType);
            System.out.println("Canceling response for " + customerID + " " + res);
        };
        Runnable task3 = () -> {
            String customerID = "MTLC4567";
//            System.out.println("Connecting Montreal Server for " + customerID);
            String res = servant.bookAppointment(customerID, eventID, eventType);
            System.out.println("Booking response for " + customerID + " " + res);
            res = servant.cancelAppointment(customerID, eventID, eventType);
            System.out.println("Canceling response for " + customerID + " " + res);
        };
        Runnable task4 = () -> {
            String customerID = "MTLC6789";
//            System.out.println("Connecting Montreal Server for " + customerID);
            String res = servant.bookAppointment(customerID, eventID, eventType);
            System.out.println("Booking response for " + customerID + " " + res);
            res = servant.cancelAppointment(customerID, eventID, eventType);
            System.out.println("Canceling response for " + customerID + " " + res);
        };
        Runnable task5 = () -> {
            String customerID = "MTLC7890";
//            System.out.println("Connecting Montreal Server for " + customerID);
            String res = servant.bookAppointment(customerID, eventID, eventType);
            System.out.println("Booking response for " + customerID + " " + res);
            res = servant.cancelAppointment(customerID, eventID, eventType);
            System.out.println("Canceling response for " + customerID + " " + res);
        };

        Thread thread1 = new Thread(task1);
        Thread thread2 = new Thread(task2);
        Thread thread3 = new Thread(task3);
        Thread thread4 = new Thread(task4);
        Thread thread5 = new Thread(task5);
//        synchronized (thread1) {
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();
//        }
        thread1.join();
        thread2.join();
        thread3.join();
        thread4.join();
        thread5.join();
//        if (!thread1.isAlive() && !thread2.isAlive() && !thread3.isAlive() && !thread4.isAlive() && !thread5.isAlive()) {
        System.out.println("Concurrency Test Finished for BookEvent");
        init(ncRef);
//        }
    }
*/
    private static String getServerID(String userID) {
        return "FrontEnd";
    }

    private static int checkUserType(String userID) {
        if (userID.length() == 8) {
            if (userID.substring(0, 3).equalsIgnoreCase("MTL") ||
                    userID.substring(0, 3).equalsIgnoreCase("QUE") ||
                    userID.substring(0, 3).equalsIgnoreCase("SHE")) {
                if (userID.substring(3, 4).equalsIgnoreCase("C")) {
                    return USER_TYPE_PATIENT;
                } else if (userID.substring(3, 4).equalsIgnoreCase("M")) {
                    return USER_TYPE_ADMIN;
                }
            }
        }
        return 0;
    }

    private static void patient(String customerID, NamingContextExt ncRef) throws Exception {
        String serverID = getServerID(customerID);
        if (serverID.equals("1")) {
            init(ncRef);
        }
        ServerObjectInterface servant = ServerObjectInterfaceHelper.narrow(ncRef.resolve_str(serverID));
        boolean repeat = true;
        printMenu(USER_TYPE_PATIENT);
        int menuSelection = input.nextInt();
        String eventType;
        String eventID;
        String serverResponse;
        switch (menuSelection) {
            case PATIENT_BOOK_APPOINTMENT:
                eventType = promptForEventType();
                eventID = promptForEventID();
                ClientLogger.clientLog(customerID, " attempting to bookEvent");
                serverResponse = servant.bookAppointment(customerID, eventID, eventType);
                System.out.println(serverResponse);
                ClientLogger.clientLog(customerID, " bookEvent", " eventID: " + eventID + " eventType: " + eventType + " ", serverResponse);
                break;
            case PATIENT_GET_APPOINTMENT_SCHEDULE:
                ClientLogger.clientLog(customerID, " attempting to getBookingSchedule");
                serverResponse = servant.getAppointmentSchedule(customerID);
                System.out.println(serverResponse);
                ClientLogger.clientLog(customerID, " bookEvent", " null ", serverResponse);
                break;
            case PATIENT_CANCEL_APPOINTMENT:
                eventType = promptForEventType();
                eventID = promptForEventID();
                ClientLogger.clientLog(customerID, " attempting to cancelEvent");
                serverResponse = servant.cancelAppointment(customerID, eventID, eventType);
                System.out.println(serverResponse);
                ClientLogger.clientLog(customerID, " bookEvent", " eventID: " + eventID + " eventType: " + eventType + " ", serverResponse);
                break;
            case PATIENT_SWAP_APPOINTMENT:
                System.out.println("Please Enter the OLD event to be replaced");
                eventType = promptForEventType();
                eventID = promptForEventID();
                System.out.println("Please Enter the NEW event to be replaced");
                String newEventType = promptForEventType();
                String newEventID = promptForEventID();
                ClientLogger.clientLog(customerID, " attempting to swapEvent");
                serverResponse = servant.swapAppointment(customerID, newEventID, newEventType, eventID, eventType);
                System.out.println(serverResponse);
                ClientLogger.clientLog(customerID, " swapEvent", " oldEventID: " + eventID + " oldEventType: " + eventType + " newEventID: " + newEventID + " newEventType: " + newEventType + " ", serverResponse);
                break;
            case SHUTDOWN:
                ClientLogger.clientLog(customerID, " attempting ORB shutdown");
                servant.shutdown();
                ClientLogger.clientLog(customerID, " shutdown");
                return;
            case PATIENT_LOGOUT:
                repeat = false;
                ClientLogger.clientLog(customerID, " attempting to Logout");
                init(ncRef);
                break;
        }
        if (repeat) {
            patient(customerID, ncRef);
        }
    }

    private static void admin(String eventManagerID, NamingContextExt ncRef) throws Exception {
        String serverID = getServerID(eventManagerID);
        if (serverID.equals("1")) {
            init(ncRef);
        }
        ServerObjectInterface servant = ServerObjectInterfaceHelper.narrow(ncRef.resolve_str(serverID));
        boolean repeat = true;
        printMenu(USER_TYPE_ADMIN);
        String customerID;
        String eventType;
        String eventID;
        String serverResponse;
        int capacity;
        int menuSelection = input.nextInt();
        switch (menuSelection) {
            case ADMIN_ADD_APPOINTMENT:
                eventType = promptForEventType();
                eventID = promptForEventID();
                capacity = promptForCapacity();
                ClientLogger.clientLog(eventManagerID, " attempting to addEvent");
                serverResponse = servant.addAppointment(eventManagerID, eventID, eventType, capacity);
                System.out.println(serverResponse);
                ClientLogger.clientLog(eventManagerID, " addEvent", " eventID: " + eventID + " eventType: " + eventType + " eventCapacity: " + capacity + " ", serverResponse);
                break;
            case ADMIN_REMOVE_APPOINTMENT:
                eventType = promptForEventType();
                eventID = promptForEventID();
                ClientLogger.clientLog(eventManagerID, " attempting to removeEvent");
                serverResponse = servant.removeAppointment(eventManagerID, eventID, eventType);
                System.out.println(serverResponse);
                ClientLogger.clientLog(eventManagerID, " removeEvent", " eventID: " + eventID + " eventType: " + eventType + " ", serverResponse);
                break;
            case ADMIN_LIST_APPOINTMENT_AVAILABILITY:
                eventType = promptForEventType();
                ClientLogger.clientLog(eventManagerID, " attempting to listEventAvailability");
                serverResponse = servant.listAppointmentAvailability(eventManagerID, eventType);
                System.out.println(serverResponse);
                ClientLogger.clientLog(eventManagerID, " listEventAvailability", " eventType: " + eventType + " ", serverResponse);
                break;
            case ADMIN_BOOK_APPOINTMENT:
                customerID = askForCustomerIDFromManager(eventManagerID.substring(0, 3));
                eventType = promptForEventType();
                eventID = promptForEventID();
                ClientLogger.clientLog(eventManagerID, " attempting to bookEvent");
                serverResponse = servant.bookAppointment(customerID, eventID, eventType);
                System.out.println(serverResponse);
                ClientLogger.clientLog(eventManagerID, " bookEvent", " customerID: " + customerID + " eventID: " + eventID + " eventType: " + eventType + " ", serverResponse);
                break;
            case ADMIN_GET_APPOINTMENT_SCHEDULE:
                customerID = askForCustomerIDFromManager(eventManagerID.substring(0, 3));
                ClientLogger.clientLog(eventManagerID, " attempting to getBookingSchedule");
                serverResponse = servant.getAppointmentSchedule(customerID);
                System.out.println(serverResponse);
                ClientLogger.clientLog(eventManagerID, " getBookingSchedule", " customerID: " + customerID + " ", serverResponse);
                break;
            case ADMIN_CANCEL_APPOINTMENT:
                customerID = askForCustomerIDFromManager(eventManagerID.substring(0, 3));
                eventType = promptForEventType();
                eventID = promptForEventID();
                ClientLogger.clientLog(eventManagerID, " attempting to cancelEvent");
                serverResponse = servant.cancelAppointment(customerID, eventID, eventType);
                System.out.println(serverResponse);
                ClientLogger.clientLog(eventManagerID, " cancelEvent", " customerID: " + customerID + " eventID: " + eventID + " eventType: " + eventType + " ", serverResponse);
                break;
            case ADMIN_SWAP_APPOINTMENT:
                customerID = askForCustomerIDFromManager(eventManagerID.substring(0, 3));
                System.out.println("Please Enter the OLD event to be swapped");
                eventType = promptForEventType();
                eventID = promptForEventID();
                System.out.println("Please Enter the NEW event to be swapped");
                String newEventType = promptForEventType();
                String newEventID = promptForEventID();
                ClientLogger.clientLog(eventManagerID, " attempting to swapEvent");
                serverResponse = servant.swapAppointment(customerID, newEventID, newEventType, eventID, eventType);
                System.out.println(serverResponse);
                ClientLogger.clientLog(eventManagerID, " swapEvent", " customerID: " + customerID + " oldEventID: " + eventID + " oldEventType: " + eventType + " newEventID: " + newEventID + " newEventType: " + newEventType + " ", serverResponse);
                break;
            case SHUTDOWN:
                ClientLogger.clientLog(eventManagerID, " attempting ORB shutdown");
                servant.shutdown();
                ClientLogger.clientLog(eventManagerID, " shutdown");
                return;
            case ADMIN_LOGOUT:
                repeat = false;
                ClientLogger.clientLog(eventManagerID, "attempting to Logout");
                init(ncRef);
                break;
        }
        if (repeat) {
            admin(eventManagerID, ncRef);
        }
    }

    private static String askForCustomerIDFromManager(String branchAcronym) {
        System.out.println("Please enter a customerID(Within " + branchAcronym + " Server):");
        String userID = input.next().trim().toUpperCase();
        if (checkUserType(userID) != USER_TYPE_PATIENT || !userID.substring(0, 3).equals(branchAcronym)) {
            return askForCustomerIDFromManager(branchAcronym);
        } else {
            return userID;
        }
    }

    private static void printMenu(int userType) {
        System.out.println("*************************************");
        System.out.println("Please choose an option below:");
        if (userType == USER_TYPE_PATIENT) {
            System.out.println("1.Book Event");
            System.out.println("2.Get Booking Schedule");
            System.out.println("3.Cancel Event");
            System.out.println("4.Swap Event");
            System.out.println("5.Logout");
            System.out.println("0.ShutDown");
        } else if (userType == USER_TYPE_ADMIN) {
            System.out.println("1.Add Event");
            System.out.println("2.Remove Event");
            System.out.println("3.List Event Availability");
            System.out.println("4.Book Event");
            System.out.println("5.Get Booking Schedule");
            System.out.println("6.Cancel Event");
            System.out.println("7.Swap Event");
            System.out.println("8.Logout");
            System.out.println("0.ShutDown");
        }
    }

    private static String promptForEventType() {
        System.out.println("*************************************");
        System.out.println("Please choose an eventType below:");
        System.out.println("1.Conferences");
        System.out.println("2.Seminars");
        System.out.println("3.Trade Shows");
        switch (input.nextInt()) {
            case 1:
                return "PHYSICIAN";
            case 2:
                return "SURGEON";
            case 3:
                return "DENTAL";
        }
        return promptForEventType();
    }

    private static String promptForEventID() {
        System.out.println("*************************************");
        System.out.println("Please enter the EventID (e.g MTLM190120)");
        String eventID = input.next().trim().toUpperCase();
        if (eventID.length() == 10) {
            if (eventID.substring(0, 3).equalsIgnoreCase("MTL") ||
                    eventID.substring(0, 3).equalsIgnoreCase("SHE") ||
                    eventID.substring(0, 3).equalsIgnoreCase("QUE")) {
                if (eventID.substring(3, 4).equalsIgnoreCase("M") ||
                        eventID.substring(3, 4).equalsIgnoreCase("A") ||
                        eventID.substring(3, 4).equalsIgnoreCase("E")) {
                    return eventID;
                }
            }
        }
        return promptForEventID();
    }

    private static int promptForCapacity() {
        System.out.println("*************************************");
        System.out.println("Please enter the booking capacity:");
        int cap = input.nextInt();
        if (cap > 0) {
            return cap;
        }
        return promptForCapacity();
    }
}