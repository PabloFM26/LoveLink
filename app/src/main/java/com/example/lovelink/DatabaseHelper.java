package com.example.lovelink;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, "lovelink_db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE LOVELINK_USERS (" +
                "codigo INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT, " +
                "apellidos TEXT, " +
                "correo TEXT, " +
                "telefono TEXT, " +
                "contrasena TEXT, " +
                "genero TEXT, " +
                "localidad TEXT, " +
                "edad INTEGER, " +
                "orientacion_sexual TEXT, " +
                "signo_zodiaco TEXT, " +
                "imagenes TEXT, " +
                "biografia TEXT, " +
                "hobbies TEXT, " +
                "empleo TEXT, " +
                "que_se_busca TEXT, " +
                "altura INTEGER)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS LOVELINK_USERS");
        onCreate(db);
    }
    public static Connection getConnection() throws SQLException {
        // Asegúrate de tener los siguientes parámetros para tu conexión a Oracle
        String url = "jdbc:oracle:thin:@<hostname>:<port>:<sid>";
        String user = "<lovelink>";
        String password = "<lovelink>";

        try {
            // Cargar el driver de Oracle JDBC
            Class.forName("oracle.jdbc.driver.OracleDriver");
            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException("Error de conexión con la base de datos", e);
        }
    }
}
