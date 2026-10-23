/**
 *
 */
package no.hvl.dat152.rest.ws.service;

import java.util.List;

import java.time.LocalDate;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.repository.OrderRepository;

/**
 * @author tdoy
 */
@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;

	public Order saveOrder(Order order) {

		order = orderRepository.save(order);

		return order;
	}

	public Order findOrder(Long id) throws OrderNotFoundException {

		Order order = orderRepository.findById(id)
				.orElseThrow(()-> new OrderNotFoundException("Order with id: "+id+" not found in the order list!"));

		return order;
	}

	// deleteOrder
	public void deleteOrder(Long id) throws OrderNotFoundException {

		Order order = findOrder(id);
		orderRepository.delete(order);
	}

	// findAllOrders
	public List<Order> findAllOrders() {

		List<Order> allOrders = new ArrayList<>();
		orderRepository.findAll().forEach(allOrders::add);

		return allOrders;
	}

	// findAllOrders with pagination
	public List<Order> findAllOrders(Pageable pageable) {

		Page<Order> orders = orderRepository.findAll(pageable);

		return orders.getContent();
	}

	// findOrdersByExpiryDate
	public List<Order> findOrdersByExpiryDate(LocalDate expiry, Pageable pageable) {

		Page<Order> orders = orderRepository.findByExpiryBefore(expiry, pageable);

		return orders.getContent();
	}

	// updateOrder
	public Order updateOrder(Order order, Long id) throws OrderNotFoundException {

		Order updateOrder = findOrder(id);
		updateOrder.setIsbn(order.getIsbn());
		updateOrder.setExpiry(order.getExpiry());

		return orderRepository.save(updateOrder);
	}

}
