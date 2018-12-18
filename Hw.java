import java.sql.*;
import java.util.*;
import java.io.*;

public class Hw
{
	private static Connection connection = null;

	public static void main(final String[] args) {
		try {		
			// Check if required arguments are supplied
			if (args.length > 0) {			
				// Read user supplied db file path
				String fileDBProperties = null;
				
				// Read user supplied Query Number
				String queryNumber = null;
				
				try { 
					// Read user supplied db file path
					fileDBProperties = args[0].trim();
					
					// Read user supplied Query Number
					queryNumber = args[1].trim();
				} 
				catch (Exception ex) {
					System.out.println("Please provide valid inputs and try again."); 
					System.exit(0);
				}
				
				// Declare required variables
				String query = null;
				ResultSet rs = null;
				PreparedStatement ps = null;
				
				// Connect to Database
				connection = connectDB(fileDBProperties);

				// Check if connection created
				if (connection != null) {
					//Check if Query number is q1
					if (queryNumber.equals("q1")) {
						// Declare user input variable "# of vertices"
						int numVertices = -99;
						
						try {
							// Assign user input variable "# of vertices"
							numVertices = Integer.parseInt(args[2]);
						} 
						catch (Exception ex) {
							System.out.println("Please provide valid inputs and try again."); 
							
							try { if (connection != null) closeConnection(connection); } catch (Exception e) { }
							
							System.exit(0);
						}
						
						// Assign user supplied first Lat-Long tuple
						String zoneLongLat = "Polygon((" + args[3].trim() + " " + args[4].trim();
						
						// Loop thru rest of user supplied Lat-Long tuples and Concatenate  
						for (int i = 5; i < args.length; i += 2) {
							zoneLongLat = zoneLongLat + "," + args[i].trim() + " " + args[i + 1].trim();
						}
						
						// Concatenate first tuple at the end to complete the loop
						zoneLongLat = zoneLongLat + "," + args[3].trim() + " " + args[4].trim() + "))";
						
						// MySQL Query to get all incidents that occurred within the given polygon
						query = "Select incidentID, ST_Y(incidentLocation), ST_X(incidentLocation), incidentType";
						query = query + " from incident where MBRContains(ST_GeomFromText(?), incidentLocation) order by incidentID";
						
						try	{
							// Create a PreparedStatement object for sending parameterized SQL statements to the database
							ps = connection.prepareStatement(query);
							ps.setString(1, zoneLongLat);
							
							// Pass gathered variables to getResults function to Query Database and assign received result to ResultSet
							rs = getResults(fileDBProperties, queryNumber, ps);
							
							// Check if ResultSet is not empty
							if (rs != null) {
								int resultCount = 0;
								
								// Loop thru ResultSet
								while (rs.next()) {
									 // Assign output Incident ID
									 int incidentID  = rs.getInt("incidentID");
									 
									 // Assign output Incident Location
									 String incidentLocation_Lat = rs.getString(2);
									 
									 // Assign output Incident Location
									 String incidentLocation_Long = rs.getString(3);
									 
									 // Assign output Incident Type
									 String incidentType = rs.getString("incidentType");
									 
									 // Display Required results
									 System.out.println(incidentID + "\t" + incidentLocation_Lat + ", " + incidentLocation_Long + "\t" + incidentType);
									 
									 resultCount++;
								}
								
								if (resultCount == 0)
									System.out.println("No incident has been registered in provided zone!");
							} else {
								// Display No results
								System.out.println("No Incident has been registered in provided zone!");
							}					
						}
						catch (Exception e) {						
							System.out.println("Error while printing result!");
						}
					} else if (queryNumber.equals("q2")) {		
						// Declare user input variable "Incident ID"
						String incidentID = null;
						
						// Declare user input variable "Distance"
						String sDistance = null;
						
						try { 
							// Assign user input variable "Incident ID"
							incidentID = args[2];
							
							// Assign user input variable "Distance"
							sDistance = args[3];
						} 
						catch (Exception ex) {
							System.out.println("Please provide valid inputs and try again."); 
							
							try { if (connection != null) closeConnection(connection); } catch (Exception e) { }
							
							System.exit(0);
						}
						
						// MySQL Query to get all officers that are within the given distance of the incident
						// query = "select badgeNumber, round(abs(GCDist(st_x(currLocation), st_y(currLocation),";
						// query = query + " st_x(incidentlocation), st_y(incidentlocation)) * 111.325 * 1000)) as distance, name";
						// query = query + " from officer cross join incident where incidentID = ?";
						// query = query + " and abs(gcdist(st_x(currLocation), st_y(currLocation),";
						// query = query + " st_x(incidentlocation), st_y(incidentlocation)) * 111.325 * 1000) < ?";
						// query = query + " order by distance";
						
						query = "select badgeNumber, round(abs(ST_DISTANCE_SPHERE(currLocation,incidentlocation))) as distance, name";
						query = query + " from officer cross join incident where incidentID = ?";
						query = query + " and ST_DISTANCE_SPHERE(currLocation,incidentlocation) < ?";
						query = query + " order by distance";
						
						try	{
							// Create a PreparedStatement object for sending parameterized SQL statements to the database
							ps = connection.prepareStatement(query);
							ps.setString(1, incidentID);
							ps.setString(2, sDistance);
							
							// Pass gathered variables to getResults function to Query Database and assign received result to ResultSet
							rs = getResults(fileDBProperties, queryNumber, ps);
							
							// Check if ResultSet is not empty
							if (rs != null)	{
								int resultCount = 0;
								
								// Loop thru ResultSet
								while (rs.next()) {
									 // Assign output Badge Number
									 int badgeNumber  = rs.getInt("badgeNumber");
									 
									 // Assign output Distance
									 int distance = rs.getInt(2);
									 
									 // Assign output Officer Name
									 String name = rs.getString("name");								 
									 
									 //Display Required results
									 System.out.println(badgeNumber + "\t" + distance + "m\t" + name);
									 
									 resultCount++;
								}
								
								if (resultCount == 0)
									System.out.println("No Officer was found in given distance of the incident!");
							} else {
								// Display No results								
								System.out.println("No Officer was found in given distance of the incident!");
							}						
						}
						catch (Exception e)	{
							System.out.println("Error while printing result!");
						}
					} else if (queryNumber.equals("q3")) {
						String squadNumber = null;
						
						try { 
							squadNumber = args[2]; 
						} 
						catch (Exception ex) {
							System.out.println("Please provide valid inputs and try again."); 
							
							try { if (connection != null) closeConnection(connection); } catch (Exception e) { }
							
							System.exit(0);
						}
						
						query = "select zoneName from zone where squadNumber = ?";

						try	{
							ps = connection.prepareStatement(query);	
							ps.setString(1, squadNumber);
							rs = getResults(fileDBProperties, queryNumber, ps);

							if (rs != null) {			
								int resultCount = 0;
								
								while (rs.next()) {
									String zoneName = rs.getString("zoneName");
									
									//Display values
									System.out.println("Sqaud " + squadNumber + " is now patrolling: " + zoneName);	

									resultCount++;
								}
								
								if (resultCount == 0)
									System.out.println("No zone found for Squad Number :" + squadNumber + ".");
							}
							
							query = "select o.badgeNumber, case MBRContains(z.zonePoints, o.currLocation) when 0 then 'OUT' when 1 then 'IN' end, o.name";
							query = query + " from officer o inner join zone z on o.squadNumber = z.squadNumber where o.squadNumber = ?";

							ps = connection.prepareStatement(query);						
							ps.setString(1, squadNumber);
							
							rs = getResults(fileDBProperties, queryNumber, ps);
							
							if (rs != null)	{
								int resultCount = 0;
								
								while (rs.next()) {								
									//Retrieve by column name
									 int badgeNumber  = rs.getInt("badgeNumber");
									 String in_or_out = rs.getString(2);
									 String name = rs.getString("name");								 
								
									 //Display values
									 System.out.println(badgeNumber + "\t" + in_or_out + "\t" + name);	

									resultCount++;
								}
								
								if (resultCount == 0)
									System.out.println("There is no Officer assigned in Sqaud Number :" + squadNumber + ".");
							}
						}
						catch (Exception e)	{
							System.out.println("Error while printing result!");
						}					
					} else if (queryNumber.equals("q4")) {
						String routeNumber = null;
						
						try { 
							routeNumber = args[2]; 
						} 
						catch (Exception ex) {
							System.out.println("Please provide valid inputs and try again."); 
							
							try { if (connection != null) closeConnection(connection); } catch (Exception e) { }
							
							System.exit(0);
						}
						
						query = "select zoneID, zoneName from zone where st_intersects(zonePoints,";
						query = query + " (select routeLatLong from route where routeNumber = ?))";
						
						try	{
							ps = connection.prepareStatement(query);
							ps.setString(1, routeNumber);
							
							rs = getResults(fileDBProperties, queryNumber, ps);
							
							if (rs != null)	{
								int resultCount = 0;
								
								while (rs.next()) {
									//Retrieve by column name
									int zoneID  = rs.getInt("zoneID");
									String zoneName = rs.getString("zoneName");						 
									 
									//Display values
									System.out.println(zoneID + "\t" + zoneName);
								
									resultCount++;
								}
								
								if (resultCount == 0)
									System.out.println("There is no zone through which Route Number :" + routeNumber + " passes.");
							}
						}
						catch (Exception e) {
							System.out.println("Error while printing result!");
						}
					} else {
						System.out.println("Please enter correct query number!\n");
					}
				}
				
				try { if (rs != null) rs.close(); } catch (Exception e) { }
				try { if (ps != null) ps.close(); } catch (Exception e) { }
				try { if (connection != null) closeConnection(connection); } catch (Exception e) { }
			} else {
				System.out.println("Please supply required arguments.\n");
			}		
		}
		catch (Exception ex) {
			System.out.println("Unknow error occured. Please try again!\n");
		}		
	}
	
