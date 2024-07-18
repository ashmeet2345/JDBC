import com.mysql.cj.protocol.Resultset;

import javax.xml.crypto.Data;
import java.sql.*;
import java.util.Scanner;

public class Main {
    public static DataManager dataManager;
    public static HandlingOperations handlingOperations;

    public static void main(String[] args) {
        dataManager=new DataManager();
        handlingOperations=new HandlingOperations(dataManager);

       // handlingOperations.getDataFromDB();
       // handlingOperations.setDataIntoDB("Ankita",24,92.2);
       // handlingOperations.updateData(null,28,null,4);
       // handlingOperations.deleteFromDB(2);
       // handlingOperations.insertMultipleRows();
       // handlingOperations.insertImages();
       // handlingOperations.readImages();

        handlingOperations.transactionHandling();
    }
}
