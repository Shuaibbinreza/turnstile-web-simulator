package com.test.qr_generator.service;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QrReaderService {

    private static final Map<DecodeHintType, Object> QR_HINTS = new HashMap<>();
    private static final Map<DecodeHintType, Object> BARCODE_HINTS = new HashMap<>();
    private static final Map<DecodeHintType, Object> ALL_HINTS = new HashMap<>();

    static {
        // QR Code only hints
        List<BarcodeFormat> qrFormats = new ArrayList<>();
        qrFormats.add(BarcodeFormat.QR_CODE);
        QR_HINTS.put(DecodeHintType.POSSIBLE_FORMATS, qrFormats);
        QR_HINTS.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        
        // Barcode (1D) formats hints
        List<BarcodeFormat> barcodeFormats = new ArrayList<>();
        barcodeFormats.add(BarcodeFormat.CODE_128);
        barcodeFormats.add(BarcodeFormat.CODE_39);
        barcodeFormats.add(BarcodeFormat.CODE_93);
        barcodeFormats.add(BarcodeFormat.CODABAR);
        barcodeFormats.add(BarcodeFormat.EAN_13);
        barcodeFormats.add(BarcodeFormat.EAN_8);
        barcodeFormats.add(BarcodeFormat.ITF);
        barcodeFormats.add(BarcodeFormat.UPC_A);
        barcodeFormats.add(BarcodeFormat.UPC_E);
        BARCODE_HINTS.put(DecodeHintType.POSSIBLE_FORMATS, barcodeFormats);
        BARCODE_HINTS.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        
        // All formats hints (QR + Barcode)
        List<BarcodeFormat> allFormats = new ArrayList<>();
        allFormats.add(BarcodeFormat.QR_CODE);
        allFormats.add(BarcodeFormat.CODE_128);
        allFormats.add(BarcodeFormat.CODE_39);
        allFormats.add(BarcodeFormat.CODE_93);
        allFormats.add(BarcodeFormat.CODABAR);
        allFormats.add(BarcodeFormat.EAN_13);
        allFormats.add(BarcodeFormat.EAN_8);
        allFormats.add(BarcodeFormat.ITF);
        allFormats.add(BarcodeFormat.UPC_A);
        allFormats.add(BarcodeFormat.UPC_E);
        ALL_HINTS.put(DecodeHintType.POSSIBLE_FORMATS, allFormats);
        ALL_HINTS.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
    }

    /**
     * Reads QR code from uploaded image file
     * @param file Multipart image file containing QR code
     * @return The decoded text content from QR code
     * @throws IOException if image cannot be read
     * @throws NotFoundException if no QR code is found in the image
     */
    public String readQrCode(MultipartFile file) throws IOException, NotFoundException {
        BufferedImage image = ImageIO.read(file.getInputStream());
        
        if (image == null) {
            throw new IOException("Could not read image file");
        }

        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        
        Result result = new MultiFormatReader().decode(bitmap, QR_HINTS);
        return result.getText();
    }

    /**
     * Reads QR code from base64 encoded image string
     * @param base64Image Base64 encoded image string
     * @return The decoded text content from QR code
     * @throws NotFoundException if no QR code is found
     */
    public String readQrCodeFromBase64(String base64Image) throws NotFoundException, IOException {
        // Remove data URL prefix if present
        String imageData = base64Image;
        if (base64Image.contains(",")) {
            imageData = base64Image.split(",")[1];
        }
        
        byte[] imageBytes = java.util.Base64.getDecoder().decode(imageData);
        BufferedImage image = ImageIO.read(new java.io.ByteArrayInputStream(imageBytes));
        
        if (image == null) {
            throw new IOException("Could not decode image from base64 string");
        }

        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        
        Result result = new MultiFormatReader().decode(bitmap, QR_HINTS);
        return result.getText();
    }

    /**
     * Reads barcode (1D) from uploaded image file
     * @param file Multipart image file containing barcode
     * @return The decoded text content from barcode
     * @throws IOException if image cannot be read
     * @throws NotFoundException if no barcode is found in the image
     */
    public String readBarcode(MultipartFile file) throws IOException, NotFoundException {
        BufferedImage image = ImageIO.read(file.getInputStream());
        
        if (image == null) {
            throw new IOException("Could not read image file");
        }

        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        
        Result result = new MultiFormatReader().decode(bitmap, BARCODE_HINTS);
        return result.getText();
    }

    /**
     * Reads barcode from base64 encoded image string
     * @param base64Image Base64 encoded image string
     * @return The decoded text content from barcode
     * @throws NotFoundException if no barcode is found
     */
    public String readBarcodeFromBase64(String base64Image) throws NotFoundException, IOException {
        String imageData = base64Image;
        if (base64Image.contains(",")) {
            imageData = base64Image.split(",")[1];
        }
        
        byte[] imageBytes = java.util.Base64.getDecoder().decode(imageData);
        BufferedImage image = ImageIO.read(new java.io.ByteArrayInputStream(imageBytes));
        
        if (image == null) {
            throw new IOException("Could not decode image from base64 string");
        }

        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        
        Result result = new MultiFormatReader().decode(bitmap, BARCODE_HINTS);
        return result.getText();
    }

    /**
     * Reads any code (QR or barcode) from uploaded image file
     * @param file Multipart image file containing code
     * @return Map containing the decoded text and format type
     * @throws IOException if image cannot be read
     * @throws NotFoundException if no code is found in the image
     */
    public Map<String, String> readCode(MultipartFile file) throws IOException, NotFoundException {
        BufferedImage image = ImageIO.read(file.getInputStream());
        
        if (image == null) {
            throw new IOException("Could not read image file");
        }

        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        
        Result result = new MultiFormatReader().decode(bitmap, ALL_HINTS);
        
        Map<String, String> response = new HashMap<>();
        response.put("content", result.getText());
        response.put("format", result.getBarcodeFormat().toString());
        return response;
    }

    /**
     * Reads any code from base64 encoded image string
     * @param base64Image Base64 encoded image string
     * @return Map containing the decoded text and format type
     * @throws NotFoundException if no code is found
     */
    public Map<String, String> readCodeFromBase64(String base64Image) throws NotFoundException, IOException {
        String imageData = base64Image;
        if (base64Image.contains(",")) {
            imageData = base64Image.split(",")[1];
        }
        
        byte[] imageBytes = java.util.Base64.getDecoder().decode(imageData);
        BufferedImage image = ImageIO.read(new java.io.ByteArrayInputStream(imageBytes));
        
        if (image == null) {
            throw new IOException("Could not decode image from base64 string");
        }

        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        
        Result result = new MultiFormatReader().decode(bitmap, ALL_HINTS);
        
        Map<String, String> response = new HashMap<>();
        response.put("content", result.getText());
        response.put("format", result.getBarcodeFormat().toString());
        return response;
    }
}
