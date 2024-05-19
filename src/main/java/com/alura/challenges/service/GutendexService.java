package com.alura.challenges.service;

import com.alura.challenges.model.*;
import com.alura.challenges.repository.AuthorRepository;
import com.alura.challenges.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GutendexService {

    private final RestTemplate restTemplate;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    @Autowired
    public GutendexService(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
        this.restTemplate = new RestTemplate();
        this.bookRepository = bookRepository;
    }


    @Transactional(readOnly = true)
    public List<BookEntity> obtainAllBooks() {
        return bookRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<AuthorEntity> obtainAllAuthors() {
        return authorRepository.findAll();
    }

    public List<Book> buscarLibros(String query) {
        String url = UriComponentsBuilder.fromHttpUrl("https://gutendex.com/books")
                .queryParam("search", query)
                .toUriString();

        DataResponse response = restTemplate.getForObject(url, DataResponse.class);
        return response != null ? response.getResults() : List.of();
    }

    public List<Book> buscarLibrosPorAutor(String autor) {
        String url = UriComponentsBuilder.fromHttpUrl("https://gutendex.com/books")
                .queryParam("search", autor)
                .toUriString();

        DataResponse response = restTemplate.getForObject(url, DataResponse.class);
        if (response != null && response.getResults() != null) {
            return response.getResults().stream()
                    .filter(book -> book.getAuthors().stream()
                            .anyMatch(a -> a.getName().toLowerCase().contains(autor)))
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    public BookEntity guardarLibro(Book libro) {
        BookEntity bookEntity = new BookEntity();
        bookEntity.setId(libro.getId());
        bookEntity.setTitle(libro.getTitle());
        bookEntity.setDownloadCount(libro.getDownloadCount());
        bookEntity.setLanguages(libro.getLanguages());

        List<AuthorEntity> authorEntities = libro.getAuthors().stream()
                .map(author -> {
                    AuthorEntity authorEntity = new AuthorEntity();
                    authorEntity.setName(author.getName());
                    authorEntity.setBirthYear(author.getBirth_year());
                    authorEntity.setDeathYear(author.getDeath_year());
                    authorEntity.setBook(bookEntity);
                    return authorEntity;
                }).collect(Collectors.toList());

        bookEntity.setAuthors(authorEntities);

        return bookRepository.save(bookEntity);
    }

    @Transactional(readOnly = true)
    public List<BookEntity> listarTopLibros() {
        return bookRepository.findAll().stream()
                .sorted((b1, b2) -> Integer.compare(b2.getDownloadCount(), b1.getDownloadCount()))
                .limit(10)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookEntity> listarLibrosPorIdioma(String idioma) {
        return bookRepository.findAll().stream()
                .filter(book -> book.getLanguages().contains(idioma))
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<AuthorEntity> listarAutoresVivosEnAno(int year) {
        return authorRepository.findByBirthYearLessThanEqualAndDeathYearGreaterThanEqual(year, year);
    }


}
