package utils;

import java.util.LinkedList;

import main.Region;
import state.GlobalState;

public class MyRegionUtils extends MoveUtils {

	/** Все мои регионы
	 * 
	 * @return */
	public static LinkedList<Region> getMyRegions() {
		LinkedList<Region> myRegions = new LinkedList<Region>();
		LinkedList<Region> allRegions = GlobalState.getCurrentState().getVisibleMap().getRegions();
		for (Region region : allRegions) {
			if (region.isMy()) {
				myRegions.add(region);
			}
		}
		return myRegions;
	}

	/** Мои внутренние регионы
	 * 
	 * @return */
	public static LinkedList<Region> getMyInnerRegions() {
		LinkedList<Region> myInnerRegions = new LinkedList<Region>();
		LinkedList<Region> allRegions = GlobalState.getCurrentState().getVisibleMap().getRegions();
		for (Region region : allRegions) {
			if (region.isMy() && region.isInner()) {
				myInnerRegions.add(region);
			}
		}
		return myInnerRegions;
	}

	/** Все мои регионы, имеющие рядом противника
	 * 
	 * @return */
	public static LinkedList<Region> getMyRegionsNearOpponent() {
		LinkedList<Region> myRegions = new LinkedList<Region>();
		LinkedList<Region> allRegions = GlobalState.getCurrentState().getVisibleMap().getRegions();
		for (Region region : allRegions) {
			if (region.isMy() && region.hasOpponentNeighbor()) {
				myRegions.add(region);
			}
		}
		return myRegions;
	}

	/** Все мои регионы, имеющие нейтральных соседей и не имеющие рядом противника
	 * 
	 * @return */
	public static LinkedList<Region> getMyBorderRegions() {
		LinkedList<Region> myBorderRegions = new LinkedList<Region>();
		LinkedList<Region> allRegions = GlobalState.getCurrentState().getVisibleMap().getRegions();
		for (Region region : allRegions) {
			if (region.isMy() && region.hasNeutralNeighbor()) {
				myBorderRegions.add(region);
			}
		}
		return myBorderRegions;
	}

	/** Кол-во свободных армий в моих регионах
	 * 
	 * @param myRegions
	 * @return */
	public static int getMyFreeArmies(LinkedList<Region> myRegions) {
		int armies = 0;
		for (Region region : myRegions) {
			armies += region.getFreeArmies();
		}
		return armies;
	}

	/** Кол-во армий противника
	 * 
	 * @param regions
	 * @return */
	public static int getOpponentArmies(LinkedList<Region> regions) {
		int armies = 0;
		for (Region region : regions) {
			armies += region.getArmies();
		}
		return armies;
	}

}
