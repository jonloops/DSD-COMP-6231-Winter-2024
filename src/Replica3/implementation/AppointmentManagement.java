package Replica3.implementation;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import Replica3.server.Montreal;
import Replica3.server.Quebec;
import Replica3.server.Sherbrook;
import Replica3.CommonOutput;

public class AppointmentManagement  extends UnicastRemoteObject  implements Manager  {

	public AppointmentManagement() throws RemoteException {
		super();
	}

	@Override
	public String addAppointment(String appointmentID, String appointmentType,
			int bookingCapacity, String serv) throws RemoteException {

		if (serv.equalsIgnoreCase("MTL")) {
			Montreal mn = new Montreal();
			String var = mn.getHashMap(appointmentType);
			// mn.addHashMap(var, appointmentID, bookingCapacity);
			return (mn.addHashMap(var, appointmentID, bookingCapacity));
		} else if (serv.equalsIgnoreCase("QUE")) {
			Quebec mn = new Quebec();

			String var = mn.getHashMap(appointmentType);
			// mn.addHashMap(var, appointmentID, bookingCapacity);
			return (mn.addHashMap(var, appointmentID, bookingCapacity));
		} else if (serv.equalsIgnoreCase("SHE")) {
			Sherbrook mn = new Sherbrook();
			String var = mn.getHashMap(appointmentType);
			// mn.addHashMap(var, appointmentID, bookingCapacity);
			return (mn.addHashMap(var, appointmentID, bookingCapacity));
		}
		return CommonOutput.addAppointmentOutput(false, null);

	}

	@Override
	public String removeAppointment(String appointmentID, String appointmentType, String serv) throws RemoteException {

		if (serv.equalsIgnoreCase("MTL")) {
			Montreal mn = new Montreal();
			String var = mn.getHashMap(appointmentType);
			return mn.removeHashMap(var, appointmentID);
		} else if (serv.equalsIgnoreCase("QUE")) {
			Quebec mn = new Quebec();
			String var = mn.getHashMap(appointmentType);
			return mn.removeHashMap(var, appointmentID);
		} else if (serv.equalsIgnoreCase("SHE")) {
			Sherbrook mn = new Sherbrook();
			String var = mn.getHashMap(appointmentType);
			return mn.removeHashMap(var, appointmentID);
		}
		return CommonOutput.removeAppointmentOutput(false, null);

	}

	@Override
	public String listAppointmentAvailability(String appointmentType, String serv) throws RemoteException {

		String str = "";
		String temp1 = "";
		String temp2 = "";
		String temp3 = "";

		if (serv.equalsIgnoreCase("MTL")) {
			Montreal mn = new Montreal();
			String var = mn.getHashMap(appointmentType)+"display";
			
			temp1 = mn.display(var.substring(0, 1));

			temp2 = mn.UDPConnect(7001, var);

			temp3 = mn.UDPConnect(7002, var);
			
			str = temp1.trim() +","+ temp2.trim() +","+ temp3.trim();
			String str1[] = str.split(",");
			List<String> al = new ArrayList<String>();
			al = Arrays.asList(str1);
			/*String str1 = temp1+temp3;
			str=str1+temp2;*/
			//str=temp1.concat(temp2).concat(temp3);
			
			return  CommonOutput.listAppointmentAvailabilityOutput(true,al , null);

		} else if (serv.equalsIgnoreCase("QUE")) {
			Quebec mn = new Quebec();
			String var = mn.getHashMap(appointmentType)+"display";
			temp1 = mn.display(var.substring(0, 1));

			temp2 = mn.UDPConnect(7000, var);

			temp3 = mn.UDPConnect(7002, var);

			str = temp1.trim() +","+ temp2.trim() +","+ temp3.trim();
			String str1[] = str.split(",");
			List<String> al = new ArrayList<String>();
			al = Arrays.asList(str1);
			
			/*String str1 = temp1+temp3;
			str=str1+temp2;*/
			//str=temp1.concat(temp2).concat(temp3);
			
			return  CommonOutput.listAppointmentAvailabilityOutput(true,al , null);
		} else if (serv.equalsIgnoreCase("SHE")) {
			Sherbrook mn = new Sherbrook();
			String var = mn.getHashMap(appointmentType)+"display";
			temp1 = mn.display(var.substring(0, 1));

			temp2 = mn.UDPConnect(7001, var);

			temp3 = mn.UDPConnect(7000, var);

			str = temp1.trim() +","+ temp2.trim() +","+ temp3.trim();
			String str1[] = str.split(",");
			List<String> al = new ArrayList<String>();
			al = Arrays.asList(str1);
			
			/*String str1 = temp1+temp3;
			str=str1+temp2;*/
			//str=temp1.concat(temp2).concat(temp3);
			
			return  CommonOutput.listAppointmentAvailabilityOutput(true,al , null);
		}

		else
		
		/*String str1 = temp1+temp3;
		str=str1+temp2;*/
		//str=temp1.concat(temp2).concat(temp3);
		
		return  CommonOutput.listAppointmentAvailabilityOutput(false,null , null);
	}

