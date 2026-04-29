package Metier;

import java.util.*;


/**
 * Class ContenuPiece
 */
public class ContenuPiece {

  //
  // Fields
  //

  private int quantite;
  private boolean enSupplement;

  private Vector piecesVector = new Vector();

  private Couleur m_couleur;
  
  //
  // Constructors
  //
  public ContenuPiece () { };
  
  //
  // Methods
  //


  //
  // Accessor methods
  //

  /**
   * Set the value of quantite
   * @param newVar the new value of quantite
   */
  public void setQuantite (int newVar) {
    quantite = newVar;
  }

  /**
   * Get the value of quantite
   * @return the value of quantite
   */
  public int getQuantite () {
    return quantite;
  }

  /**
   * Set the value of enSupplement
   * @param newVar the new value of enSupplement
   */
  public void setEnSupplement (boolean newVar) {
    enSupplement = newVar;
  }

  /**
   * Get the value of enSupplement
   * @return the value of enSupplement
   */
  public boolean getEnSupplement () {
    return enSupplement;
  }

  /**
   * Add a Pieces object to the piecesVector List
   */
  private void addPieces (Piece new_object) {
    piecesVector.add(new_object);
  }

  /**
   * Remove a Pieces object from piecesVector List
   */
  private void removePieces (Piece new_object)
  {
    piecesVector.remove(new_object);
  }

  /**
   * Get the List of Pieces objects held by piecesVector
   * @return List of Pieces objects held by piecesVector
   */
  private List getPiecesList () {
    return (List) piecesVector;
  }


  /**
   * Set the value of m_couleur
   * @param newVar the new value of m_couleur
   */
  private void setCouleur (Couleur newVar) {
    m_couleur = newVar;
  }

  /**
   * Get the value of m_couleur
   * @return the value of m_couleur
   */
  private Couleur getCouleur () {
    return m_couleur;
  }

  //
  // Other methods
  //

  /**
   * @return       int
   */
  public int obtenirQuantite()
  {
  }


  /**
   * @return       boolean
   */
  public boolean estEnSupplement()
  {
  }


  /**
   * @return       Metier.Piece
   */
  public Metier.Piece obtenirPiece()
  {
  }


  /**
   * @return       Metier.Couleur
   */
  public Metier.Couleur obtenirCouleur()
  {
  }


}
