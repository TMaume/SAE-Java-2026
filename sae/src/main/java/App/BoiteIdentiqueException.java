package App;

/**
 * Exception levée lorsqu'une boîte identique existe déjà dans la collection.
 */
public class BoiteIdentiqueException extends Exception {
    /**
     * Crée une exception avec le message spécifié.
     *
     * @param message le message de l'exception
     */
    public BoiteIdentiqueException(String message) {
        super(message);
    }
}