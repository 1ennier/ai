package utils;

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
		return opponentRegion.getArmies() <= getOpponentArmiesToAttack(myFreeArmies);
	}

	private static int getOpponentArmiesToAttack(int myFreeArmies) {
		int roundNumber = GlobalState.getCurrentState().getRoundNumber();
		double k = Math.pow(roundNumber, 0.5);
		return Math.max((int) Math.ceil((1.01 * k - 1) / k * myFreeArmies) - GlobalState.getCurrentState().getStartingArmies(), 0);
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
