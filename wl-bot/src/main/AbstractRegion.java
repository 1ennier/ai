package main;

public abstract class AbstractRegion {

	protected int id;
	private int weightPick;

	public int getId() {
		return id;
	}

	public void incWeightPick(int value, int multiplier) {
		weightPick += value * multiplier;
	}

	public int getWeightPick() {
		return weightPick;
	}

	protected abstract String getName();

	@Override
	public String toString() {
		return getName();
	}

}
