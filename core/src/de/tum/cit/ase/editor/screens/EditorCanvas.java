package de.tum.cit.ase.editor.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.tum.cit.ase.editor.data.EditorConfig;
import de.tum.cit.ase.editor.data.Map;
import de.tum.cit.ase.editor.drawing.Canvas;
import de.tum.cit.ase.editor.utlis.Helper;
import de.tum.cit.ase.editor.utlis.MapGenerator;
import de.tum.cit.ase.editor.utlis.TileTypes;
import de.tum.cit.ase.maze.utils.CONSTANTS;

/**
 * The EditorCanvas class represents a canvas for editing game maps.
 * It provides methods for rendering and updating the canvas, as well as handling user input.
 * The canvas is responsible for displaying the grid and tiles of the map.
 * <p>
 * The EditorCanvas class implements the Disposable interface, allowing it to be disposed
 * when it is no longer needed.
 */
public class EditorCanvas implements Disposable {
    private final Editor editor;
    private final ScreenViewport viewport;
    private final Stage stage;
    private final float tileSize = 16f;
    private final Image grid;
    private final Container<Image> gridContainer;
    private final TiledDrawable gridDrawable;
    private final Canvas canvas;
    private float width = 16f, height = 16f;
    private GridPoint2 mouseGridPos;
    public boolean isBigZoom = false;


    /**
     * Creates an instance of EditorCanvas.
     *
     * @param editor The Editor instance.
     */
    public EditorCanvas(Editor editor) {
        this.canvas = new Canvas(this);
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
        this.canvas.virtualGrid = new TileTypes[(int) (width)][(int) (height)];

        if (EditorConfig.loadPreviousProject) {
            if (EditorConfig.loadedMapProject != null) {
                this.loadMap(MapGenerator.readMapProject(EditorConfig.loadedMapProject));
            }
        }

    }

    public void resizeCanvas(float size) {
        this.resizeCanvas(size, size);

    }

    /**
     * Resizes the canvas to the specified width and height.
     *
     * @param width  the new width of the canvas
     * @param height the new height of the canvas
     */
    public void resizeCanvas(float width, float height) {
        this.width = width;
        this.height = height;
        //this.grid.resizeCanvas(tileSize * width, tileSize * height);
        this.gridContainer.size(tileSize * width, tileSize * height);
        gridContainer.layout();
        this.canvas.createNewGrid((int) width, (int) height);
    }

    /**
     * Renders the canvas by applying the viewport and drawing the stage.
     * Also renders the grid and the selected tool on the canvas.
     *
     * @param dt the time elapsed since the last frame
     */
    public void render(float dt) {
        viewport.apply();
        stage.draw();


        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        editor.shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

        var c1 = new Color(.5f, .5f, .5f, 1f);
//        var c2 = new Color(1f, 1f, 1f, .2f);
        var c2 = new Color(0f, 0, 0, .9f);


        editor.shapeRenderer.setAutoShapeType(true);
        editor.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        this.canvas.draw(editor.shapeRenderer);
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


    public Image getGrid() {
        return grid;
    }

    /**
     * Updates the canvas and stage.
     *
     * @param dt the time difference between the current frame and the previous frame
     */
    public void update(float dt) {
        this.updateCamera(dt);


        this.updateMouseGridPosition(viewport.unproject(getMousePosition()));

        this.canvas.update(dt);
        stage.act(dt);
    }

    /**
     * Loads a {@link Map} into the editor canvas.
     *
     * @param map the Map object representing the map to be loaded
     */
    public void loadMap(Map map) {
        var grid = map.map();
        height = grid.length;
        width = grid[0].length;
        this.canvas.createNewGrid(grid);
        this.gridContainer.size(tileSize * width, tileSize * height);
        gridContainer.layout();


    }

    private void updateCamera(float dt) {
        this.viewport.getCamera().update();
    }

    private void updateMouseGridPosition(Vector2 position) {
        this.mouseGridPos = getMouseGridPosition(position);
    }

    public void resize(int width, int height) {
        this.viewport.update(width, height, true);
    }

    @Deprecated
    public boolean processMouseInput(float x, float y, int button) {

        return true;
    }

    /**
     * Retrieves the grid position of the mouse.
     *
     * @param position the mouse position in world coordinates
     * @return the grid position of the mouse as a GridPoint2 object, or null if the mouse position is outside the grid
     */
    private GridPoint2 getMouseGridPosition(Vector2 position) {
        return getMouseGridPosition(position, false);
    }

    /**
     * Retrieves the grid position of the mouse.
     *
     * @param position   the mouse position in world coordinates
     * @param alwaysGrid specifies whether the mouse position should always be restricted to the grid
     * @return the grid position of the mouse as a GridPoint2 object, or null if the mouse position is outside the grid
     */
    public GridPoint2 getMouseGridPosition(Vector2 position, boolean alwaysGrid) {


        var gridWidth = tileSize * width;
        var gridHeight = tileSize * height;

        var minX = grid.getX();
        var maxX = minX + gridWidth;

        var minY = grid.getY();
        var maxY = minY + gridHeight;

        if (!alwaysGrid) {
            if (position.x > maxX || position.x < minX) {

                return null;
            }
            if (position.y > maxY || position.y < minY) {

                return null;
            }
        } else {
            position.x = MathUtils.clamp(position.x, minX, maxX);
            position.y = MathUtils.clamp(position.y, minY, maxY);
        }


        Vector2 vector = new Vector2(((position.x - grid.getX()) / tileSize), ((position.y - grid.getY()) / tileSize));

        vector.x = MathUtils.clamp((int) (double) vector.x, 0, width - 1);
        vector.y = MathUtils.clamp((int) (double) vector.y, 0, height - 1);

        return Helper.convertVector2ToGridPoint(vector);


    }

    public ScreenViewport getViewport() {
        return viewport;
    }


    public float getScreenTileSize() {
        return tileSize * ((OrthographicCamera) viewport.getCamera()).zoom;
    }


    private void setGridTile(int x, int y, TileTypes tileType) {
        canvas.setGridTile(x, y, tileType);
    }

    /**
     * Moves the camera in the editor canvas.
     *
     * @param x the amount to move the camera in the x-axis
     * @param y the amount to move the camera in the y-axis
     * @param z the amount to zoom the camera
     */
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
        float calcZoom = (Math.max(width * tileSize, height * tileSize) * 2f) / Math.max(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        float maxZoom = Math.max(calcZoom, 2);
        camera.zoom = MathUtils.clamp(camera.zoom + z * 0.1f, minZoom, maxZoom);

        if (camera.zoom > 3) {
            this.grid.setDrawable(new TextureRegionDrawable(new Texture("Editor/backGrid.png")));
            isBigZoom = true;
        } else {
            this.grid.setDrawable(gridDrawable);
            isBigZoom = false;
        }

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

    public float getTileSize() {
        return tileSize;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Editor getEditor() {
        return editor;
    }

    @Override
    public void dispose() {
        this.stage.dispose();
    }

}
