package core.model.match;

import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.util.HOLogger;
import module.lineup.Lineup;
import module.lineup.substitution.model.MatchOrderType;
import module.lineup.substitution.model.Substitution;

import java.util.*;

public class MatchLineupTeam {

	private SourceSystem sourceSystem;
	private String teamName;
	private Vector<MatchLineupPlayer> lineup = new Vector<>();
	private ArrayList<Substitution> substitutions = new ArrayList<>();
	private int experience;
	private int teamId;
	private int styleOfPlay;
	// null player to fill empty spots
	private final static MatchLineupPlayer NULLPLAYER = new MatchLineupPlayer(SourceSystem.HATTRICK, -1, 0, -1, -1d, "", 0);
	private MatchType matchType = MatchType.NONE;
	private int matchId;
	private Matchdetails matchdetails;

	// ~ Constructors
	// -------------------------------------------------------------------------------

	/**
	 * Creates a new instance of MatchLineupTeam
	 */
	public MatchLineupTeam(SourceSystem sourceSystem, int matchId, String teamName, int teamID, int erfahrung, int styleOfPlay) {
		this.sourceSystem = sourceSystem;
		this.teamName = teamName;
		experience = erfahrung;
		teamId = teamID;
		this.styleOfPlay = styleOfPlay;
		this.matchId = matchId;
	}

	// ~ Methods
	// ------------------------------------------------------------------------------------

	/**
	 * Setter for property m_vAufstellung.
	 * 
	 * @param m_vAufstellung
	 *            New value of property m_vAufstellung.
	 */
	public final void setLineup(Vector<MatchLineupPlayer> m_vAufstellung) {
		this.lineup = m_vAufstellung;
	}

	/**
	 * Getter for property m_vAufstellung.
	 * 
	 * @return Value of property m_vAufstellung.
	 */
	public final Vector<MatchLineupPlayer> getLineup() {
		return lineup;
	}

	/**
	 * Setter for property m_vSubstitutions.
	 * 
	 * @param substitutions
	 *            New value of property m_vSubstitutions.
	 */
	public final void setSubstitutions(List<Substitution> substitutions) {

		// defensive copy
		this.substitutions = new ArrayList<>(substitutions);

		// Make sure substitutions are sorted first on minute, then by ID.
		this.substitutions.sort(new Comparator<Substitution>() {
			@Override
			public int compare(Substitution o1, Substitution o2) {

				if (o1.getMatchMinuteCriteria() == o2.getMatchMinuteCriteria()) {
					return Integer.compare(o1.getPlayerOrderId(), o2.getPlayerOrderId());

				}

				if (o1.getMatchMinuteCriteria() < o2.getMatchMinuteCriteria()) {
					return -1;
				}

				// minutes in o1 is greater than o2
				return 1;
			}

			public boolean equals(Substitution o1, Substitution o2) {
				// Lazy solution, a proper would compare all fields, but we
				// don't need that.
				return o1 == o2;
			}
		});
	}

	/**
	 * Getter for property m_vSubstitutions.
	 * 
	 * @return Value of property m_vSubstitutions.
	 */
	public final List<Substitution> getSubstitutions() {
		return substitutions;
	}

	/**
	 * Setter for property m_iErfahrung.
	 * 
	 * @param m_iErfahrung
	 *            New value of property m_iErfahrung.
	 */
	public final void setExperience(int m_iErfahrung) {
		this.experience = m_iErfahrung;
	}

	/**
	 * Getter for property m_iErfahrung.
	 * 
	 * @return Value of property m_iErfahrung.
	 */
	public final int getExperience() {
		return experience;
	}

	/**
	 * Returns a player by ID, players in captain and set piece positions are
	 * ignored.
	 * 
	 * @param id
	 *            The spielerId of the player
	 * 
	 * @return The object matching the criteria, or null if none found
	 */
	public final MatchLineupPlayer getPlayerByID(int id) {

		for (MatchLineupPlayer player : lineup) {
			if (player.getPlayerId() == id) {
				if (player.getId() != IMatchRoleID.captain && (player.getId() != IMatchRoleID.setPieces)) {
					return player;
				}
			}
		}

		return null;
	}

