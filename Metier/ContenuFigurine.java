package Metier;

import java.util.*;


/**
 * Class ContenuFigurine
 */
public class ContenuFigurine {

  //
  // Fields
  //

  private int quantite;

  public Figurine m_figurine;
  
  //
  // Constructors
  //
  public ContenuFigurine () { };
  
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
   * Set the value of m_figurine
   * @param newVar the new value of m_figurine
   */
  public void setFigurine (Figurine newVar) {
    m_figurine = newVar;
  }

  /**
   * Get the value of m_figurine
   * @return the value of m_figurine
   */
  public Figurine getFigurine () {
    return m_figurine;
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
   * @return       Metier.Figurine
   */
  public Metier.Figurine obtenirFigurine()
  {
  }


}
