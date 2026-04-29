package Metier;


/**
 * Class Theme
 */
public class Theme {

  //
  // Fields
  //

  private int idTheme;
  private String nomTheme;
  
  //
  // Constructors
  //
  public Theme () { };
  
  //
  // Methods
  //


  //
  // Accessor methods
  //

  /**
   * Set the value of idTheme
   * @param newVar the new value of idTheme
   */
  public void setIdTheme (int newVar) {
    idTheme = newVar;
  }

  /**
   * Get the value of idTheme
   * @return the value of idTheme
   */
  public int getIdTheme () {
    return idTheme;
  }

  /**
   * Set the value of nomTheme
   * @param newVar the new value of nomTheme
   */
  public void setNomTheme (String newVar) {
    nomTheme = newVar;
  }

  /**
   * Get the value of nomTheme
   * @return the value of nomTheme
   */
  public String getNomTheme () {
    return nomTheme;
  }

  //
  // Other methods
  //

  /**
   * @return       List
   */
  public List obtenirSousThemes()
  {
  }


  /**
   * @return       boolean
   */
  public boolean estRacine()
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
  public Metier.Theme obtenirParent()
  {
  }


}