	public static ResultSet getResults(final String dbFIle, final String queryNumber, final PreparedStatement ps) {
		ResultSet rs = null;
		
		if (connection != null) {
			try	{
				rs = ps.executeQuery();
			}
			catch (SQLException se) {
				System.out.println("Unknown error occured while executing the query");
				se.printStackTrace();
				try { if (rs != null) rs.close(); } catch (Exception e) { }
				return null;
			}
		}	
		
		return rs;
	}	

	//@SuppressWarnings("deprecation")
	public static Connection connectDB(final String propertiesFile) {
		Properties prop = new Properties();
		InputStream input = null;
		
		try {
			try { input = new FileInputStream(propertiesFile); } 
			catch (Exception e) { System.out.println("The system cannot find the file specified. Please try again!!!"); return null; }

			BufferedReader br = new BufferedReader(new InputStreamReader(input));
			
			prop.setProperty("host", br.readLine().trim());
			prop.setProperty("port", br.readLine().trim());
			prop.setProperty("dbName", br.readLine().trim());
			prop.setProperty("user", br.readLine().trim());
			prop.setProperty("pass", br.readLine().trim());
						
			String url = "jdbc:mysql://" + prop.getProperty("host") + ":" + prop.getProperty("port")
							+ "/" + prop.getProperty("dbName") + "?useSSL=false";
			String user = prop.getProperty("user");
			String pass = prop.getProperty("pass");
						
			try {
				try	{ Class.forName("com.mysql.jdbc.Driver"); } catch (Exception e) { Class.forName("com.mysql.cj.jdbc.Driver"); }							
				connection = DriverManager.getConnection(url, user, pass);
				
				if (connection != null) {
					//System.out.println("Connection Successful!");
					System.out.println("");
				} else {
					System.out.println("Failed to make a MySQL connection!");
				}
			}
			catch (ClassNotFoundException e) {
				System.out.println("Please provide JDBC driver.");
				return null;
			}
			catch (SQLException e) {
				System.out.println("Given Address, User & Password do not match. Please verify and try again.");
				return null;
			}
		}
		catch (IOException ex) {
			System.out.println("Please provide Properties File.");
		}
		catch (Exception ex) {
			System.out.println("Provided db.properties file doesn't have all the required parameters.");
		}
		finally {
			if (input != null) {
				try {
					input.close();
				}
				catch (IOException e) {
					System.out.println("Error while closing db.properties file.");
				}
			}
		}
		
		return connection;
	}

	public static void closeConnection(final Connection connection) {
		try	{
			if (connection != null)	{
				connection.close();
				//System.out.println("Connection Closed!");				
				System.out.println("");
			}
		}
		catch (SQLException se) {
			System.out.println("Failed to close connection.");
		}
	}
}
