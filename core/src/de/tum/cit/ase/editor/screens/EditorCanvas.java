package de.tum.cit.ase.editor.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

        OrthographicCamera camera = (OrthographicCamera) viewport.getCamera();
        this.viewport.getCamera().translate(x, y, 0);
        float oldZ = camera.zoom;
        float amount = z * 0.1f;
        float halfScreenWidth = viewport.getScreenWidth() / 2f;
        float halfScreenHeight = viewport.getScreenHeight() / 2f;
        var mousePosition = getMousePosition();

        // Zoom
        camera.zoom += amount;

        // Zoom Boundaries
        float minZoom = .1f;
        float maxZoom = 2f;
        camera.zoom = MathUtils.clamp(camera.zoom + z * 0.1f, minZoom, maxZoom);

        // Move to cursor position
        Vector3 target = new Vector3((mousePosition.x - halfScreenWidth), (-mousePosition.y + halfScreenHeight), 0).add(camera.position);
        var alpha = (oldZ - camera.zoom);
        camera.position.lerp(target, alpha);


        // Bound camera
        float viewHalfWidth = camera.zoom * (camera.viewportWidth / 2); // half viewport width
        float viewHalfHeight = camera.zoom * (camera.viewportHeight / 2); // half viewport height

        float gridWidth = tileSize * width;
        float gridHeight = tileSize * height;

// Clamping x-position
        float minX = grid.getX() - viewHalfWidth / viewHalfWidth / gridWidth; // let the left edge of the grid/canvas move toward the center of viewport
        float maxX = grid.getX() + gridWidth + viewHalfWidth / viewHalfWidth / gridWidth; // let the right edge of the grid/canvas move toward the center of viewport
        camera.position.x = MathUtils.clamp(camera.position.x, minX, maxX);

// clamping y-position
        float minY = grid.getY() - viewHalfHeight / viewHalfHeight / gridHeight; // let the bottom edge of the grid/canvas move toward the center of viewport
        float maxY = grid.getY() + gridHeight + viewHalfHeight / viewHalfHeight / gridHeight; // let the top edge of the grid/canvas move toward the center of viewport
        camera.position.y = MathUtils.clamp(camera.position.y, minY, maxY);

    }

    public Vector2 getMousePosition() {
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
