package com.test.qr_generator.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Service
public class TurnstileService {

    // Expected barcode format: "DATE:TIME" (e.g., "2026-03-24:09:44")
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    
    // Barcode format with seconds (e.g., "2026-03-24T09:44:00")
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter ISO_DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    /**
     * Processes barcode data and determines if the turnstile gate should open
     * 
     * @param barcodeData The barcode content containing date and time
     * @return Map containing:
     *   - success: boolean indicating if validation passed
     *   - gateOpen: boolean indicating if gate should open
     *   - message: description of the result
     *   - scannedData: the parsed date/time from barcode
     *   - currentTime: current system time
     *   - timeDiff: difference in minutes between scanned time and current time
     */
    public Map<String, Object> processBarcode(String barcodeData) {
        Map<String, Object> response = new HashMap<>();
        
        if (barcodeData == null || barcodeData.trim().isEmpty()) {
            response.put("success", false);
            response.put("gateOpen", false);
            response.put("message", "Empty barcode data");
            return response;
        }

        try {
            // Parse the barcode data (supports multiple formats)
            LocalDateTime scannedDateTime = parseBarcodeData(barcodeData.trim());
            
            if (scannedDateTime == null) {
                response.put("success", false);
                response.put("gateOpen", false);
                response.put("message", "Invalid barcode format. Expected format: YYYY-MM-DD:HH:MM");
                return response;
            }

            LocalDateTime now = LocalDateTime.now();
            LocalDate today = now.toLocalDate();
            LocalDate scannedDate = scannedDateTime.toLocalDate();
            
            // Calculate time difference in minutes
            long minutesDiff = ChronoUnit.MINUTES.between(scannedDateTime, now);
            
            // Store parsed data for response
            response.put("scannedDate", scannedDateTime.format(DATE_FORMAT));
            response.put("scannedTime", scannedDateTime.format(TIME_FORMAT));
            response.put("currentDate", now.format(DATE_FORMAT));
            response.put("currentTime", now.format(TIME_FORMAT));
            response.put("timeDiffMinutes", minutesDiff);
            
            // Validation logic
            // 1. Check if date is today
            // 2. Check if time is within 1 hour (60 minutes) from now
            
            boolean isToday = scannedDate.equals(today);
            boolean isWithinOneHour = Math.abs(minutesDiff) <= 60;
            
            if (isToday && isWithinOneHour) {
                // Gate opens - valid ticket
                response.put("success", true);
                response.put("gateOpen", true);
                response.put("message", "✓ GATE OPEN - Access Granted!");
                response.put("status", "valid");
            } else if (!isToday) {
                // Date is not today
                response.put("success", true);
                response.put("gateOpen", false);
                response.put("message", "✗ ACCESS DENIED - Date expired. Ticket is for " + scannedDate.format(DATE_FORMAT) + " only.");
                response.put("status", "expired_date");
            } else if (minutesDiff > 60) {
                // Time is more than 1 hour in the past
                response.put("success", true);
                response.put("gateOpen", false);
                response.put("message", "✗ ACCESS DENIED - Ticket expired. Valid for 1 hour only.");
                response.put("status", "expired_time");
            } else if (minutesDiff < -60) {
                // Time is more than 1 hour in the future (not yet valid)
                response.put("success", true);
                response.put("gateOpen", false);
                response.put("message", "✗ ACCESS DENIED - Ticket not yet valid. Valid from " + scannedDateTime.format(TIME_FORMAT));
                response.put("status", "future_time");
            }
            
            return response;
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("gateOpen", false);
            response.put("message", "Error parsing barcode: " + e.getMessage());
            return response;
        }
    }

    /**
     * Parse barcode data in various formats
     * Supported formats:
     * - "YYYY-MM-DD:HH:MM" 
     * - "YYYY-MM-DD HH:MM"
     * - "YYYY-MM-DDTHH:MM" (ISO-like)
     * - "YYYY-MM-DDTHH:MM:SSZ" (ISO 8601 with Z)
     * - "YYYY-MM-DD" (date only - will use current time)
     */
    private LocalDateTime parseBarcodeData(String data) {
        // Try format: YYYY-MM-DDTHH:MM:SSZ (ISO 8601 format like 2024-11-23T18:30:00Z)
        if (data.contains("T") && data.endsWith("Z")) {
            try {
                // Remove the 'Z' and parse as local datetime (remove timezone)
                String withoutZ = data.replace("Z", "");
                return LocalDateTime.parse(withoutZ);
            } catch (Exception e) {
                // Continue to next format
            }
        }
        
        // Try format: YYYY-MM-DDTHH:MM (ISO-like without Z)
        if (data.contains("T")) {
            try {
                return LocalDateTime.parse(data);
            } catch (Exception e) {
                // Try alternative - handle missing seconds
                try {
                    return LocalDateTime.parse(data);
                } catch (Exception ex) {
                    // Continue to next format
                }
            }
        }
        
        // Try format: YYYY-MM-DD:HH:MM or YYYY-MM-DD:HH (colon separator)
        // The date part is 10 characters (YYYY-MM-DD), so find colon after position 10
        int colonIndex = data.indexOf(':');
        if (colonIndex >= 10) {
            try {
                String datePart = data.substring(0, colonIndex);
                String timePart = data.substring(colonIndex + 1);
                // Handle both HH:MM and HH formats
                if (timePart.length() == 2) {
                    timePart = timePart + ":00";
                }
                return LocalDateTime.parse(datePart + " " + timePart, DATETIME_FORMAT);
            } catch (Exception e) {
                // Continue to next format
            }
        }
        
        // Try format: YYYY-MM-DD HH:MM
        if (data.contains(" ") && !data.contains("T")) {
            try {
                return LocalDateTime.parse(data, DATETIME_FORMAT);
            } catch (Exception e) {
                // Try alternative
            }
        }
        
        // Try direct parsing
        try {
            return LocalDateTime.parse(data);
        } catch (Exception e) {
            // Continue to last attempt
        }
        
        // Try parsing just date and use current time
        try {
            LocalDate date = LocalDate.parse(data, DATE_FORMAT);
            return LocalDateTime.of(date, LocalTime.now());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Generate a sample barcode data for testing
     * @return A valid barcode string with today's date and current time
     */
    public String generateSampleBarcode() {
        LocalDateTime now = LocalDateTime.now();
        return now.format(DATE_FORMAT) + ":" + now.format(TIME_FORMAT);
    }

    /**
     * Generate a barcode with future time for testing
     * @param minutesAhead Number of minutes in the future
     * @return A barcode string with future time
     */
    public String generateFutureBarcode(int minutesAhead) {
        LocalDateTime future = LocalDateTime.now().plusMinutes(minutesAhead);
        return future.format(DATE_FORMAT) + ":" + future.format(TIME_FORMAT);
    }

    /**
     * Generate a barcode with past time for testing
     * @param minutesAgo Number of minutes in the past
     * @return A barcode string with past time
     */
    public String generatePastBarcode(int minutesAgo) {
        LocalDateTime past = LocalDateTime.now().minusMinutes(minutesAgo);
        return past.format(DATE_FORMAT) + ":" + past.format(TIME_FORMAT);
    }
}