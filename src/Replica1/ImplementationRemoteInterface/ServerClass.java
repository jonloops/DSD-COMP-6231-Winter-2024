package Replica1.ImplementationRemoteInterface;

import Replica1.CommonOutput;
import Replica1.Database.ClientDetail;
import Replica1.Database.AppointmentDetail;
import Replica1.ServerInterface.AppointmentManagementInterface;


import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class ServerClass extends UnicastRemoteObject implements AppointmentManagementInterface {

    private ConcurrentHashMap<String, ConcurrentHashMap<String, AppointmentDetail>> AppointmentMap;
    private ConcurrentHashMap<String, ConcurrentHashMap<String, ClientDetail>> ClientMap;
    private final int quebec_port;
    private final int montreal_port;
    private final int sherbrooke_port;
    private final String serverName;

    public ServerClass(int quebec_port, int montreal_port, int sherbrooke_port, String serverName) throws RemoteException {
        super();

        this.quebec_port = quebec_port;
        this.montreal_port = montreal_port;
        this.sherbrooke_port = sherbrooke_port;
        this.serverName = serverName.toUpperCase().trim();
        AppointmentMap = new ConcurrentHashMap<>();
        ClientMap = new ConcurrentHashMap<>();

    }

    @Override
    public synchronized String addAppointment( String AppointmentID,  String AppointmentType,  int bookingCapacity) throws RemoteException {
        if(AppointmentMap.containsKey(AppointmentType.toUpperCase().trim()) && AppointmentMap.get(AppointmentType.toUpperCase().trim()).containsKey(AppointmentID.toUpperCase().trim()))
        {
            int currentCapacity = AppointmentMap.get(AppointmentType.toUpperCase().trim()).get(AppointmentID.toUpperCase().trim()).bookingCapacity;
            AppointmentMap.get(AppointmentType.toUpperCase().trim()).replace(AppointmentID,new AppointmentDetail(AppointmentType.toUpperCase().trim(), AppointmentID.toUpperCase().trim(), currentCapacity + bookingCapacity));

            try
            {
                serverLog("Add Appointment", " AppointmentType:"+AppointmentType+ " AppointmentID:"+AppointmentID +
                        "bookingCapacity:"+ bookingCapacity,"successfully completed", "Capacity added to Appointment");
            } catch ( IOException e) {
                e.printStackTrace();
            }
            return CommonOutput.addAppointmentOutput(true, CommonOutput.addAppointment_success_capacity_updated);
        }
        else if(AppointmentMap.containsKey(AppointmentType.toUpperCase().trim()))
        {
            AppointmentMap.get(AppointmentType.toUpperCase().trim()).put(AppointmentID.toUpperCase().trim(), new AppointmentDetail(AppointmentType.toUpperCase().trim(), AppointmentID.toUpperCase().trim(), bookingCapacity));
            try
            {
                serverLog("Add Appointment", " AppointmentType:"+AppointmentType+ " AppointmentID:"+AppointmentID +
                        "bookingCapacity:"+ bookingCapacity,"successfully completed", "Appointment added to" + serverName.toUpperCase().trim());
            } catch ( IOException e) {
                e.printStackTrace();
            }
            return CommonOutput.addAppointmentOutput(true, CommonOutput.addAppointment_success_added);
        }
        else
        {
            ConcurrentHashMap <String, AppointmentDetail> subHashMap = new ConcurrentHashMap<>();
            subHashMap.put(AppointmentID.toUpperCase().trim(), new AppointmentDetail(AppointmentType.toUpperCase().trim(), AppointmentID.toUpperCase().trim(), bookingCapacity));
            AppointmentMap.put(AppointmentType.toUpperCase().trim(), subHashMap);
            try
            {
                serverLog("Add Appointment", " AppointmentType:"+AppointmentType+ " AppointmentID:"+AppointmentID +
                        "bookingCapacity:"+ bookingCapacity,"successfully completed", "Appointment added to" + serverName.toUpperCase().trim());
            } catch ( IOException e) {
                e.printStackTrace();
            }
            return CommonOutput.addAppointmentOutput(true, CommonOutput.addAppointment_success_added);
        }
    }
    @Override
    public synchronized String removeAppointment( String AppointmentID,  String AppointmentType) throws RemoteException{
        if(AppointmentMap.containsKey(AppointmentType.toUpperCase().trim()) && AppointmentMap.get(AppointmentType.toUpperCase().trim()).containsKey(AppointmentID.toUpperCase().trim()))
        {
            String response="";
            String branch = AppointmentID.substring(0,3).toUpperCase().trim();
            AppointmentMap.get(AppointmentType.toUpperCase().trim()).remove(AppointmentID.toUpperCase().trim());
            try {
                serverLog("Remove Appointment", " AppointmentType:"+AppointmentType+ " AppointmentID:"+AppointmentID
                        ,"successfully completed", "Appointment removed from server" + serverName.toUpperCase().trim());
            } catch ( IOException e) {
                e.printStackTrace();
            }

            response = remove_client_Appointment(AppointmentID.toUpperCase().trim(), AppointmentType.toUpperCase().trim());

            if(branch.trim().equals("QUE"))
            {
                send_data_request(montreal_port, "remove_client_Appointment", AppointmentID.toUpperCase().trim(), AppointmentType.toUpperCase().trim(),"-").trim();
                send_data_request(sherbrooke_port, "remove_client_Appointment",AppointmentID.toUpperCase().trim(), AppointmentType.toUpperCase().trim(),"-").trim();
            }
            else if(branch.trim().equals("MTL"))
            {
                send_data_request(quebec_port, "remove_client_Appointment", AppointmentID.toUpperCase().trim(), AppointmentType.toUpperCase().trim(),"-").trim();
                send_data_request(sherbrooke_port, "remove_client_Appointment", AppointmentID.toUpperCase().trim(), AppointmentType.toUpperCase().trim(),"-").trim();

            }
            else if(branch.trim().equals("SHE"))
            {
                send_data_request(montreal_port, "remove_client_Appointment", AppointmentID.toUpperCase().trim(), AppointmentType.toUpperCase().trim(),"-").trim();
                send_data_request(quebec_port, "remove_client_Appointment", AppointmentID.toUpperCase().trim(), AppointmentType.toUpperCase().trim(),"-").trim();
            }

            return CommonOutput.removeAppointmentOutput(true, null);
        }
        else
        {
            return CommonOutput.removeAppointmentOutput(false, CommonOutput.removeAppointment_fail_no_such_Appointment);
        }
    }

    public String remove_client_Appointment( String AppointmentID,  String AppointmentType)
    {
        String data = "";
        String new_AppointmentID = "";
        for( Entry<String, ConcurrentHashMap<String, ClientDetail>> customer : ClientMap.entrySet())
        {
            ConcurrentHashMap<String, ClientDetail> AppointmentDetail = customer.getValue();
            String branch = AppointmentID.substring(0,3).toUpperCase().trim();

            if(AppointmentDetail.containsKey(AppointmentType.toUpperCase().trim() +";"+ AppointmentID.toUpperCase().trim()+""))
            {
                AppointmentDetail.remove(AppointmentType.toUpperCase().trim() +";"+ AppointmentID.toUpperCase().trim());

                for ( ConcurrentHashMap.Entry<String,ClientDetail> entry : customer.getValue().entrySet())
                {
                    data +=(entry.getValue().AppointmentID.toUpperCase().trim()+":");
                }
                if(branch.trim().equals("QUE"))
                {
                    new_AppointmentID = send_data_request(quebec_port, "boook_next_Appointment", data,AppointmentID.toUpperCase().trim() ,AppointmentType.toUpperCase().trim()).trim();
                }
                else if(branch.trim().equals("MTL"))
                {
                    new_AppointmentID = send_data_request(montreal_port, "boook_next_Appointment", data, AppointmentID.toUpperCase().trim() ,AppointmentType.toUpperCase().trim()).trim();

                }
                else if(branch.trim().equals("SHE"))
                {
                    new_AppointmentID = send_data_request(sherbrooke_port, "boook_next_Appointment", data, AppointmentID.toUpperCase().trim() ,AppointmentType.toUpperCase().trim()).trim();
                }

                try
                {
                    if(new_AppointmentID.trim().equals(""))
                    {
                        serverLog("Remove Appointment", " AppointmentType:"+AppointmentType+ " AppointmentID:"+AppointmentID,"successfully completed",
                                "Appointment removed for client:" + customer.getKey().toUpperCase().trim());
                    }
                    else
                    {
                        add_book_customer(customer.getKey().toUpperCase().trim(),new_AppointmentID, AppointmentType);
                        serverLog("Remove Appointment", " AppointmentType:"+AppointmentType+ " AppointmentID:"+new_AppointmentID,"successfully completed",
                                "Appointment has been replaced for client:" + customer.getKey().toUpperCase().trim());
                    }
                } catch ( IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return "Appointment with AppointmentID:"+ AppointmentID.toUpperCase().trim() +" and AppointmentType: "+AppointmentType.toUpperCase().trim() +" for clients has been removed for server\n";
    }

    public String boook_next_Appointment( String temp, String removedAppointmentID,  String AppointmentType) {

        String response="";
        String [] data = temp.split(":");
        String AppointmentID="";
        int capacity =0;
        List<String> sortedAppointmentIDs = new ArrayList<String>();
        if(AppointmentMap.containsKey(AppointmentType.toUpperCase().trim()) && AppointmentMap.get(AppointmentType.toUpperCase().trim()).values().size() != 0)
        {
            sortedAppointmentIDs = getSortedAppointmentID(AppointmentType, removedAppointmentID);
            if(data.length!= 0)
            {
                for (int count = 0; count < sortedAppointmentIDs.size(); count++) {
                    boolean check = false;
                    for (int i =0; i< data.length; ++i)
                    {
                        capacity = AppointmentMap.get(AppointmentType).get(sortedAppointmentIDs.get(count)).bookingCapacity;

                        if(!(data[i].indexOf(AppointmentMap.get(AppointmentType).get(sortedAppointmentIDs.get(count)).AppointmentID)!=-1) && capacity!=0)
                        {
                            check = true;
                        }
                        else
                        {
                            check = false;
                            break;
                        }
                    }
                    if(check == true)
                    {
                        AppointmentID = AppointmentMap.get(AppointmentType).get(sortedAppointmentIDs.get(count)).AppointmentID.toUpperCase().trim();
                        AppointmentMap.get(AppointmentType.toUpperCase().trim()).replace(AppointmentID,new AppointmentDetail(AppointmentType.toUpperCase().trim(), AppointmentID.toUpperCase().trim(), capacity - 1));
                        try
                        {
                            serverLog("Remove Appointment", " AppointmentType:"+AppointmentType+ " AppointmentID:"+AppointmentID,"successfully completed",
                                    "Next available Appointment replaced for client:");
                        } catch ( IOException e) {
                            e.printStackTrace();
                        }
                        return AppointmentID;
                    }
                }
            }
            else
            {
                capacity = AppointmentMap.get(AppointmentType).get(sortedAppointmentIDs.get(0)).bookingCapacity;
                if(capacity!=0)
                {
                    AppointmentID = AppointmentMap.get(AppointmentType).get(sortedAppointmentIDs.get(0)).AppointmentID.toUpperCase().trim();
                    AppointmentMap.get(AppointmentType.toUpperCase().trim()).replace(AppointmentID,new AppointmentDetail(AppointmentType.toUpperCase().trim(), AppointmentID.toUpperCase().trim(), capacity - 1));
                    try
                    {
                        serverLog("Remove Appointment", " AppointmentType:"+AppointmentType+ " AppointmentID:"+AppointmentID,"successfully completed",
                                "Next available Appointment replaced for client:");
                    } catch ( IOException e) {
                        e.printStackTrace();
                    }
                    return AppointmentID;
                }
            }
        }

        return response;
    }

    private List<String> getSortedAppointmentID( String AppointmentType,  String removedAppointmentID) {
        List<String> sortedAppointmentIDs = new ArrayList<String>();
        List<String> morningAppointmentIDs = new ArrayList<String>();
        List<String> afternoonAppointmentIDs = new ArrayList<String>();
        List<String> eveningAppointmentIDs = new ArrayList<String>();

        for (ConcurrentHashMap.Entry<String, AppointmentDetail> entry : AppointmentMap.get(AppointmentType).entrySet()) {
            if (entry.getValue().AppointmentID.startsWith("M", 3) && !removedAppointmentID.startsWith("A", 3) && !removedAppointmentID.startsWith("E", 3)) {
                if (Integer.parseInt(entry.getValue().AppointmentID.substring(8)) >= Integer.parseInt(removedAppointmentID.substring(8)) && Integer.parseInt(entry.getValue().AppointmentID.substring(6, 8)) >= Integer.parseInt(removedAppointmentID.substring(6, 8)) && Integer.parseInt(entry.getValue().AppointmentID.substring(4, 6)) >= Integer.parseInt(removedAppointmentID.substring(4, 6))) {
                    morningAppointmentIDs.add(entry.getValue().AppointmentID);
                }
            } else if (entry.getValue().AppointmentID.startsWith("A", 3) && !removedAppointmentID.startsWith("E", 3)) {
                if (Integer.parseInt(entry.getValue().AppointmentID.substring(8)) >= Integer.parseInt(removedAppointmentID.substring(8)) && Integer.parseInt(entry.getValue().AppointmentID.substring(6, 8)) >= Integer.parseInt(removedAppointmentID.substring(6, 8)) && Integer.parseInt(entry.getValue().AppointmentID.substring(4, 6)) >= Integer.parseInt(removedAppointmentID.substring(4, 6))) {
                    afternoonAppointmentIDs.add(entry.getValue().AppointmentID);
                }
            } else if (entry.getValue().AppointmentID.startsWith("E", 3)) {
                if (Integer.parseInt(entry.getValue().AppointmentID.substring(8)) >= Integer.parseInt(removedAppointmentID.substring(8)) && Integer.parseInt(entry.getValue().AppointmentID.substring(6, 8)) >= Integer.parseInt(removedAppointmentID.substring(6, 8)) && Integer.parseInt(entry.getValue().AppointmentID.substring(4, 6)) >= Integer.parseInt(removedAppointmentID.substring(4, 6))) {
                    eveningAppointmentIDs.add(entry.getValue().AppointmentID);
                }
            }
        }

        sortByDate(morningAppointmentIDs);
        sortByDate(afternoonAppointmentIDs);
        sortByDate(eveningAppointmentIDs);

        sortedAppointmentIDs.addAll(morningAppointmentIDs);
        sortedAppointmentIDs.addAll(afternoonAppointmentIDs);
        sortedAppointmentIDs.addAll(eveningAppointmentIDs);

        return sortedAppointmentIDs;
    }

    private List<String> sortByDate( List<String> list) {
        int n = list.size();
        //sort by year
        for (int i = 0; i < n-1; i++)
            for (int j = 0; j < n-i-1; j++)
            {
                int a = Integer.parseInt(list.get(j).substring(8));
                int b = Integer.parseInt(list.get(j+1).substring(8));

                if (a > b)
                {
                    Collections.swap(list, j, j+1);
                }
            }
        //sort by month
        for (int i = 0; i < n-1; i++)
            for (int j = 0; j < n-i-1; j++)
            {
                int a = Integer.parseInt(list.get(j).substring(6,8));
                int b = Integer.parseInt(list.get(j+1).substring(6,8));

                if (a > b)
                {
                    Collections.swap(list, j, j+1);
                }
            }
        //sort by day
        for (int i = 0; i < n-1; i++)
            for (int j = 0; j < n-i-1; j++)
            {
                int a = Integer.parseInt(list.get(j).substring(4,6));
                int b = Integer.parseInt(list.get(j+1).substring(4,6));

                if (a > b)
                {
                    Collections.swap(list, j, j+1);
                }
            }
        return list;
    }

    @Override
    public String listAppointmentAvailability( String AppointmentType) throws RemoteException{
        List<String> allAppointmentIDsWithCapacity = new ArrayList<>();
        String response1="" ,response2="";
        List<String> server1 = new ArrayList<>();
        List<String> server2 = new ArrayList<>();
        if(AppointmentMap.containsKey(AppointmentType.toUpperCase().trim()))
        {
            for ( Map.Entry<String, AppointmentDetail> entry : AppointmentMap.get(AppointmentType.toUpperCase().trim()).entrySet())
            {
                allAppointmentIDsWithCapacity.add(entry.getKey() + " " + entry.getValue().bookingCapacity);
            }
        }
        if(serverName.trim().equals("QUE"))
        {
            response1 = send_data_request(montreal_port, "list_Appointments", "-", AppointmentType.toUpperCase().trim(),"-").trim();
            response2 = send_data_request(sherbrooke_port, "list_Appointments", "-", AppointmentType.toUpperCase().trim(),"-").trim();

        }
        else if(serverName.trim().equals("MTL"))
        {
            response1 = send_data_request(quebec_port, "list_Appointments", "-", AppointmentType.toUpperCase().trim(),"-").trim();
            response2 = send_data_request(sherbrooke_port, "list_Appointments", "-", AppointmentType.toUpperCase().trim(),"-").trim();
        }
        else if(serverName.trim().equals("SHE"))
        {
            response1 = send_data_request(montreal_port, "list_Appointments", "-", AppointmentType.toUpperCase().trim(),"-").trim();
            response2 = send_data_request(quebec_port, "list_Appointments", "-", AppointmentType.toUpperCase().trim(),"-").trim();
        }
        server1 = Arrays.asList(response1.split("@"));
        server2 = Arrays.asList(response2.split("@"));
        allAppointmentIDsWithCapacity.addAll(server1);
        allAppointmentIDsWithCapacity.addAll(server2);
        return CommonOutput.listAppointmentAvailabilityOutput(true, allAppointmentIDsWithCapacity, null);
    }
    public String list_Appointments( String AppointmentType)
    {
        String response = "";

        if(AppointmentMap.containsKey(AppointmentType.toUpperCase().trim()))
        {
            for ( ConcurrentHashMap.Entry<String, AppointmentDetail> entry : AppointmentMap.get(AppointmentType).entrySet())
            {
                response += entry.getKey() + " " + entry.getValue().bookingCapacity+"@";
            }
        }
        if (response.endsWith("@"))
            response = response.substring(0, response.length() - 1);
        return response;
    }

    @Override
    public synchronized String bookAppointment( String customerID,  String AppointmentID,  String AppointmentType) throws RemoteException{
        String response=CommonOutput.bookAppointmentOutput(false, null);
        String city = AppointmentID.substring(0,3).toUpperCase().trim();
        String AppointmentDetail = AppointmentType.toUpperCase().trim()+ ";" + AppointmentID.toUpperCase().trim();

        if(city.trim().equals(serverName))
        {
            response = book_accepted_Appointment(customerID, AppointmentID, AppointmentType);

            if(response.contains("full"))
            {
                try
                {
                    serverLog("Book an Appointment", " AppointmentType:"+AppointmentType+ " AppointmentID:"+AppointmentID +" CustomerID:"+ customerID,"failed","There is no capacity for this Appointment");
                    response = CommonOutput.bookAppointmentOutput(false, CommonOutput.bookAppointment_fail_no_capacity);
                } catch ( IOException e) {
                    e.printStackTrace();
                }
            }
            else if(response.contains("No"))
            {
                try
                {
                    serverLog("Book an Appointment", " AppointmentType:"+AppointmentType+ " AppointmentID:"+AppointmentID+" CustomerID:"+ customerID,"failed","There is no such an Appointment");
                    response = CommonOutput.bookAppointmentOutput(false, CommonOutput.bookAppointment_fail_no_such_Appointment);
                } catch ( IOException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                try
                {
                    int capacity = AppointmentMap.get(AppointmentType.toUpperCase().trim()).get(AppointmentID.toUpperCase().trim()).bookingCapacity;
                    if(serverName.trim().equals(customerID.substring(0,3).toUpperCase().trim()))
                    {
                        if(ClientMap.containsKey(customerID.toUpperCase().trim()) && ClientMap.get(customerID.toUpperCase().trim()).containsKey(AppointmentDetail))
                        {
                            try
                            {
                                serverLog("Book an Appointment", " AppointmentType:"+AppointmentType+ " AppointmentID:"+AppointmentID +" CustomerID:"+ customerID,"failed","This Appointment has already been booked");

                            } catch ( IOException e) {
                                e.printStackTrace();
                            }
                            return CommonOutput.bookAppointmentOutput(false, null);
                        }
                        AppointmentMap.get(AppointmentType.toUpperCase().trim()).replace(AppointmentID,new AppointmentDetail(AppointmentType.toUpperCase().trim(), AppointmentID.toUpperCase().trim(), capacity - 1));
                        add_book_customer(customerID, AppointmentID, AppointmentType);
                    }
                    else
                        AppointmentMap.get(AppointmentType.toUpperCase().trim()).replace(AppointmentID,new AppointmentDetail(AppointmentType.toUpperCase().trim(), AppointmentID.toUpperCase().trim(), capacity - 1));

                    serverLog("Book an Appointment", " AppointmentType:"+AppointmentType+ " AppointmentID:"+AppointmentID+" CustomerID:"+ customerID,"successfully completed","Booking request has been approved");
                    response = CommonOutput.bookAppointmentOutput(true, null);
                } catch ( IOException e) {
                    e.printStackTrace();
                }
            }
        }

        else
        {
            if(city.trim().equals("QUE"))
            {
                if(ClientMap.containsKey(customerID.toUpperCase().trim()) && ClientMap.get(customerID.toUpperCase().trim()).containsKey(AppointmentDetail))
                {
                    try
                    {
                        serverLog("Book an Appointment", " AppointmentType:"+AppointmentType+ " AppointmentID:"+AppointmentID +" CustomerID:"+ customerID,"failed","This Appointment has already been booked");

                    } catch ( IOException e) {
                        e.printStackTrace();
                    }

                    return CommonOutput.bookAppointmentOutput(false, null);
                }
                if(ClientMap.containsKey(customerID.toUpperCase().trim()))
                {
                    if(!week_limit_check(customerID.toUpperCase().trim(), AppointmentID.substring(4)))
                    {
                        try
                        {
                            serverLog("Book an Appointment", " AppointmentType:"+AppointmentType+ " AppointmentID:"+AppointmentID+" CustomerID:"+ customerID,"failed","This customer has already booked 3 times from other cities!");
                        } catch ( IOException e) {
                            e.printStackTrace();
                        }

                        return CommonOutput.bookAppointmentOutput(false, CommonOutput.bookAppointment_fail_weekly_limit);
                    }
                }
                response = send_data_request(quebec_port, "bookAppointment", AppointmentID.toUpperCase().trim(), AppointmentType.toUpperCase().trim(),customerID.toUpperCase().trim()).trim();
                if(response.contains("successful"))
                {
                    try
                    {
                        serverLog("Book an Appointment", " AppointmentType:"+AppointmentType+ " AppointmentID:"+AppointmentID+" CustomerID:"+ customerID,"successfully completed","Booking request has been approved");
                    } catch ( IOException e) {
                        e.printStackTrace();
                    }
                    add_book_customer(customerID, AppointmentID, AppointmentType);
                }
                else if(response.contains("full"))
                {
                    try
                    {
                        serverLog("Book an Appointment", " AppointmentType:"+AppointmentType+ " AppointmentID:"+AppointmentID +" CustomerID:"+ customerID,"failed","There is no capacity for this Appointment");

                    } catch ( IOException e) {
                        e.printStackTrace();
                    }
                }
                else if(response.contains("No"))
                {
                    try
                    {
                        serverLog("Book an Appointment", " AppointmentType:"+AppointmentType+ " AppointmentID:"+AppointmentID+" CustomerID:"+ customerID,"failed","There is no such an Appointment");
                    } catch ( IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            else if(city.trim().equals("MTL"))
            {
                if(ClientMap.containsKey(customerID.toUpperCase().trim()) && ClientMap.get(customerID.toUpperCase().trim()).containsKey(AppointmentDetail))
                {
                    try
                    {
                        serverLog("Book an Appointment", " AppointmentType:"+AppointmentType+ " AppointmentID:"+AppointmentID +" CustomerID:"+ customerID,"failed","This Appointment has already been booked");
                    } catch ( IOException e) {
                        e.printStackTrace();
                    }

                    return CommonOutput.bookAppointmentOutput(false, null);
                }
                if(ClientMap.containsKey(customerID.toUpperCase().trim()))
                {
                    if(!week_limit_check(customerID.toUpperCase().trim(), AppointmentID.substring(4)))
                    {
                        try
                        {
                            serverLog("Book an Appointment", " AppointmentType:"+AppointmentType+ " AppointmentID:"+AppointmentID+" CustomerID:"+ customerID,"failed","This customer has already booked 3 times from other cities!");
                        } catch ( IOException e) {
                            e.printStackTrace();
                        }
                        return CommonOutput.bookAppointmentOutput(false, CommonOutput.bookAppointment_fail_weekly_limit);
                    }
                }
                response = send_data_request(montreal_port, "bookAppointment", AppointmentID.toUpperCase().trim(), AppointmentType.toUpperCase().trim(),customerID.toUpperCase().trim()).trim();
                if(response.contains("successful"))
                {
                    try
                    {
                        serverLog("Book an Appointment", " AppointmentType:"+AppointmentType+ " AppointmentID:"+AppointmentID+" CustomerID:"+ customerID,"successfully completed","Booking request has been approved");
                    } catch ( IOException e) {
                        e.printStackTrace();
                    }
                    add_book_customer(customerID, AppointmentID, AppointmentType);
                }
                else if(response.contains("full"))
                {
                    try
                    {
                        serverLog("Book an Appointment", " AppointmentType:"+AppointmentType+ " AppointmentID:"+AppointmentID +" CustomerID:"+ customerID,"failed","There is no capacity for this Appointment");
                    } catch ( IOException e) {
                        e.printStackTrace();
                    }
                }
                else if(response.contains("No"))
                {
                    try
                    {
                        serverLog("Book an Appointment", " AppointmentType:"+AppointmentType+ " AppointmentID:"+AppointmentID+" CustomerID:"+ customerID,"failed","There is no such an Appointment");
                    } catch ( IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            else if(city.trim().equals("SHE"))
            {
                if(ClientMap.containsKey(customerID.toUpperCase().trim()) && ClientMap.get(customerID.toUpperCase().trim()).containsKey(AppointmentDetail))
                {
                    try
                    {
                        serverLog("Book an Appointment", " AppointmentType:"+AppointmentType+ " AppointmentID:"+AppointmentID +" CustomerID:"+ customerID,"failed","This Appointment has already been booked");
                    } catch ( IOException e) {
                        e.printStackTrace();
                    }

                    return CommonOutput.bookAppointmentOutput(false, null);
                }
                if(ClientMap.containsKey(customerID.toUpperCase().trim()))
                {
                    if(!week_limit_check(customerID.toUpperCase().trim(), AppointmentID.substring(4)))
                    {
                        try
                        {
                            serverLog("Book an Appointment", " AppointmentType:"+AppointmentType+ " AppointmentID:"+AppointmentID+" CustomerID:"+ customerID,"failed","This customer has already booked 3 times from other cities!");
                        } catch ( IOException e) {
                            e.printStackTrace();
                        }
                        return CommonOutput.bookAppointmentOutput(false, CommonOutput.bookAppointment_fail_weekly_limit);
                    }
                }
                response = send_data_request(sherbrooke_port, "bookAppointment", AppointmentID.toUpperCase().trim(), AppointmentType.toUpperCase().trim(),customerID.toUpperCase().trim()).trim();
                if(response.contains("successful"))
                {
                    try
                    {
                        serverLog("Book an Appointment", " AppointmentType:"+AppointmentType+ " AppointmentID:"+AppointmentID+" CustomerID:"+ customerID,"successfully completed","Booking request has been approved");
                    } catch ( IOException e) {
                        e.printStackTrace();
                    }
                    add_book_customer(customerID, AppointmentID, AppointmentType);
                }
                else if(response.contains("full"))
                {
                    try
                    {
                        serverLog("Book an Appointment", " AppointmentType:"+AppointmentType+ " AppointmentID:"+AppointmentID +" CustomerID:"+ customerID,"failed","There is no capacity for this Appointment");
                    } catch ( IOException e) {
                        e.printStackTrace();
                    }
                }
                else if(response.contains("No"))
                {
                    try {
                        serverLog("Book an Appointment", " AppointmentType:" + AppointmentType + " AppointmentID:" + AppointmentID + " CustomerID:" + customerID, "failed", "There is no such an Appointment");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        return response;
    }

    public boolean week_limit_check(String customerID, String AppointmentDate) {
        int limit = 0;

        for (Entry<String, ClientDetail> Appointments : ClientMap.get(customerID).entrySet()) {
            if (!Appointments.getValue().AppointmentID.substring(0, 3).equals(serverName) && same_week_check(Appointments.getValue().AppointmentID.substring(4), AppointmentDate)) {
                limit++;
            }
        }
        return limit < 3;
    }

    private boolean same_week_check(String newAppointmentDate, String AppointmentID) {
        if (AppointmentID.substring(2, 4).equals(newAppointmentDate.substring(2, 4)) && AppointmentID.substring(4, 6).equals(newAppointmentDate.substring(4, 6))) {
            int day1 = Integer.parseInt(AppointmentID.substring(0, 2));
            int day2 = Integer.parseInt(newAppointmentDate.substring(0, 2));
            if (day1 % 7 == 0) {
                day1--;
            }
            if (day2 % 7 == 0) {
                day2--;
            }
            int w1 = day1 / 7;
            int w2 = day2 / 7;

            return w1 == w2;
        } else
            return false;
    }

    public String book_accepted_Appointment(String customerID, String AppointmentID, String AppointmentType) {
        String response = CommonOutput.bookAppointmentOutput(false, null);

        if (AppointmentMap.containsKey(AppointmentType.toUpperCase().trim()) && AppointmentMap.get(AppointmentType.toUpperCase().trim()).containsKey(AppointmentID.toUpperCase().trim())) {
            int capacity = AppointmentMap.get(AppointmentType.toUpperCase().trim()).get(AppointmentID.toUpperCase().trim()).bookingCapacity;

            if (capacity == 0)
                return CommonOutput.bookAppointmentOutput(false, CommonOutput.bookAppointment_fail_no_capacity);
            else {
                response = CommonOutput.bookAppointmentOutput(true, null);
            }
        } else {
            response = CommonOutput.bookAppointmentOutput(false, CommonOutput.bookAppointment_fail_no_such_Appointment);
        }

        return response;
    }

    public String add_book_customer( String customerID,  String AppointmentID,  String AppointmentType)
    {
        String response = "";
        String AppointmentDetail = AppointmentType.toUpperCase().trim()+ ";" + AppointmentID.toUpperCase().trim();

        if(ClientMap.containsKey(customerID.toUpperCase().trim()))
        {
            ClientMap.get(customerID.toUpperCase().trim()).put(AppointmentDetail, new ClientDetail(customerID.toUpperCase().trim(), AppointmentType.toUpperCase().trim(), AppointmentID.toUpperCase().trim()));
            response = "BOOKED";
        }
        else
        {
            ConcurrentHashMap <String, ClientDetail> subHashMap = new ConcurrentHashMap<>();
            subHashMap.put(AppointmentDetail, new ClientDetail(customerID.toUpperCase().trim(), AppointmentType.toUpperCase().trim(), AppointmentID.toUpperCase().trim()));
            ClientMap.put(customerID.toUpperCase().trim(), subHashMap);
            response = "BOOKED";
        }
        return response;
    }

    @Override
    public String getBookingSchedule( String customerID) throws RemoteException{
        Map<String, List<String>> Appointments = new HashMap<>();
        if(ClientMap.containsKey(customerID.toUpperCase().trim()))
        {
            for ( ConcurrentHashMap.Entry<String, ClientDetail> entry : ClientMap.get(customerID.toUpperCase().trim()).entrySet())
            {
                String [] data = entry.getKey().split(";");
                List<String> list;
                if(!Appointments.containsKey(data[0]))
                    list=new ArrayList<>();
                else
                    list= Appointments.get(data[0]);
                list.add(data[1]);
                Appointments.put(data[0], list);
            }
            return CommonOutput.getBookingScheduleOutput(true, Appointments, null);
        }
        else
            return CommonOutput.getBookingScheduleOutput(true, new HashMap<>(), null);
    }

    @Override
    public String cancelAppointment( String customerID,  String AppointmentID,  String AppointmentType) throws RemoteException{
        String AppointmentDetail = AppointmentType.toUpperCase().trim()+ ";" + AppointmentID.toUpperCase().trim();
        String branch = AppointmentID.substring(0,3).toUpperCase().trim();

        if(ClientMap.containsKey(customerID.toUpperCase().trim()) && ClientMap.get(customerID.toUpperCase().trim()).containsKey(AppointmentDetail))
        {
            ClientMap.get(customerID.toUpperCase().trim()).remove(AppointmentDetail);

            if(branch.trim().equals(serverName))
            {
                int currentCapacity = AppointmentMap.get(AppointmentType.toUpperCase().trim()).get(AppointmentID.toUpperCase().trim()).bookingCapacity;
                AppointmentMap.get(AppointmentType.toUpperCase().trim()).replace(AppointmentID,new AppointmentDetail(AppointmentType.toUpperCase().trim(), AppointmentID.toUpperCase().trim(), currentCapacity + 1));
                try
                {
                    serverLog("Cancel an Appointment", " AppointmentType:"+AppointmentType+ " AppointmentID:"+AppointmentID+" CustomerID:"+ customerID,"successfully completed","Appointment has been canceled");
                } catch ( IOException e) {
                    e.printStackTrace();
                }
            }
            else if(branch.trim().equals("QUE"))
            {
                send_data_request(quebec_port, "cancel_client_Appointment", AppointmentID.toUpperCase().trim(), AppointmentType.toUpperCase().trim(),"-").trim();
                try
                {
                    serverLog("Cancel an Appointment", " AppointmentType:"+AppointmentType+ " AppointmentID:"+AppointmentID+" CustomerID:"+ customerID,"successfully completed","Appointment has been canceled");
                } catch ( IOException e) {
                    e.printStackTrace();
                }

            }
            else if(branch.trim().equals("MTL"))
            {
                send_data_request(montreal_port, "cancel_client_Appointment", AppointmentID.toUpperCase().trim(), AppointmentType.toUpperCase().trim(),"-").trim();
                try
                {
                    serverLog("Cancel an Appointment", " AppointmentType:"+AppointmentType+ " AppointmentID:"+AppointmentID+" CustomerID:"+ customerID,"successfully completed","Appointment has been canceled");
                } catch ( IOException e) {
                    e.printStackTrace();
                }

            }
            else if(branch.trim().equals("SHE"))
            {
                send_data_request(sherbrooke_port, "cancel_client_Appointment", AppointmentID.toUpperCase().trim(), AppointmentType.toUpperCase().trim(),"-").trim();
                try
                {
                    serverLog("Cancel an Appointment", " AppointmentType:"+AppointmentType+ " AppointmentID:"+AppointmentID+" CustomerID:"+ customerID,"successfully completed","Appointment has been canceled");
                } catch ( IOException e) {
                    e.printStackTrace();
                }
            }
            return CommonOutput.cancelAppointmentOutput(true, null);
        }
        else
            return CommonOutput.cancelAppointmentOutput(false, CommonOutput.cancelAppointment_fail_no_such_Appointment);
    }

    public String cancel_client_Appointment( String AppointmentID,  String AppointmentType)
    {
        if(AppointmentMap.containsKey(AppointmentType.toUpperCase().trim()) && AppointmentMap.get(AppointmentType.toUpperCase().trim()).containsKey(AppointmentID.toUpperCase().trim()))
        {
            int currentCapacity = AppointmentMap.get(AppointmentType.toUpperCase().trim()).get(AppointmentID.toUpperCase().trim()).bookingCapacity;
            AppointmentMap.get(AppointmentType.toUpperCase().trim()).replace(AppointmentID,new AppointmentDetail(AppointmentType.toUpperCase().trim(), AppointmentID.toUpperCase().trim(), currentCapacity + 1));
        }
        return "CANCELED";

    }

    private static String getDirectory( String ID,  String type) {
        String dir = System.getProperty("user.dir");
        String fileName = dir;
        if(type == "Server")
        {
            if (ID.equals("MTL")) {
                fileName = dir + "\\src\\Replica1\\Logs\\Server\\Montreal_logs.txt";
            } else if (ID.equals("SHE")) {
                fileName = dir + "\\src\\Replica1\\Logs\\Server\\Sherbrooke_logs.txt";
            } else if (ID.equals("QUE")) {
                fileName = dir + "\\src\\Replica1\\Logs\\Server\\Quebec_logs.txt";
            }
        }
        else {
            fileName = dir + "\\src\\Replica1\\Logs\\Clients\\" + ID + "_logs.txt";
        }
        return fileName;
    }

    public void serverLog( String acion,  String peram,  String requestResult,  String response) throws IOException {
        String city = serverName;
        Date date = new Date();
        String strDateFormat = "yyyy-MM-dd hh:mm:ss a";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        String formattedDate= dateFormat.format(date);

        FileWriter fileWriter = new FileWriter(getDirectory(city.trim().toUpperCase(), "Server"),true);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.println("DATE: "+formattedDate+"| Request type: "+acion+" | Request parameters: "+ peram +" | Request result: "+requestResult+" | Server resonse: "+ response);

        printWriter.close();

    }

    private static String send_data_request( int serverPort, String function, String AppointmentID,  String AppointmentType,  String customerID) {
        DatagramSocket socket = null;
        String result ="";
        String clientRequest = function+";"+AppointmentID.toUpperCase().trim()+";"+AppointmentType.toUpperCase().trim()+";" + customerID.toUpperCase().trim();
        try {
            socket = new DatagramSocket();
            byte[] data = clientRequest.getBytes();
            InetAddress host = InetAddress.getByName("localhost");
            DatagramPacket request = new DatagramPacket(data, clientRequest.length(), host, serverPort);
            socket.send(request);

            byte[] buffer = new byte[1000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

            socket.receive(reply);
            result = new String(reply.getData());
        } catch ( SocketException e) {
            System.out.println("Socket exception: " + e.getMessage());
        } catch ( IOException e) {
            e.printStackTrace();
            System.out.println("IO Error: " + e.getMessage());
        } finally {
            if (socket != null)
                socket.close();
        }
        return result;

    }

    @Override
    public synchronized String swapAppointment( String customerID,  String newAppointmentID,  String newAppointmentType,  String oldAppointmentID,  String oldAppointmentType) throws RemoteException{
        String AppointmentDetail = oldAppointmentType.toUpperCase().trim()+ ";" + oldAppointmentID.toUpperCase().trim();
        String response = CommonOutput.bookAppointmentOutput(false, null);
        if(!week_limit_check(customerID.toUpperCase().trim(), newAppointmentID.substring(4)))
        {
            response = cancelAppointment(customerID, oldAppointmentID, oldAppointmentType);
            if(response.trim().contains("successful"))
            {
                response = bookAppointment(customerID, newAppointmentID, newAppointmentType);
                if(response.trim().equals("BOOKING_APPROVED"))
                    return CommonOutput.swapAppointmentOutput(true, null);
                else {
                    bookAppointment(customerID, oldAppointmentID, oldAppointmentType);
                    if (response.contains("full")) {
                        return CommonOutput.swapAppointmentOutput(false, CommonOutput.bookAppointment_fail_no_capacity);
                    } else if (response.contains("No")) {
                        return CommonOutput.swapAppointmentOutput(false, CommonOutput.swapAppointment_fail_no_such_Appointment);
                    }
                }
            }
            else
                return CommonOutput.swapAppointmentOutput(false, null);
        }
        else if(ClientMap.containsKey(customerID.toUpperCase().trim()) && ClientMap.get(customerID.toUpperCase().trim()).containsKey(AppointmentDetail))
        {
            response = bookAppointment(customerID, newAppointmentID, newAppointmentType);
            if (response.trim().contains("successful")) {
                response = cancelAppointment(customerID, oldAppointmentID, oldAppointmentType);
                return CommonOutput.swapAppointmentOutput(true, null);
            } else if (response.contains("full")) {
                response = CommonOutput.swapAppointmentOutput(false, CommonOutput.bookAppointment_fail_no_capacity);
            } else if (response.contains("No")) {
                response = CommonOutput.swapAppointmentOutput(false, CommonOutput.swapAppointment_fail_no_such_Appointment);
            }
        }
        else
            response = CommonOutput.swapAppointmentOutput(false, CommonOutput.swapAppointment_fail_no_such_Appointment);
        return response;
    }

    @Override
    public String shutDown() throws RemoteException
    {
        AppointmentMap = new ConcurrentHashMap<>();
        ClientMap = new ConcurrentHashMap<>();
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // ignored
                }
                System.exit(1);
            }
        });
        return "Shutting down";
    }
}