	/**
	 * Liefert Einen Player per PositionsID aus der Aufstellung
	 */
	public final MatchLineupPlayer getPlayerByPosition(int id) {
		MatchLineupPlayer player;

		for (int i = 0; (lineup != null) && (i < lineup.size()); i++) {
			player = lineup.elementAt(i);

			if (player.getId() == id) {
				return player;
			}
		}
		return NULLPLAYER;
	}

	/**
	 * Setter for property m_iStyleOfPlay.
	 * 
	 * @param m_iStyleOfPlay
	 *            New value of property m_iStyleOfPlay.
	 */
	public final void setStyleOfPlay(int m_iStyleOfPlay) {
		this.styleOfPlay = m_iStyleOfPlay;
	}

	/**
	 * Getter for property m_iStyleOfPlay.
	 * 
	 * @return Value of property m_iStyleOfPlay.
	 */
	public final int getStyleOfPlay() {
		return styleOfPlay;
	}
	
	// returns offensive, defensive or neutral depending on styleOfPlay
	// e.g. -3 is 30% defensive, 10 is 100% offensive
	public static String getStyleOfPlayName(int styleOfPlay) {
		HOVerwaltung hov = HOVerwaltung.instance();
		String s;
		
		if (styleOfPlay == 0) {
			return hov.getLanguageString("ls.team.styleofplay.neutral");
		} else {
			s = (styleOfPlay > 0) ? hov.getLanguageString("ls.team.styleofplay.offensive") :
				hov.getLanguageString("ls.team.styleofplay.defensive"); 
		}
		return Math.abs(styleOfPlay * 10) + "% " + s;
	}
	
	/**
	 * Setter for property m_iTeamID.
	 * 
	 * @param m_iTeamID
	 *            New value of property m_iTeamID.
	 */
	public final void setTeamID(int m_iTeamID) {
		this.teamId = m_iTeamID;
	}

	/**
	 * Getter for property m_iTeamID.
	 * 
	 * @return Value of property m_iTeamID.
	 */
	public final int getTeamID() {
		return teamId;
	}

	/**
	 * Setter for property m_sTeamName.
	 * 
	 * @param m_sTeamName
	 *            New value of property m_sTeamName.
	 */
	public final void setTeamName(java.lang.String m_sTeamName) {
		this.teamName = m_sTeamName;
	}

	/**
	 * Getter for property m_sTeamName.
	 * 
	 * @return Value of property m_sTeamName.
	 */
	public final java.lang.String getTeamName() {
		return teamName;
	}

	public final void add2Lineup(MatchLineupPlayer player) {
		lineup.add(player);
	}

