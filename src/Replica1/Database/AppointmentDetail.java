package Replica1.Database;

public class AppointmentDetail {
    public String AppointmentType, AppointmentID;
    public int bookingCapacity;

    public AppointmentDetail(String AppointmentType, String AppointmentID, int bookingCapacity)
    {
        this.AppointmentType = AppointmentType;
        this.AppointmentID = AppointmentID;
        this.bookingCapacity = bookingCapacity;
    }
//
//    @Override
//    public String toString() {
//    	return AppointmentType + ":" + AppointmentID;
//    }
}
