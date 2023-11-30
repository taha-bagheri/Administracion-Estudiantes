package com.tuempresa;

import java.sql.*;
import java.util.Scanner;

public class AdministracionEstudiantes {

	// Configuración de la conexión a la base de datos
	private static final String URL = "jdbc:mysql://localhost:3306/AdministracionEstudiantes";
	private static final String USUARIO = "root";
	private static final String CONTRASENA = "";

	public static void main(String[] args) {
		try {
			// Establecer la conexión a la base de datos
			Connection conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);

			// Crear la tabla 'estudiantes' si no existe
			crearTablaEstudiantes(conexion);

			// Configurar la entrada del usuario
			Scanner scanner = new Scanner(System.in);

			// Menú principal
			while (true) {
				System.out.println("\n1. Agregar estudiante");
				System.out.println("2. Mostrar estudiantes");
				System.out.println("3. Actualizar estudiante");
				System.out.println("4. Eliminar estudiante");
				System.out.println("5. Salir");
				System.out.print("Selecciona una opción: ");

				int opcion = scanner.nextInt();
				scanner.nextLine(); // Limpiar el buffer del scanner

				// Manejar la opción seleccionada
				switch (opcion) {
				case 1:
					agregarEstudiante(conexion, scanner);
					break;
				case 2:
					mostrarEstudiantes(conexion);
					break;
				case 3:
					actualizarEstudiante(conexion, scanner);
					break;
				case 4:
					eliminarEstudiante(conexion, scanner);
					break;
				case 5:
					System.out.println("Saliendo...");
					System.exit(0);
					break;
				default:
					System.out.println("Opción no válida. Intenta de nuevo.");
				}
			}
		} catch (SQLException e) {
			// Manejar excepciones de SQL (por ejemplo, problemas de conexión)
			e.printStackTrace();
		}
	}

	private static void crearTablaEstudiantes(Connection conexion) throws SQLException {
		// Método para crear la tabla 'estudiantes' si no existe
		String sql = "CREATE TABLE IF NOT EXISTS estudiantes (id INT AUTO_INCREMENT PRIMARY KEY, nombre VARCHAR(255), apellido VARCHAR(255), edad INT, curso VARCHAR(255))";
		try (Statement statement = conexion.createStatement()) {
			statement.execute(sql);
		}
	}

	private static void agregarEstudiante(Connection conexion, Scanner scanner) throws SQLException {
	    // Método para agregar un estudiante a la base de datos
	    System.out.print("Nombre del estudiante: ");
	    String nombre = scanner.nextLine();
	    System.out.print("Apellido del estudiante: ");
	    String apellido = scanner.nextLine();

	    // Validar que la entrada para la edad sea un número
	    int edad = 0;
	    boolean inputValido = false;
	    while (!inputValido) {
	        System.out.print("Edad del estudiante: ");
	        String inputEdad = scanner.nextLine();
	        try {
	            edad = Integer.parseInt(inputEdad);
	            inputValido = true;
	        } catch (NumberFormatException e) {
	            System.out.println("Error: Debes escribir un número para la edad.");
	        }
	    }

	    // Continuar con la entrada del curso
	    System.out.print("Curso del estudiante: ");
	    String curso = scanner.nextLine();

	    String sql = "INSERT INTO estudiantes (nombre, apellido, edad, curso) VALUES (?, ?, ?, ?)";
	    try (PreparedStatement preparedStatement = conexion.prepareStatement(sql)) {
	        preparedStatement.setString(1, nombre);
	        preparedStatement.setString(2, apellido);
	        preparedStatement.setInt(3, edad);
	        preparedStatement.setString(4, curso);

	        preparedStatement.executeUpdate();
	        System.out.println("Estudiante agregado con éxito.");
	    }
	}

	private static void mostrarEstudiantes(Connection conexion) throws SQLException {
		// Método para mostrar todos los estudiantes en la base de datos
		String sql = "SELECT * FROM estudiantes";
		try (Statement statement = conexion.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
			System.out.println("\nLista de estudiantes:");
			System.out.printf("%-5s %-15s %-15s %-5s %-15s\n", "ID", "Nombre", "Apellido", "Edad", "Curso");

			// Verifica si hay estudiantes en el resultado
			if (!resultSet.isBeforeFirst()) {
				System.out.println("No hay estudiantes registrados.");
			} else {
				while (resultSet.next()) {
					System.out.printf("%-5s %-15s %-15s %-5s %-15s\n", resultSet.getInt("id"),
							resultSet.getString("nombre"), resultSet.getString("apellido"), resultSet.getInt("edad"),
							resultSet.getString("curso"));
				}
			}
		}
	}

	private static void actualizarEstudiante(Connection conexion, Scanner scanner) throws SQLException {
		// Método para actualizar la información de un estudiante
		System.out.print("ID del estudiante a actualizar: ");
		int id = scanner.nextInt();
		scanner.nextLine(); // Limpiar el buffer del scanner

		System.out.print("Nuevo nombre: ");
		String nuevoNombre = scanner.nextLine();
		System.out.print("Nuevo apellido: ");
		String nuevoApellido = scanner.nextLine();
		System.out.print("Nueva edad: ");
		int nuevaEdad = scanner.nextInt();
		scanner.nextLine(); // Limpiar el buffer del scanner
		System.out.print("Nuevo curso: ");
		String nuevoCurso = scanner.nextLine();

		String sql = "UPDATE estudiantes SET nombre=?, apellido=?, edad=?, curso=? WHERE id=?";
		try (PreparedStatement preparedStatement = conexion.prepareStatement(sql)) {
			preparedStatement.setString(1, nuevoNombre);
			preparedStatement.setString(2, nuevoApellido);
			preparedStatement.setInt(3, nuevaEdad);
			preparedStatement.setString(4, nuevoCurso);
			preparedStatement.setInt(5, id);

			int filasActualizadas = preparedStatement.executeUpdate();
			if (filasActualizadas > 0) {
				System.out.println("Estudiante actualizado con éxito.");
			} else {
				System.out.println("No se encontró el estudiante con ID " + id);
			}
		}
	}

	private static void eliminarEstudiante(Connection conexion, Scanner scanner) throws SQLException {
		// Método para eliminar un estudiante de la base de datos
		System.out.print("ID del estudiante a eliminar: ");
		int id = scanner.nextInt();

		String sql = "DELETE FROM estudiantes WHERE id=?";
		try (PreparedStatement preparedStatement = conexion.prepareStatement(sql)) {
			preparedStatement.setInt(1, id);

			int filasEliminadas = preparedStatement.executeUpdate();
			if (filasEliminadas > 0) {
				System.out.println("Estudiante eliminado con éxito.");
			} else {
				System.out.println("No se encontró el estudiante con ID " + id);
			}
		}
	}
}
