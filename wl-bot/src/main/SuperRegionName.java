package main;

public class SuperRegionName {

	public static String getName(int id) {
		switch (id) {
			case 1:
				return "North America";
			case 2:
				return "South America";
			case 3:
				return "Europe";
			case 4:
				return "Africa";
			case 5:
				return "Asia";
			case 6:
				return "Australia";
			default:
				return "Unknown";
		}
	}

}
