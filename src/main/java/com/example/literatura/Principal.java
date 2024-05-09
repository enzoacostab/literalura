package com.example.literatura;

import com.example.literatura.model.*;
import com.example.literatura.repository.AutorRepository;
import com.example.literatura.service.ConsumoAPI;
import com.example.literatura.service.ConvierteDatos;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Principal {
    private Scanner scanner = new Scanner(System.in);
    private AutorRepository autorRepository;
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos convierteDatos = new ConvierteDatos();
    private final String BASE_URL = "https://gutendex.com/books/?search=";

    public Principal(AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
    }

    public void mostrarMenu() {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println("""
                    -------------------------------------------
                    Elija una opcion:
                    1 - buscar libro por titulo
                    2 - listar libros registrados
                    3 - listar autores registrados
                    4 - listar autores vivos en determinado año
                    5 - listar libros por idioma
                    0 - salir 
                    """);
            try {
                opcion = Integer.valueOf(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Solo se permiten digitos");
                continue;
            }
            switch (opcion) {
                case 1:
                    buscarLibro();
                    break;
                case 2:
                    listarLibros();
                    break;
                case 3:
                    listarAutores();
                    break;
                case 4:
                    listarAutoresPorFecha();
                    break;
                case 5:
                    listarLibrosPorIdioma();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opcion invalida");
                    break;
            }
        }
    }

    public void buscarLibro() {
        System.out.println("Ingrese el nombre del libro: ");
        String busqueda = scanner.nextLine();
        String json = consumoAPI.obtenerDatos(BASE_URL + busqueda.replace(" ", "+"));
        Results results = convierteDatos.obtenerDatos(json, Results.class);
        if (results.libros().isEmpty()) {
            System.out.println("No se encontro el libro");
        } else {
            DatosLibro datosLibro = results.libros().getFirst();
            Optional<Libro> libroBuscado = autorRepository.buscarPorTitulo(datosLibro.titulo());
            if (datosLibro.autores() != null && libroBuscado.isEmpty()) {
                guardarLibro(datosLibro);
            } else {
                System.out.println("Libro ya registrado");
            }
        }
    }

    public void guardarLibro(DatosLibro datosLibro) {
        DatosAutor datosAutor = datosLibro.autores().getFirst();
        Libro libro = new Libro(datosLibro);
        Optional<Autor> autorBuscado = autorRepository.buscarPorNombre(datosAutor.nombre());
        Autor autor;
        if (autorBuscado.isPresent()) {
            autor = autorBuscado.get();
            autor.addLibro(libro);
        } else {
            autor = new Autor(datosAutor);
            autor.setLibros(List.of(libro));
        }
        System.out.println(libro);
        autorRepository.save(autor);
    }

    public void listarLibros() {
        List<Libro> libros = autorRepository.findAllLibros();
        if (libros.isEmpty()) {
            System.out.println("No se encontraron resultados");
        } else {
            libros.forEach(System.out::println);
        }
    }

    public void listarAutores() {
        List<Autor> autores = autorRepository.findAll();
        if (autores.isEmpty()) {
            System.out.println("No se encontraron resultados");
        } else {
            autores.forEach(System.out::println);
        }
    }

    public void listarAutoresPorFecha() {
        Integer year = null;
        while (year == null) {
            System.out.println("Ingrese el año:");
            try {
                year = Integer.valueOf(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Solo se permiten digitos");
            }
        }
        List<Autor> autores = autorRepository.findByYear(year);
        if (autores.isEmpty()) {
            System.out.println("No se encontraron resultados");
        } else {
            autores.forEach(System.out::println);
        }

    }

    public void listarLibrosPorIdioma() {
        List<String> idiomas = List.of("es", "fr", "en", "pt");
        System.out.println("""
        Elija el idioma:
        es - español
        fr - frances
        en - ingles
        pt - portugués
        """);
        String idioma = "";
        while (!idiomas.contains(idioma)) {
            idioma = scanner.nextLine();
            if (!idiomas.contains(idioma)) {
                System.out.println("Ingrese una opcion valida:");
            }
        }
            List<Libro> libros = autorRepository.findByIdioma(idioma);
        if (libros.isEmpty()) {
            System.out.println("No se encontraron resultados");
        } else {
            libros.forEach(System.out::println);
        }
    }
}
