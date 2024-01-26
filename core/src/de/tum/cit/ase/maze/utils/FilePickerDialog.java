package de.tum.cit.ase.maze.utils;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class FilePickerDialog extends Dialog {
    public FilePickerDialog(String title, Skin skin) {
        super(title, skin);
        TextButton closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                hide();
            }
        });
        getContentTable().add(new Label("Select a file:", skin));
        getContentTable().row();
        // Add file selection buttons or list here
        getContentTable().add(closeButton);
    }
}