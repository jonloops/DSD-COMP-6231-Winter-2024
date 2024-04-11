package Replica1.Database;

public class ClientDetail {
    public String customerID, AppointmentID, eventType;
    public int outer_city_limit;

    public ClientDetail(String customerID, String eventType, String eventID)
    {
        this.customerID = customerID;
        this.eventType = eventType;
        this.AppointmentID = eventID;
    }
}
