package Replica3.server;

import Replica3.CommonOutput;
import Replica3.implementation.AppointmentManagement;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;



public class Quebec {
	
	
	public static HashMap<String, String> appointmentList = new HashMap<String, String>();
	public static HashMap<String, Integer> a = new HashMap<String, Integer>();
	public static HashMap<String, Integer> b = new HashMap<String, Integer>();
	public static HashMap<String, Integer> c = new HashMap<String, Integer>();
	//public static HashMap<String, ArrayList<String>> Muser1 = new HashMap<String, ArrayList<String>>();
	public static HashMap<String, HashMap<String, ArrayList<String>>> Muser1 = new HashMap<String, HashMap<String, ArrayList<String>>>();

	public static void main(String[] args) throws FileNotFoundException, RemoteException, AlreadyBoundException {
		/*String location="G:\\workspace\\6231_project\\src\\logger\\clientlog\\Quebec.txt";;
		PrintStream o=new PrintStream(new File(location));
		System.setOut(o);*/
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = new Date();

		// get reference to rootpoa &amp; activate

		AppointmentManagement stub = new AppointmentManagement();
		Registry registry = LocateRegistry.createRegistry(5556);

		registry.bind("Function", stub);


		appointmentList.put("CONFERENCES", "a");
		appointmentList.put("TRADESHOWS", "b");
		appointmentList.put("SEMINARS", "c");

		System.out.println("Quebec Server ready and waiting ...");

		DatagramSocket MSocket = null;
		try {
			MSocket = new DatagramSocket(7001);
			// create socket at agreed port
			byte[] buffer = new byte[1000];

			System.out.println("Quebec UDP Server started");
	                while (true) {
	                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
	                MSocket.receive(request);
	                Quebec m = new Quebec();
	            
	                String fullid = new String(request.getData());
	                if(fullid.substring(0, 10).equalsIgnoreCase("checkCount")){
	                    int count=m.getOccurances(fullid.substring(10, 18),fullid.substring(18, 28));
	                    String mcount=String.valueOf(count);
	                    byte[] msg = mcount.getBytes();
	                    DatagramPacket reply = new DatagramPacket(msg, msg.length,
	                            request.getAddress(), request.getPort());
	                    MSocket.send(reply);
	                }
	                if(fullid.substring(0, 7).equalsIgnoreCase("Userdat")){
						String customerID=(fullid.substring(7, 15));
						String tempo = m.getUserData(customerID);
						byte[] msg = tempo.getBytes();
						DatagramPacket reply = new DatagramPacket(msg, msg.length,
								request.getAddress(), request.getPort());
						MSocket.send(reply);
					}
	                if(fullid.substring(0, 8).equalsIgnoreCase("isBooked")){
                        String customerID=(fullid.substring(8, 16));
                        String appointmentType=(fullid.substring(16, 26));
                        
                        String bookingexistence=m.isbooked(customerID,appointmentType);
                        byte[] msg = bookingexistence.getBytes();
                        DatagramPacket reply = new DatagramPacket(msg, msg.length,
                                request.getAddress(), request.getPort());
                        MSocket.send(reply);
                    }
                    if(fullid.substring(1, 13).equalsIgnoreCase("getExistence")){
                        String oldAppointmentID=(fullid.substring(13, 23));
                        String oldAppointmentType=(fullid.substring(0, 1));
                        if(oldAppointmentType.equalsIgnoreCase("c")){
							oldAppointmentType = "CONFERENCES";
                        }else if(oldAppointmentType.equalsIgnoreCase("t")){
							oldAppointmentType = "TRADESHOWS";
                        }else if(oldAppointmentType.equalsIgnoreCase("s")){
							oldAppointmentType = "SEMINARS";
                        }
                        String var = m.getHashMap(oldAppointmentType);
                        String ans=m.checkAvailabilityOfAppointment(var, oldAppointmentID);
                        
                        byte[] msg = ans.getBytes();
                        DatagramPacket reply = new DatagramPacket(msg, msg.length,
                                request.getAddress(), request.getPort());
                        MSocket.send(reply);
                    }
	                String var = fullid.substring(0, 1);
	                String var2 = fullid.substring(1, 8);
	                
	                if (var.equalsIgnoreCase("a")  ) {
		                	String appointmentType="CONFERENCES";
		                    if(var2.equalsIgnoreCase("display")){
	                    String done = m.display(var);
	                    byte[] msg = done.getBytes();
	                    DatagramPacket reply = new DatagramPacket(msg, msg.length,
	                            request.getAddress(), request.getPort());
	                    MSocket.send(reply);
	                    }
	                    else if(var2.equalsIgnoreCase("booked ")){
	                        String customerID = fullid.substring( 8,16);
	                        String appointmentID = fullid.substring(16, 26);
	                        if (m.checkAvailabilityOfAppointment(var, appointmentID).equalsIgnoreCase(
									"Available ")) {
								String s = m.bookedAppointment(var,appointmentID, customerID,appointmentType);
								String r=new String();
								if(s.contains("SecondBooking")){
									 r= CommonOutput.bookAppointmentOutput(false, null);
								}else
								 r=CommonOutput.bookAppointmentOutput(true, null);
								byte[] msg = r.getBytes();
								DatagramPacket reply = new DatagramPacket(msg, msg.length,
										request.getAddress(), request.getPort());
								MSocket.send(reply);
							} else if (m.checkAvailabilityOfAppointment(var.substring(0, 1), appointmentID).equalsIgnoreCase(
									"No Capacity ")) {//it checks both condition capacity and existence
								String r = CommonOutput.bookAppointmentOutput(false, CommonOutput.bookAppointment_fail_no_capacity);
								byte[] msg = r.getBytes();
								DatagramPacket reply = new DatagramPacket(msg, msg.length,
										request.getAddress(), request.getPort());
								MSocket.send(reply); 
								
							}else {
								String r = CommonOutput.bookAppointmentOutput(false, CommonOutput.bookAppointment_fail_no_such_appointment);
										//"No such appointment is available";
								byte[] msg = r.getBytes();
								DatagramPacket reply = new DatagramPacket(msg, msg.length,
										request.getAddress(), request.getPort());
								MSocket.send(reply);
								
							}
	                    }else if(var2.equalsIgnoreCase("cancel ")){
							String customerID = fullid.substring( 8,16);
							String appointmentID = fullid.substring(16, 26);
							if (m.checkAvailabilityOfAppointment1(var, appointmentID).equalsIgnoreCase(
									"available ")) {
								if (m.checkUserBooking(appointmentID, customerID,appointmentType)) {
									String s = m.canceledAppointment(var,appointmentID, customerID,appointmentType);
									String c=CommonOutput.cancelAppointmentOutput(true, null);
									byte[] msg = c.getBytes();
									DatagramPacket reply = new DatagramPacket(msg, msg.length,
											request.getAddress(), request.getPort());
									MSocket.send(reply);
								} else{
									String c =  CommonOutput.cancelAppointmentOutput(false, CommonOutput.cancelAppointment_fail_not_registered_in_appointment);
									byte[] msg = c.getBytes();
									DatagramPacket reply = new DatagramPacket(msg, msg.length,
											request.getAddress(), request.getPort());
									MSocket.send(reply);
								}
									
							} else {
								
								String c = CommonOutput.cancelAppointmentOutput(false, CommonOutput.cancelAppointment_fail_no_such_appointment);
								byte[] msg = c.getBytes();
								DatagramPacket reply = new DatagramPacket(msg, msg.length,
										request.getAddress(), request.getPort());
								MSocket.send(reply);
							}
						}
	                
	                } else if (var.equalsIgnoreCase("b")  ) {
	                	String appointmentType="TRADESHOWS";	
	                    if(var2.equalsIgnoreCase("display")){
	                        String done = m.display(var);
	                        byte[] msg = done.getBytes();
	                        DatagramPacket reply = new DatagramPacket(msg, msg.length,
	                                request.getAddress(), request.getPort());
	                        MSocket.send(reply);
	                        }
	                    else if(var2.equalsIgnoreCase("booked ")){
							String customerID = fullid.substring( 8,16);
							String appointmentID = fullid.substring(16, 26);
							if (m.checkAvailabilityOfAppointment(var, appointmentID).equalsIgnoreCase(
									"Available ")) {
								String s = m.bookedAppointment(var,appointmentID, customerID,appointmentType);
								String r=new String();
								if(s.contains("SecondBooking")){
									 r= CommonOutput.bookAppointmentOutput(false, null);
								}else
								 r=CommonOutput.bookAppointmentOutput(true, null);
								byte[] msg = r.getBytes();
								DatagramPacket reply = new DatagramPacket(msg, msg.length,
										request.getAddress(), request.getPort());
								MSocket.send(reply);
							} else if (m.checkAvailabilityOfAppointment(var.substring(0, 1), appointmentID).equalsIgnoreCase(
									"No Capacity ")) {//it checks both condition capacity and existence
								String r = CommonOutput.bookAppointmentOutput(false, CommonOutput.bookAppointment_fail_no_capacity);
								byte[] msg = r.getBytes();
								DatagramPacket reply = new DatagramPacket(msg, msg.length,
										request.getAddress(), request.getPort());
								MSocket.send(reply); 
								
							}else {
								String r = CommonOutput.bookAppointmentOutput(false, CommonOutput.bookAppointment_fail_no_such_appointment);
								byte[] msg = r.getBytes();
								DatagramPacket reply = new DatagramPacket(msg, msg.length,
										request.getAddress(), request.getPort());
								MSocket.send(reply);
								
							}
	                    }else if(var2.equalsIgnoreCase("cancel ")){
							String customerID = fullid.substring( 8,16);
							String appointmentID = fullid.substring(16, 26);
							if (m.checkAvailabilityOfAppointment1(var, appointmentID).equalsIgnoreCase(
									"available ")) {
								if (m.checkUserBooking(appointmentID, customerID,appointmentType)) {
									String s = m.canceledAppointment(var,appointmentID, customerID,appointmentType);
									String c=CommonOutput.cancelAppointmentOutput(true, null);
									byte[] msg = c.getBytes();
									DatagramPacket reply = new DatagramPacket(msg, msg.length,
											request.getAddress(), request.getPort());
									MSocket.send(reply);
								} else{
									String c =  CommonOutput.cancelAppointmentOutput(false, CommonOutput.cancelAppointment_fail_not_registered_in_appointment);
									byte[] msg = c.getBytes();
									DatagramPacket reply = new DatagramPacket(msg, msg.length,
											request.getAddress(), request.getPort());
									MSocket.send(reply);
								}
									
							} else {
								
								String c = CommonOutput.cancelAppointmentOutput(false, CommonOutput.cancelAppointment_fail_no_such_appointment);
								byte[] msg = c.getBytes();
								DatagramPacket reply = new DatagramPacket(msg, msg.length,
										request.getAddress(), request.getPort());
								MSocket.send(reply);
							}
						}
	                } else if (var.equalsIgnoreCase("c") ) {
	                	String appointmentType="SEMINARS";
	                    if(var2.equalsIgnoreCase("display")){
	                        String done = m.display(var);
	                        byte[] msg = done.getBytes();
	                        DatagramPacket reply = new DatagramPacket(msg, msg.length,
	                                request.getAddress(), request.getPort());
	                        MSocket.send(reply);
	                        }
	                	else if(var2.equalsIgnoreCase("booked ")){
							String customerID = fullid.substring( 8,16);
							String appointmentID = fullid.substring(16, 26);
							if (m.checkAvailabilityOfAppointment(var, appointmentID).equalsIgnoreCase(
									"Available ")) {
								String s = m.bookedAppointment(var,appointmentID, customerID,appointmentType);
								String r=new String();
								if(s.contains("SecondBooking")){
									 r= CommonOutput.bookAppointmentOutput(false, null);
								}else
								 r=CommonOutput.bookAppointmentOutput(true, null);
								byte[] msg = r.getBytes();
								DatagramPacket reply = new DatagramPacket(msg, msg.length,
										request.getAddress(), request.getPort());
								MSocket.send(reply);
							} else if (m.checkAvailabilityOfAppointment(var.substring(0, 1), appointmentID).equalsIgnoreCase(
									"No Capacity ")) {//it checks both condition capacity and existence
								String r = CommonOutput.bookAppointmentOutput(false, CommonOutput.bookAppointment_fail_no_capacity);
								byte[] msg = r.getBytes();
								DatagramPacket reply = new DatagramPacket(msg, msg.length,
										request.getAddress(), request.getPort());
								MSocket.send(reply); 
								
							}else {
								String r = CommonOutput.bookAppointmentOutput(false, CommonOutput.bookAppointment_fail_no_such_appointment);
								byte[] msg = r.getBytes();
								DatagramPacket reply = new DatagramPacket(msg, msg.length,
										request.getAddress(), request.getPort());
								MSocket.send(reply);
								
							}
	                    }else if(var2.equalsIgnoreCase("cancel ")){
							String customerID = fullid.substring( 8,16);
							String appointmentID = fullid.substring(16, 26);
							if (m.checkAvailabilityOfAppointment1(var, appointmentID).equalsIgnoreCase(
									"available ")) {
								if (m.checkUserBooking(appointmentID, customerID,appointmentType)) {
									String s = m.canceledAppointment(var,appointmentID, customerID,appointmentType);
									String c=CommonOutput.cancelAppointmentOutput(true, null);
									byte[] msg = c.getBytes();
									DatagramPacket reply = new DatagramPacket(msg, msg.length,
											request.getAddress(), request.getPort());
									MSocket.send(reply);
								} else{
									String c =  CommonOutput.cancelAppointmentOutput(false, CommonOutput.cancelAppointment_fail_not_registered_in_appointment);
									byte[] msg = c.getBytes();
									DatagramPacket reply = new DatagramPacket(msg, msg.length,
											request.getAddress(), request.getPort());
									MSocket.send(reply);
								}
									
							} else {
								
								String c = CommonOutput.cancelAppointmentOutput(false, CommonOutput.cancelAppointment_fail_no_such_appointment);
								byte[] msg = c.getBytes();
								DatagramPacket reply = new DatagramPacket(msg, msg.length,
										request.getAddress(), request.getPort());
								MSocket.send(reply);
							}
						}
	                } 
	                
	                }
	 
	            } catch (SocketException e) {
	                System.out.println("Socket: " + e.getMessage());
	            } catch (IOException e) {
	                System.out.println("IO: " + e.getMessage());
	            } finally {
	                if (MSocket != null)
	                    MSocket.close();
	            }
			

		

	}

