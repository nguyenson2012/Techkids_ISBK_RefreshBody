package com.example.asus.refreshbody.database.model;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Asus on 10/12/2016.
 */

public class DrinkIntakeItem{
    public static final String ID_DRINK = "idDrink";
    public static final String  DRINK_INTAKE_IMG_POS= "symbolPosition";
    public static final String NAME_DRINK="nameDrink";
    public static final String AMOUNT_DRINK="amountDrink";
    public static final String DRINK_ITAKE_TABLE="drink_intake_table";
    public static final String YEAR_DRINK="yearDrink";
    public static final String MONTH_DRINK="monthDrink";
    public static final String DAY_DRINK="dayDrink";
    public static final String HOUR_DRINK="hourDrink";
    public static final String MINUTE_DRINK="minuteDrink";
    public static final String DATE_STRING="dateString";
    public static final String IS_UPDATE="isUpdated";
    public static final String IS_DELETE="isDeleted";
    private String idDrink;
    private int symbolPosition;
    private String nameDrink;
    private int amountDrink;
    private String dateString;
    private boolean isUpdated;
    private boolean isDelete;
    private TimeDrink timeDrink;

    public DrinkIntakeItem(){}

    public DrinkIntakeItem(int symbol, String nameDrink, int amountDrink) {
        this.symbolPosition = symbol;
        this.nameDrink = nameDrink;
        this.amountDrink = amountDrink;
        isDelete=false;
        isUpdated=false;
    }

    public DrinkIntakeItem(int symbol, String nameDrink, int amountDrink, TimeDrink timeDrink) {
        this.symbolPosition = symbol;
        this.nameDrink = nameDrink;
        this.amountDrink = amountDrink;
        this.timeDrink = timeDrink;
        isDelete=false;
        isUpdated=false;
        dateString=convertToFormatDateString();
    }

    public DrinkIntakeItem(String idDrink, int symbolPosition, String nameDrink, int amountDrink, TimeDrink timeDrink) {
        this.idDrink = idDrink;
        this.symbolPosition = symbolPosition;
        this.nameDrink = nameDrink;
        this.amountDrink = amountDrink;
        this.timeDrink = timeDrink;
        isDelete=false;
        isUpdated=false;
        dateString=convertToFormatDateString();

    }

    private String convertToFormatDateString(){
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.YEAR,this.timeDrink.getYearDrink());
        calendar.set(Calendar.MONTH,this.timeDrink.getMonthDrink());
        calendar.set(Calendar.DAY_OF_MONTH,this.timeDrink.getDayDrink());
        calendar.set(Calendar.HOUR,this.timeDrink.getHourDrink());
        calendar.set(Calendar.MINUTE,this.timeDrink.getMinuteDrink());
        Date date = calendar.getTime();
        String datetime = dateformat.format(date);
        return datetime;

    }

    public int getSymbolPosition() {
        return symbolPosition;
    }

    public void setSymbolPosition(int symbolPosition) {
        this.symbolPosition = symbolPosition;
    }

    public String getNameDrink() {
        return nameDrink;
    }

    public void setNameDrink(String nameDrink) {
        this.nameDrink = nameDrink;
    }

    public int getAmountDrink() {
        return amountDrink;
    }

    public void setAmountDrink(int amountDrink) {
        this.amountDrink = amountDrink;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
        try {
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date date = dateformat.parse(dateString);
            this.setTimeDrink(new TimeDrink(date.getYear()+1900,date.getMonth(),date.getDate(),date.getHours(),date.getMinutes()));
            } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public TimeDrink getTimeDrink() {
        return timeDrink;
    }

    public void setTimeDrink(TimeDrink timeDrink) {
        this.timeDrink = timeDrink;
        this.dateString=convertToFormatDateString();
    }

    public String getIdDrink() {
        return idDrink;
    }

    public void setIdDrink(String idDrink) {
        this.idDrink = idDrink;
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    public void setUpdated(boolean updated) {
        isUpdated = updated;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }
}
