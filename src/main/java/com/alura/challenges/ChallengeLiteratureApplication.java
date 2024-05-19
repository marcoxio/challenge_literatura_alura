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
		Scanner teclado = new Scanner(System.in);
		muestraElMenu(teclado);

	}

	public void muestraElMenu(Scanner teclado) {

		var opcion = -1;
		while (opcion != 0) {
			var menu = """
                    1 - Buscar libro por titulo
                    2 - Buscar libros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos en un determinado año
                    5 - Listar libros por idioma
                    6 - Generador estadisticas
                    7 - Top 10 libros mas descargados
                    8 - Buscar autor por nombre
                    9 - Listar autores con otras consultas

                    0 - Salir
                    """;
			System.out.println(menu);
			opcion = teclado.nextInt();
			teclado.nextLine();

			switch (opcion) {
				case 1:
					bookByTitle(teclado);
					break;
				case 2:
                    obtainAllBooks();
					break;
				case 3:
                    obtainAllAuthors();
					break;
				case 4:
                    listarAutoresVivos(teclado);
					break;
				case 5:
                    listarLibrosPorIdioma(teclado);
					break;
				case 6:
					trabajarConEstadisticas();
					break;
				case 7:
					listarMiTop();
					break;
				case 8:
					buscarAutor(teclado);
					break;
				case 0:
					System.out.println("Cerrando la aplicación...");
					break;
				default:
					System.out.println("Opción inválida");
			}
		}



	}

	private void bookByTitle(Scanner teclado) {
		System.out.println("Ingrese el nombre del libro que desea buscar");
		String query = teclado.nextLine().toLowerCase();
		var libros = gutendexService.buscarLibros(query);

		if (libros.isEmpty()) {
			System.out.println("No se encontraron libros con el título de búsqueda proporcionado.");
			return;
		}

		System.out.println("Libros encontrados:");
		for (int i = 0; i < libros.size(); i++) {
			Book libro = libros.get(i);
			System.out.println("----- LIBRO -----");
			System.out.println("  Titulo: " + libro.getTitle());
			System.out.println("  Autor: " + libro.getAuthors().get(0).getName());
			System.out.println("  Idioma: " + libro.getLanguages().get(0));
			System.out.println("  Numero de descargas: " + libro.getDownloadCount());
			System.out.println("-----------------");

		}

		Book libroSeleccionado  = libros.stream()
				.findFirst()
				.orElse(null);

		if (libroSeleccionado != null) {
			gutendexService.guardarLibro(libroSeleccionado);
			System.out.println("Libro guardado: " + libroSeleccionado.getTitle());
		} else {
			System.out.println("Libro no guardado");
		}

	}

	private void obtainAllBooks() {
		List<BookEntity> misLibros = gutendexService.obtainAllBooks();
		if (misLibros.isEmpty()) {
			System.out.println("No hay libros en tu colección.");
			return;
		}

		System.out.println("****************************");
		System.out.println("----- LIBRO -----");
		for (BookEntity libro : misLibros) {
			Collectors Collectors = null;
			System.out.println(
					"ID: " + libro.getId() + " - Titulo: " + libro.getTitle() +
							" - Autor(es): " + libro.getAuthors().stream()
					.map(AuthorEntity::getName)
					.collect(Collectors.joining(", ")));
		}
	}

	private void obtainAllAuthors() {
		List<AuthorEntity> misAutores = gutendexService.obtainAllAuthors();
		if (misAutores.isEmpty()) {
			System.out.println("No hay autores en tu colección.");
			return;
		}

		System.out.println("****************************");
		System.out.println("Autores en tu colección:");
		for (AuthorEntity autor : misAutores) {
			System.out.println("Nombre: " + autor.getName() + " - Año de nacimiento: " + autor.getBirthYear() + " - Año de fallecimiento: " + autor.getDeathYear());
		}
	}

	private void listarAutoresVivos(Scanner teclado) {
		System.out.println("****************************");
		System.out.println("Ingrese el año:");
		while (!teclado.hasNextInt()) {
			System.out.println("Entrada inválida. Por favor, ingrese un año válido.");
			teclado.next(); // consume el input inválido
		}
		int year = teclado.nextInt();
		teclado.nextLine(); // consume newline

		List<AuthorEntity> autoresVivos = gutendexService.listarAutoresVivosEnAno(year);
		if (autoresVivos.isEmpty()) {
			System.out.println("No hay autores vivos en el año especificado.");
			return;
		}

		System.out.println("****************************");
		System.out.println("Autores vivos en el año " + year + ":");
		for (AuthorEntity autor : autoresVivos) {
			System.out.println("Nombre: " + autor.getName() + " - Año de nacimiento: " + autor.getBirthYear() + " - Año de fallecimiento: " + autor.getDeathYear());
		}
	}

	private void listarLibrosPorIdioma(Scanner teclado) {
		System.out.println("****************************");
		System.out.println("Ingrese en para inglés, es para español):");
		String idioma = teclado.nextLine().toLowerCase();
		List<BookEntity> librosPorIdioma = gutendexService.listarLibrosPorIdioma(idioma);
		if (librosPorIdioma.isEmpty()) {
			System.out.println("No hay libros en el idioma especificado.");
			return;
		}

		System.out.println("****************************");
		System.out.println("Libros en " + idioma + ":");
		for (BookEntity libro : librosPorIdioma) {
			System.out.println("ID: " + libro.getId() + " - Título: " + libro.getTitle() + " - Autor(es): " + libro.getAuthors().stream()
					.map(AuthorEntity::getName)
					.collect(Collectors.joining(", ")));
		}
	}

	private void trabajarConEstadisticas() {

	}

	//Trabajando con estadisticas