	/**
	 * Determines the played formation.
	 */
	public final byte determineSystem() {
		short abw = 0;
		short mf = 0;
		short st = 0;

		MatchLineupPlayer player;

		for (MatchLineupPlayer matchLineupPlayer : lineup) {
			player = matchLineupPlayer;

			if (player != null) {
				switch (player.getPosition()) {
					case IMatchRoleID.UNKNOWN:
						break;

					case IMatchRoleID.BACK:
					case IMatchRoleID.BACK_TOMID:
					case IMatchRoleID.BACK_OFF:
					case IMatchRoleID.BACK_DEF:
					case IMatchRoleID.CENTRAL_DEFENDER:
					case IMatchRoleID.CENTRAL_DEFENDER_TOWING:
					case IMatchRoleID.CENTRAL_DEFENDER_OFF:
						abw++;
						break;

					case IMatchRoleID.MIDFIELDER:
					case IMatchRoleID.MIDFIELDER_OFF:
					case IMatchRoleID.MIDFIELDER_DEF:
					case IMatchRoleID.MIDFIELDER_TOWING:
					case IMatchRoleID.WINGER:
					case IMatchRoleID.WINGER_TOMID:
					case IMatchRoleID.WINGER_OFF:
					case IMatchRoleID.WINGER_DEF:
						mf++;
						break;

					case IMatchRoleID.FORWARD:
					case IMatchRoleID.FORWARD_TOWING:
					case IMatchRoleID.FORWARD_DEF:
						st++;
						break;
				}
			}
		}

		if (abw == 2) {
			// 253
			if (mf == 5) {
				return Lineup.SYS_253;
			}
			// MURKS
			else {
				return Lineup.SYS_MURKS;
			}
		} else if (abw == 3) {
			// 343
			if (mf == 4) {
				return Lineup.SYS_343;
			} // 352
			else if ((mf == 5) && (st == 2)) {
				return Lineup.SYS_352;
			}
			// MURKS
			else {
				return Lineup.SYS_MURKS;
			}
		} else if (abw == 4) {
			// 433
			if ((mf == 3) && (st == 3)) {
				return Lineup.SYS_433;
			} // 442
			else if ((mf == 4) && (st == 2)) {
				return Lineup.SYS_442;
			} // 451
			else if ((mf == 5) && (st == 1)) {
				return Lineup.SYS_451;
			}
			// MURKS
			else {
				return Lineup.SYS_MURKS;
			}
		} else if (abw == 5) {
			// 532
			if ((mf == 3) && (st == 2)) {
				return Lineup.SYS_532;
			} // 541
			else if ((mf == 4) && (st == 1)) {
				return Lineup.SYS_541;
			} // 523
			else if ((mf == 2) && (st == 3)) {
				return Lineup.SYS_523;
			} // 550
			else if ((mf == 5) && (st == 0)) {
				return Lineup.SYS_550;
			}
			// MURKS
			else {
				return Lineup.SYS_MURKS;
			}
		} // MURKS
		else {
			return Lineup.SYS_MURKS;
		}
	}

	public MatchType getMatchType() {
		if (matchType == MatchType.NONE) {
			MatchKurzInfo info = DBManager.instance().getMatchesKurzInfoByMatchID(this.matchId);
			if ( info != null){
				matchType = info.getMatchType();
			}
		}
		return matchType;
	}

	public SourceSystem getSourceSystem() {
		return sourceSystem;
	}

	public void setSourceSystem(SourceSystem sourceSystem) {
		this.sourceSystem = sourceSystem;
	}

	public int getTrainMinutesPlayedInPositions(int spielerId, int[] accepted, boolean isWalkoverMatchWin) {
		if ( accepted!=null && accepted.length==0) return 0;	// NO positions are accepted
		MatchLineupPlayer player = this.getPlayerByID(spielerId);
		if (player == null) {
			return 0;
		}

		boolean inPosition = false;

		int enterMin = -1;
		int minPlayed = 0;
		// Those in the starting lineup entered at minute 0
		if (isPositionInAcceptedPositions(player.getStartPosition(), accepted)) {
			if (isWalkoverMatchWin) {
				// Opponent team did not appear
				if (accepted != null) {
					// Normal training
					return 90;
				}
				// No experience
				return 0;
			}

			enterMin = 0;
			inPosition = true;
		}

		List<Substitution> substitutions = this.getSubstitutions();
		// The substitutions are sorted on minute. Look for substitutions
		// involving the player, and check his position
		// after the substitution (on the substitution minute). Work through the
		// list, and add minutes depending on
		// entering/leaving the accepted position list.
		for (Substitution substitution : substitutions) {
			if (substitution == null) {
				HOLogger.instance().debug(getClass(),
						"getMinutesPlayedError, null in substitution list");
				break;
			}

			if (substitution.getOrderType() == MatchOrderType.SUBSTITUTION ||
					substitution.getOrderType() == MatchOrderType.POSITION_SWAP) {
				if ((substitution.getObjectPlayerID() == spielerId) || (substitution.getSubjectPlayerID() == spielerId)) {
					int newpos = getPlayerFieldPositionAtMinute(spielerId, substitution.getMatchMinuteCriteria());
					boolean newPosAccepted =  isPositionInAcceptedPositions(newpos, accepted);
					if (inPosition && !newPosAccepted) {
						// He left a counting position.
						minPlayed += substitution.getMatchMinuteCriteria() - enterMin;
						inPosition = false;
					} else if (!inPosition && newPosAccepted) {
						// He entered a counting position
						enterMin = substitution.getMatchMinuteCriteria();
						inPosition = true;
					}
				}
			}
		}
		// Done with substitutions, add end if necessary
		if (inPosition) {
			minPlayed += getMatchEndMinute(spielerId) - enterMin;
		}

		return minPlayed;
	}

