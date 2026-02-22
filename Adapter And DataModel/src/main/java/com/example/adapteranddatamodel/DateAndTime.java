package com.example.adapteranddatamodel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class DateAndTime {
    // Method to get the current date in dd-MM-yyyy format
    public static String getDate() {
        // Get the current date
        Date currentDate = new Date();

        // Define the date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        // Format the date
        String formattedDate = dateFormat.format(currentDate);

        // Return the formatted date
        return formattedDate;
    }

    // Method to get the current time in hh:mm a format
    public static String getTime() {
        // Get the current time
        Date currentTime = new Date();

        // Define the time format
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        // Format the time
        String formattedTime = timeFormat.format(currentTime);

        // Return the formatted time
        return formattedTime;
    }

    public static String convertDateFormat(String inputDate) {
        // Define input and output date formats
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH);

        try {
            // Parse the input date
            Date date = inputFormat.parse(inputDate);

            // Format the date into the desired output format
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // Return null if parsing fails
        }
    }
    public static String convertDateFormatOrder(String inputDate) {
        // Define input and output date formatters
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH);

        try {
            // Parse the input date
            Date date = inputFormat.parse(inputDate);

            // Format the date into the desired output format
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // Return null if parsing fails
        }
    }
}
