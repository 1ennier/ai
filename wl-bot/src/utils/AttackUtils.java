package utils;

import java.util.LinkedList;

import main.Region;
import state.GlobalState;

public class AttackUtils {

	public static boolean needAttack(Region myRegion, Region opponentRegion, boolean isNeutral) {
		if (isNeutral && myRegion.hasOpponentNeighbor()) {
			return false;
		}

		return isEnoughToAttack(myRegion.getFreeArmies(), opponentRegion);
	}

	public static boolean isEnoughToAttack(int myFreeArmies, Region opponentRegion) {
		int toAttack = opponentRegion.ownedByPlayer(GlobalState.getNeutralName()) ? getNeutralArmiesCanAttack(myFreeArmies)
				: getOpponentArmiesToAttack(myFreeArmies);
		return opponentRegion.getArmies() <= toAttack;
	}

	private static int getOpponentArmiesToAttack(int myFreeArmies) {
		int roundNumber = GlobalState.getCurrentState().getRoundNumber();
		double k = Math.pow(roundNumber, 0.5);
		return Math.max((int) Math.ceil((1.01 * k - 1) / k * myFreeArmies) - GlobalState.getCurrentState().getStartingArmies(), 0);
	}

	public static int getNeutralArmiesCanAttack(int myFreeArmies) {
		return (int) Math.round(0.99 * myFreeArmies / 2);
	}

	public static int getNeededArmiesToAttackNeutral(int neutralArmies) {
		return (int) Math.round(neutralArmies * 1.8);
	}

	public static int getOpponentArmiesCanAttack(int myFreeArmies) {
		return getNeutralArmiesCanAttack(myFreeArmies) + 5;//TODO
	}

	public static boolean hasEnoughArmiesToAttack(Region myRegion) {
		boolean allAttacked = true;
		int myFreeArmies = myRegion.getFreeArmies();
		LinkedList<Region> neighbors = myRegion.getNeighbors();
		for (Region neighbor : neighbors) {
			if (myFreeArmies <= 0) {
				allAttacked = false;
				break;
			}
			int armiesThatICanAttack = neighbor.ownedByPlayer(GlobalState.getNeutralName()) ? getNeutralArmiesCanAttack(myFreeArmies)
					: getOpponentArmiesCanAttack(myFreeArmies);
			int unattackedArmies = neighbor.getArmies() - armiesThatICanAttack;
			if (unattackedArmies == 0) {
				myFreeArmies -= getNeededArmiesToAttack(neighbor.getArmies());
			} else {
				allAttacked = false;
				break;
			}
		}
		return allAttacked && myFreeArmies >= 0;
	}

	public static boolean isFreeArmiesEnoughToAttack(int myArmiesCount, int opponentArmiesCount) {
		int neededArmiesToAttack = getNeededArmiesToAttack(opponentArmiesCount);
		if (myArmiesCount >= neededArmiesToAttack) {
			return true;
		}
		return false;
	}

	public static int getNeededArmiesToAttack(int opponentArmiesCount) {
		if (opponentArmiesCount > 200) {
			return (int) Math.ceil(1.1 * opponentArmiesCount);
		}
		if (opponentArmiesCount > 100) {
			return (int) Math.ceil(1.2 * opponentArmiesCount);
		}
		if (opponentArmiesCount > 60) {
			return (int) Math.ceil(1.3 * opponentArmiesCount);
		}
		if (opponentArmiesCount > 30) {
			return (int) Math.ceil(1.4 * opponentArmiesCount);
		}
		if (opponentArmiesCount > 20) {
			return (int) Math.ceil(1.5 * opponentArmiesCount);
		}
		if (opponentArmiesCount > 10) {
			return (int) Math.ceil(1.6 * opponentArmiesCount);
		}
		return (int) Math.ceil(1.7 * opponentArmiesCount);
	}

}
