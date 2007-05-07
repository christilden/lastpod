/*
 * LastPod is an application used to publish one's iPod play counts to Last.fm.
 * Copyright (C) 2007  Chris Tilden
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.lastpod.util;

import javax.swing.SwingUtilities;

/**
 * This is the 3rd version of SwingWorker (also known as
 * SwingWorker 3), an abstract class that you subclass to
 * perform GUI-related work in a dedicated thread.  For
 * instructions on using this class, see:
 *
 * http://java.sun.com/docs/books/tutorial/uiswing/misc/threads.html
 *
 * Note that the API changed slightly in the 3rd version:
 * You must now invoke start() on the SwingWorker after
 * creating it.
 */
public abstract class SwingWorker {
    /**
     * The value to return upon completion or interupt.
     */
    private Object value;

    /**
     * The thread to use to perform the work.
     */
    private ThreadVar threadVar;

    /**
     * Start a thread that will call the <code>construct</code> method
     * and then exit.
     */
    public SwingWorker() {
        final Runnable doFinished =
            new Runnable() {
                public void run() {
                    finished();
                }
            };

        Runnable doConstruct =
            new Runnable() {
                public void run() {
                    try {
                        setValue(construct());
                    } finally {
                        threadVar.clear();
                    }

                    SwingUtilities.invokeLater(doFinished);
                }
            };

        Thread t = new Thread(doConstruct);
        threadVar = new ThreadVar(t);
    }

    /**
     * Gets the value produced by the worker thread, or null if it
     * has not been constructed yet.
     * @return  The value produced by the worker thread.
     */
    protected synchronized Object getValue() {
        return value;
    }

    /**
     * Sets the value produced by worker thread.
     * @param x  The value to set.
     */
    private synchronized void setValue(Object x) {
        value = x;
    }

    /**
     * Computes the value to be returned by the <code>get</code> method.
     * @return  The value to return from the worker thread.
     */
    public abstract Object construct();

    /**
     * Called on the event dispatching thread (not on the worker thread)
     * after the <code>construct</code> method has returned.
     */
    public void finished() {
    }

    /**
     * A new method that interrupts the worker thread.  Call this method
     * to force the worker to stop what it's doing.
     */
    public void interrupt() {
        Thread t = threadVar.get();

        if (t != null) {
            t.interrupt();
        }

        threadVar.clear();
    }

    /**
     * Return the value created by the <code>construct</code> method.
     * Returns null if either the constructing thread or the current
     * thread was interrupted before a value was produced.
     * @return the value created by the <code>construct</code> method
     */
    public Object get() {
        while (true) {
            Thread t = threadVar.get();

            if (t == null) {
                return getValue();
            }

            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // propagate

                return null;
            }
        }
    }

    /**
     * Start the worker thread.
     */
    public void start() {
        Thread t = threadVar.get();

        if (t != null) {
            t.start();
        }
    }

    /**
     * Class to maintain reference to current worker thread
     * under separate synchronization control.
     */
    private static class ThreadVar {
        /**
         * The thread to use.
         */
        private Thread thread;

        /**
         * Constructs this object with a given Thread.
         * @param t  The thread to construct with.
         */
        ThreadVar(Thread t) {
            thread = t;
        }

        /**
         * Gets the Thread.
         * @return  The Thread.
         */
        synchronized Thread get() {
            return thread;
        }

        /**
         * Clears the Thread out.
         */
        synchronized void clear() {
            thread = null;
        }
    }
}