	public int getPlayerFieldPositionAtMinute(int spielerId, int minute) {
		// Captain and set piece taker don't count...
		if ((minute >= getMatchEndMinute(spielerId)) || (minute < 0)) {
			// The player is at home (they travel fast)...
			return -1;
		}

		var player = this.getPlayerByID(spielerId);
		if ( player == null) {
			// Was never on the field
			return -1;
		}

		int ret = player.getStartPosition();
		HashMap<Integer, Integer> changedPositions = new HashMap<>();	// player id, new Position
		for ( var subs : this.getSubstitutions()){
			if ( subs.getMatchMinuteCriteria() > minute) break;
			if ( subs.getOrderType() == MatchOrderType.POSITION_SWAP || subs.getOrderType() == MatchOrderType.SUBSTITUTION){
				var subPos = changedPositions.get(subs.getSubjectPlayerID());
				if ( subPos == null ) {
					subPos = this.getPlayerByID(subs.getSubjectPlayerID()).getStartPosition();
				}
				var objPos = changedPositions.get(subs.getObjectPlayerID());
				if ( objPos == null){
					objPos = this.getPlayerByID(subs.getObjectPlayerID()).getStartPosition();
				}
				changedPositions.put(subs.getSubjectPlayerID(), objPos);
				changedPositions.put(subs.getObjectPlayerID(), subPos);
				if ( spielerId == subs.getSubjectPlayerID()){
					ret = objPos;
				}
				else if ( spielerId == subs.getObjectPlayerID()){
					ret = subPos;
				}
			}
		}

		return ret;
		/*

		// Look for the last substitution before the given minute
		// Check if the player is involved. If not keep checking back in time
		// until match start.

		List<Substitution> substitutions = this.getSubstitutions();
		Substitution tmpSub;
		for (int i = substitutions.size() - 1; i >= 0; i--) {

			tmpSub = substitutions.get(i);
			if (tmpSub.getMatchMinuteCriteria() > minute) {
				// This is after our minute. Next, please.
				continue;
			}

			if ((tmpSub.getSubjectPlayerID() == spielerId) || (tmpSub.getObjectPlayerID() == spielerId)) {
				return getPlayerFieldPostitionAfterSubstitution(spielerId, i, substitutions);
			}
		}

		// We survived all the subs, lets see if we found him in the starting
		// lineup.
		return (this.getPlayerByID(spielerId)).getStartPosition();*/
	}

	public int getMatchEndMinute(int spielerId) {
		var hls = getMatchdetails().getHighlights(); // DBManager.instance().getMatchDetails(matchId).getHighlights();
		for (MatchEvent hl : hls) {
			MatchEvent.MatchEventID me = MatchEvent.MatchEventID.fromMatchEventID(hl.getiMatchEventID());
			if (me == MatchEvent.MatchEventID.MATCH_FINISHED) {
				return hl.getMinute();
			} else if (hl.getSpielerID() == spielerId) {
				if (hl.isInjured() || hl.isRedCard()) {
					return hl.getMinute();
				}
			}
		}
		return 0;
	}

