package Replica1.ServerInterface;


import java.rmi.*;

public interface AppointmentManagementInterface extends Remote{
    //Appointment Manager Role
    public String addAppointment(String AppointmentID,String AppointmentType,int bookingCapacity) throws RemoteException;
    public String removeAppointment(String AppointmentID,String AppointmentType) throws RemoteException;
    public String listAppointmentAvailability(String AppointmentType) throws RemoteException;
    //Customer Role
    public String bookAppointment(String customerID,String AppointmentID,String AppointmentType) throws RemoteException;
    public String getBookingSchedule(String customerID) throws RemoteException;
    public String cancelAppointment(String customerID,String AppointmentID, String AppointmentType) throws RemoteException;
    public String swapAppointment(String customerID, String newAppointmentID, String newAppointmentType, String oldAppointmentID, String oldAppointmentType) throws RemoteException;
    public String shutDown() throws RemoteException;
}