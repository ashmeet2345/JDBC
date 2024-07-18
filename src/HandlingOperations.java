import javax.xml.crypto.Data;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class HandlingOperations {

    public static DataManager dataManager;

    public HandlingOperations(DataManager manager){
        dataManager=manager;
    }

    public void getDataFromDB(){

        String query="SELECT * FROM STUDENTS";
        try(Connection con=dataManager.getConnection();
            PreparedStatement ps=con.prepareStatement(query)){
            ResultSet rs=ps.executeQuery(query);
            while(rs.next()){
                String name=rs.getString("NAME");
                int age=rs.getInt("AGE");
                double marks=rs.getDouble("MARKS");
                System.out.println(name+" "+age+" "+marks);
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void setDataIntoDB(String name,int age,double marks){

        String query="INSERT INTO STUDENTS(NAME,AGE,MARKS) VALUES(?, ?, ?)";
        try(Connection con=dataManager.getConnection();
            PreparedStatement ps=con.prepareStatement(query)){
            ps.setString(1,name);
            ps.setInt(2,age);
            ps.setDouble(3,marks);
            int rs=ps.executeUpdate();
            if(rs > 0){
                System.out.println("Query ran successfully");
            } else {
                throw new SQLException();
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void updateData(String name,Integer age,Double marks,int id){
        StringBuilder query=new StringBuilder("UPDATE STUDENTS SET ");
        if(name!=null) query.append("NAME = ?,"); else query.append("");
        if(age!=null) query.append(" AGE = ?,"); else query.append("");
        if(marks!=null) query.append(" MARKS = ?");

        if(query.lastIndexOf(",") == query.length()-1)
            query.deleteCharAt(query.length()-1);

        query.append(" WHERE ID = ?");

        int i=1;
        try(Connection con=dataManager.getConnection();
            PreparedStatement ps=con.prepareStatement(query.toString())) {
            if(name!=null) ps.setString(i++,name);
            if(age!=null) ps.setInt(i++,age);
            if(marks!=null) ps.setDouble(i++,marks);
            ps.setInt(i,id);
            int rs=ps.executeUpdate();
            if(rs > 0){
                System.out.println("UPDATED SUCCESSFULLY");
            } else {
                throw new SQLException();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteFromDB(int id){
        StringBuilder query=new StringBuilder();
        query.append("DELETE FROM STUDENTS WHERE ID = ?");

        try(Connection con=dataManager.getConnection();
            PreparedStatement ps=con.prepareStatement(query.toString())) {
            ps.setInt(1,id);
            int rs=ps.executeUpdate();
            if(rs > 0){
                System.out.println("ROW DELETED SUCCESSFULLY");
            } else {
                throw new SQLException();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertMultipleRows(){

        StringBuilder query=new StringBuilder("INSERT INTO STUDENTS(NAME,AGE,MARKS) VALUES(?, ?, ?)");
        try(Connection con=dataManager.getConnection();
            PreparedStatement ps=con.prepareStatement(query.toString())) {
            while(true){
                Scanner sc=new Scanner(System.in);
                System.out.print("Enter Name: ");
                String name=sc.next();
                System.out.print("Enter Age: ");
                int age=sc.nextInt();
                System.out.print("Enter Marks: ");
                double marks=sc.nextDouble();
                ps.setString(1,name);
                ps.setInt(2,age);
                ps.setDouble(3,marks);
                System.out.print("More entries?(Y/N): ");
                String choice=sc.next();
                ps.addBatch();
                if(choice.toUpperCase().equals("N")){
                    break;
                }
            }

            int[] rs=ps.executeBatch();
            for(int i=0;i<rs.length;i++){
                if(rs[i] == 0)
                    throw new SQLException("Insert Operation failed");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertImages(){
        StringBuilder query=new StringBuilder("INSERT INTO IMAGE_DATA(IMAGE) VALUES(?)");
        String path="C:\\Users\\ACER\\Downloads\\Images\\Photo.jpg";
        try(Connection con=dataManager.getConnection();
            PreparedStatement ps=con.prepareStatement(query.toString())) {
            FileInputStream inputStream=new FileInputStream(path);
            byte[] imageData=new byte[inputStream.available()];
            inputStream.read(imageData);
            ps.setBytes(1,imageData);
            int rs=ps.executeUpdate();
            if(rs > 0){
                System.out.println("Image uploaded successfully");
            } else {
                System.out.println("Image upload failed");
            }

        } catch (SQLException | FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void readImages(){
        StringBuilder query=new StringBuilder("SELECT IMAGE FROM IMAGE_DATA WHERE ID = ?");
        String path="C:\\Users\\ACER\\Downloads\\Images\\";
        try(Connection con=dataManager.getConnection();
            PreparedStatement ps=con.prepareStatement(query.toString())) {
            ps.setInt(1,7);
            ResultSet rs=ps.executeQuery();
            if(rs.next()){
                byte[] imageBytes=rs.getBytes("IMAGE");
                OutputStream outputStream=new FileOutputStream(path+"extracted.jpg");
                outputStream.write(imageBytes);
            } else {
                System.out.println("Image cannot be retrieved");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void transactionHandling(){
        StringBuilder withdraw=new StringBuilder("UPDATE ACCOUNTS SET BALANCE = BALANCE - ? WHERE ACCOUNT_NUMBER = ?");
        StringBuilder deposit=new StringBuilder("UPDATE ACCOUNTS SET BALANCE = BALANCE + ? WHERE ACCOUNT_NUMBER = ?");

        try(Connection con=dataManager.getConnection();) {
            PreparedStatement withdrawps=con.prepareStatement(withdraw.toString());
            PreparedStatement depositps=con.prepareStatement(deposit.toString());
            withdrawps.setDouble(1,500);withdrawps.setString(2,"account123");
            depositps.setDouble(1,500);depositps.setString(2,"account4562");
            con.setAutoCommit(false);

            int rs1=withdrawps.executeUpdate();
            int rs2=depositps.executeUpdate();
            if(rs1 > 0 && rs2 > 0){
                con.commit();
                System.out.println("Transaction successfull");
            } else {
                con.rollback();
                System.out.println("Transaction Failed");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
