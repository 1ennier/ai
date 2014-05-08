package strategy.impl;

import java.util.ArrayList;
import java.util.LinkedList;

import main.Region;
import move.AttackTransferMove;
import state.GlobalState;
import strategy.IStrategyAttack;
import utils.RegionUtils;
import weight.RegionWeightAttack;
import weight.RegionWeightAttack.PROP;

public class SimpleAttackStrategy implements IStrategyAttack {

	private ArrayList<AttackTransferMove> moves = new ArrayList<AttackTransferMove>();

	@Override
	public void execute() {
		moves.clear();

		LinkedList<Region> visibleRegions = GlobalState.getCurrentState().getVisibleMap().getRegions();
		for (Region region : visibleRegions) {
			processRegionAttackWeight(region);
		}

		calculateAttacks();

		if (GlobalState.debugAttack) {
			for (Region region : visibleRegions) {
				System.err.println(region + ": attack weight is " + region.getWeightAttack());
			}
		}
	}

	private void processRegionAttackWeight(Region region) {
		if (region.ownedByPlayer(GlobalState.getOpponentName())) {
			double coeff = 1;
			region.setCoeffAttack(coeff);
			region.incWeightAttack(RegionWeightAttack.getProp(PROP.opponent));
		} else if (region.ownedByPlayer(GlobalState.getNeutralName())) {
			if (region.isInFreeBonus()) {
				double coeff = 1;
				region.setCoeffAttack(coeff);
				region.incWeightAttack(RegionWeightAttack.getProp(PROP.neutralInFreeBonus));
			} else if (region.isInPossibleFreeBonus()) {
				double coeff = 1;
				region.setCoeffAttack(coeff);
				region.incWeightAttack(RegionWeightAttack.getProp(PROP.neutralInPossibleFreeBonus));
			} else {
				double coeff = 1;
				region.setCoeffAttack(coeff);
				region.incWeightAttack(RegionWeightAttack.getProp(PROP.neutral));
			}
		}
	}

	private void calculateAttacks() {
		if (GlobalState.debugAttack) {
			System.err.println("* Attacks *");
		}
		LinkedList<Region> myRegions = RegionUtils.getMyRegions();
		for (Region myRegion : myRegions) {
			if (myRegion.getFreeArmies() == 0) {
				continue;
			}
			LinkedList<Region> neighbors = myRegion.getNeighbors();
			double maxW = 0;

			for (Region neighbor : neighbors) {
				double weight = neighbor.getWeightAttack();
				maxW = Math.max(maxW, weight);
			}

		}

	}

	@Override
	public ArrayList<AttackTransferMove> getMoves() {
		return moves;
	}

}
