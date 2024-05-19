package com.alura.challenges;

import com.alura.challenges.model.AuthorEntity;
import com.alura.challenges.model.Book;
import com.alura.challenges.model.BookEntity;
import com.alura.challenges.service.GutendexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;


@SpringBootApplication
public class ChallengeLiteratureApplication implements CommandLineRunner {
	@Autowired
	private GutendexService gutendexService;

	public static void main(String[] args) {
		SpringApplication.run(ChallengeLiteratureApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Scanner keyboard = new Scanner(System.in);
		displayMenu(keyboard);

	}

	public void displayMenu(Scanner keyboard) {

		var option = -1;
		while (option != 0) {
			var menu = """
                    1 - Search book by title
                    2 - Search registered books
                    3 - List registered authors
                    4 - List authors alive in a certain year
                    5 - List books by language
                    6 - Statistics generator
                    7 - Top 10 most downloaded books
                    8 - Search author by name

                    0 - Exit
                    """;
			System.out.println(menu);
			option = keyboard.nextInt();
			keyboard.nextLine();

			switch (option) {
				case 1:
					bookByTitle(keyboard);
					break;
				case 2:
					obtainAllBooks();
					break;
				case 3:
					obtainAllAuthors();
					break;
				case 4:
					listAuthorsAlive(keyboard);
					break;
				case 5:
					listBooksByLanguage(keyboard);
					break;
				case 6:
					workWithStatistics();
					break;
				case 7:
					listMyTop();
					break;
				case 8:
					searchAuthor(keyboard);
					break;
				case 0:
					System.out.println("Closing the application...");
					break;
				default:
					System.out.println("Invalid option");
			}
		}
	}

	private void bookByTitle(Scanner keyboard) {
		String query = getBookTitleFromUser(keyboard);
		List<Book> books = gutendexService.searchBooks(query);

		if (books.isEmpty()) {
			System.out.println("No books were found with the provided search title.");
			return;
		}

		displayBooks(books);

		System.out.println("Enter the ID of the book you want to save:");
		int bookId = keyboard.nextInt();
		keyboard.nextLine(); // consume newline

		Book selectedBook = books.stream()
				.filter(book -> book.getId() == bookId)
				.findFirst()
				.orElse(null);

		saveBook(selectedBook);
	}

	private String getBookTitleFromUser(Scanner keyboard) {
		System.out.println("Enter the name of the book you want to search for");
		return keyboard.nextLine().toLowerCase();
	}

	private void displayBooks(List<Book> books) {
		System.out.println("Books found:");
		for (Book book : books) {
			displayBookDetails(book);
		}
	}

	private void displayBookDetails(Book book) {
		System.out.println("----- BOOK -----");
		System.out.println("  ID: " + book.getId());
		System.out.println("  Title: " + book.getTitle());
		if (!book.getAuthors().isEmpty()) {
			System.out.println("  Author: " + book.getAuthors().get(0).getName());
		}
		if (!book.getLanguages().isEmpty()) {
			System.out.println("  Language: " + book.getLanguages().get(0));
		}
		System.out.println("  Number of downloads: " + book.getDownloadCount());
	}

	private Book selectFirstBook(List<Book> books) {
		return books.stream().findFirst().orElse(null);
	}

	private void saveBook(Book book) {
		if (book != null) {
			gutendexService.saveBook(book);
			System.out.println("Book saved: " + book.getTitle());
		} else {
			System.out.println("Book not saved");
		}
	}

	private void obtainAllBooks() {
		List<BookEntity> myBooks = gutendexService.getAllBooks();
		if (myBooks.isEmpty()) {
			System.out.println("There are no books in your collection.");
			return;
		}
		displayAllBooks(myBooks);
	}

	private void displayAllBooks(List<BookEntity> books) {
		for (BookEntity book : books) {
			displayBookDetails(book);
		}
	}


	private void displayBookDetails(BookEntity book) {
		System.out.println("----- BOOK -----");
		System.out.println("  ID: " + book.getId());
		System.out.println("  Title: " + book.getTitle());
		if (!book.getAuthors().isEmpty()) {
			System.out.println("  Author: " + book.getAuthors().stream()
					.map(AuthorEntity::getName)
					.collect(Collectors.joining(", ")));
		}
		if (!book.getLanguages().isEmpty()) {
			System.out.println("  Language: " + String.join(", ", book.getLanguages()));
		}
		System.out.println("  Number of downloads: " + book.getDownloadCount());
		System.out.println("-----------------");
		System.out.println();
	}

	private void obtainAllAuthors() {
		List<AuthorEntity> myAuthors = gutendexService.getAllAuthors();
		if (myAuthors.isEmpty()) {
			System.out.println("There are no authors in your collection.");
			return;
		}
		System.out.println();
		for (AuthorEntity author : myAuthors) {
			displayAuthorDetails(author);
		}
	}

	private void listAuthorsAlive(Scanner keyboard) {
		System.out.println("****************************");
		System.out.println("Enter the year:");
		while (!keyboard.hasNextInt()) {
			System.out.println("Invalid entry. Please enter a valid year.");
			keyboard.next(); // consume the invalid input
		}
		int year = keyboard.nextInt();
		keyboard.nextLine(); // consume newline

		List<AuthorEntity> authorsAlive = gutendexService.listAuthorsAliveInYear(year);
		if (authorsAlive.isEmpty()) {
			System.out.println("There are no authors alive in the specified year.");
			return;
		}

		System.out.println("****************************");
		System.out.println("Authors alive in the year " + year + ":");
		for (AuthorEntity author : authorsAlive) {
			displayAuthorDetails(author);
		}
	}

	private void displayAuthorDetails(AuthorEntity author) {
		System.out.println("Author: " + author.getName());
		System.out.println("Year of birth: " + author.getBirthYear());
		System.out.println("Year of death: " + author.getDeathYear());
		if (author.getBook() != null) {
			System.out.println("Book: " + author.getBook().getTitle());
			System.out.println();
		}
	}

	private void listBooksByLanguage(Scanner keyboard) {
		System.out.println("****************************");
		System.out.println("Enter languages separated by commas (e.g., en,es,fr,pt for English, Spanish, French, Portuguese):");
		String[] languages = keyboard.nextLine().toLowerCase().split(",");
		for (String language : languages) {
			List<BookEntity> booksByLanguage = gutendexService.listBooksByLanguage(language.trim());
			if (booksByLanguage.isEmpty()) {
				System.out.println("There are no books in the language: " + language);
				continue;
			}

			System.out.println("****************************");
			System.out.println("Books in " + language + ":");
			for (BookEntity book : booksByLanguage) {
				displayBookDetails(book);
			}
		}
	}


	private void workWithStatistics() {
		DoubleSummaryStatistics statistics = gutendexService.generateStatistics();
		System.out.println("Count Books in DB: " + statistics.getCount());
		System.out.println("Min download: " + statistics.getMin());
		System.out.println("Max download: " + statistics.getMax());
		System.out.println("Average download: " + statistics.getAverage());
		System.out.println("Sum: " + statistics.getSum());


	}

	private void listMyTop() {
		List<BookEntity> topBooks = gutendexService.listTopBooks();
		if (topBooks.isEmpty()) {
			System.out.println("There are no books in your collection.");
			return;
		}

		System.out.println("****************************");
		System.out.println("Top 10 books in your collection:");
		for (BookEntity book : topBooks) {
			displayBookDetails(book);
		}
	}

	private void searchAuthor(Scanner keyboard) {
		System.out.println("****************************");
		System.out.println("Enter the author's name:");
		String author = keyboard.nextLine().toLowerCase();
		// Call a new method that searches for books by the author in your database
		var books = gutendexService.searchBooksByAuthorInDatabase(author);

		if (books.isEmpty()) {
			System.out.println("No books were found for the provided author in the database.");
			return;
		}

		System.out.println("Books found in the database:");
		for (BookEntity book : books) {
			displayBookDetails(book);
		}
	}
}
