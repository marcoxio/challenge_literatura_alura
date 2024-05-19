package com.alura.challenges.service;

import com.alura.challenges.model.*;
import com.alura.challenges.repository.AuthorRepository;
import com.alura.challenges.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.DoubleSummaryStatistics;
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
    public List<BookEntity> getAllBooks() {
        return bookRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<AuthorEntity> getAllAuthors() {
        return authorRepository.findAll();
    }

    public List<Book> searchBooks(String query) {
        String url = UriComponentsBuilder.fromHttpUrl("https://gutendex.com/books")
                .queryParam("search", query)
                .toUriString();

        DataResponse response = restTemplate.getForObject(url, DataResponse.class);
        return response != null ? response.getResults() : List.of();
    }

    public List<Book> searchBooksByAuthor(String author) {
        String url = UriComponentsBuilder.fromHttpUrl("https://gutendex.com/books")
                .queryParam("search", author)
                .toUriString();

        DataResponse response = restTemplate.getForObject(url, DataResponse.class);
        if (response != null && response.getResults() != null) {
            return response.getResults().stream()
                    .filter(book -> book.getAuthors().stream()
                            .anyMatch(a -> a.getName().toLowerCase().contains(author)))
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    public BookEntity saveBook(Book book) {
        BookEntity bookEntity = new BookEntity();
        bookEntity.setId(book.getId());
        bookEntity.setTitle(book.getTitle());
        bookEntity.setDownloadCount(book.getDownloadCount());
        bookEntity.setLanguages(book.getLanguages());

        List<AuthorEntity> authorEntities = book.getAuthors().stream()
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
    public List<BookEntity> listTopBooks() {
        return bookRepository.findAll().stream()
                .sorted((b1, b2) -> Integer.compare(b2.getDownloadCount(), b1.getDownloadCount()))
                .limit(10)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookEntity> listBooksByLanguage(String language) {
        return bookRepository.findAll().stream()
                .filter(book -> book.getLanguages().contains(language))
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<AuthorEntity> listAuthorsAliveInYear(int year) {
        return authorRepository.findByBirthYearLessThanEqualAndDeathYearGreaterThanEqual(year, year);
    }


    public DoubleSummaryStatistics generateStatistics() {
        return bookRepository.findAll().stream()
                .mapToDouble(BookEntity::getDownloadCount)
                .summaryStatistics();
    }

    public List<BookEntity> searchBooksByAuthorInDatabase(String author) {
        return bookRepository.findByAuthorsNameContainingIgnoreCase(author);
    }

}
