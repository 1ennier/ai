package strategy.impl;

import java.util.ArrayList;
import java.util.LinkedList;

import main.Region;
import move.AttackTransferMove;
import move.AttackTransferMove.MOVE_TYPE;
import state.GlobalState;
import strategy.IStrategyAttack;
import utils.AttackUtils;
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

			for (Region neighbor : neighbors) {
				double weight = neighbor.getWeightAttack();
				if (weight == maxW) {
					addAttack(myRegion, neighbor);
				}
			}

		}
	}

	private void addAttack(Region my, Region to) {
		if (AttackUtils.needAttack(my, to, to.ownedByPlayer(GlobalState.getNeutralName()))) {
			int armiesToAttack = my.getFreeArmies();
			moves.add(RegionUtils.createMove(armiesToAttack, my, to, MOVE_TYPE.LAST));
			if (GlobalState.debugAttack) {
				System.err.println("Attack from " + my + " to " + to + " with " + armiesToAttack);
			}
		} else {
			if (GlobalState.debugAttack) {
				System.err.println("Attack not needed from " + my + " to " + to);
			}
		}
	}

	@Override
	public ArrayList<AttackTransferMove> getMoves() {
		return moves;
	}

}
