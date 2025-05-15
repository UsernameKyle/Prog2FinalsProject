import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class AndreyCafeOrderingSystem extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel contentPanel;
    private JPanel orderPanel;
    private JButton checkoutBtn;
    private final HashMap<String, OrderItem> currentOrder = new HashMap<>();
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
    private JLabel totalLabel;

    public AndreyCafeOrderingSystem() {
        setTitle("Mattari Ordering System");
        setSize(980, 740);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Initialize currency format
        currencyFormat.setMaximumFractionDigits(2);

        JPanel topPanel = new JPanel();
        topPanel.setPreferredSize(new Dimension(getWidth(), 50));
        topPanel.setBackground(new Color(122, 76, 18));
        JLabel welcomeMessage = new JLabel("~ Welcome to Mattari ~");
        welcomeMessage.setForeground(new Color(245, 233, 211));
        welcomeMessage.setFont(new Font("ARIAL", Font.BOLD, 36));
        topPanel.add(welcomeMessage);
        add(topPanel, BorderLayout.NORTH);
        
        JPanel sidePanel = createSidePanel();
        add(sidePanel, BorderLayout.WEST);

        contentPanel = new JPanel();
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);
        contentPanel.setBackground(Color.WHITE);

        // Create menu panels
        contentPanel.add(createCategoryPanel("Food", new String[]{
                "Chicken na Katsu na Curry na Rice", 
                "Sweet Omerice", 
                "Many Many Sushi Rolls", 
                "Umami Ramen", 
                "Special Udon", 
                "Omega Gyudon"
        }, new double[]{219.00, 189.00, 289.00, 319.00, 249.00, 219.00}), "Food");
        
        contentPanel.add(createCategoryPanel("Drinks", new String[]{
                "Iced Matcha Latte", 
                "Choco Matcha Milk Tea", 
                "Sakura Fizz", 
                "Hojicha", 	
                "Ramune", 
                "Sake"
        }, new double[]{89.00, 119.00, 119.00, 119.00, 89.00, 219.00}), "Drinks");
        
        contentPanel.add(createCategoryPanel("Appetizers", new String[]{
                "Onigiri", 
                "Gyoza", 
                "Takoyaki", 
                "Tofu", 
                "Karaage", 
                "Tempura"
        }, new double[]{49.00, 49.00, 49.00, 20.00, 89.00, 89.00}), "Appetizers");
        
        contentPanel.add(createCategoryPanel("Desserts", new String[] {
        		"Matcha Red Bean Paste Shaved Ice",
        		"Dorayaki",
        		"Matcha Gateau au Chocolat",
        		"Dango",
        		"Chocolate Chiffon Cake",
        		"LeBron"
        }, new double[] {139.00, 49.00, 149.00, 49.00, 389.00, 999.99}), "Desserts");
        
        contentPanel.add(createCheckoutPanel(), "Checkout");
        add(contentPanel, BorderLayout.CENTER);

        add(createFooterPanel(), BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel createCategoryPanel(String category, String[] itemNames, double[] prices) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 3, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(255, 250, 240));

        for (int i = 0; i < itemNames.length; i++) {
            String itemName = itemNames[i];
            double price = prices[i];
            MenuItem item = new MenuItem(itemName, price, category);
            panel.add(createMenuItemButton(item));
        }

        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(new Color(245, 233, 211));
        footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(160, 120, 90)));
        footerPanel.setPreferredSize(new Dimension(getWidth(), 60));

        // Create a panel for both buttons and total
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        buttonPanel.setBackground(new Color(245, 233, 211));

        totalLabel = new JLabel("Total: 0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalLabel.setForeground(new Color(122, 76, 18));
        buttonPanel.add(totalLabel);
        
        // View Order button
        checkoutBtn = new JButton("View Order (0)");
        checkoutBtn.setFont(new Font("Arial", Font.BOLD, 14));
        checkoutBtn.setBackground(new Color(160, 120, 90));
        checkoutBtn.setForeground(new Color(122, 76, 18));
        checkoutBtn.setFocusPainted(false);
        checkoutBtn.setBorder(BorderFactory.createLineBorder(new Color(80, 60, 45), 2));
        checkoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR)); // to make cursor into select cursor	
        checkoutBtn.addActionListener(e -> {
            refreshOrderPanel();
            cardLayout.show(contentPanel, "Checkout");
        });
        buttonPanel.add(checkoutBtn);

        // Finalize Checkout Order button
        JButton finalCheckoutBtn = new JButton("Checkout");
        finalCheckoutBtn.setFont(new Font("Arial", Font.BOLD, 14));
        finalCheckoutBtn.setBackground(new Color(120, 100, 80));
        finalCheckoutBtn.setForeground(new Color(122, 76, 18));
        finalCheckoutBtn.setFocusPainted(false);
        finalCheckoutBtn.setBorder(BorderFactory.createLineBorder(new Color(80, 60, 45), 2));
        finalCheckoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        finalCheckoutBtn.addActionListener(e -> finalizeOrder());
        buttonPanel.add(finalCheckoutBtn);

        footerPanel.add(buttonPanel, BorderLayout.EAST);

        return footerPanel;
    }

    private void updateTotalPrice() {
        double total = currentOrder.values().stream()
            .mapToDouble(OrderItem::getTotalPrice)
            .sum();
        totalLabel.setText("Total: " + currencyFormat.format(total));
    }
    
    private void finalizeOrder() {
        if (currentOrder.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your order is empty!", "Order Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Confirm your order totaling " + totalLabel.getText() + "?", 
            "Confirm Order", 
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(
                this, 
                "Order placed successfully! Thank you for your purchase.", 
                "Order Confirmed", 
                JOptionPane.INFORMATION_MESSAGE
            );
            currentOrder.clear();
            updateItemCount();
            refreshOrderPanel();
            totalLabel.setText("Total: ₱0.00");
            cardLayout.show(contentPanel, "Food"); // Return to food menu
        }
    }

    private JPanel createSidePanel() {
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new GridLayout(4, 1, 10, 10)); // Reduced to 3 rows since checkout moved to footer
        sidePanel.setBackground(new Color(245, 233, 211));
        sidePanel.setPreferredSize(new Dimension(200, 0));
        sidePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton foodBtn = createSideButton("Food");
        JButton drinkBtn = createSideButton("Drinks");
        JButton appetizerBtn = createSideButton("Appetizers");
        JButton dessertBtn = createSideButton("Desserts");

        foodBtn.addActionListener(e -> cardLayout.show(contentPanel, "Food"));
        drinkBtn.addActionListener(e -> cardLayout.show(contentPanel, "Drinks"));
        appetizerBtn.addActionListener(e -> cardLayout.show(contentPanel, "Appetizers"));
        dessertBtn.addActionListener(e -> cardLayout.show(contentPanel, "Desserts"));

        sidePanel.add(foodBtn);
        sidePanel.add(drinkBtn);
        sidePanel.add(appetizerBtn);
        sidePanel.add(dessertBtn);

        return sidePanel;
    }

    private JButton createSideButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(160, 120, 90));
        button.setForeground(new Color(122, 76, 18));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(80, 60, 45), 2));
        button.setPreferredSize(new Dimension(180, 50));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton createMenuItemButton(MenuItem item) {
        // Create a button with image and text
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.setPreferredSize(new Dimension(180, 200));
        
        // Load image
        ImageIcon icon = loadItemImage(item.getName());
        if (icon != null) {
            JLabel imageLabel = new JLabel(icon);
            imageLabel.setHorizontalAlignment(JLabel.CENTER);
            button.add(imageLabel, BorderLayout.CENTER);
        }
        
        // Create text panel
        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        
        JLabel nameLabel = new JLabel(item.getName(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        JLabel priceLabel = new JLabel(currencyFormat.format(item.getPrice()), SwingConstants.CENTER);
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        textPanel.setBorder(new EmptyBorder(0, 0, 12, 0)); 
        
        textPanel.add(nameLabel);
        textPanel.add(priceLabel);
        button.add(textPanel, BorderLayout.SOUTH);
        
        // Styling
        button.setBackground(new Color(210, 180, 140));
        button.setBorder(BorderFactory.createLineBorder(new Color(160, 120, 90), 2));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effects
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(190, 160, 120));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(210, 180, 140));
            }
        });
        
        button.addActionListener(e -> {
            addItemToOrder(item);
            showAddedToCartNotification(item.getName());
        });
        
        return button;
    }

    private ImageIcon loadItemImage(String itemName) {
        try {
            // Map item names to image files
            String imagePath = "/MattariIMGs/" + itemName.replace(" ", "_").toLowerCase() + ".png";
            ImageIcon originalIcon = new ImageIcon(getClass().getResource(imagePath));
            Image scaledImage = originalIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            System.err.println("Error loading image for " + itemName + ": " + e.getMessage());
            return null;
        }
    }

    private void showAddedToCartNotification(String itemName) {
        // Create a small notification
        JDialog notification = new JDialog(this, "", false);
        notification.setUndecorated(true);
        notification.setSize(200, 60);
        notification.setLayout(new BorderLayout());
        notification.getContentPane().setBackground(new Color(76, 175, 80)); // Green background
        
        // Center the notification relative to main window
        Point loc = getLocation();
        notification.setLocation(loc.x + 50, loc.y + getHeight() - 75);
        
        // Create notification content
        JLabel message = new JLabel("Added " + itemName + " to cart", SwingConstants.CENTER);
        message.setForeground(Color.WHITE);
        message.setFont(new Font("Arial", Font.BOLD, 12));
        notification.add(message, BorderLayout.CENTER);
        
        notification.setVisible(true);
        
        // closes notification after 2 seconds
        new Timer(2000, e -> {
            notification.dispose();
        }).start();
    }

    private void addItemToOrder(MenuItem item) {
    	OrderItem orderItem = currentOrder.computeIfAbsent(item.getName(), k -> new OrderItem(item));
        
        orderItem.increaseQty();
        updateItemCount();
        updateTotalPrice();
    }

    private void updateItemCount() {
        int totalItems = currentOrder.values().stream().mapToInt(OrderItem::getQuantity).sum();
        checkoutBtn.setText("View Order (" + totalItems + ")");
    }

    private JPanel createCheckoutPanel() {
        JPanel checkoutPanel = new JPanel(new BorderLayout());
        checkoutPanel.setBackground(new Color(255, 250, 240));

        orderPanel = new JPanel();
        orderPanel.setLayout(new BoxLayout(orderPanel, BoxLayout.Y_AXIS));
        orderPanel.setBackground(new Color(255, 250, 240));

        JScrollPane scrollPane = new JScrollPane(orderPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // why doesn't sp show on project
        checkoutPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(255, 250, 240));
        JButton clearBtn = new JButton("Clear Order");
        clearBtn.setFont(new Font("Arial", Font.BOLD, 16));
        clearBtn.setBackground(new Color(120, 100, 80));
        clearBtn.setForeground(new Color(122, 76, 18));
        clearBtn.setFocusPainted(false);
        clearBtn.setBorder(BorderFactory.createLineBorder(new Color(80, 60, 45), 2));
        clearBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearBtn.addActionListener(e -> {
            currentOrder.clear();
            updateItemCount();
            updateTotalPrice();
            refreshOrderPanel();
        });

        bottomPanel.add(clearBtn, BorderLayout.SOUTH);
        checkoutPanel.add(bottomPanel, BorderLayout.SOUTH);

        return checkoutPanel;
    }

    private void refreshOrderPanel() {
        orderPanel.removeAll();
        double total = 0.0;

        for (OrderItem orderItem : currentOrder.values()) {
            JPanel itemRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            itemRow.setBackground(new Color(255, 250, 240));
            itemRow.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            JLabel nameLabel = new JLabel(orderItem.getItem().getName());
            nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
            nameLabel.setPreferredSize(new Dimension(150, 20));

            JButton minusBtn = createQuantityButton("-", orderItem, true);
            JLabel qtyLabel = new JLabel(String.valueOf(orderItem.getQuantity()));
            qtyLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            qtyLabel.setPreferredSize(new Dimension(30, 20));

            JButton addBtn = createQuantityButton("+", orderItem, false);
            JLabel priceLabel = new JLabel(currencyFormat.format(orderItem.getTotalPrice()));
            priceLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            priceLabel.setPreferredSize(new Dimension(80, 20));

            itemRow.add(nameLabel);
            itemRow.add(minusBtn);
            itemRow.add(qtyLabel);
            itemRow.add(addBtn);
            itemRow.add(priceLabel);

            orderPanel.add(itemRow);
            total += orderItem.getTotalPrice();
        }

        // Update total in footer
        totalLabel.setText("Total: " + currencyFormat.format(total));

        updateTotalPrice();
        orderPanel.revalidate();
        orderPanel.repaint();
    }

    private JButton createQuantityButton(String text, OrderItem orderItem, boolean isMinus) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(30, 20));
        button.setBackground(new Color(190, 160, 120));
        button.setForeground(new Color(34, 85, 34));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(120, 100, 80), 1));

        button.addActionListener(e -> {
            if (isMinus) {
                orderItem.decreaseQty();
                if (orderItem.getQuantity() == 0) {
                    currentOrder.remove(orderItem.getItem().getName());
                }
            } else {
                orderItem.increaseQty();
            }
            updateItemCount();
            updateTotalPrice();
            refreshOrderPanel();
        });

        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                UIManager.put("ScrollBar.thumb", new Color(200, 170, 130)); // Scrollpane design
                UIManager.put("ScrollBar.thumbHighlight", new Color(180, 150, 110));
                UIManager.put("ScrollBar.thumbDarkShadow", new Color(160, 130, 100));
                UIManager.put("ScrollBar.thumbShadow", new Color(140, 110, 80));
            } catch (Exception e) {
                e.printStackTrace();
            }
            new AndreyCafeOrderingSystem();
        });
    }
}