package utils;

import graph.DijkstraSP;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import main.Region;
import main.SuperRegion;
import state.GlobalState;
import utils.comparator.RegionBonusSizeComparator;
import utils.comparator.SuperRegionTakePriorityComparator;

public class RegionUtils extends MyRegionUtils {

	/** Все нейтральные регионы суперрегиона, которые видны в данном раунде
	 * 
	 * @param superRegion
	 * @return */
	public static LinkedList<Region> getRegionsToTakeBySuperRegion(SuperRegion superRegion) {
		LinkedList<Region> regionsToTake = new LinkedList<Region>();
		for (Region region : superRegion.getSubRegions()) {
			if (region.isNeutral()) {
				for (Region neutralRegionNeighbor : region.getNeighbors()) {
					if (neutralRegionNeighbor.isMy() && !regionsToTake.contains(region)) {
						regionsToTake.add(region);
						break;
					}
				}
			}
		}
		return regionsToTake;
	}

	/** Все мои регионы, которые граничат с чужими регионами. Нужно для подготовки захвата супербонуса
	 * 
	 * @param regions
	 * @return */
	public static LinkedList<Region> getMyNeighborsOfRegions(LinkedList<Region> regions) {
		LinkedList<Region> myRegions = new LinkedList<Region>();
		for (Region region : regions) {
			for (Region neighbor : region.getNeighbors()) {
				if (neighbor.isMy() && !myRegions.contains(neighbor)) {
					myRegions.add(neighbor);
				}
			}
		}
		return myRegions;
	}

	public static LinkedList<Region> getMyNeighborsOfRegion(Region region) {
		LinkedList<Region> myRegions = new LinkedList<Region>();
		for (Region neighbor : region.getNeighbors()) {
			if (neighbor.isMy() && !myRegions.contains(neighbor)) {
				myRegions.add(neighbor);
			}
		}
		return myRegions;
	}

	private static LinkedList<SuperRegion> getAvailableSuperRegions() {
		LinkedList<Region> myRegions = getMyRegions();
		LinkedList<SuperRegion> availableSuperRegions = new LinkedList<SuperRegion>();
		Collections.sort(myRegions, new RegionBonusSizeComparator());
		for (Region region : myRegions) {
			if (!availableSuperRegions.contains(region.getSuperRegion())) {
				availableSuperRegions.add(region.getSuperRegion());
			}
		}

		Iterator<SuperRegion> it = availableSuperRegions.iterator();
		while (it.hasNext()) {
			SuperRegion superRegion = it.next();
			LinkedList<Region> regions = superRegion.getSubRegions();
			boolean opponentFound = false;
			boolean neutralFound = false;
			for (Region region : regions) {
				if (region.isOpponent()) {
					opponentFound = true;
					break;
				}
				if (region.isNeutral()) {
					neutralFound = true;
				}
			}
			if (opponentFound || !neutralFound) {
				it.remove();
			}
		}

		return availableSuperRegions;
	}

	public static SuperRegion getSuperRegionToTake() {
		LinkedList<SuperRegion> availableSuperRegions = RegionUtils.getAvailableSuperRegions();
		if (availableSuperRegions.isEmpty()) {
			return null;
		}
		if (availableSuperRegions.size() == 1) {
			return availableSuperRegions.get(0);
		}
		Collections.sort(availableSuperRegions, new SuperRegionTakePriorityComparator());
		return availableSuperRegions.getFirst();
	}

	public static void randomPlaceArmies() {
		int armies = 2;
		LinkedList<Region> visibleRegions = GlobalState.getCurrentState().getVisibleMap().getRegions();

		while (GlobalState.getCurrentState().getRemainingArmies() > 0) {
			double rand = Math.random();
			int r = (int) (rand * visibleRegions.size());
			Region region = visibleRegions.get(r);

			if (RegionUtils.getMyInnerRegions().contains(region)) {
				continue;
			}

			if (region.isMy()) {
				GlobalState.getCurrentState().getPlaceArmiesMoves().add(createMove(armies, region));
			}
		}
	}

	//	/** useless, to delete
	//	 * 
	//	 * @param attackedRegions
	//	 * @return */
	//	public static ArrayList<AttackTransferMove> getRandomAttackTransferMoves(LinkedList<Region> attackedRegions) {
	//		ArrayList<AttackTransferMove> attackTransferMoves = new ArrayList<AttackTransferMove>();
	//		int armies = 5;
	//
	//		for (Region fromRegion : CurrentState.getVisibleMap().getRegions()) {
	//			if (fromRegion.ownedByPlayer(CurrentState.getMyName())) { //do an attack
	//				ArrayList<Region> possibleToRegions = new ArrayList<Region>();
	//				possibleToRegions.addAll(fromRegion.getNeighbors());
	//
	//				while (!possibleToRegions.isEmpty()) {
	//					double rand = Math.random();
	//					int r = (int) (rand * possibleToRegions.size());
	//					Region toRegion = possibleToRegions.get(r);
	//
	//					if (!toRegion.ownedByPlayer(CurrentState.getMyName()) && fromRegion.getArmies() > 6) { //do an attack
	//						attackTransferMoves.add(createMove(armies, fromRegion, toRegion, attackedRegions));
	//						break;
	//					} else if (toRegion.ownedByPlayer(CurrentState.getMyName()) && fromRegion.getArmies() > 1) { //do a transfer
	//						attackTransferMoves.add(createMove(armies, fromRegion, toRegion, attackedRegions));
	//						break;
	//					} else {
	//						possibleToRegions.remove(toRegion);
	//					}
	//				}
	//			}
	//		}
	//
	//		return attackTransferMoves;
	//	}

	public static RegionAttackInfo getRegionAttackInfo(Region regionToTake) {
		for (Region neighbor : regionToTake.getNeighbors()) {
			if (neighbor.isMy() && neighbor.getArmies() > 2 && AttackUtils.isFreeArmiesEnoughToAttack(neighbor.getFreeArmies(), regionToTake.getArmies())) {
				int armiesToAttack = AttackUtils.getNeededArmiesToAttack(regionToTake.getArmies());
				if (armiesToAttack > neighbor.getFreeArmies()) {
					armiesToAttack = neighbor.getFreeArmies();
				}
				return new RegionAttackInfo(neighbor, armiesToAttack);
			}
		}
		return null;
	}

	public static int getDistance(Region region1, Region region2) {
		int s = region1.getId() - 1;
		int t = region2.getId() - 1;
		int result = Integer.MAX_VALUE;
		DijkstraSP sp = new DijkstraSP(GlobalState.getGraph(), s);
		if (sp.hasPathTo(t)) {
			result = sp.distTo(t);
		}
		return result;
	}

	/** Все суперрегионы, граничащие с данным
	 * 
	 * @param superRegion
	 * @return */
	public static LinkedList<SuperRegion> getNeighborSuperRegions(SuperRegion superRegion) {
		LinkedList<SuperRegion> neighborSuperRegions = new LinkedList<SuperRegion>();
		LinkedList<Region> regions = superRegion.getSubRegions();
		for (Region region : regions) {
			LinkedList<Region> neighbors = region.getNeighbors();
			for (Region neighbor : neighbors) {
				SuperRegion neighborSuperRegion = neighbor.getSuperRegion();
				if (neighborSuperRegion.getId() != superRegion.getId() && !neighborSuperRegions.contains(neighborSuperRegion)) {
					neighborSuperRegions.add(neighborSuperRegion);
				}
			}
		}
		return neighborSuperRegions;
	}

}
