package bot;

import java.util.ArrayList;

import main.Region;
import move.AttackTransferMove;
import move.PlaceArmiesMove;

public interface IBot {

	public ArrayList<Region> getPreferredStartingRegions(Long timeOut);

	public ArrayList<PlaceArmiesMove> getPlaceArmiesMoves(Long timeOut);

	public ArrayList<AttackTransferMove> getAttackTransferMoves(Long timeOut);

}
