package org.example.view;

import org.example.controller.OrcamentoController;
import org.example.model.Item;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OrcamentoView extends JFrame {

    private JTextField campoNome, campoValor;
    private JPanel itensPanel;
    private List<ItemField> camposItens = new ArrayList<>();
    private OrcamentoController controller = new OrcamentoController();

    public OrcamentoView() {
        setTitle("Orçamento");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Topo
        JPanel topoPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        topoPanel.setBorder(BorderFactory.createTitledBorder("Orçamento"));
        campoNome = new JTextField();
        campoValor = new JTextField();
        topoPanel.add(new JLabel("Item:"));
        topoPanel.add(campoNome);
        topoPanel.add(new JLabel("Valor:"));
        topoPanel.add(campoValor);
        add(topoPanel, BorderLayout.NORTH);

        // Centro - Lista de Itens
        itensPanel = new JPanel();
        itensPanel.setLayout(new BoxLayout(itensPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(itensPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Botões
        JPanel botoesPanel = new JPanel();
        JButton btnAdicionar = new JButton("Adicionar");
        JButton btnFinalizar = new JButton("Finalizar");
        botoesPanel.add(btnAdicionar);
        botoesPanel.add(btnFinalizar);
        add(botoesPanel, BorderLayout.SOUTH);

        // Ações
        btnAdicionar.addActionListener(e -> adicionarCamposItem());
        btnFinalizar.addActionListener(e -> finalizarOrcamento());

        setVisible(true);
    }

    private void adicionarCamposItem() {
        JPanel linhaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField itemField = new JTextField(15);
        JTextField precoField = new JTextField(7);
        JButton btnRemover = new JButton("X");

        ItemField item = new ItemField(itemField, precoField, linhaPanel);
        camposItens.add(item);

        btnRemover.addActionListener(e -> {
            camposItens.remove(item);
            itensPanel.remove(linhaPanel);
            itensPanel.revalidate();
            itensPanel.repaint();
        });

        linhaPanel.add(itemField);
        linhaPanel.add(precoField);
        linhaPanel.add(btnRemover);

        itensPanel.add(linhaPanel);
        itensPanel.revalidate();
        itensPanel.repaint();
    }

    private void finalizarOrcamento() {
        try {
            String nome = campoNome.getText();
            double valor = Double.parseDouble(campoValor.getText());
            List<Item> itens = new ArrayList<>();

            for (ItemField f : camposItens) {
                String nomeItem = f.item.getText();
                double preco = Double.parseDouble(f.preco.getText());
                itens.add(new Item(nomeItem, preco));
            }

            controller.gerarPlanilha(itens, nome, valor);
            JOptionPane.showMessageDialog(this, "Planilha criada com sucesso!");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Certifique-se de preencher todos os valores corretamente.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao criar o arquivo: " + e.getMessage());
        }
    }

    // Classe auxiliar para agrupar os campos
    private static class ItemField {
        JTextField item, preco;
        JPanel panel;
        ItemField(JTextField item, JTextField preco, JPanel panel) {
            this.item = item;
            this.preco = preco;
            this.panel = panel;
        }
    }
}
