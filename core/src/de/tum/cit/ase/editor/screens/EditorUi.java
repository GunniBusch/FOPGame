package de.tum.cit.ase.editor.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ray3k.stripe.FreeTypeSkin;
import com.ray3k.stripe.Spinner;
import com.ray3k.stripe.StripeMenuBar;
import de.tum.cit.ase.editor.data.EditorConfig;
import de.tum.cit.ase.editor.data.Map;
import de.tum.cit.ase.editor.input.Shortcuts;
import de.tum.cit.ase.editor.tools.*;
import de.tum.cit.ase.editor.utlis.MapGenerator;
import de.tum.cit.ase.editor.utlis.TileTypes;
import de.tum.cit.ase.editor.utlis.exceptions.InvalidMapFile;
import de.tum.cit.ase.maze.utils.CONSTANTS;
import games.spooky.gdx.nativefilechooser.NativeFileChooserCallback;
import games.spooky.gdx.nativefilechooser.NativeFileChooserIntent;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.function.BooleanSupplier;

/**
 * The EditorUi class is responsible for managing the user interface of the editor screen in the Maze Runner game.
 * It extends Stage.
 */
public class EditorUi extends Stage {
    private final Editor editor;
    private final Skin skin;

    /**
     * Initializes the EditorUi with the given Editor.
     *
     * @param editor the Editor instance to be used
     */
    public EditorUi(Editor editor) {
        super(new ScreenViewport(), editor.getGame().getSpriteBatch());
        this.editor = editor;
//        this.skin = new Skin(Gdx.files.internal("Editor/skincomposerui/skin-composer-ui.json"));
        this.skin = new FreeTypeSkin(Gdx.files.internal("skin-composer-ui/skin-composer-ui.json"));
        // Menu
        var taskBar = new Container<>();
        taskBar.setFillParent(true);
        taskBar.align(Align.topLeft);

        var mBar = new StripeMenuBar(this, skin, "main");
        mBar.setDebug(CONSTANTS.DEBUG, true);
        taskBar.setActor(mBar);

        var settingsWindow = new Window("Settings", skin);
        settingsWindow.setVisible(false);
        settingsWindow.setPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
        settingsWindow.setKeepWithinStage(true);

        settingsWindow.setLayoutEnabled(true);
        settingsWindow.setResizable(true);
        settingsWindow.setSize(400, 300);
        try {
            this.addCheckBoxSetting("Check if exit exists", EditorConfig.class.getDeclaredField("exportCheckHasExit"), settingsWindow, "Check if exit can be reached");
            settingsWindow.row();
            this.addCheckBoxSetting("Check if exit can be reached", EditorConfig.class.getDeclaredField("exportCheckCanReachExit"), settingsWindow);
            settingsWindow.row();
            this.addCheckBoxSetting("Check if key exists", EditorConfig.class.getDeclaredField("exportCheckHasKey"), settingsWindow, "Check if key can be reached");
            settingsWindow.row();
            this.addCheckBoxSetting("Check if key can be reached", EditorConfig.class.getDeclaredField("exportCheckCanReachKey"), settingsWindow);
            settingsWindow.row();
            this.addCheckBoxSetting("Load previous project", EditorConfig.class.getDeclaredField("loadPreviousProject"), settingsWindow);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        settingsWindow.row().fillY();

        var closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (event.getListenerActor() instanceof TextButton textButton) {
                    textButton.getParent().setVisible(false);
                    EditorConfig.saveSettings();
                    editor.handleLostUiFocus();
                }
            }
        });
        settingsWindow.add(closeButton).align(Align.bottom);
        settingsWindow.validate();
        this.addActor(settingsWindow);

        var barMenu = mBar.menu("File");

        mBar.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                mBar.findMenu("File").findButton("Save As").setDisabled(EditorConfig.loadedMapProject == null);
            }
        });
        barMenu.item("New", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                mBar.findMenu("File").findButton("Save As").setDisabled(true);
                checkAndRun(() -> editor.saved, EditorUi.this::createNewGrid, "create a new project", "Save", () -> EditorUi.this.save(true));
            }
        });
        barMenu.item("Save", new StripeMenuBar.KeyboardShortcut(Shortcuts.UI.SAVE.toString(), Shortcuts.UI.SAVE.key(), Objects.requireNonNull(Shortcuts.UI.SAVE.modKeys())), new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                save();
            }
        });
        barMenu.item("Save As", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                save(true);
            }
        });
        barMenu.item("Open", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                open();
            }
        });
        barMenu.item("Import", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                EditorUi.this.importMap();
            }
        });
        barMenu.item("Export", new StripeMenuBar.KeyboardShortcut(Shortcuts.UI.EXPORT.toString(), Shortcuts.UI.EXPORT.key(), Objects.requireNonNull(Shortcuts.UI.EXPORT.modKeys())), new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                EditorUi.this.exportMap();
            }

        });
        barMenu.item("Settings", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                settingsWindow.setVisible(true);
            }
        });
        barMenu.item("Exit", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                EditorConfig.loadedMapProject = null;
                EditorUi.this.exit();
            }
        });


        barMenu.findButton("Save As").setDisabled(true);

        barMenu = mBar.menu("Edit");
        barMenu.item("Undo", new StripeMenuBar.KeyboardShortcut(Shortcuts.UI.UNDO.toString(), -1, -1), new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    Gdx.app.debug("Undo", "und");
                    editor.getEditorCanvas().getCanvas().undo();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        barMenu.item("Redo", new StripeMenuBar.KeyboardShortcut(Shortcuts.UI.REDO.toString(), -1, -1), new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    Gdx.app.debug("Redo", "red");

                    editor.getEditorCanvas().getCanvas().redo();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        barMenu = mBar.menu("Project");
        barMenu.item("Test map", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    editor.testMap(new Map("test", editor.getEditorCanvas().getCanvas().virtualGrid));
                } catch (Exception e) {
                    Gdx.app.error("Test map", "Error testing map", e);
                }
            }
        });

        barMenu = mBar.menu("Help");
        barMenu.item("About", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                EditorUi.this.goToHelp();
            }
        });


        this.createToolBar();
        this.createTileBar();
        this.addActor(taskBar);
        this.setDebugInvisible(true);
    }

    public void addCheckBoxSetting(String text, Field EditorConfigFieldToChange, Window window, String... fieldsDisabledWhenFalse) {

        var settingsBox = new CheckBox(text, skin, "switch");
        settingsBox.setName(text);
        settingsBox.getLabel().setColor(0, 0, 0, 1);
        try {
            settingsBox.setChecked(EditorConfigFieldToChange.getBoolean(null));
            settingsBox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (event.getListenerActor() instanceof CheckBox checkBox) {
                        try {
                            EditorConfigFieldToChange.set(null, checkBox.isChecked());
                            for (String s : fieldsDisabledWhenFalse) {
                                ((CheckBox) window.findActor(s)).setDisabled(!checkBox.isChecked());
                            }
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
        } catch (IllegalAccessException e) {
            settingsBox.setChecked(true);
        }
        window.add(settingsBox).align(Align.left);


    }

    /**
     * Saves the current state. If saveAs is true or the loadedMapProject is null, a native file chooser dialog is shown
     * to choose a file name and location to save the map project. If saveAs is false and loadedMapProject is not null,
     * the map project is saved to the same location.
     */
    protected void save() {
        this.save(false);
    }

    public void checkAndRun(BooleanSupplier check, Runnable runnableOnSuccess, String nameOfAction, String nameOfMiddleOption, Runnable runnableOnFailure) {

        if (!check.getAsBoolean()) {
            Dialog dialog = new Dialog("Warning", skin, "default") {
                public void result(Object obj) {
                    if (obj != null) {
                        if (obj instanceof Runnable runnable) {
                            runnable.run();
                        }
                    }
                }
            };
            dialog.text("Are you sure you want to " + nameOfAction + " ?");
            dialog.getContentTable().row();
            dialog.button("Yes", runnableOnSuccess); //sends "true" as the result
            dialog.button(nameOfMiddleOption, (Runnable) () -> {
                runnableOnFailure.run();
                runnableOnSuccess.run();
            }); //sends "true" as the result
            dialog.button("No", null);  //sends "false" as the result
            dialog.key(Input.Keys.ENTER, runnableOnSuccess); //sends "true" when the ENTER key is pressed
            dialog.show(EditorUi.this);

        } else {
            runnableOnSuccess.run();
        }
    }

    private void createNewGrid() {
        var width = new Spinner(16, 1, false, Spinner.Orientation.HORIZONTAL, skin);
        var height = new Spinner(16, 1, false, Spinner.Orientation.HORIZONTAL, skin);

        Dialog dialog = new Dialog("Create new grid", skin, "default") {
            public void result(Object obj) {
                if (obj != null) {
                    if (obj instanceof Spinner[] spinners) {
                        EditorUi.this.editor.getEditorCanvas().resizeCanvas(spinners[0].getValueAsInt(), spinners[1].getValueAsInt());
                    }
                }
            }
        };
        dialog.getContentTable().pad(10);

        dialog.getContentTable().row();
        var label = new Label("Size: ", skin);

        label.setColor(0, 0, 0, 1);
        dialog.getContentTable().add(label).align(Align.left);
        dialog.getContentTable().row();
        label = new Label("Width: ", skin);
        label.setColor(0, 0, 0, 1);
        dialog.getContentTable().add(label);
        dialog.getContentTable().add(width);
        dialog.getContentTable().row();
        label = new Label("Height: ", skin);
        label.setColor(0, 0, 0, 1);
        dialog.getContentTable().add(label);
        dialog.getContentTable().add(height);

        dialog.button("Apply", new Spinner[]{width, height}); //sends "true" as the result
        dialog.button("Cancel", null);  //sends "false" as the result
        dialog.key(Input.Keys.ENTER, new Spinner[]{width, height}); //sends "true" when the ENTER key is pressed
        dialog.validate();
        dialog.show(EditorUi.this);
    }

    protected void save(boolean saveAs) {

        if (saveAs || EditorConfig.loadedMapProject == null) {
            String fileFilter = "MapProject/mapproj";

            var fileCallback = new NativeFileChooserCallback() {

                @Override
                public void onFileChosen(FileHandle file) {
                    MapGenerator.saveMapProject(file, new de.tum.cit.ase.editor.data.Map(file.nameWithoutExtension(), editor.getEditorCanvas().getCanvas().virtualGrid));
                    EditorConfig.loadedMapProject = file;
                    editor.saved = true;
                }

                @Override
                public void onCancellation() {

                }

                @Override
                public void onError(Exception exception) {
                    Gdx.app.error("Save map project", "Could not save project", exception);
                }
            };
            var defFileName = EditorConfig.loadedMapProject == null ? "untitledMapProject" : EditorConfig.loadedMapProject.nameWithoutExtension();

            this.editor.chooseFile(fileFilter, defFileName, NativeFileChooserIntent.SAVE, EditorConfig.loadedMapProject, fileCallback);
        } else {
            MapGenerator.saveMapProject(EditorConfig.loadedMapProject, new Map(EditorConfig.loadedMapProject.nameWithoutExtension(), editor.getEditorCanvas().getCanvas().virtualGrid));
            editor.saved = true;
        }
        EditorConfig.saveSettings();

    }

    /**
     * Opens a map project file and loads it into the editor canvas.
     * If there is a loaded map project, it will be saved first before opening a new one.
     * <p>
     * File extension filter is set to "MapProject/mapproj".
     *
     * @see MapGenerator#readMapProject(FileHandle)
     * @see Editor#getEditorCanvas()
     * @see EditorCanvas#loadMap(Map)
     * @see EditorConfig#loadedMapProject
     * @see Editor#chooseFile(String, String, NativeFileChooserIntent, FileHandle, NativeFileChooserCallback)
     * @see EditorConfig#saveSettings()
     */
    protected void open() {

        var fileFilter = "MapProject/mapproj";
        var callback = new NativeFileChooserCallback() {

            @Override
            public void onFileChosen(FileHandle file) {
                var map = MapGenerator.readMapProject(file);
                editor.getEditorCanvas().loadMap(map);
                EditorConfig.loadedMapProject = file;
                editor.saved = true;

            }

            @Override
            public void onCancellation() {

            }

            @Override
            public void onError(Exception exception) {
                Gdx.app.error("Load map project", "Error loading project", exception);
            }
        };
        var defFileName = EditorConfig.loadedMapProject == null ? "untitledMapProject" : EditorConfig.loadedMapProject.nameWithoutExtension();
        this.editor.chooseFile(fileFilter, defFileName, NativeFileChooserIntent.OPEN, EditorConfig.loadedMapProject, callback);

        EditorConfig.saveSettings();
    }

    /**
     * Imports a map from a file and loads it into the editor canvas.
     */
    protected void importMap() {
        var fileFilter = "Map/properties";
        var callback = new NativeFileChooserCallback() {

            @Override
            public void onFileChosen(FileHandle file) {
                var map = MapGenerator.importMap(file);
                editor.getEditorCanvas().loadMap(map);
                EditorConfig.loadedMapProject = null;

            }

            @Override
            public void onCancellation() {

            }

            @Override
            public void onError(Exception exception) {
                Gdx.app.error("Import map", "Error importing map", exception);
            }
        };
        var defFileName = EditorConfig.loadedMapProject == null ? "untitledMapProject" : EditorConfig.loadedMapProject.nameWithoutExtension();
        this.editor.chooseFile(fileFilter, defFileName, NativeFileChooserIntent.OPEN, EditorConfig.loadedMapProject, callback);


    }

    /**
     * Export the given map to a file.
     */
    protected void exportMap() {
        var fileFilter = "Map/properties";
        var callback = new NativeFileChooserCallback() {
            private FileHandle file;

            @Override
            public void onFileChosen(FileHandle file) {
                this.file = file;

                MapGenerator.exportMap(new Map(file.nameWithoutExtension(), editor.getEditorCanvas().getCanvas().virtualGrid), file);
            }

            @Override
            public void onCancellation() {

            }

            @Override
            public void onError(Exception exception) {
                Gdx.app.error("Export map", "Error exporting map", exception);

                if (exception instanceof InvalidMapFile) {
                    Dialog dialog = new Dialog("Warning", skin, "default") {
                        public void result(Object obj) {
                            if (((boolean) obj)) {
                                MapGenerator.exportMap(new Map(file.nameWithoutExtension(), editor.getEditorCanvas().getCanvas().virtualGrid), file, false);
                            }
                        }
                    };
                    dialog.text("Are you sure you want to export? Checks did not complete.");
                    dialog.getContentTable().row();
                    var error = new Label("Errors: ", skin);
                    error.setColor(1, 0, 0, 1);
                    dialog.getContentTable().add(error).align(Align.left);
                    dialog.getContentTable().row();
                    error = new Label(exception.getMessage(), skin);
                    error.setColor(1, 0, 0, 1);
                    dialog.text(error);
                    dialog.button("Yes", true); //sends "true" as the result
                    dialog.button("No", false);  //sends "false" as the result
                    dialog.key(Input.Keys.ENTER, true); //sends "true" when the ENTER key is pressed
                    dialog.show(EditorUi.this);
                }


            }
        };

        var defName = EditorConfig.loadedMapProject == null ? "untitledMap" : EditorConfig.loadedMapProject.nameWithoutExtension();
        this.editor.chooseFile(fileFilter, defName, NativeFileChooserIntent.SAVE, EditorConfig.loadedMapProject, callback);
    }

    protected void exit() {
        checkAndRun(() -> editor.saved, editor::exit, "quit", "Save", () -> this.save(true));
    }

    void showMessage(String title, @NonNull String... messages) {
        Dialog dialog = new Dialog(title, skin, "default");
        for (String message : messages) {
            dialog.text(message);
            dialog.row();
        }
        dialog.button("OK"); //sends "true" as the result
        dialog.key(Input.Keys.ENTER, true); //sends "true" when the ENTER key is pressed
        dialog.show(EditorUi.this);
    }

    @Override
    public void draw() {
        this.getViewport().apply(true);
        super.draw();
    }

    public void resize(int width, int height) {
        this.getViewport().update(width, height, true);
    }

    /**
     * Create the toolbar for the editor.
     * The toolbar contains buttons representing different tools. When a tool button is clicked,
     * the corresponding tool is activated and the current tool is updated accordingly.
     */
    protected void createToolBar() {

        VerticalGroup toolGroup = new VerticalGroup();
        toolGroup.align(Align.left);
        toolGroup.setFillParent(true);
        ButtonGroup<ImageButton> toolButtonGroup = new ButtonGroup<>();
        toolButtonGroup.setMaxCheckCount(1);
        toolButtonGroup.setMinCheckCount(1);
        var button = new ImageButton(skin, "pen");
        button.setUserObject(Pen.class);
        ClickListener clickListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (event.getListenerActor() instanceof ImageButton button1 && button1.getUserObject() instanceof Class<?> oClass) {
                    ToolManager.freeTool(EditorConfig.selectedTool);
                    EditorConfig.selectedTool = ToolManager.getTool((Class<? extends EditorTool>) oClass, editor.getEditorCanvas().getCanvas().virtualGrid, editor.getEditorCanvas().getCanvas());
                }
            }
        };
        button.addListener(clickListener);

        toolButtonGroup.add(button);
        toolGroup.addActor(button);

        button = new ImageButton(skin, "eraser");


        button.setUserObject(Eraser.class);


        button.addListener(clickListener);

        toolButtonGroup.add(button);
        toolGroup.addActor(button);
        button = new ImageButton(skin, "square");

        button.setUserObject(Square.class);


        button.addListener(clickListener);

        toolButtonGroup.add(button);
        toolGroup.addActor(button);
        button = new ImageButton(skin, "bucket");

        button.setUserObject(Bucket.class);


        button.addListener(clickListener);

        toolButtonGroup.add(button);
        toolGroup.addActor(button);
        this.addActor(toolGroup);

        EditorConfig.selectedTool = ToolManager.getTool(Pen.class, editor.getEditorCanvas().getCanvas().virtualGrid, editor.getEditorCanvas().getCanvas());


    }

    /**
     * Creates a tile bar with buttons representing different tile types.
     */
    protected void createTileBar() {
        // Tools
        var tileTypeGroup = new ButtonGroup<>();
        var tileBar = new HorizontalGroup();
        tileBar.setFillParent(true);
        tileBar.setOrigin(Align.bottom);
        tileBar.align(Align.bottom);
        for (TileTypes tileType : TileTypes.values()) {
            var but = new TextButton(tileType.getDisplayName(), skin, "file");
            but.setUserObject(tileType);
            but.setColor(tileType.canvasColor);
            but.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (event.getListenerActor() instanceof TextButton button1) {
                        EditorConfig.selectedTile = (TileTypes) button1.getUserObject();
                    }
                }
            });
            tileTypeGroup.add(but);
            tileBar.addActor(but);
        }

        tileTypeGroup.setMinCheckCount(1);
        tileTypeGroup.setMaxCheckCount(1);


        tileTypeGroup.setChecked(TileTypes.Wall.getDisplayName());
        EditorConfig.selectedTile = TileTypes.Wall;


        this.addActor(tileBar);


    }

    private void goToHelp() {
        Gdx.net.openURI("https://github.com/GunniBusch/FOPGame/wiki/Editor");
    }

    public void hideAllPopups() {

    }

    @Override
    public void act(float delta) {
        super.act(delta);

    }

    @Override
    public void dispose() {
        super.dispose();
        this.skin.dispose();
    }
}
