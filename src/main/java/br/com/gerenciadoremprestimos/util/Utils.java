package br.com.gerenciadoremprestimos.util;

public class Utils {

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
