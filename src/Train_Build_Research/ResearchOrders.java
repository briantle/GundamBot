package Train_Build_Research;
import bwapi.Game;
import bwapi.Player;
import bwapi.TechType;
import bwapi.Unit;
import bwapi.UnitType;

public class ResearchOrders
{
	public static void canResearch(Game game, Player self, Unit unit)
	{
		if (unit.getType() == UnitType.Terran_Machine_Shop && unit.isResearching() == false)
		{
			// If enough resources and siege mode hasn't been researched or is being researched, research siege mode
			if (self.minerals() >= 150 && self.gas() >= 100 && unit.canResearch(TechType.Tank_Siege_Mode, true)) {
				unit.research(TechType.Tank_Siege_Mode);
				return;
			}
			// If enough resources for the following tech
			if (self.minerals() >= 100 & self.gas() >= 100)
			{
				if (unit.canResearch(TechType.Spider_Mines, true)) {
					unit.research(TechType.Spider_Mines);	
					return;
				}
			}
		}
	}
}
