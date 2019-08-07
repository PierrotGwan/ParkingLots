package fr.gwan.parkinglots.domain;

public enum ParkingSlotTypeEnum {
    PARKING_SLOT_SEDAN("PARKING_SLOT_SEDAN"),
    
    PARKING_SLOT_20KW("PARKING_SLOT_20KW"),

    PARKING_SLOT_50KW("PARKING_SLOT_50KW");

    private String value;

    ParkingSlotTypeEnum(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static ParkingSlotTypeEnum fromValue(String text) {
      for (ParkingSlotTypeEnum b : ParkingSlotTypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }
