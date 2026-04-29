package Metier;

import java.util.*;


/**
 * Class EtatBoite
 */
public class EtatBoite {

  //
  // Fields
  //

  private boolean complete;
  private List piecesManquantes;

  private Vector piecesVector = new Vector();
  
  //
  // Constructors
  //
  public EtatBoite () { };
  
  //
  // Methods
  //


  //
  // Accessor methods
  //

  /**
   * Set the value of complete
   * @param newVar the new value of complete
   */
  public void setComplete (boolean newVar) {
    complete = newVar;
  }

  /**
   * Get the value of complete
   * @return the value of complete
   */
  public boolean getComplete () {
    return complete;
  }

  /**
   * Set the value of piecesManquantes
   * @param newVar the new value of piecesManquantes
   */
  public void setPiecesManquantes (List newVar) {
    piecesManquantes = newVar;
  }

  /**
   * Get the value of piecesManquantes
   * @return the value of piecesManquantes
   */
  public List getPiecesManquantes () {
    return piecesManquantes;
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


  //
  // Other methods
  //

  /**
   * @return       boolean
   */
  public boolean estComplete()
  {
  }


  /**
   * @return       List
   */
  public List obtenirPiecesManquantes()
  {
  }


  /**
   * @return       Metier.Boite
   */
  public Metier.Boite obtenirBoite()
  {
  }


}
