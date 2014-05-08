package strategy;

import java.util.ArrayList;

import main.Region;

public interface IStrategyPick extends IStrategy {

	public ArrayList<Region> getPrefferedStartingRegions();

}
