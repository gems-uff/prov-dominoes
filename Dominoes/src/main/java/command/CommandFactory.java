package command;

import java.util.ArrayList;

import boundary.MoveData;
import domain.Configuration;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class CommandFactory {
	
	public TransposeCommand createTranspose(Group piece,MoveData data) {
		return new TransposeCommand(piece, data);
	}

}