	public synchronized String getHashMap(String appointmentType) {
		// it sends a b or c depending on input
		String value = appointmentList.get(appointmentType);

		return value;
	}

	public synchronized String addHashMap(String var, String key, int Value) {
		if (var == "a") {
			// var=appointmentType sub_hashmap , key=appointmentID Value=booking Capacity
			if (a.get(key) != null) {

				int val = a.get(key);
				a.replace(key, val + Value);
				System.out.println ("Value updated for " + key + "to " + val);
				 return CommonOutput.addAppointmentOutput(true, CommonOutput.addAppointment_success_capacity_updated);
			} else {
				a.put(key, Value);
				System.out.println ("Added Successfully " + key + "to " + a.get(key));
				 return CommonOutput.addAppointmentOutput(true, CommonOutput.addAppointment_success_added);
			}
		} else if (var == "b") {
			if (b.get(key) != null) {

				int val = b.get(key);
				b.replace(key, val + Value);
				System.out.println ("Value updated for " + key + "to " + val);
				 return CommonOutput.addAppointmentOutput(true, CommonOutput.addAppointment_success_capacity_updated);
			} else {
				b.put(key, Value);
				System.out.println ("Added Successfully " + key + "to " + a.get(key));
				 return CommonOutput.addAppointmentOutput(true, CommonOutput.addAppointment_success_added);
			}
		} else if (var == "c") {
			if (c.get(key) != null) {

				int val = c.get(key);
				c.replace(key, val + Value);
				System.out.println ("Value updated for " + key + "to " + val);
				 return CommonOutput.addAppointmentOutput(true, CommonOutput.addAppointment_success_capacity_updated);
			} else {
				c.put(key, Value);
				System.out.println ("Added Successfully " + key + "to " + a.get(key));
				 return CommonOutput.addAppointmentOutput(true, CommonOutput.addAppointment_success_added);
			}
		} else
			return CommonOutput.addAppointmentOutput(false, CommonOutput.addAppointment_fail_cannot_decrease_capacity);

	}

