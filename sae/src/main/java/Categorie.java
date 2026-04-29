package Metier;

import java.util.*;


/**
 * Class Categorie
 */
public class Categorie {

  //
  // Fields
  //

  private int idCat;
  private String nomCat;
  
  //
  // Constructors
  //
  public Categorie () { };
  
  //
  // Methods
  //


  //
  // Accessor methods
  //

  /**
   * Set the value of idCat
   * @param newVar the new value of idCat
   */
  public void setIdCat (int newVar) {
    idCat = newVar;
  }

  /**
   * Get the value of idCat
   * @return the value of idCat
   */
  public int getIdCat () {
    return idCat;
  }

  /**
   * Set the value of nomCat
   * @param newVar the new value of nomCat
   */
  public void setNomCat (String newVar) {
    nomCat = newVar;
  }

  /**
   * Get the value of nomCat
   * @return the value of nomCat
   */
  public String getNomCat () {
    return nomCat;
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
   * @return       String
   */
  public String afficher()
  {
  }


}