	@Override
	public String bookAppointment(String customerID, String appointmentID,
			String appointmentType, String serv) throws RemoteException {
		char[] ch = appointmentID.toCharArray();
		char[] ch2 = { ch[0], ch[1], ch[2] };
		String bookingServ = new String(ch2);

		if (serv.equalsIgnoreCase("MTL")) {
			Montreal mn = new Montreal();
			String var = mn.getHashMap(appointmentType)+"booked "+customerID+appointmentID;

			if (serv.equalsIgnoreCase(bookingServ)) {
				if (mn.checkAvailabilityOfAppointment(var.substring(0, 1), appointmentID).equalsIgnoreCase(
						"Available ")) {//it checks both condition capacity and existence
					String r = mn.bookedAppointment(var.substring(0, 1),appointmentID, customerID,appointmentType);
					if(r.contains("SecondBooking")){
						return CommonOutput.bookAppointmentOutput(false, null);
					}
					return CommonOutput.bookAppointmentOutput(true, null);
				} else if (mn.checkAvailabilityOfAppointment(var.substring(0, 1), appointmentID).equalsIgnoreCase(
						"No Capacity ")) {//it checks both condition capacity and existence
					 return CommonOutput.bookAppointmentOutput(false, CommonOutput.bookAppointment_fail_no_capacity);
				} else{
					 return CommonOutput.bookAppointmentOutput(false, CommonOutput.bookAppointment_fail_no_such_appointment);
				}
			}
			else if(bookingServ.equalsIgnoreCase("QUE")){
				String count=mn.UDPConnect(7001, ("checkCount"+customerID+ appointmentID));
				String count1=mn.UDPConnect(7002, ("checkCount"+customerID+ appointmentID));
				int counter=Integer.parseInt(count.substring(0, 1))+Integer.parseInt(count1.substring(0, 1));
				if(counter==3){
					//return "Cannot book.You already have 3 booking in the servers";
					return CommonOutput.bookAppointmentOutput(false, CommonOutput.bookAppointment_fail_weekly_limit);
				}
				/*if (mn.checkAvailabilityOfAppointment(var.substring(0, 1), appointmentID).equalsIgnoreCase(
						"Available ")) {*/
					String temp2;
					temp2 = mn.UDPConnect(7001, var);
					return temp2.trim();
				/*} else {
					return ("No such appointment is available");
				}*/
				
				
			}else if(bookingServ.equalsIgnoreCase("SHE")){
				String count=mn.UDPConnect(7001, ("checkCount"+customerID+ appointmentID));
				String count1=mn.UDPConnect(7002, ("checkCount"+customerID+ appointmentID));
				int counter=Integer.parseInt(count.substring(0, 1))+Integer.parseInt(count1.substring(0, 1));
				if(counter==3){
					return CommonOutput.bookAppointmentOutput(false, CommonOutput.bookAppointment_fail_weekly_limit);
				}
				
				
			/*	if (mn.checkAvailabilityOfAppointment(var.substring(0, 1), appointmentID).equalsIgnoreCase(
						"Available ")) {*/
					String temp3;
					temp3 = mn.UDPConnect(7002, var);
					return temp3.trim();
				/*} else {
					return ("No such appointment is available");
				}*/
			}
			
			
		} else if (serv.equalsIgnoreCase("QUE")) {
			Quebec mn = new Quebec();

			String var = mn.getHashMap(appointmentType)+"booked "+customerID+appointmentID;
			if (serv.equalsIgnoreCase(bookingServ)) {
				if (mn.checkAvailabilityOfAppointment(var.substring(0, 1), appointmentID).equalsIgnoreCase(
						"Available ")) {
					String r = mn.bookedAppointment(var.substring(0, 1),appointmentID, customerID,appointmentType);
					if(r.contains("SecondBooking")){
						return CommonOutput.bookAppointmentOutput(false, null);
					}
					return CommonOutput.bookAppointmentOutput(true, null);
				} else if (mn.checkAvailabilityOfAppointment(var.substring(0, 1), appointmentID).equalsIgnoreCase(
						"No Capacity ")) {//it checks both condition capacity and existence
					 return CommonOutput.bookAppointmentOutput(false, CommonOutput.bookAppointment_fail_no_capacity);
				} else{
					 return CommonOutput.bookAppointmentOutput(false, CommonOutput.bookAppointment_fail_no_such_appointment);
				}
			}
			else if(bookingServ.equalsIgnoreCase("MTL")){
				String count=(mn.UDPConnect(7000, ("checkCount"+customerID+ appointmentID)));
				String count1=(mn.UDPConnect(7002, ("checkCount"+customerID+ appointmentID)));
				int counter=Integer.parseInt(count.substring(0, 1))+Integer.parseInt(count1.substring(0, 1));
				if(counter==3){
					return CommonOutput.bookAppointmentOutput(false, CommonOutput.bookAppointment_fail_weekly_limit);
				}
				
		/*		if (mn.checkAvailabilityOfAppointment(var.substring(0, 1), appointmentID).equalsIgnoreCase(
						"Available ")) {*/
					String temp2;
					
					temp2 = mn.UDPConnect(7000, var);
					return temp2.trim();
				/*} else {
					return ("No such appointment is available");
				}*/
			}else if(bookingServ.equalsIgnoreCase("SHE")){
				String count=mn.UDPConnect(7000, ("checkCount"+customerID+ appointmentID));
				String count1=mn.UDPConnect(7002, ("checkCount"+customerID+ appointmentID));
				int counter=Integer.parseInt(count.substring(0, 1))+Integer.parseInt(count1.substring(0, 1));
				if(counter==3){
					return CommonOutput.bookAppointmentOutput(false, CommonOutput.bookAppointment_fail_weekly_limit);
				}
				/*if (mn.checkAvailabilityOfAppointment(var.substring(0, 1), appointmentID).equalsIgnoreCase(
						"Available ")) {*/
					String temp3;
					temp3 = mn.UDPConnect(7002, var);
					return temp3.trim();
				/*} else {
					return ("No such appointment is available");
				}*/
				
			}
			
		} else if (serv.equalsIgnoreCase("SHE")) {
			Sherbrook mn = new Sherbrook();

			String var = mn.getHashMap(appointmentType)+"booked "+customerID+appointmentID;
			if (serv.equalsIgnoreCase(bookingServ)) {
				if (mn.checkAvailabilityOfAppointment(var.substring(0, 1), appointmentID).equalsIgnoreCase(
						"Available ")) {
					String r = mn.bookedAppointment(var.substring(0, 1),appointmentID, customerID,appointmentType);
					if(r.contains("SecondBooking")){
						return CommonOutput.bookAppointmentOutput(false, null);
					}
					return CommonOutput.bookAppointmentOutput(true, null);
				}else if (mn.checkAvailabilityOfAppointment(var.substring(0, 1), appointmentID).equalsIgnoreCase(
						"No Capacity ")) {//it checks both condition capacity and existence
					 return CommonOutput.bookAppointmentOutput(false, CommonOutput.bookAppointment_fail_no_capacity);
				} else{
					 return CommonOutput.bookAppointmentOutput(false, CommonOutput.bookAppointment_fail_no_such_appointment);
				}
			}
			else if(bookingServ.equalsIgnoreCase("QUE")){
				String count=mn.UDPConnect(7000, ("checkCount"+customerID+ appointmentID));
				String count1=mn.UDPConnect(7001, ("checkCount"+customerID+ appointmentID));
				int counter=Integer.parseInt(count.substring(0, 1))+Integer.parseInt(count1.substring(0, 1));
				if(counter==3){
					return CommonOutput.bookAppointmentOutput(false, CommonOutput.bookAppointment_fail_weekly_limit);
				}
				/*
				if (mn.checkAvailabilityOfAppointment(var.substring(0, 1), appointmentID).equalsIgnoreCase(
						"Available ")) {*/
					String temp2;
					temp2 = mn.UDPConnect(7001, var);
					return temp2.trim();
				/*} else {
					return ("No such appointment is available");
				}*/
				
			}else if(bookingServ.equalsIgnoreCase("MTL")){
				String count=mn.UDPConnect(7000, ("checkCount"+customerID+ appointmentID));
				String count1=mn.UDPConnect(7001, ("checkCount"+customerID+ appointmentID));
				int counter=Integer.parseInt(count.substring(0, 1))+Integer.parseInt(count1.substring(0, 1));
				if(counter==3){
					return CommonOutput.bookAppointmentOutput(false, CommonOutput.bookAppointment_fail_weekly_limit);
				}
				/*if (mn.checkAvailabilityOfAppointment(var.substring(0, 1), appointmentID).equalsIgnoreCase(
						"Available ")) {*/
					String temp3;
					temp3 = mn.UDPConnect(7000, var);
					return temp3.trim();
			/*	} else {
					return ("No such appointment is available");
				}*/
				
			}
			
		}
		return CommonOutput.bookAppointmentOutput(false, null);

	}

