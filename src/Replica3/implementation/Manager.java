package Replica3.implementation;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Manager extends Remote{

	//public int add(int x, int y) throws RemoteException;
	public String addAppointment (String appointmentID,String appointmentType,int bookingCapacity,String serv) throws RemoteException;
	public String removeAppointment (String appointmentID,String appointmentType,String serv) throws RemoteException;
	public String listAppointmentAvailability (String appointmentType,String serv) throws RemoteException;
	       
	public String bookAppointment (String customerID,String appointmentID,String appointmentType,String serv) throws RemoteException;
	public String getBookingSchedule (String customerID,String serv) throws RemoteException;
	public String cancelAppointment (String customerID,String appointmentID,String appointmentType,String serv)  throws RemoteException;
	public String swapAppointment(String customerID, String newAppointmentID,
			String newAppointmentType, String oldAppointmentID, String oldAppointmentType,
			String serv) throws RemoteException;
	public String shutDown() throws RemoteException ;
}