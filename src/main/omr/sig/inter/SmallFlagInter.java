//------------------------------------------------------------------------------------------------//
//                                                                                                //
//                                   S m a l l F l a g I n t e r                                  //
//                                                                                                //
//------------------------------------------------------------------------------------------------//
// <editor-fold defaultstate="collapsed" desc="hdr">
//  Copyright © Hervé Bitteur and others 2000-2014. All rights reserved.
//  This software is released under the GNU General Public License.
//  Goto http://kenai.com/projects/audiveris to report bugs or suggestions.
//------------------------------------------------------------------------------------------------//
// </editor-fold>
package omr.sig.inter;

import omr.glyph.Shape;
import omr.glyph.facets.Glyph;

/**
 * Class {@code SmallFlagInter} is a flag for grace note (with or without slash).
 *
 * @author Hervé Bitteur
 */
public class SmallFlagInter
        extends AbstractFlagInter
{
    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new SmallFlagInter object.
     *
     * @param glyph underlying glyph
     * @param shape precise shape
     * @param grade evaluation value
     */
    public SmallFlagInter (Glyph glyph,
                           Shape shape,
                           double grade)
    {
        super(glyph, shape, grade);
    }
}