	@Override
	public String getBookingSchedule(String customerID, String serv) throws RemoteException {

		if (serv.equalsIgnoreCase("MTL")) {
			Montreal mn = new Montreal();
			String var ="Userdat"+customerID;

			String temp1 = mn.getUserData(customerID);
			String temp2 = mn.UDPConnect(7001, var);

			String temp3 = mn.UDPConnect(7002, var);

			String str = temp1.trim() +","+ temp2.trim() +","+ temp3.trim();
			
			String str2[] = str.split(",");
	    	List<String> al = new ArrayList<String>();
	    	al = Arrays.asList(str2);
	      HashMap<String, List<String>> a = new HashMap<String, List<String>>();
	    //  Map<String, List<String>>=
	    	for(String s: al){
	    		List<String> users = new ArrayList<String>();
	    	   String[] str3=s.split(" ");
	    	   if(str3.length==2){
		    		  if(a.containsKey(str3[0])){
		    			  users=a.get(str3[0]);
		    			  users.add(str3[1]);
		    			  a.put(str3[0], users);
		    		  }else{
		    			  users.add(str3[1]);
		    			  a.put(str3[0], users);
		    		  }
		    			 
		    	   }
	    	
	}

	    	return CommonOutput.getBookingScheduleOutput(true, a, null);

		} else if (serv.equalsIgnoreCase("QUE")) {
			Quebec mn = new Quebec();
			String var = "Userdat"+customerID;

			String temp1 = mn.getUserData(customerID);
			String temp2 = mn.UDPConnect(7000, var);

			String temp3 = mn.UDPConnect(7002, var);

			String str = temp1.trim() +","+ temp2.trim() +","+ temp3.trim();

			String str2[] = str.split(",");
	    	List<String> al = new ArrayList<String>();
	    	al = Arrays.asList(str2);
	      HashMap<String, List<String>> a = new HashMap<String, List<String>>();
	    //  Map<String, List<String>>=
	    	for(String s: al){
	    		List<String> users = new ArrayList<String>();
	    	   String[] str3=s.split(" ");
	    	  if(str3.length==2){
	    		  if(a.containsKey(str3[0])){
	    			  users=a.get(str3[0]);
	    			  users.add(str3[1]);
	    			  a.put(str3[0], users);
	    		  }else{
	    			  users.add(str3[1]);
	    			  a.put(str3[0], users);
	    		  }
	    			 
	    	   }
	    	
	}

	    	return CommonOutput.getBookingScheduleOutput(true, a, null);
		} else if (serv.equalsIgnoreCase("SHE")) {
			Sherbrook mn = new Sherbrook();
			String var = "Userdat"+customerID;

			String temp1 = mn.getUserData(customerID);
			String temp2 = mn.UDPConnect(7001, var);

			String temp3 = mn.UDPConnect(7000, var);

			String str = temp1.trim() +","+ temp2.trim() +","+ temp3.trim();

			String str2[] = str.split(",");
	    	List<String> al = new ArrayList<String>();
	    	al = Arrays.asList(str2);
	      HashMap<String, List<String>> a = new HashMap<String, List<String>>();
	    //  Map<String, List<String>>=
	    	for(String s: al){
	    		List<String> users = new ArrayList<String>();
	    	   String[] str3=s.split(" ");
	    	   if(str3.length==2){
		    		  if(a.containsKey(str3[0])){
		    			  users=a.get(str3[0]);
		    			  users.add(str3[1]);
		    			  a.put(str3[0], users);
		    		  }else{
		    			  users.add(str3[1]);
		    			  a.put(str3[0], users);
		    		  }
		    			 
		    	   }
	    	
	}

	    	return CommonOutput.getBookingScheduleOutput(true, a, null);
		}

		else
			return CommonOutput.getBookingScheduleOutput(false, null, null);
	}

