package ch.epfl.cs107.play.game.icrogue.area.level0.rooms;

import ch.epfl.cs107.play.game.icrogue.area.ICRogueRoom;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class level0Room extends ICRogueRoom {

    public String getTitle() { return "icrogue/level0" + this.coordinates.x + this.coordinates.y; }

    public level0Room (DiscreteCoordinates roomCoordinates){
        super("icrogue/level0Room", roomCoordinates);
    }
}
