package Metier;

import java.util.*;


/**
 * Class Boite
 */
abstract public class Boite {

  //
  // Fields
  //

  private String numBoite;
  private String nomBoite;
  private int annee;
  private int nbPieces;

  private Vector contenusVector = new Vector();

  private EtatBoite m_ boite;

  private Theme m_ theme;

  private Vector  contenubVector = new Vector();
  
  //
  // Constructors
  //
  public Boite () { };
  
  //
  // Methods
  //


  //
  // Accessor methods
  //

  /**
   * Set the value of numBoite
   * @param newVar the new value of numBoite
   */
  public void setNumBoite (String newVar) {
    numBoite = newVar;
  }

  /**
   * Get the value of numBoite
   * @return the value of numBoite
   */
  public String getNumBoite () {
    return numBoite;
  }

  /**
   * Set the value of nomBoite
   * @param newVar the new value of nomBoite
   */
  public void setNomBoite (String newVar) {
    nomBoite = newVar;
  }

  /**
   * Get the value of nomBoite
   * @return the value of nomBoite
   */
  public String getNomBoite () {
    return nomBoite;
  }

  /**
   * Set the value of annee
   * @param newVar the new value of annee
   */
  public void setAnnee (int newVar) {
    annee = newVar;
  }

  /**
   * Get the value of annee
   * @return the value of annee
   */
  public int getAnnee () {
    return annee;
  }

  /**
   * Set the value of nbPieces
   * @param newVar the new value of nbPieces
   */
  public void setNbPieces (int newVar) {
    nbPieces = newVar;
  }

  /**
   * Get the value of nbPieces
   * @return the value of nbPieces
   */
  public int getNbPieces () {
    return nbPieces;
  }

  /**
   * Add a Contenus object to the contenusVector List
   */
  private void addContenus (Contenu new_object) {
    contenusVector.add(new_object);
  }

  /**
   * Remove a Contenus object from contenusVector List
   */
  private void removeContenus (Contenu new_object)
  {
    contenusVector.remove(new_object);
  }

  /**
   * Get the List of Contenus objects held by contenusVector
   * @return List of Contenus objects held by contenusVector
   */
  private List getContenusList () {
    return (List) contenusVector;
  }


  /**
   * Set the value of m_ boite
   * @param newVar the new value of m_ boite
   */
  private void set boite (EtatBoite newVar) {
    m_ boite = newVar;
  }

  /**
   * Get the value of m_ boite
   * @return the value of m_ boite
   */
  private EtatBoite get boite () {
    return m_ boite;
  }

  /**
   * Set the value of m_ theme
   * @param newVar the new value of m_ theme
   */
  private void set theme (Theme newVar) {
    m_ theme = newVar;
  }

  /**
   * Get the value of m_ theme
   * @return the value of m_ theme
   */
  private Theme get theme () {
    return m_ theme;
  }

  /**
   * Add a  contenub object to the  contenubVector List
   */
  private void add contenub (ContenuBoite new_object) {
     contenubVector.add(new_object);
  }

  /**
   * Remove a  contenub object from  contenubVector List
   */
  private void remove contenub (ContenuBoite new_object)
  {
     contenubVector.remove(new_object);
  }

  /**
   * Get the List of  contenub objects held by  contenubVector
   * @return List of  contenub objects held by  contenubVector
   */
  private List get contenubList () {
    return (List)  contenubVector;
  }


  //
  // Other methods
  //

  /**
   * @return       Metier.Contenu
   */
  public Metier.Contenu obtenirContenu()
  {
  }


  /**
   * @return       String
   */
  public String obtenirStatistiques()
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
  public String obtenirNom()
  {
  }


  /**
   * @return       Metier.Theme
   */
  public Metier.Theme obtenirTheme()
  {
  }


}
