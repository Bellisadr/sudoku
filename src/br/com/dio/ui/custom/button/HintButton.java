package br.com.dio.ui.custom.button;

import javax.swing.JButton;
import java.awt.event.ActionListener;

public class HintButton extends JButton {

    public HintButton(ActionListener actionListener) {
        this.setText("Dica");
        this.addActionListener(actionListener);
    }
}