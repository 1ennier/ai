package state;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import main.Map;
import main.Region;
import move.AttackTransferMove;
import move.AttackTransferMove.MOVE_TYPE;
import move.Move;
import move.PlaceArmiesMove;

public class CurrentState {

	private Map visibleMap;

	private int startingArmies;
	private int remainingArmies;

	private int roundNumber;

	private ArrayList<Move> opponentMoves;

	private ArrayList<PlaceArmiesMove> placeArmiesMoves;
	private ArrayList<AttackTransferMove> attackTransferMoves;

	private ArrayList<AttackTransferMove> firstMoves;
	private ArrayList<AttackTransferMove> secondMoves;

	private LinkedList<Region> attackedRegions;

	public CurrentState() {
		super();
		roundNumber = 0;
		opponentMoves = new ArrayList<Move>();
	}

	public void setVisibleMap(Map map) {
		visibleMap = map;
	}

	public Map getVisibleMap() {
		return visibleMap;
	}

	public void setStartingArmies(int armies) {
		startingArmies = armies;
		remainingArmies = startingArmies;
		roundNumber++;
		placeArmiesMoves = new ArrayList<PlaceArmiesMove>();
		attackTransferMoves = new ArrayList<AttackTransferMove>();
		attackedRegions = new LinkedList<Region>();
		firstMoves = new ArrayList<AttackTransferMove>();
		secondMoves = new ArrayList<AttackTransferMove>();

		if (GlobalState.debug) {
			System.err.println("--- Round " + roundNumber + " ---");
		}
	}

	public int getStartingArmies() {
		return startingArmies;
	}

	public int getRemainingArmies() {
		return remainingArmies;
	}

	public int getRoundNumber() {
		return roundNumber;
	}

	public boolean noArmies() {
		return remainingArmies <= 0;
	}

	public void useArmies(int armies) {
		remainingArmies -= armies;
	}

	public void clearOpponentMoves() {
		opponentMoves.clear();
	}

	public void addOpponentMove(Move move) {
		opponentMoves.add(move);
	}

	public ArrayList<Move> getOpponentMoves() {
		return opponentMoves;
	}

	public ArrayList<PlaceArmiesMove> getPlaceArmiesMoves() {
		return placeArmiesMoves;
	}

	public void setPlaceArmiesMoves(ArrayList<PlaceArmiesMove> moves) {
		placeArmiesMoves = moves;
	}

	public ArrayList<AttackTransferMove> getAttackTransferMoves() {
		Iterator<AttackTransferMove> it = secondMoves.iterator();
		while (it.hasNext()) {
			AttackTransferMove move = it.next();
			attackTransferMoves.add(0, move);
			it.remove();
		}
		it = firstMoves.iterator();
		while (it.hasNext()) {
			AttackTransferMove move = it.next();
			attackTransferMoves.add(0, move);
			it.remove();
		}
		return attackTransferMoves;
	}

	public void addAttackTransferMove(AttackTransferMove move) {
		if (move.getType().equals(MOVE_TYPE.FIRST)) {
			firstMoves.add(move);
		} else if (move.getType().equals(MOVE_TYPE.NO_MATTER)) {
			secondMoves.add(move);
		} else {
			attackTransferMoves.add(move);
		}
	}

	public void setAttackTransferMoves(ArrayList<AttackTransferMove> moves) {
		attackTransferMoves = moves;
	}

	public LinkedList<Region> getAttackedRegions() {
		return attackedRegions;
	}

	public void addAttackedRegion(Region region) {
		if (!attackedRegions.contains(region)) {
			attackedRegions.add(region);
		}
	}

}
