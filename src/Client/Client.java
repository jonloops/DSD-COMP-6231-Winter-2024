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
        System.out.println("Concurrency Test Starting for BookAppointment");
        System.out.println("Connecting Montreal Server...");
        String AppointmentType = "Physician";
        String AppointmentID = "MTLE101020";
        ServerObjectInterface servant = ServerObjectInterfaceHelper.narrow(ncRef.resolve_str("MTL"));
        System.out.println("adding " + AppointmentID + " " + AppointmentType + " with capacity 2 to Montreal Server...");
        String response = servant.addAppointment("MTLM1111", AppointmentID, AppointmentType, 2);
        System.out.println(response);
        Runnable task1 = () -> {
            String patientID = "MTLP2345";
//            System.out.println("Connecting Montreal Server for " + patientID);
            String res = servant.bookAppointment(patientID, AppointmentID, AppointmentType);
            System.out.println("Booking response for " + patientID + " " + res);
            res = servant.cancelAppointment(patientID, AppointmentID, AppointmentType);
            System.out.println("Canceling response for " + patientID + " " + res);
        };
        Runnable task2 = () -> {
            String patientID = "MTLP3456";
//            System.out.println("Connecting Montreal Server for " + patientID);
            String res = servant.bookAppointment(patientID, AppointmentID, AppointmentType);
            System.out.println("Booking response for " + patientID + " " + res);
            res = servant.cancelAppointment(patientID, AppointmentID, AppointmentType);
            System.out.println("Canceling response for " + patientID + " " + res);
        };
        Runnable task3 = () -> {
            String patientID = "MTLP4567";
//            System.out.println("Connecting Montreal Server for " + patientID);
            String res = servant.bookAppointment(patientID, AppointmentID, AppointmentType);
            System.out.println("Booking response for " + patientID + " " + res);
            res = servant.cancelAppointment(patientID, AppointmentID, AppointmentType);
            System.out.println("Canceling response for " + patientID + " " + res);
        };
        Runnable task4 = () -> {
            String patientID = "MTLP6789";
//            System.out.println("Connecting Montreal Server for " + patientID);
            String res = servant.bookAppointment(patientID, AppointmentID, AppointmentType);
            System.out.println("Booking response for " + patientID + " " + res);
            res = servant.cancelAppointment(patientID, AppointmentID, AppointmentType);
            System.out.println("Canceling response for " + patientID + " " + res);
        };
        Runnable task5 = () -> {
            String patientID = "MTLP7890";
//            System.out.println("Connecting Montreal Server for " + patientID);
            String res = servant.bookAppointment(patientID, AppointmentID, AppointmentType);
            System.out.println("Booking response for " + patientID + " " + res);
            res = servant.cancelAppointment(patientID, AppointmentID, AppointmentType);
            System.out.println("Canceling response for " + patientID + " " + res);
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
        System.out.println("Concurrency Test Finished for BookAppointment");
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

    private static void patient(String patientID, NamingContextExt ncRef) throws Exception {
        String serverID = getServerID(patientID);
        if (serverID.equals("1")) {
            init(ncRef);
        }
        ServerObjectInterface servant = ServerObjectInterfaceHelper.narrow(ncRef.resolve_str(serverID));
        boolean repeat = true;
        printMenu(USER_TYPE_PATIENT);
        int menuSelection = input.nextInt();
        String AppointmentType;
        String AppointmentID;
        String serverResponse;
        switch (menuSelection) {
            case PATIENT_BOOK_APPOINTMENT:
                AppointmentType = promptForAppointmentType();
                AppointmentID = promptForAppointmentID();
                ClientLogger.clientLog(patientID, " attempting to bookAppointment");
                serverResponse = servant.bookAppointment(patientID, AppointmentID, AppointmentType);
                System.out.println(serverResponse);
                ClientLogger.clientLog(patientID, " bookAppointment", " AppointmentID: " + AppointmentID + " AppointmentType: " + AppointmentType + " ", serverResponse);
                break;
            case PATIENT_GET_APPOINTMENT_SCHEDULE:
                ClientLogger.clientLog(patientID, " attempting to getBookingSchedule");
                serverResponse = servant.getAppointmentSchedule(patientID);
                System.out.println(serverResponse);
                ClientLogger.clientLog(patientID, " bookAppointment", " null ", serverResponse);
                break;
            case PATIENT_CANCEL_APPOINTMENT:
                AppointmentType = promptForAppointmentType();
                AppointmentID = promptForAppointmentID();
                ClientLogger.clientLog(patientID, " attempting to cancelAppointment");
                serverResponse = servant.cancelAppointment(patientID, AppointmentID, AppointmentType);
                System.out.println(serverResponse);
                ClientLogger.clientLog(patientID, " bookAppointment", " AppointmentID: " + AppointmentID + " AppointmentType: " + AppointmentType + " ", serverResponse);
                break;
            case PATIENT_SWAP_APPOINTMENT:
                System.out.println("Please Enter the OLD Appointment to be replaced");
                AppointmentType = promptForAppointmentType();
                AppointmentID = promptForAppointmentID();
                System.out.println("Please Enter the NEW Appointment to be replaced");
                String newAppointmentType = promptForAppointmentType();
                String newAppointmentID = promptForAppointmentID();
                ClientLogger.clientLog(patientID, " attempting to swapAppointment");
                serverResponse = servant.swapAppointment(patientID, newAppointmentID, newAppointmentType, AppointmentID, AppointmentType);
                System.out.println(serverResponse);
                ClientLogger.clientLog(patientID, " swapAppointment", " oldAppointmentID: " + AppointmentID + " oldAppointmentType: " + AppointmentType + " newAppointmentID: " + newAppointmentID + " newAppointmentType: " + newAppointmentType + " ", serverResponse);
                break;
            case SHUTDOWN:
                ClientLogger.clientLog(patientID, " attempting ORB shutdown");
                servant.shutdown();
                ClientLogger.clientLog(patientID, " shutdown");
                return;
            case PATIENT_LOGOUT:
                repeat = false;
                ClientLogger.clientLog(patientID, " attempting to Logout");
                init(ncRef);
                break;
        }
        if (repeat) {
            patient(patientID, ncRef);
        }
    }

    private static void admin(String AppointmentAdminID, NamingContextExt ncRef) throws Exception {
        String serverID = getServerID(AppointmentAdminID);
        if (serverID.equals("1")) {
            init(ncRef);
        }
        ServerObjectInterface servant = ServerObjectInterfaceHelper.narrow(ncRef.resolve_str(serverID));
        boolean repeat = true;
        printMenu(USER_TYPE_ADMIN);
        String patientID;
        String AppointmentType;
        String AppointmentID;
        String serverResponse;
        int capacity;
        int menuSelection = input.nextInt();
        switch (menuSelection) {
            case ADMIN_ADD_APPOINTMENT:
                AppointmentType = promptForAppointmentType();
                AppointmentID = promptForAppointmentID();
                capacity = promptForCapacity();
                ClientLogger.clientLog(AppointmentAdminID, " attempting to addAppointment");
                serverResponse = servant.addAppointment(AppointmentAdminID, AppointmentID, AppointmentType, capacity);
                System.out.println(serverResponse);
                ClientLogger.clientLog(AppointmentAdminID, " addAppointment", " AppointmentID: " + AppointmentID + " AppointmentType: " + AppointmentType + " AppointmentCapacity: " + capacity + " ", serverResponse);
                break;
            case ADMIN_REMOVE_APPOINTMENT:
                AppointmentType = promptForAppointmentType();
                AppointmentID = promptForAppointmentID();
                ClientLogger.clientLog(AppointmentAdminID, " attempting to removeAppointment");
                serverResponse = servant.removeAppointment(AppointmentAdminID, AppointmentID, AppointmentType);
                System.out.println(serverResponse);
                ClientLogger.clientLog(AppointmentAdminID, " removeAppointment", " AppointmentID: " + AppointmentID + " AppointmentType: " + AppointmentType + " ", serverResponse);
                break;
            case ADMIN_LIST_APPOINTMENT_AVAILABILITY:
                AppointmentType = promptForAppointmentType();
                ClientLogger.clientLog(AppointmentAdminID, " attempting to listAppointmentAvailability");
                serverResponse = servant.listAppointmentAvailability(AppointmentAdminID, AppointmentType);
                System.out.println(serverResponse);
                ClientLogger.clientLog(AppointmentAdminID, " listAppointmentAvailability", " AppointmentType: " + AppointmentType + " ", serverResponse);
                break;
            case ADMIN_BOOK_APPOINTMENT:
                patientID = askForpatientIDFromAdmin(AppointmentAdminID.substring(0, 3));
                AppointmentType = promptForAppointmentType();
                AppointmentID = promptForAppointmentID();
                ClientLogger.clientLog(AppointmentAdminID, " attempting to bookAppointment");
                serverResponse = servant.bookAppointment(patientID, AppointmentID, AppointmentType);
                System.out.println(serverResponse);
                ClientLogger.clientLog(AppointmentAdminID, " bookAppointment", " patientID: " + patientID + " AppointmentID: " + AppointmentID + " AppointmentType: " + AppointmentType + " ", serverResponse);
                break;
            case ADMIN_GET_APPOINTMENT_SCHEDULE:
                patientID = askForpatientIDFromAdmin(AppointmentAdminID.substring(0, 3));
                ClientLogger.clientLog(AppointmentAdminID, " attempting to getBookingSchedule");
                serverResponse = servant.getAppointmentSchedule(patientID);
                System.out.println(serverResponse);
                ClientLogger.clientLog(AppointmentAdminID, " getBookingSchedule", " patientID: " + patientID + " ", serverResponse);
                break;
            case ADMIN_CANCEL_APPOINTMENT:
                patientID = askForpatientIDFromAdmin(AppointmentAdminID.substring(0, 3));
                AppointmentType = promptForAppointmentType();
                AppointmentID = promptForAppointmentID();
                ClientLogger.clientLog(AppointmentAdminID, " attempting to cancelAppointment");
                serverResponse = servant.cancelAppointment(patientID, AppointmentID, AppointmentType);
                System.out.println(serverResponse);
                ClientLogger.clientLog(AppointmentAdminID, " cancelAppointment", " patientID: " + patientID + " AppointmentID: " + AppointmentID + " AppointmentType: " + AppointmentType + " ", serverResponse);
                break;
            case ADMIN_SWAP_APPOINTMENT:
                patientID = askForpatientIDFromAdmin(AppointmentAdminID.substring(0, 3));
                System.out.println("Please Enter the OLD Appointment to be swapped");
                AppointmentType = promptForAppointmentType();
                AppointmentID = promptForAppointmentID();
                System.out.println("Please Enter the NEW Appointment to be swapped");
                String newAppointmentType = promptForAppointmentType();
                String newAppointmentID = promptForAppointmentID();
                ClientLogger.clientLog(AppointmentAdminID, " attempting to swapAppointment");
                serverResponse = servant.swapAppointment(patientID, newAppointmentID, newAppointmentType, AppointmentID, AppointmentType);
                System.out.println(serverResponse);
                ClientLogger.clientLog(AppointmentAdminID, " swapAppointment", " patientID: " + patientID + " oldAppointmentID: " + AppointmentID + " oldAppointmentType: " + AppointmentType + " newAppointmentID: " + newAppointmentID + " newAppointmentType: " + newAppointmentType + " ", serverResponse);
                break;
            case SHUTDOWN:
                ClientLogger.clientLog(AppointmentAdminID, " attempting ORB shutdown");
                servant.shutdown();
                ClientLogger.clientLog(AppointmentAdminID, " shutdown");
                return;
            case ADMIN_LOGOUT:
                repeat = false;
                ClientLogger.clientLog(AppointmentAdminID, "attempting to Logout");
                init(ncRef);
                break;
        }
        if (repeat) {
            admin(AppointmentAdminID, ncRef);
        }
    }

    private static String askForpatientIDFromAdmin(String branchAcronym) {
        System.out.println("Please enter a patientID(Within " + branchAcronym + " Server):");
        String userID = input.next().trim().toUpperCase();
        if (checkUserType(userID) != USER_TYPE_PATIENT || !userID.substring(0, 3).equals(branchAcronym)) {
            return askForpatientIDFromAdmin(branchAcronym);
        } else {
            return userID;
        }
    }

    private static void printMenu(int userType) {
        System.out.println("*************************************");
        System.out.println("Please choose an option below:");
        if (userType == USER_TYPE_PATIENT) {
            System.out.println("1.Book Appointment");
            System.out.println("2.Get Booking Schedule");
            System.out.println("3.Cancel Appointment");
            System.out.println("4.Swap Appointment");
            System.out.println("5.Logout");
            System.out.println("0.ShutDown");
        } else if (userType == USER_TYPE_ADMIN) {
            System.out.println("1.Add Appointment");
            System.out.println("2.Remove Appointment");
            System.out.println("3.List Appointment Availability");
            System.out.println("4.Book Appointment");
            System.out.println("5.Get Booking Schedule");
            System.out.println("6.Cancel Appointment");
            System.out.println("7.Swap Appointment");
            System.out.println("8.Logout");
            System.out.println("0.ShutDown");
        }
    }

    private static String promptForAppointmentType() {
        System.out.println("*************************************");
        System.out.println("Please choose an AppointmentType below:");
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
        return promptForAppointmentType();
    }

    private static String promptForAppointmentID() {
        System.out.println("*************************************");
        System.out.println("Please enter the AppointmentID (e.g MTLM190120)");
        String AppointmentID = input.next().trim().toUpperCase();
        if (AppointmentID.length() == 10) {
            if (AppointmentID.substring(0, 3).equalsIgnoreCase("MTL") ||
                    AppointmentID.substring(0, 3).equalsIgnoreCase("SHE") ||
                    AppointmentID.substring(0, 3).equalsIgnoreCase("QUE")) {
                if (AppointmentID.substring(3, 4).equalsIgnoreCase("M") ||
                        AppointmentID.substring(3, 4).equalsIgnoreCase("A") ||
                        AppointmentID.substring(3, 4).equalsIgnoreCase("E")) {
                    return AppointmentID;
                }
            }
        }
        return promptForAppointmentID();
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