	private Matchdetails getMatchdetails() {
		if ( matchdetails == null){
			matchdetails = DBManager.instance().loadMatchDetails(this.sourceSystem.getValue(), this.matchId);
		}
		return matchdetails;
	}
/*
	private int getPlayerFieldPostitionAfterSubstitution(int spielerId, int arrIndex,
														 List<Substitution> substitutions) {
		// arrIndex should be the index of the sub in the substitution vector.
		// We have 100%
		// trust in our caller (this is a private method), and never verify
		// that.

		// This is the api logic:
		// The sub order contains a position, pos.
		// - Player swap: Pos contains data for the new playerOut position
		// - Normal sub: Pos contains data for the new playerIn position
		// - Red card: Pos contains data (0) for playerOut position
		// PlayerIn is empty
		// - Repositioning: The player is both playerIn and playerOut,
		// pos contains is his new one.

		if (arrIndex < 0) {
			// We have run out of substitutions. Start lineup got answer
			return (this.getPlayerByID(spielerId)).getStartPosition();
		}

		Substitution tmpSub = substitutions.get(arrIndex);

		if ((tmpSub.getObjectPlayerID() != spielerId) &&
				(tmpSub.getSubjectPlayerID() != spielerId)
				|| tmpSub.getOrderType() == MatchOrderType.MAN_MARKING		// man marking order has no influence on positions
		) {
			// This substitution is not exciting, check the next one
			return getPlayerFieldPostitionAfterSubstitution(spielerId, arrIndex - 1, substitutions);
		}

		for (int i = arrIndex; i >= 0; i--) {
			tmpSub = substitutions.get(i);
			if (tmpSub.getSubjectPlayerID() == spielerId) {

				if (tmpSub.getObjectPlayerID() == spielerId) {
					// Repositioning
					return tmpSub.getRoleId();
				}

				if ((tmpSub.getObjectPlayerID() == 0) && (tmpSub.getRoleId() == 0)) {
					// Sent off or no sub after injury
					return -1;
				}

				if (tmpSub.getOrderType().getId() == 1) {
					// Normal substitution and he left the field
					return -1;
				}

				if (tmpSub.getOrderType().getId() == 3) {
					// Player swap
					// The sub object got his new position
					return tmpSub.getRoleId();
				}

				HOLogger.instance().debug(
						getClass(),
						"getPlayerFieldPostitionAfterSubstitution had a playerOut fall through. "
								+ this.matchId + " " + spielerId + " " + tmpSub.getPlayerOrderId());
			}

			if (tmpSub.getObjectPlayerID() == spielerId) {
				// Repositioning is already caught.
				// Sent off does not exist here.

				if (tmpSub.getOrderType().getId() == 1) {
					// A sub entering. His position is in the sub object.
					return tmpSub.getRoleId();
				}

				if (tmpSub.getOrderType().getId() == 3) {
					// A player swap. We need to know where the other player
					// came from.
					// We figure this out by asking where he was at the end of
					// the previous sub
					// object (it is safe no matter the value of i).
					return getPlayerFieldPostitionAfterSubstitution(tmpSub.getSubjectPlayerID(), i - 1,
							substitutions);
				}

				HOLogger.instance().debug(
						getClass(),
						"getPlayerFieldPostitionAfterSubstitution had a playerIn fall through. "
								+ this.matchId + " " + spielerId + " " + tmpSub.getPlayerOrderId());
			}
		} // End for loop

		HOLogger.instance().debug(
				getClass(),
				"getPlayerFieldPostitionAfterSubstitution reached the end, which should never happen "
						+this.matchId + " " + spielerId + " " + tmpSub.getPlayerOrderId());
		return -1;
	}
*/


