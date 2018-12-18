import java.sql.*;
import java.util.*;
import java.io.*;

public class Populate {
	private static Connection connection = null;
	private static BufferedReader reader;

    public static void main(final String[] args) {
		try	{
			// Declare required filepath variables
			String fileDBProperties = null;
			String fileZone = null;
			String fileOfficer = null;
			String fileRoute = null;
			String fileIncident = null;

			// Check if required arguments are supplied
			if (args.length > 0) {
				//Check if supplied number of arguments are valid.
				if (args.length == 5) {
					try	{
						// Assign filepath to respective filepath variable
						fileDBProperties = args[0].trim();
						fileZone = args[1].trim();
						fileOfficer = args[2].trim();
						fileRoute = args[3].trim();
						fileIncident = args[4].trim();
					} 
					catch (Exception ex) {
						System.out.println("Please provide valid inputs and try again."); 					
						System.exit(0);
					}
					
					// Read data from files and store data into List variables
					List<Zone> lstZone = readZoneFile(fileZone);
					List<Officer> lstOfficer = readOfficerFile(fileOfficer);
					List<Route> lstRoute = readRouteFile(fileRoute);
					List<Incident> lstIncident = readIncidentFile(fileIncident);
					
					// Connect to Database
					connection = connectDB(fileDBProperties);
					
					if (connection != null) {
						// Clear Database
						System.out.println("Clearing Database");
						clearDB();
						System.out.println("Cleared Database\n");
						
						// Insert Zone Data
						System.out.println("Inserting Zone List");
						insertZoneList(lstZone);
						System.out.println("Inserted Zone List\n");
						
						// Insert Officer Data
						System.out.println("Inserting Officer List");
						insertOfficers(lstOfficer);
						System.out.println("Inserted Officer List\n");
						
						// Insert Route Data
						System.out.println("Inserting Route List");
						insertRoute(lstRoute);
						System.out.println("Inserted Route list\n");

						// Insert Incident Data
						System.out.println("Inserting Incident List");
						insertIncident(lstIncident);
						System.out.println("Inserted Incident list\n");
						
						// Close Database connection
						closeConnection(connection);
					}
				} else {
					System.out.println("Supplied count of arguments incorrect. Please retry!\n");			
				}
			} else {
				System.out.println("Please supply required arguments.\n");			
			}		
		}
		catch (Exception ex)	{
			System.out.println("Unknow error occured. Please try again!\n");
		}
    }

	public static List<Zone> readZoneFile(final String zoneFile) {
		// Declare Array to store Zone details
		List<Zone> lstZone = new ArrayList<Zone>();
		
		try {
			reader = new BufferedReader(new FileReader(zoneFile));
			String line = reader.readLine();
			
			while (line != null) {
				List<String> zoneLine = Arrays.asList(line.split(","));
				Zone z = new Zone();
				z.zoneID = Integer.parseInt(zoneLine.get(0).trim());
				z.zoneName = zoneLine.get(1).trim().replaceAll("^\"|\"$", "");
				z.squadNumber = Integer.parseInt(zoneLine.get(2).trim());
				z.numVertices = Integer.parseInt(zoneLine.get(3).trim());
				z.zoneLongLat = "POLYGON((" + zoneLine.get(4).trim() + " " + zoneLine.get(5).trim();
				
				int i = 6;
				while (i < zoneLine.size())	{					
					z.zoneLongLat = z.zoneLongLat + "," + zoneLine.get(i).trim() + " " + zoneLine.get(i + 1).trim();
					i = i + 2;
				}
				
				z.zoneLongLat = z.zoneLongLat + "," + zoneLine.get(4).trim() + " " + zoneLine.get(5).trim() + "))";
				
				lstZone.add(z);
				 
				// read next line
				line = reader.readLine();
			}
			
			reader.close();
		} 
		catch (IOException e) {
			System.out.println("Error while reading Zone file. Please validate the file and try again!");
			return null;
		}

		return lstZone;
	}
	
	public static List<Officer> readOfficerFile(final String officerFile)	{
		List<Officer> lstOfficer = new ArrayList<Officer>();
				
		try {
			reader = new BufferedReader(new FileReader(officerFile));
			String line = reader.readLine();
			
			while (line != null) {
				List<String> officerLine = Arrays.asList(line.split(","));
				Officer o = new Officer();
				o.badgeNumber = Integer.parseInt(officerLine.get(0).trim());
				o.name = officerLine.get(1).trim().replaceAll("^\"|\"$", "");
				o.squadNumber = Integer.parseInt(officerLine.get(2).trim());
				o.currLocation = "Point(" + officerLine.get(3).trim() + " " + officerLine.get(4).trim() + ")";
				
				lstOfficer.add(o);
				 
				// read next line
				line = reader.readLine();
			}
			
			reader.close();
		} 
		catch (IOException e) {
			System.out.println("Here 3");
			e.printStackTrace();
		}

		return lstOfficer;
	}
	
