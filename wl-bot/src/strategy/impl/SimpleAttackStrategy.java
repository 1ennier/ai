package strategy.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import main.Region;
import move.AttackTransferMove;
import move.AttackTransferMove.MOVE_TYPE;
import state.GlobalState;
import strategy.IStrategyAttack;
import utils.AttackUtils;
import utils.RegionUtils;
import utils.comparator.desc.RegionWeightAttackDescComparator;
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

		if (GlobalState.debugAttack) {
			System.err.println("* Attack weights *");
		}
		if (GlobalState.debugAttack) {
			for (Region region : visibleRegions) {
				System.err.println(region + ": attack weight is " + region.getWeightAttack());
			}
		}

		if (GlobalState.debugAttack) {
			System.err.println("* Attacks *");
		}
		calculateAttacks();
	}

	private void processRegionAttackWeight(Region region) {
		if (region.isOpponent()) {
			double coeff = 1;
			region.setCoeffAttack(coeff);
			region.incWeightAttack(RegionWeightAttack.getProp(PROP.opponent));
		} else if (region.isNeutral()) {
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
		LinkedList<Region> myRegions = RegionUtils.getMyRegions();
		for (Region myRegion : myRegions) {
			if (myRegion.getFreeArmies() == 0) {
				continue;
			}

			LinkedList<Region> neighbors = myRegion.getNeighbors();
			Collections.sort(neighbors, new RegionWeightAttackDescComparator());

			for (Region neighbor : neighbors) {
				addAttack(myRegion, neighbor);
				if (myRegion.getFreeArmies() == 0) {
					break;
				}
			}

		}
	}

	private boolean addAttack(Region my, Region to) {
		boolean result = false;
		if (AttackUtils.needAttack(my, to)) {
			int armiesToAttack = my.getFreeArmies();
			if (to.isNeutral()) {
				int need = AttackUtils.getNeededArmiesToAttackNeutral(to.getArmies());
				if (need < armiesToAttack) {
					armiesToAttack = need;
				}
			}

			moves.add(RegionUtils.createMove(armiesToAttack, my, to, MOVE_TYPE.LAST));
			if (GlobalState.debugAttack) {
				System.err.println("Attack from " + my + " to " + to + " with " + armiesToAttack);
			}
			result = true;
		} else {
			if (GlobalState.debugAttack) {
				System.err.println("Attack not needed from " + my + " to " + to);
			}
		}
		return result;
	}

	@Override
	public ArrayList<AttackTransferMove> getMoves() {
		return moves;
	}

}
