import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.io.File;

import javafx.util.Pair;

import java.util.ArrayList;


public class Assignment4 {
    // change to ( jdbc:sqlserver://localhost;integratedSecurity=true )
    private static String connectionString = "jdbc:sqlserver://localhost;integratedSecurity=true";
    private static String databaseName = "DB2019_Ass2";
    private Assignment4() {
    }

    public static void executeFunc(Assignment4 ass, String[] args) {
        String funcName = args[0];
        switch (funcName) {
            case "loadNeighborhoodsFromCsv":
                ass.loadNeighborhoodsFromCsv(args[1]);
                break;
            case "dropDB":
                ass.dropDB();
                break;
            case "initDB":
                ass.initDB(args[1]);
                break;
            case "updateEmployeeSalaries":
                ass.updateEmployeeSalaries(Double.parseDouble(args[1]));
                break;
            case "getEmployeeTotalSalary":
                System.out.println(ass.getEmployeeTotalSalary());
                break;
            case "updateAllProjectsBudget":
                ass.updateAllProjectsBudget(Double.parseDouble(args[1]));
                break;
            case "getTotalProjectBudget":
                System.out.println(ass.getTotalProjectBudget());
                break;
            case "calculateIncomeFromParking":
                System.out.println(ass.calculateIncomeFromParking(Integer.parseInt(args[1])));
                break;
            case "getMostProfitableParkingAreas":
                System.out.println(ass.getMostProfitableParkingAreas());
                break;
            case "getNumberOfParkingByArea":
                System.out.println(ass.getNumberOfParkingByArea());
                break;
            case "getNumberOfDistinctCarsByArea":
                System.out.println(ass.getNumberOfDistinctCarsByArea());
                break;
            case "AddEmployee":
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                ass.AddEmployee(Integer.parseInt(args[1]), args[2], args[3], java.sql.Date.valueOf(args[4]), args[5], Integer.parseInt(args[6]), Integer.parseInt(args[7]), args[8]);
                break;
            default:
                break;
        }
    }



    public static void main(String[] args) {
        File file = new File(".");
        String csvFile = args[0];
        String line = "";
        String cvsSplitBy = ",";
        Assignment4 ass = new Assignment4();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] row = line.split(cvsSplitBy);

