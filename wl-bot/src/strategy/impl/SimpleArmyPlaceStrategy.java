package strategy.impl;

import java.util.ArrayList;
import java.util.LinkedList;

import main.Region;
import main.SuperRegion;
import move.PlaceArmiesMove;
import state.GlobalState;
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
			processInner(region);
			processOpponentNear(region);
			processFreeBonus(region);
			processPossibleBonus(region);
		}
	}

	private void processInner(Region region) {
		if (region.isInner()) {
			region.incWeightArmyPlace(-1000);
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
		SuperRegion superRegion = region.getSuperRegion();
		if (superRegion.isFree()) {
			region.incWeightArmyPlace(RegionWeightArmyPlace.getProp(PROP.freeBonus));
		}

		LinkedList<Region> otherBonusNeighbors = region.getMyRegionOtherSuperRegionNeutralNeighbors();
		for (Region neighbor : otherBonusNeighbors) {
			SuperRegion neighborSuperRegion = neighbor.getSuperRegion();
			if (neighborSuperRegion.isFree()) {
				region.incWeightArmyPlace(RegionWeightArmyPlace.getProp(PROP.freeBonus));
			}
		}
	}

	private void processPossibleBonus(Region region) {
		SuperRegion superRegion = region.getSuperRegion();
		LinkedList<Region> regions = superRegion.getSubRegions();
		boolean neutralsAndUnknown = true;
		for (Region subregion : regions) {
			if (!subregion.ownedByPlayer(GlobalState.getNeutralName()) && !subregion.ownedByPlayer(GlobalState.getUnknownName())) {
				neutralsAndUnknown = false;
				break;
			}
		}
		if (neutralsAndUnknown) {
			region.incWeightArmyPlace(RegionWeightArmyPlace.getProp(PROP.possibleBonus));
		}

		LinkedList<Region> otherBonusNeighbors = region.getMyRegionOtherSuperRegionNeutralNeighbors();
		if (!otherBonusNeighbors.isEmpty()) {
			region.incWeightArmyPlace(RegionWeightArmyPlace.getProp(PROP.freeBonus));
		}
	}

	@Override
	public ArrayList<PlaceArmiesMove> getMoves() {
		return moves;
	}

}
