package strategy;

import java.util.ArrayList;

import main.Region;

public interface IPickStrategy extends IStrategy {

	public ArrayList<Region> getPrefferedStartingRegions();

}
