package de.tum.cit.ase.editor.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
import de.tum.cit.ase.editor.utlis.TileTypes;
import de.tum.cit.ase.maze.utils.CONSTANTS;
import org.checkerframework.checker.nullness.qual.Nullable;

public class EditorCanvas implements Disposable {
    private final Editor editor;
    private final ScreenViewport viewport;
    private final Stage stage;
    private final float tileSize = 16f;
    private final Image grid;
    private final Container<Image> gridContainer;
    private final TiledDrawable gridDrawable;
    private float width = 16f, height = 16f;
    private TileTypes[][] virtualGrid;
    private @Nullable Vector2 mouseGridPos;


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
        this.virtualGrid = new TileTypes[(int) (width) + 1][(int) (height) + 1];


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
        this.virtualGrid = new TileTypes[(int) (width) + 1][(int) (height) + 1];


    }

    public void render(float dt) {
        viewport.apply();
        stage.draw();


        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        editor.shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

        var c1 = new Color(.5f, .5f, .5f, 1f);
        var c2 = new Color(1f, 1f, 1f, .2f);

        editor.shapeRenderer.setAutoShapeType(true);
        editor.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        renderGrid();
        if (mouseGridPos != null) {
            editor.shapeRenderer.setColor(c2);

            editor.shapeRenderer.rect(mouseGridPos.x * tileSize + 1 + grid.getX(), mouseGridPos.y * tileSize + 1 + grid.getY(), tileSize - 2, tileSize - 2);

            editor.shapeRenderer.set(ShapeRenderer.ShapeType.Line);
            editor.shapeRenderer.setColor(c1);
            editor.shapeRenderer.rect(mouseGridPos.x * tileSize + grid.getX(), mouseGridPos.y * tileSize + grid.getY(), tileSize, tileSize);
        }
        editor.shapeRenderer.end();
        editor.shapeRenderer.setAutoShapeType(false);
        Gdx.gl.glDisable(GL20.GL_BLEND);


    }

    private void renderGrid() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                if (virtualGrid[y][x] != null) {
                    editor.shapeRenderer.setColor(virtualGrid[y][x].canvasColor);
                    editor.shapeRenderer.rect(x * tileSize + 1 + grid.getX(), y * tileSize + 1 + grid.getY(), tileSize - 2, tileSize - 2);
                }

            }
        }

    }

    public void update(float dt) {
        this.updateCamera(dt);


        this.updateMouseGridPosition(viewport.unproject(getMousePosition()));
        stage.act(dt);
    }

    private void updateCamera(float dt) {
        this.viewport.getCamera().update();
    }

    public void resize(int width, int height) {
        this.viewport.update(width, height, true);
    }

    private void updateMouseGridPosition(Vector2 position) {


        var gridWidth = tileSize * width;
        var gridHeight = tileSize * height;

        var minX = grid.getX();
        var maxX = minX + gridWidth;

        var minY = grid.getY();
        var maxY = minY + gridHeight;

        if (position.x > maxX || position.x < minX) {
            this.mouseGridPos = null;
            return;
        }
        if (position.y > maxY || position.y < minY) {
            this.mouseGridPos = null;
            return;
        }


        Vector2 vector = new Vector2((position.x - grid.getX()) / tileSize, (position.y - grid.getY()) / tileSize);

        vector.x = (float) Math.floor(vector.x);
        vector.y = (float) Math.floor(vector.y);

        this.mouseGridPos = vector;


    }

    private void setGridTile(int x, int y, TileTypes tileType) {
        virtualGrid[x][y] = tileType;
    }

    public boolean processMouseInput(float x, float y, int button) {
        switch (button) {
            case Input.Buttons.LEFT -> {
                var mousePos = viewport.unproject(new Vector2(x, y));
                this.updateMouseGridPosition(mousePos);
                if (mouseGridPos != null) {
                    markDraw(mouseGridPos);
                }
            }
            case Input.Buttons.RIGHT -> {
                break;
            }
        }
        return true;
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

    private void markDraw(Vector2 position) {

        this.virtualGrid[(int) position.y][(int) position.x] = this.editor.getActiveTool();

    }

    public Vector2 getMousePosition() {
        return new Vector2(Gdx.input.getX(), Gdx.input.getY());
    }

    public Vector2 getCameraPosition() {
        return new Vector2(this.viewport.getCamera().position.x, this.viewport.getCamera().position.y);

    }

    public Editor getEditor() {
        return editor;
    }

    @Override
    public void dispose() {
        this.stage.dispose();
    }

}
