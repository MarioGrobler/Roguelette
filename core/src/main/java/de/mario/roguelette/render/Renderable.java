package de.mario.roguelette.render;

public interface Renderable {

    /**
     * Renders whatever the implementing renderer visualizes
     */
    void render();

    /**
     * @return true if the coordinate (x, y) is within the rendered bounds
     */
    boolean contains(float x, float y);
}
