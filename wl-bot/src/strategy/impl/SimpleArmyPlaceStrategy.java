package strategy.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import main.Region;
import move.PlaceArmiesMove;
import state.GlobalState;
import strategy.IStrategyPlaceArmies;
import utils.AttackUtils;
import utils.MoveUtils;
import utils.RegionUtils;
import utils.comparator.desc.RegionWeightArmyPlacementDescComparator;
import weight.RegionWeightArmyPlace;
import weight.RegionWeightArmyPlace.PROP;

public class SimpleArmyPlaceStrategy implements IStrategyPlaceArmies {

	private ArrayList<PlaceArmiesMove> moves = new ArrayList<PlaceArmiesMove>();

	@Override
	public void execute() {
		moves.clear();
		LinkedList<Region> myRegions = RegionUtils.getMyRegions();
		for (Region region : myRegions) {
			if (region.isInner()) {
				continue;
			}
			processOpponentNear(region);
			processFreeBonus(region);
			processPossibleBonus(region);
		}

		calculateArmyPlacements();
	}

	private void calculateArmyPlacements() {
		if (GlobalState.debugArmyPlace) {
			System.err.println("* Placements *");
		}
		Map<Region, Integer> placements = new HashMap<Region, Integer>();
		int toPlace = GlobalState.getCurrentState().getRemainingArmies();
		double extra = 0;
		double totalW = 0;
		for (Region region : RegionUtils.getMyRegions()) {
			totalW += region.getWeightArmyPlace();
		}
		for (Region region : RegionUtils.getMyRegions()) {
			double temp = region.getWeightArmyPlace() * toPlace / totalW;
			int placement = (int) Math.floor(temp);
			extra += temp - placement;
			placements.put(region, placement);
			if (GlobalState.debugArmyPlace) {
				System.err.println(region + ": place " + placement);
			}
		}
		if (extra > 0) {
			int extraInt = (int) Math.round(extra);
			LinkedList<Region> myRegions = RegionUtils.getMyRegions();
			Collections.sort(myRegions, new RegionWeightArmyPlacementDescComparator());
			Region r = myRegions.getFirst();
			double maxW = r.getWeightArmyPlace();

			while (extraInt > 0) {
				for (Region region : myRegions) {
					if (extraInt == 0) {
						break;
					}
					if (region.getWeightArmyPlace() == maxW) {
						int placement = placements.get(region) + 1;
						placements.put(region, placement);
						extraInt--;
						if (GlobalState.debugArmyPlace) {
							System.err.println(region + ": placement corrected to " + placement);
						}
					}
				}
			}
		}

		for (Region region : placements.keySet()) {
			int value = placements.get(region);
			if (value > 0) {
				PlaceArmiesMove move = MoveUtils.createMove(value, region);
				moves.add(move);
			}
		}

	}

	private void processOpponentNear(Region region) {
		if (!AttackUtils.hasEnoughArmiesToAttack(region) && region.hasOpponentNeighbor()) {
			double coeff = 1;
			region.setCoeffArmyPlace(coeff);
			region.incWeightArmyPlace(RegionWeightArmyPlace.getProp(PROP.opponentNear));
		}
	}

	private void processFreeBonus(Region region) {
		if (!AttackUtils.hasEnoughArmiesToAttack(region)) {
			if (region.isInFreeBonus()) {
				region.incWeightArmyPlace(RegionWeightArmyPlace.getProp(PROP.freeBonus));
			}

			LinkedList<Region> otherBonusNeighbors = region.getMyRegionOtherSuperRegionNeutralNeighbors();
			for (Region neighbor : otherBonusNeighbors) {
				if (neighbor.isInFreeBonus()) {
					region.incWeightArmyPlace(RegionWeightArmyPlace.getProp(PROP.freeBonus));
				}
			}
		}
	}

	private void processPossibleBonus(Region region) {
		if (!AttackUtils.hasEnoughArmiesToAttack(region)) {
			if (region.isInPossibleFreeBonus()) {
				region.incWeightArmyPlace(RegionWeightArmyPlace.getProp(PROP.possibleBonus));
			}

			LinkedList<Region> otherBonusNeighbors = region.getMyRegionOtherSuperRegionNeutralNeighbors();
			for (Region neighbor : otherBonusNeighbors) {
				if (neighbor.isInPossibleFreeBonus()) {
					region.incWeightArmyPlace(RegionWeightArmyPlace.getProp(PROP.possibleBonus));
				}
			}
		}
	}

	@Override
	public ArrayList<PlaceArmiesMove> getMoves() {
		return moves;
	}

}
