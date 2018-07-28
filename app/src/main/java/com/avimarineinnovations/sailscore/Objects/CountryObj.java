package com.avimarineinnovations.sailscore.Objects;

import java.util.Date;

/**
 * This file is part of an
 * Avi Marine Innovations project: sailscore
 * first created by aayaffe on 26/07/2018.
 */
public class CountryObj {
  private String id;
  private String name;
  private int certificatesCount;
  private Date lastUpdate;
  private int internationalCertCount;
  private int clubCertCount;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getCertificatesCount() {
    return certificatesCount;
  }

  public void setCertificatesCount(int certificatesCount) {
    this.certificatesCount = certificatesCount;
  }

  public Date getLastUpdate() {
    return lastUpdate;
  }

  public void setLastUpdate(Date lastUpdate) {
    this.lastUpdate = lastUpdate;
  }

  public int getInternationalCertCount() {
    return internationalCertCount;
  }

  public void setInternationalCertCount(int internationalCertCount) {
    this.internationalCertCount = internationalCertCount;
  }

  public int getClubCertCount() {
    return clubCertCount;
  }

  public void setClubCertCount(int clubCertCount) {
    this.clubCertCount = clubCertCount;
  }
}