	public synchronized String removeHashMap(String var, String key) {
		//kye=appointment id
		if (var == "a") {
			if (a.get(key) != null) {
				String key1 = key;
				StringBuffer remover = new StringBuffer("Removed ");
				if (Muser1.containsKey(key)) {
					remover.append("Booking found for customer " + Muser1.get(key));
					char[] c = key.toCharArray();
					String str = Character.toString(c[4]);
					String str2 = Character.toString(c[5]);
					String dat = str + str2;

					int i = Integer.parseInt(dat);
					int counter = 0;
					for (; i < 30; i++) {
						char[] c1 = key1.toCharArray();
						if (c1[3] == 'M') {
							key1 = key.substring(0, 3) + "A" + key1.substring(4);
							if (a.containsKey(key1) && a.get(key1) != 0) {
								Muser1.put(key1, Muser1.get(key));
								Muser1.remove(key);
								int Value = a.get(key1);
								a.replace(key1, Value - 1);
												remover.append(". "+"Booking got changed to "+key1);
												counter++;
												break;
												}
											} c1 = key1.toCharArray();
										if(c1[3]=='A'){
											key1 = key.substring(0, 3)+"E" + key1.substring(4);
											if (a.containsKey(key1) && a.get(key1) != 0 ){
												Muser1.put(key1, Muser1.get(key));
												Muser1.remove(key);
												int Value = a.get(key1);
												a.replace(key1, Value - 1);
												remover.append(". " + "Booking got changed to " + key1);
												counter++;
												break;
											}
											c1 = key1.toCharArray();
										}
						if (i + 1 < 10)
							key1 = key1.substring(0, 4) + "0" + (i + 1) + key1.substring(6);
						else
							key1 = key1.substring(0, 4) + (i + 1) + key1.substring(6);
										c1 = key1.toCharArray();
										if(c1[3]=='E'){
													key1 = key.substring(0, 3)+"M" + key1.substring(4);
													if (a.containsKey(key1) && a.get(key1) != 0 ){
														Muser1.put(key1, Muser1.get(key));
														Muser1.remove(key);
														int Value=a.get(key1);
														a.replace(key1, Value-1);
														remover.append(". "+"Booking got changed to "+key1);
														counter++;
														break;
													} }c1 = key1.toCharArray();
					}
					if(counter==0){
						Muser1.remove(key);
						remover.append(". "+"All Booking got cancelled for "+key);
					}
				}
				a.remove(key);
				System.out.println(key + " ." +remover.toString());
				return CommonOutput.removeAppointmentOutput(true, null);
			} else {
				 return CommonOutput.removeAppointmentOutput(false, CommonOutput.removeAppointment_fail_no_such_appointment);
				//return("No record");
			}
		} else if (var == "b") {
			if (b.get(key) != null) {
				String key1 = key;
				StringBuffer remover = new StringBuffer("Removed ");
				if (Muser1.containsKey(key)) {
					remover.append("Booking found for customer " + Muser1.get(key));
					char[] c = key.toCharArray();
					String str = Character.toString(c[4]);
					String str2 = Character.toString(c[5]);
					String dat = str + str2;

					int i = Integer.parseInt(dat);
					int counter = 0;
					for (; i < 30; i++) {
						char[] c1 = key1.toCharArray();
						if (c1[3] == 'M') {
							key1 = key.substring(0, 3) + "A" + key1.substring(4);
							if (b.containsKey(key1) && b.get(key1) != 0) {
								Muser1.put(key1, Muser1.get(key));
								Muser1.remove(key);
								int Value = b.get(key1);
								b.replace(key1, Value - 1);
												remover.append(". "+"Booking got changed to "+key1);
												counter++;
												break;
												}
											} c1 = key1.toCharArray();
										if(c1[3]=='A'){
											key1 = key.substring(0, 3)+"E" + key1.substring(4);
											if (b.containsKey(key1) && b.get(key1) != 0 ){
												Muser1.put(key1, Muser1.get(key));
												Muser1.remove(key);
												int Value = b.get(key1);
												b.replace(key1, Value - 1);
												remover.append(". " + "Booking got changed to " + key1);
												counter++;
												break;
											}
											c1 = key1.toCharArray();
										}
						if (i + 1 < 10)
							key1 = key1.substring(0, 4) + "0" + (i + 1) + key1.substring(6);
						else
							key1 = key1.substring(0, 4) + (i + 1) + key1.substring(6);
										c1 = key1.toCharArray();
										if(c1[3]=='E'){
													key1 = key.substring(0, 3)+"M" + key1.substring(4);
													if (b.containsKey(key1) && b.get(key1) != 0 ){
														Muser1.put(key1, Muser1.get(key));
														Muser1.remove(key);
														int Value=b.get(key1);
														b.replace(key1, Value-1);
														remover.append(". "+"Booking got changed to "+key1);
														counter++;
														break;
													} }c1 = key1.toCharArray();
					}
					if(counter==0){
						Muser1.remove(key);
						remover.append(". "+"All Booking got cancelled for "+key);
					}
				}
				b.remove(key);
				System.out.println(key + " ." +remover.toString());
				return CommonOutput.removeAppointmentOutput(true, null);
			} else {
				 return CommonOutput.removeAppointmentOutput(false, CommonOutput.removeAppointment_fail_no_such_appointment);
				//return("No record");
			}
		} else if (var == "c") {
			if (c.get(key) != null) {
				String key1 = key;
				StringBuffer remover = new StringBuffer("Removed ");
				if (Muser1.containsKey(key)) {
					remover.append("Booking found for customer " + Muser1.get(key));
					char[] ch = key.toCharArray();
					String str = Character.toString(ch[4]);
					String str2 = Character.toString(ch[5]);
					String dat = str + str2;

					int i = Integer.parseInt(dat);
					int counter = 0;
					for (; i < 30; i++) {
						char[] c1 = key1.toCharArray();
						if (c1[3] == 'M') {
							key1 = key.substring(0, 3) + "A" + key1.substring(4);
							if (c.containsKey(key1) && c.get(key1) != 0) {
								Muser1.put(key1, Muser1.get(key));
								Muser1.remove(key);
								int Value = c.get(key1);
								c.replace(key1, Value - 1);
												remover.append(". "+"Booking got changed to "+key1);
												counter++;
												break;
												}
											} c1 = key1.toCharArray();
										if(c1[3]=='A'){
											key1 = key.substring(0, 3)+"E" + key1.substring(4);
											if (c.containsKey(key1) && c.get(key1) != 0 ){
												Muser1.put(key1, Muser1.get(key));
												Muser1.remove(key);
												int Value = c.get(key1);
												c.replace(key1, Value - 1);
												remover.append(". " + "Booking got changed to " + key1);
												counter++;
												break;
											}
											c1 = key1.toCharArray();
										}
						if (i + 1 < 10)
							key1 = key1.substring(0, 4) + "0" + (i + 1) + key1.substring(6);
						else
							key1 = key1.substring(0, 4) + (i + 1) + key1.substring(6);
										c1 = key1.toCharArray();
										if(c1[3]=='E'){
													key1 = key.substring(0, 3)+"M" + key1.substring(4);
													if (c.containsKey(key1) && c.get(key1) != 0 ){
														Muser1.put(key1, Muser1.get(key));
														Muser1.remove(key);
														int Value=c.get(key1);
														c.replace(key1, Value-1);
														remover.append(". "+"Booking got changed to "+key1);
														counter++;
														break;
													} }c1 = key1.toCharArray();
					}
					if(counter==0){
						Muser1.remove(key);
						remover.append(". "+"All Booking got cancelled for "+key);
					}
				}
				c.remove(key);
				System.out.println(key + " ." +remover.toString());
				return CommonOutput.removeAppointmentOutput(true, null);
			} else {
				 return CommonOutput.removeAppointmentOutput(false, CommonOutput.removeAppointment_fail_no_such_appointment);
				//return("No record");
			}
		}
		return CommonOutput.removeAppointmentOutput(false, null);
		
	}

