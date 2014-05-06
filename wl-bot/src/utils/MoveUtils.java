package utils;

import main.Region;
import move.AttackTransferMove;
import move.AttackTransferMove.MOVE_TYPE;
import move.PlaceArmiesMove;
import state.GlobalState;

public class MoveUtils {

	public static AttackTransferMove createMove(int armies, Region from, Region to, MOVE_TYPE type) {
		if (armies > from.getFreeArmies()) {
			armies = from.getFreeArmies();
		}
		AttackTransferMove move = new AttackTransferMove(GlobalState.getMyName(), from, to, armies, type);
		from.setArmies(from.getArmies() - armies);
		GlobalState.getCurrentState().addAttackedRegion(to);
		return move;
	}

	public static PlaceArmiesMove createMove(int armies, Region region) {
		armies = ArmyPlaceUtils.fixArmiesCount(armies);
		PlaceArmiesMove move = new PlaceArmiesMove(GlobalState.getMyName(), region, armies);
		region.setArmies(region.getArmies() + armies);
		GlobalState.getCurrentState().useArmies(armies);
		return move;
	}

}
