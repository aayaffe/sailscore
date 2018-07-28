/*
 * This class defines a data type for use in the EntriesSelectListActivity.
 * It allows a single data type to be bound to a row in the list using the
 * EntriesSelectListAdapter custom list adapter.
 */

package com.avimarineinnovations.sailscore.Objects;

public class OrcRowObj {
	private boolean cbox_state = false;
	private String boatClass = "";
	private String yachtName = "";
	private double cdl = 0.0;
	private String sailno = "";

	public String getYachtName() {
		return yachtName;
	}

	public void setYachtName(String yachtName) {
		this.yachtName = yachtName;
	}

	public double getCdl() {
		return cdl;
	}

	public void setCdl(double cdl) {
		this.cdl = cdl;
	}

	public void setSail (String sailno) {
		this.sailno = sailno;
	}
	
	public String getSail () {
		return sailno;
	}
	
	public void setBoatClass (String boatclass) {
		this.boatClass = boatclass;
	}
	
	public String getBoatClass () {
		return boatClass;
	}
	
	public void setCheckState (Boolean state) {
		this.cbox_state = state;
	}
	
	public Boolean getCheckState () {
		return cbox_state;
	}

}
