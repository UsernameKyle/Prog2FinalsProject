public class OrderItem {
    private final MenuItem item;
    private int quantity;

    public OrderItem(MenuItem item) {
        this.item = item;
        this.quantity = 0;
    }

    public MenuItem getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void increaseQty() {
        quantity++;
    }

    public void decreaseQty() {
        if (quantity > 0) {
            quantity--;
        }
    }

    public double getTotalPrice() {
        return item.getPrice() * quantity;
    }
}
