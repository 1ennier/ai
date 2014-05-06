package main;

import graph.DijkstraSP;
import graph.DirectedEdge;

import java.util.Iterator;
import java.util.LinkedList;

import state.GlobalState;
import utils.RegionUtils;

public class Region extends AbstractRegion {

	private LinkedList<Region> neighbors;
	private SuperRegion superRegion;
	private int armies;
	private String playerName;

	private int opponentArmies;
	private int neutralArmies;

	private int armiesUsedThisRound;

	private DijkstraSP shortestPaths;

	private double weightPick;
	private double pickCoeff = 1;

	public void incWeightPick(int multiplier) {
		if (GlobalState.debug) {
			System.out.println(this + ": add " + multiplier + "*" + pickCoeff);
		} else {
			System.err.println(this + ": add " + multiplier + "*" + pickCoeff);
		}
		weightPick += pickCoeff * multiplier;
	}

	public double getWeightPick() {
		return weightPick;
	}

	public void setPickCoeff(double coeff) {
		this.pickCoeff = coeff;
	}

	private Region(SuperRegion superRegion, int id) {
		super();
		this.id = id;
		this.superRegion = superRegion;
		this.neighbors = new LinkedList<Region>();

		superRegion.addSubRegion(this);
	}

	public Region(int id, SuperRegion superRegion) {
		this(superRegion, id);
		this.playerName = GlobalState.getUnknownName();
		this.armies = 0;
	}

	public Region(int id, SuperRegion superRegion, String playerName, int armies, DijkstraSP sp) {
		this(superRegion, id);
		this.playerName = playerName;
		this.armies = armies;
		this.shortestPaths = sp;
	}

	public void initShortestPaths() {
		shortestPaths = new DijkstraSP(GlobalState.getGraph(), id - 1);
	}

	public DijkstraSP getShortestPaths() {
		return shortestPaths;
	}

	public void addNeighbor(Region neighbor) {
		if (!neighbors.contains(neighbor)) {
			neighbors.add(neighbor);
			neighbor.addNeighbor(this);
			addToGraph(neighbor);
		}
	}

	private void addToGraph(Region neighbor) {
		boolean found = false;
		Iterable<DirectedEdge> adj = GlobalState.getGraph().adj(id - 1);
		Iterator<DirectedEdge> it = adj.iterator();
		while (it.hasNext()) {
			DirectedEdge edge = it.next();
			if (edge.from() == id || edge.to() == id) {
				found = true;
				continue;
			}
		}
		if (!found) {
			DirectedEdge edge = new DirectedEdge(id - 1, neighbor.getId() - 1, 1);
			GlobalState.getGraph().addEdge(edge);
			DirectedEdge edge2 = new DirectedEdge(neighbor.getId() - 1, id - 1, 1);
			GlobalState.getGraph().addEdge(edge2);
		}
	}

	/** @param region
	 *            a Region object
	 * @return True if this Region is a neighbor of given Region, false otherwise */
	public boolean isNeighbor(Region region) {
		if (neighbors.contains(region))
			return true;
		return false;
	}

	/** @param playerName
	 *            A string with a player's name
	 * @return True if this region is owned by given playerName, false otherwise */
	public boolean ownedByPlayer(String playerName) {
		if (playerName.equals(this.playerName))
			return true;
		return false;
	}

	/** @param armies
	 *            Sets the number of armies that are on this Region */
	public void setArmies(int armies) {
		this.armies = armies;
	}

	/** @param playerName
	 *            Sets the Name of the player that this Region belongs to */
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	/** @return A list of this Region's neighboring Regions */
	public LinkedList<Region> getNeighbors() {
		return neighbors;
	}

	/** @return The SuperRegion this Region is part of */
	public SuperRegion getSuperRegion() {
		return superRegion;
	}

	/** @return The number of armies on this region */
	public int getArmies() {
		return armies;
	}

	public int getFreeArmies() {
		return armies - 1;
	}

	/** @return A string with the name of the player that owns this region */
	public String getPlayerName() {
		return playerName;
	}

	public boolean hasOpponentNeighbor() {
		for (Region region : neighbors) {
			if (region.ownedByPlayer(GlobalState.getOpponentName())) {
				return true;
			}
		}
		return false;
	}

	public boolean hasNeutralNeighbor() {
		for (Region region : neighbors) {
			if (region.ownedByPlayer(GlobalState.getNeutralName())) {
				return true;
			}
		}
		return false;
	}

	public boolean isInner() {
		for (Region region : neighbors) {
			if (!region.ownedByPlayer(GlobalState.getMyName())) {
				return false;
			}
		}
		return true;
	}

	public boolean isBorder() {
		return !isInner();
	}

	public void setOpponentArmies(int armies) {
		this.opponentArmies = armies;
	}

	public int getOpponentArmies() {
		return opponentArmies;
	}

	public void setNeutralArmies(int armies) {
		this.neutralArmies = armies;
	}

	public int getNeutralArmies() {
		return neutralArmies;
	}

	public int getArmiesUsedThisRound() {
		return armiesUsedThisRound;
	}

	public boolean canUseArmies(int armies) {
		return armies < this.armies - armiesUsedThisRound;
	}

	public void useArmies(int armies) {
		armiesUsedThisRound += armies;
	}

