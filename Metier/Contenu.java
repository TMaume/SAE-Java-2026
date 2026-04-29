package Metier;

import java.util.*;


/**
 * Class Contenu
 */
public class Contenu {

  //
  // Fields
  //

  private int idCont;
  private int version;
  private List boitesIncluses;
  private ContenuBoite boiteIncluse;
  private Metier.ContenuFigurine figurines;
  private Metier.ContenuPiece pieces;
  private Metier.ContenuPiece nouvel_attribut;

  private Boite m_boites;

  private Vector figurinefVector = new Vector();
  
  //
  // Constructors
  //
  public Contenu () { };
  
  //
  // Methods
  //


  //
  // Accessor methods
  //

  /**
   * Set the value of idCont
   * @param newVar the new value of idCont
   */
  public void setIdCont (int newVar) {
    idCont = newVar;
  }

  /**
   * Get the value of idCont
   * @return the value of idCont
   */
  public int getIdCont () {
    return idCont;
  }

  /**
   * Set the value of version
   * @param newVar the new value of version
   */
  public void setVersion (int newVar) {
    version = newVar;
  }

  /**
   * Get the value of version
   * @return the value of version
   */
  public int getVersion () {
    return version;
  }

  /**
   * Set the value of boitesIncluses
   * @param newVar the new value of boitesIncluses
   */
  public void setBoitesIncluses (List newVar) {
    boitesIncluses = newVar;
  }

  /**
   * Get the value of boitesIncluses
   * @return the value of boitesIncluses
   */
  public List getBoitesIncluses () {
    return boitesIncluses;
  }

  /**
   * Set the value of boiteIncluse
   * @param newVar the new value of boiteIncluse
   */
  public void setBoiteIncluse (ContenuBoite newVar) {
    boiteIncluse = newVar;
  }

  /**
   * Get the value of boiteIncluse
   * @return the value of boiteIncluse
   */
  public ContenuBoite getBoiteIncluse () {
    return boiteIncluse;
  }

  /**
   * Set the value of figurines
   * @param newVar the new value of figurines
   */
  public void setFigurines (Metier.ContenuFigurine newVar) {
    figurines = newVar;
  }

  /**
   * Get the value of figurines
   * @return the value of figurines
   */
  public Metier.ContenuFigurine getFigurines () {
    return figurines;
  }

  /**
   * Set the value of pieces
   * @param newVar the new value of pieces
   */
  public void setPieces (Metier.ContenuPiece newVar) {
    pieces = newVar;
  }

  /**
   * Get the value of pieces
   * @return the value of pieces
   */
  public Metier.ContenuPiece getPieces () {
    return pieces;
  }

  /**
   * Set the value of nouvel_attribut
   * @param newVar the new value of nouvel_attribut
   */
  public void setNouvel_attribut (Metier.ContenuPiece newVar) {
    nouvel_attribut = newVar;
  }

  /**
   * Get the value of nouvel_attribut
   * @return the value of nouvel_attribut
   */
  public Metier.ContenuPiece getNouvel_attribut () {
    return nouvel_attribut;
  }

  /**
   * Set the value of m_boites
   * @param newVar the new value of m_boites
   */
  private void setBoites (Boite newVar) {
    m_boites = newVar;
  }

  /**
   * Get the value of m_boites
   * @return the value of m_boites
   */
  private Boite getBoites () {
    return m_boites;
  }

  /**
   * Add a Figurinef object to the figurinefVector List
   */
  private void addFigurinef (Figurine new_object) {
    figurinefVector.add(new_object);
  }

  /**
   * Remove a Figurinef object from figurinefVector List
   */
  private void removeFigurinef (Figurine new_object)
  {
    figurinefVector.remove(new_object);
  }

  /**
   * Get the List of Figurinef objects held by figurinefVector
   * @return List of Figurinef objects held by figurinefVector
   */
  private List getFigurinefList () {
    return (List) figurinefVector;
  }


  //
  // Other methods
  //

  /**
   * @return       boolean
   */
  public boolean estComplet()
  {
  }


  /**
   * @return       int
   */
  public int obtenirNbTotalPiece()
  {
  }


  /**
   * @return       List
   */
  public List obtenirPieceManquantes()
  {
  }


  /**
   * @return       List
   */
  public List obtenirPiece()
  {
  }


  /**
   * @return       List
   */
  public List obtenirFigurines()
  {
  }


  /**
   * @return       List
   */
  public List obtenirBoitesIncluses()
  {
  }


}