	public synchronized String display(String var) {
		HashMap<String, Integer> temp = new HashMap<String, Integer>();
		String value = var;
		System.out.println("List for appointment type ");
		String ss = " ";
		if (value.equalsIgnoreCase("a")) {
			a.entrySet().forEach(entry -> {
				System.out.println(entry.getKey());
				System.out.println(entry.getValue());
				// ss=ss+entry.getKey();
					temp.put(entry.getKey(), entry.getValue());
				});
			
			
		} else if (value.equalsIgnoreCase("b")) {
			b.entrySet().forEach(entry -> {
				temp.put(entry.getKey(), entry.getValue());
			});

		} else if (value.equalsIgnoreCase("c")) {
			c.entrySet().forEach(entry -> {
				temp.put(entry.getKey(), entry.getValue());
			});

		}

		StringBuffer str = new StringBuffer();
		temp.entrySet().forEach(entry -> {

			str.append(entry.getKey() + " " + entry.getValue() + ",");
		});
		return str.toString();

	}

	public synchronized String checkAvailabilityOfAppointment(String var, String key) {
		// key is appointment id

		if (var.equalsIgnoreCase("a")) {
			if (a.containsKey(key) ) {
				if(a.get(key) != 0)
					return ("Available ");
				else
					return ("No Capacity ");
			} else {

				return ("Not");
			}
		} else if (var.equalsIgnoreCase("b")) {
			if (b.containsKey(key) ) {
				if(b.get(key) != 0)
					return ("Available ");
				else
					return ("No Capacity ");
			} else {

				return ("Not");
			}
		} else if (var.equalsIgnoreCase("c")) {
			if (c.containsKey(key)) {
				if(c.get(key) != 0)
					return ("Available ");
				else
					return ("No Capacity ");
			} else {

				return ("Not");
			}
		}
		return null;
	}
	public synchronized String checkAvailabilityOfAppointment1(String var, String key) {
		// key is appointment id

		if (var.equalsIgnoreCase("a")) {
			if (a.containsKey(key)) {
				return ("Available ");
			} else {

				return ("Not");
			}
		} else if (var.equalsIgnoreCase("b")) {
			if (b.containsKey(key)) {
				return ("Available ");
			} else {

				return ("Not");
			}
		} else if (var.equalsIgnoreCase("c")) {
			if (c.containsKey(key)) {
				return ("Available ");
			} else {

				return ("Not");
			}
		}
		return null;
	}
	public synchronized String bookedAppointment(String var,String appointmentID, String customerID,String appointmentType) {
		// TODO Auto-generated method stub
		char[] ch = customerID.toCharArray();
		char[] ch1 = {ch[0], ch[1], ch[2]};
		String server = new String(ch1);
		ArrayList<String> users = new ArrayList<String>();
		if (Muser1.containsKey(appointmentID)) {
			Quebec m = new Quebec();
			if(m.secondBook(appointmentID,customerID,appointmentType)){
				return "SecondBooking";
			}
			HashMap<String, ArrayList<String>> h=new HashMap<String, ArrayList<String>>();
			h= Muser1.get(appointmentID);
			
			if(h.containsKey(appointmentType)){
			users = h.get(appointmentType);
			users.add(customerID);
			h.replace(appointmentType, users);
			Muser1.put(appointmentID, h);
			}
			else{
				//HashMap<String, ArrayList<String>> h1=new HashMap<String, ArrayList<String>>();
				users.add(customerID);
				h.put(appointmentType, users);
				Muser1.put(appointmentID, h);
				
			}
		} else {
			HashMap<String, ArrayList<String>> h=new HashMap<String, ArrayList<String>>();
			users.add(customerID);
			h.put(appointmentType, users);
			Muser1.put(appointmentID, h);
		}
		
		if(var.equalsIgnoreCase("a")){
			int Value=a.get(appointmentID);
			a.replace(appointmentID, Value-1);
		} else if(var.equalsIgnoreCase("b")){
			int Value=b.get(appointmentID);
			b.replace(appointmentID, Value-1);
		} else if(var.equalsIgnoreCase("c")){
			int Value=c.get(appointmentID);
			c.replace(appointmentID, Value-1);
		}
		
		String s = "booked appointment " + appointmentID + " for " + customerID;
		System.out.println(s);
		return s;
	}

