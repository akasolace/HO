package core.file.xml;

import core.util.HOLogger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import static core.file.xml.XMLManager.xmlValue2Hash;


public class XMLTeamDetailsParser {

	private XMLTeamDetailsParser() {}

	public static String fetchRegionID(String xmlFile) {
		return fetchTeamDetail(xmlFile, "Region", "RegionID");
	}

	public static String fetchLogoURI(String xmlFile) {
		return fetchTeamDetail(xmlFile, "LogoURL", null);
	}

	private static String fetchTeamDetail(String xmlFile, String section, String attribute){
		try {
			Document doc = XMLManager.parseString(xmlFile);

			if (doc == null) {
				return "-1";
			}

			// Tabelle erstellen
			Element root = doc.getDocumentElement();

			// Root wechseln
			root = (Element) root.getElementsByTagName("Team").item(0);
			root = (Element) root.getElementsByTagName(section).item(0);

			if(attribute != null) {
				root = (Element) root.getElementsByTagName(attribute).item(0);
			}

			return XMLManager.getFirstChildNodeValue(root);

		} catch (Exception ex) {
			HOLogger.instance().log(XMLTeamDetailsParser.class, ex);
		}

		return "-1";
	}

	public static Map<String, String> parseTeamdetailsFromString(String inputStream, int teamId) {
		return parseDetails(XMLManager.parseString(inputStream), teamId);
	}

	private static Map<String, String> parseDetails(@Nullable Document doc, int teamId) {
		Element ele, root;
		Map<String, String> hash = new core.file.xml.MyHashtable();

//		HOLogger.instance().debug(XMLTeamDetailsParser.class, "parsing teamDetails for teamID: " + teamId);

		if (doc == null) {
			return hash;
		}

		root = doc.getDocumentElement();

		try {

			// FetchedDate
			xmlValue2Hash(hash, root, "FetchedDate");

			// User
			root = (Element) root.getElementsByTagName("User").item(0);
			xmlValue2Hash(hash, root, "Loginname");
			xmlValue2Hash(hash, root, "LastLoginDate");

			String supportStatus = "False";
			NodeList supporterTier = root.getElementsByTagName("SupporterTier");
			if (supporterTier.getLength() > 0) {
				ele = (Element) supporterTier.item(0);
				String supportValue = XMLManager.getFirstChildNodeValue(ele);
				if (supportValue.trim().length() > 0) {
					supportStatus = "True";
				}
			}
			hash.put("HasSupporter", supportStatus);

			// We need to find the correct team

			Element team = null;
			ele = (Element) doc.getDocumentElement().getElementsByTagName("Teams").item(0);
			if ( ele != null) {
				root = ele;
				NodeList list = root.getElementsByTagName("Team");
				for (int i = 0; (list != null) && (i < list.getLength()); i++) {
					team = (Element) list.item(i);

					ele = (Element) team.getElementsByTagName("TeamID").item(0);
					if (Integer.parseInt(XMLManager.getFirstChildNodeValue(ele)) == teamId) {
						break;
					}
				}
			}
			else {
				team = (Element)doc.getDocumentElement().getElementsByTagName("Team").item(0);
			}

			if (team == null) { 
				return hash;
			}

			xmlValue2Hash(hash, team, "TeamID");
			xmlValue2Hash(hash, team, "TeamName");
			xmlValue2Hash(hash, team, "FoundedDate", "ActivationDate");
			xmlValue2Hash(hash, team, "HomePage");
			xmlValue2Hash(hash, team, "LogoURL");
			// youth team info
			xmlValue2Hash(hash, team, "YouthTeamID");
			xmlValue2Hash(hash, team, "YouthTeamName");

			root = (Element) team.getElementsByTagName("League").item(0);
			xmlValue2Hash(hash, root, "LeagueID");

			try {
				xmlValue2Hash(hash, team, "LeagueLevel");
				xmlValue2Hash(hash, team, "LeagueLevelUnitName");
				xmlValue2Hash(hash, team, "LeagueLevelUnitID");
			} catch (Exception ex) {
				HOLogger.instance().log(XMLTeamDetailsParser.class, ex);
			}

			try {
				xmlValue2Hash(hash, team, "NumberOfVictories");
				xmlValue2Hash(hash, team, "NumberOfUndefeated");
			} catch (Exception exp) {
				HOLogger.instance().log(XMLTeamDetailsParser.class, exp);
			}

			var fanclub = (Element) team.getElementsByTagName("Fanclub").item(0);
			xmlValue2Hash(hash, fanclub, "FanclubSize");

			root = (Element) team.getElementsByTagName("Trainer").item(0);
			xmlValue2Hash(hash, root, "PlayerID", "TrainerID");

			root = (Element) team.getElementsByTagName("Arena").item(0);
			xmlValue2Hash(hash, root, "ArenaName");
			xmlValue2Hash(hash, root, "ArenaID");
			root = (Element) team.getElementsByTagName("Region").item(0);
			xmlValue2Hash(hash, root, "RegionID");

			// Power Rating
			Element PowerRating = (Element)doc.getDocumentElement().getElementsByTagName("PowerRating").item(0);
			xmlValue2Hash(hash, PowerRating, "GlobalRanking");
			xmlValue2Hash(hash, PowerRating, "LeagueRanking");
			xmlValue2Hash(hash, PowerRating, "RegionRanking");
			xmlValue2Hash(hash, PowerRating, "PowerRating");

			if (team.getElementsByTagName("TeamRank").getLength() > 0) {
				hash.put("TeamRank", team.getElementsByTagName("TeamRank").item(0).getTextContent());
			}

		} catch (Exception e) {
			HOLogger.instance().log(XMLTeamDetailsParser.class, e);
		}

		return hash;
	}

