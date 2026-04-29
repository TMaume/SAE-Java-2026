package Metier;


/**
 * Class Couleur
 */
public class Couleur {

  //
  // Fields
  //

  private int idCoul;
  private String nomCoul;
  private String rgb;
  private boolean transparent;
  
  //
  // Constructors
  //
  public Couleur () { };
  
  //
  // Methods
  //


  //
  // Accessor methods
  //

  /**
   * Set the value of idCoul
   * @param newVar the new value of idCoul
   */
  public void setIdCoul (int newVar) {
    idCoul = newVar;
  }

  /**
   * Get the value of idCoul
   * @return the value of idCoul
   */
  public int getIdCoul () {
    return idCoul;
  }

  /**
   * Set the value of nomCoul
   * @param newVar the new value of nomCoul
   */
  public void setNomCoul (String newVar) {
    nomCoul = newVar;
  }

  /**
   * Get the value of nomCoul
   * @return the value of nomCoul
   */
  public String getNomCoul () {
    return nomCoul;
  }

  /**
   * Set the value of rgb
   * @param newVar the new value of rgb
   */
  public void setRgb (String newVar) {
    rgb = newVar;
  }

  /**
   * Get the value of rgb
   * @return the value of rgb
   */
  public String getRgb () {
    return rgb;
  }

  /**
   * Set the value of transparent
   * @param newVar the new value of transparent
   */
  public void setTransparent (boolean newVar) {
    transparent = newVar;
  }

  /**
   * Get the value of transparent
   * @return the value of transparent
   */
  public boolean getTransparent () {
    return transparent;
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
   * @return       boolean
   */
  public boolean estTransparente()
  {
  }


  /**
   * @return       String
   */
  public String afficher()
  {
  }


}
