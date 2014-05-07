package strategy;

import java.util.ArrayList;

import move.PlaceArmiesMove;

public interface IPlaceArmiesStrategy extends IStrategy {

	public ArrayList<PlaceArmiesMove> getMoves();

}
