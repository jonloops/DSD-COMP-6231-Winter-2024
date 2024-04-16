package FrontEnd.ServerObjectInterfaceApp;


/**
 * ServerObjectInterfaceApp/ServerObjectInterfaceOperations.java .
 * Generated by the IDL-to-Java compiler (portable), version "3.2"
 * from C:/Users/SepJaProROG/StudioProjects/DEMS_Project/src/FrontEnd/ServerObjectInterface.idl
 * Wednesday, April 8, 2020 7:11:05 PM EDT
 */

public interface ServerObjectInterfaceOperations {

    /**
     * Only manager
     */
    String addEvent(String managerID, String eventID, String eventType, int bookingCapacity);

    String removeEvent(String managerID, String eventID, String eventType);

    String listEventAvailability(String managerID, String eventType);

    /**
     * Both manager and Customer
     */
    String bookEvent(String customerID, String eventID, String eventType);

    String getBookingSchedule(String customerID);

    String cancelEvent(String customerID, String eventID, String eventType);

    String swapEvent(String customerID, String newEventID, String newEventType, String oldEventID, String oldEventType);

    void shutdown();
} // interface ServerObjectInterfaceOperations