	public synchronized boolean secondBook(String appointmentID,String customerID,String appointmentType){
		HashMap<String, ArrayList<String>> h=Muser1.get(appointmentID); 
		ArrayList<String> users1 = h.get(appointmentType);
		if (/*Muser.containsKey(appointmentID) &&*/users1 != null && users1.contains(customerID)){
			return true;
		}
		return false;
		
	}
	public synchronized String canceledAppointment(String var,String appointmentID, String customerID,String appointmentType) {
		// TODO Auto-generated method stub


		char[] ch = customerID.toCharArray();
		char[] ch1 = {ch[0], ch[1], ch[2]};
		String server = new String(ch1);

		ArrayList<String> users = new ArrayList<String>();
		HashMap<String, ArrayList<String>> h=new HashMap<String, ArrayList<String>>();
		
		if (Muser1.containsKey(appointmentID)) {
			h = Muser1.get(appointmentID);
			/*if(h.size()==1){*/
			users=h.get(appointmentType);
			
				users.remove(customerID);
				h.replace(appointmentType, users);
				Muser1.put(appointmentID, h);
			
			
			if(users.size() == 0){
			h.remove(appointmentType);
			if(h.size()==0)
				Muser1.remove(appointmentID);
			}
			
			if(var.equalsIgnoreCase("a")){
				int Value=a.get(appointmentID);
				a.replace(appointmentID, Value+1);
			} else if(var.equalsIgnoreCase("b")){
				int Value=b.get(appointmentID);
				b.replace(appointmentID, Value+1);
			} else if(var.equalsIgnoreCase("c")){
				int Value=c.get(appointmentID);
				c.replace(appointmentID, Value+1);
			}
		}
		
		
		String s = "cancelled appointment " + appointmentID + " for " + customerID;
		return s;
	}

