package org.victornieto.gestionbiblioteca.utility;

import java.util.Map;

public class TypeSancion {
    /**
     * Valores de los tipos de sanción. Cada Map contiene "tipo de sanción": "días de retardo".
     * El nombre del tipo de sanción corresponde a su nombre en la BD.
     */
    public static Map<String, Integer> RETARDO_1 = Map.of("min", 1, "max", 3, "id", 1);
    public static Map<String, Integer> RETARDO_2 = Map.of("min", 4, "max", 7, "id", 2);
    public static Map<String, Integer> RETARDO_3 = Map.of("min", 8, "max", 14, "id", 3);
    public static Map<String, Integer> RETARDO_4 = Map.of("min", 15, "max", 30, "id", 4);
    public static Map<String, Integer> RETARDO_5 = Map.of("min", 31, "id", 5);
}