	public LinkedList<Region> getMyRegionNeutralNeighbors() {
		LinkedList<Region> neutralNeighbors = new LinkedList<Region>(neighbors);
		Iterator<Region> it = neutralNeighbors.iterator();
		while (it.hasNext()) {
			Region neighbor = it.next();
			if (neighbor.ownedByPlayer(GlobalState.getMyName()) || neighbor.ownedByPlayer(GlobalState.getOpponentName())) {
				it.remove();
			}
		}
		return neutralNeighbors;
	}

	public LinkedList<Region> getMyRegionSameSuperRegionNeutralNeighbors() {
		LinkedList<Region> neutralNeighbors = new LinkedList<Region>(neighbors);
		Iterator<Region> it = neutralNeighbors.iterator();
		while (it.hasNext()) {
			Region neighbor = it.next();
			if (neighbor.ownedByPlayer(GlobalState.getMyName()) || neighbor.ownedByPlayer(GlobalState.getOpponentName())
					|| !neighbor.getSuperRegion().equals(superRegion)) {
				it.remove();
			}
		}
		return neutralNeighbors;
	}

	public LinkedList<Region> getMyRegionOpponentNeighbors() {
		LinkedList<Region> opponentNeighbors = new LinkedList<Region>(neighbors);
		Iterator<Region> it = opponentNeighbors.iterator();
		while (it.hasNext()) {
			Region neighbor = it.next();
			if (!neighbor.ownedByPlayer(GlobalState.getOpponentName())) {
				it.remove();
			}
		}
		return opponentNeighbors;
	}

	public boolean hasSingleOpponent() {
		if (isInner()) {
			return false;
		}
		LinkedList<Region> neutrals = getMyRegionNeutralNeighbors();
		if (neutrals.size() > 1) {
			return false;
		}
		LinkedList<Region> opponents = getMyRegionOpponentNeighbors();
		return neutrals.size() + opponents.size() == 1;
	}

	public boolean hasOnlyMyNeighbors() {
		LinkedList<Region> regionNeighbors = new LinkedList<Region>(neighbors);
		Iterator<Region> it = regionNeighbors.iterator();
		while (it.hasNext()) {
			Region neighbor = it.next();
			if (neighbor.ownedByPlayer(GlobalState.getMyName())) {
				it.remove();
			}
		}
		return regionNeighbors.isEmpty();
	}

	public LinkedList<Region> getMyRegionsNear() {
		LinkedList<Region> myRegionsNear = new LinkedList<Region>(neighbors);
		Iterator<Region> it = myRegionsNear.iterator();
		while (it.hasNext()) {
			Region myRegion = it.next();
			if (!myRegion.ownedByPlayer(GlobalState.getMyName())) {
				it.remove();
			}
		}
		return myRegionsNear;
	}

	public Region getNearestBorder() {
		int minDistance = Integer.MAX_VALUE;
		Region target = null;
		LinkedList<Region> myBorderRegions = RegionUtils.getMyBorderRegions();
		for (Region borderRegion : myBorderRegions) {
			int b = borderRegion.getId() - 1;
			int dist = shortestPaths.distTo(b);
			if (dist < minDistance) {
				minDistance = dist;
				target = borderRegion;
			}
			if (minDistance == 0) {
				break;
			}
		}
		return target;
	}

	public Iterable<DirectedEdge> getPathToRegion(Region target) {
		if (shortestPaths.hasPathTo(target.getId() - 1)) {
			return shortestPaths.pathTo(target.getId() - 1);
		}
		return null;
	}

	public Region getMyRegionToTransferFromInner() {
		Region nearestBorder = getNearestBorder();
		if (nearestBorder == null) {
			return null;
		}

		return getFirstInPath(nearestBorder);
	}

	public Region getFirstInPath(Region endOfPath) {
		int toId = 0;
		Iterable<DirectedEdge> iter = getPathToRegion(endOfPath);
		if (iter != null) {
			Iterator<DirectedEdge> it = iter.iterator();
			if (it.hasNext()) {
				DirectedEdge edge = it.next();
				toId = edge.from() == id - 1 ? edge.to() + 1 : edge.from() + 1;
			}
		}

		if (toId > 0) {
			for (Region neighbor : getMyRegionsNear()) {
				if (neighbor.getId() == toId) {
					return neighbor;
				}
			}
		}

		return null;
	}

	/** @deprecated
	 * @return */
	public boolean hasBorderNeighbor() {
		return !getBorderNeighbors().isEmpty();
	}

	/** @deprecated
	 * @return */
	public LinkedList<Region> getBorderNeighbors() {
		LinkedList<Region> myNeighbors = new LinkedList<Region>(neighbors);
		Iterator<Region> it = myNeighbors.iterator();
		while (it.hasNext()) {
			Region neighbor = it.next();
			if (neighbor.ownedByPlayer(GlobalState.getMyName())) {
				it.remove();
			}
		}
		return myNeighbors;
	}

	/** Кол-во армий противника
	 * 
	 * @return */
	public int getOpponentArmies(LinkedList<Region> regions) {
		int armies = 0;
		for (Region neighbor : neighbors) {
			if (neighbor.ownedByPlayer(GlobalState.getOpponentName())) {
				armies += neighbor.getArmies();
			}
		}
		return armies;
	}

	@Override
	protected String getName() {
		String result = RegionName.getName(id, armies);
		if (GlobalState.debug) {
			result += " [wp:" + weightPick + "]";
		}
		return result;
	}

}