	public static List<Route> readRouteFile(final String routeFile) {
		List<Route> lstRoute = new ArrayList<Route>();
		
		try {
			reader = new BufferedReader(new FileReader(routeFile));
			String line = reader.readLine();
			
			while (line != null) {
				List<String> routeLine = Arrays.asList(line.split(","));
				Route r = new Route();
				r.routeNumber = Integer.parseInt(routeLine.get(0).trim());
				r.numVertices = Integer.parseInt(routeLine.get(1).trim());
				r.routeLongLat = "LineString(" + routeLine.get(2).trim() + " " + routeLine.get(3).trim();
				
				int i = 4;
				while (i < routeLine.size()) {					
					r.routeLongLat = r.routeLongLat + "," + routeLine.get(i).trim() + " " + routeLine.get(i + 1).trim();
					i = i + 2;
				}
				
				r.routeLongLat = r.routeLongLat + ")";
				
				lstRoute.add(r);
				 
				// read next line
				line = reader.readLine();
			}			
						
			reader.close();
		} 
		catch (IOException e) {
			System.out.println("Here 4");
			e.printStackTrace();
		}	

		return lstRoute;
	}
	
	public static List<Incident> readIncidentFile(final String incidentFile) {
		List<Incident> lstIncident = new ArrayList<Incident>();
				
		try {
			reader = new BufferedReader(new FileReader(incidentFile));
			String line = reader.readLine();
			
			while (line != null) {
				List<String> incidentLine = Arrays.asList(line.split(","));
				Incident i = new Incident();
				i.incidentID = Integer.parseInt(incidentLine.get(0).trim());
				i.incidentType = incidentLine.get(1).trim().replaceAll("^\"|\"$", "");
				i.incidentLocation = "POINT(" + incidentLine.get(2).trim() + " " + incidentLine.get(3).trim() + ")";
				
				lstIncident.add(i);
				 
				// read next line
				line = reader.readLine();
			}			
						
			reader.close();
		} 
		catch (IOException e) {
			System.out.println("Here 5");
			e.printStackTrace();
		}

		return lstIncident;
	}
	
	public static void clearDB() {
		String truncateZone = "truncate table zone";
		String truncateOfficer = "truncate table officer";
		String truncateRoute = "truncate table route";
		String truncateIncident = "truncate table incident";
		
		PreparedStatement ps = null;

		try {
			try	{
				ps = connection.prepareStatement(truncateZone);
				ps.executeUpdate();
				System.out.println("\tCleared Zone table");
			}
			catch (SQLException se) {					
				System.out.println("Error while clearing Zone table.");
				
				if (se.getSQLState().equalsIgnoreCase("42S02")) {
					System.err.println("Zone table doesn't exists.");
					try { if (connection != null) closeConnection(connection); } catch (Exception e) { }
					System.exit(0);
				}				
			}
			try {
				ps = connection.prepareStatement(truncateOfficer);
				ps.executeUpdate();
				System.out.println("\tCleared Officer table");
			}
			catch (SQLException se) {					
				System.out.println("Error while clearing Officer table");
				
				if (se.getSQLState().equalsIgnoreCase("42S02")) {
					System.err.println("Officer table doesn't exists.");
					try { if (connection != null) closeConnection(connection); } catch (Exception e) { }
					System.exit(0);
				}	
			}

			try	{		
				ps = connection.prepareStatement(truncateRoute);
				ps.executeUpdate();
				System.out.println("\tCleared Route table");
			}
			catch (SQLException se) {
				System.out.println("Error while clearing Route table");
				
				if (se.getSQLState().equalsIgnoreCase("42S02")) {
					System.err.println("Route table doesn't exists.");
					try { if (connection != null) closeConnection(connection); } catch (Exception e) { }
					System.exit(0);
				}					
			}

			try	{	
				ps = connection.prepareStatement(truncateIncident);
				ps.executeUpdate();
				System.out.println("\tCleared Incident table");
			}
			catch (SQLException se) {
				System.out.println("Error while clearing Incident table");
				
				if (se.getSQLState().equalsIgnoreCase("42S02")) {
					System.err.println("Incident table doesn't exists.");
					try { if (connection != null) closeConnection(connection); } catch (Exception e) { }
					System.exit(0);
				}	
			}						

			ps.close();
		}		
		catch (SQLException se) {
			System.out.println("Unknown error while clearing database!");
		}
	}
	
