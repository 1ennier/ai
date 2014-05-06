package temp;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RegionPickWeight {

	private static Properties p;

	public enum PROP {
		superRegionBonus, sameSuperRegionAndNear, neighbor, nearBonuses
	}

	public static void init() throws IOException {
		p = new Properties();
		InputStream fis = RegionPickWeight.class.getResourceAsStream("/regionpick.properties");
		p.load(fis);
		fis.close();
	}

	public static int getProp(PROP prop) {
		return Integer.parseInt(p.getProperty(prop.name()));
	}

}
