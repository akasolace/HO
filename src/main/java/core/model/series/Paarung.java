package core.model.series;

import core.model.misc.Basics;
import core.util.HOLogger;
import org.jetbrains.annotations.NotNull;

public class Paarung implements Comparable<Paarung>{
    //~ Instance fields ----------------------------------------------------------------------------
    protected String m_sDatum = "";
    protected String m_sGastName = "";
    protected String m_sHeimName = "";
    protected int m_iGastId = -1;
    protected int m_iHeimId = -1;
    protected int m_iLigaId = -1;
    protected int m_iMatchId = -1;
    protected int m_iSaison = -1;
    protected int m_iSpieltag = -1;
    protected int m_iToreGast = -1;
    protected int m_iToreHeim = -1;
    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new instance of Paarung
     */
    public Paarung() {
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Setter for property m_lDatum.
     *
     * @param date New value of property m_lDatum.
     */
    public final void setDatum(String date) {
        if (date != null) {
            m_sDatum = date;
        }
    }

    /**
     * Getter for property m_lDatum.
     *
     * @return Value of property m_lDatum.
     */
    public final java.sql.Timestamp getDatum() {
        return Basics.parseHattrickDate(m_sDatum);
    }

    /**
     * Setter for property m_iGastId.
     *
     * @param m_iGastId New value of property m_iGastId.
     */
    public final void setGastId(int m_iGastId) {
        this.m_iGastId = m_iGastId;
    }

    /**
     * Getter for property m_iGastId.
     *
     * @return Value of property m_iGastId.
     */
    public final int getGastId() {
        return m_iGastId;
    }

    /**
     * Setter for property m_sGastName.
     *
     * @param m_sGastName New value of property m_sGastName.
     */
    public final void setGastName(java.lang.String m_sGastName) {
        this.m_sGastName = m_sGastName;
    }

    /**
     * Getter for property m_sGastName.
     *
     * @return Value of property m_sGastName.
     */
    public final java.lang.String getGastName() {
        return m_sGastName;
    }

    /**
     * Setter for property m_iHeimId.
     *
     * @param m_iHeimId New value of property m_iHeimId.
     */
    public final void setHeimId(int m_iHeimId) {
        this.m_iHeimId = m_iHeimId;
    }

    /**
     * Getter for property m_iHeimId.
     *
     * @return Value of property m_iHeimId.
     */
    public final int getHeimId() {
        return m_iHeimId;
    }

    /**
     * Setter for property m_sHeimName.
     *
     * @param m_sHeimName New value of property m_sHeimName.
     */
    public final void setHeimName(java.lang.String m_sHeimName) {
        this.m_sHeimName = m_sHeimName;
    }

    /**
     * Getter for property m_sHeimName.
     *
     * @return Value of property m_sHeimName.
     */
    public final java.lang.String getHeimName() {
        return m_sHeimName;
    }

    /**
     * Setter for property m_iLigaId.
     *
     * @param m_iLigaId New value of property m_iLigaId.
     */
    public final void setLigaId(int m_iLigaId) {
        this.m_iLigaId = m_iLigaId;
    }

    /**
     * Getter for property m_iLigaId.
     *
     * @return Value of property m_iLigaId.
     */
    public final int getLigaId() {
        return m_iLigaId;
    }

    /**
     * Setter for property m_iMatchId.
     *
     * @param m_iMatchId New value of property m_iMatchId.
     */
    public final void setMatchId(int m_iMatchId) {
        this.m_iMatchId = m_iMatchId;
    }

    /**
     * Getter for property m_iMatchId.
     *
     * @return Value of property m_iMatchId.
     */
    public final int getMatchId() {
        return m_iMatchId;
    }

    /**
     * Setter for property m_iSaison.
     *
     * @param m_iSaison New value of property m_iSaison.
     */
    public final void setSaison(int m_iSaison) {
        this.m_iSaison = m_iSaison;
    }

    /**
     * Getter for property m_iSaison.
     *
     * @return Value of property m_iSaison.
     */
    public final int getSaison() {
        return m_iSaison;
    }

    /**
     * Setter for property m_iSpieltag.
     *
     * @param m_iSpieltag New value of property m_iSpieltag.
     */
    public final void setSpieltag(int m_iSpieltag) {
        this.m_iSpieltag = m_iSpieltag;
    }

    /**
     * Getter for property m_iSpieltag.
     *
     * @return Value of property m_iSpieltag.
     */
    public final int getSpieltag() {
        return m_iSpieltag;
    }

    public final String getStringDate() {
        return m_sDatum;
    }

    /**
     * Setter for property m_iToreGast.
     *
     * @param m_iToreGast New value of property m_iToreGast.
     */
    public final void setToreGast(int m_iToreGast) {
        this.m_iToreGast = m_iToreGast;
    }

    /**
     * Getter for property m_iToreGast.
     *
     * @return Value of property m_iToreGast.
     */
    public final int getToreGast() {
        return m_iToreGast;
    }

    /**
     * Setter for property m_iToreHeim.
     *
     * @param m_iToreHeim New value of property m_iToreHeim.
     */
    public final void setToreHeim(int m_iToreHeim) {
        this.m_iToreHeim = m_iToreHeim;
    }

    /**
     * Getter for property m_iToreHeim.
     *
     * @return Value of property m_iToreHeim.
     */
    public final int getToreHeim() {
        return m_iToreHeim;
    }

    /**
     * vergleicht anhand des SPieltages um eine nach Spieltagen sortierte Liste zu bekommen
     */
    public final int compareTo(@NotNull Paarung obj) {

        if (m_iSpieltag > obj.getSpieltag()) {
            return 1;
        } else if (m_iSpieltag < obj.getSpieltag()) {
            return -1;
        }
        
        return 0;
    }

    ////////////////////////////////////////////////////////////////////////////////
    @Override
	public final boolean equals(Object obj) {
        if (obj instanceof Paarung) {
            final Paarung spiel = (Paarung) obj;

            return (spiel.getStringDate().equals(m_sDatum))
                    && (spiel.getGastId() == m_iGastId)
                    && (spiel.getGastName().equals(m_sGastName))
                    && (spiel.getHeimId() == m_iHeimId)
                    && (spiel.getHeimName().equals(m_sHeimName))
                    && (spiel.getMatchId() == m_iMatchId)
                    && (spiel.getSpieltag() == m_iSpieltag)
                    && (spiel.getToreGast() == m_iToreGast)
                    && (spiel.getToreHeim() == m_iToreHeim);
        }

        return false;
    }

    public final boolean isGameOver() {
        return (m_iToreHeim > -1) && (m_iToreGast > -1);
    }
}