	/**
	 * Check if specified player is in one of the accepted positions
	 *
	 * @param playerId Id of the player
	 * @param accepted list of the accepted positions. List may also include special positions set pieces or captain.
	 *                 empty list => no position is accepted
	 *                 null => any position is accepted
	 * @return
	 * 	true, if player is in one of the accepted starting positions.
	 * 	false, if not
	 */
	private boolean isPlayerInAcceptedPositions(int playerId, int[] accepted) {
		if (accepted == null) {
			var role = this.getPlayerByID(playerId);
			return role != null && role.isFieldMatchRoleId(); // all positions are accepted, use an empty array if NO position should be accepted
		}

		for (int position : accepted) {
			var player = this.getPlayerByPosition(position);
			if ( player != null && player.getPlayerId() == playerId) {
				return true;
			}
		}
		return false;
	}

	private boolean isPositionInAcceptedPositions(int pos, int[] accepted) {
		if (accepted == null)
			return MatchRoleID.isFieldMatchRoleId(pos); // all positions are accepted, use an empty array if NO position should be accepted

		for (int value : accepted) {
			if (value == pos) {
				return true;
			}
		}
		return false;
	}

	public void setMatchDetails(Matchdetails details) {
		this.matchdetails = details;
	}

	public Map<MatchRoleID.Sector, Integer> getTrainMinutesPlayedInSectors(int playerId) {
		var ret = new HashMap<MatchRoleID.Sector, Integer>();
		var player = this.getPlayerByID(playerId);
		if (player == null) return ret;

		int startminute = 0;
		int sumMinutes = 0;

		MatchRoleID.Sector sector;
		var startPosition = player.getStartPosition();
		if (startPosition != -1) {
			sector = MatchRoleID.getSector(startPosition);
		} else {
			sector = MatchRoleID.Sector.None;
		}

		HashMap<Integer, MatchRoleID.Sector> changedPositions = new HashMap<>();    // player id, new Position
		for (var subs : this.getSubstitutions()) {
			if (subs.getOrderType() == MatchOrderType.POSITION_SWAP || subs.getOrderType() == MatchOrderType.SUBSTITUTION) {
				var subSector = changedPositions.get(subs.getSubjectPlayerID());
				if (subSector == null) {
					subSector = MatchRoleID.getSector(this.getPlayerByID(subs.getSubjectPlayerID()).getStartPosition());
				}
				var objSector = changedPositions.get(subs.getObjectPlayerID());
				if (objSector == null) {
					objSector = MatchRoleID.getSector(this.getPlayerByID(subs.getObjectPlayerID()).getStartPosition());
				}
				if (subSector != objSector) {
					changedPositions.put(subs.getSubjectPlayerID(), objSector);
					changedPositions.put(subs.getObjectPlayerID(), subSector);
					if (playerId == subs.getSubjectPlayerID() || playerId == subs.getObjectPlayerID()) {
						if (sector != MatchRoleID.Sector.None) {
							var minutesInSector = subs.getMatchMinuteCriteria() - startminute;
							sumMinutes += minutesInSector;
							if (sumMinutes > 90) {
								minutesInSector -= (sumMinutes - 90);
							}
							if (minutesInSector > 0) {
								ret.put(sector, minutesInSector);
							}
						}
						if (playerId == subs.getSubjectPlayerID()) {
							sector = objSector;
						} else if (playerId == subs.getObjectPlayerID()) {
							sector = subSector;
						}
						startminute = subs.getMatchMinuteCriteria();
					}
				}
				if (sumMinutes >= 90) break;
			}
		}

		if (sector != MatchRoleID.Sector.None && sumMinutes < 90) {
			var minutesInSector = this.getMatchEndMinute(playerId) - startminute;
			sumMinutes += minutesInSector;
			if (sumMinutes > 90) {
				minutesInSector -= (sumMinutes - 90);
			}
			if (minutesInSector > 0) {
				ret.put(sector, minutesInSector);
			}
		}
		return ret;
	}


	public boolean hasPlayerPlayed(int playerId) {
		return this.getTrainMinutesPlayedInSectors(playerId).size() > 0;
	}
}
