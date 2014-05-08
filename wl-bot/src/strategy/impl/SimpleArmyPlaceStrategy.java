package strategy.impl;

import java.util.ArrayList;
import java.util.LinkedList;

import main.Region;
import move.PlaceArmiesMove;
import strategy.IPlaceArmiesStrategy;
import utils.RegionUtils;
import weight.RegionWeightArmyPlace;
import weight.RegionWeightArmyPlace.PROP;

public class SimpleArmyPlaceStrategy implements IPlaceArmiesStrategy {

	private ArrayList<PlaceArmiesMove> moves = new ArrayList<PlaceArmiesMove>();

	@Override
	public void execute() {
		LinkedList<Region> myRegions = RegionUtils.getMyRegions();
		for (Region region : myRegions) {
			if (region.isInner()) {
				continue;
			}
			processOpponentNear(region);
			processFreeBonus(region);
			processPossibleBonus(region);
		}
	}

	private void processOpponentNear(Region region) {
		if (region.hasOpponentNeighbor()) {
			double coeff = 1;
			region.setCoeffArmyPlace(coeff);
			region.incWeightArmyPlace(RegionWeightArmyPlace.getProp(PROP.opponentNear));
		}
	}

	private void processFreeBonus(Region region) {
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

	private void processPossibleBonus(Region region) {
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

	@Override
	public ArrayList<PlaceArmiesMove> getMoves() {
		return moves;
	}

}
