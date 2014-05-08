package strategy;

import java.util.ArrayList;

import move.PlaceArmiesMove;

public interface IStrategyPlaceArmies extends IStrategy {

	public ArrayList<PlaceArmiesMove> getMoves();

}
