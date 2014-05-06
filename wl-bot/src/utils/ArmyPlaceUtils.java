package utils;

import java.util.Collections;
import java.util.LinkedList;

import main.Region;
import main.SuperRegion;
import move.AttackTransferMove.MOVE_TYPE;
import state.GlobalState;
import utils.comparator.RegionArmiesComparator;
import utils.comparator.RegionBonusSizeComparator;
import utils.comparator.RegionInDangerComparator;

public class ArmyPlaceUtils {

	/** если есть противник рядом
	 * 
	 * @param placeArmiesMoves */
	public static void placeArmiesIfHasOpponent() {
		if (!RegionUtils.getMyBorderRegions().isEmpty()) {//есть пограничные регионы
			SuperRegion superRegion = RegionUtils.getSuperRegionToTake();
			if (superRegion != null) {
				LinkedList<Region> regionsToTake = RegionUtils.getRegionsToTakeBySuperRegion(superRegion);
				LinkedList<Region> myRegions = RegionUtils.getMyNeighborsOfRegions(regionsToTake);
				if (regionsToTake.size() <= 2) {//чуток до бонуса
					Collections.sort(myRegions, new RegionArmiesComparator());
					Region myRegionFrom = myRegions.getFirst();
					double rand = Math.random();
					int armiesToPlace = (int) (rand + 1);
					armiesToPlace = fixArmiesCount(armiesToPlace);
					GlobalState.getCurrentState().getPlaceArmiesMoves().add(RegionUtils.createMove(armiesToPlace, myRegionFrom));
				}
			}
		}

		LinkedList<Region> myRegionsNearOpponent = RegionUtils.getMyRegionsNearOpponent();
		int toPlace = GlobalState.getCurrentState().getRemainingArmies();
		if (toPlace > 0) {
			Collections.sort(myRegionsNearOpponent, new RegionInDangerComparator());
			GlobalState.getCurrentState().getPlaceArmiesMoves().add(RegionUtils.createMove(toPlace, myRegionsNearOpponent.getFirst()));
		}
		for (Region myRegionInDanger : myRegionsNearOpponent) {
			for (Region my : myRegionInDanger.getMyRegionsNear()) {
				if (!my.hasOpponentNeighbor()) {
					GlobalState.getCurrentState().addAttackTransferMove(RegionUtils.createMove(my.getFreeArmies(), my, myRegionInDanger, MOVE_TYPE.FIRST));
				}
			}
		}
	}

	/** Корректировка кол-ва армий с учетом того, сколько армий осталось
	 * 
	 * @param armies
	 * @return */
	public static int fixArmiesCount(int armies) {
		if (armies > GlobalState.getCurrentState().getRemainingArmies()) {
			return GlobalState.getCurrentState().getRemainingArmies();
		}
		return armies;
	}

	/** если нет рядом противника */
	public static void placeArmiesIfHasNoOpponent() {
		SuperRegion superRegion = RegionUtils.getSuperRegionToTake();
		if (superRegion == null) {//Если нет бонусов для захвата
			placeAllToOneBorder();
		} else {//Пытаемся добавить войск туда, где нужно захватывать бонус
			placeAllToTakeSuperRegion(superRegion);
		}
	}