                executeFunc(ass, row);

            }

        } catch (IOException e) {
            e.printStackTrace();

        }
    }
    private Connection connectToDB(String connectionString){
        Connection conn = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection(connectionString);
            //System.out.println(String.format("Connection established to: '%s'",connectionString));
        } catch (Exception e) {
           // e.printStackTrace();
        }
        return conn;
    }
    private boolean useDB(Connection conn,String databaseName,String connectionString){
        try {
            Statement stmt = conn.createStatement();
            stmt.execute("use " + databaseName);
            stmt.close();
            //  System.out.println(String.format("Database selected: '%s'", databaseName));
            return true;
        } catch (Exception e) {
            //  System.out.println(String.format("Database '%s' not found at: %s", databaseName, connectionString));
            //  System.out.println(e.getMessage());
            return false;
        }
    }

    private void CloseDB(Connection conn,String connectionString){

        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                //  System.out.println(String.format("Database connection closed!", connectionString));
            }
        } catch (Exception e) {
            //   System.out.println(String.format("Error closing connection to: %s", connectionString));
            //   System.out.println(e.getMessage());
        }
    }
    private void loadNeighborhoodsFromCsv(String csvPath) {
        // connect to db
        Connection conn = connectToDB(connectionString);
        if(conn == null)
            return;
        // load db
        if(!useDB(conn,databaseName,connectionString)) {
            return;
        }
        // load the csv
        String line = "";
        String cvsSplitBy = ",";
        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {

            try {
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO dbo.Neighborhood(NID,Name) VALUES (?, ?)");
                while ((line = br.readLine()) != null) {

                    // use comma as separator

                    String[] row = line.split(cvsSplitBy);
                    stmt.setInt(1, Integer.valueOf(row[0]));
                    stmt.setString(2, row[1]);
                    try {
                        stmt.execute();
                    }catch(Exception e){
                        //    System.out.println("NID Already taken");
                    }

                }
            }catch(java.sql.SQLException e){
                //   System.out.println("error loading csv"+e.getMessage());
            }


        } catch (IOException e) {
            // System.out.println("Read File Error");
            // e.printStackTrace();
        }



        // close connection
        CloseDB(conn,connectionString);
    }

    private void updateEmployeeSalaries(double percentage) {
        // connect to db
        Connection conn = connectToDB(connectionString);
        if(conn == null)
            return;
        // load db
        if(!useDB(conn,databaseName,connectionString)) {
            return;
        }
        try {
            String query ="use DB2019_Ass2\n" +
                    "update ConstructorEmployee\n" +
                    "set SalaryPerDay = SalaryPerDay*?\n" +
                    "where EID in (select EID from ConstructionEmployeeOverFifty)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setDouble(1,percentage/100 + 1);
            stmt.execute();
        } catch (SQLException e) {
            //  e.printStackTrace();
        }
        //close db
        CloseDB(conn,connectionString);
    }


    public void updateAllProjectsBudget(double percentage) {
        // connect to db
        Connection conn = connectToDB(connectionString);
        if(conn == null)
            return;
        // load db
        if(!useDB(conn,databaseName,connectionString)) {
            return;
        }
        try {
            String query ="update Project\n" +
                    "set Budget = Budget*?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setDouble(1,percentage/100 + 1);
            stmt.execute();
        } catch (SQLException e) {
            //     e.printStackTrace();
        }
        //close db
        CloseDB(conn,connectionString);
    }


    private double getEmployeeTotalSalary() {
        // connect to db
        Connection conn = connectToDB(connectionString);
        if(conn == null)
            return 0;
        // load db
        if(!useDB(conn,databaseName,connectionString)) {
            return 0;
        }
        try {
            PreparedStatement stmt = conn.prepareStatement("select sum(SalaryPerDay) as Salary\n" +
                    "from ConstructorEmployee");
            ResultSet set = stmt.executeQuery();
            set.next();
            return set.getDouble("Salary");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //close db
        CloseDB(conn,connectionString);
        return 0;
    }


    private int getTotalProjectBudget() {
        // connect to db
        Connection conn = connectToDB(connectionString);
        if(conn == null)
            return 0;
        // load db
        if(!useDB(conn,databaseName,connectionString)) {
            return 0;
        }
        try {
            PreparedStatement stmt = conn.prepareStatement("select sum(Budget) as totalBudget from Project");
            ResultSet set = stmt.executeQuery();
            set.next();
            return set.getInt("totalBudget");
        } catch (SQLException e) {
            //        e.printStackTrace();
        }
        //close db
        CloseDB(conn,connectionString);
        return 0;
    }
    private void dropDB() {
        // connect to db
        Connection conn = connectToDB(connectionString);
        if(conn == null)
            return;
        try {
            PreparedStatement stmt = conn.prepareStatement("USE master;\n" +
                    "ALTER DATABASE DB2019_Ass2 SET SINGLE_USER WITH ROLLBACK IMMEDIATE;\n" +
                    "DROP DATABASE DB2019_Ass2;");
            stmt.execute();
        }catch(Exception e) {
            // System.out.println("failed to drop");
            // System.out.println(e.getMessage());
        }

        CloseDB(conn,connectionString);
    }

    private void initDB(String csvPath) {
        // connect to db
        Connection conn = connectToDB(connectionString);
        try {
            String line;
            String[] args = {"sqlcmd","-i",csvPath};
            Process p = Runtime.getRuntime().exec (args);
        } catch (Exception err) {
            //  err.printStackTrace();
        }
    }
    private int calculateIncomeFromParking(int year) {
// connect to db
        Connection conn = connectToDB(connectionString);
        if(conn == null)
            return 0;
        // load db
        if(!useDB(conn,databaseName,connectionString)) {
            return 0;
        }
        try {
            PreparedStatement stmt = conn.prepareStatement("select sum(Cost) as cost\n" +
                    "from CarParking\n" +
                    "where year(EndTime) = ?");
            stmt.setInt(1,year);
            ResultSet set = stmt.executeQuery();
            set.next();
            return set.getInt("cost");
        } catch (SQLException e) {
            //  e.printStackTrace();
        }
        //close db
        CloseDB(conn,connectionString);
        return 0;
    }

    private ArrayList<Pair<Integer, Integer>> getMostProfitableParkingAreas() {
        // connect to db
        Connection conn = connectToDB(connectionString);
        if(conn == null)
            return null;
        // load db
        if(!useDB(conn,databaseName,connectionString)) {
            return null;
        }
        try {
            PreparedStatement stmt = conn.prepareStatement("select TOP 5 ParkingAreaID,sum(Cost) as 'Total Profit'\n" +
                    "from CarParking \n" +
                    "group by ParkingAreaID\n" +
                    "ORDER BY [Total Profit] desc");
            ResultSet set = stmt.executeQuery();
            ArrayList<Pair<Integer, Integer>> list = new  ArrayList<Pair<Integer, Integer>>();
            while(set.next()){
                list.add(new Pair<>(set.getInt("ParkingAreaID"),set.getInt("Total Profit")));
            }
            return list;
        } catch (SQLException e) {
            //e.printStackTrace();
        }
        //close db
        CloseDB(conn,connectionString);
        return null;
    }

    private ArrayList<Pair<Integer, Integer>> getNumberOfParkingByArea() {
        // connect to db
        Connection conn = connectToDB(connectionString);
        if(conn == null)
            return null;
        // load db
        if(!useDB(conn,databaseName,connectionString)) {
            return null;
        }
        try {
            PreparedStatement stmt = conn.prepareStatement("select ParkingAreaID,count(ParkingAreaID) as \"Parking Number\"\n" +
                    "from CarParking\n" +
                    "group by ParkingAreaID");
            ResultSet set = stmt.executeQuery();
            ArrayList<Pair<Integer, Integer>> list = new  ArrayList<Pair<Integer, Integer>>();
            while(set.next()){
                list.add(new Pair<>(set.getInt("ParkingAreaID"),set.getInt("Parking Number")));
            }
            return list;
        } catch (SQLException e) {
            //     e.printStackTrace();
        }
        //close db
        CloseDB(conn,connectionString);
        return null;
    }


    private ArrayList<Pair<Integer, Integer>> getNumberOfDistinctCarsByArea() {
        // connect to db
        Connection conn = connectToDB(connectionString);
        if(conn == null)
            return null;
        // load db
        if(!useDB(conn,databaseName,connectionString)) {
            return null;
        }
        try {
            PreparedStatement stmt = conn.prepareStatement("select  ParkingAreaID,count(distinct CarParking.CID) as \"Distinct Cars Number\"\n" +
                    "from CarParking\n" +
                    "group by ParkingAreaID");
            ResultSet set = stmt.executeQuery();
            ArrayList<Pair<Integer, Integer>> list = new  ArrayList<Pair<Integer, Integer>>();
            while(set.next()){
                list.add(new Pair<>(set.getInt("ParkingAreaID"),set.getInt("Distinct Cars Number")));
            }
            return list;
        } catch (SQLException e) {
            //   e.printStackTrace();
        }
        //close db
        CloseDB(conn,connectionString);
        return null;
    }


    private void AddEmployee(int EID, String LastName, String FirstName, Date BirthDate, String StreetName, int Number, int door, String City) {
        // connect to db
        Connection conn = connectToDB(connectionString);
        if(conn == null)
            return;
        // load db
        if(!useDB(conn,databaseName,connectionString)) {
            return;
        }
        try {
            String query ="insert into Employee values(?,?,?,?,?,?,?,?);";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1,EID);
            stmt.setString(2,LastName);
            stmt.setString(3,FirstName);
            stmt.setDate(4,BirthDate);
            stmt.setString(5,StreetName);
            stmt.setInt(6,Number);
            stmt.setInt(7,door);
            stmt.setString(8,City);
            stmt.execute();
        } catch (SQLException e) {
            //  e.printStackTrace();
        }
        //close db
        CloseDB(conn,connectionString);
    }
}
