/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.extjfx.samples.chart;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;

abstract class AbstractSamplePane extends BorderPane {

    AbstractSamplePane() {
        this.init();
    }

    private void init() {
        final Label titleLabel = new Label(this.getDescription());
        titleLabel.setStyle("-fx-border-color: grey");
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        this.setTop(titleLabel);

        final SplitPane centerPane = new SplitPane(this.createSamplePane());
        final Node controlPane = this.createControlPane();
        if (controlPane != null) {
            centerPane.getItems().add(controlPane);
            centerPane.setDividerPositions(0.65);
        }
        this.setCenter(centerPane);
    }

    public abstract String getName();

    public abstract String getDescription();

    protected abstract Node createSamplePane();

    protected Node createControlPane() {
        return null;
    }
}
