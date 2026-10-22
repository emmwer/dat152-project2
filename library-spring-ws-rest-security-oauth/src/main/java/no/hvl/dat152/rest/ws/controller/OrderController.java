/**
 *
 */
package no.hvl.dat152.rest.ws.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.service.OrderService;

/**
 * @author tdoy
 */
@RestController
@RequestMapping("/elibrary/api/v1")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // getAllBorrowOrders + filter by expiry and paginate - read requests only for ADMIN
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/orders")
    public ResponseEntity<Object> getAllBorrowOrders(
            @RequestParam(value = "expiry", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiry,
            @PageableDefault(page = 0, size = 3) Pageable pageable) {

        List<Order> orders = (expiry != null) ? orderService.findOrdersByExpiryDate(expiry, pageable)
                : orderService.findAllOrders(pageable);

        if(orders.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    // getBorrowOrder - accessible to ADMIN and USER
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping(value = "/orders/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<Object> getBorrowOrder(@PathVariable Long id) throws OrderNotFoundException {

        Order order = orderService.findOrder(id);

        // links to the endpoints with actions possible on the order resource
        order.add(linkTo(methodOn(OrderController.class).getBorrowOrder(id)).withSelfRel());
        order.add(linkTo(methodOn(OrderController.class).updateOrder(null, id)).withRel("update order"));
        order.add(linkTo(methodOn(OrderController.class).deleteBookOrder(id)).withRel("cancel order"));

        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    // updateOrder - accessible to ADMIN and USER
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PutMapping("/orders/{id}")
    public ResponseEntity<Object> updateOrder(@RequestBody Order order, @PathVariable Long id) throws OrderNotFoundException {

        Order uorder = orderService.updateOrder(order, id);

        return new ResponseEntity<>(uorder, HttpStatus.OK);
    }

    // deleteBookOrder (cancel) - accessible to ADMIN and USER
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Object> deleteBookOrder(@PathVariable Long id) throws OrderNotFoundException {

        orderService.deleteOrder(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}