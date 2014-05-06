package main;

public class RegionName {

	public static String getName(int id, int armies) {
		String name = Integer.toString(id);
		switch (id) {
			case 1:
				name = "Alaska";
				break;
			case 2:
				name = "Northwest Territory";
				break;
			case 3:
				name = "Greenland";
				break;
			case 4:
				name = "Alberta";
				break;
			case 5:
				name = "Ontario";
				break;
			case 6:
				name = "Quebec";
				break;
			case 7:
				name = "Western United States";
				break;
			case 8:
				name = "Eastern United States";
				break;
			case 9:
				name = "Central America";
				break;
			case 10:
				name = "Venezuela";
				break;
			case 11:
				name = "Peru";
				break;
			case 12:
				name = "Brazil";
				break;
			case 13:
				name = "Argentina";
				break;
			case 14:
				name = "Iceland";
				break;
			case 15:
				name = "Great Britain";
				break;
			case 16:
				name = "Scandinavia";
				break;
			case 17:
				name = "Ukraine";
				break;
			case 18:
				name = "Western Europe";
				break;
			case 19:
				name = "Northern Europe";
				break;
			case 20:
				name = "Southern Europe";
				break;
			case 21:
				name = "North Africa";
				break;
			case 22:
				name = "Egypt";
				break;
			case 23:
				name = "East Africa";
				break;
			case 24:
				name = "Congo";
				break;
			case 25:
				name = "South Africa";
				break;
			case 26:
				name = "Madagascar";
				break;
			case 27:
				name = "Ural";
				break;
			case 28:
				name = "Siberia";
				break;
			case 29:
				name = "Yakutsk";
				break;
			case 30:
				name = "Kamchatka";
				break;
			case 31:
				name = "Irkutsk";
				break;
			case 32:
				name = "Kazakhstan";
				break;
			case 33:
				name = "China";
				break;
			case 34:
				name = "Mongolia";
				break;
			case 35:
				name = "Japan";
				break;
			case 36:
				name = "Middle East";
				break;
			case 37:
				name = "India";
				break;
			case 38:
				name = "Siam";
				break;
			case 39:
				name = "Indonesia";
				break;
			case 40:
				name = "New Guinea";
				break;
			case 41:
				name = "Western Australia";
				break;
			case 42:
				name = "Eastern Australia";
				break;
			default:
				name = "Unknown";
				break;
		}
		return name + "[" + id + "] (armies=" + armies + ")";
	}

}
