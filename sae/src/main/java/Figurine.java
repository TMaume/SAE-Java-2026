package Metier;

import java.util.*;


/**
 * Class Figurine
 */
public class Figurine {

  //
  // Fields
  //

  private String idFig;
  private String nomFig;
  private int nbParties;
  
  //
  // Constructors
  //
  public Figurine () { };
  
  //
  // Methods
  //


  //
  // Accessor methods
  //

  /**
   * Set the value of idFig
   * @param newVar the new value of idFig
   */
  public void setIdFig (String newVar) {
    idFig = newVar;
  }

  /**
   * Get the value of idFig
   * @return the value of idFig
   */
  public String getIdFig () {
    return idFig;
  }

  /**
   * Set the value of nomFig
   * @param newVar the new value of nomFig
   */
  public void setNomFig (String newVar) {
    nomFig = newVar;
  }

  /**
   * Get the value of nomFig
   * @return the value of nomFig
   */
  public String getNomFig () {
    return nomFig;
  }

  /**
   * Set the value of nbParties
   * @param newVar the new value of nbParties
   */
  public void setNbParties (int newVar) {
    nbParties = newVar;
  }

  /**
   * Get the value of nbParties
   * @return the value of nbParties
   */
  public int getNbParties () {
    return nbParties;
  }

  //
  // Other methods
  //

  /**
   * @return       String
   */
  public String obtenirNom()
  {
  }


  /**
   * @return       int
   */
  public int obtenirNombreParties()
  {
  }


  /**
   * @return       String
   */
  public String afficher()
  {
  }


}
