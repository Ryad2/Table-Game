package ch.epfl.cs107.play.game.icrogue.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.area.ICRogueRoom;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Connector extends AreaEntity {

    enum Condition{
        OPEN,
        CLOSED,
        LOCKED,
        INVISIBLE
    }


    private String destinationAreaName;
    private Condition condition;
    private DiscreteCoordinates targetCoordinates;
    private final int NO_KEY_ID = 0;// have to make a setter for this attribute

    Map<Condition, Sprite> sprites = new HashMap<>();

    // fix. ICRogueRoom should be Area
    public Connector(Area ownerArea, DiscreteCoordinates currentMainCellCoordinates, Orientation orientation) {
        super(ownerArea, orientation, currentMainCellCoordinates);//maybe owner Area is just an area and not an ICRogue room

        this.condition=Condition.INVISIBLE;//we suppose that the default condition is invisible

        this.destinationAreaName = ownerArea.getTitle();        // for now using getTitle() of level0Room specifically
                                                                // need new getTitle for other room types

        this.targetCoordinates = futureCoordinatesFinder(currentMainCellCoordinates, orientation);

        createSpriteMAP();
    }

    public Connector(Area owner, DiscreteCoordinates currentMainCellCoordinates, DiscreteCoordinates targetCoordinates,
                     Orientation orientation){
        this(owner, currentMainCellCoordinates, orientation);

        this.targetCoordinates = targetCoordinates;
    }


    //THIS SHIT CALCULATE THE COORDINATE OF THE FUTURE ROOM OF IN OUR LEVEL
    //can use methods Left() Right() and jump .... of DiscreteCoordinates

    //THIS SHIT IS NOT ASKED TO DO
    public DiscreteCoordinates futureRoomCoordinatesFinder(DiscreteCoordinates ownerAreaCoordinates, Orientation orientation){
        return new DiscreteCoordinates((int)(ownerAreaCoordinates.x+orientation.toVector().x), (int)(ownerAreaCoordinates.y+orientation.toVector().y));
    }




    //HAVE TO MAKE IT CLEAN IT IS TOO DISGUSTING
    public DiscreteCoordinates futureCoordinatesFinder(DiscreteCoordinates currentMainCellCoordinates,Orientation orientation){
    return new DiscreteCoordinates((int)(currentMainCellCoordinates.x-orientation.toVector().x* this.getOwnerArea().getWidth()), (int)(currentMainCellCoordinates.y-orientation.toVector().y* this.getOwnerArea().getHeight()));
    }




    //sure that we will need it one day to put our player in the next room
    public DiscreteCoordinates getTargetCoordinates() {
        return targetCoordinates;
    }




    public void createSpriteMAP() {

        /*Sprite CLOSED = new Sprite("icrogue/door_" + this.getOrientation().ordinal(),
                (this.getOrientation().ordinal()+1)%2+1, this.getOrientation().ordinal()%2+1, this);


        Sprite INVISIBLE = new Sprite("icrogue/invisibleDoor_" + this.getOrientation().ordinal(),
                (this.getOrientation().ordinal()+1)%2+1, this.getOrientation().ordinal()%2+1, this);


        Sprite LOCKED = new Sprite("icrogue/lockedDoor_" + this.getOrientation().ordinal(),
                (this.getOrientation().ordinal()+1)%2+1, this.getOrientation().ordinal()%2+1, this);*/




        Sprite CLOSED = new Sprite("icrogue/door_" + getOrientation().ordinal(),
                (getOrientation().ordinal()+1)%2+1, getOrientation().ordinal()%2+1, this);


        Sprite INVISIBLE = new Sprite("icrogue/invisibleDoor_" + getOrientation().ordinal(),
                (getOrientation().ordinal()+1)%2+1, getOrientation().ordinal()%2+1, this);


        Sprite LOCKED = new Sprite("icrogue/lockedDoor_" + getOrientation().ordinal(),
                (getOrientation().ordinal()+1)%2+1, getOrientation().ordinal()%2+1, this);

        sprites.put(Condition.CLOSED, CLOSED);
        sprites.put(Condition.INVISIBLE, INVISIBLE);
        sprites.put(Condition.LOCKED, LOCKED);

    }

    //I'M NOT SURE FOR THAT BUT SEEMS NICE XD
    public boolean isCellInteractable() {
        return condition == Condition.OPEN;
    }//NOT WRITTEN IN THE PDF, but I think that should be false always

    @Override
    public boolean isViewInteractable() {
        return true;
    }


    public boolean takeCellSpace() {
        return condition != Condition.OPEN;
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        DiscreteCoordinates coord = getCurrentMainCellCoordinates();
        return List.of(coord , coord.jump(new Vector((getOrientation().ordinal()+1)%2, getOrientation().ordinal()%2)));

    }


    public void draw(Canvas canvas) {
        if (condition != Condition.OPEN){
            sprites.get(condition).draw(canvas);
        }
    }





    //IDK BUT I THINK THAT I HAVE TO LET IT EMPTY FOR NOW
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
    }




}
