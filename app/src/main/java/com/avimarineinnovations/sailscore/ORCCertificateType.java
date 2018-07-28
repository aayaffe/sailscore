package com.avimarineinnovations.sailscore;

/**
 * This file is part of an
 * Avi Marine Innovations project: sailscore
 * first created by aayaffe on 27/07/2018.
 */
public enum ORCCertificateType {
  INTERNATIONAL,CLUB;

  public static ORCCertificateType fromString(String c_type) {
    if (c_type.equals("INTL"))
      return INTERNATIONAL;
    else
      return CLUB;
  }
}
