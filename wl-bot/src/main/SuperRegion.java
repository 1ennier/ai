package main;

import java.util.Iterator;
import java.util.LinkedList;

import state.GlobalState;

public class SuperRegion extends AbstractRegion {

	private int armiesReward;
	private LinkedList<Region> subRegions;

	public SuperRegion(int id, int armiesReward) {
		this.id = id;
		this.armiesReward = armiesReward;
		subRegions = new LinkedList<Region>();
	}

	public void addSubRegion(Region subRegion) {
		if (!subRegions.contains(subRegion))
			subRegions.add(subRegion);
	}

	/** @return A string with the name of the player that fully owns this SuperRegion */
	public String ownedByPlayer() {
		String playerName = subRegions.getFirst().getPlayerName();
		for (Region region : subRegions) {
			if (!playerName.equals(region.getPlayerName()))
				return null;
		}
		return playerName;
	}

	/** @return The number of armies a Player is rewarded when he fully owns this SuperRegion */
	public int getArmiesReward() {
		return armiesReward;
	}

	/** @return A list with the Regions that are part of this SuperRegion */
	public LinkedList<Region> getSubRegions() {
		return subRegions;
	}

	public LinkedList<Region> getUnknownSubRegions() {
		LinkedList<Region> unknown = new LinkedList<Region>(subRegions);
		Iterator<Region> it = unknown.iterator();
		while (it.hasNext()) {
			Region region = it.next();
			if (region.ownedByPlayer(GlobalState.getMyName()) || region.ownedByPlayer(GlobalState.getOpponentName())
					|| region.ownedByPlayer(GlobalState.getNeutralName())) {
				it.remove();
			}
		}
		return unknown;
	}

	public int getNeutralArmies() {
		int armies = 0;
		for (Region region : subRegions) {
			if (region.ownedByPlayer(GlobalState.getNeutralName())) {
				armies += region.getArmies();
			}
		}
		return armies;
	}

	public int getMyFreeArmiesNear() {
		int armies = 0;
		LinkedList<Region> checked = new LinkedList<Region>();
		for (Region region : subRegions) {
			LinkedList<Region> myRegions = region.getMyRegionsNear();
			for (Region myRegion : myRegions) {
				if (!checked.contains(myRegion)) {
					armies += myRegion.getFreeArmies();
					checked.add(myRegion);
				}
			}
		}
		return armies;
	}

	/** Бонус полностью открыт и на нем нет противников (только нейтралы)
	 * 
	 * @return */
	public boolean isFree() {
		for (Region subregion : subRegions) {
			if (!subregion.ownedByPlayer(GlobalState.getNeutralName())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String getName() {
		return SuperRegionName.getName(id);
	}

}
