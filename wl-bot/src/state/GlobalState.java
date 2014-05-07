package state;

import graph.EdgeWeightedDigraph;

import java.util.ArrayList;

import main.Map;
import main.Region;

public class GlobalState {

	public static boolean debugPick;
	public static boolean debugArmyPlace;
	public static boolean debugAttack;

	private static String myName = "";
	private static String opponentName = "";
	private static String neutralName = "neutral";
	private static String unknownName = "unknown";

	private static final Map fullMap = new Map();

	private static ArrayList<Region> pickableStartingRegions = new ArrayList<Region>();
	private static ArrayList<Integer> preferredStartingRegionIds = new ArrayList<Integer>();

	private static ArrayList<Integer> opponentPickIds = new ArrayList<Integer>();

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

	public static void addPreferredStartingRegionId(int id) {
		preferredStartingRegionIds.add(id);
	}

	public static ArrayList<Integer> getPreferredStartingRegionIds() {
		return preferredStartingRegionIds;
	}

	public static void addOpponentPickId(Integer region) {
		if (!opponentPickIds.contains(region)) {
			opponentPickIds.add(region);
		}
	}

	public static void removeOpponentPickId(Integer region) {
		opponentPickIds.remove(region);
	}

	public static ArrayList<Integer> getOpponentPickIds() {
		return opponentPickIds;
	}

}
