package state;

import graph.EdgeWeightedDigraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import main.Map;
import main.Region;
import main.SuperRegion;
import utils.Weight.SuperRegionPick;
import utils.comparator.desc.SuperRegionBonusSizeDescComparator;

public class GlobalState {

	private static String myName = "";
	private static String opponentName = "";
	private static String neutralName = "neutral";
	private static String unknownName = "unknown";

	private static final Map fullMap = new Map();

	private static ArrayList<Region> pickableStartingRegions = new ArrayList<Region>();

	private static CurrentState state = new CurrentState();

	private static EdgeWeightedDigraph graph;

	public static void setMyName(String name) {
		myName = name;
	}

	public static String getMyName() {
		return myName;
	}

	public static void setOpponentName(String name) {
		opponentName = name;
	}

	public static String getOpponentName() {
		return opponentName;
	}

	public static String getNeutralName() {
		return neutralName;
	}

	public static String getUnknownName() {
		return unknownName;
	}

	public static Map getFullMap() {
		return fullMap;
	}

	public static ArrayList<Region> getPickableStartingRegions() {
		return pickableStartingRegions;
	}

	public static void addPickableStartingRegion(Region region) {
		pickableStartingRegions.add(region);
	}

	public static int getTotalRegionsCount() {
		return fullMap.getRegions().size();
	}

	public static CurrentState getCurrentState() {
		return state;
	}

	public static void setGraph(EdgeWeightedDigraph g) {
		graph = g;
	}

	public static EdgeWeightedDigraph getGraph() {
		return graph;
	}

	public static void initSuperRegionWeightPick() {
		LinkedList<SuperRegion> superRegions = new LinkedList<SuperRegion>(fullMap.getSuperRegions());
		Collections.sort(superRegions, new SuperRegionBonusSizeDescComparator());
		int value = 0;
		int currentBonusSize = superRegions.getFirst().getArmiesReward();
		for (SuperRegion superRegion : superRegions) {
			if (superRegion.getArmiesReward() != currentBonusSize) {
				value++;
			}
			superRegion.incWeightPick(value, SuperRegionPick.BONUS);
			currentBonusSize = superRegion.getArmiesReward();
		}

	}
}
