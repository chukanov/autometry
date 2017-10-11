package ru.autometry.obd.utils;

/**
 * Created by jeck on 08/08/14
 */
public enum CommandType {
  UnknownAnswer,
  RPM,
  VSS,

  Flags,

  ECT,
  IAT,
  MAP,
  PA,
  TPS,
  O2,
  Bat,
  ALTF,

  CorrCT,
  CorrLT,
  InjTime,
  InjAdv1_FAngle,
  InjAdv2,
  IAC,

  Knock,

  Errors,

  ClearErrors,

  IDECU;
}
