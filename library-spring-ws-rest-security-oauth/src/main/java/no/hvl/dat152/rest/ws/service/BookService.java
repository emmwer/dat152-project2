/**
 *
 */
package no.hvl.dat152.rest.ws.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import no.hvl.dat152.rest.ws.exceptions.BookNotFoundException;
import no.hvl.dat152.rest.ws.exceptions.UpdateBookFailedException;
import no.hvl.dat152.rest.ws.model.Author;
import no.hvl.dat152.rest.ws.model.Book;
import no.hvl.dat152.rest.ws.repository.BookRepository;

/**
 * @author tdoy
 */
@Service
public class BookService {

	@Autowired
	private BookRepository bookRepository;


	public Book saveBook(Book book) {

		return bookRepository.save(book);

	}

	public List<Book> findAll(){

		return (List<Book>) bookRepository.findAll();

	}


	public Book findByISBN(String isbn) throws BookNotFoundException {

		Book book = bookRepository.findByIsbn(isbn)
				.orElseThrow(() -> new BookNotFoundException("Book with isbn = "+isbn+" not found!"));

		return book;
	}

	// updateBook
	public Book updateBook(Book book, String isbn) throws BookNotFoundException, UpdateBookFailedException {

		Book updateBook = findByISBN(isbn);
		updateBook.setTitle(book.getTitle());

		Set<Author> authors = updateBook.getAuthors();
		authors.clear();
		authors.addAll(book.getAuthors());

		try {
			return bookRepository.save(updateBook);
		} catch (Exception e) {
			throw new UpdateBookFailedException("Update of book with isbn: "+isbn+" failed!");
		}
	}

	// findAllPaginate
	public List<Book> findAllPaginate(Pageable page) {

		Page<Book> books = bookRepository.findAll(page);

		return books.getContent();
	}

	// findAuthorsOfBookByISBN
	public Set<Author> findAuthorsOfBookByISBN(String isbn) throws BookNotFoundException {

		Book book = findByISBN(isbn);

		return book.getAuthors();
	}

	// deleteById
	public void deleteById(long id) {

		bookRepository.deleteById(id);
	}

	// deleteByISBN
	public void deleteByISBN(String isbn) throws BookNotFoundException {

		Book book = findByISBN(isbn);
		bookRepository.delete(book);
	}

}
