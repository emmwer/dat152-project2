/**
 *
 */
package no.hvl.dat152.rest.ws.service;

import java.util.List;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.hvl.dat152.rest.ws.exceptions.AuthorNotFoundException;
import no.hvl.dat152.rest.ws.model.Author;
import no.hvl.dat152.rest.ws.model.Book;
import no.hvl.dat152.rest.ws.repository.AuthorRepository;
import no.hvl.dat152.rest.ws.repository.BookRepository;

/**
 * @author tdoy
 */
@Service
public class AuthorService {

	@Autowired
	private AuthorRepository authorRepository;

	@Autowired
	private BookRepository bookRepository;


	public Author findById(long id) throws AuthorNotFoundException {

		Author author = authorRepository.findById((int) id)
				.orElseThrow(()-> new AuthorNotFoundException("Author with the id: "+id+ "not found!"));

		return author;
	}

	// saveAuthor
	public Author saveAuthor(Author author) {

		return authorRepository.save(author);
	}

	// updateAuthor
	public Author updateAuthor(Author author, int id) throws AuthorNotFoundException {

		Author updateAuthor = findById(id);
		updateAuthor.setFirstname(author.getFirstname());
		updateAuthor.setLastname(author.getLastname());

		return authorRepository.save(updateAuthor);
	}

	// findAll
	public List<Author> findAll() {

		return (List<Author>) authorRepository.findAll();
	}

	// deleteById
	public void deleteById(int id) throws AuthorNotFoundException {

		Author author = findById(id);
		authorRepository.delete(author);
	}

	// findBooksByAuthorId
	public Set<Book> findBooksByAuthorId(int id) {

		return new HashSet<>(bookRepository.findBooksByAuthorId(id));
	}
}
