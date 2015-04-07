/*
 * Copyright 2015 Thierry Wasylczenko
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.twasyl.base64fx.controllers;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;

/**
 * The controller class for the main screen of Base64FX.
 * @author Thierry Wasylczenko
 * @version 0.1
 * @since Base64FX 0.1
 */
public class Base64FXController implements Initializable {

    @FXML private VBox messages;
    @FXML private TextArea sourceArea;
    @FXML private TextArea targetArea;

    /**
     * The {@link java.util.Base64.Decoder decoder} that is used by the application.
     */
    private final Base64.Decoder decoder = Base64.getDecoder();

    /**
     * The {@link java.util.Base64.Encoder encoder} that is used by the application.
     */
    private final Base64.Encoder encoder = Base64.getEncoder();

    /**
     * This method is called by the main screen of the application when the user chooses to decode a Base64 string.
     * @param event The event that triggers this method.
     */
    @FXML private void decode(ActionEvent event) {
        try {
            this.targetArea.setText(this.decode(this.sourceArea.getText()));
        } catch(NullPointerException | IllegalArgumentException e) {
            this.displayMessage(Level.SEVERE, e.getMessage());
        }

    }

    /**
     * This method is called by the main screen of the application when the user chooses to encode a String in Base64.
     * @param event The event that triggers this method.
     */
    @FXML private void encode(ActionEvent event) {
        try {
            this.targetArea.setText(this.encode(this.sourceArea.getText()));
        } catch(NullPointerException | IllegalArgumentException e) {
            this.displayMessage(Level.SEVERE, e.getMessage());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    /**
     * This method decodes the given Base64 {@code source} into plain UTF-8 string.
     * @param source The Base64 string to decode.
     * @return The decoded String in the UTF-8 charset.
     * @throws NullPointerException If the source is null.
     * @throws IllegalArgumentException If the source is empty.
     */
    public String decode(final String source) {
        if(source == null) throw new NullPointerException("The string to decode can not be null");
        if(source.trim().isEmpty()) throw new IllegalArgumentException("The string to decode can not be empty");

        final byte[] decodedBytes = this.decoder.decode(source.getBytes(StandardCharsets.UTF_8));
        final String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);

        copyToClipboard(decodedString);

        return decodedString;
    }

    /**
     * This method encodes the given {@code source} into a Base64 string.
     * @param source The string to encode.
     * @return The encoded Base64 string.
     * @throws NullPointerException If the source is null.
     * @throws IllegalArgumentException If the source is empty.
     */
    public String encode(final String source) {
        if(source == null) throw new NullPointerException("The string to encode can not be null");
        if(source.trim().isEmpty()) throw new IllegalArgumentException("The string to encode can not be empty");

        final String encodedString = this.encoder.encodeToString(source.getBytes(StandardCharsets.UTF_8));

        copyToClipboard(encodedString);

        displayMessage(Level.INFO, "Encoded value copied to the clipboard");

        return encodedString;
    }

    /**
     * Copy the {@code content} as plain text in the system clipboard.
     * @param content The content to copy to the clipboard.
     */
    private void copyToClipboard(final String content) {
        final Map<DataFormat, Object> data = new HashMap<>();
        data.put(DataFormat.PLAIN_TEXT, content);

        Clipboard.getSystemClipboard().setContent(data);
    }

    /**
     * Display a message in the UI. If the {@code level} is equal to {@code Level.INFO}, the message will be displayed
     * as an information message, for all other values it will be displayed as a warning message.
     * @param level The level of the message to display.
     * @param message The message to display.
     */
    private void displayMessage(final Level level, final String message) {

        final Label messageLabel = new Label(message);
        messageLabel.setOpacity(0);
        messageLabel.getStyleClass().addAll("message", level == Level.INFO ? "info" : "warning");

        messages.getChildren().add(0, messageLabel);

        final FadeTransition fadeIn = new FadeTransition(Duration.millis(500), messageLabel);
        fadeIn.setToValue(1.0);

        final PauseTransition pause = new PauseTransition(Duration.seconds(5));

        final FadeTransition fadeOut = new FadeTransition(Duration.millis(500), messageLabel);
        fadeOut.setToValue(0);

        SequentialTransition sequentialTransition = new SequentialTransition(fadeIn, pause, fadeOut);
        sequentialTransition.setOnFinished(event -> messages.getChildren().remove(messageLabel));
        sequentialTransition.playFromStart();
    }
}