	public static List<TeamInfo> getTeamInfoFromString(String input) {
		Document doc = XMLManager.parseString(input);
		List<TeamInfo> ret = new ArrayList<>();
		
		Element root = doc.getDocumentElement();
		root = (Element) root.getElementsByTagName("Teams").item(0);

		NodeList list = root.getElementsByTagName("Team");
		
		for (int i = 0; (list != null) && (i < list.getLength()); i++) {
			Element team = (Element) list.item(i);
			Element ele;
			
			TeamInfo info = new TeamInfo();

			ele = (Element) team.getElementsByTagName("TeamID").item(0);
			info.setTeamId(Integer.parseInt(XMLManager.getFirstChildNodeValue(ele)));

			ele = (Element) team.getElementsByTagName("YouthTeamID").item(0);
			info.setYouthTeamId(Integer.parseInt(XMLManager.getFirstChildNodeValue(ele)));

			ele = (Element) team.getElementsByTagName("TeamName").item(0);
			info.setName(XMLManager.getFirstChildNodeValue(ele));

			ele = (Element) team.getElementsByTagName("IsPrimaryClub").item(0);
			info.setPrimaryTeam(Boolean.parseBoolean(XMLManager.getFirstChildNodeValue(ele)));

			
			Element league = (Element) team.getElementsByTagName("League").item(0);
			ele = (Element) league.getElementsByTagName("LeagueName").item(0);
			info.setCountry(XMLManager.getFirstChildNodeValue(ele));
			
			ele = (Element) league.getElementsByTagName("LeagueID").item(0);
			info.setLeagueId(Integer.parseInt(XMLManager.getFirstChildNodeValue(ele)));
			
			ele = (Element) team.getElementsByTagName("LeagueLevelUnit").item(0);
			ele = (Element) ele.getElementsByTagName("LeagueLevelUnitName").item(0);
			info.setLeague(XMLManager.getFirstChildNodeValue(ele));
			
			ret.add(info);
		}
		
		return ret;
	}
		
}
