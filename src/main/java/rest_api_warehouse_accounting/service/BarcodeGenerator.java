package rest_api_warehouse_accounting.service;
import org.apache.commons.lang3.RandomStringUtils;

public class BarcodeGenerator {

    /**
     * Генерирует случайный штрихкод длиной 13 символов (EAN-13).
     *
     * @return Сгенерированный штрихкод.
     */
    public static String generateBarcode() {
        return RandomStringUtils.randomNumeric(13); // Генерация 13 цифр
    }

}
