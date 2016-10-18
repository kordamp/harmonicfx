/*
 * Copyright 2013-2016 Gerrit Grunwald.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.hansolo.fx.cardnav;

import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.Ikonli;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;
import java.util.stream.Collectors;


/**
 * Created by hansolo on 21.08.16.
 */
public class CardBox extends StackPane {
    private boolean                  firstTime       = true;
    private FontIcon                 icon;
    private BooleanProperty          open;
    private ObservableList<Card>     cards           = FXCollections.observableArrayList();
    private EventHandler<MouseEvent> cardHandler;
    private Card                     selectedCard;
    private ParallelTransition       openTransition  = new ParallelTransition();
    private SequentialTransition     closeTransition = new SequentialTransition();
    private Ikon                     menuIkon        = Ikonli.NONE;
    private Ikon                     closeIkon       = Ikonli.NONE;

    public CardBox() { this(new Card[]{}); }
    public CardBox(final Card... CARDS) {
        super(CARDS);
        cards.addAll(CARDS);
        open            = new BooleanPropertyBase(false) {
            @Override protected void invalidated() { handleState(get()); }
            @Override public Object getBean() { return CardBox.this; }
            @Override public String getName() { return "open"; }
        };
        cardHandler     = e -> handleCardSelect((Card) e.getSource());
        selectedCard    = cards.size() == 0 ? null : cards.get(cards.size() - 1);
        initGraphics();
        registerListeners();
    }

    private void initGraphics() {
        icon = new FontIcon(menuIkon);
        icon.setIconSize(24);
        icon.setIconColor(Color.WHITE);
        icon.toFront();
        getChildren().add(icon);
        StackPane.setAlignment(icon, Pos.TOP_LEFT);
        StackPane.setMargin(icon, new Insets(5));
    }

    private void registerListeners() {
        widthProperty().addListener(e -> {
            if (firstTime) return;
            List<Card> allOtherCards = cards.stream().filter(card -> !card.equals(selectedCard)).collect(Collectors.toList());
            allOtherCards.forEach(card -> card.setTranslateX(getWidth() + 100));
        });
        icon.setOnMousePressed(e -> open.set(menuIkon == icon.getIconCode()));
        cards.forEach(card -> card.setOnMousePressed(cardHandler));
        getChildren().addListener(new ListChangeListener<Node>() {
            @Override public void onChanged(Change<? extends Node> c) {
                List<Node> allCards = getChildren().stream().filter(node -> node instanceof Card).collect(Collectors.toList());
                if (allCards.size() == 1) selectedCard = (Card) allCards.get(0);
                Platform.runLater(() -> icon.toFront());
            }
        });
    }

    private void handleState(final boolean IS_OPEN) {
        if (cards.isEmpty()) return;
        if (IS_OPEN) {
            firstTime = false;
            icon.setIconCode(closeIkon);
            icon.setIconColor(Color.BLACK);
            // Animate to open
            openTransition.getChildren().clear();
            for (int i = 0 ; i < cards.size() ; i++) {
                TranslateTransition openCard = new TranslateTransition(Duration.millis(200), cards.get(i));
                openCard.setFromX(cards.get(i).getTranslateX());
                openCard.setFromY(cards.get(i).getTranslateY());
                openCard.setToX(30 + i * 20);
                openCard.setToY(30 + i * 40);
                openTransition.getChildren().add(openCard);
            };
            openTransition.play();
        } else {
            icon.setIconCode(menuIkon);
            List<Card> allOtherCards = cards.stream().filter(card -> !card.equals(selectedCard)).collect(Collectors.toList());
            // Animate to close
            closeTransition.getChildren().clear();
            TranslateTransition selectedCardTranslate = new TranslateTransition(Duration.millis(200), selectedCard);
            selectedCardTranslate.setFromX(selectedCard.getTranslateX());
            selectedCardTranslate.setFromY(selectedCard.getTranslateY());
            selectedCardTranslate.setToX(0);
            selectedCardTranslate.setToY(0);

            ParallelTransition moveAllOut = new ParallelTransition();
            allOtherCards.forEach(card -> {
                TranslateTransition moveCardToRight = new TranslateTransition(Duration.millis(200), card);
                moveCardToRight.setFromX(card.getTranslateX());
                moveCardToRight.setFromY(card.getTranslateY());
                moveCardToRight.setToX(getWidth() + 100);
                moveCardToRight.setToY(card.getTranslateY());
                moveAllOut.getChildren().addAll(moveCardToRight);
            });
            closeTransition.getChildren().addAll(moveAllOut, selectedCardTranslate);

            closeTransition.setOnFinished(e -> icon.setIconColor(Color.WHITE));
            closeTransition.play();
        }
    }

    private void handleCardSelect(final Card CARD) {
        if (!open.get()) return;
        selectedCard = CARD;
        open.set(false);
    }

    public ObservableList<Card> getCards() { return cards; }
    public void setCards(final Card... CARDS) {
        cards.forEach(card -> card.removeEventHandler(MouseEvent.MOUSE_PRESSED, cardHandler));
        cards.setAll(CARDS);
        cards.forEach(card -> card.setOnMousePressed(e -> card.setOnMousePressed(cardHandler)));
        getChildren().setAll(icon);
        getChildren().addAll(CARDS);
    }
    public void addCard(final Card CARD) {
        cards.add(CARD);
        CARD.setOnMousePressed(cardHandler);
        getChildren().add(CARD);
    }
    public void removeCard(final Card CARD) {
        CARD.removeEventHandler(MouseEvent.MOUSE_PRESSED, cardHandler);
        cards.remove(CARD);
        getChildren().remove(CARD);
    }

    public Ikon getMenuIkon() { return menuIkon; }
    public Ikon getCloseIkon() { return closeIkon; }

    public void setMenuIkon(Ikon ikon) {
        menuIkon = ikon;
        if (!open.get()) {
            icon.setIconCode(menuIkon);
        }
    }

    public void setCloseIkon(Ikon ikon) {
        closeIkon = ikon;
        if (open.get()) {
            icon.setIconCode(closeIkon);
            icon.setIconColor(Color.BLACK);
        }
    }
}
