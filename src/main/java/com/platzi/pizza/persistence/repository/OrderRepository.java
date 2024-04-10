package com.platzi.pizza.persistence.repository;

import com.platzi.pizza.persistence.entity.OrderEntity;
import com.platzi.pizza.persistence.projection.OrderSummary;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends ListCrudRepository<OrderEntity, Integer> {
    List<OrderEntity> findAllByDateAfter(LocalDateTime date);
    List<OrderEntity> findAllByMethodIn(List<String> methods);

    @Query(value = "SELECT * FROM pizza_order WHERE id_customer = :id", nativeQuery = true)
    List<OrderEntity> findCustomerOrders(@Param("id") String idCustomer);

    @Query(value =
            "SELECT  po.id_order AS idOrder, cu.name AS customerName, po.date AS orderDate," +
            "        po.total AS orderTotal, GROUP_CONCAT(pi.name) AS pizzaNames " +
            "FROM   pizza_order po  " +
            "   INNER JOIN customer cu ON po.id_customer = cu.id_customer  " +
            "   INNER JOIN order_item oi ON po.id_order = oi.id_order  " +
            "   INNER JOIN pizza pi ON oi.id_pizza = pi.id_pizza  " +
            "WHERE  po.id_order = :orderId " +
            "GROUP BY po.id_order, cu.name, po.date, po.total", nativeQuery = true)
    OrderSummary findSummary(@Param("orderId") int orderId);

    // USING POSTGRESQL
//    SELECT 	po.id_order AS idOrder, cu.name AS customerName, po.date AS orderDate,
//    po.total AS orderTotal, STRING_AGG(PI.name, ', ') AS pizzaNames
//    FROM 	pizza_order po
//    INNER JOIN customer cu ON po.id_customer = cu.id_customer
//    INNER JOIN order_item oi ON po.id_order = oi.id_order
//    INNER JOIN pizza PI ON oi.id_pizza = PI.id_pizza
//    WHERE po.id_order = 1
//    GROUP BY po.id_order, cu.id_customer, po.date, po.total

    @Procedure(value = "take_random_pizza_order", outputParameterName = "order_taken")
    boolean saveRandomOrder(@Param("id_customer") String idCustomer, @Param("method") String method);

//    DROP procedure IF EXISTS `take_random_pizza_order`;
//    DELIMITER $$
//    CREATE PROCEDURE `take_random_pizza_order`(	IN id_customer VARCHAR(15),
//    IN method CHAR(1),
//    OUT order_taken BOOL)
//    BEGIN
//    DECLARE id_random_pizza INT;
//    DECLARE price_random_pizza DECIMAL(5,2);
//    DECLARE price_with_discount DECIMAL(5,2);
//
//    DECLARE WITH_ERRORS BOOL DEFAULT FALSE;
//    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
//            BEGIN
//    SET WITH_ERRORS = TRUE;
//    END;
//
//    SELECT	id_pizza, price
//    INTO 	id_random_pizza, price_random_pizza
//    FROM 	pizza
//    WHERE 	available = 1
//    ORDER BY RAND()
//    LIMIT 	1;
//
//    SET price_with_discount = price_random_pizza - (price_random_pizza * 0.20);
//
//    START TRANSACTION;
//    INSERT INTO pizza_order (id_customer, date, total, method, additional_notes)
//    VALUES (id_customer, SYSDATE(), price_with_discount, method, '20% OFF PIZZA RANDOM PROMOTION');
//
//    INSERT INTO order_item (id_item, id_order, id_pizza, quantity, price)
//    VALUES (1, LAST_INSERT_ID(), id_random_pizza, 1, price_random_pizza);
//
//    IF WITH_ERRORS THEN
//    SET order_taken = FALSE;
//    ROLLBACK;
//    ELSE
//    SET order_taken = TRUE;
//    COMMIT;
//    END IF;
//
//    SELECT order_taken;
//    END$$
//
//            DELIMITER ;
}
