/*---------------------------------------------------------------------------*\
  $Id$
  ---------------------------------------------------------------------------
  This software is released under a BSD-style license:

  Copyright (c) 2004-2006 Brian M. Clapper. All rights reserved.

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions are
  met:

  1.  Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.

  2.  The end-user documentation included with the redistribution, if any,
      must include the following acknowlegement:

        "This product includes software developed by Brian M. Clapper
        (bmc@clapper.org, http://www.clapper.org/bmc/). That software is
        copyright (c) 2004-2006 Brian M. Clapper."

      Alternately, this acknowlegement may appear in the software itself,
      if wherever such third-party acknowlegements normally appear.

  3.  Neither the names "clapper.org", "clapper.org Java Utility Library",
      nor any of the names of the project contributors may be used to
      endorse or promote products derived from this software without prior
      written permission. For written permission, please contact
      bmc@clapper.org.

  4.  Products derived from this software may not be called "clapper.org
      Java Utility Library", nor may "clapper.org" appear in their names
      without prior written permission of Brian M.a Clapper.

  THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
  NO EVENT SHALL BRIAN M. CLAPPER BE LIABLE FOR ANY DIRECT, INDIRECT,
  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
\*---------------------------------------------------------------------------*/

package org.clapper.util.misc;

/**
 * <p>Contains the software version for the <i>org.clapper.util</i>
 * library. Also contains a main program which, invoked, displays the name
 * of the API, the version, and detailed build information on standard
 * output.</p>
 *
 * @version <tt>$Revision$</tt>
 *
 * @author Copyright &copy; 2004-2006 Brian M. Clapper
 */
public final class Version
{
    /*----------------------------------------------------------------------*\
                             Public Constants
    \*----------------------------------------------------------------------*/

    /**
     * The name of the resource bundle containing the build info.
     */
    public static final String BUILD_INFO_BUNDLE_NAME
        = "org.clapper.util.misc.BuildInfoBundle";

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    private Version()
    {
    }

    /*----------------------------------------------------------------------*\
                               Main Program
    \*----------------------------------------------------------------------*/

    /**
     * Get the version number of the API.
     *
     * @return the version number
     */
    public static String getVersion()
    {
        return BundleUtil.getString (Package.BUNDLE_NAME, "api.version", "?");
    }

    /**
     * Get the copyright.
     *
     * @return the copyright string
     */
    public static String getCopyright()
    {
        return BundleUtil.getString (Package.BUNDLE_NAME, "api.copyright", "?");
    }

    /**
     * Display the build information
     *
     * @param args  command-line parameters (ignored)
     */
    public static void main (String[] args)
    {
        BuildInfo buildInfo = new BuildInfo (BUILD_INFO_BUNDLE_NAME);

        System.out.println("org.clapper.util library, version " +
                           getVersion());
        System.out.println(getCopyright());
        System.out.println();
        System.out.println("Build:          " + buildInfo.getBuildID());
        System.out.println("Build date:     " + buildInfo.getBuildDate());
        System.out.println("Built by:       " + buildInfo.getBuildUserID());
        System.out.println("Built on:       " +
                           buildInfo.getBuildOperatingSystem());
        System.out.println("Build Java VM:  " +
                           buildInfo.getBuildJavaVM());
        System.out.println("Build compiler: " +
                           buildInfo.getBuildJavaCompiler());
        System.out.println("Ant version:    " +
                           buildInfo.getBuildAntVersion());
        System.exit (0);
    }
}
