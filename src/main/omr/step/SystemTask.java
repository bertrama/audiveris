//----------------------------------------------------------------------------//
//                                                                            //
//                            S y s t e m T a s k                             //
//                                                                            //
//  Copyright (C) Herve Bitteur 2000-2007. All rights reserved.               //
//  This software is released under the GNU General Public License.           //
//  Contact author at herve.bitteur@laposte.net to report bugs & suggestions. //
//----------------------------------------------------------------------------//
//
package omr.step;

import omr.sheet.Sheet;
import omr.sheet.SystemInfo;

import omr.step.StepException;

import omr.util.Logger;
import omr.util.OmrExecutors;
import omr.util.SignallingRunnable;

import java.util.*;
import java.util.concurrent.*;

/**
 * Class <code>SystemTask</code> defines the ask for a step at system level
 *
 * @author Herv&eacute Bitteur
 * @version $Id$
 */
public abstract class SystemTask
    extends SheetTask
{
    //~ Static fields/initializers ---------------------------------------------

    /** Usual logger utility */
    private static final Logger logger = Logger.getLogger(SystemTask.class);

    //~ Instance fields --------------------------------------------------------

    /** Flag which systems have been done */
    private final SortedMap<SystemInfo, Boolean> systemDone;

    //~ Constructors -----------------------------------------------------------

    //------------//
    // SystemTask //
    //------------//
    SystemTask (Sheet sheet,
                Step  step)
    {
        super(sheet, step);
        systemDone = new TreeMap<SystemInfo, Boolean>();
    }

    //~ Methods ----------------------------------------------------------------

    //----------//
    // doSystem //
    //----------//
    /**
     * Actually perform the step on the given system
     * @param system the system on which the step must be performed
     * @throws StepException raised if processing failed
     */
    public abstract void doSystem (SystemInfo system)
        throws StepException;

    //---------//
    // doFinal //
    //---------//
    /**
     * Final processing for this step, once all systems have been processed
     * @throws StepException raised if processing failed
     */
    public void doFinal ()
        throws StepException
    {
        // Empty by default
    }

    //------//
    // doit //
    //------//
    /**
     * Actually perform the step.
     * This method is run when this step is explicitly selected
     * @throws StepException raised if processing failed
     */
    public void doit ()
        throws StepException
    {
        if (logger.isFineEnabled()) {
            logger.fine(step + " SystemTask doit ...");
        }

        // Processing per system
        //        if (OmrExecutors.useParallelism() &&
        //            (OmrExecutors.getNumberOfCpus() > 1)) {
        //            doitParallel();
        //        } else {
        doitSerial();
        //        }

        // Final actions
        doFinal();

        if (logger.isFineEnabled()) {
            logger.fine(step + " SystemTask doit end");
        }
    }

    //------//
    // done //
    //------//
    /**
     * Flag this system as done
     * @param system the provided system
     */
    public synchronized void done (SystemInfo system)
    {
        if (logger.isFineEnabled()) {
            logger.fine(
                step + " done system #" + system.getScoreSystem().getId());
        }

        done();
        systemDone.put(system, Boolean.valueOf(true));
    }

    //-----------//
    // getResult //
    //-----------//
    /**
     * Make sure this step has been run for the given system
     * @param system the system for which processing may be required
     * @throws StepException raised if processing failed
     */
    public void getResult (SystemInfo system)
        throws StepException
    {
        if (!isDone(system)) {
            doSystem(system);

            ///logger.info("");
        }
    }

    //--------//
    // isDone //
    //--------//
    /**
     * Check whether processing for this system has been done/started
     * @param system the provided system
     * @return true if done/started, false otherwise
     */
    public synchronized boolean isDone (SystemInfo system)
    {
        Boolean result = systemDone.get(system);

        return (result != null) && result;
    }

    private void doitParallel ()
    {
        Executor       executor = OmrExecutors.getHighExecutor();
        CountDownLatch doneSignal = new CountDownLatch(
            sheet.getSystems().size());

        for (SystemInfo info : sheet.getSystems()) {
            final SystemInfo   system = info;
            SignallingRunnable work = new SignallingRunnable(
                doneSignal,
                new Runnable() {
                        public void run ()
                        {
                            try {
                                doSystem(system);

                                ///logger.info("");
                            } catch (StepException ex) {
                                logger.warning("Step aborted on system", ex);
                            }
                        }
                    });
            executor.execute(work);
        }

        // Wait for end of work
        try {
            doneSignal.await();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private void doitSerial ()
        throws StepException
    {
        for (SystemInfo system : sheet.getSystems()) {
            doSystem(system);

            ///logger.info("");
        }
    }
}