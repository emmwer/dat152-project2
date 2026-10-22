/**
 *
 */
package no.hvl.dat152.rest.ws.service;


import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.exceptions.UnauthorizedOrderActionException;
import no.hvl.dat152.rest.ws.exceptions.UserNotFoundException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.model.User;
import no.hvl.dat152.rest.ws.repository.OrderRepository;
import no.hvl.dat152.rest.ws.repository.UserRepository;

/**
 * @author tdoy
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;


    public List<User> findAllUsers(){

        List<User> allUsers = (List<User>) userRepository.findAll();

        return allUsers;
    }

    public User findUser(Long userid) throws UserNotFoundException {

        User user = userRepository.findById(userid)
                .orElseThrow(()-> new UserNotFoundException("User with id: "+userid+" not found"));

        return user;
    }


    // saveUser
    public User saveUser(User user) {

        return userRepository.save(user);
    }

    // deleteUser
    public void deleteUser(Long id) throws UserNotFoundException {

        User user = findUser(id);
        userRepository.delete(user);
    }

    // updateUser
    public User updateUser(User user, Long id) throws UserNotFoundException {

        User updateUser = findUser(id);
        updateUser.setEmail(user.getEmail());
        updateUser.setFirstname(user.getFirstname());
        updateUser.setLastname(user.getLastname());

        return userRepository.save(updateUser);
    }

    // getUserOrders
    public Set<Order> getUserOrders(Long userid) throws UserNotFoundException {

        User user = findUser(userid);

        return user.getOrders();
    }

    // getUserOrder
    public Order getUserOrder(Long userid, Long oid) throws UserNotFoundException, UnauthorizedOrderActionException {

        User user = findUser(userid);

        Order order = user.getOrders().stream()
                .filter(o -> o.getId().equals(oid))
                .findFirst()
                .orElseThrow(() -> new UnauthorizedOrderActionException("User with id: "+userid+" is not authorized to access the order with id: "+oid));

        return order;
    }

    // deleteOrderForUser
    public void deleteOrderForUser(Long userid, Long oid) throws UserNotFoundException, UnauthorizedOrderActionException {

        Order order = getUserOrder(userid, oid);
        User user = findUser(userid);

        user.removeOrder(order);
        orderRepository.delete(order);
    }

    // createOrdersForUser
    public User createOrdersForUser(Long userid, Order order) throws UserNotFoundException {

        User user = findUser(userid);
        Order savedOrder = orderRepository.save(order);
        user.addOrder(savedOrder);

        return userRepository.save(user);
    }
}