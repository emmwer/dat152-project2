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
	
	// DONE - public Book updateBook(Book book, String isbn)
	public Book updateBook(Book book, String isbn) throws UpdateBookFailedException, BookNotFoundException {
		Book existingBook = findByISBN(isbn);
		existingBook.setTitle(book.getTitle());
		existingBook.setAuthors(book.getAuthors());
		return bookRepository.save(existingBook);
	}
	
	// DONE - public List<Book> findAllPaginate(Pageable page)
	public List<Book> findAllPaginate(Pageable page) {
		List<Book> bookPage = bookRepository.findAllPaginate(page.getPageSize(), (int) page.getOffset());
		return bookPage;
	}
	
	// DONE - public Set<Author> findAuthorsOfBookByISBN(String isbn)
	public Set<Author> findAuthorsOfBookByISBN(String isbn) throws BookNotFoundException {
		Book book = findByISBN(isbn);
		return book.getAuthors();
	}
	
	// DONE - public void deleteById(long id)
	public void deleteById(long id) throws BookNotFoundException {
		bookRepository.deleteById(id);
	}
	
	// DONE - public void deleteByISBN(String isbn) 
	public void deleteByISBN(String isbn) throws BookNotFoundException {
		Book book = findByISBN(isbn);
		bookRepository.delete(book);
	}
	
}
