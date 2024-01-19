package de.tum.cit.ase.maze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.tum.cit.ase.maze.objects.ObjectType;
import de.tum.cit.ase.maze.objects.dynamic.Player;
import de.tum.cit.ase.maze.objects.still.Wall;
import de.tum.cit.ase.maze.utils.MapLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.tum.cit.ase.maze.utils.CONSTANTS.PPM;
import static de.tum.cit.ase.maze.utils.CONSTANTS.SCALE;

public class MiniMap implements Disposable {
    private final double relationToSize = 0.3;
    private final Viewport viewport;
    private final GameScreen game;
    private final SpriteBatch spriteBatch;
    private final Map<TileType, List<Vector2>> visited;
    private final Map<TileType, List<Vector2>> notVisited;
    private final Player player;
    private final float aspactRatio = MapLoader.width / MapLoader.height;
    private final Texture texture = new Texture("basictiles.png");
    private final TextureRegion textureRegion = new TextureRegion(texture, 16, 0, 16, 16);

    public MiniMap(GameScreen game, SpriteBatch spriteBatch, Player player) {
        this.game = game;
        this.spriteBatch = spriteBatch;
        this.player = player;
        this.notVisited = new HashMap<>();
        this.notVisited.put(TileType.Wall, MapLoader.getMapCoordinates(ObjectType.Wall));
        this.visited = new HashMap<>();
        var temp = new ArrayList<Vector2>();
        for (int i = 0; i < MapLoader.height + 1; i++) {
            for (int j = 0; j < MapLoader.width + 1; j++) {
                temp.add(new Vector2(j, i));
            }
        }

        temp.removeAll(MapLoader.getMapCoordinates(ObjectType.Wall));
        this.notVisited.put(TileType.Path, temp);

        viewport = new FillViewport((MapLoader.width + 1) * Wall.width * SCALE, (MapLoader.height + 1) * Wall.height * SCALE);
// Calculate width and height of the viewport based on the aspect ratio
        int viewportWidth = (int) (Gdx.graphics.getWidth() * relationToSize);
        int viewportHeight = (int) (viewportWidth / aspactRatio);

// Calculate the position of the viewport
        int viewportX = Gdx.graphics.getWidth() - viewportWidth / 2; // Align right
        int viewportY = Gdx.graphics.getHeight() - viewportHeight / 2; // Align bottom

// Set the screen bounds of the viewport
        viewport.setScreenBounds(viewportX - 10, viewportY - 10, viewportWidth, viewportHeight);
    }

    public void render() {
        viewport.apply(true);
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        game.getShapeRenderer().setProjectionMatrix(viewport.getCamera().combined);

        game.getShapeRenderer().begin(ShapeRenderer.ShapeType.Filled);
        for (Map.Entry<TileType, List<Vector2>> visitedEntry : visited.entrySet()) {
            switch (visitedEntry.getKey()) {
                case Wall -> {
                    textureRegion.setRegionX(16);
                    textureRegion.setRegionY(0);
                    game.getShapeRenderer().setColor(1.0f, 1.0f, 1.0f, 1.0f);

                    for (Vector2 position : visitedEntry.getValue()) {
                        game.getShapeRenderer().rect(position.cpy().scl(Wall.width).x, position.cpy().scl(Wall.height).y, Wall.width, Wall.height);

                        //spriteBatch.draw(textureRegion, position.cpy().scl(Wall.width).x, position.cpy().scl(Wall.height).y, Wall.width / SCALE, Wall.height / SCALE);
                    }
                }
                case Path -> {
                    textureRegion.setRegionX(16);
                    textureRegion.setRegionY(16);
                    game.getShapeRenderer().setColor(1.0f, 0.0f, 0.0f, 1.0f);


                    for (Vector2 position : visitedEntry.getValue()) {
                        game.getShapeRenderer().rect(position.cpy().scl(Wall.width).x, position.cpy().scl(Wall.height).y, Wall.width, Wall.height);
                        //spriteBatch.draw(textureRegion, position.cpy().scl(Wall.width).x, position.cpy().scl(Wall.height).y, Wall.width / SCALE, Wall.height / SCALE);
                    }
                }
            }
        }


//        game.getShapeRenderer().setColor(0.1f, 0.453f, 1f, 1f);
        game.getShapeRenderer().setColor(0f, 0f, 0f, 1.0f);

        var plpos = player.getPosition().cpy().scl(PPM);
        game.getShapeRenderer().circle(plpos.x / SCALE, plpos.y / SCALE, Wall.width);
        game.getShapeRenderer().end();


    }

    public void update(float dt) {
        var tempList = new ArrayList<Vector2>();
        float range = 5f;
        for (TileType type : notVisited.keySet()) {
            for (Vector2 vector2 : notVisited.getOrDefault(type, new ArrayList<>())) {
                if ((vector2.cpy().scl(SCALE).x > player.getPosition().x - range && vector2.cpy().scl(SCALE).x < player.getPosition().x + range) && (vector2.cpy().scl(SCALE).y > player.getPosition().y - range && vector2.cpy().scl(SCALE).y < player.getPosition().y + range)) {
                    tempList.add(vector2.cpy());
                }
            }
            notVisited.computeIfPresent(type, (tileType, vector2s) -> {
                vector2s.removeAll(tempList);
                return vector2s;
            });
            // visited.putIfAbsent(type, new ArrayList<>());
            visited.compute(type, (tileType, vector2s) -> {
                if (vector2s == null) {
                    vector2s = new ArrayList<>();
                }
                vector2s.addAll(tempList);
                return vector2s;
            });
            tempList.clear();
        }
    }

    @Override
    public void dispose() {

    }

    public void resize(int width, int height) {
// Calculate width and height of the viewport based on the aspect ratio
        int viewportWidth = (int) (width * relationToSize);
        int viewportHeight = (int) (viewportWidth / aspactRatio);

// Calculate the position of the viewport
        int viewportX = width - viewportWidth / 2; // Align right
        int viewportY = height - viewportHeight / 2; // Align bottom

// Set the screen bounds of the viewport
        viewport.setScreenBounds(viewportX - 10, viewportY - 10, viewportWidth, viewportHeight);
    }

    public Viewport getViewport() {
        return viewport;
    }

    /**
     * Types that can be represented by the miniMap
     */
    public enum TileType {
        Wall, Path
    }
}
