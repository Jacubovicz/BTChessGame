package org.portfolio.benttchessgame.ui.button;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.PseudoClass;
import javafx.scene.control.Button;

public class ButtonGroup {

    private static final PseudoClass SELECTED = PseudoClass.getPseudoClass("selected");

    private final ObjectProperty<Button> selectedButton = new SimpleObjectProperty<>(null);

    public ButtonGroup(){
        selectedButton.addListener((observable, oldButton, newButton) -> {
            // 1. Gasimy stary przycisk (jeśli jakiś był zaznaczony)
            if (oldButton != null) {
                setSelected(oldButton, false);
            }
            // 2. Zapalamy nowy przycisk (jeśli nowa wartość to nie null)
            if (newButton != null) {
                setSelected(newButton, true);
            }
            // USUNIĘTO: selectButton(newButton) - to naprawia StackOverflowError
        });
    }

    // Pozwala zaznaczyć przycisk programistycznie z poziomu kodu Javy
    public void selectButton(Button button){
        selectedButton.set(button);
    }

    // Poprawnie czyści stan grupy (zmień na null, listener sam zgasi aktywny przycisk)
    public void clearSelection(){
        selectedButton.set(null);
    } // USUNIĘTO: zbędny średnik

    public Button getSelectedButton(){
        return selectedButton.get();
    }

    public void setSelected(Button button, boolean active){
        button.pseudoClassStateChanged(SELECTED, active);
    }

    public void addButton(Button button){
        button.pseudoClassStateChanged(SELECTED, false);

        // POPRAWKA: Kliknięcie przycisku aktualizuje stan CAŁEJ grupy
        button.setOnAction(event -> selectButton(button));
    }

    public void addButtons(Button... buttons){
        for(Button button : buttons){
            addButton(button);
        }
    }
}