	/** ставить все чтобы захватить супербонус
	 * 
	 * @param superRegion
	 * @param placeArmiesMoves */
	private static void placeAllToTakeSuperRegion(SuperRegion superRegion) {
		LinkedList<Region> regionsToTake = RegionUtils.getRegionsToTakeBySuperRegion(superRegion);

		for (Region regionToTake : regionsToTake) {
			int neededArmiesToAttack = AttackUtils.getNeededArmiesToAttack(regionToTake.getArmies());
			LinkedList<Region> myRegionsNear = regionToTake.getMyRegionsNear();
			int myFreeArmiesNear = RegionUtils.getMyFreeArmies(myRegionsNear);
			if (AttackUtils.isFreeArmiesEnoughToAttack(myFreeArmiesNear, regionToTake.getArmies())) {//если армий достаточно

				Collections.sort(myRegionsNear, new RegionArmiesComparator());
				for (Region myRegionNear : myRegionsNear) {
					if (myRegionNear.getFreeArmies() >= neededArmiesToAttack) {
						GlobalState.getCurrentState().addAttackTransferMove(
								RegionUtils.createMove(neededArmiesToAttack, myRegionNear, regionToTake, MOVE_TYPE.NO_MATTER));
						break;
					}

					neededArmiesToAttack = fixArmiesCount(neededArmiesToAttack);
					GlobalState.getCurrentState().getPlaceArmiesMoves().add(RegionUtils.createMove(neededArmiesToAttack, myRegionNear));
					if (AttackUtils.isFreeArmiesEnoughToAttack(myRegionNear.getFreeArmies(), regionToTake.getArmies())) {
						GlobalState.getCurrentState().addAttackTransferMove(
								RegionUtils.createMove(myRegionNear.getFreeArmies(), myRegionNear, regionToTake, MOVE_TYPE.NO_MATTER));
						break;
					}
				}
			} else {//если армий недостаточно
				Collections.sort(myRegionsNear, new RegionArmiesComparator());
				Region myAttackingRegion = myRegionsNear.getFirst();
				if (!AttackUtils.isFreeArmiesEnoughToAttack(myAttackingRegion.getFreeArmies(), regionToTake.getArmies())) {
					neededArmiesToAttack -= myAttackingRegion.getFreeArmies();
					neededArmiesToAttack = fixArmiesCount(neededArmiesToAttack);
					if (neededArmiesToAttack > 0) {
						GlobalState.getCurrentState().getPlaceArmiesMoves().add(RegionUtils.createMove(neededArmiesToAttack, myAttackingRegion));
					}
				}
				GlobalState.getCurrentState().addAttackTransferMove(
						RegionUtils.createMove(myAttackingRegion.getFreeArmies(), myAttackingRegion, regionToTake, MOVE_TYPE.NO_MATTER));
			}
		}

		if (GlobalState.getCurrentState().getRemainingArmies() > 0) {//если что-то вдруг осталось - кладем куда-нибудь
			LinkedList<Region> borders = RegionUtils.getMyBorderRegions();
			for (Region border : borders) {
				for (Region neutral : border.getMyRegionNeutralNeighbors()) {
					if (AttackUtils.isFreeArmiesEnoughToAttack(border.getFreeArmies(), neutral.getArmies())) {
						GlobalState.getCurrentState().addAttackTransferMove(
								RegionUtils.createMove(border.getFreeArmies(), border, neutral, MOVE_TYPE.NO_MATTER));
					} else {
						int neededArmiesToAttack = AttackUtils.getNeededArmiesToAttack(neutral.getArmies());
						neededArmiesToAttack -= border.getFreeArmies();
						neededArmiesToAttack = fixArmiesCount(neededArmiesToAttack);
						if (neededArmiesToAttack > 0) {
							GlobalState.getCurrentState().getPlaceArmiesMoves().add(RegionUtils.createMove(neededArmiesToAttack, border));
						}
						if (AttackUtils.isFreeArmiesEnoughToAttack(border.getFreeArmies(), neutral.getArmies())) {
							GlobalState.getCurrentState().addAttackTransferMove(
									RegionUtils.createMove(border.getFreeArmies(), border, neutral, MOVE_TYPE.NO_MATTER));
						}
					}
					if (GlobalState.getCurrentState().getRemainingArmies() == 0) {
						break;
					}
				}
				if (GlobalState.getCurrentState().getRemainingArmies() == 0) {
					break;
				}
			}
		}

		if (GlobalState.getCurrentState().getRemainingArmies() > 0) {
			RegionUtils.randomPlaceArmies();
		}
	}

	/** ставить всё в один регион на границу */
	private static void placeAllToOneBorder() {
		LinkedList<Region> borderNeighbors = new LinkedList<Region>();
		for (Region region : RegionUtils.getMyBorderRegions()) {
			for (Region neighbor : region.getMyRegionNeutralNeighbors()) {
				if (!borderNeighbors.contains(neighbor)) {
					borderNeighbors.add(neighbor);
				}
			}
		}
		Collections.sort(borderNeighbors, new RegionBonusSizeComparator());
		Region targetRegion = borderNeighbors.getFirst();
		LinkedList<Region> myRegionsNear = targetRegion.getMyRegionsNear();
		Collections.sort(myRegionsNear, new RegionArmiesComparator());
		Region regionToPlace = myRegionsNear.getFirst();
		GlobalState.getCurrentState().getPlaceArmiesMoves().add(RegionUtils.createMove(GlobalState.getCurrentState().getRemainingArmies(), regionToPlace));
	}

}
