class SystemManager {
    processOrder(order) {
        if (order.type == "standard") {
            verifyInventory(order);
            processStandardPayment(order);
        } else if (order.type == "express") {
            verifyInventory(order);
            processExpressPayment(order, "highPriority");
        }
        updateOrderStatus(order, "processed");
        notifyCustomer(order);
    }

    verifyInventory(order) {
        // Checks inventory levels
        if (inventory < order.quantity) {
            throw new Error("Out of stock");
        }
    }

    processStandardPayment(order) {
        // Handles standard payment processing
        if (paymentService.process(order.amount)) {
            return true;
        } else {
            throw new Error("Payment failed");
        }
    }

    processExpressPayment(order, priority) {
        // Handles express payment processing
        if (expressPaymentService.process(order.amount, priority)) {
            return true;
        } else {
            throw new Error("Express payment failed");
        }
    }

    updateOrderStatus(order, status) {
        // Updates the order status in the database
        database.updateOrderStatus(order.id, status);
    }

    notifyCustomer(order) {
        // Sends an email notification to the customer
        emailService.sendEmail(order.customerEmail, "Your order has been processed.");
    }
}


//Refactorizacion



// Interface for payment processing
interface PaymentProcessor {
    boolean process(Order order);
}

// Implementation for standard payment processing
class StandardPaymentProcessor implements PaymentProcessor {
    public boolean process(Order order) {
        if (paymentService.process(order.amount)) {
            return true;
        } else {
            throw new Error("Payment failed");
        }
    }
}

// Implementation for express payment processing
class ExpressPaymentProcessor implements PaymentProcessor {
    public boolean process(Order order) {
        if (expressPaymentService.process(order.amount, "highPriority")) {
            return true;
        } else {
            throw new Error("Express payment failed");
        }
    }
}

// Interface for inventory management
interface InventoryManager {
    void verifyInventory(Order order);
}

// Implementation for inventory verification
class InventoryService implements InventoryManager {
    public void verifyInventory(Order order) {
        // Checks inventory levels
        if (inventory < order.quantity) {
            throw new Error("Out of stock");
        }
    }
}

// Interface for order status management
interface OrderStatusUpdater {
    void updateOrderStatus(Order order, String status);
}

// Implementation for updating order status
class DatabaseOrderStatusUpdater implements OrderStatusUpdater {
    public void updateOrderStatus(Order order, String status) {
        // Updates the order status in the database
        database.updateOrderStatus(order.id, status);
    }
}

// Interface for customer notification
interface NotificationService {
    void notifyCustomer(Order order);
}

// Implementation for customer notification
class EmailNotificationService implements NotificationService {
    public void notifyCustomer(Order order) {
        // Sends an email notification to the customer
        emailService.sendEmail(order.customerEmail, "Your order has been processed.");
    }
}

// Main order processor class using dependency injection
class OrderProcessor {
    private final InventoryManager inventoryManager;
    private final PaymentProcessor paymentProcessor;
    private final OrderStatusUpdater orderStatusUpdater;
    private final NotificationService notificationService;

    public OrderProcessor(InventoryManager inventoryManager, 
                          PaymentProcessor paymentProcessor, 
                          OrderStatusUpdater orderStatusUpdater, 
                          NotificationService notificationService) {
        this.inventoryManager = inventoryManager;
        this.paymentProcessor = paymentProcessor;
        this.orderStatusUpdater = orderStatusUpdater;
        this.notificationService = notificationService;
    }

    public void processOrder(Order order) {
        inventoryManager.verifyInventory(order); // Verify inventory

        // Process payment based on order type
        if (order.type.equals("standard")) {
            paymentProcessor = new StandardPaymentProcessor();
        } else if (order.type.equals("express")) {
            paymentProcessor = new ExpressPaymentProcessor();
        }
        
        paymentProcessor.process(order); // Process payment
        orderStatusUpdater.updateOrderStatus(order, "processed"); // Update order status
        notificationService.notifyCustomer(order); // Notify customer
    }
}