	public static void insertZoneList(List<Zone> zoneList) {
		PreparedStatement ps = null;
		String insertedZone = "";
		String notInsertedZone = "";
		
		try	{
			String sql = "insert into zone (zoneID, zoneName, squadNumber, numVertices, zonePoints) values (?, ?, ?, ?, ST_GeomFromText(?))";					
			ps = connection.prepareStatement(sql);
			
			for (Zone z: zoneList) {
				try	{				
					ps.setString(1, Integer.toString(z.zoneID));
					ps.setString(2, z.zoneName);
					ps.setString(3, Integer.toString(z.squadNumber));
					ps.setString(4, Integer.toString(z.numVertices));
					ps.setString(5, z.zoneLongLat);
					
					try {
						int i = ps.executeUpdate();
						
						if (i > 0) {
							//System.out.println("Successfully Inserted");
							
							if (insertedZone == "")
								insertedZone = Integer.toString(z.zoneID);
							else
								insertedZone = insertedZone + ", " + Integer.toString(z.zoneID);
						}
						else {
							System.out.println("Insert Failed"); 
							
							if (notInsertedZone == "")
								notInsertedZone = Integer.toString(z.zoneID);
							else
								notInsertedZone = notInsertedZone + ", " + Integer.toString(z.zoneID);
						}
					}
					catch (SQLException se) {						
						if (se.getSQLState().equalsIgnoreCase("42S02")) {
							System.err.println("Zone table doesn't exists.");

							try { if (connection != null) closeConnection(connection); } catch (Exception e) { }
							System.exit(0);
						}
					}					
				}
				catch (SQLException se) {
					if (notInsertedZone == "")
						notInsertedZone = Integer.toString(z.zoneID);
					else
						notInsertedZone = notInsertedZone + ", " + Integer.toString(z.zoneID);
				}				
			}
			
			if (insertedZone != "")
				System.out.println("\tInserted Zone ID: " + insertedZone);
			if (notInsertedZone != "")
				System.out.println("\tError while inserting Zone ID: " + notInsertedZone);
				
			ps.close();
		}
		catch (SQLException se) {
			System.out.println("\tUnknown Error while inserting Zone data");
		}	
	}
	
	public static void insertOfficers(List<Officer> officerList) {
		String sql = "insert into officer (badgeNumber, name, squadNumber, currLocation) values (?, ?, ?, ST_GeomFromText(?))";
		PreparedStatement ps = null;
		String insertedOfficer = "";
		String notInsertedOfficer = "";

		try {
			ps = connection.prepareStatement(sql);
			
			for (Officer o: officerList) {
				try {
					ps.setString(1, Integer.toString(o.badgeNumber));
					ps.setString(2, o.name);
					ps.setString(3, Integer.toString(o.squadNumber));
					ps.setString(4, o.currLocation);
					
					try {
						int i = ps.executeUpdate();
						
						if (i > 0) {
							//System.out.println("Successfully Inserted");
						
							if (insertedOfficer == "")
								insertedOfficer = Integer.toString(o.badgeNumber);
							else
								insertedOfficer = insertedOfficer + ", " + Integer.toString(o.badgeNumber);
						}
						else
						{
							System.out.println("Insert Failed"); 
						
							if (notInsertedOfficer == "")
								notInsertedOfficer = Integer.toString(o.badgeNumber);
							else
								notInsertedOfficer = notInsertedOfficer + ", " + Integer.toString(o.badgeNumber);
						}
					}
					catch (SQLException se) {
						if (se.getSQLState().equalsIgnoreCase("42S02")) {
							System.err.println("Officer table doesn't exists.");
							try { if (connection != null) closeConnection(connection); } catch (Exception e) { }
							System.exit(0);
						}
					}
				}
				catch (SQLException se) {
					if (notInsertedOfficer == "")
						notInsertedOfficer = Integer.toString(o.badgeNumber);
					else
						notInsertedOfficer = notInsertedOfficer + ", " + Integer.toString(o.badgeNumber);
				}
			}
			
			if (insertedOfficer != "")
				System.out.println("\tInserted Badge Number : " + insertedOfficer);
			if (notInsertedOfficer != "")
				System.out.println("\tError while inserting Badge Number : " + notInsertedOfficer);
			
			ps.close();
		}
		catch (SQLException se) {
			System.out.println("\tUnknown Error while inserting Officer data");
		}	
	}
	