//	DoubleSummaryStatistics est = datos.resultados().stream()
//			.filter(d -> d.numeroDeDescargas() >0 )
//			.collect(Collectors.summarizingDouble(DatosLibros::numeroDeDescargas));
//        System.out.println("Cantidad media de descargas: " + est.getAverage());
//        System.out.println("Cantidad máxima de descargas: "+ est.getMax());
//        System.out.println("Cantidad mínima de descargas: " + est.getMin());
//        System.out.println(" Cantidad de registros evaluados para calcular las estadisticas: " + est.getCount());


	private void listarMiTop() {
		List<BookEntity> topLibros = gutendexService.listarTopLibros();
		if (topLibros.isEmpty()) {
			System.out.println("No hay libros en tu colección.");
			return;
		}

		System.out.println("****************************");
		System.out.println("Top 5 libros en tu colección:");
		for (BookEntity libro : topLibros) {
			System.out.println("ID: " + libro.getId() + " - Título: " + libro.getTitle() + " - Descargas: " + libro.getDownloadCount());
		}
	}

	private void buscarAutor(Scanner teclado) {
		System.out.println("****************************");
		System.out.println("Ingrese el nombre del autor:");
		String autor = teclado.nextLine().toLowerCase();
		var libros = gutendexService.buscarLibrosPorAutor(autor);

		if (libros.isEmpty()) {
			System.out.println("No se encontraron libros para el autor proporcionado.");
			return;
		}

		System.out.println("Libros encontrados:");
		for (int i = 0; i < libros.size(); i++) {
			Book libro = libros.get(i);
			System.out.println("ID: " + libro.getId() + " - Título: " + libro.getTitle() + " - Autor: " + libro.getAuthors().get(0).getName());
		}

		System.out.println("¿Desea guardar alguno de estos libros en la colección? (si/no)");
		String respuesta = teclado.nextLine();

		if (respuesta.equalsIgnoreCase("si")) {
			System.out.println("Ingrese el ID del libro que desea guardar:");
			int idLibro = teclado.nextInt();
			teclado.nextLine(); // consume newline

			Book libroSeleccionado = libros.stream()
					.filter(libro -> libro.getId() == idLibro)
					.findFirst()
					.orElse(null);

			if (libroSeleccionado != null) {
				gutendexService.guardarLibro(libroSeleccionado);
				System.out.println("Libro guardado: " + libroSeleccionado.getTitle());
			} else {
				System.out.println("ID de libro inválido.");
			}
		}
	}
}
