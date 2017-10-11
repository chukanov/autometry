package ru.autometry.obd.utils;

/**
 * Created by jeck on 30/06/14
 */
public enum CommandsOffset {
  RPM(0x00, CommandType.RPM), // обороты (2 байта)
  VSS(0x02, CommandType.VSS), // скорость
  Flag1(0x08, CommandType.Flags), //15 0-SW Starter 1-SW AC 3-SW Brake
  Flag2(0x09, CommandType.Flags), //16 3- SCS самодиагностика
  Flag3(0x0A, CommandType.Flags), //17 2- VTS Vtec Control
  Flag4(0x0B, CommandType.Flags), //18 0-MainRelay 2-O2Heater 5-Engine
  Flag5(0x0C, CommandType.Flags), //19 3-VTEC E 7-Econo
  Flag6(0x0D, CommandType.Flags), //20
  Flag7(0x0E, CommandType.Flags), //21
  Flag8(0x0F, CommandType.Flags), //22
  ECT(0x10, CommandType.ECT), // темп антифриза
  IAT(0x11, CommandType.IAT), // температура воздуха
  MAP(0x12, CommandType.MAP), // давление во впуске
  PA(0x13, CommandType.PA), // давление атмосферы
  TPS(0x14, CommandType.TPS), // датчик дросс заслонки
  O2(0x15, CommandType.O2), // лямбда или датчик обедненной смеси
  Bat(0x17, CommandType.Bat), // напряжение борт сети
  ALTF(0x18, CommandType.ALTF), // Alternator FR Signal (ALTF) Сигнал контроля бортового напряжения
  CorrCT(0x20, CommandType.CorrCT), // крастковременная топл коррекция
  CorrLT(0x22, CommandType.CorrLT), // долговременная топл коррекция
  Inj(0x24, CommandType.InjTime),    //  (2 байта) время открытия форсунки, по ней я считал мгновенный расход топлива
  IngAdv1(0x26, CommandType.InjAdv1_FAngle),// это Угол ОЗ
  IngAdv2(0x27, CommandType.InjAdv2),// что-то с зажиганием
  IAC(0x29, CommandType.IAC), // The Idle air control valve, or IACV regulates the car's idle based on the coolant temperature.

  Knock(0x3C, CommandType.Knock), // Датчик детонации

  Errors1(0x40, CommandType.Errors),// Парсятся по пол-байта, если пол-байта не равны 0, то значит
  Errors2(0x50, CommandType.Errors),// есть такой код ошибки. Номер ошибки - смещение полубайта. смещение начинается от 0

  VSS2(0x61, CommandType.VSS), // скорость
  RPM2(0x62, CommandType.RPM), // обороты (2 байта)
  ECT2(0x64, CommandType.ECT), // темп антифриза
  IAT2(0x65, CommandType.IAT), // температура воздуха
  MAP2(0x66, CommandType.MAP), // давление во впуске
  PA2(0x67, CommandType.PA), // давление атмосферы
  TPS2(0x68, CommandType.TPS), // датчик дросс заслонуи
  Bat2(0x69, CommandType.Bat), // напряжение борт сети

  O22(0x6C, CommandType.O2), // лямбда или датчик обедненной смеси
  CorrCT2(0x6D, CommandType.CorrCT), // крастковременная топл коррекция
  IAC2(0x6E, CommandType.IAC), // The Idle air control valve, or IACV regulates the car's idle based on the coolant temperature.

  IDECU(0x76, CommandType.IDECU); // ID ECU

  public byte value;
  public CommandType answerType;

  CommandsOffset(int offset, CommandType type) {
    this.value = (byte) offset;
    this.answerType = type;
  }

}