	public synchronized boolean checkUserBooking(String appointmentID,
			String customerID,String appointmentType) {
		// TODO Auto-generated method stub
		//Muser.put(appointmentID, customerID);
		return Muser1.containsKey(appointmentID)
				&& (Muser1.get(appointmentID)).get(appointmentType).contains(customerID);
	}

	public synchronized String getUserData(String customerID) {
		HashMap<String, String> temp11 = new HashMap<String, String>();

		StringBuffer str = new StringBuffer(" ");
		Muser1.entrySet().forEach(entry -> {
			
			
			entry.getValue().entrySet().forEach(entry1 -> {
				System.out.println(entry1.getKey());
				if (entry1.getValue().contains(customerID))
					str.append(entry1.getKey()+" "+entry.getKey()+",");
			});
		});
		return str.toString();

	}
	
	public synchronized int getOccurances(String customerID,String AppointmentId) {
		// TODO Auto-generated method stub
	/*	int[] count = {0};
		Muser.entrySet().forEach(entry -> {
			
			if (entry.getValue().contains(customerID)){
				count[0]++;
			}
				
		});
		return count[0];*/
		int[] count = {0};
		int date=Integer.parseInt(AppointmentId.substring(4,6));
		ArrayList<String> ar=new ArrayList<String>();
		
		int w=date/7;
		
		for(int i=7*w;i<7*w+7;i++){
			int c=i+1;
			String newAppointment = "";
			if (c >= 1 && c < 10) {
				newAppointment = "QUE" + AppointmentId.substring(3, 4) + "0" + c + AppointmentId.substring(6, 10);
			} else if (c >= 10) {
				newAppointment = "QUE" + AppointmentId.substring(3, 4) + c + AppointmentId.substring(6, 10);
			}
			 if(AppointmentId.substring(3,4).equalsIgnoreCase("M")){
            	 ar.add(newAppointment);
            	 ar.add(newAppointment.substring(0, 3)+"A"+newAppointment.substring(4));
            	 ar.add(newAppointment.substring(0, 3)+"E"+newAppointment.substring(4));
            }else if(AppointmentId.substring(3,4).equalsIgnoreCase("A")){
            	 ar.add(newAppointment);
            	 ar.add(newAppointment.substring(0, 3)+"M"+newAppointment.substring(4));
            	 ar.add(newAppointment.substring(0, 3)+"E"+newAppointment.substring(4));
            }else if(AppointmentId.substring(3,4).equalsIgnoreCase("E")){
            	 ar.add(newAppointment);
            	 ar.add(newAppointment.substring(0, 3)+"A"+newAppointment.substring(4));
            	 ar.add(newAppointment.substring(0, 3)+"M"+newAppointment.substring(4));
            }
		}
		
		
		Muser1.entrySet().forEach(entry -> {
			
			if (ar.contains(entry.getKey())){
				
				entry.getValue().entrySet().forEach(entry1 -> {
					//System.out.println(entry1.getKey());
					if(entry1.getValue().contains(customerID))
						count[0]++;
				});
			}
				
		});
		return count[0];
	}
	 public synchronized String isbooked(String customerID,String AppointmentType) {
		 StringBuffer str = new StringBuffer();
		 int[] count = {0};
         Muser1.entrySet().forEach(entry -> {
        	 entry.getValue().entrySet().forEach(entry1 -> {
 				//System.out.println(entry1.getKey());
 				if (entry1.getKey().equalsIgnoreCase(AppointmentType) && entry1.getValue().contains(customerID))
 					count[0]++;
 			});
                 
             
         });
         
         if(count[0]==0)
             str.append("false");
         else
             str.append("true");
         return str.toString();
     }
	
	public synchronized String UDPConnect(int serverPort, String combinedId) {
		DatagramSocket aSocket = null;
		String str = "";
		try {
			System.out.println("Quebec client started");
			aSocket = new DatagramSocket();
			byte[] message = combinedId.getBytes();

			InetAddress aHost = InetAddress.getByName("localhost");

			// int serverPort = this.;
			// DatagramPacket request =new DatagramPacket(m, args[0].length(),
			// aHost, serverPort);
			DatagramPacket request = new DatagramPacket(message,
					combinedId.length(), aHost, serverPort);

			aSocket.send(request);
			System.out.println("Request message sent via UDP");

			byte[] buffer = new byte[1000];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

			aSocket.receive(reply);
			String str1=new String(reply.getData());
            str=str1.trim();
            
            return str;
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (aSocket != null)
				aSocket.close();
		}
		return str;
	}

}