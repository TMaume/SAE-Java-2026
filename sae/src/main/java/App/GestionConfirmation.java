package App;

/**
 * Interface pour gérer les confirmations utilisateur.
 */
public interface GestionConfirmation {
    /**
     * Demande une confirmation à l'utilisateur.
     *
     * @param message le message de confirmation
     * @return true si l'utilisateur confirme, false sinon
     */
    boolean confirmer(String message);
}
