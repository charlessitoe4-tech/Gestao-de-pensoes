package View;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;

/** Paleta verde já utilizada nas telas do sistema. */
final class UiTheme {

    static final Color GREEN = new Color(0, 102, 0);
    static final Color GREEN_DARK = new Color(0, 77, 38);
    static final Color GREEN_ACCENT = new Color(0, 153, 51);
    static final Color GREEN_SOFT = new Color(153, 255, 153);
    static final Color GREEN_PALE = new Color(237, 248, 239);
    static final Color SURFACE = Color.WHITE;
    static final Color TEXT = new Color(37, 52, 43);
    static final Color MUTED = new Color(91, 110, 98);
    static final Color BORDER = new Color(205, 226, 209);

    private UiTheme() {
    }

    static void configureDialogs() {
        javax.swing.UIManager.put("Panel.background", GREEN_PALE);
        javax.swing.UIManager.put("OptionPane.background", GREEN_PALE);
        javax.swing.UIManager.put("OptionPane.messageForeground", TEXT);
        javax.swing.UIManager.put("Button.background", GREEN);
        javax.swing.UIManager.put("Button.foreground", Color.WHITE);
        javax.swing.UIManager.put("TextField.background", SURFACE);
        javax.swing.UIManager.put("TextField.foreground", TEXT);
        javax.swing.UIManager.put("PasswordField.background", SURFACE);
        javax.swing.UIManager.put("PasswordField.foreground", TEXT);
        javax.swing.UIManager.put("ComboBox.background", SURFACE);
        javax.swing.UIManager.put("ComboBox.foreground", TEXT);
    }

    static void apply(Container raiz) {
        raiz.setBackground(GREEN_PALE);
        applyChildren(raiz);
    }

    static void styleTable(JTable tabela) {
        tabela.setBackground(SURFACE);
        tabela.setForeground(TEXT);
        tabela.setSelectionBackground(GREEN_SOFT);
        tabela.setSelectionForeground(GREEN_DARK);
        tabela.setGridColor(BORDER);
        tabela.setRowHeight(30);
        tabela.setFillsViewportHeight(true);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setShowVerticalLines(false);
        tabela.setIntercellSpacing(new java.awt.Dimension(0, 1));
        tabela.getTableHeader().setBackground(GREEN);
        tabela.getTableHeader().setForeground(Color.WHITE);
        tabela.getTableHeader().setFont(
                tabela.getTableHeader().getFont().deriveFont(Font.BOLD));
        tabela.getTableHeader().setPreferredSize(new java.awt.Dimension(0, 36));
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean selected, boolean focused, int row, int column) {
                Component cell = super.getTableCellRendererComponent(
                        table, value, selected, focused, row, column);
                if (!selected) {
                    cell.setBackground(row % 2 == 0 ? SURFACE : GREEN_PALE);
                    cell.setForeground(TEXT);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return cell;
            }
        };
        tabela.setDefaultRenderer(Object.class, renderer);
    }

    private static void applyChildren(Container container) {
        for (Component component : container.getComponents()) {
            if (component instanceof JPanel panel) {
                panel.setBackground(GREEN_PALE);
            }
            if (component instanceof JCheckBox checkBox) {
                checkBox.setBackground(container.getBackground());
                checkBox.setForeground(TEXT);
                checkBox.setFocusPainted(false);
                checkBox.setOpaque(false);
            } else if (component instanceof AbstractButton button) {
                button.setBackground(GREEN);
                button.setForeground(Color.WHITE);
                button.setFocusPainted(false);
                button.setOpaque(true);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(GREEN),
                        BorderFactory.createEmptyBorder(7, 12, 7, 12)));
            } else if (component instanceof JTextField || component instanceof JTextArea
                    || component instanceof JComboBox<?>) {
                component.setBackground(SURFACE);
                component.setForeground(TEXT);
                if (component instanceof JComponent swing) {
                    swing.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(BORDER),
                            BorderFactory.createEmptyBorder(5, 8, 5, 8)));
                }
            } else if (component instanceof JLabel label) {
                label.setForeground(TEXT);
            } else if (component instanceof JTable table) {
                styleTable(table);
            } else if (component instanceof JScrollPane scroll) {
                scroll.setBackground(SURFACE);
                scroll.getViewport().setBackground(SURFACE);
                scroll.setBorder(BorderFactory.createLineBorder(BORDER));
            }
            if (component instanceof Container child) {
                applyChildren(child);
            }
        }
    }
}
