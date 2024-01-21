package de.tum.cit.ase.editor.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.tum.cit.ase.maze.utils.CONSTANTS;

public class EditorCanvas implements Disposable {
    private final Editor editor;
    private final ScreenViewport viewport;
    private final Stage stage;
    private final float tileSize = 16f;
    private final Image grid;
    private final Container<Image> gridContainer;
    private final TiledDrawable gridDrawable;
    private float width = 16f, height = 16f;


    public EditorCanvas(Editor editor) {
        this.editor = editor;
        this.viewport = new ScreenViewport();
        this.stage = new Stage(this.viewport, this.editor.getGame().getSpriteBatch());
        this.stage.setDebugAll(CONSTANTS.DEBUG);
        this.viewport.apply(true);
        Texture gridTex = new Texture("Editor/grid.png");
        this.gridDrawable = new TiledDrawable(new TextureRegion(gridTex));
        this.grid = new Image(gridDrawable);
        gridContainer = new Container<>(this.grid);
        gridContainer.align(Align.center).size(tileSize * width, tileSize * height);
        gridContainer.setActor(grid);
        gridContainer.setFillParent(true);
        gridContainer.fill(true);
        this.stage.addActor(gridContainer);


    }

    public void setSize(float size) {
        this.setSize(size, size);

    }

    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
        //this.grid.setSize(tileSize * width, tileSize * height);
        this.gridContainer.size(tileSize * width, tileSize * height);
        gridContainer.layout();


    }

    public void render(float dt) {
        viewport.apply();
        stage.draw();
    }

    public void update(float dt) {
        this.updateCamera(dt);
        stage.act(dt);
    }

    private void updateCamera(float dt) {
        this.viewport.getCamera().update();
    }

    public void resize(int width, int height) {
        this.viewport.update(width, height, true);
    }

    public void move(float x, float y, float z) {
        this.viewport.getCamera().translate(x, y, 0);
        var zoomBef = ((OrthographicCamera) this.viewport.getCamera()).zoom;

        ((OrthographicCamera) this.viewport.getCamera()).zoom = MathUtils.clamp(zoomBef + z * 0.1f, 0.1f, 2f);
        zoomBef = ((OrthographicCamera) this.viewport.getCamera()).zoom;
        if (z < 0 && zoomBef <= 2f) {

            var mp = getMousePosition();

            mp.y = Gdx.graphics.getHeight() - mp.y;  // flip the y-coordinate
            Gdx.app.debug("NewPos", mp + " : " + new Vector2(Gdx.input.getX(), Gdx.input.getY()) + " : " + getCameraPosition());

            this.viewport.getCamera().position.interpolate(new Vector3(mp, 0), zoomBef / 2, Interpolation.exp10In);
        }
        this.viewport.apply();
    }

    public Vector2 getMousePosition() {
//        var mp = this.viewport.getCamera().project(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
//        Gdx.app.debug("Scrolled", mp + " : " + new Vector2(Gdx.input.getX(), Gdx.input.getY()));

        return new Vector2(Gdx.input.getX(), Gdx.input.getY());
    }

    public Vector2 getCameraPosition() {
        return new Vector2(this.viewport.getCamera().position.x, this.viewport.getCamera().position.y);
    }

    @Override
    public void dispose() {
        this.stage.dispose();
    }
}
