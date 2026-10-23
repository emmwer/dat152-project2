/**
 * 
 */
package no.hvl.dat152.rest.ws.service;


import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.exceptions.UserNotFoundException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.model.User;
import no.hvl.dat152.rest.ws.repository.UserRepository;

/**
 * @author tdoy
 */
@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	
	public List<User> findAllUsers(){
		
		List<User> allUsers = (List<User>) userRepository.findAll();
		
		return allUsers;
	}
	
	public User findUser(Long userid) throws UserNotFoundException {
		
		User user = userRepository.findById(userid)
				.orElseThrow(()-> new UserNotFoundException("User with id: "+userid+" not found"));
		
		return user;
	}
	
	
	// DONE - public User saveUser(User user)
	public User saveUser(User user) {
		return userRepository.save(user);
	}
	
	// DONE - public void deleteUser(Long id) throws UserNotFoundException 
	public void deleteUser(Long id) throws UserNotFoundException {
		User user = findUser(id);
		userRepository.delete(user);
	}
	
	// DONE - public User updateUser(User user, Long id)
	public User updateUser(User user, Long id) throws UserNotFoundException{
		User existingUser = findUser(id);
		existingUser.setFirstname(user.getFirstname());
		existingUser.setLastname(user.getLastname());
		existingUser.setOrders(user.getOrders());
		return userRepository.save(existingUser);
	}
	
	// DONE - public Set<Order> getUserOrders(Long userid) 
	public Set<Order> getUserOrders(Long userid)  throws UserNotFoundException {
		User user = findUser(userid);
		return user.getOrders();
	}
	
	// DONE - public Order getUserOrder(Long userid, Long oid)
	public Order getUserOrder(Long userid, Long oid) throws UserNotFoundException, OrderNotFoundException {
		User user = findUser(userid);
		Set<Order> orders = user.getOrders();
		for (Order order : orders) {
			if (order.getId().equals(oid)) {
				return order;
			}
		}
		throw new OrderNotFoundException("Order with id: " + oid + " not found for user with id: " + userid);
	}
	
	// DONE - public void deleteOrderForUser(Long userid, Long oid)
	public void deleteOrderForUser(Long userid, Long oid) throws UserNotFoundException, OrderNotFoundException {
		User user = findUser(userid);
		Set<Order> orders = user.getOrders();
		for (Order order : orders) {
			if (order.getId().equals(oid)) {
				orders.remove(order);
				user.setOrders(orders);
				userRepository.save(user);
			}
			else {
				throw new OrderNotFoundException("Order with id: " + oid + " not found for user with id: " + userid);
			}
		}
	}
	
	// DONE - public User createOrdersForUser(Long userid, Order order)
	public User createOrdersForUser(Long userid, Order order) throws UserNotFoundException {
		User user = findUser(userid);
		user.addOrder(order);
		return userRepository.save(user);
	}
}
