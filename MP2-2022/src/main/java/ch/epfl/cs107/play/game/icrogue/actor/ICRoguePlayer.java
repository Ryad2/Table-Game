package ch.epfl.cs107.play.game.icrogue.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Interactor;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.actor.enemies.Turret;
import ch.epfl.cs107.play.game.icrogue.actor.items.Item;
import ch.epfl.cs107.play.game.icrogue.actor.items.Key;
import ch.epfl.cs107.play.game.icrogue.actor.items.Staff;
import ch.epfl.cs107.play.game.icrogue.actor.projectiles.FireBall;
import ch.epfl.cs107.play.game.icrogue.handler.ICRogueInteractionHandler;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Keyboard;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class ICRoguePlayer extends ICRogueActor implements Interactor {
    private static final int MOVE_DURATION = 8;
    private final Orientation DEFAULT_ORIENTATION;      // not very clean
    private final ICRoguePlayerInteractionHandler handler;


    private int KeyID;
    private boolean collectedStaff;
    private boolean launchFireBall;
    private int fireBallCooldownValue;
    private final int fireBallCooldown = 10;     // 10 is a placeholder value. It needs to be changed later
    private int hitPoints = 3;


    private boolean isCrossing;
    private Connector crossingConnector;


    Map<Orientation, Sprite> sprites = new HashMap<>();

    public ICRoguePlayer(Area owner, Orientation orientation, DiscreteCoordinates coordinates) {

        super(owner, orientation, coordinates);

        DEFAULT_ORIENTATION = orientation;
        createSpriteMAP();

        this.handler = new ICRoguePlayerInteractionHandler();

        collectedStaff = false;
        launchFireBall = false;
        fireBallCooldownValue = 0;
    }


    public Sprite getSprites(Orientation orientation){
        return sprites.get(orientation);
    }

    public boolean getIsCrossing(){
        return isCrossing;
    }

    public Connector getCrossingConnector() {
        return crossingConnector;
    }
    public void resetCrossing(){
        crossingConnector = null;
        isCrossing = false;
    }

    public void createSpriteMAP() {

        Sprite down = new Sprite("zelda/player", .75f, 1.5f, this,
                new RegionOfInterest(0, 0, 16, 32), new Vector(.15f, -.15f));
        Sprite right = new Sprite("zelda/player", .75f, 1.5f, this,
                new RegionOfInterest(0, 32, 16, 32), new Vector(.15f, -.15f));
        Sprite top = new Sprite("zelda/player", .75f, 1.5f, this,
                new RegionOfInterest(0, 64, 16, 32), new Vector(.15f, -.15f));
        Sprite left = new Sprite("zelda/player", .75f, 1.5f, this,
                new RegionOfInterest(0, 96, 16, 32), new Vector(.15f, -.15f));

        sprites.put(Orientation.UP, top);
        sprites.put(Orientation.DOWN, down);
        sprites.put(Orientation.RIGHT, right);
        sprites.put(Orientation.LEFT, left);
    }

    public void resetPlayer(){
        orientate(DEFAULT_ORIENTATION);
        collectedStaff = false;                 // may not be necessary. Maybe constructor is called when player is reset
                                                // to lazy to check
    }


    public void update(float deltaTime) {

        Keyboard keyboard = getOwnerArea().getKeyboard();

        moveIfPressed(Orientation.LEFT, keyboard.get(Keyboard.LEFT));
        moveIfPressed(Orientation.UP, keyboard.get(Keyboard.UP));
        moveIfPressed(Orientation.RIGHT, keyboard.get(Keyboard.RIGHT));
        moveIfPressed(Orientation.DOWN, keyboard.get(Keyboard.DOWN));

        launchFireBallIfPressed(keyboard.get(Keyboard.X));

        sprite = getSprites(getOrientation());

        super.update(deltaTime);


        // player specific actions

        if (fireBallCooldownValue != 0){
            fireBallCooldownValue--;
        }

        if (launchFireBall){
            launchFireBall();
            launchFireBall = false;
            fireBallCooldownValue = fireBallCooldown;
        }
    }

    // Keyboard interactions

    private void moveIfPressed(Orientation orientation, Button b){
        if(b.isDown()) {
            if (!isDisplacementOccurs()) {
                orientate(orientation);
                move(MOVE_DURATION);
            }
        }
    }
    private void launchFireBallIfPressed(Button button){
        if (button.isDown() && collectedStaff && fireBallCooldownValue == 0){
            launchFireBall = true;
        }
    }

    // player actions

    private void launchFireBall(){
        new FireBall(getOwnerArea(), getOrientation(), getCurrentMainCellCoordinates());
    }

    public void takeDamage(){
        hitPoints--;
        System.out.println("ouch " + hitPoints + " hit points left");

        if (hitPoints == 0){
            leaveArea();
            System.out.println("Game Over");
        }
    }

    public void moveToNewRoom(){
        setCurrentPosition(crossingConnector.getTargetCoordinates().toVector());
    }

    // =====              =====
    // =====  Interactor  =====
    // =====              =====

    @Override
    public boolean takeCellSpace(){
        return true;
    }
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return super.getCurrentCells();
    }
    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
    }

    @Override
    public boolean wantsCellInteraction() {
        return true;
    }
    @Override
    public boolean wantsViewInteraction() {
        return keyIsPressed(Keyboard.W);
    }
    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);
    }





    // generalize the usage of this!!!
    // And move on!

    private boolean keyIsPressed(int key){
        return getOwnerArea().getKeyboard().get(key).isDown();
    }







    private class ICRoguePlayerInteractionHandler implements ICRogueInteractionHandler{
        @Override
        public void interactWith(Item item, boolean isCellInteraction){

            if (isCellInteraction && item.isCellInteractable()){
                item.collect();
            }
            else if (item.isViewInteractable()){
                item.collect();
            }
        }

        @Override
        public void interactWith(Staff item, boolean isCellInteraction){

            if (!isCellInteraction && item.isViewInteractable()){
                item.collect();
                collectedStaff = true;
            }
        }

        @Override public void interactWith(Turret turret, boolean isCellInteraction){
            if (isCellInteraction && turret.isCellInteractable()){
                turret.die();
            }
        }

        @Override
        public void interactWith(Key key, boolean isCellInteraction){

            if (isCellInteraction && key.isCellInteractable()){
                key.collect();
                KeyID = key.getID();
            }
        }

        @Override
        public void interactWith(Connector connector, boolean isCellInteraction){
            if (isCellInteraction && connector.isCellInteractable()){
                if (!isDisplacementOccurs()){
                    isCrossing = true;
                    crossingConnector = connector;
                }
            }
            else if (connector.isViewInteractable()){
                connector.unlock(KeyID);
            }
        }
    }




    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICRogueInteractionHandler) v).interactWith(this , isCellInteraction);
    }

}







