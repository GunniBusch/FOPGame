package de.tum.cit.ase.editor.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.tum.cit.ase.editor.data.EditorConfig;
import de.tum.cit.ase.editor.tools.*;
import de.tum.cit.ase.editor.utlis.TileTypes;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.Map;

public class EditorUi extends Stage {
    private final Editor editor;
    private final Skin skin;
    private final HorizontalGroup menuPopups;

    public EditorUi(Editor editor) {
        super(new ScreenViewport(), editor.getGame().getSpriteBatch());
        this.editor = editor;
        this.skin = new Skin(Gdx.files.internal("Editor/skincomposerui/skin-composer-ui.json"));
        var vGroup = new VerticalGroup();
        vGroup.setFillParent(true);
        vGroup.setOrigin(Align.topLeft);
        vGroup.columnAlign(Align.topLeft);
        vGroup.align(Align.topLeft);

        // Menu
        var taskBar = new HorizontalGroup();
        vGroup.addActor(taskBar);

        this.addActor(vGroup);

        menuPopups = new HorizontalGroup();


        vGroup.addActor(menuPopups);
        var filePopUpMap = new HashMap<String, EventListener>();
        filePopUpMap.put("Save", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (event.getListenerActor() instanceof TextButton textButton) {
                    save();
                    textButton.getButtonGroup().uncheckAll();
                    textButton.getParent().setVisible(false);
                }
            }

        });
        filePopUpMap.put("Open", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (event.getListenerActor() instanceof TextButton textButton) {
                    open();
                    textButton.getButtonGroup().uncheckAll();
                    textButton.getParent().setVisible(false);

                }
            }

        });
        filePopUpMap.put("Exit", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (event.getListenerActor() instanceof TextButton textButton) {
                    textButton.getButtonGroup().uncheckAll();
                    textButton.getParent().setVisible(false);
                    EditorUi.this.exit();
                }
            }

        });

        addMenuItem(skin, null, "file", "File", taskBar, menuPopups, filePopUpMap);
        addMenuItem(skin, null, "file", "Info", taskBar, menuPopups, new HashMap<>());

        this.createToolBar();
        this.createTileBar();
        this.setDebugInvisible(true);

    }

    protected void save() {

        Gdx.app.error("Save file", "Could not save file");
    }

    protected void open() {
        Gdx.app.error("Open file", "Could not open file");


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

    protected void addMenuItem(Skin skin,
                               @Nullable String styleNameMenuButton,
                               @Nullable String styleNameExpandButtons,
                               String menuButtonName,
                               HorizontalGroup menuGroup,
                               HorizontalGroup expandableGroup,
                               Map<String, @Nullable EventListener> expandableButonNamesEventListenerMap) {


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

        for (Map.Entry<String, EventListener> menuItemEntry : expandableButonNamesEventListenerMap.entrySet()) {

            TextButton menuItemButton;
            if (styleNameExpandButtons == null) {
                menuItemButton = new TextButton(menuItemEntry.getKey(), skin);

            } else {
                menuItemButton = new TextButton(menuItemEntry.getKey(), skin, styleNameExpandButtons);

            }
            if (menuItemEntry.getValue() != null) {
                menuItemButton.addListener(menuItemEntry.getValue());

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
        for (Actor child : this.menuPopups.getChildren()) {
            child.setVisible(false);
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);

    }
}
