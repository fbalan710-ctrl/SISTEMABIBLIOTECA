import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class SistemaBiblioteca {

    public static class LibroNoDisponibleException extends Exception {
        public LibroNoDisponibleException(String mensaje) {
            super(mensaje);
        }
    }

    public static class LibroNoEncontradoException extends Exception {
        public LibroNoEncontradoException(String mensaje) {
            super(mensaje);
        }
    }

    static class Libro {
        private String isbn;
        private String titulo;
        private String autor;
        private int copiasDisponibles;

        public Libro(String isbn, String titulo, String autor, int copiasDisponibles) {
            this.isbn = isbn;
            this.titulo = titulo;
            this.autor = autor;
            this.copiasDisponibles = copiasDisponibles;
        }

        public String getIsbn() { return isbn; }
        public String getTitulo() { return titulo; }
        public int getCopiasDisponibles() { return copiasDisponibles; }

        public void prestar() {
            if (copiasDisponibles > 0) {
                copiasDisponibles--;
            }
        }

        public void devolver() {
            copiasDisponibles++;
        }

        @Override
        public String toString() {
            return String.format("ISBN: %-13s | Título: %-20s | Autor: %-15s | Copias: %d",
                    isbn, titulo.length() > 20 ? titulo.substring(0,17)+"..." : titulo, autor, copiasDisponibles);
        }
    }

    static class Usuario {
        private String claveUsuario;
        private String nombre;
        private String licenciatura;
        private int semestre;

        public Usuario(String claveUsuario, String nombre, String licenciatura, int semestre) {
            this.claveUsuario = claveUsuario;
            this.nombre = nombre;
            this.licenciatura = licenciatura;
            this.semestre = semestre;
        }

        public String getNombre() { return nombre; }
        public String getClaveUsuario() { return claveUsuario; }

        @Override
        public String toString() {
            return String.format("Clave: %-10s | Nombre: %-20s | Lic: %-15s | Semestre: %d",
                    claveUsuario, nombre, licenciatura, semestre);
        }
    }

    static class Prestamo {
        private Usuario usuario;
        private Libro libro;
        private Date fechaPrestamo;

        public Prestamo(Usuario usuario, Libro libro, Date fechaPrestamo) {
            this.usuario = usuario;
            this.libro = libro;
            this.fechaPrestamo = fechaPrestamo;
        }

        @Override
        public String toString() {
            return "Usuario: " + usuario.getNombre() + " | Libro: " + libro.getTitulo() + " | Fecha: " + fechaPrestamo;
        }
    }

    static class BibliotecaManager {
        private List<Libro> inventarioLibros = new ArrayList<>();
        private List<Usuario> listaUsuarios = new ArrayList<>();
        private List<Prestamo> listaPrestamos = new ArrayList<>();

        public void registrarLibro(String isbn, String titulo, String autor, int copias) {
            if (isbn.isEmpty() || titulo.isEmpty() || autor.isEmpty()) {
                throw new IllegalArgumentException("Error: Ningún campo del libro puede estar vacío.");
            }
            if (copias < 0) {
                throw new IllegalArgumentException("Error: El número de copias no puede ser negativo.");
            }
            inventarioLibros.add(new Libro(isbn, titulo, autor, copias));
            System.out.println("Libro registrado con éxito.");
        }

        public void registrarUsuario(String clave, String nombre, String lic, int sem) {
            if (sem < 1 || sem > 12) {
                throw new IllegalArgumentException("Error: El semestre debe estar entre 1 y 12.");
            }
            listaUsuarios.add(new Usuario(clave, nombre, lic, sem));
            System.out.println("Usuario registrado con éxito.");
        }

        public Libro buscarLibro(String isbn) throws LibroNoEncontradoException {
            for (Libro l : inventarioLibros) {
                if (l.getIsbn().equals(isbn)) {
                    return l;
                }
            }
            throw new LibroNoEncontradoException("El libro con ISBN " + isbn + " no existe en el sistema.");
        }

        public Usuario buscarUsuario(String nombre) {
            for (Usuario u : listaUsuarios) {
                if (u.getNombre().equalsIgnoreCase(nombre)) {
                    return u;
                }
            }
            return null;
        }

        public void realizarPrestamo(String nombreUsuario, String isbnLibro)
                throws LibroNoEncontradoException, LibroNoDisponibleException {

            Usuario usuario = buscarUsuario(nombreUsuario);

            if (usuario == null) {
                throw new NullPointerException("Intento de acceso a usuario no existente: " + nombreUsuario);
            }

            Libro libro = buscarLibro(isbnLibro);

            assert libro != null : "Error Crítico: La búsqueda de libro retornó null pero no lanzó excepción.";

            if (libro.getCopiasDisponibles() <= 0) {
                throw new LibroNoDisponibleException("Lo sentimos, no quedan copias disponibles de: " + libro.getTitulo());
            }

            libro.prestar();

            assert libro.getCopiasDisponibles() >= 0 : "Error de Lógica: El inventario de libros es negativo tras el préstamo.";

            listaPrestamos.add(new Prestamo(usuario, libro, new Date()));
            System.out.println("Préstamo realizado exitosamente a " + usuario.getNombre());
        }

        public void mostrarReporte() {
            System.out.println("\n--- REPORTE DE INVENTARIO ---");
            if (inventarioLibros.isEmpty()) System.out.println("No hay libros registrados.");
            for (Libro l : inventarioLibros) {
                System.out.println(l);
            }

            System.out.println("\n--- REPORTE DE PRÉSTAMOS ACTIVOS ---");
            if (listaPrestamos.isEmpty()) System.out.println("No hay préstamos activos.");
            for (Prestamo p : listaPrestamos) {
                System.out.println(p);
            }
            System.out.println("------------------------------------");
        }
    }

    public static void main(String[] args) {
        BibliotecaManager manager = new BibliotecaManager();
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;

        System.out.println("=============================================");
        System.out.println("   SISTEMA DE BIBLIOTECA - MODO DEPURACIÓN   ");
        System.out.println("=============================================");

        while (!salir) {
            System.out.println("\nMENÚ PRINCIPAL:");
            System.out.println("1. Registrar Libro");
            System.out.println("2. Registrar Usuario");
            System.out.println("3. Realizar Préstamo");
            System.out.println("4. Ver Reporte");
            System.out.println("5. Salir");
            System.out.print("Seleccione una opción: ");

            try {
                String entrada = scanner.nextLine();

                int opcion = Integer.parseInt(entrada);

                switch (opcion) {
                    case 1:
                        try {
                            System.out.print("ISBN: ");
                            String isbn = scanner.nextLine();
                            System.out.print("Título: ");
                            String titulo = scanner.nextLine();
                            System.out.print("Autor: ");
                            String autor = scanner.nextLine();
                            System.out.print("Número de copias: ");
                            int copias = Integer.parseInt(scanner.nextLine());

                            manager.registrarLibro(isbn, titulo, autor, copias);

                        } catch (NumberFormatException e) {
                            System.err.println("Error de Formato: El número de copias debe ser un valor numérico entero.");
                        } catch (IllegalArgumentException e) {
                            System.err.println("Error de Argumento: " + e.getMessage());
                        }
                        break;

                    case 2:
                        try {
                            System.out.print("Clave Usuario: ");
                            String clave = scanner.nextLine();
                            System.out.print("Nombre: ");
                            String nombre = scanner.nextLine();
                            System.out.print("Licenciatura: ");
                            String lic = scanner.nextLine();
                            System.out.print("Semestre (número): ");
                            int sem = Integer.parseInt(scanner.nextLine());

                            manager.registrarUsuario(clave, nombre, lic, sem);

                        } catch (NumberFormatException e) {
                            System.err.println("Error de Formato: El semestre debe ser un número.");
                        } catch (IllegalArgumentException e) {
                            System.err.println("Error de Datos: " + e.getMessage());
                        }
                        break;

                    case 3:
                        System.out.print("Nombre exacto del Usuario: ");
                        String usuarioPrestamo = scanner.nextLine();
                        System.out.print("ISBN del Libro: ");
                        String isbnPrestamo = scanner.nextLine();

                        try {
                            manager.realizarPrestamo(usuarioPrestamo, isbnPrestamo);
                        } catch (LibroNoEncontradoException e) {
                            System.err.println("Error de Búsqueda: " + e.getMessage());
                        } catch (LibroNoDisponibleException e) {
                            System.err.println("Alerta de Stock: " + e.getMessage());
                        } catch (NullPointerException e) {
                            System.err.println("Error Crítico (NPE Controlado): El usuario ingresado no existe o no está inicializado.");
                            System.err.println("   Detalle técnico: " + e.getMessage());
                        }
                        break;

                    case 4:
                        manager.mostrarReporte();
                        break;

                    case 5:
                        salir = true;
                        break;

                    default:
                        System.out.println("Opción no válida. Intente de nuevo.");
                }

            } catch (NumberFormatException e) {
                System.err.println("Error: Debe ingresar un número para seleccionar la opción del menú.");
            } catch (Exception e) {
                System.err.println("Error inesperado del sistema: " + e.toString());
                e.printStackTrace();
            } finally {
                System.out.println("[Sistema]: Operación procesada.");
            }
        }

        scanner.close();
        System.out.println("Saliendo del sistema...");
    }
}