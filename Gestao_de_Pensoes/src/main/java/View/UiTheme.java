package View;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/** Paleta comum inspirada no verde da interface original. */
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

    static void apply(Container raiz) {
        raiz.setBackground(GREEN_PALE);
        styleChildren(raiz);
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

        JTableHeader cabecalho = tabela.getTableHeader();
        cabecalho.setBackground(GREEN);
        cabecalho.setForeground(Color.WHITE);
        cabecalho.setFont(cabecalho.getFont().deriveFont(Font.BOLD));
        cabecalho.setPreferredSize(new java.awt.Dimension(0, 36));
        DefaultTableCellRenderer renderizador = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean selected, boolean focused, int row, int column) {
                Component celula = super.getTableCellRendererComponent(
                        table, value, selected, focused, row, column);
                if (!selected) {
                    celula.setBackground(row % 2 == 0 ? SURFACE : GREEN_PALE);
                    celula.setForeground(TEXT);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return celula;
            }
        };
        tabela.setDefaultRenderer(Object.class, renderizador);
    }

    static void styleNavigationButton(AbstractButton botao, boolean selecionado) {
        botao.setBackground(selecionado ? GREEN_SOFT : GREEN);
        botao.setForeground(selecionado ? GREEN_DARK : Color.WHITE);
        botao.setFont(botao.getFont().deriveFont(selecionado ? Font.BOLD : Font.PLAIN, 14f));
        botao.setHorizontalAlignment(AbstractButton.LEFT);
        botao.setFocusPainted(false);
        botao.setOpaque(true);
        botao.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 10));
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    static void styleDashboardCard(AbstractButton botao) {
        botao.setBackground(SURFACE);
        botao.setForeground(TEXT);
        botao.setFont(botao.getFont().deriveFont(Font.PLAIN, 15f));
        botao.setHorizontalAlignment(AbstractButton.LEFT);
        botao.setVerticalAlignment(AbstractButton.CENTER);
        botao.setFocusPainted(false);
        botao.setOpaque(true);
        botao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)));
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private static void styleChildren(Container container) {
        for (Component componente : container.getComponents()) {
            if (componente instanceof JPanel painel) {
                painel.setBackground(GREEN_PALE);
            }
            if (componente instanceof JCheckBox caixa) {
                caixa.setBackground(GREEN_PALE);
                caixa.setForeground(TEXT);
                caixa.setFocusPainted(false);
                caixa.setOpaque(false);
            } else if (componente instanceof AbstractButton botao) {
                botao.setBackground(GREEN);
                botao.setForeground(Color.WHITE);
                botao.setFocusPainted(false);
                botao.setOpaque(true);
                botao.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(GREEN),
                        BorderFactory.createEmptyBorder(7, 12, 7, 12)));
                botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            } else if (componente instanceof JTextField || componente instanceof JTextArea
                    || componente instanceof JComboBox<?>) {
                componente.setBackground(SURFACE);
                componente.setForeground(TEXT);
                if (componente instanceof JComponent swing) {
                    swing.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(BORDER),
                            BorderFactory.createEmptyBorder(5, 8, 5, 8)));
                }
            } else if (componente instanceof JLabel label) {
                label.setForeground(TEXT);
            } else if (componente instanceof JScrollPane scroll) {
                scroll.setBackground(SURFACE);
                scroll.getViewport().setBackground(SURFACE);
                scroll.setBorder(BorderFactory.createLineBorder(BORDER));
            }
            if (componente instanceof Container filho) {
                styleChildren(filho);
            }
        }
    }
}
