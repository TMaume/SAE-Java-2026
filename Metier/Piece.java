package Metier;

import java.util.*;


/**
 * Class Piece
 */
public class Piece {

  //
  // Fields
  //

  private String numPiece;
  private String nomPiece;

  private Categorie m_categorie;
  
  //
  // Constructors
  //
  public Piece () { };
  
  //
  // Methods
  //


  //
  // Accessor methods
  //

  /**
   * Set the value of numPiece
   * @param newVar the new value of numPiece
   */
  public void setNumPiece (String newVar) {
    numPiece = newVar;
  }

  /**
   * Get the value of numPiece
   * @return the value of numPiece
   */
  public String getNumPiece () {
    return numPiece;
  }

  /**
   * Set the value of nomPiece
   * @param newVar the new value of nomPiece
   */
  public void setNomPiece (String newVar) {
    nomPiece = newVar;
  }

  /**
   * Get the value of nomPiece
   * @return the value of nomPiece
   */
  public String getNomPiece () {
    return nomPiece;
  }

  /**
   * Set the value of m_categorie
   * @param newVar the new value of m_categorie
   */
  private void setCategorie (Categorie newVar) {
    m_categorie = newVar;
  }

  /**
   * Get the value of m_categorie
   * @return the value of m_categorie
   */
  private Categorie getCategorie () {
    return m_categorie;
  }

  //
  // Other methods
  //

  /**
   * @return       Metier.Categorie
   */
  public Metier.Categorie obtenirCategorie()
  {
  }


  /**
   * @return       Metier.Couleur
   */
  public Metier.Couleur obtenirCouleur()
  {
  }


  /**
   * @return       String
   */
  public String obtenirNumero()
  {
  }


  /**
   * @return       String
   */
  public String afficher()
  {
  }


}
