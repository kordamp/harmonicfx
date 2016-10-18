/*
 * Copyright 2014-2016 Gerrit Grunwald.
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

import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.StyleablePropertyFactory;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.List;


/**
 * User: hansolo
 * Date: 21.08.16
 * Time: 07:57
 */
@DefaultProperty("children")
public class Card extends StackPane {
    private static final StyleablePropertyFactory<Card> FACTORY = new StyleablePropertyFactory<>(Region.getClassCssMetaData());

    private static final double                   PREFERRED_WIDTH  = 300;
    private static final double                   PREFERRED_HEIGHT = 400;
    private static final double                   MINIMUM_WIDTH    = 100;
    private static final double                   MINIMUM_HEIGHT   = 100;
    private static final double                   MAXIMUM_WIDTH    = 1024;
    private static final double                   MAXIMUM_HEIGHT   = 1024;
    private static final CssMetaData<Card, Color> CARD_COLOR       = FACTORY.createColorCssMetaData("-card-color", s -> s.cardColor, Color.web("#e9ee8f"), false);
    private static final CssMetaData<Card, Color> TITLE_COLOR      = FACTORY.createColorCssMetaData("-title-color", s -> s.titleColor, Color.BLACK, false);
    private        final StyleableProperty<Color> cardColor;
    private        final StyleableProperty<Color> titleColor;
    private              StringProperty           title;
    private              Label                    titleText;
    private              AnchorPane               content;
    private              VBox                     container;


    // ******************** Constructors **************************************
    public Card() { this(""); }
    public Card(final String TITLE) {
        getStylesheets().add(Card.class.getResource("card.css").toExternalForm());
        cardColor  = new StyleableObjectProperty<Color>(Color.web("#dae18f")) {
            @Override protected void invalidated() { container.setBackground(new Background(new BackgroundFill(get(), CornerRadii.EMPTY, Insets.EMPTY))); }
            @Override public Object getBean() { return Card.this; }
            @Override public String getName() { return "cardColor"; }
            @Override public CssMetaData<? extends Styleable, Color> getCssMetaData() { return CARD_COLOR; }
        };
        titleColor = new StyleableObjectProperty<Color>(Color.BLACK) {
            @Override protected void invalidated() { titleText.setTextFill(get()); }
            @Override public Object getBean() { return Card.this; }
            @Override public String getName() { return "titleColor"; }
            @Override public CssMetaData<? extends Styleable, Color> getCssMetaData() { return TITLE_COLOR; }
        };
        title      = new StringPropertyBase(TITLE) {
            @Override protected void invalidated() { titleText.setText(get()); }
            @Override public Object getBean() { return Card.this; }
            @Override public String getName() { return "title"; }
        };
        init();
        initGraphics();
        registerListeners();
    }


    // ******************** Initialization ************************************
    private void init() {
        if (Double.compare(getPrefWidth(), 0.0) <= 0 || Double.compare(getPrefHeight(), 0.0) <= 0 ||
            Double.compare(getWidth(), 0.0) <= 0 || Double.compare(getHeight(), 0.0) <= 0) {
            if (getPrefWidth() > 0 && getPrefHeight() > 0) {
                setPrefSize(getPrefWidth(), getPrefHeight());
            } else {
                setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
            }
        }

        if (Double.compare(getMinWidth(), 0.0) <= 0 || Double.compare(getMinHeight(), 0.0) <= 0) {
            setMinSize(MINIMUM_WIDTH, MINIMUM_HEIGHT);
        }

        if (Double.compare(getMaxWidth(), 0.0) <= 0 || Double.compare(getMaxHeight(), 0.0) <= 0) {
            setMaxSize(MAXIMUM_WIDTH, MAXIMUM_HEIGHT);
        }
    }

    private void initGraphics() {
        getStyleClass().setAll("card");

        titleText = new Label(title.get());
        titleText.getStyleClass().setAll("title");
        titleText.setPrefWidth(Double.MAX_VALUE);
        VBox.setVgrow(titleText, Priority.NEVER);

        content = new AnchorPane();
        content.getStyleClass().setAll("content");
        VBox.setVgrow(content, Priority.ALWAYS);

        container = new VBox(titleText, content);
        container.getStyleClass().setAll("container");
        container.setSpacing(5);
        container.setPadding(new Insets(5));

        getChildren().setAll(container);
    }

    private void registerListeners() {

    }


    // ******************** Methods *******************************************
    @Override public void layoutChildren() {
        super.layoutChildren();
    }

    private void handleControlPropertyChanged(final String PROPERTY) {
        if ("".equals(PROPERTY)) {

        }
    }

    public ObservableList<Node> getContent() { return content.getChildren(); }

    public String getTitle() { return title.get(); }
    public void setTitle(final String TITLE) { title.set(TITLE); }
    public StringProperty titleProperty() { return title; }

    public Color getCardColor() { return cardColor.getValue(); }
    public void setCardColor(final Color COLOR) { cardColor.setValue(COLOR); }
    public ObjectProperty<Color> cardColorProperty() { return (ObjectProperty<Color>) cardColor; }

    public Color getTitleColor() { return titleColor.getValue(); }
    public void setTitleColor(final Color COLOR) { titleColor.setValue(COLOR); }
    public ObjectProperty<Color> titleColorProperty() { return (ObjectProperty<Color>) titleColor; }


    // ******************** Style related *************************************
    public static  List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() { return FACTORY.getCssMetaData(); }
    @Override public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() { return FACTORY.getCssMetaData(); }
}
