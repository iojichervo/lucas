package service;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

import util.UiUtils;
import exception.ParseException;

public class Parser {

    private static final String DELIMITER = "=";
    private static final int QUERIES_AMOUNT = 4;

    public static final String QUERY = "query";
    public static final String MAX = "tope";
    public static final String N = "n";
    public static final String PATH = "path";

    private HashMap<String, Object> p = new HashMap<String, Object>();

    public static void main(String[] args) {
        try {
            Parser p = new Parser(args);
            p.dump();
        } catch (ParseException e) {
            UiUtils.showError(e.getMessage());
        }
    }

    /* Formas de ejecución soportadas: */
    // query=1 n=[numero entero] path=[ubicaciondel archivo]
    // query=2 tope=[numero entero] path=[ubicacion del archivo]
    // query=3 path=[ubicacion del archivo]
    // query=4 path=[ubicacion del archivo]
    public Parser(String[] args) throws ParseException {
        for (int i = 0; i < args.length; i++) {
            String parameter = args[i];
            Scanner scanner = new Scanner(parameter);
            scanner.useDelimiter(DELIMITER);
            String vble = scanner.next();
            if (!scanner.hasNext()) {
                scanner.close();
                String msg = String.format("Parameter %s is incorrect", vble);
                throw new ParseException(msg);
            }
            String value = scanner.next();
            vble = vble.trim();

            if (vble.equalsIgnoreCase(QUERY)) {
                handleQuery(value);
            } else if (vble.equalsIgnoreCase(MAX)) {
                handleMax(value);
            } else if (vble.equalsIgnoreCase(N)) {
                handleN(value);
            } else if (vble.equalsIgnoreCase(PATH)) {
                handlePath(value);
            }
            scanner.close();
        }

        checkQueryValid();
    }

    private void handleQuery(String query) throws ParseException {
        try {
            int value = Integer.parseInt(query);
            if (value < 1 || value > QUERIES_AMOUNT) {
                String msg = String.format(
                        "The query number must be between 1 and %d",
                        QUERIES_AMOUNT);
                throw new ParseException(msg);
            }
            p.put(QUERY, value);
        } catch (NumberFormatException e) {
            String msg = String.format("%s is not a valid query number", query);
            throw new ParseException(msg);
        }
    }

    private void handleMax(String max) throws ParseException {
        try {
            int value = Integer.parseInt(max);
            p.put(MAX, value);
        } catch (NumberFormatException e) {
            String msg = String.format("%s is not a valid year", max);
            throw new ParseException(msg);
        }
    }

    private void handleN(String n) throws ParseException {
        try {
            int value = Integer.parseInt(n);
            if (value < 1)
                throw new ParseException("The number must be greater than 0");
            p.put(N, value);
        } catch (NumberFormatException e) {
            String msg = String.format("%s is not a valid number", n);
            throw new ParseException(msg);
        }
    }

    private void handlePath(String path) throws ParseException {
        File f = new File(path);
        if (f.isFile()) {
            p.put(PATH, f);
        } else {
            String msg = String.format("The file %s doesn't exist", path);
            throw new ParseException(msg);
        }
    }

    private void checkQueryValid() throws ParseException {
        if (!p.containsKey(PATH) && !p.containsKey(QUERY)) {
            String msg = "A query number and file path must be specified";
            throw new ParseException(msg);
        }

        int query = (int) p.get(QUERY);

        if (query == 1 && !p.containsKey(N)) {
            throw new ParseException("The number of actors must be specified");
        }

        if (query == 2 && !p.containsKey(MAX)) {
            throw new ParseException("The year must be specified");
        }
    }

    public Object get(String key) {
        return p.get(key);
    }

    public void dump() {
        UiUtils.showMessage("Using: ");
        for (String key : p.keySet()) {
            UiUtils.showMessage(String.format("%s=%s", key, p.get(key)));
        }
    }
}