	@Override
	public String cancelAppointment(String customerID, String appointmentID,
			String appointmentType, String serv) throws RemoteException {

		char[] ch = appointmentID.toCharArray();
		char[] ch2 = { ch[0], ch[1], ch[2] };
		String bookingServ = new String(ch2);

		if (serv.equalsIgnoreCase("MTL")) {
			Montreal mn = new Montreal();
			String var = mn.getHashMap(appointmentType)+"cancel "+customerID+appointmentID;

			if (serv.equalsIgnoreCase(bookingServ)) {
				
				if (mn.checkAvailabilityOfAppointment1(var.substring(0, 1), appointmentID).equalsIgnoreCase(
						"available ")) {
					if (mn.checkUserBooking(appointmentID, customerID,appointmentType)) {
						String c = mn.canceledAppointment(var.substring(0, 1),appointmentID, customerID,appointmentType);

						 return CommonOutput.cancelAppointmentOutput(true, null);
					} else
						return CommonOutput.cancelAppointmentOutput(false, CommonOutput.cancelAppointment_fail_not_registered_in_appointment);
				} else {
					return CommonOutput.cancelAppointmentOutput(false, CommonOutput.cancelAppointment_fail_no_such_appointment);
				}
			}
			else if(bookingServ.equalsIgnoreCase("QUE")){
				
				String temp2;
				temp2 = mn.UDPConnect(7001, var);
				return temp2.trim();
				
			}else if(bookingServ.equalsIgnoreCase("SHE")){
				
				String temp3;
				temp3 = mn.UDPConnect(7002, var);
				return temp3.trim();
			}
			
			
		} else if (serv.equalsIgnoreCase("QUE")) {
			Quebec mn = new Quebec();

			String var = mn.getHashMap(appointmentType)+"cancel "+customerID+appointmentID;
			if (serv.equalsIgnoreCase(bookingServ)) {
				if (mn.checkAvailabilityOfAppointment1(var.substring(0, 1), appointmentID).equalsIgnoreCase(
						"available ")) {
					if (mn.checkUserBooking(appointmentID, customerID,appointmentType)) {
						String c = mn.canceledAppointment(var.substring(0, 1),appointmentID, customerID,appointmentType);

						 return CommonOutput.cancelAppointmentOutput(true, null);
					} else
						return CommonOutput.cancelAppointmentOutput(false, CommonOutput.cancelAppointment_fail_not_registered_in_appointment);
				} else {
					return CommonOutput.cancelAppointmentOutput(false, CommonOutput.cancelAppointment_fail_no_such_appointment);
				}
			}
			else if(bookingServ.equalsIgnoreCase("MTL")){
				
				String temp2;
				temp2 = mn.UDPConnect(7000, var);
				return temp2.trim();
				
			}else if(bookingServ.equalsIgnoreCase("SHE")){
				
				String temp3;
				temp3 = mn.UDPConnect(7002, var);
				return temp3.trim();
			}
			
		} else if (serv.equalsIgnoreCase("SHE")) {
			Sherbrook mn = new Sherbrook();

			String var = mn.getHashMap(appointmentType)+"cancel "+customerID+appointmentID;
			if (serv.equalsIgnoreCase(bookingServ)) {
				if (mn.checkAvailabilityOfAppointment1(var.substring(0, 1), appointmentID).equalsIgnoreCase(
						"available ")) {
					if (mn.checkUserBooking(appointmentID, customerID,appointmentType)) {
						String c = mn.canceledAppointment(var.substring(0, 1),appointmentID, customerID,appointmentType);

						 return CommonOutput.cancelAppointmentOutput(true, null);
					} else
						return CommonOutput.cancelAppointmentOutput(false, CommonOutput.cancelAppointment_fail_not_registered_in_appointment);
				} else {
					return CommonOutput.cancelAppointmentOutput(false, CommonOutput.cancelAppointment_fail_no_such_appointment);
				}
			}
			else if(bookingServ.equalsIgnoreCase("QUE")){
				
				String temp2;
				temp2 = mn.UDPConnect(7001, var);
				return temp2.trim();
				
			}else if(bookingServ.equalsIgnoreCase("MTL")){
				
				String temp3;
				temp3 = mn.UDPConnect(7000, var);
				return temp3.trim();
			}
			
		}
		return CommonOutput.cancelAppointmentOutput(false, null);
		

	}
	public String swapAppointment(String customerID, String newAppointmentID,
			String newAppointmentType, String oldAppointmentID, String oldAppointmentType,
			String serv) throws RemoteException {
		AppointmentManagement d1 = new AppointmentManagement();
		AppointmentManagement d2 = new AppointmentManagement();
		StringBuffer str = new StringBuffer();

		char[] ch = newAppointmentID.toCharArray();
		char[] ch2 = { ch[0], ch[1], ch[2] };
		String newServ = new String(ch2);

		char[] ch1 = oldAppointmentID.toCharArray();
		char[] ch21 = { ch1[0], ch1[1], ch1[2] };
		String oldServ = new String(ch21);

		String Lowest_AppointmentId = new String();

		int month1 = Integer.parseInt(newAppointmentID.substring(6, 8));
		int month2 = Integer.parseInt(oldAppointmentID.substring(6, 8));
		int month = month1 - month2;
		int date1 = Integer.parseInt(newAppointmentID.substring(4, 6));
		int date2 = Integer.parseInt(oldAppointmentID.substring(4, 6));
		int date = 0;
		if (month == 0) {
			date = date1 - date2;
			if (date < 0) {
				date = date * -1;
				Lowest_AppointmentId = newAppointmentID;
			} else {
				Lowest_AppointmentId = oldAppointmentID;
			}
		} else if (month == 1) {
			date = (date1 + 30) - date2;
			Lowest_AppointmentId = oldAppointmentID;
		} else if (month == -1) {
			date = (date2 + 30) - date1;
			Lowest_AppointmentId = newAppointmentID;
		}

		int numberOfBooking = 0;
		int count = 0;
		if (serv.equalsIgnoreCase("MTL")) {
			Montreal mn = new Montreal();
			if (oldServ.equalsIgnoreCase("MTL")) {

				String var = mn.getHashMap(oldAppointmentType);

				// String variable="isBooked"+customerID;
				String bookingexistence = mn.isbooked(customerID,oldAppointmentType);
				if (bookingexistence.contains("false")) {
					return CommonOutput.swapAppointmentOutput(false, CommonOutput.swapAppointment_fail_not_registered_in_appointment);
				}

				if (mn.checkAvailabilityOfAppointment(var, oldAppointmentID)
						.equalsIgnoreCase("Available ")) {
					count++;
					if (Lowest_AppointmentId.equals(oldAppointmentID)) {
						String count1 = mn.UDPConnect(7001, ("checkCount"
								+ customerID + Lowest_AppointmentId));
						String count2 = mn.UDPConnect(7002, ("checkCount"
								+ customerID + Lowest_AppointmentId));
						numberOfBooking = Integer.parseInt(count1.substring(0,
								1)) + Integer.parseInt(count2.substring(0, 1));

					}
				}

			} else if (oldServ.equalsIgnoreCase("QUE")) {
				String var = oldAppointmentType.substring(0, 1) + "getExistence"
						+ oldAppointmentID;
				String ans = (mn.UDPConnect(7001, var));

				String variable = "isBooked" + customerID +oldAppointmentType;
				String bookingexistence = (mn.UDPConnect(7001, variable))
						;
				if (bookingexistence.contains("false")) {
					return CommonOutput.swapAppointmentOutput(false, CommonOutput.swapAppointment_fail_not_registered_in_appointment);
				}
				if (ans.contains("Available")) {
					count++;
					if (Lowest_AppointmentId.equals(oldAppointmentID)) {
						String count1 = mn.UDPConnect(7001, ("checkCount"
								+ customerID + Lowest_AppointmentId));
						String count2 = mn.UDPConnect(7002, ("checkCount"
								+ customerID + Lowest_AppointmentId));
						numberOfBooking = Integer.parseInt(count1.substring(0,
								1)) + Integer.parseInt(count2.substring(0, 1));
					}
				}

			} else if (oldServ.equalsIgnoreCase("SHE")) {
				String var = oldAppointmentType.substring(0, 1) + "getExistence"
						+ oldAppointmentID;
				String ans = (mn.UDPConnect(7002, var));

				String variable = "isBooked" + customerID+oldAppointmentType;
				String bookingexistence = (mn.UDPConnect(7002, variable))
						;
				if (bookingexistence.contains("false")) {
					return CommonOutput.swapAppointmentOutput(false, CommonOutput.swapAppointment_fail_not_registered_in_appointment);
				}
				if (ans.contains("Available")) {
					count++;
					if (Lowest_AppointmentId.equals(oldAppointmentID)) {
						String count1 = mn.UDPConnect(7001, ("checkCount"
								+ customerID + Lowest_AppointmentId));
						String count2 = mn.UDPConnect(7002, ("checkCount"
								+ customerID + Lowest_AppointmentId));
						numberOfBooking = Integer.parseInt(count1.substring(0,
								1)) + Integer.parseInt(count2.substring(0, 1));
					}
				}

			}

			if (newServ.equalsIgnoreCase("MTL")) {

				String var = mn.getHashMap(newAppointmentType);

				if (mn.checkAvailabilityOfAppointment(var, newAppointmentID)
						.equalsIgnoreCase("Available ")) {
					count++;
					if (Lowest_AppointmentId.equals(newAppointmentID)) {
						String count1 = mn.UDPConnect(7001, ("checkCount"
								+ customerID + Lowest_AppointmentId));
						String count2 = mn.UDPConnect(7002, ("checkCount"
								+ customerID + Lowest_AppointmentId));
						numberOfBooking = Integer.parseInt(count1.substring(0,
								1)) + Integer.parseInt(count2.substring(0, 1));

					}
				}

			} else if (newServ.equalsIgnoreCase("QUE")) {
				String var = newAppointmentType.substring(0, 1) + "getExistence"
						+ newAppointmentID;
				String ans = (mn.UDPConnect(7001, var));
				if (ans.contains("Available")) {
					count++;
					if (Lowest_AppointmentId.equals(newAppointmentID)) {
						String count1 = mn.UDPConnect(7001, ("checkCount"
								+ customerID + Lowest_AppointmentId));
						String count2 = mn.UDPConnect(7002, ("checkCount"
								+ customerID + Lowest_AppointmentId));
						numberOfBooking = Integer.parseInt(count1.substring(0,
								1)) + Integer.parseInt(count2.substring(0, 1));
					}
				}

			} else if (newServ.equalsIgnoreCase("SHE")) {
				String var = newAppointmentType.substring(0, 1) + "getExistence"
						+ newAppointmentID;
				String ans = (mn.UDPConnect(7002, var));
				if (ans.contains("Available")) {
					count++;
					if (Lowest_AppointmentId.equals(newAppointmentID)) {
						String count1 = mn.UDPConnect(7001, ("checkCount"
								+ customerID + Lowest_AppointmentId));
						String count2 = mn.UDPConnect(7002, ("checkCount"
								+ customerID + Lowest_AppointmentId));
						numberOfBooking = Integer.parseInt(count1.substring(0,
								1)) + Integer.parseInt(count2.substring(0, 1));
					}
				}

			}
		} else if (serv.equalsIgnoreCase("QUE")) {
			Quebec mn = new Quebec();
			if (oldServ.equalsIgnoreCase("QUE")) {

				String var = mn.getHashMap(oldAppointmentType);

				// String variable="isBooked"+customerID;
				String bookingexistence = mn.isbooked(customerID,oldAppointmentType);
				if (bookingexistence.contains("false")) {
					return CommonOutput.swapAppointmentOutput(false, CommonOutput.swapAppointment_fail_not_registered_in_appointment);
				}

				if (mn.checkAvailabilityOfAppointment(var, oldAppointmentID)
						.equalsIgnoreCase("Available ")) {
					count++;
					if (Lowest_AppointmentId.equals(oldAppointmentID)) {
						String count1 = mn.UDPConnect(7000, ("checkCount"
								+ customerID + Lowest_AppointmentId));
						String count2 = mn.UDPConnect(7002, ("checkCount"
								+ customerID + Lowest_AppointmentId));
						numberOfBooking = Integer.parseInt(count1.substring(0,
								1)) + Integer.parseInt(count2.substring(0, 1));

					}
				}

			} else if (oldServ.equalsIgnoreCase("MTL")) {
				String var = oldAppointmentType.substring(0, 1) + "getExistence"
						+ oldAppointmentID;
				String ans = (mn.UDPConnect(7000, var));
				//String test2=ans.substring(0, 10);
				String variable = "isBooked" + customerID+oldAppointmentType;
				String bookingexistence = (mn.UDPConnect(7000, variable))
						;
				if (bookingexistence.contains("false")) {
					return CommonOutput.swapAppointmentOutput(false, CommonOutput.swapAppointment_fail_not_registered_in_appointment);
				}
				if (ans.contains("Available")) {
					count++;
					if (Lowest_AppointmentId.equals(oldAppointmentID)) {
						String count1 = mn.UDPConnect(7000, ("checkCount"
								+ customerID + Lowest_AppointmentId));
						String count2 = mn.UDPConnect(7002, ("checkCount"
								+ customerID + Lowest_AppointmentId));
						numberOfBooking = Integer.parseInt(count1.substring(0,
								1)) + Integer.parseInt(count2.substring(0, 1));
					}
				}

			} else if (oldServ.equalsIgnoreCase("SHE")) {
				String var = oldAppointmentType.substring(0, 1) + "getExistence"
						+ oldAppointmentID;
				String ans = (mn.UDPConnect(7002, var));

				String variable = "isBooked" + customerID+oldAppointmentType;
				String bookingexistence = (mn.UDPConnect(7002, variable))
						;
				if (bookingexistence.contains("false")) {
					return CommonOutput.swapAppointmentOutput(false, CommonOutput.swapAppointment_fail_not_registered_in_appointment);
				}
				if (ans.contains("Available")) {
					count++;
					if (Lowest_AppointmentId.equals(oldAppointmentID)) {
						String count1 = mn.UDPConnect(7000, ("checkCount"
								+ customerID + Lowest_AppointmentId));
						String count2 = mn.UDPConnect(7002, ("checkCount"
								+ customerID + Lowest_AppointmentId));
						numberOfBooking = Integer.parseInt(count1.substring(0,
								1)) + Integer.parseInt(count2.substring(0, 1));
					}
				}

			}

			if (newServ.equalsIgnoreCase("QUE")) {

				String var = mn.getHashMap(newAppointmentType);

				if (mn.checkAvailabilityOfAppointment(var, newAppointmentID)
						.equalsIgnoreCase("Available ")) {
					count++;
					if (Lowest_AppointmentId.equals(newAppointmentID)) {
						String count1 = mn.UDPConnect(7000, ("checkCount"
								+ customerID + Lowest_AppointmentId));
						String count2 = mn.UDPConnect(7002, ("checkCount"
								+ customerID + Lowest_AppointmentId));
						numberOfBooking = Integer.parseInt(count1.substring(0,
								1)) + Integer.parseInt(count2.substring(0, 1));

					}
				}

			} else if (newServ.equalsIgnoreCase("MTL")) {
				String var = newAppointmentType.substring(0, 1) + "getExistence"
						+ newAppointmentID;
				String ans = (mn.UDPConnect(7000, var));
				if (ans.contains("Available")) {
					count++;
					if (Lowest_AppointmentId.equals(newAppointmentID)) {
						String count1 = mn.UDPConnect(7000, ("checkCount"
								+ customerID + Lowest_AppointmentId));
						String count2 = mn.UDPConnect(7002, ("checkCount"
								+ customerID + Lowest_AppointmentId));
						numberOfBooking = Integer.parseInt(count1.substring(0,
								1)) + Integer.parseInt(count2.substring(0, 1));
					}
				}

			} else if (newServ.equalsIgnoreCase("SHE")) {
				String var = newAppointmentType.substring(0, 1) + "getExistence"
						+ newAppointmentID;
				String ans = (mn.UDPConnect(7002, var));
				if (ans.contains("Available")) {
					count++;
					if (Lowest_AppointmentId.equals(newAppointmentID)) {
						String count1 = mn.UDPConnect(7000, ("checkCount"
								+ customerID + Lowest_AppointmentId));
						String count2 = mn.UDPConnect(7002, ("checkCount"
								+ customerID + Lowest_AppointmentId));
						numberOfBooking = Integer.parseInt(count1.substring(0,
								1)) + Integer.parseInt(count2.substring(0, 1));
					}
				}

			}
		} else if (serv.equalsIgnoreCase("SHE")) {
			Sherbrook mn = new Sherbrook();
			if (oldServ.equalsIgnoreCase("SHE")) {

				String var = mn.getHashMap(oldAppointmentType);

				// String variable="isBooked"+customerID;
				String bookingexistence = mn.isbooked(customerID,oldAppointmentType);
				if (bookingexistence.contains("false")) {
					return CommonOutput.swapAppointmentOutput(false, CommonOutput.swapAppointment_fail_not_registered_in_appointment);
				}

				if (mn.checkAvailabilityOfAppointment(var, oldAppointmentID)
						.equalsIgnoreCase("Available ")) {
					count++;
					if (Lowest_AppointmentId.equals(oldAppointmentID)) {
						String count1 = mn.UDPConnect(7000, ("checkCount"
								+ customerID + Lowest_AppointmentId));
						String count2 = mn.UDPConnect(7001, ("checkCount"
								+ customerID + Lowest_AppointmentId));
						numberOfBooking = Integer.parseInt(count1.substring(0,
								1)) + Integer.parseInt(count2.substring(0, 1));

					}
				}

			} else if (oldServ.equalsIgnoreCase("MTL")) {
				String var = oldAppointmentType.substring(0, 1) + "getExistence"
						+ oldAppointmentID;
				String ans = (mn.UDPConnect(7000, var));

				String variable = "isBooked" + customerID+oldAppointmentType;
				String bookingexistence = (mn.UDPConnect(7000, variable))
						;
				if (bookingexistence.contains("false")) {
					return CommonOutput.swapAppointmentOutput(false, CommonOutput.swapAppointment_fail_not_registered_in_appointment);
				}
				if (ans.contains("Available")) {
					count++;
					if (Lowest_AppointmentId.equals(oldAppointmentID)) {
						String count1 = mn.UDPConnect(7000, ("checkCount"
								+ customerID + Lowest_AppointmentId));
						String count2 = mn.UDPConnect(7001, ("checkCount"
								+ customerID + Lowest_AppointmentId));
						numberOfBooking = Integer.parseInt(count1.substring(0,
								1)) + Integer.parseInt(count2.substring(0, 1));
					}
				}

			} else if (oldServ.equalsIgnoreCase("QUE")) {
				String var = oldAppointmentType.substring(0, 1) + "getExistence"
						+ oldAppointmentID;
				String ans = (mn.UDPConnect(7001, var));

				String variable = "isBooked" + customerID+oldAppointmentType;
				String bookingexistence = (mn.UDPConnect(7001, variable))
						;
				if (bookingexistence.contains("false")) {
					return CommonOutput.swapAppointmentOutput(false, CommonOutput.swapAppointment_fail_not_registered_in_appointment);
				}
				if (ans.contains("Available")) {
					count++;
					if (Lowest_AppointmentId.equals(oldAppointmentID)) {
						String count1 = mn.UDPConnect(7000, ("checkCount"
								+ customerID + Lowest_AppointmentId));
						String count2 = mn.UDPConnect(7001, ("checkCount"
								+ customerID + Lowest_AppointmentId));
						numberOfBooking = Integer.parseInt(count1.substring(0,
								1)) + Integer.parseInt(count2.substring(0, 1));
					}
				}

			}

			if (newServ.equalsIgnoreCase("SHE")) {

				String var = mn.getHashMap(newAppointmentType);

				if (mn.checkAvailabilityOfAppointment(var, newAppointmentID)
						.equalsIgnoreCase("Available ")) {
					count++;
					if (Lowest_AppointmentId.equals(newAppointmentID)) {
						String count1 = mn.UDPConnect(7000, ("checkCount"
								+ customerID + Lowest_AppointmentId));
						String count2 = mn.UDPConnect(7001, ("checkCount"
								+ customerID + Lowest_AppointmentId));
						numberOfBooking = Integer.parseInt(count1.substring(0,
								1)) + Integer.parseInt(count2.substring(0, 1));

					}
				}

			} else if (newServ.equalsIgnoreCase("MTL")) {
				String var = newAppointmentType.substring(0, 1) + "getExistence"
						+ newAppointmentID;
				String ans = (mn.UDPConnect(7000, var));
				if (ans.contains("Available")) {
					count++;
					if (Lowest_AppointmentId.equals(newAppointmentID)) {
						String count1 = mn.UDPConnect(7000, ("checkCount"
								+ customerID + Lowest_AppointmentId));
						String count2 = mn.UDPConnect(7001, ("checkCount"
								+ customerID + Lowest_AppointmentId));
						numberOfBooking = Integer.parseInt(count1.substring(0,
								1)) + Integer.parseInt(count2.substring(0, 1));
					}
				}

			} else if (newServ.equalsIgnoreCase("QUE")) {
				String var = newAppointmentType.substring(0, 1) + "getExistence"
						+ newAppointmentID;
				String ans = (mn.UDPConnect(7001, var));
				if (ans.contains("Available")) {
					count++;
					if (Lowest_AppointmentId.equals(newAppointmentID)) {
						String count1 = mn.UDPConnect(7000, ("checkCount"
								+ customerID + Lowest_AppointmentId));
						String count2 = mn.UDPConnect(7001, ("checkCount"
								+ customerID + Lowest_AppointmentId));
						numberOfBooking = Integer.parseInt(count1.substring(0,
								1)) + Integer.parseInt(count2.substring(0, 1));
					}
				}

			}
		}

		if (count < 2) {
			return CommonOutput.swapAppointmentOutput(false, CommonOutput.swapAppointment_fail_no_such_appointment);
			//return ("Appointment Id doesn't exist or no capacity for the appointment");
		}

		if (date <= 3
				&& date >= 0
				&& !(oldServ.equalsIgnoreCase(newServ)
						&& newServ.equalsIgnoreCase(serv) && oldServ
							.equalsIgnoreCase(serv))) {
			if (numberOfBooking == 3) {
				String str2 = d1.cancelAppointment(customerID, oldAppointmentID,
						oldAppointmentType, serv);
				str.append(str2.trim());

				if (str2.contains("Success")) {
					String str1 = d1.bookAppointment(customerID, newAppointmentID,
							newAppointmentType, serv);
					str.append(str1);
					if (!(str1.contains("Success"))) {
						String str3 = d1.bookAppointment(customerID, oldAppointmentID,
								oldAppointmentType, serv);
						str.append(". Failed to swap appointment because booking was not availablle");
					}
				}
			} else {
				String str2 = d1.bookAppointment(customerID, newAppointmentID,
						newAppointmentType, serv);
				str.append(str2.trim());

				if (str2.contains("Success")) {
					String str1 = d1.cancelAppointment(customerID, oldAppointmentID,
							oldAppointmentType, serv);
					str.append(str1);
					if (!(str1.contains("Success"))) {
						String str3 = d1.cancelAppointment(customerID, newAppointmentID,
								newAppointmentType, serv);
						str.append(". Failed to swap appointment because booking was not availablle");
					}
				}
			}
		} else {
			String str2 = d1.bookAppointment(customerID, newAppointmentID, newAppointmentType,
					serv);
			str.append(str2.trim());

			if (str2.contains("Success")) {
				String str1 = d1.cancelAppointment(customerID, oldAppointmentID,
						oldAppointmentType, serv);
				str.append(str1);
				if (!(str1.contains("Success"))) {
					String str3 = d1.cancelAppointment(customerID, newAppointmentID,
							newAppointmentType, serv);
					str.append(". Failed to swap appointment because booking was not availablle");
				}
			}
		}

		/*
		 * ExecutorService executor = Executors.newFixedThreadPool(2);
		 * executor.execute(runnableTask); executor.execute(runnableTask2);
		 * 
		 * executor.shutdownNow();
		 */
		if((str.toString()).contains("Failed to swap")){
			return CommonOutput.swapAppointmentOutput(false, null);
		}
		else
		return CommonOutput.swapAppointmentOutput(true, null);
	}

	
	public String shutDown() throws RemoteException 
	{
		System.exit(0); 
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