/**
 *
 */
package no.hvl.dat152.rest.ws.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.exceptions.UserNotFoundException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.model.User;
import no.hvl.dat152.rest.ws.service.UserService;

/**
 * @author tdoy
 */
@RestController
@RequestMapping("/elibrary/api/v1")
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping("/users")
	public ResponseEntity<Object> getUsers(){

		List<User> users = userService.findAllUsers();

		if(users.isEmpty())

			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		else
			return new ResponseEntity<>(users, HttpStatus.OK);
	}

	@GetMapping(value = "/users/{id}")
	public ResponseEntity<Object> getUser(@PathVariable Long id) throws UserNotFoundException, OrderNotFoundException{

		User user = userService.findUser(id);

		return new ResponseEntity<>(user, HttpStatus.OK);

	}

	// createUser
	@PostMapping("/users")
	public ResponseEntity<Object> createUser(@RequestBody User user) {

		User nuser = userService.saveUser(user);

		return new ResponseEntity<>(nuser, HttpStatus.CREATED);
	}

	// updateUser
	@PutMapping("/users/{id}")
	public ResponseEntity<Object> updateUser(@RequestBody User user, @PathVariable Long id) throws UserNotFoundException {

		User uuser = userService.updateUser(user, id);

		return new ResponseEntity<>(uuser, HttpStatus.OK);
	}

	// deleteUser
	@DeleteMapping("/users/{id}")
	public ResponseEntity<Object> deleteUser(@PathVariable Long id) throws UserNotFoundException {

		userService.deleteUser(id);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	// getUserOrders
	@GetMapping("/users/{id}/orders")
	public ResponseEntity<Object> getUserOrders(@PathVariable Long id) throws UserNotFoundException {

		Set<Order> orders = userService.getUserOrders(id);

		if(orders.isEmpty())
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);

		return new ResponseEntity<>(orders, HttpStatus.OK);
	}

	// getUserOrder
	@GetMapping("/users/{uid}/orders/{oid}")
	public ResponseEntity<Object> getUserOrder(@PathVariable Long uid, @PathVariable Long oid) throws UserNotFoundException, OrderNotFoundException {

		Order order = userService.getUserOrder(uid, oid);

		return new ResponseEntity<>(order, HttpStatus.OK);
	}

	// deleteUserOrder
	@DeleteMapping("/users/{uid}/orders/{oid}")
	public ResponseEntity<Object> deleteUserOrder(@PathVariable Long uid, @PathVariable Long oid) throws UserNotFoundException, OrderNotFoundException {

		userService.deleteOrderForUser(uid, oid);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	// createUserOrder + HATEOAS links
	@PostMapping("/users/{id}/orders")
	public ResponseEntity<Object> createUserOrder(@PathVariable Long id, @RequestBody Order order) throws UserNotFoundException, OrderNotFoundException {

		User user = userService.createOrdersForUser(id, order);

		Set<Order> orders = user.getOrders();

		List<Map<String, Object>> response = new ArrayList<>();

		for (Order o : orders) {
			// links to the endpoints with actions possible on the order resource
			Link self = linkTo(methodOn(UserController.class).getUserOrder(id, o.getId())).withSelfRel();
			Link cancel = linkTo(methodOn(UserController.class).deleteUserOrder(id, o.getId())).withRel("cancel order");

			Map<String, Object> om = new LinkedHashMap<>();
			om.put("id", o.getId());
			om.put("isbn", o.getIsbn());
			om.put("expiry", o.getExpiry().toString());
			om.put("links", List.of(
					Map.of("rel", self.getRel().value(), "href", self.getHref()),
					Map.of("rel", cancel.getRel().value(), "href", cancel.getHref())));

			response.add(om);
		}

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

}