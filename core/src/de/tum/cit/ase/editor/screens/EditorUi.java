package de.tum.cit.ase.editor.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ray3k.stripe.FreeTypeSkin;
import com.ray3k.stripe.StripeMenuBar;
import de.tum.cit.ase.editor.data.EditorConfig;
import de.tum.cit.ase.editor.data.Map;
import de.tum.cit.ase.editor.input.Shortcuts;
import de.tum.cit.ase.editor.tools.*;
import de.tum.cit.ase.editor.utlis.MapGenerator;
import de.tum.cit.ase.editor.utlis.TileTypes;
import de.tum.cit.ase.maze.utils.CONSTANTS;
import games.spooky.gdx.nativefilechooser.NativeFileChooserCallback;
import games.spooky.gdx.nativefilechooser.NativeFileChooserIntent;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.Field;
import java.util.Objects;

public class EditorUi extends Stage {
    private final Editor editor;
    private final Skin skin;

    public EditorUi(Editor editor) {
        super(new ScreenViewport(), editor.getGame().getSpriteBatch());
        this.editor = editor;
        this.skin = new Skin(Gdx.files.internal("Editor/skincomposerui/skin-composer-ui.json"));

        // Menu
        var taskBar = new Container<>();
        taskBar.setFillParent(true);
        taskBar.align(Align.topLeft);

        var mBar = new StripeMenuBar(this, new FreeTypeSkin(Gdx.files.internal("skin-composer-ui/skin-composer-ui.json")));
        mBar.setDebug(CONSTANTS.DEBUG, true);
        taskBar.setActor(mBar);

        var settingsWindow = new Window("Settings", skin);
        settingsWindow.setVisible(false);
        settingsWindow.setPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
        settingsWindow.setKeepWithinStage(true);

        //settingsWindow.setColor(0, 0, 0, .7f);
        settingsWindow.setLayoutEnabled(true);
        settingsWindow.setResizable(true);
        settingsWindow.setSize(400, 300);
        try {
            this.addCheckBoxSetting("Check if exit can be reached", EditorConfig.class.getDeclaredField("exportCheckCanReachExit"), settingsWindow);
            settingsWindow.row();
            this.addCheckBoxSetting("Check if exit exists", EditorConfig.class.getDeclaredField("exportCheckHasExit"), settingsWindow);
            settingsWindow.row();
            this.addCheckBoxSetting("Check if key exists", EditorConfig.class.getDeclaredField("exportCheckHasKey"), settingsWindow);
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
                EditorConfig.loadedMapProject = null;
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
        barMenu = mBar.menu("Project");
        barMenu.item("Test map", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                editor.testGame(new Map("test", editor.getEditorCanvas().getCanvas().virtualGrid));
            }
        });

        barMenu = mBar.menu("Info");
        barMenu.item("About", new ClickListener());


        this.createToolBar();
        this.createTileBar();
        this.addActor(taskBar);
        this.setDebugInvisible(true);

    }

    public void addCheckBoxSetting(String text, Field EditorConfigFieldToChange, Window window) {
        var settingsBox = new CheckBox(text, skin);
        settingsBox.getLabel().setColor(0, 0, 0, 1);
        try {
            settingsBox.setChecked(EditorConfigFieldToChange.getBoolean(null));
            settingsBox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (event.getListenerActor() instanceof CheckBox checkBox) {
                        try {
                            EditorConfigFieldToChange.set(null, checkBox.isChecked());
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

    protected void save() {
        this.save(false);
    }

    protected void save(boolean saveAs) {

        if (saveAs || EditorConfig.loadedMapProject == null) {
            String fileFilter = "MapProject/mapproj";

            var fileCallback = new NativeFileChooserCallback() {

                @Override
                public void onFileChosen(FileHandle file) {
                    MapGenerator.saveMapProject(file, new de.tum.cit.ase.editor.data.Map(file.nameWithoutExtension(), editor.getEditorCanvas().getCanvas().virtualGrid));
                    EditorConfig.loadedMapProject = file;
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
        }
        EditorConfig.saveSettings();

    }

    protected void open() {

        var fileFilter = "MapProject/mapproj";
        var callback = new NativeFileChooserCallback() {

            @Override
            public void onFileChosen(FileHandle file) {
                var map = MapGenerator.readMapProject(file);
                editor.getEditorCanvas().loadMap(map);
                EditorConfig.loadedMapProject = file;

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

    protected void exportMap() {
        var fileFilter = "Map/properties";
        var callback = new NativeFileChooserCallback() {

            @Override
            public void onFileChosen(FileHandle file) {
                MapGenerator.exportMap(new Map(file.nameWithoutExtension(), editor.getEditorCanvas().getCanvas().virtualGrid), file);
            }

            @Override
            public void onCancellation() {

            }

            @Override
            public void onError(Exception exception) {
                Gdx.app.error("Export map", "Error exporting map", exception);

            }
        };

        var defName = EditorConfig.loadedMapProject == null ? "untitledMap" : EditorConfig.loadedMapProject.nameWithoutExtension();
        this.editor.chooseFile(fileFilter, defName, NativeFileChooserIntent.SAVE, EditorConfig.loadedMapProject, callback);
    }


    @Override
    public void draw() {
        this.getViewport().apply(true);
        super.draw();
    }

    public void resize(int width, int height) {
        this.getViewport().update(width, height, true);
    }

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
        this.addActor(toolGroup);

        EditorConfig.selectedTool = ToolManager.getTool(Pen.class, editor.getEditorCanvas().getCanvas().virtualGrid, editor.getEditorCanvas().getCanvas());


    }

    protected void addMenuItem(Skin skin, @Nullable String styleNameMenuButton, @Nullable String styleNameExpandButtons, String menuButtonName, HorizontalGroup menuGroup, HorizontalGroup expandableGroup, OrderedMap<String, @Nullable EventListener> expandableButonNamesEventListenerMap) {


        TextButton button;
        var buttonGroup = new ButtonGroup<TextButton>();
        var popupGroup = new VerticalGroup();

        popupGroup.fill();
        popupGroup.setVisible(false);
        buttonGroup.setMinCheckCount(0);
        buttonGroup.setMaxCheckCount(1);

        // Create Menu Button
        if (styleNameMenuButton != null) {
            button = new TextButton(menuButtonName, skin, styleNameMenuButton);

        } else {
            button = new TextButton(menuButtonName, skin);
        }
        button.setUserObject(popupGroup);
        menuGroup.addActor(button);


        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (event.getListenerActor().getUserObject() instanceof Actor popup) {
                    var visPop = popup.isVisible();
                    hideAllPopups();
                    popup.setVisible(!visPop);

                }
            }
        });

        for (ObjectMap.Entry<String, @Nullable EventListener> menuItemEntry : expandableButonNamesEventListenerMap.entries()) {

            TextButton menuItemButton;
            if (styleNameExpandButtons == null) {
                menuItemButton = new TextButton(menuItemEntry.key, skin);

            } else {
                menuItemButton = new TextButton(menuItemEntry.key, skin, styleNameExpandButtons);

            }
            if (menuItemEntry.value != null) {
                menuItemButton.addListener(menuItemEntry.value);

            }


            popupGroup.addActor(menuItemButton);
            buttonGroup.add(menuItemButton);

        }


        expandableGroup.addActor(popupGroup);

    }

    protected void exit() {
        editor.exit();
    }

    public void hideAllPopups() {

    }

    @Override
    public void act(float delta) {
        super.act(delta);

    }
}
