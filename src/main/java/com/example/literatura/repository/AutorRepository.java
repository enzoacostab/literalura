package com.example.literatura.repository;

import com.example.literatura.model.Autor;
import com.example.literatura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Integer> {
    @Query("select a from Autor a where a.nombre=:nombre")
    Optional<Autor> buscarPorNombre(String nombre);
    @Query("select l from Autor a join a.libros l where l.titulo=:titulo")
    Optional<Libro> buscarPorTitulo(String titulo);
    @Query("select l from Autor a join a.libros l")
    List<Libro> findAllLibros();
    @Query("select a from Autor a where a.fechaDeNacimiento<:year and a.fechaDeFallecimiento>:year")
    List<Autor> findByYear(Integer year);
    @Query("select l from Autor a join a.libros l where array_to_string(l.idiomas, ',') = :idioma")
    List<Libro> findByIdioma(String idioma);
}
