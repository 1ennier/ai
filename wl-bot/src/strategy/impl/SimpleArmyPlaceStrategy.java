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
		LinkedList<Region> regions = superRegion.getSubRegions();
		for (Region subregion : regions) {
			if (subregion.ownedByPlayer(GlobalState.getUnknownName()) || subregion.ownedByPlayer(GlobalState.getOpponentName())) {
				return;
			}
		}
		region.incWeightArmyPlace(RegionWeightArmyPlace.getProp(PROP.freeBonus));
	}

	@Override
	public ArrayList<PlaceArmiesMove> getMoves() {
		return moves;
	}

}
