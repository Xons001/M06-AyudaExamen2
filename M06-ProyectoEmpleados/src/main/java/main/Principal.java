package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.Scanner;

import com.mysql.cj.jdbc.CallableStatement;

public class Principal {

	public static int leerInteger() {
		Scanner lector = new Scanner(System.in);
		int numero = lector.nextInt();
		return numero;
	}

	public static float leerFloat() {
		Scanner lector = new Scanner(System.in);
		Float numero = lector.nextFloat();
		return numero;
	}
	public static String leerString() {
		Scanner lector = new Scanner(System.in);
		String texto= lector.nextLine();
		return texto;
	}
	public static void main(String[] args) {
		conexionMySQL();
	}

	static String usuari = "root";
	static String contr = "";
	static String url = "jdbc:mysql://localhost:3306/empleados?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

	private static void conexionMySQL() {

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("Hola");
			System.out.println("Driver cargado correctamente");
			Connection conn = DriverManager.getConnection(url, usuari, contr);
			System.out.println("Conexion creada correctamente");
			System.out.println("--------------------------------------------------");
			System.out.println("EJERCICIO 1");
			insertDepartamento(conn);

			System.out.println("EJERCICIO 2");
			//storeProcedureUpdate();

			System.out.println("EJERCICIO 3");
			consultaEmpleadosXDepartamento();
		} catch (Exception e) {
			System.out.println("No se ha podido conectar a la base de datos");		
		}
	}

	//Ejercicio 1
	public static void insertDepartamento(Connection conn) {
		PreparedStatement preparedStatement,preparedStatement2;

		try {
			// Insertar departamento
			preparedStatement = conn.prepareStatement("insert into departamentos values (?, ?, ?)");

			preparedStatement.setInt(1, 50);
			preparedStatement.setString(2, "DEVOPS");
			preparedStatement.setString(3, "VALENCIA");

			preparedStatement.executeUpdate();
			System.out.println("Departamento DEVOPS insertado");

			// Insertar empleado
			preparedStatement2 = conn.prepareStatement("insert into empleados values (?, ?, ?, ?, ?, ?, ?, ?)");

			preparedStatement2.setInt(1, 7450);
			preparedStatement2.setString(2, "LOPEZ");
			preparedStatement2.setString(3, "EMPLEADO");
			preparedStatement2.setInt(4, 7450);
			preparedStatement2.setDate(5, java.sql.Date.valueOf("2020-01-20"));
			preparedStatement2.setFloat(6, 1500);
			preparedStatement2.setFloat(7, 0);
			preparedStatement2.setInt(8, 50);

			preparedStatement2.executeUpdate();
			System.out.println("Empleado Lopez insertado");

			System.out.println("--------------------------------------------------");
		} catch (SQLIntegrityConstraintViolationException e) {
			System.out.println("Los datos ya estan insertados");
		} catch (SQLException e) {
			System.out.println("No encuentra las bases de datos");
		} 
	}

	public static void storeProcedureUpdate() {
		CallableStatement cs;

		try (Connection conn = DriverManager.getConnection(url, usuari, contr);
				Statement stmt = conn.createStatement()) {

			cs = (CallableStatement) conn.prepareCall("{ call updateComisionEmpleados(?) }");

			System.out.println("Que comision quieres que tengan los vendedores");
			float comisionDato = leerFloat();
			cs.setFloat(1, comisionDato);
			cs.executeUpdate();

			System.out.println("Comision de los vendedores cambiada");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public static void consultaEmpleadosXDepartamento() {
		CallableStatement cs;
		ResultSet rs;

		try (Connection conn = DriverManager.getConnection(url, usuari, contr);
				Statement stmt = conn.createStatement()) {

			cs = (CallableStatement) conn.prepareCall("{ call consultaEmpleadosDepartamento(?) }");

			System.out.println("Que departamento quieres ver?");
			String nomDep = leerString();
			cs.setString(1, nomDep);
			cs.executeUpdate();

			rs = cs.executeQuery();
			while(rs.next()) {
				System.out.println("==================================");
				System.out.println("ID-Emp"+"->"+rs.getInt(1));
				System.out.println("Apellido"+"->"+rs.getString(2));
				System.out.println("Oficio"+"->"+rs.getString(3));
				System.out.println("Dir"+"->"+rs.getInt(4));
				System.out.println("Fecha Alta"+"->"+rs.getDate(5));       
				System.out.println("Salario"+"->"+rs.getFloat(6));
				System.out.println("Comision"+"->"+rs.getFloat(7));
				System.out.println("dept-no"+"->"+rs.getInt(8));   
				System.out.println("==================================");
			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
}
