package com.test.qr_generator.controller;

import com.google.zxing.NotFoundException;
import com.test.qr_generator.service.QrReaderService;
import com.test.qr_generator.service.TurnstileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/qr")
@CrossOrigin(origins = "*")
public class QrReaderController {

    private final QrReaderService qrReaderService;
    private final TurnstileService turnstileService;

    public QrReaderController(QrReaderService qrReaderService, TurnstileService turnstileService) {
        this.qrReaderService = qrReaderService;
        this.turnstileService = turnstileService;
    }

    /**
     * Read QR code from uploaded image file
     */
    @PostMapping("/read")
    public ResponseEntity<Map<String, Object>> readQrCode(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        
        if (file.isEmpty()) {
            response.put("success", false);
            response.put("message", "Please select a file to upload");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            String content = qrReaderService.readQrCode(file);
            response.put("success", true);
            response.put("content", content);
            response.put("format", "QR_CODE");
            response.put("message", "QR code read successfully");
            return ResponseEntity.ok(response);
        } catch (NotFoundException e) {
            response.put("success", false);
            response.put("message", "No QR code found in the image");
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Error reading image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Read QR code from base64 encoded image (from webcam)
     */
    @PostMapping("/read-base64")
    public ResponseEntity<Map<String, Object>> readQrCodeFromBase64(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        String base64Image = request.get("image");
        if (base64Image == null || base64Image.isEmpty()) {
            response.put("success", false);
            response.put("message", "No image data provided");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            String content = qrReaderService.readQrCodeFromBase64(base64Image);
            response.put("success", true);
            response.put("content", content);
            response.put("format", "QR_CODE");
            response.put("message", "QR code read successfully");
            return ResponseEntity.ok(response);
        } catch (NotFoundException e) {
            response.put("success", false);
            response.put("message", "No QR code found in the image");
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Error decoding image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Read barcode (1D) from uploaded image file
     */
    @PostMapping("/read-barcode")
    public ResponseEntity<Map<String, Object>> readBarcode(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        
        if (file.isEmpty()) {
            response.put("success", false);
            response.put("message", "Please select a file to upload");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            String content = qrReaderService.readBarcode(file);
            response.put("success", true);
            response.put("content", content);
            response.put("message", "Barcode read successfully");
            return ResponseEntity.ok(response);
        } catch (NotFoundException e) {
            response.put("success", false);
            response.put("message", "No barcode found in the image");
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Error reading image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Read barcode from base64 encoded image
     */
    @PostMapping("/read-barcode-base64")
    public ResponseEntity<Map<String, Object>> readBarcodeFromBase64(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        String base64Image = request.get("image");
        if (base64Image == null || base64Image.isEmpty()) {
            response.put("success", false);
            response.put("message", "No image data provided");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            String content = qrReaderService.readBarcodeFromBase64(base64Image);
            response.put("success", true);
            response.put("content", content);
            response.put("message", "Barcode read successfully");
            return ResponseEntity.ok(response);
        } catch (NotFoundException e) {
            response.put("success", false);
            response.put("message", "No barcode found in the image");
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Error decoding image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Read any code (QR or barcode) from uploaded image file
     */
    @PostMapping("/read-any")
    public ResponseEntity<Map<String, Object>> readAnyCode(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        
        if (file.isEmpty()) {
            response.put("success", false);
            response.put("message", "Please select a file to upload");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Map<String, String> result = qrReaderService.readCode(file);
            response.put("success", true);
            response.put("content", result.get("content"));
            response.put("format", result.get("format"));
            response.put("message", "Code read successfully");
            return ResponseEntity.ok(response);
        } catch (NotFoundException e) {
            response.put("success", false);
            response.put("message", "No code (QR or barcode) found in the image");
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Error reading image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Read any code from base64 encoded image
     */
    @PostMapping("/read-any-base64")
    public ResponseEntity<Map<String, Object>> readAnyCodeFromBase64(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        String base64Image = request.get("image");
        if (base64Image == null || base64Image.isEmpty()) {
            response.put("success", false);
            response.put("message", "No image data provided");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Map<String, String> result = qrReaderService.readCodeFromBase64(base64Image);
            response.put("success", true);
            response.put("content", result.get("content"));
            response.put("format", result.get("format"));
            response.put("message", "Code read successfully");
            return ResponseEntity.ok(response);
        } catch (NotFoundException e) {
            response.put("success", false);
            response.put("message", "No code (QR or barcode) found in the image");
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Error decoding image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== Turnstile Gate Simulation Endpoints ====================

    /**
     * Process barcode for turnstile gate access
     * Validates if date is today and time is within 1 hour from now
     */
    @PostMapping("/turnstile/validate")
    public ResponseEntity<Map<String, Object>> validateTurnstile(@RequestBody Map<String, String> request) {
        String barcodeData = request.get("barcode");
        
        if (barcodeData == null || barcodeData.trim().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("gateOpen", false);
            response.put("message", "No barcode data provided");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Map<String, Object> result = turnstileService.processBarcode(barcodeData);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("gateOpen", false);
            response.put("message", "Error processing barcode: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Process barcode from scanned image for turnstile gate access
     * First reads the barcode, then validates it
     */
    @PostMapping("/turnstile/scan")
    public ResponseEntity<Map<String, Object>> scanTurnstile(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        
        if (file.isEmpty()) {
            response.put("success", false);
            response.put("gateOpen", false);
            response.put("message", "Please select a file to upload");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // First read the barcode
            Map<String, String> codeResult = qrReaderService.readCode(file);
            String barcodeData = codeResult.get("content");
            
            // Then validate for turnstile
            Map<String, Object> validationResult = turnstileService.processBarcode(barcodeData);
            validationResult.put("scannedFormat", codeResult.get("format"));
            return ResponseEntity.ok(validationResult);
            
        } catch (NotFoundException e) {
            response.put("success", false);
            response.put("gateOpen", false);
            response.put("message", "No barcode found in the image");
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
        } catch (IOException e) {
            response.put("success", false);
            response.put("gateOpen", false);
            response.put("message", "Error reading image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("gateOpen", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Generate sample barcode data for testing
     */
    @GetMapping("/turnstile/sample")
    public ResponseEntity<Map<String, Object>> getSampleBarcode(@RequestParam(required = false, defaultValue = "0") String type) {
        Map<String, Object> response = new HashMap<>();
        
        String sampleBarcode;
        String description;
        
        switch (type) {
            case "future":
                sampleBarcode = turnstileService.generateFutureBarcode(30);
                description = "Valid in 30 minutes";
                break;
            case "past":
                sampleBarcode = turnstileService.generatePastBarcode(30);
                description = "Expired 30 minutes ago";
                break;
            case "expired_date":
                sampleBarcode = "2025-01-01:09:00";
                description = "Expired date (2025)";
                break;
            case "future_date":
                sampleBarcode = "2027-12-31:09:00";
                description = "Future date (2027)";
                break;
            default:
                sampleBarcode = turnstileService.generateSampleBarcode();
                description = "Current time (valid)";
        }
        
        response.put("success", true);
        response.put("barcode", sampleBarcode);
        response.put("description", description);
        response.put("format", "YYYY-MM-DD:HH:MM");
        
        return ResponseEntity.ok(response);
    }
}
