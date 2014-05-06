package main;


public abstract class AbstractRegion {

	protected int id;

	public int getId() {
		return id;
	}

	protected abstract String getName();

	@Override
	public String toString() {
		String result = getName();
		return result;
	}
}