	public static void insertRoute(List<Route> routeList) {
		String sql = "insert into route (routeNumber, numVertices, routeLatLong) values (?, ?, ST_GeomFromText(?))";
		PreparedStatement ps = null;
		String insertedRoute = "";
		String notInsertedRoute = "";
		
		try {
			ps = connection.prepareStatement(sql);
			
			for (Route r: routeList) {
				try	{
					ps.setString(1, Integer.toString(r.routeNumber));				
					ps.setString(2, Integer.toString(r.numVertices));
					ps.setString(3, r.routeLongLat);				
					
					try {
						int i = ps.executeUpdate();
						
						if (i > 0) {
							//System.out.println("Successfully Inserted");
							
							if (insertedRoute == "")
								insertedRoute = Integer.toString(r.routeNumber);
							else
								insertedRoute = insertedRoute + ", " + Integer.toString(r.routeNumber);
						}
						else
						{
							System.out.println("Insert Failed"); 
							
							if (notInsertedRoute == "")
								notInsertedRoute = Integer.toString(r.routeNumber);
							else
								notInsertedRoute = notInsertedRoute + ", " + Integer.toString(r.routeNumber);
						}
					}
					catch (SQLException se) {
						if (se.getSQLState().equalsIgnoreCase("42S02")) {
							System.err.println("Route table doesn't exists.");
							try { if (connection != null) closeConnection(connection); } catch (Exception e) { }
							System.exit(0);
						}
					}
				}
				catch (SQLException se) {
					if (notInsertedRoute == "")
						notInsertedRoute = Integer.toString(r.routeNumber);
					else
						notInsertedRoute = notInsertedRoute + ", " + Integer.toString(r.routeNumber);
				}
			}
			
			if (insertedRoute != "")
				System.out.println("\tInserted Route Number : " + insertedRoute);
			if (notInsertedRoute != "")
				System.out.println("\tError while inserting Route Number : " + notInsertedRoute);
			
			ps.close();
		}
		catch (SQLException se)
		{
			System.out.println("\tUnknown Error while inserting Route data");
		}
	}
	
	public static void insertIncident(List<Incident> incidentList) {
		String sql = "insert into incident (incidentID, incidentType, incidentLocation) values (?, ?, ST_GeomFromText(?))";		
		PreparedStatement ps = null;
		String insertedIncident = "";
		String notInsertedIncident = "";
		
		try	{
			ps = connection.prepareStatement(sql);
			
			for (Incident i: incidentList) {
				try {
					ps.setString(1, Integer.toString(i.incidentID));				
					ps.setString(2, i.incidentType);
					ps.setString(3, i.incidentLocation);				
					
					try {
						int c = ps.executeUpdate();
						
						if (c > 0) {
							//System.out.println("Successfully Inserted");

							if (insertedIncident == "")
								insertedIncident = Integer.toString(i.incidentID);
							else
								insertedIncident = insertedIncident + ", " + Integer.toString(i.incidentID);
						}
						else {
							System.out.println("Insert Failed"); 
							
							if (notInsertedIncident == "")
								notInsertedIncident = Integer.toString(i.incidentID);
							else
								notInsertedIncident = notInsertedIncident + ", " + Integer.toString(i.incidentID);
						}
					}
					catch (SQLException se) {
						if (se.getSQLState().equalsIgnoreCase("42S02")) {
							System.err.println("Incident table doesn't exists.");
							try { if (connection != null) closeConnection(connection); } catch (Exception e) { }
							System.exit(0);
						}
					}
				}
				catch (SQLException se) {
					if (notInsertedIncident == "")
						notInsertedIncident = Integer.toString(i.incidentID);
					else
						notInsertedIncident = notInsertedIncident + ", " + Integer.toString(i.incidentID);
				}
			}
			
			if (insertedIncident != "")
				System.out.println("\tInserted Incident ID : " + insertedIncident);
			if (notInsertedIncident != "")
				System.out.println("\tError while inserting Incident ID : " + notInsertedIncident);
			
			ps.close();
		}
		catch (SQLException se) {
			System.out.println("\tUnknown Error while inserting Incident data");
		}	
	}
	
	//@SuppressWarnings("deprecation")
	public static Connection connectDB(String propertiesFile) {
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
				}
				else {
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

	public static void closeConnection(Connection connection) {
		try	{
			if (connection != null) {
				connection.close();
				//System.out.println("Connection Closed!");				
				System.out.println("");
			}
		}
		catch (SQLException se) {
			System.out.println("Failed to close connection.");
		}
	}

	public static class Zone {
		private int zoneID;
		private String zoneName;
		private int squadNumber;
		private int numVertices;
		private String zoneLongLat;
	}

	public static class Officer {
		private int badgeNumber;
		private String name;
		private int squadNumber;
		private String currLocation;
	}

	public static class Route {
		private int routeNumber;
		private int numVertices;
		private String routeLongLat;
	}

	public static class Incident {
		private int incidentID;
		private String incidentType;
		private String incidentLocation;
	}
}
