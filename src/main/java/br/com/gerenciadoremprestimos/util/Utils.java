package br.com.gerenciadoremprestimos.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


import java.time.LocalDate;

public class Utils {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static LocalDateTime convertStringToLocalDateTime(String dateString) {
        LocalDate date = LocalDate.parse(dateString, DATE_FORMATTER);
        return date.atStartOfDay();
    }

    public static boolean contemApenasNumeros(String texto) {
        if (texto == null || texto.isEmpty()) {
            return false;
        }

        // Verifica se cada caractere é um dígito
        for (char c : texto.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }

        return true;
    }
}
