package com.alroca.apr_xativa.utils;

public class ValidacionUtils {

    // DNI español: 8 números + 1 letra
    private static final String REGEX_DNI = "^[0-9]{8}[A-Z]$";

    // NIE extranjero: X/Y/Z + 7 números + 1 letra
    private static final String REGEX_NIE = "^[XYZ][0-9]{7}[A-Z]$";

    // Matrícula nueva española: 4 números + 3 letras
    private static final String REGEX_MATRICULA_NUEVA = "^[0-9]{4}[BCDFGHJKLMNPRSTUVWXYZ]{3}$";

    // Matrícula antigua española: 1-2 letras + 4 números + 2 letras
    private static final String REGEX_MATRICULA_ANTIGUA = "^[A-Z]{1,2}[0-9]{4}[A-Z]{2}$";

    public static boolean esDniValido(String dni) {
        if (dni == null) return false;
        return dni.toUpperCase().matches(REGEX_DNI) || dni.toUpperCase().matches(REGEX_NIE);
    }

    public static boolean esMatriculaValida(String matricula) {
        if (matricula == null) return false;
        return matricula.toUpperCase().matches(REGEX_MATRICULA_NUEVA) ||
                matricula.toUpperCase().matches(REGEX_MATRICULA_ANTIGUA);
    }
}