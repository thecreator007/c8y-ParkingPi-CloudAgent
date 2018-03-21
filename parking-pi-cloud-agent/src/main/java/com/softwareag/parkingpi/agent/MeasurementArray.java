package com.softwareag.parkingpi.agent;

import org.joda.time.DateTime;

import java.math.BigDecimal;

public class MeasurementArray {
    BigDecimal value;
    DateTime dateTime;
  public MeasurementArray(BigDecimal value,DateTime dateTime) {
      this.value = value;
      this.dateTime = dateTime;
  }

    public BigDecimal getValue() {
        return value;
    }

    public DateTime getDateTime() {
        return dateTime;
    }
}
